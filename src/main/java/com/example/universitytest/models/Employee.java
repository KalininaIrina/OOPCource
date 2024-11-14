package com.example.universitytest.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Employee {
    private SimpleIntegerProperty id;
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty surname; // Если нужно, оставляем; иначе удаляем это поле
    private SimpleStringProperty department;
    private SimpleStringProperty position;
    private SimpleDoubleProperty baseSalary;

    // Исправленный конструктор с параметром `surname`
    public Employee(int id, String firstName, String lastName, String surname, String department, String position) {
        this.id = new SimpleIntegerProperty(id);
        this.lastName = new SimpleStringProperty(lastName);
        this.firstName = new SimpleStringProperty(firstName);
        this.surname = new SimpleStringProperty(surname); // Инициализация добавлена
        this.department = new SimpleStringProperty(department);
        this.position = new SimpleStringProperty(position);
        this.baseSalary = new SimpleDoubleProperty(0.0); // Можно изменить по необходимости
    }

    // Методы доступа для привязки данных
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
}
