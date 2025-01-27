package Classes;

public class WorkingTime {
    private int workingTimeId;   // WorkingTimeId in the database
    private int restaurantId;    // Foreign key to Restaurant table
    private String day;          // Day of the week (e.g., "Monday")
    private String openAt;       // Opening time (e.g., "10:00:00")
    private String closeAt;      // Closing time (e.g., "22:00:00")

    // Constructor
    public WorkingTime(int workingTimeId, int restaurantId, String day, String openAt, String closeAt) {
        this.workingTimeId = workingTimeId;
        this.restaurantId = restaurantId;
        this.day = day;
        this.openAt = openAt;
        this.closeAt = closeAt;
    }

    // Getters and Setters
    public int getWorkingTimeId() {
        return workingTimeId;
    }

    public void setWorkingTimeId(int workingTimeId) {
        this.workingTimeId = workingTimeId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getOpenAt() {
        return openAt;
    }

    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }

    public String getCloseAt() {
        return closeAt;
    }

    public void setCloseAt(String closeAt) {
        this.closeAt = closeAt;
    }

    // Method to check if a restaurant is open at a given time
    public boolean isOpen(String time) {
        return time.compareTo(openAt) >= 0 && time.compareTo(closeAt) <= 0;
    }

    @Override
    public String toString() {
        return "WorkingTime{" +
                "workingTimeId=" + workingTimeId +
                ", restaurantId=" + restaurantId +
                ", day='" + day + '\'' +
                ", openAt='" + openAt + '\'' +
                ", closeAt='" + closeAt + '\'' +
                '}';
    }
}
