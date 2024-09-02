import javax.swing.*;
import java.awt.*;

public class ImageChecker {
    // Load image method
    public Image loadImage(String path) {
        Image image = Toolkit.getDefaultToolkit().getImage(path);
        if (image == null) {
            System.out.println("Failed to load image from path: " + path);
        }
        return image;
    }

    private void addGuiComponents() {
        // Example of loading an image
        Image image = loadImage("path/to/your/image.png");

        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            // Add icon to your GUI component
        } else {
            System.out.println("Cannot add icon, image is null");
        }
    }

    public ImageChecker() {
        // Initialize components
        addGuiComponents();
    }
}
