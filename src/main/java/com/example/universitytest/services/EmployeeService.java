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
        String query = "INSERT INTO employees (first_name, last_name, surname, position_id, base_salary, years_worked, academic_degree, department_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getSurname());
            stmt.setInt(4, employee.getPositionId());
            stmt.setDouble(5, employee.getBaseSalary());
            stmt.setInt(6, employee.getYearsWorked());
            stmt.setBoolean(7, employee.hasAcademicDegree());
            stmt.setInt(8, employee.getDepartmentId());

            stmt.executeUpdate(); // Вставляем запись в базу данных

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    employee.setId(rs.getInt(1)); // Устанавливаем ID для объекта
                }
            }

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

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employeeList = new ArrayList<>();
        String query = "SELECT * FROM employees";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee(); // Пустой конструктор
                employee.setId(rs.getInt("id")); // Устанавливаем ID из базы
                employee.setFirstName(rs.getString("first_name"));
                employee.setLastName(rs.getString("last_name"));
                employee.setSurname(rs.getString("surname"));
                employee.setPositionId(rs.getInt("position_id"));
                employee.setBaseSalary(rs.getDouble("base_salary"));
                employee.setYearsWorked(rs.getInt("years_worked"));
                employee.setAcademicDegree(rs.getBoolean("academic_degree"));
                employee.setDepartmentId(rs.getInt("department_id"));
                employee.setHoursWorked(rs.getDouble("hours_worked"));

                employeeList.add(employee);
            }
        }

        return employeeList;
    }



    // Метод для обновления данных сотрудника по ID
    public void updateEmployee(Employee employee) throws SQLException {
        String query = "UPDATE employees SET first_name = ?, last_name = ?, surname = ?, base_salary = ?, years_worked = ?, " +
                "position_id = ?, academic_degree = ?, department_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getSurname());
            stmt.setDouble(4, employee.getBaseSalary());
            stmt.setInt(5, employee.getYearsWorked());
            stmt.setInt(6, employee.getPositionId());
            stmt.setBoolean(7, employee.hasAcademicDegree());
            stmt.setInt(8, employee.getDepartmentId());
            stmt.setInt(9, employee.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Сотрудник с ID " + employee.getId() + " не найден.");
            }
        }
    }



    // Получение всех сотрудников
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("surname"),
                        rs.getInt("position_id"),
                        rs.getDouble("base_salary"),
                        rs.getInt("years_worked"),
                        rs.getBoolean("academic_degree"),
                        rs.getInt("department_id")
                );

                emp.setId(rs.getInt("id")); // Устанавливаем ID после создания объекта
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
                    Employee emp = new Employee(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("surname"),
                            rs.getInt("position_id"),
                            rs.getDouble("base_salary"),
                            rs.getInt("years_worked"),
                            rs.getBoolean("academic_degree"),
                            rs.getInt("department_id")
                    );
                    emp.setId(rs.getInt("id")); // Устанавливаем ID после создания объекта
                    return emp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPositionById(int positionId) {
        String query = "SELECT position_name FROM positions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, positionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("position_name");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Выведите исключение для отладки
        }
        return "Неизвестная должность"; // Если должность не найдена
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
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("surname"),
                            rs.getInt("position_id"),
                            rs.getDouble("base_salary"),
                            rs.getInt("years_worked"),
                            rs.getBoolean("academic_degree"),
                            rs.getInt("department_id")
                    );
                    emp.setId(rs.getInt("id")); // Устанавливаем ID после создания объекта
                    employees.add(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }


    public List<String> getAllPositions() {
        List<String> positions = new ArrayList<>();
        String query = "SELECT position_name FROM positions"; // Запрос для получения всех должностей

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                positions.add(rs.getString("position_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }


    public int getPositionIdByName(String positionName) {
        String query = "SELECT id FROM positions WHERE position_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, positionName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Возвращаем -1, если должность не найдена
    }

    public String getDepartmentById(int departmentId) {
        String query = "SELECT name FROM departments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Неизвестный отдел";
    }

    public List<String> getAllDepartments() {
        List<String> departments = new ArrayList<>();
        String query = "SELECT name FROM departments";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }


    public int getDepartmentIdByName(String departmentName) {
        String query = "SELECT id FROM departments WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, departmentName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Возвращаем -1, если департамент не найден
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

    public void updateHoursWorked(int employeeId, double hoursWorked) throws SQLException {
        String query = "UPDATE employees SET hours_worked = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, hoursWorked);
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
        }
    }

}
