/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerycompare;

import gallery.GalleryNode;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author fabian
 */
public class GalleryCompareView extends BorderPane {
    
    public final GalleryCompareViewController controller;
    
    public GalleryCompareView(GalleryNode g1, GalleryNode g2) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GalleryCompareView.fxml"));
        fxmlLoader.setRoot((BorderPane)this);
        this.controller = new GalleryCompareViewController(g1, g2);
        fxmlLoader.setController(this.controller);
        
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
}
