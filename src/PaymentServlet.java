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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declare WebServlet called PaymentServlet. Maps to url "/api/payment"
@WebServlet( name = "PaymentServlet", urlPatterns = "/api/payment" )
public class PaymentServlet extends HttpServlet {
    private static final long SerialVersionUID = 7L;

    // Create a dataSource which registered in web.xml
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
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String credit_card_number = request.getParameter("credit_card_number");
        Date expiration_date = Date.valueOf(request.getParameter("expiration_date"));

        JsonObject responseJsonObject = new JsonObject();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM creditcards CC, customers C WHERE CC.id = ? AND " +
                    "CC.firstName = ? AND " +
                    "CC.lastName = ? AND " +
                    "CC.expiration = ? ";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query
            statement.setString(1, credit_card_number);
            statement.setString(2, first_name);
            statement.setString(3, last_name);
            statement.setDate(4, expiration_date);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                // Login fail due to incorrect username
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "payment information incorrect");
            } else {
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");

                String updateQuery = "INSERT INTO sales VALUES(?, ?, ?, ?)";
                PreparedStatement update = conn.prepareStatement(updateQuery);

                update.setString(1, null);
                update.setInt(2, user.customerId);
                update.setString(3, "tt0286917");
                update.setDate(4, new Date(System.currentTimeMillis()));

                update.executeUpdate();

                update.close();
            }
            rs.close();
            statement.close();

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
            request.getServletContext().log("Login failed");

            out.write(responseJsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}