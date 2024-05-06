import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Include "integratedSecurity=true" to use Windows Authentication
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=ToDoListDB;user=TestUser;password=TestUser123!;encrypt=false";

    public static Connection getConnection() throws SQLException {
        // Load SQL Server JDBC driver to manage integrated security
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Establish and return the connection using the URL
        return DriverManager.getConnection(URL);
    }
}
