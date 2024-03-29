import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Declaring a WebServlet called MovieServlet, which maps to url "/api/movie"
@WebServlet(name = "MovieServlet", urlPatterns = "/api/movie")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM movies M, ratings R WHERE M.id = ? AND R.movieId = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);
            statement.setString(2, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                JsonArray movie_genres = getGenres(conn, movie_id);
                JsonArray movie_stars = getStars(conn, movie_id);

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.add("movie_genres", movie_genres);
                jsonObject.add("movie_stars", movie_stars);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }

    /**
     * @param conn Existing connection to MySQL database
     * @param movie_id Movie ID to obtain genres for
     * @return Array containing all genres associated with the given movie
     * @throws SQLException
     */
    private JsonArray getGenres(Connection conn, String movie_id) throws SQLException {
        String query = "SELECT * FROM genres G, genres_in_movies GM WHERE G.id = GM.genreId AND GM.movieId = ? ORDER BY G.name";

        // Declare our statement
        PreparedStatement statement = conn.prepareStatement(query);

        // Set the parameter represented by "?" in the query to the id we get from url,
        // num 1 indicates the first "?" in the query
        statement.setString(1, movie_id);

        // Perform the query
        ResultSet rs = statement.executeQuery();

        JsonArray jsonArray = new JsonArray();

        while (rs.next()) {
            String genre_id = rs.getString("genreId");
            String genre_name = rs.getString("name");

            // Create a JsonObject based on the data we retrieve from rs
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("genre_id", genre_id);
            jsonObject.addProperty("genre_name", genre_name);

            jsonArray.add(jsonObject);
        }
        rs.close();
        statement.close();

        return jsonArray;
    }

    /**
     * @param conn Existing connection to MySQL database
     * @param movie_id Movie ID to obtain genres for
     * @return Array containing all stars associated with the given movie
     * @throws SQLException
     */
    private JsonArray getStars(Connection conn, String movie_id) throws SQLException {
        String query = "SELECT * FROM stars S, stars_in_movies SM WHERE S.id = SM.starId AND SM.movieId = ? ORDER BY S.name";

        // Declare our statement
        PreparedStatement statement = conn.prepareStatement(query);

        // Set the parameter represented by "?" in the query to the id we get from url,
        // num 1 indicates the first "?" in the query
        statement.setString(1, movie_id);

        // Perform the query
        ResultSet rs = statement.executeQuery();

        JsonArray jsonArray = new JsonArray();

        while (rs.next()) {
            String star_id = rs.getString("starId");
            String star_name = rs.getString("name");
            String star_birthYear = rs.getString("birthYear");

            // Create a JsonObject based on the data we retrieve from rs
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("star_id", star_id);
            jsonObject.addProperty("star_name", star_name);
            jsonObject.addProperty("star_birthYear", star_birthYear);

            jsonArray.add(jsonObject);
        }
        rs.close();
        statement.close();

        return jsonArray;
    }
}