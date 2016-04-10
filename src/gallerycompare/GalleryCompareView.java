/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerycompare;

import gallery.GalleryNode;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author fabian
 */
public class GalleryCompareView extends BorderPane {
    
    public GalleryCompareView(GalleryNode g1, GalleryNode g2) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GalleryCompareView.fxml"));
        fxmlLoader.setRoot((BorderPane)this);
        fxmlLoader.setController(new GalleryCompareViewController(g1, g2));
        
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
}
