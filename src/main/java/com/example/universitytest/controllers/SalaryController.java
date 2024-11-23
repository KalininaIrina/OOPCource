package com.example.universitytest.controllers;

import com.example.universitytest.models.Employee;
import com.example.universitytest.services.EmployeeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.universitytest.Main;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SalaryController {
    @FXML
    private TableView<Employee> salaryTable;

    @FXML
    private TableColumn<Employee, Integer> idColumn;

    @FXML
    private TableColumn<Employee, String> nameColumn;

    @FXML
    private TableColumn<Employee, Double> salaryColumn;

    @FXML
    private TextField salaryField;

    @FXML
    private Label totalSalaryLabel;

    private ObservableList<Employee> employees = FXCollections.observableArrayList();

    // Этот метод вызывается после загрузки FXML
    @FXML
    private void initialize() {
        // Привязываем столбцы таблицы к свойствам модели Employee
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        salaryColumn.setCellValueFactory(cellData -> cellData.getValue().baseSalaryProperty().asObject());

        // Добавляем тестовые данные
        employees.addAll(
                new Employee(1, "Иван", "Иванов", "Петрович", "IT", "Разработчик"),
                new Employee(2, "Мария", "Сидорова", "Васильевна", "HR", "Менеджер")
        );
        employees.get(0).setBaseSalary(50000);
        employees.get(1).setBaseSalary(60000);

        salaryTable.setItems(employees);
        updateTotalSalary();
    }

    @FXML
    private void handleUpdateSalary() {
        Employee selectedEmployee = salaryTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try {
                double newSalary = Double.parseDouble(salaryField.getText());
                selectedEmployee.setBaseSalary(newSalary);
                salaryTable.refresh();
                updateTotalSalary();
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Введите корректное значение зарплаты!");
            }
        } else {
            showAlert("Ошибка", "Выберите сотрудника для обновления зарплаты.");
        }
    }

    private void updateTotalSalary() {
        double total = employees.stream().mapToDouble(Employee::getBaseSalary).sum();
        totalSalaryLabel.setText("Общая зарплата: " + total);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleOpenSalaryView(ActionEvent event) throws IOException {
        // Вызываем метод для отображения окна калькулятора зарплаты
        Main main = new Main();
        main.showSalaryView();
    }

}
