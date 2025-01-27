package Classes;


public class OrderDetail {
    private int orderDetailId;   // OrderDetailId in the database
    private double price;        // Price in the database
    private int count;           // Count in the database (quantity of the item)
    private int orderId;         // Foreign key to Order table
    private int itemId;          // Foreign key to Item table

    // Constructor
    public OrderDetail(int orderDetailId, double price, int count, int orderId, int itemId) {
        this.orderDetailId = orderDetailId;
        this.price = price;
        this.count = count;
        this.orderId = orderId;
        this.itemId = itemId;
    }

    // Getters and Setters
    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    // Method to calculate the total price for the order detail
    public double calculateTotal() {
        return price * count;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailId=" + orderDetailId +
                ", price=" + price +
                ", count=" + count +
                ", orderId=" + orderId +
                ", itemId=" + itemId +
                ", total=" + calculateTotal() +
                '}';
    }
}
