/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerysettings;

import gallerydemo.settings.GallerySettings;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

/**
 *
 * @author fabian
 */
public class GallerySettingsView extends GridPane {
    
    public GallerySettingsView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GallerySettingsView.fxml"));
        fxmlLoader.setRoot((GridPane)this);
        fxmlLoader.setController(new GallerySettingsViewController(new GallerySettings()));
        
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
}