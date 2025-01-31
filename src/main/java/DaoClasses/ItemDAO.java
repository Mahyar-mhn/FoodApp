package DaoClasses;

import Classes.Item;
import org.example.foodapp.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    /**
     * Retrieves all menu items for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @return A list of Item objects for the given restaurant.
     */
    public List<Item> getMenuItemsByRestaurant(int restaurantId) {
        List<Item> items = new ArrayList<>();
        String procedureCall = "{CALL GetMenuItemsByRestaurant(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, restaurantId);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = new Item(
                            resultSet.getInt("ItemId"),
                            resultSet.getInt("RestaurantId"),
                            resultSet.getString("Name"),
                            resultSet.getDouble("Price"),
                            resultSet.getString("Photo"),
                            resultSet.getString("FoodDetail"),
                            resultSet.getBoolean("IsDeleted")
                    );
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Adds a new menu item using the AddMenuItem stored procedure.
     *
     * @param item The Item object to be added.
     * @return true if the item was added successfully, false otherwise.
     */
    public boolean addMenuItem(Item item) {
        String procedureCall = "{CALL AddMenuItem(?, ?, ?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, item.getRestaurantId());
            callableStatement.setString(2, item.getName());
            callableStatement.setDouble(3, item.getPrice());
            callableStatement.setString(4, item.getPhoto());
            callableStatement.setString(5, item.getFoodDetail());

            callableStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a menu item using the UpdateMenuItem stored procedure.
     *
     * @param itemId     The ID of the item to update.
     * @param columnName The column to update (e.g., "Price").
     * @param newValue   The new value for the column.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateMenuItem(int itemId, String columnName, String newValue) {
        String procedureCall = "{CALL UpdateMenuItem(?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, itemId);
            callableStatement.setString(2, columnName);
            callableStatement.setString(3, newValue);

            callableStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Soft deletes a menu item using the DeleteMenuItem stored procedure.
     *
     * @param itemId The ID of the item to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteMenuItem(int itemId) {
        String procedureCall = "{CALL DeleteMenuItem(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, itemId);

            callableStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all menu items for a specific restaurant using the getItemsByRestaurantId stored procedure.
     *
     * @param restaurantId The ID of the restaurant.
     * @return A list of Item objects for the given restaurant.
     */
    public List<Item> getItemsByRestaurantId(int restaurantId) {
        List<Item> items = new ArrayList<>();
        String procedureCall = "{CALL getItemsByRestaurantId(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, restaurantId);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = new Item(
                            resultSet.getInt("ItemId"),
                            restaurantId,
                            resultSet.getString("Name"),
                            resultSet.getDouble("Price"),
                            resultSet.getString("Photo"),
                            resultSet.getString("FoodDetail"),
                            false // IsDeleted is always false since the query filters out deleted items
                    );
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }


}
