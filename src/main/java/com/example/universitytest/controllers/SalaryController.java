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

    @FXML
    private void handleCalculateSalary() {
        try {
            double hoursWorked = getValidatedInput(hoursWorkedField, "Часы работы");

            // Вызываем общий метод расчета зарплаты
            double calculatedSalary = salaryCalculator.calculateTotalSalary(
                    hoursWorked,
                    applyAcademicDegreeCheck.isSelected()
            );

            totalSalaryLabel.setText("Итоговая зарплата: " + formatDouble(calculatedSalary));
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

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
