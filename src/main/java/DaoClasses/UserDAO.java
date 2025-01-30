package DaoClasses;

import Classes.User;
import org.example.foodapp.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Fetch all users from the database, including Role and IsDeleted status.
     *
     * @return A list of User objects without passwords for security.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String storedProcedure = "{CALL GetAllUsers()}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(storedProcedure);
             ResultSet resultSet = callableStatement.executeQuery()) {

            while (resultSet.next()) {
                // Create a User object from each row in the result set
                users.add(new User(
                        resultSet.getInt("UserId"),
                        resultSet.getString("Name"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Role"),
                        resultSet.getBoolean("IsDeleted")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Verifies the user's login credentials using the UserLogin stored procedure.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user (raw, before hashing).
     * @return A User object if the credentials are valid, otherwise null.
     */
    public User loginUser(String username, String password) {
        String procedureCall = "{CALL UserLogin(?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Set input parameters for the stored procedure
            callableStatement.setString(1, username.trim());
            callableStatement.setString(2, password.trim());

            // Execute the stored procedure
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Log the fetched role for debugging
                    String role = resultSet.getString("Role");
                    System.out.println("Fetched role: " + role);

                    // Create a User object with the result set data
                    return new User(
                            resultSet.getInt("UserId"),
                            resultSet.getString("Name"),
                            null,  // Password is not fetched for security
                            role,  // Correct role fetched from the result
                            resultSet.getBoolean("IsDeleted")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during user login: " + e.getMessage());
        }

        return null; // Return null if no user is found
    }


    /**
     * Adds a new user to the database using the SignUpUser stored procedure.
     *
     * @param user The User object containing user details.
     * @return true if the user was successfully added, false otherwise.
     */
    public boolean addUser(User user) {
        String procedureCall = "{CALL SignUpUser(?, ?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setString(1, user.getName().trim());
            callableStatement.setString(2, user.getPhone().trim());
            callableStatement.setString(3, user.getPassword().trim());
            callableStatement.setString(4, user.getRole());

            callableStatement.execute();
            return true;

        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                System.err.println("Duplicate user error: " + e.getMessage());
            } else {
                System.err.println("Error adding user: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Fetches a user by ID using the stored procedure `GetUserById`.
     *
     * @param userId The ID of the user to fetch.
     * @return The User object or null if not found.
     */
    public User getUserById(int userId) {
        String storedProcedure = "{CALL GetUserById(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(storedProcedure)) {

            callableStatement.setInt(1, userId);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("UserId"),
                            resultSet.getString("Name"),
                            resultSet.getString("Phone")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Updates a user's details (Name, Phone, or Role) using the EditRecord stored procedure.
     *
     * @param userId     The ID of the user to update.
     * @param columnName The column name to update (e.g., "Name", "Phone", "Role").
     * @param newValue   The new value for the specified column.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateUser(int userId, String columnName, String newValue) {
        String storedProcedure = "{CALL EditRecord(?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(storedProcedure)) {

            callableStatement.setString(1, "User");
            callableStatement.setString(2, columnName);
            callableStatement.setString(3, newValue.trim());
            callableStatement.setString(4, "UserId");
            callableStatement.setInt(5, userId);

            callableStatement.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Removes a user by soft-deleting them using the `DeleteUser` stored procedure.
     *
     * @param userId The ID of the user to remove.
     * @return true if the user was successfully removed, false otherwise.
     */
    public boolean removeUser(int userId) {
        String storedProcedure = "{CALL DeleteUser(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(storedProcedure)) {

            callableStatement.setInt(1, userId);

            // Execute the stored procedure
            callableStatement.execute();
            System.out.println("User with ID " + userId + " has been soft-deleted.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error removing user: " + e.getMessage());
        }

        return false;
    }

}
