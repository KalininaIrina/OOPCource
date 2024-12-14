package com.example.universitytest.services;

import com.example.universitytest.database.DatabaseConnection;
import com.example.universitytest.models.Employee;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SalaryCalculator {
    private int totalSalary; // Убрали final
    private Employee employee;
    private EmployeeService employeeService;
    private Connection connection;

    public SalaryCalculator(Employee employee, EmployeeService employeeService) {
        this.employee = employee;
        this.employeeService = employeeService;
    }

    public SalaryCalculator(Employee employee, EmployeeService employeeService, Connection connection) {
        this.employee = employee;
        this.employeeService = employeeService;
        this.connection = connection;
    }


    /*public SalaryCalculator(Employee employee) {
        this.employee = employee;
        this.totalSalary = 0;
    }*/

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /*public Employee getEmployee() {
        return employee;
    }*/

    public double calculateTotalSalary(double hoursWorked, boolean includeAcademicDegreeBonus) {

        if (employee == null) {
            throw new IllegalStateException("Сотрудника не существует");
        }

        double baseSalary = employee.getBaseSalary();
        double allowances = calculateAllowances(hoursWorked, includeAcademicDegreeBonus);
        double deductions = calculateDeductions();

        this.totalSalary = (int) round(baseSalary + allowances - deductions, 2);
        return totalSalary;
    }

    public double calculateAllowances(double hoursWorked, boolean includeAcademicDegreeBonus) {
        double baseSalary = employee.getBaseSalary();
        double allowances = 0;

        allowances += baseSalary * 0.01 * employee.getYearsWorked();
        if (includeAcademicDegreeBonus && employee.hasAcademicDegree()) {
            allowances += baseSalary * 0.10;
        }
        if (isLecturer() && hoursWorked > 0) {
            allowances += hoursWorked * 10;
        }

        return allowances;
    }

    public double calculateDeductions() {
        double baseSalary = employee.getBaseSalary();
        double tax = baseSalary * 0.13;
        double otherDeductions = baseSalary * 0.05;
        return tax + otherDeductions;
    }

    public String generateReport(double hoursWorked) {
        if (employee == null) {
            throw new IllegalStateException("Сотрудника не существует");
        }

        // Получение информации о сотруднике
        String positionName = employeeService.getPositionById(employee.getPositionId());
        double baseSalary = employee.getBaseSalary();
        double allowances = calculateAllowances(hoursWorked, true);
        double deductions = calculateDeductions();
        double totalSalary = round(baseSalary + allowances - deductions, 2);

        // Сохранение отчета в таблицу salary_reports
        try {
            employeeService.addSalaryReport(
                    employee.getId(),                // ID сотрудника
                    totalSalary,                     // Итоговая зарплата
                    new Timestamp(System.currentTimeMillis()) // Текущая дата и время
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось сохранить отчет по зарплате в базе данных.");
        }

        // Формирование текста отчета
        return new StringBuilder()
                .append("Отчет по заработной плате для сотрудника: ")
                .append(employee.getLastName()).append(" ").append(employee.getFirstName()).append(" ").append(employee.getSurname()).append("\n")
                .append("Должность: ").append(positionName).append("\n")
                .append("Оклад: ").append(round(baseSalary, 2)).append("\n")
                .append("Надбавка за стаж: ").append(round(baseSalary * 0.01 * employee.getYearsWorked(), 2)).append("\n")
                .append("Надбавка за ученую степень: ").append(employee.hasAcademicDegree() ? round(baseSalary * 0.10, 2) : "0.00").append("\n")
                .append("Надбавка за количество часов: ").append(isLecturer() ? round(hoursWorked * 10, 2) : "0.00").append("\n")
                .append("Вычеты (налоги и взносы): ").append(round(deductions, 2)).append("\n")
                .append("Итоговая зарплата: ").append(totalSalary).append("\n")
                .toString();
    }


    private boolean isLecturer() {
        if (employeeService == null) {
            throw new IllegalStateException("EmployeeService не инициализирован");
        }
        String positionName = employeeService.getPositionById(employee.getPositionId());
        return positionName.equalsIgnoreCase("Преподаватель");
    }

    private static double round(double value, int scale) {
        return new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
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
