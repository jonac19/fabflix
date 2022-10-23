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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// Declaring a WebServlet called ItemServlet, which maps to url "api/items"
@WebServlet(name = "ItemServlet", urlPatterns = "/api/items")

public class ItemsServlet extends HttpServlet {
    private static final long serialVersionUID = 6L;


    // Create a dataSource which registered in web.
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get a instance of current session on the request
        HttpSession session = request.getSession();

        // Retrieve data named "previousItems" from session
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems
        // ArrayList for the user
        if (previousItems == null) {

            // Add the newly created ArrayList to session, so that it could be retrieved next time
            previousItems = new ArrayList<>();
            session.setAttribute("previousItems", previousItems);
        }

        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");

        String newItem = request.getParameter("newItem"); // Get parameter that sent by GET request url

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we
        // lock the ArrayList while updating
        synchronized (previousItems) {
            if (newItem != null) {
                previousItems.add(newItem); // Add the new item to the previousItems ArrayList
            }

            JsonArray jsonArray = new JsonArray();

            try (Connection conn = dataSource.getConnection()){
                out.println("<ul style='color:black'>");
                for (String previousItem : previousItems) {
                    // Call database to match previousItem (movieID) to movie title
                    String query = "SELECT * FROM movies M WHERE M.id=?";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, previousItem);
                    ResultSet rs = statement.executeQuery();
                    rs.next();

                    // Make JSON Object to hold movie title, add to Array, and send to items.js
                    JsonObject jsonObject = new JsonObject();
                    String movie_Title = rs.getString("title");
                    jsonObject.addProperty("movie_title", movie_Title);
                    jsonArray.add(jsonObject);

                    rs.close();
                    statement.close();
                }
                out.write(jsonArray.toString());
                response.setStatus(200);

            } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
            }

        }
        out.close();
    }
}