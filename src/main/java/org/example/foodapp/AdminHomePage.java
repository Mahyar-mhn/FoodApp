package org.example.foodapp;

import Classes.OrderDetail;
import Classes.Restaurant;
import Classes.User;
import DaoClasses.OrderDAO;
import DaoClasses.OrderDetailDAO;
import DaoClasses.RestaurantDAO;
import DaoClasses.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminHomePage {

    @FXML
    private Label admin_name;

    @FXML
    private Button edit_submission_button;

    @FXML
    private TextField edit_user_name_txtfield;

    @FXML
    private TextField edit_user_phone_txtfield;

    @FXML
    private ChoiceBox<String> edit_user_role_choicebox;

    @FXML
    private AnchorPane edit_user_scroll_pane;

    @FXML
    private VBox edit_user_scroll_vbox;

    @FXML
    private AnchorPane main_pane_adminPage;

    @FXML
    private Button manage_restaurant_button;

    @FXML
    private Button manage_user_button;

    @FXML
    private AnchorPane ordering_pane_admin;

    @FXML
    private AnchorPane profile_pane_admin;

    @FXML
    private AnchorPane restaurant_manage_pane;

    @FXML
    private AnchorPane scroll_pane_admin_homePage;

    @FXML
    private AnchorPane user_manage_pane;

    @FXML
    private VBox vbox_scroll_pane_admin_homePage;


    @FXML
    private AnchorPane remove_user_scrolpane;

    @FXML
    private VBox remove_user_vbox;

    @FXML
    private Button remove_user_button;

    @FXML
    private Button add_user_button;

    @FXML
    private TextField add_user_name_txtfield;

    @FXML
    private TextField add_user_phone_txtfield;

    @FXML
    private TextField add_user_password_txtfield;

    @FXML
    private ChoiceBox<String> add_user_role_choicebox;

    private UserDAO userDAO = new UserDAO(); // DAO for database operations
    private int selectedUserId = -1; // Track selected user ID for editing
    private int selectedUserIdToRemove = -1; // Track the user ID for removal


    @FXML
    private VBox edit_restaurant_scroll_vbox;
    @FXML
    private VBox remove_restaurant_vbox;

    @FXML
    private TextField edit_restaurant_name_txtfield;
    @FXML
    private TextField edit_restaurant_city_txtfield;
    @FXML
    private TextField edit_restaurant_address_txtfield;

    @FXML
    private TextField add_restaurant_name_txtfield;
    @FXML
    private TextField add_restaurant_city_txtfield;
    @FXML
    private TextField add_restaurant_address_txtfield;

    @FXML
    private Button edit_restaurant_submission_button;
    @FXML
    private Button remove_restaurant_button;
    @FXML
    private Button add_restaurant_button;

    @FXML
    private Button admin_show_addresses_button;
    @FXML
    private AnchorPane showAddressesPane;

    @FXML
    private VBox vbox_admin_addresses;


    @FXML
    private TextField city_txtfield_newAddress;

    @FXML
    private TextArea detail_txtArea_newAddress;

    @FXML
    private TextField coordinate_txtfielad_newAddress;

    @FXML
    private void handleAddAdminAddress() {
        // Get input values from the text fields
        String city = city_txtfield_newAddress.getText().trim();
        String addressDetail = detail_txtArea_newAddress.getText().trim();
        String coordinate = coordinate_txtfielad_newAddress.getText().trim();

        // Validate inputs
        if (city.isEmpty() || addressDetail.isEmpty() || coordinate.isEmpty()) {
            showAlert("Error", "All fields (City, Address, and Coordinate) must be filled.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL AddAddress(?, ?, ?, ?, ?)}")) {

            // Set parameters for the stored procedure
            callableStatement.setInt(1, adminId); // Set user ID (admin ID)
            callableStatement.setBoolean(2, false); // DefaultAddress = false (not default initially)
            callableStatement.setString(3, city); // City
            callableStatement.setString(4, addressDetail); // Address detail
            callableStatement.setString(5, coordinate); // Coordinate

            // Execute the procedure
            callableStatement.execute();

            // Show success message and clear fields
            showAlert("Success", "New address added successfully.");
            clearNewAddressFields();
            loadAdminAddresses(); // Reload addresses to display the new one

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while adding the new address.");
        }
    }

    private void clearNewAddressFields() {
        city_txtfield_newAddress.clear();
        detail_txtArea_newAddress.clear();
        coordinate_txtfielad_newAddress.clear();
    }


    @FXML
    private void toggleShowAddressesButton() {
        if ("Home Page".equals(admin_show_addresses_button.getText())) {
            showProfilePane(); // Go back to the home page
        } else {
            profile_pane_admin.setVisible(true);
            ordering_pane_admin.setVisible(false);
            scroll_pane_admin_homePage.setVisible(false);
            user_manage_pane.setVisible(false);
            restaurant_manage_pane.setVisible(false);
            showAddressesPane.setVisible(true);

            loadAdminAddresses();

            admin_show_addresses_button.setText("Home Page");
            manage_user_button.setText("Manage Users");
        }
    }

    @FXML
    private void loadAdminAddresses() {
        if (adminId <= 0) { // Ensure adminId is set
            showAlert("Error", "Admin ID is not valid.");
            return;
        }

        vbox_admin_addresses.getChildren().clear(); // Clear existing content in the VBox

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL GetUserAddresses(?)}")) {

            // Set the admin ID as input for the stored procedure
            callableStatement.setInt(1, adminId);

            // Execute the stored procedure and get the result set
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    // If no addresses are found
                    Label noAddressesLabel = new Label("No addresses found for this admin.");
                    noAddressesLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
                    vbox_admin_addresses.getChildren().add(noAddressesLabel);
                    return;
                }

                // Loop through the result set and add addresses to the VBox
                while (resultSet.next()) {
                    int addressId = resultSet.getInt("AddId");
                    String city = resultSet.getString("City");
                    String addressDetail = resultSet.getString("AddressDetail");
                    boolean isDefault = resultSet.getBoolean("DefaultAddress");

                    // Create a VBox for each address
                    VBox addressBox = new VBox();
                    addressBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f9f9f9; -fx-spacing: 5;");
                    addressBox.setPrefWidth(300); // Optional: Set fixed width for each address box

                    // Add address details
                    Label cityLabel = new Label("City: " + city);
                    cityLabel.setStyle("-fx-font-size: 14px;");

                    Label addressDetailLabel = new Label("Detail: " + addressDetail);
                    addressDetailLabel.setStyle("-fx-font-size: 14px;");

                    Label defaultLabel = new Label(isDefault ? "Default: Yes" : "Default: No");
                    defaultLabel.setStyle(isDefault
                            ? "-fx-font-size: 14px; -fx-text-fill: green;"
                            : "-fx-font-size: 14px; -fx-text-fill: red;");

                    Button setDefaultButton = new Button("Set as Default");
                    setDefaultButton.setOnAction(event -> handleSetAdminDefaultAddress(addressId));

                    // Add elements to the VBox
                    addressBox.getChildren().addAll(cityLabel, addressDetailLabel, defaultLabel, setDefaultButton);

                    // Add the address VBox to the main VBox
                    vbox_admin_addresses.getChildren().add(addressBox);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading admin addresses.");
        }
    }
    @FXML
    private void handleSetAdminDefaultAddress(int addressId) {
        if (adminId <= 0) {
            showAlert("Error", "Admin ID is not valid.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL SetDefaultAddress(?, ?)}")) {

            // Set the input parameters for the stored procedure
            callableStatement.setInt(1, adminId);
            callableStatement.setInt(2, addressId);

            // Execute the procedure
            callableStatement.execute();

            // Show success message and reload the addresses
            showAlert("Success", "Default address updated successfully.");
            loadAdminAddresses();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while setting the default address.");
        }
    }

    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private int selectedRestaurantId = -1; // For editing
    private int selectedRestaurantIdToRemove = -1; // For removal


    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailDAO orderDetailDAO = new OrderDetailDAO();

    private int adminId; // Admin ID for the logged-in user

    @FXML
    private VBox vbox_admin_orders;

    /**
     * Sets the customer name into the label.
     *
     * @param name The name of the customer.
     */
    public void loadAdminName(String name) {
        admin_name.setText(name);
    }

    /**
     * Sets the admin data (ID and name) and initializes the page.
     *
     * @param adminId   The ID of the admin.
     * @param adminName The name of the admin.
     */
    public void setAdminData(int adminId, String adminName) {
        this.adminId = adminId;

        // Set the admin name
        loadAdminName(adminName);

        int addressId = getAdminDefaultAddressId();
        if (addressId != -1) {
            System.out.println("Manager's Default Address ID: " + addressId);
        } else {
            System.out.println("No default address found for the manager.");
        }
        // Load profile pane and other data
        showProfilePane();
        populateRoleChoiceBox(edit_user_role_choicebox);
        populateRoleChoiceBox(add_user_role_choicebox);
        loadRestaurantsIntoEditScrollPane();
        loadRestaurantsIntoRemoveScrollPane();

        loadAdminOrders();

        // Event handlers
        manage_restaurant_button.setOnAction(event -> toggleManageRestaurants());
        manage_user_button.setOnAction(event -> toggleManageUsers());
        add_restaurant_button.setOnAction(event -> handleAddRestaurant());
        remove_restaurant_button.setOnAction(event -> handleRemoveRestaurant());
        edit_restaurant_submission_button.setOnAction(event -> updateRestaurantDetails());
        remove_user_button.setOnAction(event -> handleRemoveUser());
    }



    private void loadUsersIntoRemoveScrollPane() {
        List<User> users = userDAO.getAllUsers();
        remove_user_vbox.getChildren().clear();

        for (User user : users) {
            if (!user.isDeleted()) { // Only show users who are not soft-deleted
                Label userLabel = new Label(user.getName() + " (" + user.getPhone() + ") - Role: " + user.getRole());
                userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent; -fx-cursor: hand;");

                userLabel.setOnMouseEntered(event -> userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #c40000;"));
                userLabel.setOnMouseExited(event -> userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;"));

                userLabel.setOnMouseClicked(event -> {
                    selectedUserIdToRemove = user.getUserId();
                    highlightSelectedUser(userLabel);
                });

                remove_user_vbox.getChildren().add(userLabel);
            }
        }
    }

    /**
     * Handles the removal of the selected user.
     */
    private void handleRemoveUser() {
        if (selectedUserIdToRemove == -1) {
            showAlert("Error", "No user selected for removal.");
            return;
        }

        // Perform the user removal
        boolean success = userDAO.removeUser(selectedUserIdToRemove);

        if (success) {
            showAlert("Success", "User removed successfully.");
            loadUsersIntoRemoveScrollPane(); // Refresh the list
            loadUsersIntoScrollPane();
            selectedUserIdToRemove = -1; // Reset the selected user ID
        } else {
            showAlert("Error", "Failed to remove user.");
        }
    }

    /**
     * Displays an alert dialog to the user.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     */

    /**
     * Populates the role ChoiceBox with the values: Admin, Manager, and User.
     */
    private void populateRoleChoiceBox(ChoiceBox<String> choiceBox) {
        ObservableList<String> roles = FXCollections.observableArrayList("Admin", "Manager", "User");
        choiceBox.setItems(roles);
        choiceBox.setValue("User"); // Set default value
    }

    /**
     * Shows the profile pane and resets buttons to their default states.
     */
    @FXML
    private void showProfilePane() {
        // Show and reset the visibility of panes
        profile_pane_admin.setVisible(true);
        scroll_pane_admin_homePage.setVisible(true);
        ordering_pane_admin.setVisible(true);
        user_manage_pane.setVisible(false);
        restaurant_manage_pane.setVisible(false);
        showAddressesPane.setVisible(false);

        // Update button text
        manage_restaurant_button.setText("Manage Restaurants");
        manage_user_button.setText("Manage Users");

        // Clear existing content in the VBox
        vbox_scroll_pane_admin_homePage.getChildren().clear();

        // Fetch restaurant data from the database
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();

        // Load only non-deleted restaurants as a cart
        for (Restaurant restaurant : restaurants) {
            if (!restaurant.isDeleted()) { // Check the isDeleted flag
                try {
                    // Load the RestaurantCart.fxml file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantCart.fxml"));
                    AnchorPane restaurantCart = loader.load();

                    // Get the controller for the cart
                    RestaurantCart controller = loader.getController();
                    controller.setRestaurantData(
                            restaurant.getName(),
                            "file:src/main/resources/images/" + restaurant.getPhoto()
                    );

                    // Apply the new color scheme
                    restaurantCart.setStyle("-fx-background-color: #638C6D; " +  // Dark Green
                            "-fx-border-color: #C84C05; " +  // Deep Orange
                            "-fx-border-width: 3; " +
                            "-fx-border-radius: 15; " +
                            "-fx-background-radius: 15; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0.4, 0, 6);");

                    // Add hover effects
                    restaurantCart.setOnMouseEntered(event -> {
                        restaurantCart.setStyle("-fx-background-color: #E7FBB4; " + // Light Green on hover
                                "-fx-border-color: #C84C05; " +
                                "-fx-border-radius: 15px;");
                    });

                    restaurantCart.setOnMouseExited(event -> {
                        restaurantCart.setStyle("-fx-background-color: #638C6D; " + // Dark Green default
                                "-fx-border-color: #C84C05; " +
                                "-fx-border-radius: 15px;");
                    });

                    // Add click event to load selected restaurant's detailed page
                    restaurantCart.setOnMouseClicked(event -> {
                        System.out.println("Clicked on: " + restaurant.getName());
                        loadSelectedRestaurantPage(restaurant.getRestaurantId());
                    });

                    // Add the cart to the VBox
                    vbox_scroll_pane_admin_homePage.getChildren().add(restaurantCart);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getAdminDefaultAddressId() {
        String procedureCall = "{CALL GetDefaultAddressByUserId(?)}";
        int addressId = -1; // Default value indicating no address found

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Set the manager's ID as input
            callableStatement.setInt(1, adminId);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    addressId = resultSet.getInt("AddId"); // Fetch the address ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }

        return addressId;
    }

    /**
     * Loads the selected restaurant's page or detailed view.
     *
     * @param restaurantId The ID of the selected restaurant.
     */
    private void loadSelectedRestaurantPage(int restaurantId) {
        try {
            // Fetch restaurant details from the database using the RestaurantDAO
            Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);

            if (restaurant != null) {
                System.out.println("Loading details for: " + restaurant.getName());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantPage.fxml"));
                AnchorPane restaurantDetailsPane = loader.load();

                // Pass restaurant details and user address ID to the controller
                RestaurantPage controller = loader.getController();

                // Here, use the appropriate user address ID (replace '1' with a dynamic ID from the logged-in user)
                controller.loadRestaurantPage(restaurant, getAdminDefaultAddressId());

                // Switch the main pane to the restaurant details pane
                main_pane_adminPage.getChildren().clear();
                main_pane_adminPage.getChildren().add(restaurantDetailsPane);
            } else {
                System.out.println("Restaurant not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Toggles between the Manage Restaurants view and the Home Page.
     */
    @FXML
    private void toggleManageRestaurants() {
        if ("Home Page".equals(manage_restaurant_button.getText())) {
            showProfilePane(); // Go back to the home page
        } else {
            profile_pane_admin.setVisible(true);
            ordering_pane_admin.setVisible(false);
            scroll_pane_admin_homePage.setVisible(false);
            user_manage_pane.setVisible(false);
            restaurant_manage_pane.setVisible(true);
            showAddressesPane.setVisible(false);

            manage_restaurant_button.setText("Home Page");
            manage_user_button.setText("Manage Users");
        }
    }

    /**
     * Toggles between the Manage Users view and the Home Page.
     */
    @FXML
    private void toggleManageUsers() {
        if ("Home Page".equals(manage_user_button.getText())) {
            showProfilePane(); // Go back to the home page
        } else {
            profile_pane_admin.setVisible(true);
            ordering_pane_admin.setVisible(false);
            scroll_pane_admin_homePage.setVisible(false);
            user_manage_pane.setVisible(true);
            restaurant_manage_pane.setVisible(false);
            showAddressesPane.setVisible(false);

            manage_user_button.setText("Home Page");
            manage_restaurant_button.setText("Manage Restaurants");

            loadUsersIntoScrollPane();
            loadUsersIntoRemoveScrollPane();
        }
    }

    /**
     * Loads all users into the scroll pane for selection.
     */
    private void loadUsersIntoScrollPane() {
        List<User> users = userDAO.getAllUsers();
        edit_user_scroll_vbox.getChildren().clear();

        for (User user : users) {
            if (!user.isDeleted()) { // Only show users who are not soft-deleted
                Label userLabel = new Label(user.getName() + " (" + user.getPhone() + ") - Role: " + user.getRole());
                userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent; -fx-cursor: hand;");

                userLabel.setOnMouseEntered(event -> userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #c40000;"));
                userLabel.setOnMouseExited(event -> userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;"));

                userLabel.setOnMouseClicked(event -> {
                    selectedUserId = user.getUserId();
                    loadUserDetails(user.getUserId());
                });

                edit_user_scroll_vbox.getChildren().add(userLabel);
            }
        }
    }

    /**
     * Loads the details of the selected user into the text fields and choice box.
     *
     * @param userId The ID of the user to fetch.
     */
    private void loadUserDetails(int userId) {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            edit_user_name_txtfield.setText(user.getName());
            edit_user_phone_txtfield.setText(user.getPhone());
            edit_user_role_choicebox.setValue(user.getRole()); // Set the role in the ChoiceBox
        } else {
            edit_user_name_txtfield.setText("");
            edit_user_phone_txtfield.setText("");
            edit_user_role_choicebox.setValue("User"); // Default value
        }
    }

    /**
     * Updates the details of the selected user.
     */
    @FXML
    private void updateUserDetails() {
        if (selectedUserId == -1) {
            System.out.println("No user selected for editing.");
            return;
        }

        String newName = edit_user_name_txtfield.getText().trim();
        String newPhone = edit_user_phone_txtfield.getText().trim();
        String newRole = edit_user_role_choicebox.getValue();

        if (newName.isEmpty() || newPhone.isEmpty() || newRole == null) {
            System.out.println("Name, phone, and role fields cannot be empty.");
            return;
        }

        boolean nameUpdated = userDAO.updateUser(selectedUserId, "Name", newName);
        boolean phoneUpdated = userDAO.updateUser(selectedUserId, "Phone", newPhone);
        boolean roleUpdated = userDAO.updateUser(selectedUserId, "Role", newRole);

        if (nameUpdated && phoneUpdated && roleUpdated) {
            System.out.println("User updated successfully!");
            loadUsersIntoScrollPane();
            loadUsersIntoRemoveScrollPane();
        } else {
            System.out.println("Failed to update user.");
        }
    }

    @FXML
    private void handleAddUser() {
        String name = add_user_name_txtfield.getText().trim();
        String phone = add_user_phone_txtfield.getText().trim();
        String password = add_user_password_txtfield.getText().trim();
        String role = add_user_role_choicebox.getValue();

        // Validate input fields
        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || role == null) {
            System.out.println("Error: All fields must be filled.");
            return;
        }

        // Create a new User object
        User newUser = new User(0, name, phone, password, role, false);

        // Attempt to add the user to the database
        boolean success = userDAO.addUser(newUser);

        if (success) {
            System.out.println("User added successfully!");
            clearAddUserFields(); // Clear the input fields
            loadUsersIntoScrollPane(); // Reload the user list
            loadUsersIntoRemoveScrollPane();
        } else {
            System.out.println("Failed to add user.");
        }
    }

    /**
     * Clears the input fields for adding a new user.
     */
    private void clearAddUserFields() {
        add_user_name_txtfield.clear();
        add_user_phone_txtfield.clear();
        add_user_password_txtfield.clear();
        add_user_role_choicebox.setValue("User");
    }
    private void highlightSelectedUser(Label userLabel) {
        // Reset styles for all labels
        for (var child : remove_user_vbox.getChildren()) {
            if (child instanceof Label) {
                child.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;");
            }
        }

        // Highlight the selected label
        userLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #e0e0e0;");
    }











    /**
     * Loads all non-deleted restaurants into the edit scroll pane.
     */
    private void loadRestaurantsIntoEditScrollPane() {
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
        edit_restaurant_scroll_vbox.getChildren().clear();

        for (Restaurant restaurant : restaurants) {
            if (!restaurant.isDeleted()) {
                Label restaurantLabel = new Label(restaurant.getName() + " - " + restaurant.getCity());
                restaurantLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent; -fx-cursor: hand;");

                restaurantLabel.setOnMouseEntered(event -> restaurantLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #c40000;"));
                restaurantLabel.setOnMouseExited(event -> restaurantLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;"));

                restaurantLabel.setOnMouseClicked(event -> {
                    selectedRestaurantId = restaurant.getRestaurantId();
                    loadRestaurantDetails(restaurant.getRestaurantId());
                });

                edit_restaurant_scroll_vbox.getChildren().add(restaurantLabel);
            }
        }
    }

    /**
     * Loads all non-deleted restaurants into the remove scroll pane.
     */
    private void loadRestaurantsIntoRemoveScrollPane() {
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
        remove_restaurant_vbox.getChildren().clear();

        for (Restaurant restaurant : restaurants) {
            if (!restaurant.isDeleted()) {
                Label restaurantLabel = new Label(restaurant.getName() + " - " + restaurant.getCity());
                restaurantLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent; -fx-cursor: hand;");

                restaurantLabel.setOnMouseEntered(event -> restaurantLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #c40000;"));
                restaurantLabel.setOnMouseExited(event -> restaurantLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;"));

                restaurantLabel.setOnMouseClicked(event -> {
                    selectedRestaurantIdToRemove = restaurant.getRestaurantId();
                    highlightSelectedRestaurant(restaurantLabel);
                });

                remove_restaurant_vbox.getChildren().add(restaurantLabel);
            }
        }
    }

    /**
     * Highlights the selected restaurant in the remove scroll pane.
     *
     * @param restaurantLabel The label of the selected restaurant.
     */
    private void highlightSelectedRestaurant(Label restaurantLabel) {
        for (var child : remove_restaurant_vbox.getChildren()) {
            if (child instanceof Label) {
                child.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: transparent;");
            }
        }
        restaurantLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #e0e0e0;");
    }

    /**
     * Handles adding a new restaurant.
     */
    @FXML
    private void handleAddRestaurant() {
        String name = add_restaurant_name_txtfield.getText().trim();
        String city = add_restaurant_city_txtfield.getText().trim();
        String address = add_restaurant_address_txtfield.getText().trim();

        if (name.isEmpty() || city.isEmpty() || address.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        Restaurant newRestaurant = new Restaurant(0, name, null, 0.0, city, address, null, false);
        boolean success = restaurantDAO.addRestaurant(newRestaurant);

        if (success) {
            showAlert("Success", "Restaurant added successfully.");
            clearAddRestaurantFields();
            loadRestaurantsIntoEditScrollPane();
            loadRestaurantsIntoRemoveScrollPane();
        } else {
            showAlert("Error", "Failed to add restaurant.");
        }
    }

    /**
     * Clears the fields for adding a new restaurant.
     */
    private void clearAddRestaurantFields() {
        add_restaurant_name_txtfield.clear();
        add_restaurant_city_txtfield.clear();
        add_restaurant_address_txtfield.clear();
    }

    /**
     * Updates the details of the selected restaurant.
     */
    @FXML
    private void updateRestaurantDetails() {
        if (selectedRestaurantId == -1) {
            showAlert("Error", "No restaurant selected for editing.");
            return;
        }

        String newName = edit_restaurant_name_txtfield.getText().trim();
        String newCity = edit_restaurant_city_txtfield.getText().trim();
        String newAddress = edit_restaurant_address_txtfield.getText().trim();

        if (newName.isEmpty() || newCity.isEmpty() || newAddress.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        boolean nameUpdated = restaurantDAO.updateRestaurant(String.valueOf(selectedRestaurantId), "Name", newName);
        boolean cityUpdated = restaurantDAO.updateRestaurant(String.valueOf(selectedRestaurantId), "City", newCity);
        boolean addressUpdated = restaurantDAO.updateRestaurant(String.valueOf(selectedRestaurantId), "Address", newAddress);

        if (nameUpdated && cityUpdated && addressUpdated) {
            showAlert("Success", "Restaurant updated successfully.");
            loadRestaurantsIntoEditScrollPane();
            loadRestaurantsIntoRemoveScrollPane();
        } else {
            showAlert("Error", "Failed to update restaurant.");
        }
    }

    /**
     * Handles removing a restaurant (soft delete).
     */
    private void handleRemoveRestaurant() {
        if (selectedRestaurantIdToRemove == -1) {
            showAlert("Error", "No restaurant selected for removal.");
            return;
        }

        boolean success = restaurantDAO.removeRestaurant(selectedRestaurantIdToRemove);

        if (success) {
            showAlert("Success", "Restaurant removed successfully.");
            loadRestaurantsIntoRemoveScrollPane();
            loadRestaurantsIntoEditScrollPane();
        } else {
            showAlert("Error", "Failed to remove restaurant.");
        }
    }

    /**
     * Displays an alert dialog.
     *
     * @param title   The title of the alert.
     * @param message The message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads restaurant details into the editing fields.
     *
     * @param restaurantId The ID of the restaurant to load.
     */
    private void loadRestaurantDetails(int restaurantId) {
        Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);
        if (restaurant != null) {
            edit_restaurant_name_txtfield.setText(restaurant.getName());
            edit_restaurant_city_txtfield.setText(restaurant.getCity());
            edit_restaurant_address_txtfield.setText(restaurant.getAddress());
        } else {
            edit_restaurant_name_txtfield.clear();
            edit_restaurant_city_txtfield.clear();
            edit_restaurant_address_txtfield.clear();
        }
    }

    public void loadAdminOrders() {
        // Clear existing VBox orders
        vbox_admin_orders.getChildren().clear();

        try {
            // Fetch the customer's order details from the database
            List<OrderDetail> customerOrderDetails = orderDAO.getUserOrderHistory(adminId);

            if (customerOrderDetails.isEmpty()) {
                Label noOrdersLabel = new Label("No orders available.");
                noOrdersLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
                vbox_admin_orders.getChildren().add(noOrdersLabel);
                return;
            }

            // Group order details by OrderId
            Map<Integer, List<OrderDetail>> ordersGroupedByOrderId = customerOrderDetails.stream()
                    .collect(Collectors.groupingBy(OrderDetail::getOrderId));

            // Display grouped orders in the VBox
            for (Map.Entry<Integer, List<OrderDetail>> entry : ordersGroupedByOrderId.entrySet()) {
                int orderId = entry.getKey();
                List<OrderDetail> orderDetails = entry.getValue();

                VBox orderBox = new VBox();
                orderBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-background-color: #f9f9f9; -fx-spacing: 5;");

                // Add order details (Order ID)
                Label orderLabel = new Label("Order ID: " + orderId);
                orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                VBox detailsBox = new VBox();
                detailsBox.setStyle("-fx-spacing: 5; -fx-padding: 5;");

                for (OrderDetail detail : orderDetails) {
                    Label detailLabel = new Label(
                            "Item ID: " + detail.getItemId() +
                                    ", Quantity: " + detail.getCount() +
                                    ", Price: $" + detail.getPrice() +
                                    ", Total: $" + detail.calculateTotal()
                    );
                    detailLabel.setStyle("-fx-font-size: 14px;");
                    detailsBox.getChildren().add(detailLabel);
                }

                // Add button to display an alert for the order
                Button alertButton = new Button("Show Total Price");
                alertButton.setStyle("-fx-padding: 5; -fx-background-color: #007bff; -fx-text-fill: white;");
                alertButton.setOnAction(event -> {
                    handleCalculateTotalPrice(orderId);
                });

                orderBox.getChildren().addAll(orderLabel, detailsBox, alertButton);

                // Add click event for the order box to reorder
                orderBox.setOnMouseClicked(event -> {
                    reorderSelectedOrder(orderId); // Call the reorder method when clicked
                });

                vbox_admin_orders.getChildren().add(orderBox);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Failed to load orders. Please try again.");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-text-fill: red;");
            vbox_admin_orders.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCalculateTotalPrice(int orderId) {
        // Validate the orderId
        if (orderId <= 0) {
            showAlert("Error", "Invalid order selected. Please select a valid order.");
            return;
        }

        // Variable to store the total price
        BigDecimal totalPrice = BigDecimal.ZERO;

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL CalculateTotalPrice(?, ?)}")) {

            // Set the input parameter (OrderId) and register the output parameter (TotalPrice)
            callableStatement.setInt(1, orderId);
            callableStatement.registerOutParameter(2, java.sql.Types.DECIMAL);

            // Execute the stored procedure
            callableStatement.execute();

            // Retrieve the calculated total price
            totalPrice = callableStatement.getBigDecimal(2);

            // Show the calculated total price in an alert
            showAlert("Total Price", "The total price for Order ID " + orderId + " is: $" + totalPrice);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while calculating the total price.");
        }
    }

    @FXML
    private void reorderSelectedOrder(int selectedOrderId) {
        if (selectedOrderId <= 0) {
            showAlert("Error", "Invalid order selected. Please select a valid order to reorder.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableCreateOrder = connection.prepareCall("{CALL NewOrder(?)}");
             CallableStatement callableAddOrderDetail = connection.prepareCall("{CALL AddOrderDetail(?, ?, ?)}");
             CallableStatement callableGetOrderDetails = connection.prepareCall("{CALL GetOrderDetailsByOrderId(?)}")) {

            // Step 1: Create a new order
            callableCreateOrder.setInt(1, getAdminDefaultAddressId());
            callableCreateOrder.execute();

            // Retrieve the newly created order ID
            int newOrderId = -1;
            try (ResultSet resultSet = connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() AS OrderId")) {
                if (resultSet.next()) {
                    newOrderId = resultSet.getInt("OrderId");
                }
            }

            if (newOrderId == -1) {
                showAlert("Error", "Failed to create a new order.");
                return;
            }

            // Step 2: Fetch the items from the selected order
            callableGetOrderDetails.setInt(1, selectedOrderId);
            try (ResultSet orderDetailsResult = callableGetOrderDetails.executeQuery()) {
                while (orderDetailsResult.next()) {
                    int itemId = orderDetailsResult.getInt("ItemId");
                    int quantity = orderDetailsResult.getInt("Quantity");

                    // Step 3: Add each item to the new order
                    callableAddOrderDetail.setInt(1, newOrderId);
                    callableAddOrderDetail.setInt(2, itemId);
                    callableAddOrderDetail.setInt(3, quantity);
                    callableAddOrderDetail.execute();
                }
            }

            showAlert("Success", "Reorder successful! Your new Order ID is: " + newOrderId);
            loadAdminOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while reordering.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            // Load the Login.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            AnchorPane loginPage = loader.load();

            // Set the Login scene
            Stage stage = (Stage) admin_name.getScene().getWindow();
            stage.setScene(new Scene(loginPage));
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "An error occurred while navigating back to the login page.");
        }
    }

}
