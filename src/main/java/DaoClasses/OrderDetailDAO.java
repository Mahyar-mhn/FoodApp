package DaoClasses;

import org.example.foodapp.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class OrderDetailDAO {

    /**
     * Adds a new order detail to the database using the `AddOrderDetail` stored procedure.
     *
     * @param orderId  The ID of the order.
     * @param itemId   The ID of the item being ordered.
     * @param quantity The quantity of the item being ordered.
     * @return true if the operation was successful, false otherwise.
     */
    public boolean addOrderDetail(int orderId, int itemId, int quantity) {
        String procedureCall = "{CALL AddOrderDetail(?, ?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, orderId);
            callableStatement.setInt(2, itemId);
            callableStatement.setInt(3, quantity);

            callableStatement.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
