
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sql.ConnectionPoolDataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class CastSAXParser extends DefaultHandler {

    List<Cast> casts;

    private String tempVal;

    //to maintain context
    private Cast tempCast;

    private ConnectionPool connectionPool;

    private int nonexistingMoviesCount;
    private int nonexistingStarsCount;
    private int insertedStarsInMoviesCount;

//    private PrintWriter writer;

    public CastSAXParser() {
        try {
            casts = new ArrayList<Cast>();
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

    public int getNonexistingMoviesCount() {
        return nonexistingMoviesCount;
    }

    public int getNonexistingStarsCount() {
        return nonexistingStarsCount;
    }

    public int getInsertedStarsInMoviesCount() {
        return insertedStarsInMoviesCount;
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("../pipeline_source/casts124.xml", this);

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
            Iterator<Cast> it = casts.iterator();
            casts = new ArrayList<>();
            while (it.hasNext()) {
                Cast cast = it.next();

                QueryWorker worker = new QueryWorker(cast);
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

            String updateQuery = "INSERT IGNORE INTO stars_in_movies VALUES(?, ?)";
            PreparedStatement update = conn.prepareStatement(updateQuery);

            Iterator<Cast> it = casts.iterator();
            while (it.hasNext()) {
                Cast cast = it.next();

                update.setString(1, cast.getStarId());
                update.setString(2, cast.getMovieId());
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

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            //create a new instance of cast
            tempCast = new Cast();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length).trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("m")) {
            //add it to the list
            casts.add(tempCast);
        } else if (qName.equalsIgnoreCase("f")) {
            tempCast.setMovieId(tempVal);
        } else if (qName.equalsIgnoreCase("a")) {
            for (char c : tempVal.toCharArray()) {
                if (!Character.isAlphabetic(c) && c != ' ') {
                    tempCast.setStarName("");
                    return;
                }
            }
            tempCast.setStarName(tempVal);
        }
    }

    public static void main(String[] args) {
        CastSAXParser spe = new CastSAXParser();
        spe.run();
    }

    class QueryWorker implements Runnable {
        Cast cast;

        QueryWorker(Cast cast) {
            this.cast = cast;
        }

        @Override
        public void run() {
            Connection conn = connectionPool.getConnection();
            try {
                if ("".equals(cast.getStarName())) {
                    return;
                }

                String query = "SELECT * FROM movies M WHERE M.id = ?";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                statement.setString(1, cast.getMovieId());

                // Perform the query
                ResultSet rs = statement.executeQuery();

                if (!rs.isBeforeFirst()) {
                    rs.close();
                    statement.close();
                    nonexistingMoviesCount++;
                    return;
                }
                rs.close();
                statement.close();

                query = "SELECT * FROM stars S WHERE MATCH (S.name) AGAINST (? IN BOOLEAN MODE) LIMIT 1";

                // Declare our statement
                statement = conn.prepareStatement(query);

                String fulltext = "";
                for (String word: cast.getStarName().split("\\s+")) {
                    fulltext += "+" + word + " ";
                }
                fulltext.trim();

                statement.setString(1, fulltext);

                // Perform the query
                rs = statement.executeQuery();

                if (!rs.isBeforeFirst()) {
                    rs.close();
                    statement.close();
                    nonexistingStarsCount++;
                    return;
                } else {
                    rs.next();
                    cast.setStarId(rs.getString("id"));
                }
                rs.close();
                statement.close();

                String finalQuery = "SELECT * FROM movies M, stars S, stars_in_movies SM WHERE M.id = SM.movieId AND S.id = SM.starId AND M.id = ? AND S.id = ?";

                // Declare our statement
                statement = conn.prepareStatement(finalQuery);

                statement.setString(1, cast.getMovieId());
                statement.setString(2, cast.getStarId());

                // Perform the query
                rs = statement.executeQuery();

                // Iterate through each row of rs
                if (!rs.isBeforeFirst()) {
                    casts.add(cast);
                    insertedStarsInMoviesCount++;
                }
                rs.close();
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connectionPool.releaseConnection(conn);
            }
        }
    }
}