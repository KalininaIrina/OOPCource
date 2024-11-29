package com.example.universitytest.controllers;

import com.example.universitytest.Main;
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
import java.util.List;

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
    @FXML
    private ComboBox<String> departmentComboBox;

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
        //loadDepartments();
        ObservableList<String> departmentList = FXCollections.observableArrayList(employeeService.getAllDepartments());
        departmentComboBox.setItems(departmentList);
        departmentColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(employeeService.getDepartmentById(cellData.getValue().getDepartmentId())));
        baseSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().baseSalaryProperty().asObject());

        // Заполняем список сотрудников из базы данных
        employeeList.addAll(employeeService.getEmployees());
        employeeTable.setItems(employeeList);
    }

    /*private void loadDepartments() {
        List<String> departments = employeeService.getAllDepartments(); // Метод из сервиса
        if (departments != null && !departments.isEmpty()) {
            departmentComboBox.getItems().addAll(departments); // Добавляем данные в ComboBox
        } else {
            System.out.println("Список отделов пуст или не загружен.");
        }
    }*/

    private void loadDepartments() {
        List<String> departments = employeeService.getAllDepartments(); // Метод для получения всех отделов
        ObservableList<String> departmentList = FXCollections.observableArrayList(departments);
        departmentComboBox.setItems(departmentList); // Устанавливаем отделы в ComboBox
    }


    @FXML
    private TextField positionField;

    @FXML
    private void handleAddEmployee() {
        // Получаем выбранную должность из ComboBox
        String selectedPosition = positionComboBox.getSelectionModel().getSelectedItem();
        String selectedDepartment = departmentComboBox.getSelectionModel().getSelectedItem();

        if (selectedPosition != null && selectedDepartment != null) {
            // Преобразуем название должности в ID
            int positionId = employeeService.getPositionIdByName(selectedPosition);
            int departmentId = employeeService.getDepartmentIdByName(selectedDepartment);

            // Проверяем, что все текстовые поля заполнены
            if (firstNameField.getText().isEmpty() ||
                    lastNameField.getText().isEmpty() ||
                    surnameField.getText().isEmpty() ||
                    baseSalaryField.getText().isEmpty() ||
                    yearsWorkedField.getText().isEmpty()) {
                showAlert("Ошибка", "Пожалуйста, заполните все поля.");
                return;
            }

            try {
                // Преобразуем данные из текстовых полей
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String surname = surnameField.getText();
                double baseSalary = Double.parseDouble(baseSalaryField.getText());
                int yearsWorked = Integer.parseInt(yearsWorkedField.getText());
                boolean hasAcademicDegree = academicDegreeCheck.isSelected();

                // Создаем нового сотрудника
                Employee newEmployee = new Employee(
                        firstName,
                        lastName,
                        surname,
                        positionId,
                        baseSalary,
                        yearsWorked,
                        hasAcademicDegree,
                        departmentId
                );

                // Добавляем сотрудника в базу данных
                employeeService.addEmployee(newEmployee);

                // Добавляем сотрудника в локальный список и обновляем таблицу
                employeeList.add(newEmployee);
                employeeTable.refresh();

                // Очищаем все поля
                firstNameField.clear();
                lastNameField.clear();
                surnameField.clear();
                positionComboBox.getSelectionModel().clearSelection();
                departmentComboBox.getSelectionModel().clearSelection();
                baseSalaryField.clear();
                yearsWorkedField.clear();
                academicDegreeCheck.setSelected(false);

            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Пожалуйста, введите корректные значения для оклада и стажа.");
            }
        } else {
            showAlert("Ошибка", "Пожалуйста, выберите должность и департамент.");
        }
    }

    private void updateEmployeeTable() {
        try {
            // Загружаем актуальный список сотрудников из базы
            List<Employee> updatedEmployeeList = employeeService.getAllEmployees();

            // Проверяем, что ID каждого сотрудника корректно загружен
            /*for (Employee employee : updatedEmployeeList) {
                System.out.println("Сотрудник ID: " + employee.getId());
            }*/

            // Обновляем таблицу
            employeeTable.setItems(FXCollections.observableArrayList(updatedEmployeeList));
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось обновить таблицу сотрудников.");
        }
    }



    /*@FXML
    private void handleUpdateEmployee() {
        // Получаем выбранного сотрудника
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            // Обновляем информацию о сотруднике в базе данных
            employeeService.updateEmployee(selectedEmployee);

            // После обновления данных обновляем таблицу сотрудников
            updateEmployeeTable();

            // Показываем сообщение об успешном обновлении
            showAlert("Успех", "Данные сотрудника обновлены успешно!");

        } else {
            // Если сотрудник не выбран, показываем ошибку
            showAlert("Ошибка", "Пожалуйста, выберите сотрудника для обновления.");
        }
    }*/

    @FXML
    private void loadEmployeeDataForEditing() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            // Заполняем текстовые поля
            firstNameField.setText(selectedEmployee.getFirstName());
            lastNameField.setText(selectedEmployee.getLastName());
            surnameField.setText(selectedEmployee.getSurname());
            baseSalaryField.setText(String.valueOf(selectedEmployee.getBaseSalary()));
            yearsWorkedField.setText(String.valueOf(selectedEmployee.getYearsWorked()));
            //hoursWorkedField.setText(String.valueOf(selectedEmployee.getHoursWorked())); // Поле часов работы

            // Заполняем ComboBox для должности и выбираем текущую
            List<String> positions = employeeService.getAllPositions();
            positionComboBox.getItems().setAll(positions);
            positionComboBox.setValue(employeeService.getPositionById(selectedEmployee.getPositionId()));

            // Заполняем ComboBox для отдела и выбираем текущий
            List<String> departments = employeeService.getAllDepartments();
            departmentComboBox.getItems().setAll(departments);
            departmentComboBox.setValue(employeeService.getDepartmentById(selectedEmployee.getDepartmentId()));

            // Заполняем CheckBox для ученой степени
            academicDegreeCheck.setSelected(selectedEmployee.hasAcademicDegree());
        } else {
            showAlert("Ошибка", "Выберите сотрудника для редактирования.");
        }
    }



    @FXML
    private void handleSaveEmployeeChanges() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try {
                // Получаем данные из текстовых полей
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String surname = surnameField.getText();
                double baseSalary = Double.parseDouble(baseSalaryField.getText());
                //double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
                int yearsWorked = Integer.parseInt(yearsWorkedField.getText());
                String positionName = positionComboBox.getValue();
                String departmentName = departmentComboBox.getValue();
                boolean hasAcademicDegree = academicDegreeCheck.isSelected();

                // Получаем ID должности и отдела
                int positionId = employeeService.getPositionIdByName(positionName);
                int departmentId = employeeService.getDepartmentIdByName(departmentName);

                // Обновляем данные сотрудника
                selectedEmployee.setFirstName(firstName);
                selectedEmployee.setLastName(lastName);
                selectedEmployee.setSurname(surname);
                selectedEmployee.setBaseSalary(baseSalary);
                //selectedEmployee.setHoursWorked(hoursWorked);
                selectedEmployee.setYearsWorked(yearsWorked);
                selectedEmployee.setPositionId(positionId);
                selectedEmployee.setDepartmentId(departmentId);
                selectedEmployee.setAcademicDegree(hasAcademicDegree);

                // Обновляем данные в базе
                employeeService.updateEmployee(selectedEmployee);

                // Обновляем таблицу сотрудников
                updateEmployeeTable();

                // Очищаем все поля
                firstNameField.clear();
                lastNameField.clear();
                surnameField.clear();
                positionComboBox.getSelectionModel().clearSelection();
                departmentComboBox.getSelectionModel().clearSelection();
                baseSalaryField.clear();
                yearsWorkedField.clear();
                academicDegreeCheck.setSelected(false);

                showAlert("Успех", "Данные сотрудника успешно обновлены.");
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Некорректный ввод данных.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            showAlert("Ошибка", "Выберите сотрудника для сохранения изменений.");
        }
    }


    /*private void updateEmployeeTable() {
        try {
            List<Employee> updatedEmployees = employeeService.getAllEmployees();  // Получаем актуальный список сотрудников
            employeeTable.setItems(FXCollections.observableList(updatedEmployees));  // Перезагружаем таблицу
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось обновить данные таблицы сотрудников.");
        }
    }*/




    @FXML
    private void handleUpdateEmployee() throws SQLException {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            double baseSalary = Double.parseDouble(baseSalaryField.getText());
            //double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
            String positionName = positionComboBox.getValue();

            // Получаем ID должности
            int positionId = employeeService.getPositionIdByName(positionName);

            // Обновляем данные сотрудника
            selectedEmployee.setFirstName(firstName);
            selectedEmployee.setLastName(lastName);
            selectedEmployee.setBaseSalary(baseSalary);
            //selectedEmployee.setHoursWorked(hoursWorked);
            selectedEmployee.setPositionId(positionId);
            //selectedEmployee.setPositionName(positionName);
            employeeService.getPositionById(selectedEmployee.getPositionId());

            // Обновляем запись в базе данных
            employeeService.updateEmployee(selectedEmployee);
            updateEmployeeTable();  // Обновляем таблицу сотрудников
            showAlert("Успех", "Данные сотрудника обновлены.");
        } else {
            showAlert("Ошибка", "Выберите сотрудника для обновления.");
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
            scene.getStylesheets().add(Main.class.getResource("/com/example/universitytest/styles/styles.css").toExternalForm());

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
