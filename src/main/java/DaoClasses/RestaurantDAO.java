package DaoClasses;

import Classes.Restaurant;
import org.example.foodapp.DatabaseConnection;

import java.sql.*;
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

            while (resultSet.next()) {
                int restaurantId = resultSet.getInt("RestaurantId");
                String name = resultSet.getString("Name");
                String photo = resultSet.getString("Photo");
                double minPurchase = resultSet.getDouble("MinPurchase");
                String city = resultSet.getString("City");
                String address = resultSet.getString("Address");
                String coordinate = resultSet.getString("Coordinate");
                boolean isDeleted = resultSet.getBoolean("IsDeleted");

                restaurants.add(new Restaurant(restaurantId, name, photo, minPurchase, city, address, coordinate, isDeleted));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging purposes
        }

        return restaurants;
    }

    /**
     * Inserts a new restaurant into the database using the `AddRestaurant` stored procedure.
     *
     * @param restaurant The Restaurant object containing the restaurant details.
     * @return true if the restaurant was successfully added, false otherwise.
     */
    public boolean addRestaurant(Restaurant restaurant) {
        String procedureCall = "{CALL AddRestaurant(?, ?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Set the input parameters for the stored procedure
            callableStatement.setString(1, restaurant.getName());
            callableStatement.setString(2, restaurant.getPhoto());
            callableStatement.setDouble(3, restaurant.getMinPurchase());
            callableStatement.setString(4, restaurant.getCity());
            callableStatement.setString(5, restaurant.getAddress());
            callableStatement.setString(6, restaurant.getCoordinate());

            callableStatement.execute();
            return true; // Return true if no exception occurs
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }

        return false; // Return false if an exception occurs
    }

    /**
     * Updates the details of a restaurant in the database using the `UpdateRestaurant` stored procedure.
     *
     * @param restaurantName The name of the restaurant to update.
     * @param columnName     The column name to update (e.g., "City", "Address").
     * @param newValue       The new value for the specified column.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateRestaurant(String restaurantName, String columnName, String newValue) {
        String procedureCall = "{CALL UpdateRestaurant(?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setString(1, restaurantName);
            callableStatement.setString(2, columnName);
            callableStatement.setString(3, newValue);

            callableStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }

        return false;
    }

    /**
     * Soft deletes a restaurant from the database using the `DeleteRestaurant` stored procedure.
     *
     * @param restaurantId The ID of the restaurant to delete.
     * @return true if the restaurant was successfully soft-deleted, false otherwise.
     */
    public boolean removeRestaurant(int restaurantId) {
        String procedureCall = "{CALL DeleteRestaurant(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, restaurantId);

            callableStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }

        return false;
    }

    /**
     * Fetches a restaurant by ID using a stored procedure.
     *
     * @param restaurantId The ID of the restaurant to fetch.
     * @return The Restaurant object or null if not found.
     */
    public Restaurant getRestaurantById(int restaurantId) {
        String procedureCall = "{CALL GetRestaurantById(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, restaurantId);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Restaurant(
                            resultSet.getInt("RestaurantId"),
                            resultSet.getString("Name"),
                            resultSet.getString("Photo"),
                            resultSet.getDouble("MinPurchase"),
                            resultSet.getString("City"),
                            resultSet.getString("Address"),
                            resultSet.getString("Coordinate"),
                            resultSet.getBoolean("IsDeleted")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }

        return null;
    }
}
