package Classes;


public class Feedback {
    private int feedbackId;       // FeedbackId in the database
    private int orderDetailId;    // Foreign key to OrderDetail table
    private double rate;          // Rate in the database (e.g., 4.5 out of 5)
    private String description;   // Description in the database

    // Constructor
    public Feedback(int feedbackId, int orderDetailId, double rate, String description) {
        this.feedbackId = feedbackId;
        this.orderDetailId = orderDetailId;
        this.rate = rate;
        this.description = description;
    }

    // Getters and Setters
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", orderDetailId=" + orderDetailId +
                ", rate=" + rate +
                ", description='" + description + '\'' +
                '}';
    }
}
