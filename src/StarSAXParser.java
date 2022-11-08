
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class StarSAXParser extends DefaultHandler {

    List<Star> stars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    private String starId;

    private ConnectionPool connectionPool;

    private int duplicateStarsCount;
    private int insertStarsCount;

//    private PrintWriter writer;

    public StarSAXParser() {
        try {
            stars = new ArrayList<Star>();
            connectionPool = new ConnectionPool(5);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        parseDocument();
        cleanData();
        insertData();
    }

    public int getDuplicateStarsCount() {
        return duplicateStarsCount;
    }

    public int getInsertStarsCount() {
        return insertStarsCount;
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("../pipeline_source/actors63.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and clean the contents
     */
    private void cleanData() {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            Iterator<Star> it = stars.iterator();
            stars = new ArrayList<>();
            while (it.hasNext()) {
                Star star = it.next();

                QueryWorker worker = new QueryWorker(star);
                executor.execute(worker);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {}
            connectionPool.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Iterate through the list and insert the contents
     */
    private void insertData() {
        try {
            // Incorporate mySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false",
                    "mytestuser", "My6$Password");
            
            int batchSize = 100;
            int count = 0;

            starId = getStarId(conn);
            String updateStarQuery = "INSERT IGNORE INTO stars VALUES(?, ?, ?)";
            PreparedStatement update = conn.prepareStatement(updateStarQuery);
            
            Iterator<Star> it = stars.iterator();
            while (it.hasNext()) {
                Star star = it.next();
                
                update.setString(1, starId);
                update.setString(2, star.getName());
                if (star.getBirthYear() != 0) {
                    update.setInt(3, star.getBirthYear());
                } else {
                    update.setString(3, null);
                }
                updateStarId();
                update.addBatch();

                count++;
                if (count % batchSize == 0) {
                    update.executeBatch();
                }
            }
            update.executeBatch();
            update.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String getStarId(Connection conn) throws SQLException {
        String query = "SELECT MAX(id) id from stars WHERE id LIKE BINARY 'nm%'";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);

        rs.next();
        String starId = rs.getString("id");
        if (starId == null) {
            starId = "nm0000000";
        }
        statement.close();
        rs.close();

        int starIdDigits = (Integer.parseInt(starId.substring(2)) + 1);
        starId = "nm" + "0".repeat(7 - String.valueOf(starIdDigits).length()) + starIdDigits;

        return starId;
    }

    private void updateStarId() {
        int starIdDigits = (Integer.parseInt(starId.substring(2)) + 1);
        starId = "nm" + "0".repeat(7 - String.valueOf(starIdDigits).length()) + starIdDigits;
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of star
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length).trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            stars.add(tempStar);
        } else if (qName.equalsIgnoreCase("stagename")) {
            for (char c : tempVal.toCharArray()) {
                if (Character.isDigit(c)) {
                    tempVal = "";
                }
            }
            tempStar.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            if ("".equalsIgnoreCase(tempVal)) {
                tempVal = "0";
            }

            for (char c : tempVal.toCharArray()) {
                if (!Character.isDigit(c)) {
                    tempVal = "0";
                    break;
                }
            }
            tempStar.setBirthYear(Integer.parseInt(tempVal));
        }

    }

    class QueryWorker implements Runnable {
        Star star;

        QueryWorker(Star star) {
            this.star = star;
        }

        @Override
        public void run() {
            Connection conn = connectionPool.getConnection();

            try {
                if ("".equals(star.getName())) {
                    return;
                }

                String query = "SELECT * FROM stars S WHERE S.name = ? LIMIT 1";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                statement.setString(1, star.getName());

                // Perform the query
                ResultSet rs = statement.executeQuery();

                // Iterate through each row of rs
                if (!rs.isBeforeFirst()) {
                    stars.add(star);
                    insertStarsCount++;
                } else {
                    duplicateStarsCount++;
                }

                rs.close();
                statement.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    public static void main(String[] args) {
        StarSAXParser spe = new StarSAXParser();
        spe.run();
    }
}