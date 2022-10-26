import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Declaring a WebServlet called ItemServlet, which maps to url "api/items"
@WebServlet(name = "ItemServlet", urlPatterns = "/api/items")

public class ItemsServlet extends HttpServlet {
    private static final long serialVersionUID = 6L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + shoppingCart.size() + " items");

        // write all the data into the jsonObject
        response.getWriter().write(shoppingCart.toJsonArray().toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movie_id = request.getParameter("movie_id");
        String action = request.getParameter("action");
        System.out.println("ItemServlet.Post: " + movie_id);
        HttpSession session = request.getSession();

        ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.incrementMovie(movie_id);
            System.out.println("incrementing movie: " + movie_id);
            session.setAttribute("shoppingCart", shoppingCart);
        } else if ("increment".equals(action)) {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (shoppingCart) {
                System.out.println("Adding movie: " + movie_id);
                shoppingCart.incrementMovie(movie_id);
            }
        } else if ("decrement".equals(action)) {
            synchronized (shoppingCart) {
                System.out.println("decrementing movie: " + movie_id);
                shoppingCart.decrementMovie(movie_id);
            }
        } else if ("remove".equals(action)) {
            // Check for "remove" keyword in "item". If exists, take this overriding branch to remove entry
            System.out.println("Removal call received on...: " + movie_id);
            shoppingCart.removeMovie(movie_id);
            session.setAttribute("shoppingCart", shoppingCart);
        } else if ("flush".equals(action)) {
            // If Flush flag is true, empty out the entire arraylist containing movieIds
            System.out.println("Flush call received...: ");
            shoppingCart.flush();
            session.setAttribute("shoppingCart", shoppingCart);
        }

        response.getWriter().write(shoppingCart.toJsonArray().toString());
    }


}