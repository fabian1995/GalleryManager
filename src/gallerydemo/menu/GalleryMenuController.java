/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.menu;

import gallery.GalleryNode;
import gallery.load.AddImageService;
import gallery.load.CopyGalleryService;
import gallerycompare.GalleryCompareView;
import gallerydemo.GalleryDemoViewController;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public final class GalleryMenuController extends AbstractMenu {
    
    private final FileChooser fileChooser = new FileChooser();

    @FXML
    private Button addimgGalleryButton;
    
    @FXML
    private Button exportGalleryButton;
    
    @FXML
    private Button syncGalleryButton;
    
    public GalleryMenuController(GalleryDemoViewController controller) {
        super(controller, "GalleryMenu.fxml");
        
        this.actualizeButtons();
        
        // Add extension filter to file chooser
        this.fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Images", "*.*"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        
        this.syncGalleryButton.setOnAction((ActionEvent event) -> {
            GalleryNode g = controller.getActiveGallery();

            if (g.getOrigin() != null && g.getOrigin().exists()) {
                Parent root1 = new GalleryCompareView(g, new GalleryNode(g.getOrigin()));
                Stage stage = new Stage();
                stage.setTitle(g.getName() + " synchronisieren");
                stage.setScene(new Scene(root1));
                stage.setMinWidth(750);
                stage.setMinHeight(450);
                stage.show();
                stage.setOnCloseRequest((WindowEvent we) -> {
                    controller.enableInput();
                    controller.reloadGalleryImages(g, true);
                });
                controller.disableInput(g.getName() + " wird synchronisiert...");
            }
        });
        
        this.exportGalleryButton.setOnAction((ActionEvent event)  -> {
            GalleryNode g = this.controller.getActiveGallery();
            
            File origin = g.getLocation();
            String absOriginPath = origin.getAbsolutePath().replace('\\', '/');
            String absLocalPath = controller.getLocalGalleryLocation().getAbsolutePath().replace('\\', '/');
            String absRemotePath = controller.getRemoteGalleryLocation().getAbsolutePath().replace('\\', '/');

            Logger.getLogger("logfile").log(Level.INFO, "[export] ORIGIN " + absOriginPath);
            Logger.getLogger("logfile").log(Level.INFO, "[export] LOCAL  " + absLocalPath);
            Logger.getLogger("logfile").log(Level.INFO, "[export] REMOTE " + absRemotePath);

            File target = new File(absRemotePath + "/" + absOriginPath.replaceFirst(absLocalPath, ""));
            Logger.getLogger("logfile").info("[export] " + g.getFileName() + ": " + origin + " -> " + target );
            
            CopyGalleryService task = new CopyGalleryService(this.controller, origin, target,
                    "Exporting gallery '" + g + "'",
                    () -> {
                        g.setOrigin(target);
                        g.saveConfigFile();
                        this.actualizeButtons();
                    }
            );
            task.start();
            
            this.exportGalleryButton.setDisable(true);
        });
        
        this.addimgGalleryButton.setOnAction((ActionEvent event)  -> {
            this.controller.disableInput("Bilder zur Gallerie hinzufügen...");
            List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
            
            if (list != null) {
                AddImageService task = new AddImageService(controller, this.controller.getActiveGallery(), list);
                task.start();
            }
            this.controller.enableInput();
        });
    }
    
    @Override
    public void actualizeButtons() {
        if (this.controller.getActiveGallery() != null
                && this.controller.getActiveGallery().isGallery()) {
            
            if (this.controller.getActiveGallery().getOrigin().exists()) {
                this.syncGalleryButton.setVisible(true);
                this.exportGalleryButton.setVisible(false);
            }
            else {
                this.syncGalleryButton.setVisible(false);
                this.exportGalleryButton.setVisible(true);
            }
            this.exportGalleryButton.setDisable(false);
        }
        else {
            this.syncGalleryButton.setVisible(false);
            this.exportGalleryButton.setVisible(true);
            this.exportGalleryButton.setDisable(true);
        }
        
        if (this.controller.getActiveGallery() != null
                && this.controller.getActiveGallery().isGallery()) {
            this.addimgGalleryButton.setDisable(false);
        }
        else {
            this.addimgGalleryButton.setDisable(true);
        }
    }
}
