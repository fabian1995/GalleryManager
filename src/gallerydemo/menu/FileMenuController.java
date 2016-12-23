/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.menu;

import gallerydemo.GalleryDemoViewController;
import galleryremote.GalleryRemoteView;
import java.awt.Desktop;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public final class FileMenuController extends AbstractMenu {

    @FXML
    private Button locationButton;

    @FXML
    private Button importGalleryButton;
    
    /*@FXML
    private Button settingsButton;*/

    public FileMenuController(GalleryDemoViewController controller) {

        super(controller, "FileMenu.fxml");

        this.locationButton.setOnAction((ActionEvent) -> {
            if (Desktop.isDesktopSupported()) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(this.controller.settings.getLocalGalleryLocation());
                    } catch (IOException ex) {
                        Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
                    }
                }).start();
            }
        });

        this.importGalleryButton.setOnAction((ActionEvent) -> {
            controller.disableInput("Gallerien werden hinzugefügt...");
            Parent root1 = new GalleryRemoteView(
                    this.controller.getLocalManager(),
                    this.controller.getRemoteManager()
            );
            Stage stage = new Stage();
            stage.setTitle("Gallerien importieren...");
            stage.setScene(new Scene(root1));
            stage.show();
            stage.setOnCloseRequest((WindowEvent we) -> {
                controller.enableInput();
            });
        });
        
        /*this.settingsButton.setOnAction((ActionEvent) -> {
            Parent root1 = new GallerySettingsView();
            Stage stage = new Stage();
            stage.setTitle("Einstellungen");
            stage.setScene(new Scene(root1));
            stage.show();
            stage.setOnCloseRequest((WindowEvent we) -> {
                controller.enableInput();
                this.controller.refreshTreeItems();
            });
            controller.disableInput("Einstellungen werden geändert...");
        });*/

        this.actualizeButtons();
    }

    @Override
    public void actualizeButtons() {
        if (this.controller.getRemoteManager() == null) {
            this.importGalleryButton.setDisable(true);
        } else {
            this.importGalleryButton.setDisable(false);
        }
    }
}
