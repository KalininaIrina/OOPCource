package com.example.universitytest.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/university";
    private static final String USER = "root";
    private static final String PASSWORD = "nt7n7xrtpy";

    // Метод для получения соединения с базой данных
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Метод для тестирования подключения
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Подключение успешно!");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }
}
