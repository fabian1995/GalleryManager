/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo.imageView;

import gallery.GalleryImage;
import gallery.GalleryImage.GalleryImageType;
import gallerydemo.GalleryDemoViewController;
import java.awt.Desktop;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    
    @FXML
    private HBox imageRatingBar;

    public ImageViewContainerController(GalleryDemoViewController controller, GalleryImage model, Image thumbnail) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageViewContainer.fxml"));
        fxmlLoader.setRoot((StackPane) this);
        fxmlLoader.setController((StackPane) this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.controller = controller;
        this.model = model;

        this.setOnMouseClicked((MouseEvent) -> {
            if (this.model.type == GalleryImageType.IMAGE) {
                this.controller.enableFullSizeImageView(this.model);
            } else if (Desktop.isDesktopSupported()) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(this.model.file);
                    } catch (IOException ex) {
                        Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
                    }
                }).start();
            }
        });

        this.stackBottomImageView.setImage(thumbnail);
        this.imageRatingBar.setVisible(false);
        
        if (this.model.type == GalleryImageType.VIDEO) {
            this.stackTopInfoPane.setVisible(true);
            this.resolutionText.setText(this.model.file.getName());
        }
        else
            this.stackTopInfoPane.setVisible(false);

        //this.resolutionText.setText("" + originalWidth + " x " + originalHeight + " px");
    }
}
