package com.scheduler.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "created_at TEXT DEFAULT (datetime('now'))"
                + ");";

        String createTasksTable = "CREATE TABLE IF NOT EXISTS tasks ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "title TEXT NOT NULL, "
                + "description TEXT, "
                + "due_date TEXT, "
                + "start_time TEXT, "
                + "priority TEXT, "
                + "status TEXT, "
                + "recurrence TEXT, "
                + "category TEXT, "
                + "created_at TEXT DEFAULT (datetime('now')), "
                + "updated_at TEXT DEFAULT (datetime('now')), "
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ");";

        String createTrigger = "CREATE TRIGGER IF NOT EXISTS update_task_updated_at " 
                + "AFTER UPDATE ON tasks "
                + "FOR EACH ROW "
                + "BEGIN "
                + "UPDATE tasks SET updated_at = datetime('now') WHERE id = OLD.id; "
                + "END;";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createTasksTable);
            stmt.execute(createTrigger);
            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

