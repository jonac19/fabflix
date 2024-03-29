import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declare WebServlet called SingleStarServlet. Maps to url "/api/single-star"
@WebServlet( name = "StarServlet", urlPatterns = "/api/star" )
public class StarServlet extends HttpServlet {
    private static final long SerialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id );

        //Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try ( Connection conn = dataSource.getConnection() ) {
            // Get a connection from datasource

            // Construct a query with parameter represented by "?"
            String query = "SELECT * FROM stars AS S, stars_in_movies AS SM, movies AS M " +
                    "WHERE M.id = SM.movieId AND SM.starId = S.id AND S.id = ? ORDER BY M.title";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement( query );

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString( 1, id );

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            //Iterate through each row of rs
            while ( rs.next() ) {
                String starId = rs.getString("starId" );
                String starName = rs.getString( "name" );
                String starDob = rs.getString( "birthYear" );

                if (starDob == null) {
                    starDob = "N/A";
                }

                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", starId);
                jsonObject.addProperty("star_name", starName);
                jsonObject.addProperty("star_dob", starDob);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);

                jsonArray.add( jsonObject );
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write( jsonArray.toString() );
            // Set response status to 200 (OK)
            response.setStatus( 200 );

        } catch ( Exception e ) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }

}