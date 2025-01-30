package org.example.foodapp;
import Classes.User;
import DaoClasses.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import static org.example.foodapp.SceneChanger.changeScene;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        // Set the primary stage for global access
        primaryStage = stage;

        // Load the initial scene
        changeScene(stage,"CustomerHomePage.fxml", "Admin Page");
    }

    public static void main(String[] args) {
//        // Create an instance of UserDAO
//        UserDAO userDAO = new UserDAO();
//
//        // Fetch all users
//        List<User> users = userDAO.getAllUsers();
//
//        // Print the list of users
//        System.out.println("List of Users:");
//        for (User user : users) {
//            System.out.println(user);
//        }
        launch(args);
    }
}