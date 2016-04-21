/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.menu;

import gallerydemo.GalleryDemoViewController;
import galleryremote.GalleryRemoteView;
import java.awt.Desktop;
import javafx.event.ActionEvent;
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

    public FileMenuController(GalleryDemoViewController controller) {

        super(controller, "FileMenu.fxml");

        this.locationButton.setOnAction((ActionEvent event) -> {
            System.out.println("" + this.controller.getLocalGalleryLocation() + " | " + Desktop.isDesktopSupported());
            // TODO: The fillowing lines do not work :( -> program freezes (only on my OS...?)
            /*try {
                Desktop.getDesktop().open(new File("/home/fabian"));//this.controller.getLocalGalleryLocation());
            } catch (IOException ex) {
                Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
            }*/
        });

        this.importGalleryButton.setOnAction((ActionEvent event) -> {
            Parent root1 = new GalleryRemoteView(controller.getRemoteGalleryLocation(), controller.getRoot());
            Stage stage = new Stage();
            stage.setTitle("Gallerien importieren...");
            stage.setScene(new Scene(root1));
            stage.show();
            stage.setOnCloseRequest((WindowEvent we) -> {
                controller.enableInput();
                this.controller.reloadTreeItems();
            });
            controller.disableInput("Gallerien werden hinzugefügt...");
        });

        this.actualizeButtons();
    }

    @Override
    public void actualizeButtons() {
        if (!this.controller.getRemoteGalleryLocation().exists()) {
            this.importGalleryButton.setDisable(true);
        } else {
            this.importGalleryButton.setDisable(false);
        }
    }
}
