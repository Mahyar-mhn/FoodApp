//package Classes;
//
//
//public class Item {
//    private int itemId;           // ItemId in the database
//    private int restaurantId;     // Foreign key to Restaurant table
//    private String name;          // Name in the database
//    private double price;         // Price in the database
//    private String photo;         // Photo in the database
//    private String foodDetail;    // FoodDetail in the database
//
//    // Constructor
//    public Item(int itemId, int restaurantId, String name, double price, String photo, String foodDetail) {
//        this.itemId = itemId;
//        this.restaurantId = restaurantId;
//        this.name = name;
//        this.price = price;
//        this.photo = photo;
//        this.foodDetail = foodDetail;
//    }
//
//    // Getters and Setters
//    public int getItemId() {
//        return itemId;
//    }
//
//    public void setItemId(int itemId) {
//        this.itemId = itemId;
//    }
//
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
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
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
//    public String getFoodDetail() {
//        return foodDetail;
//    }
//
//    public void setFoodDetail(String foodDetail) {
//        this.foodDetail = foodDetail;
//    }
//
//    @Override
//    public String toString() {
//        return "Item{" +
//                "itemId=" + itemId +
//                ", restaurantId=" + restaurantId +
//                ", name='" + name + '\'' +
//                ", price=" + price +
//                ", photo='" + photo + '\'' +
//                ", foodDetail='" + foodDetail + '\'' +
//                '}';
//    }
//}
package Classes;

public class Item {
    private int itemId;           // ItemId in the database
    private int restaurantId;     // Foreign key to Restaurant table
    private String name;          // Name in the database
    private double price;         // Price in the database
    private String photo;         // Photo in the database
    private String foodDetail;    // FoodDetail in the database
    private boolean isDeleted;    // IsDeleted in the database (soft delete status)

    // Constructor
    public Item(int itemId, int restaurantId, String name, double price, String photo, String foodDetail, boolean isDeleted) {
        this.itemId = itemId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.photo = photo;
        this.foodDetail = foodDetail;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFoodDetail() {
        return foodDetail;
    }

    public void setFoodDetail(String foodDetail) {
        this.foodDetail = foodDetail;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", restaurantId=" + restaurantId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", photo='" + photo + '\'' +
                ", foodDetail='" + foodDetail + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
