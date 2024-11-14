package com.example.universitytest.controllers;

import com.example.universitytest.models.Employee;
import com.example.universitytest.services.EmployeeService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField departmentField;

    private EmployeeService employeeService = new EmployeeService();
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        surnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        departmentColumn.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().positionProperty());

        employeeTable.setItems(employeeList);

        // Предварительные данные для тестирования
        //employeeList.add(new Employee(1, "Иван", "Иванов", "Физика", "Преподаватель"));
        //employeeList.add(new Employee(2, "Анна", "Петрова", "Химия", "Доцент"));
    }


    @FXML
    private TextField positionField;

    @FXML
    private void handleAddEmployee() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String surname = surnameField.getText(); // Получение фамилии
        String department = departmentField.getText();
        String position = positionField.getText();

        if (!firstName.isEmpty() && !lastName.isEmpty() && !surname.isEmpty() && !department.isEmpty() && !position.isEmpty()) {
            Employee newEmployee = new Employee(employeeList.size() + 1, firstName, lastName, surname, department, position);
            employeeService.addEmployee(newEmployee);
            employeeList.add(newEmployee);

            firstNameField.clear();
            lastNameField.clear();
            surnameField.clear(); // Очистка поля фамилии
            departmentField.clear();
            positionField.clear();
        } else {
            showAlert("Ошибка", "Пожалуйста, заполните все поля");
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
}
