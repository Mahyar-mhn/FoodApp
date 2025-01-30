package DaoClasses;

import Classes.User;
import org.example.foodapp.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Fetch all users from the database.
     *
     * @return A list of User objects without passwords.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT UserId, Name, Phone FROM User";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                // Create a User object from each row in the result set
                User user = new User(
                        resultSet.getInt("UserId"),
                        resultSet.getString("Name"),
                        resultSet.getString("Phone")
                );
                users.add(user); // Add the User to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Verifies the user's login credentials using the UserLogin stored procedure.
     *
     * @param userName     The username entered by the user.
     * @param userPassword The password entered by the user (raw, before hashing).
     * @return A User object if the credentials are valid, otherwise null.
     */
    public User loginUser(String userName, String userPassword) {
        User user = null;

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Call the stored procedure
            String procedureCall = "{CALL UserLogin(?, ?)}";
            CallableStatement callableStatement = connection.prepareCall(procedureCall);

            // Set input parameters
            callableStatement.setString(1, userName);
            callableStatement.setString(2, userPassword);

            // Execute the procedure and get the result
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Create a User object with the retrieved data
                    int userId = resultSet.getInt("UserId");
                    String name = resultSet.getString("Name");
                    user = new User(userId, name, null); // Password is not retrieved for security
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user; // Return the user object if valid, or null if invalid
    }
    /**
     * Adds a new user to the database using the SignUpUser stored procedure.
     *
     * @param user The User object containing user details.
     * @return true if the user was successfully added, false otherwise.
     */
    public boolean addUser(User user) {
        String procedureCall = "{CALL SignUpUser(?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Set the input parameters for the stored procedure
            callableStatement.setString(1, user.getName());
            callableStatement.setString(2, user.getPhone());
            callableStatement.setString(3, user.getPassword());

            // Execute the stored procedure
            callableStatement.execute();
            return true; // Return true if no exception occurs

        } catch (SQLException e) {
            // Handle specific SQL exception for duplicate users
            if (e.getSQLState().equals("45000")) {
                System.err.println("Error: " + e.getMessage());
            } else {
                e.printStackTrace();
            }
        }

        return false; // Return false if an exception occurs
    }
//
//    /**
//     * Updates an existing user's phone or password.
//     *
//     * @param userId       The ID of the user to update.
//     * @param newPhone     The new phone number (optional, pass null to skip).
//     * @param newPassword  The new password (optional, pass null to skip).
//     * @return true if the update was successful, false otherwise.
//     */
//    public boolean updateUser(int userId, String newPhone, String newPassword) {
//        StringBuilder query = new StringBuilder("UPDATE User SET ");
//        boolean updatePhone = (newPhone != null && !newPhone.isEmpty());
//        boolean updatePassword = (newPassword != null && !newPassword.isEmpty());
//
//        if (updatePhone) {
//            query.append("Phone = ?");
//        }
//        if (updatePassword) {
//            if (updatePhone) query.append(", ");
//            query.append("Password = SHA2(?, 256)");
//        }
//        query.append(" WHERE UserId = ?");
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
//
//            int paramIndex = 1;
//
//            if (updatePhone) {
//                preparedStatement.setString(paramIndex++, newPhone);
//            }
//            if (updatePassword) {
//                preparedStatement.setString(paramIndex++, newPassword);
//            }
//            preparedStatement.setInt(paramIndex, userId);
//
//            int rowsAffected = preparedStatement.executeUpdate();
//            return rowsAffected > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//    /**
//     * Deletes a user from the database.
//     *
//     * @param userId The ID of the user to delete.
//     * @return true if the user was successfully deleted, false otherwise.
//     */
//    public boolean deleteUser(int userId) {
//        String query = "DELETE FROM User WHERE UserId = ?";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, userId);
//
//            int rowsAffected = preparedStatement.executeUpdate();
//            return rowsAffected > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
}
