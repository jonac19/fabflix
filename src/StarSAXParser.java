
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
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
    private String tempMovie;

    public StarSAXParser() {
        stars = new ArrayList<Star>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/actors63.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {
        try {
            // Incorporate mySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false",
                    "mytestuser", "My6$Password");

            Iterator<Star> it = stars.iterator();
            int totalStars = 0;
            while (it.hasNext()) {
                Star star = it.next();

                if (star.getBirthYear() == 0) {
                    continue;
                }

                String query = "SELECT * FROM stars S WHERE S.name = ?";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                statement.setString(1, star.getName());

                // Perform the query
                ResultSet rs = statement.executeQuery();

                // Iterate through each row of rs
                if (!rs.isBeforeFirst()) {
                    System.out.println(star.toString());
                    totalStars += 1;
                }
                rs.close();
                statement.close();
            }
            System.out.println("No of Stars '" + totalStars + "'.");
        } catch (Exception e) {

        }
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
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            stars.add(tempStar);
        } else if (qName.equalsIgnoreCase("stagename")) {
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

    public static void main(String[] args) {
        StarSAXParser spe = new StarSAXParser();
        spe.runExample();
    }
}