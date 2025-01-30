package DaoClasses;

import Classes.Restaurant;
import org.example.foodapp.DatabaseConnection;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {

    /**
     * Fetches all restaurant data (including RestaurantId, Name, Photo, MinPurchase, City, Address, Coordinate, and IsDeleted)
     * from the database using the stored procedure `GetAllRestaurants`.
     *
     * @return A list of Restaurant objects containing the data.
     */
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String procedureCall = "{CALL GetAllRestaurants()}"; // Stored procedure call

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall);
             ResultSet resultSet = callableStatement.executeQuery()) {

            // Iterate through the result set and populate the Restaurant list
            while (resultSet.next()) {
                int restaurantId = resultSet.getInt("RestaurantId");
                String name = resultSet.getString("Name");
                String photo = resultSet.getString("Photo");
                double minPurchase = resultSet.getDouble("MinPurchase");
                String city = resultSet.getString("City");
                String address = resultSet.getString("Address");
                String coordinate = resultSet.getString("Coordinate");
                boolean isDeleted = resultSet.getBoolean("IsDeleted");

                // Add a new Restaurant object to the list
                restaurants.add(new Restaurant(restaurantId, name, photo, minPurchase, city, address, coordinate, isDeleted));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging purposes
        }

        return restaurants;
    }
}
