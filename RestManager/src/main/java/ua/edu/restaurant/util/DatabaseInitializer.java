package ua.edu.restaurant.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Таблиця страв
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS dishes (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    name        TEXT    NOT NULL,
                    price       REAL    NOT NULL,
                    category    TEXT    NOT NULL,
                    description TEXT
                )
            """);

            // Таблиця клієнтів
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS clients (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    name  TEXT NOT NULL,
                    phone TEXT
                )
            """);

            // Таблиця замовлень
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    client_id  INTEGER NOT NULL,
                    status     TEXT    NOT NULL DEFAULT 'CREATED',
                    created_at TEXT    NOT NULL,
                    FOREIGN KEY (client_id) REFERENCES clients(id)
                )
            """);

            // Таблиця позицій замовлення
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id INTEGER NOT NULL,
                    dish_id  INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (dish_id)  REFERENCES dishes(id)
                )
            """);

            // Таблиця персоналу
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS employees (
                    id     INTEGER PRIMARY KEY AUTOINCREMENT,
                    name   TEXT    NOT NULL,
                    role   TEXT    NOT NULL,
                    salary REAL    NOT NULL,
                    extra  TEXT
                )
            """);

            System.out.println("[DB] Таблиці успішно ініціалізовано.");

        } catch (SQLException e) {
            System.out.println("[DB] Помилка ініціалізації БД: " + e.getMessage());
        }
    }
}
