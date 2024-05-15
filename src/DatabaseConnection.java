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
        String sql = "SELECT TaskID, TaskNAME FROM Tasks WHERE TaskNAME LIKE ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchText + "%");
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

    public List<String> getTasksSortedBy(String columnName) throws SQLException {
        List<String> tasks = new ArrayList<>();
        String sql = "SELECT TaskID, TaskNAME FROM Tasks ORDER BY " + columnName;
        try (Connection conn = getConnection();  // Ensure this method is static if called statically
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("TaskID");
                String name = rs.getString("TaskNAME");
                tasks.add(id + ": " + name);
            }
        }
        return tasks;
    }    
    
    private String buildSortQuery(String sortBy) {
        switch (sortBy) {
            case "Priority":
                return "SELECT TaskID, TaskNAME FROM Tasks ORDER BY Priority DESC";
            case "CompletionStatus":
                return "SELECT TaskID, TaskNAME FROM Tasks ORDER BY CompletionStatus DESC";
            case "Date":
                return "SELECT TaskID, TaskNAME FROM Tasks ORDER BY DueDate DESC";
            default:
                return "SELECT TaskID, TaskNAME FROM Tasks ORDER BY TaskID";
        }
    }

    public void insertTask(String taskName, String description, String dueDate, int priority, boolean completionStatus) throws SQLException {
        String sql = "INSERT INTO Tasks(TaskNAME, Description, DueDate, Priority, CompletionStatus) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, taskName);
            pstmt.setString(2, description);
            pstmt.setString(3, dueDate);
            pstmt.setInt(4, priority);
            pstmt.setBoolean(5, completionStatus);
            pstmt.executeUpdate();
        }
    }
}
