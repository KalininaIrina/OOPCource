package com.example.universitytest.controllers;

import com.example.universitytest.database.DatabaseConnection;
import com.example.universitytest.models.Employee;
import com.example.universitytest.services.EmployeeService;
import com.example.universitytest.services.SalaryCalculator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class SalaryController {

    @FXML
    public Button generateReportButton;
    @FXML
    private Text employeeNameText;
    @FXML
    private Text positionText;
    @FXML
    private Text baseSalaryText;
    @FXML
    private Text yearsWorkedText;
    @FXML
    private Text academicDegreeText;

    @FXML
    private TextField hoursWorkedField;
    @FXML
    private TextField bonusField;

    @FXML
    private CheckBox applyAcademicDegreeCheck;

    @FXML
    private Label totalSalaryLabel;

    private Employee currentEmployee;
    private SalaryCalculator salaryCalculator;
    private EmployeeService employeeService;

    //private Connection connection;

    public void initialize(Employee employee) {
        this.currentEmployee = employee;
        try {
            this.employeeService = new EmployeeService(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка подключения к базе данных.");
            throw new RuntimeException(e);
        }
        this.salaryCalculator = new SalaryCalculator(currentEmployee, employeeService);

        setupEmployeeData();
        setupInputListeners();
    }

    public Employee getEmployee() {
        return currentEmployee;
    }

    private void setupEmployeeData() {
        String positionName = employeeService.getPositionById(currentEmployee.getPositionId());

        employeeNameText.setText(currentEmployee.getLastName() + " " + currentEmployee.getFirstName() + " " + currentEmployee.getSurname());
        positionText.setText("Должность: " + positionName);
        baseSalaryText.setText("Оклад: " + currentEmployee.getBaseSalary());
        yearsWorkedText.setText("Стаж: " + currentEmployee.getYearsWorked() + " лет");
        academicDegreeText.setText("Учёная степень: " + (currentEmployee.hasAcademicDegree() ? "Есть" : "Нет"));
    }

    private void setupInputListeners() {
        // Только числовой ввод
        hoursWorkedField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                hoursWorkedField.setText(oldValue);
            }
        });

        bonusField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                bonusField.setText(oldValue);
            }
        });
    }

    //SalaryCalculator salaryCalculator = new SalaryCalculator(currentEmployee);
    @FXML
    private void handleCalculateSalary() {
        try {
            // Получаем отработанные часы из поля ввода
            if (hoursWorkedField.getText().isEmpty()) {
                showAlert("Ошибка", "Пожалуйста, введите количество отработанных часов.");
                return;
            }

            double hoursWorked = Double.parseDouble(hoursWorkedField.getText());

            // 1. Сохраняем отработанные часы в базе данных
            saveHoursWorkedToDatabase(hoursWorked);

            // 2. Рассчитываем зарплату
            calculateSalary(hoursWorked);

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректное количество часов.");
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка при сохранении данных в базу.");
        }
    }

    // Метод для сохранения отработанных часов в базу данных
    private void saveHoursWorkedToDatabase(double hoursWorked) throws SQLException {
        Employee employee = getEmployee(); // Получаем текущего сотрудника
        EmployeeService employeeService = new EmployeeService(DatabaseConnection.getConnection());
        SalaryCalculator salaryCalculator = new SalaryCalculator(employee, employeeService, DatabaseConnection.getConnection());
        salaryCalculator.updateHoursWorked(employee.getId(), hoursWorked);
    }

    // Метод для расчета зарплаты
    private void calculateSalary(double hoursWorked) {
        Employee employee = getEmployee(); // Получаем текущего сотрудника
        EmployeeService employeeService = null;
        try {
            employeeService = new EmployeeService(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SalaryCalculator salaryCalculator = null;
        try {
            salaryCalculator = new SalaryCalculator(employee, employeeService, DatabaseConnection.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        double totalSalary = salaryCalculator.calculateTotalSalary(hoursWorked, applyAcademicDegreeCheck.isSelected());

        // Отображаем рассчитанную зарплату
        totalSalaryLabel.setText("Итоговая зарплата: " + totalSalary);
    }


    /*@FXML
    private void handleSaveHoursWorked() {
        try {
            // Проверяем, что поле для ввода часов не пустое
            if (hoursWorkedField.getText().isEmpty()) {
                showAlert("Ошибка", "Пожалуйста, введите количество отработанных часов.");
                return;
            }

            // Получаем количество отработанных часов
            double hoursWorked = Double.parseDouble(hoursWorkedField.getText());

            // Получаем текущего сотрудника
            Employee employee = getEmployee(); // Метод для получения текущего сотрудника

            // Создаем объект EmployeeService для работы с данными
            EmployeeService employeeService = new EmployeeService(DatabaseConnection.getConnection());

            // Создаем объект SalaryCalculator с переданными параметрами
            SalaryCalculator salaryCalculator = new SalaryCalculator(employee, employeeService, DatabaseConnection.getConnection());

            // Обновляем отработанные часы в базе данных
            salaryCalculator.updateHoursWorked(employee.getId(), hoursWorked);

            // Обновляем поле у сотрудника (если оно нужно для дальнейших вычислений)
            employee.setHoursWorked(hoursWorked);

            // Отображаем успешное сообщение
            showAlert("Успех", "Отработанные часы успешно обновлены!");

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректное количество часов.");
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка при обновлении данных в базе.");
        }
    }*/



    @FXML
    private void handleGenerateReport() {
        try {
            double hoursWorked = getValidatedInput(hoursWorkedField, "Часы работы");
            String report = salaryCalculator.generateReport(hoursWorked);
            saveReportToFile(report);
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    private double getValidatedInput(TextField field, String fieldName) {
        if (field.getText().isEmpty()) {
            throw new IllegalArgumentException("Пожалуйста, заполните поле: " + fieldName);
        }
        try {
            double value = Double.parseDouble(field.getText());
            if (value < 0) {
                throw new IllegalArgumentException(fieldName + " должно быть положительным числом.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Введите корректное число в поле: " + fieldName);
        }
    }

    private void saveReportToFile(String report) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("salary_report.txt");
        File file = fileChooser.showSaveDialog(totalSalaryLabel.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(report);
                showAlert("Успех", "Отчет успешно сохранен!");
            } catch (IOException e) {
                showAlert("Ошибка", "Ошибка при сохранении отчета.");
            }
        }
    }

    private String formatDouble(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toString();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCloseSalaryView() {
        Stage stage = (Stage) totalSalaryLabel.getScene().getWindow();
        stage.close();
    }
}
