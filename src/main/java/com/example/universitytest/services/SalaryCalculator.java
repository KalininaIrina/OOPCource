package com.example.universitytest.services;
import com.example.universitytest.models.Employee;

public class SalaryCalculator {
    private Employee employee; //ссылка на сотрудника, для которого ведется расчет.
    private double totalSalary; //итоговая зарплата после расчетов

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

        totalSalary = baseSalary + allowances - deductions;
        return totalSalary;
    }

    // Метод для расчета надбавок (пример)
    private double calculateAllowances() {
        // Здесь можно добавить логику расчета надбавок в зависимости от позиции и других факторов
        return employee.getBaseSalary() * 0.2; // Например, 20% надбавка
    }

    // Метод для расчета вычетов (пример)
    private double calculateDeductions() {
        // Здесь можно добавить логику расчета вычетов (налоги, страховые взносы и т.д.)
        return employee.getBaseSalary() * 0.1; // Например, 10% вычеты
    }

    // Метод для создания отчета по зарплате
    public String generateReport() {
        if (employee == null) {
            throw new IllegalStateException("Сотрудник не существует");
        }

        StringBuilder report = new StringBuilder();
        report.append("Отчет по заработной плате для сотрудника: ").append(employee.getFirstName()).append(" ").append(employee.getLastName()).append("\n")
                .append("Должность: ").append(employee.getPosition()).append("\n")
                .append("Оклад: ").append(employee.getBaseSalary()).append("\n")
                .append("Надбавки: ").append(calculateAllowances()).append("\n")
                .append("Вычеты: ").append(calculateDeductions()).append("\n")
                .append("Итоговая сумма заработной платы: ").append(calculateNetSalary()).append("\n");

        return report.toString();
    }
}