import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
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
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        JsonObject responseJsonObject = new JsonObject();
        try (Connection conn = dataSource.getConnection()) {
            if ("addStar".equals(action)) {
                String starName = request.getParameter("starName");
                String starBirthYear = request.getParameter("starBirthYear");
                String starId = getStarId(conn);

                String updateQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";

                PreparedStatement update = conn.prepareStatement(updateQuery);

                update.setString(1, starId);
                update.setString(2, starName);
                update.setInt(3, Integer.parseInt(starBirthYear));

                update.executeUpdate();
                update.close();

                // Operation succeeded
                responseJsonObject.addProperty("status", "success");
                // Log to localhost log
                request.getServletContext().log("Operation succeeded");
                responseJsonObject.addProperty("message", "Star added: " + starId);
            } else if ("addMovie".equals(action)) {
                String movieTitle = request.getParameter("movieTitle");
                String movieYear = request.getParameter("movieYear");
                String movieDirector = request.getParameter("movieDirector");
                String starName = request.getParameter("starName");
                String starBirthYear = request.getParameter("starBirthYear");
                String genreName = request.getParameter("genreName");

                String callQuery = "CALL add_movie(?, ?, ?, ?, ?, ?)";
                CallableStatement call = conn.prepareCall(callQuery);

                call.setString(1, movieTitle);
                call.setInt(2, Integer.parseInt(movieYear));
                call.setString(3, movieDirector);
                call.setString(4, starName);
                call.setInt(5, Integer.parseInt(starBirthYear));
                call.setString(6, genreName);

                call.executeUpdate();
                call.close();
            } else {
                // Operation failed due to invalid action
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Operation failed");
                responseJsonObject.addProperty("message", "Invalid action");
            }

            // Write JSON string to output
            out.write(responseJsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
        } catch (Exception e) {
            // Write error message JSON object to output
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            // Log to localhost log
            request.getServletContext().log("Operation failed");

            out.write(responseJsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

    }

    private String getStarId(Connection conn) throws SQLException {
        String query = "SELECT MAX(id) id from stars";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);

        String starId;
        if (!rs.next()) {
            starId = "nm0000001";
        } else {
            starId = rs.getString("id");
        }
        statement.close();
        rs.close();


        starId = "nm" + (Integer.parseInt(starId.substring(2)) + 1);

        return starId;
    }
}