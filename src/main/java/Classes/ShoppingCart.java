package Classes;

import java.time.LocalDateTime;

public class ShoppingCart {
    private int cartId;         // CartId in the database
    private int userId;         // Foreign key to User table
    private int itemId;         // Foreign key to Item table
    private LocalDateTime date; // Date when the item was added to the cart
    private int count;          // Quantity of the item in the cart

    // Constructor
    public ShoppingCart(int cartId, int userId, int itemId, LocalDateTime date, int count) {
        this.cartId = cartId;
        this.userId = userId;
        this.itemId = itemId;
        this.date = date;
        this.count = count;
    }

    // Getters and Setters
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // Method to calculate the total price for the items in the cart
    public double calculateTotal(double itemPrice) {
        return itemPrice * count;
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", date=" + date +
                ", count=" + count +
                '}';
    }
}
