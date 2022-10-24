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
@WebServlet(name = "BackServlet", urlPatterns = "/api/back")
public class BackServlet extends HttpServlet {
    private static final long serialVersionUID = 6L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        JsonObject jsonObject = new JsonObject();
        String backURL = (String) session.getAttribute("backURL");
        if (backURL == null) {
            backURL = "index.html";
        }
        jsonObject.addProperty("backURL", backURL);

        // Write JSON string to output
        out.write(jsonObject.toString());
        // Set response status to 200 (OK)
        response.setStatus(200);

        out.close();

    }
}
