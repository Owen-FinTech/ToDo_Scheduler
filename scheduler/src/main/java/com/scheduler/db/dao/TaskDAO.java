package com.scheduler.db.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.scheduler.db.DatabaseUtil;
import com.scheduler.model.Task;

public class TaskDAO {

    public static void addTask(Task task) {
        String sql = "INSERT INTO tasks (user_id, title, description, due_date, start_time, priority, status, recurrence, category) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, task.getUserId());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getDueDate());
            pstmt.setString(5, task.getStartTime());
            pstmt.setString(6, task.getPriority());
            pstmt.setString(7, task.getStatus());
            pstmt.setString(8, task.getRecurrence());
            pstmt.setString(9, task.getCategory());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getAllTasks(int currentUserId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ?;";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setUserId(rs.getInt("user_id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setDueDate(rs.getString("due_date"));
                task.setStartTime(rs.getString("start_time"));
                task.setPriority(rs.getString("priority"));
                task.setStatus(rs.getString("status"));
                task.setRecurrence(rs.getString("recurrence"));
                task.setCategory(rs.getString("category"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void updateTask(Task task) {

        String sql = "UPDATE tasks "
                + "SET title = ?, description = ?, "
                + "due_date = ?, start_time = ?, priority = ?, "
                + "status = ?, recurrence = ?, category = ? "
                + "WHERE id = ?;";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDueDate());
            pstmt.setString(4, task.getStartTime());
            pstmt.setString(5, task.getPriority());
            pstmt.setString(6, task.getStatus());
            pstmt.setString(7, task.getRecurrence());
            pstmt.setString(8, task.getCategory());
            pstmt.setInt(9, task.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(Task task) {
        String sql = "DELETE FROM tasks "
                + "WHERE id = ?;";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, task.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
