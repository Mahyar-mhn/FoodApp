package Classes;

import java.util.List;

public class Order {
    private int orderId;   // OrderId in the database
    private int addressId; // Foreign key to Address table
    private String status; // Status in the database
    private List<OrderDetail> orderDetails;


    // Constructor
    public Order(int orderId, int addressId, String status) {
        this.orderId = orderId;
        this.addressId = addressId;
        this.status = status;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", addressId=" + addressId +
                ", status='" + status + '\'' +
                '}';
    }
}
