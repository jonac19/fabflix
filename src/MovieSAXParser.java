
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

@WebServlet(name = "MovieSAXParser", urlPatterns = "/api/movie-sax-parser")
public class MovieSAXParser extends DefaultHandler {
    private List<Movie> movies;
    private HashMap<String, Integer> genres;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;
    private String tempDirector;

    public MovieSAXParser() {
        movies = new ArrayList<Movie>();
        genres = new HashMap<String, Integer>();
    }

    public void runExample() {
        System.out.println("---Inconsistencies in Movie XML---");
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

            String query = "SELECT * FROM genres";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            HashMap<String, Integer> genresDB = new HashMap<>();
            while (rs.next()) {
                genresDB.put(rs.getString("name").toLowerCase(), rs.getInt("id"));
            }
            rs.close();
            statement.close();

            for (String genreXML: genres.keySet()) {
                for (String genreDB: genresDB.keySet()) {
                    int index = 0;

                    for (char ch: genreDB.toCharArray()) {
                        if (ch == genreXML.charAt(index)) {
                            index += 1;
                        }

                        if (index == genreXML.length()) {
                            genres.replace(genreXML, genresDB.get(genreDB));
                            break;
                        }
                    }
                }

                if (genres.get(genreXML) == -1) {
                    String updateGenreQuery = "INSERT INTO genres VALUES(?, ?)";
                    PreparedStatement updateGenre = conn.prepareStatement(updateGenreQuery);

                    updateGenre.setString(1, null);
                    updateGenre.setString(2, genreXML);

                    updateGenre.executeUpdate();
                    updateGenre.close();

                    String idQuery = "SELECT id FROM genres G WHERE G.name = ?";
                    PreparedStatement idStatement = conn.prepareStatement(idQuery);
                    idStatement.setString(1, genreXML);
                    ResultSet idRS = idStatement.executeQuery();
                    idRS.next();
                    genres.replace(genreXML, idRS.getInt("id"));
                    idStatement.close();
                    idRS.close();
                }
            }
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

            Iterator<Movie> it = movies.iterator();
            while (it.hasNext()) {
                Movie movie = it.next();

                if ("".equals(movie.getMovieId())) {
                    continue;
                } else if (movie.getYear() == 0) {
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
                    String updateMovieQuery = "INSERT IGNORE INTO movies VALUES(?, ?, ?, ?)";
                    PreparedStatement updateMovie = conn.prepareStatement(updateMovieQuery);

                    updateMovie.setString(1, movie.getMovieId());
                    updateMovie.setString(2, movie.getTitle());
                    updateMovie.setInt(3, movie.getYear());
                    updateMovie.setString(4, movie.getDirector());

                    updateMovie.executeUpdate();
                    updateMovie.close();

                    List<String> movieGenres = movie.getGenreNames();
                    for (int i = 0; i < movieGenres.size(); i++) {
                        String updateGenreQuery = "INSERT IGNORE INTO genres_in_movies VALUES(?, ?)";
                        PreparedStatement updateGenre = conn.prepareStatement(updateGenreQuery);

                        updateGenre.setInt(1, genres.get(movieGenres.get(i)));
                        updateGenre.setString(2, movie.getMovieId());

                        updateGenre.executeUpdate();
                        updateGenre.close();
                    }
                } else {
                    System.out.println(movie);
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
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of movie
            tempMovie = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length).trim();
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
                    System.out.println("<year>" + tempVal + "</year>");
                    tempVal = "0";
                }
            }
            tempMovie.setYear(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("dirname")) {
            tempDirector = tempVal;
            for (char c : tempVal.toCharArray()) {
                if (Character.isDigit(c)) {
                    System.out.println("<dirname>" + tempVal + "</dirname>");
                    tempDirector = "";
                }
            }
        } else if (qName.equalsIgnoreCase("cat")) {
            if ("".equals(tempVal)) {
                System.out.println("<cat>" + tempVal + "</cat>");
                return;
            }

            for (char c : tempVal.toCharArray()) {
                if (!Character.isAlphabetic(c)) {
                    System.out.println("<cat>" + tempVal + "</cat>");
                    return;
                }
            }

            for (String genre: tempVal.toLowerCase().split("\\s+")) {
                if (!genres.containsKey(genre)) {
                    genres.put(genre, -1);
                }
                tempMovie.addGenreName(genre);
            }
        }
    }

    public static void main(String[] args) {
        MovieSAXParser spe = new MovieSAXParser();
        spe.runExample();
    }
}