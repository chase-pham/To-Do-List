import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


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

    public void deleteTask(int taskId) throws SQLException {
        String sql = "DELETE FROM Tasks WHERE TaskID = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        }
    }    

    public void updateTask(int taskId, String newTaskName) throws SQLException {
        String sql = "UPDATE Tasks SET TaskNAME = ? WHERE TaskID = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newTaskName);
            stmt.setInt(2, taskId);
            stmt.executeUpdate();
        }
    }

    public List<String> getAllTasksWithIds() throws SQLException {
        List<String> tasks = new ArrayList<>();
        String sql = "SELECT TaskID, TaskNAME FROM Tasks;";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("TaskID");
                String name = rs.getString("TaskNAME");
                tasks.add(id + ": " + name); // Combining ID and name for display
            }
        }
        return tasks;
    }
    

    public String getTaskDetails(int taskId) throws SQLException {
        String taskDetails = "No task found with ID: " + taskId;
        String sql = "SELECT TaskNAME, Description FROM Tasks WHERE TaskID = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("TaskNAME");
                    String description = rs.getString("Description");
                    taskDetails = "Task: " + name + "\nDescription: " + description;
                }
            }
        }
        return taskDetails;
    }

    public List<String> searchTasks(String searchText) throws SQLException {
        List<String> foundTasks = new ArrayList<>();
        String sql = "SELECT TaskID, TaskNAME FROM Tasks WHERE TaskNAME LIKE ? OR Description LIKE ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("TaskID");
                    String name = rs.getString("TaskNAME");
                    foundTasks.add(id + ": " + name);
                }
            }
        }
        return foundTasks;
    }
}
