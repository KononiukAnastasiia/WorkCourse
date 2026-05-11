package ua.edu.restaurant.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:restaurant.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            // Вмикаємо підтримку зовнішніх ключів
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            // WAL режим — дозволяє одночасний доступ з кількох програм
            connection.createStatement().execute("PRAGMA journal_mode = WAL");
        }
        return connection;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] З'єднання закрито.");
            }
        } catch (SQLException e) {
            System.out.println("[DB] Помилка закриття: " + e.getMessage());
        }
    }
}
