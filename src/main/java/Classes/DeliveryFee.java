package Classes;


public class DeliveryFee {
    private int feeId;           // FeeId in the database
    private int restaurantId;    // Foreign key to Restaurant table
    private double price;        // Price in the database
    private boolean tenderArea;  // TenderArea in the database

    // Constructor
    public DeliveryFee(int feeId, int restaurantId, double price, boolean tenderArea) {
        this.feeId = feeId;
        this.restaurantId = restaurantId;
        this.price = price;
        this.tenderArea = tenderArea;
    }

    // Getters and Setters
    public int getFeeId() {
        return feeId;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isTenderArea() {
        return tenderArea;
    }

    public void setTenderArea(boolean tenderArea) {
        this.tenderArea = tenderArea;
    }

    @Override
    public String toString() {
        return "DeliveryFee{" +
                "feeId=" + feeId +
                ", restaurantId=" + restaurantId +
                ", price=" + price +
                ", tenderArea=" + tenderArea +
                '}';
    }
}
