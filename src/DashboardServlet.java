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
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;


@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbMaster");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.setContentType("application/json"); // Response mime type

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query = "DESCRIBE ";
            JsonArray tablesJsonArray = new JsonArray();
            for (String table : retrieveTables(conn)) {
                PreparedStatement statement = conn.prepareStatement(query + table);

                // Perform the query
                ResultSet rs = statement.executeQuery();


                JsonArray tableColumnsJsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {
                    String table_field = rs.getString("Field");
                    String table_type = rs.getString("Type");

                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject tableColumnJsonObject = new JsonObject();
                    tableColumnJsonObject.addProperty("table_field", table_field);
                    tableColumnJsonObject.addProperty("table_type", table_type);

                    tableColumnsJsonArray.add(tableColumnJsonObject);
                }
                rs.close();
                statement.close();

                JsonObject tableJsonObject = new JsonObject();
                tableJsonObject.addProperty("table_name", table);
                tableJsonObject.add("table_columns", tableColumnsJsonArray);
                tablesJsonArray.add(tableJsonObject);
            }

            // Log to localhost log
            request.getServletContext().log("getting " + tablesJsonArray.size() + " results");

            // Write JSON string to output
            out.write(tablesJsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
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

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        JsonObject responseJsonObject = new JsonObject();
        try (Connection conn = dataSource.getConnection()) {
            if ("addStar".equals(action)) {
                String starName = request.getParameter("starName");
                String starBirthYear = request.getParameter("starBirthYear");
                String starId = getStarId(conn);

                if (!"".equals(starName)) {
                    String updateQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";

                    PreparedStatement update = conn.prepareStatement(updateQuery);

                    update.setString(1, starId);
                    update.setString(2, starName);
                    if ("".equals(starBirthYear)) {
                        update.setString(3, null);
                    } else {
                        update.setInt(3, Integer.parseInt(starBirthYear));
                    }

                    update.executeUpdate();
                    update.close();

                    // Operation succeeded
                    responseJsonObject.addProperty("status", "success");
                    // Log to localhost log
                    request.getServletContext().log("Operation succeeded");
                    responseJsonObject.addProperty("message", "Star added with Star ID: " + starId);
                } else {
                    // Operation failed due to invalid action
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Operation failed");
                    responseJsonObject.addProperty("message", "Name missing");
                }
            } else if ("addMovie".equals(action)) {
                String movieTitle = request.getParameter("movieTitle");
                String movieYear = request.getParameter("movieYear");
                String movieDirector = request.getParameter("movieDirector");
                String starName = request.getParameter("starName");
                String starBirthYear = request.getParameter("starBirthYear");
                String genreName = request.getParameter("genreName");

                String callQuery = "CALL add_movie(?, ?, ?, ?, ?, ?, ?)";
                CallableStatement call = conn.prepareCall(callQuery);

                call.setString(1, movieTitle);
                call.setInt(2, Integer.parseInt(movieYear));
                call.setString(3, movieDirector);
                call.setString(4, starName);
                if ("".equals(starBirthYear)) {
                    call.setInt(5, 0);
                } else {
                    call.setInt(5, Integer.parseInt(starBirthYear));
                }
                call.setString(6, genreName);

                call.registerOutParameter(7, Types.VARCHAR);
                call.executeUpdate();

                String message = call.getString(7);
                // Operation succeeded
                responseJsonObject.addProperty("status", "success");
                // Log to localhost log
                request.getServletContext().log("Operation succeeded");
                responseJsonObject.addProperty("message", message);

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

    private ArrayList<String> retrieveTables(Connection conn) throws SQLException {
        String query = "SHOW TABLES";
        PreparedStatement statement = conn.prepareStatement(query);

        // Perform the query
        ResultSet rs = statement.executeQuery();

        ArrayList<String> tables = new ArrayList<>();
        // Iterate through each row of rs
        while (rs.next()) {
            String table = rs.getString("Tables_in_moviedb");
            tables.add(table);
        }
        rs.close();
        statement.close();

        return tables;
    }

    private String getStarId(Connection conn) throws SQLException {
        String query = "SELECT MAX(id) id from stars WHERE id LIKE BINARY 'nm%'";
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);

        String starId;
        if (!rs.next()) {
            starId = "nm0000000";
        } else {
            starId = rs.getString("id");
        }
        statement.close();
        rs.close();


        starId = "nm" + (Integer.parseInt(starId.substring(2)) + 1);

        return starId;
    }
}