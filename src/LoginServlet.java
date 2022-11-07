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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;

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
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        JsonObject responseJsonObject = new JsonObject();

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // Login fail due to incorrect password
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            responseJsonObject.addProperty("message", "recaptcha failed");

            out.write(responseJsonObject.toString());

            response.setStatus(200);
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM customers C WHERE C.email = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, username);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                // Login fail due to incorrect username
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            } else {
                boolean passwordMatched = false;

                while (rs.next()) {
                    if (password.equals(rs.getString("password"))) {
                        // Login success:

                        // set this user into the session
                        request.getSession().setAttribute("user", new User(username, rs.getInt("id")));

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");

                        passwordMatched = true;
                        break;
                    }
                }

                if (!passwordMatched) {
                    // Login fail due to incorrect password
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(responseJsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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