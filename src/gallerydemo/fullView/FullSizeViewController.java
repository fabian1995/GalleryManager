/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.fullView;

import gallery.GalleryImage;
import gallerydemo.GalleryDemoViewController;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class FullSizeViewController extends BorderPane {

    private final GalleryDemoViewController controller;
    
    @FXML
    private VBox fullScreenPane;
    
    @FXML
    private ImageView fullScreenImage;
    
    @FXML
    private Button buttonClose;
    
    @FXML
    private Button buttonPrev;
    
    @FXML
    private Button buttonNext;
    
    private List<GalleryImage> imageList;
    private int selectedIndex;
    
    public FullSizeViewController(GalleryDemoViewController controller) {
        this.controller = controller;
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FullSizeView.fxml"));
        fxmlLoader.setRoot((BorderPane) this);
        fxmlLoader.setController((BorderPane) this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        this.fullScreenImage.fitWidthProperty().bind(this.fullScreenPane.widthProperty());
        this.fullScreenImage.fitHeightProperty().bind(this.fullScreenPane.heightProperty());
        this.fullScreenImage.setPreserveRatio(true);
        
        this.buttonClose.setOnAction((ActionEvent) -> {
            this.controller.disableFullSizeImageView();
        });
        
        this.buttonNext.setOnAction((ActionEvent) -> {
            if (this.selectedIndex < this.imageList.size()) {
                this.selectedIndex ++;
                this.selectImageByIndex();
            }
        });
        
        this.buttonPrev.setOnAction((ActionEvent) -> {
            if (this.selectedIndex > 0) {
                this.selectedIndex --;
                this.selectImageByIndex();
            }
        });
    }
    
    public void imageSelected(List<GalleryImage> imageList, GalleryImage image) {
        this.imageList = imageList;
        this.selectedIndex = imageList.indexOf(image);
        
        this.selectImageByIndex();
    }
    
    private void selectImageByIndex() {
        this.fullScreenImage.setImage(new Image("file:" + this.imageList.get(this.selectedIndex).file));
        
        if (this.selectedIndex+1 < this.imageList.size())
            this.buttonNext.setDisable(false);
        else
            this.buttonNext.setDisable(true);
        
        if (this.selectedIndex > 0)
            this.buttonPrev.setDisable(false);
        else
            this.buttonPrev.setDisable(true);
    }
}
