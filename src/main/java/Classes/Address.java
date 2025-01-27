package Classes;


public class Address {
    private int addressId; // AddId in the database
    private int userId;    // Foreign key to User table
    private boolean defaultAddress; // DefaultAddress in the database
    private String city;   // City in the database
    private String addressDetail; // AddressDetail in the database
    private String coordinate; // Coordinate in the database

    // Constructor
    public Address(int addressId, int userId, boolean defaultAddress, String city, String addressDetail, String coordinate) {
        this.addressId = addressId;
        this.userId = userId;
        this.defaultAddress = defaultAddress;
        this.city = city;
        this.addressDetail = addressDetail;
        this.coordinate = coordinate;
    }

    // Getters and Setters
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", userId=" + userId +
                ", defaultAddress=" + defaultAddress +
                ", city='" + city + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", coordinate='" + coordinate + '\'' +
                '}';
    }
}

