import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

// Declaring a WebServlet called SessionServlet, which maps to url "/session"
@WebServlet( name = "SessionServlet", urlPatterns = "/session")
public class SessionServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response ) throws IOException {

        response.setContentType( "text/html" );
        PrintWriter out = response.getWriter();
        String title = "Fbiflix Session Tracker";

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);

        String heading;

        // Retrieve data named "accessCount" from session, which count how many times the user requested before
        Integer accessCount = (Integer) session.getAttribute( "accessCount" );

        if ( accessCount == null ){
            // Means User is new
            accessCount = 0;
            heading = "Welcome, New User";
        } else {
            // Else User is not new; they have session information
            accessCount++;
            heading = "Welcome Back";
        }

        // Update their Session's accessCount information
        session.setAttribute( "accessCount", accessCount );

        out.println( "<html lang=\"en\"><head><title>" + title + "</title></head>" +

                "<body bgcolor=\"#FDF5E6\">\n" +
                "<h1 ALIGN=\"center\">" + heading + "</h1>\n" +  // Set the greeting heading generated before
                "<h2>Information on Your Session:</H2>\n" +
                // Create a <table>
                "<table border=1 align=\"center\">\n" +
                "  <tr bgcolor=\"#FFAD00\">\n" +  // Create a <tr> (table row)
                "    <th>Info Type<th>Value\n" +  // Create two <th>s (table header)

                // Create a <tr> (table row)
                "  <tr>\n" +
                "    <td>ID\n" +  // Create the first <td> (table data) in <tr>, which corresponding to the first column
                "    <td>" + session.getId() + "\n" +   // Create the second <td> (table data) in <tr>, which
                // corresponding to the second column

                // Repeat for more table rows and data.
                "  <tr>\n" +
                "    <td>Creation Time\n" +
                "    <td>" +
                new Date(session.getCreationTime()) + "\n" +
                "  <tr>\n" +
                "    <td>Time of Last Access\n" +
                "    <td>" +
                new Date(session.getLastAccessedTime()) + "\n" +
                "  <tr>\n" +
                "    <td>Number of Previous Accesses\n" +
                "    <td>" + accessCount + "\n" +

//                //Sean's add-in
//                // Test out func found online. Yields some other SessionID
//                "  <tr>\n" +
//                "    <td>Client Session ID\n" +
//                "    <td>" + request.getRequestedSessionId() + "\n" +

                "  </tr>" +
                "</table>\n");

        // The following two statements show how to retrieve parameters in the request. The URL format is something like:
        // http://localhost:8080/cs122b-fall22-project2-session-example/session?myname=Konaka%20Sean
        String myName = request.getParameter("myname");
        if (myName != null)
            out.println("Hey " + myName + "<br><br>");

        out.println("</body></html>");

        
    }
}
