package com.example.universitytest.models;

public class Position{
    private String title;
    private double baseRate; //базовая ставка для данной должности
    private double hourlyRate; //ставка за почасовую работу

    public Position() {}

    public Position(String title, double baseRate, double hourlyRate){
        this.title = title;
        this.baseRate = baseRate;
        this.hourlyRate = hourlyRate;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }
    public double getBaseRate() {
        return baseRate;
    }
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    public double getHourlyRate() {
        return hourlyRate;
    }

    public void getPositionDetails(String title, double baseRate, double hourlyRate){
        System.out.println("Название должности: " + title + ", Базовая ставка для данной должности: " + baseRate + ", Ставка за почасовую работу: " + hourlyRate);
    }
}