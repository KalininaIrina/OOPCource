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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.stage.Modality;
import javafx.scene.layout.VBox;

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
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> filterDepartmentComboBox;
    @FXML
    private ComboBox<String> filterPositionComboBox;


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

        positionComboBox.setEditable(true);
        departmentComboBox.setEditable(true);

        // Заполняем список сотрудников из базы данных
        employeeList.addAll(employeeService.getEmployees());
        employeeTable.setItems(employeeList);

        // Инициализируем ComboBox
        sortComboBox.getItems().clear();
        sortComboBox.getItems().addAll("По фамилии", "По зарплате");
        //sortComboBox.setValue("По фамилии"); // Значение по умолчанию

        initializeFilters();
    }

    private void initializeFilters() {
        // Заполняем ComboBox кафедрами
        List<String> departments = employeeService.getAllDepartments();
        departments.add(0, "Все"); // Добавляем опцию "Все" для сброса фильтра
        filterDepartmentComboBox.setItems(FXCollections.observableArrayList(departments));
        //filterDepartmentComboBox.setValue("Все");

        // Заполняем ComboBox должностями
        List<String> positions = employeeService.getAllPositions();
        positions.add(0, "Все"); // Добавляем опцию "Все" для сброса фильтра
        filterPositionComboBox.setItems(FXCollections.observableArrayList(positions));
        //filterPositionComboBox.setValue("Все");
    }


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
            //handleUpdateEmployee();
            updateEmployeeTable();
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
    private void handleSortEmployees() {
        // Получаем выбранный критерий сортировки из ComboBox
        String selectedCriterion = sortComboBox.getValue();

        // Проверяем, что пользователь выбрал критерий сортировки
        if (selectedCriterion == null) {
            showAlert("Ошибка", "Выберите критерий сортировки.");
            return;
        }

        // Копируем текущие данные из таблицы
        ObservableList<Employee> employeeList = employeeTable.getItems();

        // Если таблица пустая, сообщаем об этом
        if (employeeList == null || employeeList.isEmpty()) {
            showAlert("Ошибка", "Нет данных для сортировки.");
            return;
        }

        // Сортируем список сотрудников в зависимости от выбранного критерия
        List<Employee> sortedEmployeeList = new ArrayList<>(employeeList);
        switch (selectedCriterion) {
            case "По фамилии":
                sortedEmployeeList.sort(Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER));
                break;

            case "По зарплате":
                sortedEmployeeList.sort(Comparator.comparingDouble(Employee::getBaseSalary));
                break;

            default:
                showAlert("Ошибка", "Неизвестный критерий сортировки.");
                return;
        }

        // Обновляем таблицу с отсортированным списком
        employeeTable.setItems(FXCollections.observableArrayList(sortedEmployeeList));
    }

    @FXML
    private void resetFilters() {
        filterDepartmentComboBox.setValue("Все");
        filterPositionComboBox.setValue("Все");
        updateEmployeeTable(); // Перезагружаем всех сотрудников
    }


    @FXML
    private void handleFilterEmployees() {
        String selectedDepartment = filterDepartmentComboBox.getValue();
        String selectedPosition = filterPositionComboBox.getValue();

        // Получаем полный список сотрудников из базы
        List<Employee> allEmployees;
        try {
            allEmployees = employeeService.getAllEmployees();
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить данные сотрудников.");
            return;
        }

        // Фильтруем список
        List<Employee> filteredEmployees = allEmployees.stream()
                .filter(employee -> {
                    boolean matchesDepartment = "Все".equals(selectedDepartment) ||
                            employeeService.getDepartmentById(employee.getDepartmentId()).equals(selectedDepartment);

                    boolean matchesPosition = "Все".equals(selectedPosition) ||
                            employeeService.getPositionById(employee.getPositionId()).equals(selectedPosition);

                    return matchesDepartment && matchesPosition;
                })
                .collect(Collectors.toList());

        // Обновляем таблицу
        employeeTable.setItems(FXCollections.observableArrayList(filteredEmployees));
    }

    @FXML
    private void onGenerateYearlyReportClicked() {
        // Проверяем, выбран ли сотрудник
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Ошибка", "Сотрудник не выбран", Alert.AlertType.ERROR);
            return;
        }

        // Запрашиваем год у пользователя
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Годовой отчет");
        dialog.setHeaderText("Введите год для формирования отчета:");
        dialog.setContentText("Год:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int year = Integer.parseInt(result.get());
                String report = employeeService.generateYearlyReport(selectedEmployee.getId(), year);
                showReportInModal(report);
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат года", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showReportInModal(String report) {
        TextArea textArea = new TextArea(report);
        textArea.setEditable(false);

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Годовой отчет");
        modalStage.setScene(new Scene(new VBox(textArea), 400, 300));
        modalStage.show();
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
