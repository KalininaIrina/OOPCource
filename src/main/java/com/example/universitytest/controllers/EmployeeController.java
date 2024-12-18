package com.example.universitytest.controllers;

import com.example.universitytest.Main;
import com.example.universitytest.models.Employee;
import com.example.universitytest.services.EmployeeService;
import com.example.universitytest.database.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.stage.Modality;
import javafx.scene.layout.VBox;
import javafx.scene.control.TitledPane;
import java.time.LocalDate; // Для LocalDate
import javafx.geometry.Insets; // Для Insets
import javafx.stage.FileChooser; // Для FileChooser
import java.io.BufferedWriter; // Для записи файла
import java.io.FileWriter; // Для записи файла
import java.io.File; // Для работы с файлами
import java.io.IOException; // Для обработки исключений
import javafx.scene.control.Alert; // Для отображения Alert
import javafx.scene.control.Button; // Для кнопок
import javafx.scene.control.TextArea; // Для текстовой области
import javafx.scene.layout.VBox; // Для VBox


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

    private List<Employee> originalEmployeeList;  // Оригинальный список сотрудников
    private void loadEmployees() throws SQLException {
        List<Employee> employees = employeeService.getAllEmployees();  // Загрузка данных из сервиса
        originalEmployeeList = new ArrayList<>(employees);  // Сохраняем оригинальный список
        employeeTable.setItems(FXCollections.observableArrayList(employees));  // Устанавливаем данные в таблицу
    }


    @FXML
    public void initialize() {
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        surnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());

        // Инициализация должностей
        List<String> positions = employeeService.getAllPositions();
        if (positions != null && !positions.isEmpty()) {
            ObservableList<String> positionList = FXCollections.observableArrayList(positions);
            positionComboBox.setItems(positionList);
        } else {
            positionComboBox.setItems(FXCollections.observableArrayList("Все")); // Принудительное значение "Все"
            showAlert("Ошибка", "Нет доступных должностей.");
        }

        positionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(employeeService.getPositionById(cellData.getValue().getPositionId())));

        // Инициализация кафедр
        List<String> departments = employeeService.getAllDepartments();
        if (departments != null && !departments.isEmpty()) {
            ObservableList<String> departmentList = FXCollections.observableArrayList(departments);
            departmentComboBox.setItems(departmentList);
        } else {
            departmentComboBox.setItems(FXCollections.observableArrayList("Все")); // Принудительное значение "Все"
            showAlert("Ошибка", "Нет доступных кафедр.");
        }

        departmentColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(employeeService.getDepartmentById(cellData.getValue().getDepartmentId())));

        baseSalaryColumn.setCellValueFactory(cellData -> cellData.getValue().baseSalaryProperty().asObject());

        positionComboBox.setEditable(true);
        departmentComboBox.setEditable(true);

        // Загрузка сотрудников из базы данных
        List<Employee> employees = employeeService.getEmployees();
        if (employees.isEmpty()) {
            showAlert("Ошибка", "Нет доступных сотрудников для отображения.");
        } else {
            employeeList.addAll(employees);
        }
        employeeTable.setItems(employeeList);

        originalEmployeeList = new ArrayList<>(employeeTable.getItems());

        // Инициализация ComboBox для сортировки
        /*sortComboBox.getItems().clear();
        sortComboBox.getItems().addAll("По фамилии", "По зарплате");*/

        // Инициализация фильтров
        //initializeFilters();

    }



    /*private void initializeFilters() {
        // Заполняем ComboBox кафедрами
        List<String> departments = employeeService.getAllDepartments();
        departments.add(0, "Все");
        filterDepartmentComboBox.setItems(FXCollections.observableArrayList(departments));

        // Заполняем ComboBox должностями
        List<String> positions = employeeService.getAllPositions();
        positions.add(0, "Все");
        filterPositionComboBox.setItems(FXCollections.observableArrayList(positions));
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

            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Пожалуйста, введите корректные значения для оклада и стажа.");
            }
        } else {
            showAlert("Ошибка", "Пожалуйста, выберите должность и департамент.");
        }

        originalEmployeeList = new ArrayList<>(employeeTable.getItems());
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
        originalEmployeeList = new ArrayList<>(employeeTable.getItems());
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
        originalEmployeeList = new ArrayList<>(employeeTable.getItems());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    private void handleSortByLastName() {
        ObservableList<Employee> employeeList = employeeTable.getItems();

        if (employeeList == null || employeeList.isEmpty()) {
            showAlert("Ошибка", "Нет данных для сортировки.");
            return;
        }

        // Сортировка по фамилии
        employeeList.sort(Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER));
        employeeTable.refresh();
    }

    @FXML
    private void handleSortByLastNameDescending() {
        ObservableList<Employee> employeeList = employeeTable.getItems();

        if (employeeList == null || employeeList.isEmpty()) {
            showAlert("Ошибка", "Нет данных для сортировки.");
            return;
        }

        // Сортировка по фамилии в убывающем порядке
        employeeList.sort(Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER).reversed());
        employeeTable.refresh();
    }


    @FXML
    private void handleSortBySalary() {
        ObservableList<Employee> employeeList = employeeTable.getItems();

        if (employeeList == null || employeeList.isEmpty()) {
            showAlert("Ошибка", "Нет данных для сортировки.");
            return;
        }

        // Сортировка по зарплате
        employeeList.sort(Comparator.comparingDouble(Employee::getBaseSalary));
        employeeTable.refresh();
    }

    @FXML
    private void handleSortBySalaryDescending() {
        ObservableList<Employee> employeeList = employeeTable.getItems();

        if (employeeList == null || employeeList.isEmpty()) {
            showAlert("Ошибка", "Нет данных для сортировки.");
            return;
        }

        // Сортировка по зарплате в убывающем порядке
        employeeList.sort(Comparator.comparingDouble(Employee::getBaseSalary).reversed());
        employeeTable.refresh();
    }



    @FXML
    private void resetFilters() {
        // Сброс всех фильтров
        lastNameField.clear();
        firstNameField.clear();
        surnameField.clear();
        departmentComboBox.getSelectionModel().clearSelection();
        positionComboBox.getSelectionModel().clearSelection();
        academicDegreeCheck.setSelected(false);

        // Обновление таблицы после сброса фильтров
        updateEmployeeTable();
    }



    @FXML
    private void resetSorting() {
        // Очищаем текущую сортировку
        employeeTable.getSortOrder().clear();

        // Обновляем таблицу после сброса сортировки
        updateEmployeeTable();
    }





    @FXML
    private void resetAllFilters() {
        // Сбрасываем фильтры по кафедре и должности
        //filterDepartmentComboBox.setValue("Все");
        //filterPositionComboBox.setValue("Все");

        // Сбрасываем сортировку
        //sortComboBox.setValue(null);

        // Перезагружаем все сотрудники в таблицу
        updateEmployeeTable();
    }

    @FXML
    private void handleFilterByDepartment() {
        // Создаем окно с выпадающим списком кафедр
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Фильтрация по кафедре");
        dialog.setHeaderText("Выберите кафедру для фильтрации:");

        // ComboBox с кафедрами
        ComboBox<String> filterDepartmentComboBox = new ComboBox<>();
        filterDepartmentComboBox.getItems().add("Все"); // Добавляем опцию "Все"

        // Загружаем кафедры из базы данных
        List<String> departments = employeeService.getAllDepartments();
        if (departments == null || departments.isEmpty()) {
            showAlert("Ошибка", "Нет доступных кафедр для фильтрации.");
            return;
        }
        filterDepartmentComboBox.getItems().addAll(departments);
        filterDepartmentComboBox.setValue("Все");

        // Добавляем ComboBox в диалог
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(filterDepartmentComboBox);
        dialog.getDialogPane().setContent(content);

        // Кнопки ОК и Отмена
        ButtonType okButtonType = new ButtonType("Применить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Обрабатываем результат
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return filterDepartmentComboBox.getValue();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::applyDepartmentFilter);
    }



    // Применяем фильтр по кафедре
    private void applyDepartmentFilter(String department) {
        // Загружаем всех сотрудников
        List<Employee> allEmployees;
        try {
            allEmployees = employeeService.getAllEmployees();
            if (allEmployees == null || allEmployees.isEmpty()) {
                showAlert("Ошибка", "Нет доступных сотрудников для фильтрации.");
                return;
            }
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить данные сотрудников.");
            return;
        }

        // Фильтрация по кафедре
        List<Employee> filteredEmployees = allEmployees.stream()
                .filter(employee -> "Все".equals(department) ||
                        employeeService.getDepartmentById(employee.getDepartmentId()).equals(department))
                .collect(Collectors.toList());

        // Обновляем таблицу
        employeeTable.setItems(FXCollections.observableArrayList(filteredEmployees));
    }

    @FXML
    private void handleFilterByPosition() {
        // Создаем окно с выпадающим списком должностей
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Фильтрация по должности");
        dialog.setHeaderText("Выберите должность для фильтрации:");

        // ComboBox с должностями
        ComboBox<String> filterPositionComboBox = new ComboBox<>();
        filterPositionComboBox.getItems().add("Все"); // Добавляем опцию "Все"

        // Загружаем должности из базы данных
        List<String> positions = employeeService.getAllPositions();
        if (positions == null || positions.isEmpty()) {
            showAlert("Ошибка", "Нет доступных должностей для фильтрации.");
            return;
        }
        filterPositionComboBox.getItems().addAll(positions);
        filterPositionComboBox.setValue("Все");

        // Добавляем ComboBox в диалог
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(filterPositionComboBox);
        dialog.getDialogPane().setContent(content);

        // Кнопки ОК и Отмена
        ButtonType okButtonType = new ButtonType("Применить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Обрабатываем результат
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return filterPositionComboBox.getValue();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::applyPositionFilter);
    }


    // Применяем фильтр по должности
    private void applyPositionFilter(String position) {
        // Загружаем всех сотрудников
        List<Employee> allEmployees;
        try {
            allEmployees = employeeService.getAllEmployees();
            if (allEmployees == null || allEmployees.isEmpty()) {
                showAlert("Ошибка", "Нет доступных сотрудников для фильтрации.");
                return;
            }
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить данные сотрудников.");
            return;
        }

        // Фильтрация по должности
        List<Employee> filteredEmployees = allEmployees.stream()
                .filter(employee -> "Все".equals(position) ||
                        employeeService.getPositionById(employee.getPositionId()).equals(position))
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
                showYearlyReportInModal(report);
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Неверный формат года", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void onGenerateDepartmentReportClicked() {
        // Открываем окно выбора кафедры
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, employeeService.getAllDepartments());
        dialog.setTitle("Отчет по кафедре");
        dialog.setHeaderText("Выберите кафедру для формирования отчета:");
        dialog.setContentText("Кафедра:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String departmentName = result.get();
            int departmentId = employeeService.getDepartmentIdByName(departmentName);

            if (departmentId != -1) {
                // Запрашиваем год у пользователя
                TextInputDialog yearDialog = new TextInputDialog();
                yearDialog.setTitle("Годовой отчет по кафедре");
                yearDialog.setHeaderText("Введите год для формирования отчета:");
                yearDialog.setContentText("Год:");

                Optional<String> yearResult = yearDialog.showAndWait();
                if (yearResult.isPresent()) {
                    try {
                        int year = Integer.parseInt(yearResult.get());
                        // Генерация отчета
                        String report = employeeService.generateDepartmentReport(departmentId, year);
                        showDepartmentReportInModal(report);

                        // Предлагаем сохранить отчет в файл
                        //saveDepartmentReportToFile(report, "department_report_" + departmentId + "_" + year + ".txt");
                    } catch (NumberFormatException e) {
                        showAlert("Ошибка", "Неверный формат года", Alert.AlertType.ERROR);
                    }
                }
            } else {
                showAlert("Ошибка", "Кафедра не найдена", Alert.AlertType.ERROR);
            }
        }
    }



    public void showDepartmentReportInModal(String report) {
        // Создаем новый Stage и отображаем отчет
        /*Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        VBox vbox = new VBox(new javafx.scene.control.TextArea(report));
        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Отчет по кафедре");
        stage.show();*/












        // Создаем новое модальное окно
        Stage modalStage = new Stage();
        modalStage.setTitle("Годовой отчет по кафедре");
        modalStage.initModality(Modality.APPLICATION_MODAL);

        // Текстовое поле для отображения отчета
        TextArea reportTextArea = new TextArea(report);
        reportTextArea.setEditable(false);
        reportTextArea.setWrapText(true);

        //String departmentName = result.get();
        //int departmentId = employeeService.getDepartmentIdByName(departmentName);

        // Кнопка для сохранения отчета
        Button saveButton = new Button("Сохранить отчет");
        saveButton.setOnAction(event -> {
            saveDepartmentReportToFile(report, "department_report", LocalDate.now().getYear());
            //saveReportToFile(report, "Employee_Report", LocalDate.now().getYear());
        });

        // Размещаем TextArea и кнопку в контейнере VBox
        VBox root = new VBox(10, reportTextArea, saveButton);
        root.setPadding(new Insets(10));

        // Создаем сцену и добавляем ее в окно
        Scene scene = new Scene(root, 500, 400);
        modalStage.setScene(scene);

        // Показываем модальное окно
        modalStage.showAndWait();
    }



    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showYearlyReportInModal(String report) {
        // Создаем новое модальное окно
        Stage modalStage = new Stage();
        modalStage.setTitle("Годовой отчет");
        modalStage.initModality(Modality.APPLICATION_MODAL);

        // Текстовое поле для отображения отчета
        TextArea reportTextArea = new TextArea(report);
        reportTextArea.setEditable(false);
        reportTextArea.setWrapText(true);

        // Кнопка для сохранения отчета
        Button saveButton = new Button("Сохранить отчет");
        saveButton.setOnAction(event -> {
            saveReportToFile(report, "Employee_Report", LocalDate.now().getYear());
        });

        // Размещаем TextArea и кнопку в контейнере VBox
        VBox root = new VBox(10, reportTextArea, saveButton);
        root.setPadding(new Insets(10));

        // Создаем сцену и добавляем ее в окно
        Scene scene = new Scene(root, 500, 400);
        modalStage.setScene(scene);

        // Показываем модальное окно
        modalStage.showAndWait();


        /*TextArea textArea = new TextArea(report);
        textArea.setEditable(false);

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Годовой отчет");
        modalStage.setScene(new Scene(new VBox(textArea), 400, 300));
        modalStage.show();*/
    }


    private void saveReportToFile(String report, String reportName, int year) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчет");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        fileChooser.setInitialFileName(reportName + "_" + year + ".txt");

        // Отображаем диалог сохранения файла
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(report);
                showAlert("Успех", "Отчет успешно сохранен!", Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                showAlert("Ошибка", "Ошибка при сохранении файла: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }


    private void saveDepartmentReportToFile(String report, String fileName, int year) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчет");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        fileChooser.setInitialFileName(fileName + "_" + year + ".txt");

        // Отображаем диалог сохранения файла
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(report);
                showAlert("Успех", "Отчет успешно сохранен!", Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                showAlert("Ошибка", "Ошибка при сохранении файла: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
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
