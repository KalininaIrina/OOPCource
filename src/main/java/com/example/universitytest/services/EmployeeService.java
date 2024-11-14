package com.example.universitytest.services;

import com.example.universitytest.models.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeService {
    private List<Employee> employees; //список сотрудников

    // Конструктор
    public EmployeeService() {
        this.employees = new ArrayList<>();
    }

    // Метод для добавления сотрудника
    public void addEmployee(Employee employee) {
        employees.add(employee);
        System.out.println("Добавлен сотрудник: " + employee.getFirstName() + " " + employee.getLastName());
    }

    // Метод для удаления сотрудника по ID
    public boolean removeEmployee(int id) {
        return employees.removeIf(emp -> emp.getId() == id);
    }

    // Метод для обновления данных сотрудника по ID
    public boolean updateEmployee(Employee updatedEmployee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == updatedEmployee.getId()) {
                employees.set(i, updatedEmployee);
                System.out.println("Сотрудник изменен: " + updatedEmployee.getFirstName() + " " + updatedEmployee.getLastName());
                return true;
            }
        }
        return false;
    }

    // Получение всех сотрудников
    public List<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    // Метод для фильтрации сотрудников по определенному критерию
    public List<Employee> filterEmployeesByDepartment(String departmentName) {
        return employees.stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(departmentName))
                .collect(Collectors.toList());
    }

    // Метод для сортировки сотрудников по имени
    public List<Employee> sortEmployeesByName(boolean ascending) {
        return employees.stream()
                .sorted((e1, e2) -> ascending ? e1.getFirstName().compareTo(e2.getFirstName())
                        : e2.getFirstName().compareTo(e1.getFirstName()))
                .collect(Collectors.toList());
    }

    // Метод для получения информации о сотруднике по ID
    public Employee getEmployeeById(int id) {
        return employees.stream()
                .filter(emp -> emp.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // Метод для создания отчета по всем сотрудникам
    public void generateEmployeeReport() {
        for (Employee emp : employees) {
            System.out.println("ID: " + emp.getId() + ", Имя Фамилия: " + emp.getFirstName() + " " + emp.getLastName() +
                    ", Отдел: " + emp.getDepartment() + ", Должность: " + emp.getPosition() +
                    ", Оклад: " + emp.getBaseSalary());
        }
    }
}
