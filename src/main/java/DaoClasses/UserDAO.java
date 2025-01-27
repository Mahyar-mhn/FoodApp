package DaoClasses;

import Classes.User;
import org.example.foodapp.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Fetch all users from the database
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM User";

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
}
