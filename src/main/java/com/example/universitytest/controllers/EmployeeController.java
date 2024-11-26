package com.example.universitytest.controllers;

import com.example.universitytest.models.Employee;
import com.example.universitytest.services.EmployeeService;
import com.example.universitytest.database.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class EmployeeController {
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> idColumn;
    @FXML
    private TableColumn<Employee, String> firstNameColumn;
    @FXML
    private TableColumn<Employee, String> lastNameColumn;
    @FXML
    private TableColumn<Employee, String> surnameColumn;
    @FXML
    private TableColumn<Employee, String> departmentColumn;
    @FXML
    private TableColumn<Employee, String> positionColumn;
    @FXML
    private TableColumn<Employee, Double> baseSalaryColumn;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField baseSalaryField;

    // Новые поля
    @FXML
    private TextField yearsWorkedField;
    //@FXML
    //private TextField hoursWorkedField;
    @FXML
    private CheckBox academicDegreeCheck;
    @FXML
    private ComboBox<String> positionComboBox;  // ComboBox для должностей

    private Connection connection;

    {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private EmployeeService employeeService = new EmployeeService(connection);
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        surnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());

        // Получаем все должности из базы данных
        ObservableList<String> positionList = FXCollections.observableArrayList(employeeService.getAllPositions());
        positionComboBox.setItems(positionList); // Устанавливаем должности в ComboBox
        positionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(employeeService.getPositionById(cellData.getValue().getPositionId())));

        baseSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().baseSalaryProperty().asObject());

        // Заполняем список сотрудников из базы данных
        employeeList.addAll(employeeService.getEmployees());
        employeeTable.setItems(employeeList);
    }


    @FXML
    private TextField positionField;

    @FXML
    private void handleAddEmployee() {
        // Получаем выбранную должность из ComboBox
        String selectedPosition = positionComboBox.getSelectionModel().getSelectedItem();
        if (selectedPosition != null) {
            // Преобразуем название должности в ID
            int positionId = employeeService.getPositionIdByName(selectedPosition);

            // Преобразуем другие значения
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String surname = surnameField.getText();
            double baseSalary = Double.parseDouble(baseSalaryField.getText());
            int yearsWorked = Integer.parseInt(yearsWorkedField.getText());
            boolean hasAcademicDegree = academicDegreeCheck.isSelected();

            // Создаем нового сотрудника без ID (оно будет назначено в базе данных)
            Employee newEmployee = new Employee(
                    firstName,
                    lastName,
                    surname,
                    positionId,
                    baseSalary,
                    yearsWorked,
                    hasAcademicDegree
            );

            // Добавляем сотрудника в базу данных и в таблицу
            employeeService.addEmployee(newEmployee);
            employeeList.add(newEmployee);
            // Очищаем все поля
            firstNameField.clear();  // Очищаем имя
            lastNameField.clear();   // Очищаем фамилию
            surnameField.clear();    // Очищаем отчество
            positionComboBox.getSelectionModel().clearSelection();  // Очищаем выбор в ComboBox
            baseSalaryField.clear();  // Очищаем оклад
            yearsWorkedField.clear();  // Очищаем стаж
            academicDegreeCheck.setSelected(false);  // Сбрасываем чекбокс

            // Обновляем таблицу
            employeeTable.refresh(); // Обновляем таблицу в UI

        } else {
            showAlert("Ошибка", "Пожалуйста, выберите должность.");
        }
    }

    @FXML
    private void handleDeleteEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            employeeService.removeEmployee(selectedEmployee.getId());
            employeeList.remove(selectedEmployee);
        } else {
            showAlert("Ошибка", "Выберите сотрудника для удаления");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleOpenSalaryView(ActionEvent event) throws IOException {
        // Получаем выбранного сотрудника
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            // Загружаем FXML файл для калькулятора зарплаты
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/universitytest/views/salary-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());

            // Получаем контроллер и передаем выбранного сотрудника
            SalaryController salaryController = loader.getController();
            salaryController.initialize(selectedEmployee); // Передаем сотрудника в новый контроллер

            // Устанавливаем размеры окна (ширина и высота)
            stage.setWidth(400); // Ширина окна
            stage.setHeight(600); // Высота окна

            // Отображаем новое окно
            stage.setTitle("Калькулятор зарплаты");
            stage.setScene(scene);
            stage.show();
        } else {
            showAlert("Ошибка", "Пожалуйста, выберите сотрудника для расчета зарплаты.");
        }
    }



}
