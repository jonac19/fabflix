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
import java.sql.ResultSet;
import java.sql.Statement;

// Declaring a WebServlet called StarsServlet. Maps to url "/api/stars"
@WebServlet( name = "StarsServlet", urlPatterns = "/api/stars" )
public class StarsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    //create a datasource which registered in web
    private DataSource dataSource;

    public void init( ServletConfig config ) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbexample");
        } catch ( NamingException e ) {
            e.printStackTrace();
        }
    }

    //@see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws IOException {

        response.setContentType("application/json");    //reponse mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection later
        try ( Connection conn = dataSource.getConnection() ) {

            // Declare statement
            Statement statement = conn.createStatement();

            // Create query
            String query = "SELECT * FROM stars";

            // Execute query through statement. Store in "rs"
            ResultSet rs = statement.executeQuery( query );

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs and create a jsonObject each
            while( rs.next() ) {
                String star_id = rs.getString("id");
                String star_name = rs.getString("name");
                String star_dob = rs.getString("birthYear");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty( "star_id", star_id );
                jsonObject.addProperty( "star_name", star_name );
                jsonObject.addProperty( "star_dob", star_dob );

                jsonArray.add( jsonObject );
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write( jsonArray.toString() );
            // Set response status to 200 (OK)
            response.setStatus( 200 );

        } catch ( Exception e ) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty( "errorMessage", e.getMessage() );
            out.write( jsonObject.toString() );

            // Set response status to 500 (Internal Server Error)
            response.setStatus( 500 );

        } finally {
            out.close();
        }
    }
}