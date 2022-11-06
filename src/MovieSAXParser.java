
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.google.gson.JsonArray;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

@WebServlet(name = "MovieSAXParser", urlPatterns = "/api/movie-sax-parser")
public class MovieSAXParser extends DefaultHandler {
    private List<Movie> movies;
    private HashMap<String, String> genres;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;
    private String tempDirector;

    public MovieSAXParser() {
        movies = new ArrayList<Movie>();
        genres = new HashMap<String, String>();
    }

    public void runExample() {
        parseDocument();
        linkGenres();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void linkGenres() {
        try {
            // Incorporate mySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the test database
            Connection conn = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false",
                    "mytestuser", "My6$Password");

            String query = "SELECT name FROM genres";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            for (String genreXML: genres.keySet()) {
                while (rs.next()) {
                    String genreDB = rs.getString("name");
                    int index = 0;

                    for (char ch: genreDB.toLowerCase().toCharArray()) {
                        if (ch == genreXML.charAt(index)) {
                            index += 1;
                        }

                        if (index == genreXML.length()) {
                            genres.replace(genreXML, genreDB);
                            break;
                        }
                    }
                }
                rs.beforeFirst();
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

            int totalMovies = 0;
            Iterator<Movie> it = movies.iterator();
            while (it.hasNext()) {
                Movie movie = it.next();

                if (movie.getYear() == 0) {
                    continue;
                } else if ("".equals(movie.getDirector())) {
                    continue;
                }

                String query = "SELECT * FROM movies M WHERE M.title = ? AND M.year = ? AND M.director = ?";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                statement.setString(1, movie.getTitle());
                statement.setInt(2, movie.getYear());
                statement.setString(3, movie.getDirector());

                // Perform the query
                ResultSet rs = statement.executeQuery();

                // Iterate through each row of rs
                if (!rs.isBeforeFirst()) {
                    List<String> movieGenres = movie.getGenres();
                    for (int i = 0; i < movieGenres.size(); i++) {
                        if (!"".equals(genres.get(movieGenres.get(i)))) {
                            movie.linkGenre(i, genres.get(movieGenres.get(i)));
                        }
                    }
                    System.out.println(movie);
                    totalMovies += 1;
                }
                rs.close();
                statement.close();
            }
            System.out.println("No of Movies '" + totalMovies);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of movie
            tempMovie = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            tempMovie.setDirector(tempDirector);
            movies.add(tempMovie);
        } else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            for (char c : tempVal.toCharArray()) {
                if (!Character.isDigit(c)) {
                    tempVal = "0";
                }
            }
            tempMovie.setYear(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("dirname")) {
            tempDirector = tempVal;
            for (char c : tempVal.toCharArray()) {
                if (!Character.isAlphabetic(c)) {
                    tempDirector = "";
                }
                break;
            }
        } else if (qName.equalsIgnoreCase("cat")) {
            if ("".equals(tempVal)) {
                return;
            }

            for (char c : tempVal.toCharArray()) {
                if (!Character.isAlphabetic(c)) {
                    return;
                }
            }

            tempVal = tempVal.toLowerCase();
            if (!genres.containsKey(tempVal)) {
                genres.put(tempVal, "");
            }
            tempMovie.addGenre(tempVal);
        }
    }

    public static void main(String[] args) {
        MovieSAXParser spe = new MovieSAXParser();
        spe.runExample();
    }
}