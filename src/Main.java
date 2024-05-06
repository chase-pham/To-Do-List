import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Create the GUI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("To-Do List Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);

            JTextField textField = new JTextField(20);
            JButton button = new JButton("Submit Task");

            // Layout components in a panel
            JPanel panel = new JPanel();
            panel.add(textField);
            panel.add(button);
            frame.getContentPane().add(panel); // Adds panel to frame

            // Add action listener to button
            button.addActionListener(e -> {
                String task = textField.getText();
                if (!task.isEmpty()) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        // Insert task into database
                        String sql = "INSERT INTO Tasks (TaskNAME, Description) VALUES (?, ?);";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, task);
                            stmt.setString(2, "New Task added from GUI");
                            int rowsAffected = stmt.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(frame, "Task added successfully!");
                                textField.setText(""); // Clear text field after submission
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Error adding task: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a task name.");
                }
            });

            frame.setVisible(true);
        });
    }
}
