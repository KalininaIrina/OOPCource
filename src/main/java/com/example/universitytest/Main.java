package com.example.universitytest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private static Stage primaryStage; // Хранение основного окна

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Сохраняем ссылку на основное окно
        showEmployeeView();  // Загружаем начальный экран
    }

    // Метод для отображения окна управления сотрудниками
    public void showEmployeeView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/employee-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(Main.class.getResource("/com/example/universitytest/styles/styles.css").toExternalForm());
        primaryStage.setTitle("Управление сотрудниками");
        primaryStage.setScene(scene);
        primaryStage.show();
    }




    // Метод для отображения окна калькулятора зарплаты
    /*public void showSalaryView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/salary-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            primaryStage.setTitle("Калькулятор заработной платы");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Выводим подробности ошибки
            System.err.println("Ошибка при загрузке FXML для калькулятора зарплаты.");
        }
    }*/


    public static void main(String[] args) {
        launch();
    }
}