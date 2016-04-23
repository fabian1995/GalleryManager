/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo.imageView;

import gallery.GalleryImage;
import gallerydemo.GalleryDemoViewController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class ImageViewContainerController extends StackPane {
    
    private final GalleryDemoViewController controller;
    private final GalleryImage model;
    
    @FXML
    private ImageView stackBottomImageView;
    
    @FXML
    private BorderPane stackTopInfoPane;
    
    @FXML
    private Text resolutionText;
    
    public ImageViewContainerController(GalleryDemoViewController controller, GalleryImage model, Image thumbnail) {
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageViewContainer.fxml"));
        fxmlLoader.setRoot((StackPane)this);
        fxmlLoader.setController((StackPane)this);
        
        try {
            fxmlLoader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        this.controller = controller;
        this.model = model;
        
        this.setOnMouseClicked((MouseEvent) -> {
            this.controller.enableFullImageView(this.model);
        });
        
        this.stackBottomImageView.setImage(thumbnail);
        this.stackTopInfoPane.setVisible(false);
        
        //this.resolutionText.setText("" + originalWidth + " x " + originalHeight + " px");
    }
}
