package org.example.foodapp;
import Classes.User;
import DaoClasses.UserDao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Create an instance of UserDAO
        UserDao userDAO = new UserDao();

        // Fetch all users
        List<User> users = userDAO.getAllUsers();

        // Print the list of users
        System.out.println("List of Users:");
        for (User user : users) {
            System.out.println(user);
        }
    }
}