import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class ConnectionPool {
    private List<Connection> connectionPool;
    private List<Connection> usedConnectionPool = new ArrayList<>();

    ConnectionPool(int size) throws ClassNotFoundException, SQLException {
        // Incorporate mySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        connectionPool = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            // Connect to the database
            Connection conn = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false",
                    "mytestuser", "My6$Password");

            connectionPool.add(conn);
        }
    }

    public synchronized Connection getConnection() {
        Connection conn = connectionPool.remove(connectionPool.size() - 1);
        usedConnectionPool.add(conn);
        return conn;
    }

    public synchronized void releaseConnection(Connection conn) {
        usedConnectionPool.remove(conn);
        connectionPool.add(conn);
    }

    public void close() throws SQLException {
        for (Connection conn: connectionPool) {
            conn.close();
        }

        for (Connection conn: usedConnectionPool) {
            conn.close();
        }
    }
}