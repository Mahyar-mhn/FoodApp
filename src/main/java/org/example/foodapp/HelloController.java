package org.example.foodapp;


import DaoClasses.UserDAO;
import Classes.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;

import java.util.List;

public class HelloController {

    @FXML
    private Button loadButton;

    @FXML
    private ListView<String> userListView;

    // Initialize method (called after FXML is loaded)
    public void initialize() {
        // Optionally, you can preload users here
        // loadUsers();
    }

    // Action handler for the button
    @FXML
    public void onLoadButtonClick(ActionEvent event) {
        loadUsers();
    }

    // Method to load users into the ListView
    private void loadUsers() {
        // Fetch users from the database
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();

        // Clear the ListView and add user names
        userListView.getItems().clear();
        for (User user : users) {
            userListView.getItems().add(user.getName());
        }
    }
}
