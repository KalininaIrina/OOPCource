package com.example.universitytest.models;

import javafx.beans.property.*;

public class Employee {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty surname;
    private final SimpleIntegerProperty positionId;
    private final SimpleDoubleProperty baseSalary;
    private final SimpleIntegerProperty yearsWorked;
    private final SimpleBooleanProperty academicDegree;
    private final SimpleIntegerProperty departmentId;

    // Конструктор с инициализацией свойств
    public Employee(String firstName, String lastName, String surname, int positionId, double baseSalary, int yearsWorked, boolean academicDegree, int departmentId) {
        this.id = new SimpleIntegerProperty(-1);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.surname = new SimpleStringProperty(surname);
        this.positionId = new SimpleIntegerProperty(positionId);
        this.baseSalary = new SimpleDoubleProperty(baseSalary);
        this.yearsWorked = new SimpleIntegerProperty(yearsWorked);
        this.academicDegree = new SimpleBooleanProperty(academicDegree);
        this.departmentId = new SimpleIntegerProperty(departmentId);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public String getSurname() {
        return surname.get();
    }

    public int getPositionId() {
        return positionId.get();
    }

    public double getBaseSalary() {
        return baseSalary.get();
    }

    public int getYearsWorked() {
        return yearsWorked.get();
    }

    public boolean hasAcademicDegree() {
        return academicDegree.get();
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public void setPositionId(int positionId) {
        this.positionId.set(positionId);
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary.set(baseSalary);
    }

    public void setYearsWorked(int yearsWorked) {
        this.yearsWorked.set(yearsWorked);
    }

    public void setAcademicDegree(boolean academicDegree) {
        this.academicDegree.set(academicDegree);
    }

    // Property Methods
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public SimpleIntegerProperty positionIdProperty() {
        return positionId;
    }

    public SimpleDoubleProperty baseSalaryProperty() {
        return baseSalary;
    }

    public SimpleIntegerProperty yearsWorkedProperty() {
        return yearsWorked;
    }

    public SimpleBooleanProperty academicDegreeProperty() {
        return academicDegree;
    }
    public int getDepartmentId() {
        return departmentId.get();
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId.set(departmentId);
    }

    public SimpleIntegerProperty departmentIdProperty() {
        return departmentId;
    }
}
