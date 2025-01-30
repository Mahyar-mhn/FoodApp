//package Classes;
//
//public class Restaurant {
//    private int restaurantId;  // RestaurantId in the database
//    private String name;       // Name in the database
//    private String photo;      // Photo in the database
//    private double minPurchase; // MinPurchase in the database
//    private String city;       // City in the database
//    private String address;    // Address in the database
//    private String coordinate; // Coordinate in the database
//
//    // Constructor
//    public Restaurant(int restaurantId, String name, String photo, double minPurchase, String city, String address, String coordinate) {
//        this.restaurantId = restaurantId;
//        this.name = name;
//        this.photo = photo;
//        this.minPurchase = minPurchase;
//        this.city = city;
//        this.address = address;
//        this.coordinate = coordinate;
//    }
//
//    // Getters and Setters
//    public int getRestaurantId() {
//        return restaurantId;
//    }
//
//    public void setRestaurantId(int restaurantId) {
//        this.restaurantId = restaurantId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(String photo) {
//        this.photo = photo;
//    }
//
//    public double getMinPurchase() {
//        return minPurchase;
//    }
//
//    public void setMinPurchase(double minPurchase) {
//        this.minPurchase = minPurchase;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getCoordinate() {
//        return coordinate;
//    }
//
//    public void setCoordinate(String coordinate) {
//        this.coordinate = coordinate;
//    }
//
//    @Override
//    public String toString() {
//        return "Restaurant{" +
//                "restaurantId=" + restaurantId +
//                ", name='" + name + '\'' +
//                ", photo='" + photo + '\'' +
//                ", minPurchase=" + minPurchase +
//                ", city='" + city + '\'' +
//                ", address='" + address + '\'' +
//                ", coordinate='" + coordinate + '\'' +
//                '}';
//    }
//}
package Classes;

public class Restaurant {
    private int restaurantId;  // RestaurantId in the database
    private String name;       // Name in the database
    private String photo;      // Photo in the database
    private double minPurchase; // MinPurchase in the database
    private String city;       // City in the database
    private String address;    // Address in the database
    private String coordinate; // Coordinate in the database
    private boolean isDeleted; // IsDeleted in the database (soft delete status)

    // Constructor
    public Restaurant(int restaurantId, String name, String photo, double minPurchase, String city, String address, String coordinate, boolean isDeleted) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.photo = photo;
        this.minPurchase = minPurchase;
        this.city = city;
        this.address = address;
        this.coordinate = coordinate;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getMinPurchase() {
        return minPurchase;
    }

    public void setMinPurchase(double minPurchase) {
        this.minPurchase = minPurchase;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantId=" + restaurantId +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", minPurchase=" + minPurchase +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", coordinate='" + coordinate + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
