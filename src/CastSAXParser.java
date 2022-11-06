
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public CastSAXParser() {
        casts = new ArrayList<Cast>();
    }

    public void runExample() {
        System.out.println("---Inconsistencies in Cast XML---");
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
            sp.parse("src/casts124.xml", this);

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

            Iterator<Cast> it = casts.iterator();
            while (it.hasNext()) {
                Cast cast = it.next();

                String query = "SELECT * FROM movies M, stars S, stars_in_movies SM WHERE M.id = SM.movieId AND S.id = SM.starId AND S.name = ? AND M.id = ?";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                statement.setString(1, cast.getMovieId());
                statement.setString(2, cast.getStarName());

                // Perform the query
                ResultSet rs = statement.executeQuery();

                // Iterate through each row of rs
                if (!rs.isBeforeFirst()) {

                } else {
                    System.out.println(cast);
                }
                rs.close();
                statement.close();
            }
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
            tempCast.setStarName(tempVal);
        }
    }

    public static void main(String[] args) {
        CastSAXParser spe = new CastSAXParser();
        spe.runExample();
    }
}