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
import java.sql.Statement;

// Declare WebServlet called SingleStarServlet. Maps to url "/api/browse"
@WebServlet( name = "BrowseServlet", urlPatterns = "/api/browse" )
public class BrowseServlet extends HttpServlet {
    private static final long SerialVersionUID = 5L;

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
        String criteria = request.getParameter("criteria");

        // The log message can be found in localhost log
        request.getServletContext().log("getting criteria: " + criteria );

        //Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try ( Connection conn = dataSource.getConnection() ) {
            // Get a connection from datasource

            // declare statement
            Statement statement = conn.createStatement();
            // prepare query
            String query = "";

            ResultSet rs;
            JsonArray jsonArray = new JsonArray();
            if (criteria.equals("genre")) {
                query = "SELECT * FROM genres GROUP BY 1";

                // Perform the query
                rs = statement.executeQuery(query);

                //Iterate through each row of rs
                while ( rs.next() ) {
                    String genreId = rs.getString("id");
                    String genreName = rs.getString("name");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("genre_id", genreId);
                    jsonObject.addProperty("genre_name", genreName);

                    jsonArray.add( jsonObject );
                }
            } else if (criteria.equals("title")) {
                query = "SELECT LEFT(title, 1) AS letter FROM movies GROUP BY 1 ORDER BY LEFT(title, 1)";

                // Perform the query
                rs = statement.executeQuery(query);

                //Iterate through each row of rs
                while ( rs.next() ) {
                    String titleLetter = rs.getString("letter");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("title_letter", titleLetter);

                    jsonArray.add( jsonObject );
                }
            } else {
                throw new Exception("Invalid criteria for browsing");
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