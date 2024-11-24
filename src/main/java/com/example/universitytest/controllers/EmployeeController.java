package com.example.universitytest.controllers;

import com.example.universitytest.models.Employee;
import com.example.universitytest.services.EmployeeService;
import javafx.beans.property.SimpleDoubleProperty;
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
        baseSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().baseSalaryProperty().asObject());

        employeeTable.setItems(employeeList);
    }

    @FXML
    private TextField positionField;

    @FXML
    private void handleAddEmployee() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String surname = surnameField.getText();
        String department = departmentField.getText();
        String position = positionField.getText();
        String baseSalaryText = baseSalaryField.getText();

        // Новые поля
        String yearsWorkedText = yearsWorkedField.getText();
        //String hoursWorkedText = hoursWorkedField.getText();
        boolean hasAcademicDegree = academicDegreeCheck.isSelected();

        if (!firstName.isEmpty() && !lastName.isEmpty() && !surname.isEmpty() && !department.isEmpty() && !position.isEmpty() && !baseSalaryText.isEmpty()) {
            try {
                // Преобразуем строку оклада в double
                double baseSalary = Double.parseDouble(baseSalaryText);

                // Преобразуем стаж и количество часов в числа
                int yearsWorked = Integer.parseInt(yearsWorkedText);
                //int hoursWorked = Integer.parseInt(hoursWorkedText);

                // Создаем нового сотрудника с дополнительными параметрами
                Employee newEmployee = new Employee(
                        employeeList.size() + 1, // ID будет автоматически увеличиваться
                        firstName,
                        lastName,
                        surname,
                        department,
                        position,
                        baseSalary,
                        yearsWorked,
                        hasAcademicDegree
                        //hoursWorked
                );

                // Добавляем сотрудника в сервис и в список
                employeeService.addEmployee(newEmployee);
                employeeList.add(newEmployee);

                // Очистка всех полей
                firstNameField.clear();
                lastNameField.clear();
                surnameField.clear();
                departmentField.clear();
                positionField.clear();
                baseSalaryField.clear();
                yearsWorkedField.clear();
                //hoursWorkedField.clear();
                academicDegreeCheck.setSelected(false);

            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Все числовые поля должны содержать валидные значения.");
            }
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
