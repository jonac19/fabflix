/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    final String username;
    final int customerId;

    public User(String username, int customerId) {
        this.username = username;
        this.customerId = customerId;
    }

}