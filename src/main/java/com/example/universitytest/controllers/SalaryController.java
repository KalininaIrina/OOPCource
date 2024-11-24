package com.example.universitytest.controllers;

import com.example.universitytest.models.Employee;
import com.example.universitytest.services.SalaryCalculator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.text.Text;

public class SalaryController {

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

    public void initialize(Employee employee) {
        this.currentEmployee = employee;
        this.salaryCalculator = new SalaryCalculator(currentEmployee);

        // Заполняем информацию о сотруднике
        employeeNameText.setText(currentEmployee.getLastName() + " " + currentEmployee.getFirstName() + " " + currentEmployee.getSurname());
        positionText.setText("Должность: " + currentEmployee.getPosition());
        baseSalaryText.setText("Оклад: " + currentEmployee.getBaseSalary());
        yearsWorkedText.setText("Стаж: " + currentEmployee.getYearsWorked() + " лет");
        academicDegreeText.setText("Учёная степень: " + (currentEmployee.hasAcademicDegree() ? "Есть" : "Нет"));
    }

    @FXML
    private void handleCalculateSalary() {
        try {
            // Проверяем, что поля для ввода не пустые
            if (hoursWorkedField.getText().isEmpty() || bonusField.getText().isEmpty()) {
                showAlert("Ошибка", "Пожалуйста, заполните все поля.");
                return;
            }

            // Преобразуем строки в числовые значения
            double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
            double bonusPercent = Double.parseDouble(bonusField.getText());

            // Рассчитываем итоговую зарплату
            double calculatedSalary = salaryCalculator.calculateNetSalary(); // Рассчитаем начальную зарплату
            double bonus = (bonusPercent / 100) * calculatedSalary; // Рассчитываем бонус
            calculatedSalary += bonus; // Добавляем бонус

            // Если выбран флажок для ученой степени, применяем 10% увеличение
            if (applyAcademicDegreeCheck.isSelected()) {
                calculatedSalary += calculatedSalary * 0.1; // 10% добавка за ученую степень
            }

            // Округляем итоговую зарплату до двух знаков
            calculatedSalary = Math.round(calculatedSalary * 100.0) / 100.0;

            // Отображаем итоговую зарплату
            totalSalaryLabel.setText("Итоговая зарплата: " + calculatedSalary);

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректные данные для расчета.");
        }
    }


    @FXML
    private void handleGenerateReport() {
        try {
            double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
            String report = salaryCalculator.generateReport(hoursWorked);
            // Показываем отчет (например, в диалоге или выводим в консоль)
            System.out.println(report);

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректное количество часов.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
