/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package galleryremote;

import gallery.GalleryNode;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author fabian
 */
public class GalleryRemoteView extends BorderPane {
    
    public GalleryRemoteView(File remote, GalleryNode base) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GalleryRemoteView.fxml"));
        fxmlLoader.setRoot((BorderPane)this);
        fxmlLoader.setController(new GalleryRemoteViewController(remote, base));
        
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
}
