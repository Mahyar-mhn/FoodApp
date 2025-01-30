package Classes;

public class User {
    private int userId;    // UserId in the database
    private String name;   // Name in the database
    private String phone;  // Phone in the database
    private String password; // Password in the database (hashed)

    /**
     * Constructor for User without password.
     *
     * @param userId The user ID.
     * @param name   The user's name.
     * @param phone  The user's phone.
     */
    public User(int userId, String name, String phone) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
    }

    /**
     * Constructor for User with password.
     *
     * @param userId   The user ID.
     * @param name     The user's name.
     * @param phone    The user's phone.
     * @param password The user's password (hashed).
     */
    public User(int userId, String name, String phone, String password) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.password = password;
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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
