package Classes;

public class User {
    private int userId;       // UserId in the database
    private String name;      // Name in the database
    private String phone;     // Phone in the database
    private String password;  // Password in the database (hashed)
    private String role;      // Role in the database (e.g., User, Admin, Manager)
    private boolean isDeleted; // IsDeleted in the database (soft delete status)

    // Constructor: Full fields
    public User(int userId, String name, String phone, String password, String role, boolean isDeleted) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    // Constructor: Without password (for fetching user data without sensitive info)
    public User(int userId, String name, String phone, String role, boolean isDeleted) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    // Constructor: Minimal fields (e.g., for simplified queries)
    public User(int userId, String name, String phone, boolean isDeleted) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.role = "User"; // Default role when not provided
        this.isDeleted = isDeleted;
    }

    // Default Constructor
    public User() {
        // Empty constructor for initialization flexibility
    }

    public User(int userId, String name, String phone) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
