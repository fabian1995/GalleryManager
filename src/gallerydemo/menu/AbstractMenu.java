/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.menu;

import gallerydemo.GalleryDemoViewController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author fabian
 */
public abstract class AbstractMenu extends BorderPane {
    
    protected final GalleryDemoViewController controller;
    
    public AbstractMenu(GalleryDemoViewController controller, String fxml) {
        this.controller = controller;
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        fxmlLoader.setRoot((BorderPane) this);
        fxmlLoader.setController((BorderPane) this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public abstract void actualizeButtons();
}
