package org.example.foodapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RestaurantCart {

    @FXML
    private ImageView restaurant_image;

    @FXML
    private Label restaurant_name;

    /**
     * Populates the cart with restaurant data.
     *
     * @param name      The name of the restaurant.
     * @param photoFile The photo file name from the `Photo` column in the database.
     */
    public void setRestaurantData(String name, String photoFile) {
        // Set the restaurant name
        restaurant_name.setText(name);

        // Construct the full image path from the file name
//        String imagePath = "file:src/main/resources/images/" + photoFile;


        // Load the image into the ImageView
        try {
            //Image image = new Image(imagePath); // Load image from the constructed path
            Image image = new Image(photoFile); // Load image from the constructed path
            restaurant_image.setImage(image); // Set the loaded image in the ImageView
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to a default image in case of an error
//            restaurant_image.setImage(new Image("C:\\Users\\beta\\Downloads\\foodImage.jpg"));
        }
    }

    /**
     * Resets the cart display in case no data is available.
     */
    public void clearCart() {
        restaurant_name.setText("No Restaurant Data");
//        restaurant_image.setImage(new Image("C:\\Users\\beta\\Downloads\\foodImage.jpg"));
    }
}
