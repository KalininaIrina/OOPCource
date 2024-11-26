package com.example.universitytest.services;

import com.example.universitytest.models.Employee;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    private final Connection connection;

    // Конструктор сервиса
    public EmployeeService(Connection connection) {
        this.connection = connection;
    }

    // Метод для добавления сотрудника
    public void addEmployee(Employee employee) {
        String query = "INSERT INTO employees (first_name, last_name, surname, position_id, base_salary, years_worked, academic_degree, hours_worked) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getSurname());
            stmt.setInt(4, employee.getPositionId());
            stmt.setDouble(5, employee.getBaseSalary());
            stmt.setInt(6, employee.getYearsWorked());
            stmt.setBoolean(7, employee.hasAcademicDegree()); // Передача учёной степени
            stmt.setInt(8, 0); // Значение по умолчанию для часов работы
            stmt.executeUpdate();
            System.out.println("Сотрудник добавлен: " + employee.getFirstName() + " " + employee.getLastName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Метод для удаления сотрудника по ID
    public boolean removeEmployee(int id) {
        String query = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Метод для обновления данных сотрудника по ID
    public boolean updateEmployee(Employee updatedEmployee) {
        String query = "UPDATE employees SET first_name = ?, last_name = ?, surname = ?, position_id = ?, base_salary = ?, years_worked = ?, academic_degree = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, updatedEmployee.getFirstName());
            stmt.setString(2, updatedEmployee.getLastName());
            stmt.setString(3, updatedEmployee.getSurname());
            stmt.setInt(4, updatedEmployee.getPositionId());
            stmt.setDouble(5, updatedEmployee.getBaseSalary());
            stmt.setInt(6, updatedEmployee.getYearsWorked());
            stmt.setBoolean(7, updatedEmployee.hasAcademicDegree());
            stmt.setInt(8, updatedEmployee.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Получение всех сотрудников
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("surname"),
                        rs.getInt("position_id"), // Получаем position_id
                        rs.getDouble("base_salary"),
                        rs.getInt("years_worked"),
                        rs.getBoolean("academic_degree") // Получаем учёную степень
                );
                employees.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }


    // Метод для получения информации о сотруднике по ID
    public Employee getEmployeeById(int id) {
        String query = "SELECT * FROM employees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("surname"),
                            rs.getInt("position_id"), // Получаем position_id
                            rs.getDouble("base_salary"),
                            rs.getInt("years_worked"),
                            rs.getBoolean("academic_degree") // Получаем учёную степень
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPositionById(int positionId) {
        String position = "";
        String query = "SELECT position_name FROM positions WHERE position_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, positionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                position = rs.getString("position_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return position;
    }

    // Метод для фильтрации сотрудников по отделу
    public List<Employee> filterEmployeesByPositionId(int positionId) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees WHERE position_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, positionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("surname"),
                            rs.getInt("position_id"), // Получаем position_id
                            rs.getDouble("base_salary"),
                            rs.getInt("years_worked"),
                            rs.getBoolean("academic_degree") // Получаем учёную степень
                    );
                    employees.add(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }


    // Метод для создания отчета по всем сотрудникам
    public void generateEmployeeReport() {
        List<Employee> employees = getEmployees();
        for (Employee emp : employees) {
            System.out.println("ID: " + emp.getId() +
                    ", Имя Фамилия: " + emp.getFirstName() + " " + emp.getLastName() +
                    ", Отчество: " + emp.getSurname() +
                    ", ID позиции: " + emp.getPositionId() +
                    ", Оклад: " + emp.getBaseSalary() +
                    ", Стаж работы: " + emp.getYearsWorked() +
                    ", Учёная степень: " + (emp.hasAcademicDegree() ? "Да" : "Нет"));
        }
    }

}
