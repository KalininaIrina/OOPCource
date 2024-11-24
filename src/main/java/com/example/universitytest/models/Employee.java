package com.example.universitytest.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Employee {
    private SimpleIntegerProperty id;
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty surname;
    private SimpleStringProperty department;
    private SimpleStringProperty position;
    private SimpleDoubleProperty baseSalary;
    private SimpleIntegerProperty yearsWorked; // Стаж
    private SimpleBooleanProperty hasAcademicDegree; // Ученая степень
    //private SimpleIntegerProperty hoursWorked; // Часы работы

    public Employee(int id, String firstName, String lastName, String surname, String department, String position,
                    double baseSalary, int yearsWorked, boolean hasAcademicDegree) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.surname = new SimpleStringProperty(surname);
        this.department = new SimpleStringProperty(department);
        this.position = new SimpleStringProperty(position);
        this.baseSalary = new SimpleDoubleProperty(baseSalary);
        this.yearsWorked = new SimpleIntegerProperty(yearsWorked);
        this.hasAcademicDegree = new SimpleBooleanProperty(hasAcademicDegree);
        //this.hoursWorked = new SimpleIntegerProperty(hoursWorked); // Инициализация поля для часов работы
    }

    // Методы доступа для привязки данных (getters и setters)
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public String getSurname() {
        return surname.get();
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public SimpleStringProperty departmentProperty() {
        return department;
    }

    public String getPosition() {
        return position.get();
    }

    public void setPosition(String position) {
        this.position.set(position);
    }

    public SimpleStringProperty positionProperty() {
        return position;
    }

    public double getBaseSalary() {
        return baseSalary.get();
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary.set(baseSalary);
    }

    public SimpleDoubleProperty baseSalaryProperty() {
        return baseSalary;
    }

    // Методы для работы со стажем и ученой степенью
    public int getYearsWorked() {
        return yearsWorked.get();
    }

    public void setYearsWorked(int yearsWorked) {
        this.yearsWorked.set(yearsWorked);
    }

    public SimpleIntegerProperty yearsWorkedProperty() {
        return yearsWorked;
    }

    public boolean hasAcademicDegree() {
        return hasAcademicDegree.get();
    }

    public void setHasAcademicDegree(boolean hasAcademicDegree) {
        this.hasAcademicDegree.set(hasAcademicDegree);
    }

    public SimpleBooleanProperty hasAcademicDegreeProperty() {
        return hasAcademicDegree;
    }

    // Методы для работы с часами работы
    /*public int getHoursWorked() {
        return hoursWorked.get();
    }*/

    /*public void setHoursWorked(int hoursWorked) {
        this.hoursWorked.set(hoursWorked);
    }

    public SimpleIntegerProperty hoursWorkedProperty() {
        return hoursWorked;
    }*/
}
