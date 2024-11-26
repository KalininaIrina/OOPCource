package com.example.universitytest.services;

import com.example.universitytest.database.DatabaseConnection;
import com.example.universitytest.models.Employee;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class SalaryCalculator {
    private Employee employee; //ссылка на сотрудника, для которого ведется расчет.
    private double totalSalary; // итоговая зарплата после расчетов

    public SalaryCalculator() {
    }

    public SalaryCalculator(Employee employee) {
        this.employee = employee;
        this.totalSalary = 0; // Начальное значение
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    // Метод для расчета чистой зарплаты
    public double calculateNetSalary() {
        if (employee == null) {
            throw new IllegalStateException("Сотрудника не существует");
        }

        double baseSalary = employee.getBaseSalary();
        double allowances = calculateAllowances();
        double deductions = calculateDeductions();

        // Рассчитываем итоговую зарплату
        totalSalary = baseSalary + allowances - deductions;

        // Округляем итоговую зарплату
        BigDecimal roundedSalary = new BigDecimal(totalSalary).setScale(2, RoundingMode.HALF_UP);

        // Обновляем totalSalary округленным значением
        totalSalary = roundedSalary.doubleValue();

        return totalSalary;
    }



    // Метод для расчета надбавок
    public double calculateAllowances() {
        double baseSalary = employee.getBaseSalary();
        double allowances = 0;

        // Надбавка за стаж: 1% за каждый год работы
        int yearsWorked = employee.getYearsWorked();
        allowances += baseSalary * 0.01 * yearsWorked;

        // Надбавка за ученую степень
        if (employee.hasAcademicDegree()) {
            allowances += baseSalary * 0.10; // 10% за ученую степень
        }

        // Надбавка за количество часов (если это преподаватель)
        /*if (employee.getPosition().equalsIgnoreCase("Преподаватель") && employee.getHoursWorked() > 0) {
            allowances += employee.getHoursWorked() * 1000; // 1000 рублей за каждый рабочий час
        }*/

        return allowances;
    }

    // Метод для расчета вычетов
    public double calculateDeductions() {
        double baseSalary = employee.getBaseSalary();

        // Пример вычета налога на доходы физических лиц (НДФЛ): 13% от общей зарплаты
        double tax = baseSalary * 0.13;

        // Пример других вычетов (например, пенсионных взносов и страховых взносов)
        double otherDeductions = baseSalary * 0.05; // Допустим, 5% других вычетов

        return tax + otherDeductions;
    }

    // Метод для создания отчета по зарплате
    public String generateReport(double hoursWorked) {
        if (employee == null) {
            throw new IllegalStateException("Сотрудник не существует");
        }

        String positionName = null;
        try {
            positionName = new EmployeeService(DatabaseConnection.getConnection()).getPositionById(employee.getPositionId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        StringBuilder report = new StringBuilder();
        double baseSalary = employee.getBaseSalary();
        double allowances = calculateAllowances();
        double deductions = calculateDeductions();
        double hourlyBonus = 0;

        // Если сотрудник преподаватель, добавляем надбавку за часы
        if (positionName.equalsIgnoreCase("Преподаватель") && hoursWorked > 0) {
            hourlyBonus = hoursWorked * 1000; // 1000 рублей за час
            allowances += hourlyBonus;
        }

        // Округление значений до двух знаков
        BigDecimal roundedBaseSalary = new BigDecimal(baseSalary).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedAllowances = new BigDecimal(allowances).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedDeductions = new BigDecimal(deductions).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedHourlyBonus = new BigDecimal(hourlyBonus).setScale(2, RoundingMode.HALF_UP);

        // Рассчитываем чистую зарплату и округляем ее
        BigDecimal netSalary = roundedBaseSalary.add(roundedAllowances).subtract(roundedDeductions);
        BigDecimal roundedNetSalary = netSalary.setScale(2, RoundingMode.HALF_UP);

        // Формируем отчет
        report.append("Отчет по заработной плате для сотрудника: ")
                .append(employee.getLastName()).append(" ").append(employee.getFirstName()).append(" ").append(employee.getSurname()).append("\n")
                .append("Должность: ").append(positionName).append("\n")
                .append("Оклад: ").append(roundedBaseSalary).append("\n")
                .append("Надбавка за стаж: ").append(new BigDecimal(baseSalary * 0.01 * employee.getYearsWorked()).setScale(2, RoundingMode.HALF_UP)).append("\n")
                .append("Надбавка за ученую степень: ").append(employee.hasAcademicDegree() ? new BigDecimal(baseSalary * 0.10).setScale(2, RoundingMode.HALF_UP) : "0.00").append("\n")
                .append("Надбавка за количество часов: ").append(roundedHourlyBonus).append("\n")
                .append("Вычеты (налог + другие): ").append(roundedDeductions).append("\n")
                .append("Итоговая сумма заработной платы (чистая): ").append(roundedNetSalary).append("\n");

        return report.toString();
    }



}
