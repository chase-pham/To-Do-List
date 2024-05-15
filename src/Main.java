import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Create the GUI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("To-Do List Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(350, 500); // Increased height to accommodate new fields

            JTextField textField = new JTextField(20);
            textField.setBorder(new TitledBorder("Enter Task"));
            Border border = BorderFactory.createTitledBorder("Task");
            textField.setBorder(border);

            JTextField descriptionField = new JTextField(20);
            descriptionField.setBorder(BorderFactory.createTitledBorder("Description"));

            JTextField dueDateField = new JTextField(20);
            dueDateField.setBorder(BorderFactory.createTitledBorder("Due Date (YYYY-MM-DD)"));

            JTextField priorityField = new JTextField(20);
            priorityField.setBorder(BorderFactory.createTitledBorder("Priority (Integer)"));

            JCheckBox completionStatusCheckBox = new JCheckBox("Completed");

            JTextField searchField = new JTextField(20);
            searchField.setBorder(BorderFactory.createTitledBorder("Search Tasks"));

            String[] sortOptions = {"Priority", "CompletionStatus", "Due Date"};
            JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);

            JButton createButton = new JButton("Submit Task");
            JButton deleteButton = new JButton("Delete Task");
            JButton updateButton = new JButton("Update Task");
            JButton searchButton = new JButton("Search");
            JButton sortButton = new JButton("Sort");

            JList<String> taskList = new JList<>();

            Font font = new Font("Arial", Font.PLAIN, 14);
            textField.setFont(font);
            descriptionField.setFont(font);
            dueDateField.setFont(font);
            priorityField.setFont(font);
            completionStatusCheckBox.setFont(font);
            createButton.setFont(font);
            deleteButton.setFont(font);
            updateButton.setFont(font);
            taskList.setFont(font);

            taskList.setSelectionBackground(new Color(200, 200, 255)); // Light blue selection
            taskList.setSelectionForeground(Color.DARK_GRAY);

            // Layout components in a panel
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking
            panel.add(textField);
            panel.add(descriptionField);
            panel.add(dueDateField);
            panel.add(priorityField);
            panel.add(completionStatusCheckBox);
            panel.add(createButton);
            panel.add(deleteButton);
            panel.add(updateButton);
            panel.add(searchField);
            panel.add(searchButton);
            panel.add(sortComboBox);
            panel.add(sortButton);
            panel.add(new JScrollPane(taskList)); // Add scrolling to task list

            frame.getContentPane().add(panel); // Adds panel to frame
            frame.setVisible(true);

            // Add action listener to button
            createButton.addActionListener(e -> {
                String task = textField.getText();
                String description = descriptionField.getText();
                String dueDate = dueDateField.getText();
                int priority = Integer.parseInt(priorityField.getText());
                boolean completionStatus = completionStatusCheckBox.isSelected();

                if (!task.isEmpty() && !dueDate.isEmpty() && !priorityField.getText().isEmpty()) {
                    insertTask(task, description, dueDate, priority, completionStatus, frame, textField, dueDateField, priorityField, completionStatusCheckBox, taskList);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please fill all fields.");
                }
            });

            deleteButton.addActionListener(e -> deleteTask(frame, taskList));

            updateButton.addActionListener(e -> updateTask(frame, taskList));

            searchButton.addActionListener(e -> searchTasks(searchField, taskList, frame));

            sortButton.addActionListener(e -> {
                String selectedSort = (String) sortComboBox.getSelectedItem();
                sortTasks(selectedSort, taskList);
            });

            refreshTaskList(taskList);
        });
    }

    private static void insertTask(String task, String description, String dueDate, int priority, boolean completionStatus, JFrame frame, JTextField textField, JTextField dueDateField, JTextField priorityField, JCheckBox completionStatusCheckBox, JList<String> taskList) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Tasks (TaskNAME, Description, DueDate, Priority, CompletionStatus) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, task);
                stmt.setString(2, description);
                stmt.setString(3, dueDate);
                stmt.setInt(4, priority);
                stmt.setBoolean(5, completionStatus);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Task added successfully!");
                    textField.setText("");
                    dueDateField.setText("");
                    priorityField.setText("");
                    completionStatusCheckBox.setSelected(false);
                    refreshTaskList(taskList);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding task: " + ex.getMessage());
        }
    }

    private static void deleteTask(JFrame frame, JList<String> taskList) {
        String taskIdString = JOptionPane.showInputDialog(frame, "Enter Task ID to delete:");
        if (taskIdString != null && !taskIdString.isEmpty()) {
            try {
                int taskId = Integer.parseInt(taskIdString);
                new DatabaseConnection().deleteTask(taskId);
                JOptionPane.showMessageDialog(frame, "Task deleted successfully!");
                refreshTaskList(taskList);
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error deleting task: " + ex.getMessage());
            }
        }
    }

    private static void updateTask(JFrame frame, JList<String> taskList) {
        String taskIdString = JOptionPane.showInputDialog(frame, "Enter Task ID to update:");
        if (taskIdString != null && !taskIdString.isEmpty()) {
            try {
                int taskId = Integer.parseInt(taskIdString);
                String newTaskName = JOptionPane.showInputDialog(frame, "Enter new task name:");
                String newDescription = JOptionPane.showInputDialog(frame, "Enter new description:");
                String newDueDate = JOptionPane.showInputDialog(frame, "Enter new due date (YYYY-MM-DD):");
                int newPriority = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter new priority (Integer):"));
                boolean newCompletionStatus = JOptionPane.showConfirmDialog(frame, "Is the task completed?") == JOptionPane.YES_OPTION;

                if (newTaskName != null && !newTaskName.isEmpty() && newDescription != null && !newDescription.isEmpty()) {
                    new DatabaseConnection().updateTask(taskId, newTaskName, newDescription, newDueDate, newPriority, newCompletionStatus);
                    JOptionPane.showMessageDialog(frame, "Task updated successfully!");
                    refreshTaskList(taskList);
                }
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error updating task: " + ex.getMessage());
            }
        }
    }

    private static void searchTasks(JTextField searchField, JList<String> taskList, JFrame frame) {
        String searchText = searchField.getText();
        if (!searchText.isEmpty()) {
            try {
                List<String> searchResults = new DatabaseConnection().searchTasks(searchText);
                taskList.setListData(searchResults.toArray(new String[0]));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error searching tasks: " + ex.getMessage());
            }
        } else {
            refreshTaskList(taskList);
        }
    }

    private static void refreshTaskList(JList<String> list) {
        try {
            List<String> tasks = new DatabaseConnection().getAllTasksWithIds();
            list.setListData(tasks.toArray(new String[0]));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch tasks: " + e.getMessage());
        }
    }

    private static void sortTasks(String sortType, JList<String> list) {
        try {
            List<String> sortedTasks;
            switch (sortType) {
                case "Priority":
                    sortedTasks = new DatabaseConnection().getTasksSortedBy("Priority");
                    break;
                case "CompletionStatus":
                    sortedTasks = new DatabaseConnection().getTasksSortedBy("CompletionStatus");
                    break;
                case "Due Date":
                    sortedTasks = new DatabaseConnection().getTasksSortedBy("DueDate");
                    break;
                default:
                    sortedTasks = new DatabaseConnection().getAllTasksWithIds(); // Default to unsorted
                    break;
            }
            list.setListData(sortedTasks.toArray(new String[0]));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Failed to sort tasks: " + ex.getMessage());
        }
    }
}
