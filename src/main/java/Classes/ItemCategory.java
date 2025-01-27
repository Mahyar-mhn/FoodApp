package Classes;


public class ItemCategory {
    private int itemCategoryId;  // ItemCategoryId in the database
    private int itemId;          // Foreign key to Item table
    private int categoryId;      // Foreign key to Category table

    // Constructor
    public ItemCategory(int itemCategoryId, int itemId, int categoryId) {
        this.itemCategoryId = itemCategoryId;
        this.itemId = itemId;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public int getItemCategoryId() {
        return itemCategoryId;
    }

    public void setItemCategoryId(int itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ItemCategory{" +
                "itemCategoryId=" + itemCategoryId +
                ", itemId=" + itemId +
                ", categoryId=" + categoryId +
                '}';
    }
}
