package DaoClasses;

import Classes.Order;
import Classes.OrderDetail;
import org.example.foodapp.DatabaseConnection;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /**
     * Creates a new order in the database and returns its ID.
     *
     * @param addressId The AddressId for the new order.
     * @return The ID of the newly created order, or -1 if the operation failed.
     */
    public int createOrder(int addressId) {
        String procedureCall = "{CALL NewOrder(?)}";
        int orderId = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, addressId);

            callableStatement.execute();

            // Get the ID of the last inserted order
            String query = "SELECT LAST_INSERT_ID() AS orderId";
            try (ResultSet resultSet = connection.createStatement().executeQuery(query)) {
                if (resultSet.next()) {
                    orderId = resultSet.getInt("orderId");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }

    /**
     * Retrieves the order history for a user, including order details and items.
     *
     * @param userId The ID of the user whose orders are to be retrieved.
     * @return A list of orders with their associated details.
     */
    public List<OrderDetail> getUserOrderHistory(int userId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String procedureCall = "{CALL GetUserOrderHistory(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Set the input parameter for the stored procedure
            callableStatement.setInt(1, userId);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Retrieve data from the ResultSet
                    int orderId = resultSet.getInt("OrderId");               // Order ID
                    String orderStatus = resultSet.getString("OrderStatus"); // Order Status
                    double price = resultSet.getDouble("ItemPrice");         // Price of the item
                    int count = resultSet.getInt("Quantity");                // Quantity of the item
                    String itemName = resultSet.getString("ItemName");       // Name of the item
                    boolean isItemDeleted = resultSet.getBoolean("IsItemDeleted"); // Is the item deleted

                    // Create a new OrderDetail object and populate it
                    OrderDetail orderDetail = new OrderDetail(0, price, count, orderId, 0);

                    // Add the OrderDetail to the list
                    orderDetails.add(orderDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }

        return orderDetails;
    }


}
