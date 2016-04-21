/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.menu;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallerycompare.GalleryCompareView;
import gallerydemo.GalleryDemoViewController;
import galleryremote.GalleryRemoteViewController;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
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
import org.apache.commons.io.FileUtils;

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
                stage.show();
                stage.setOnCloseRequest((WindowEvent we) -> {
                    controller.enableInput();
                    controller.reloadGalleryImages(g);
                });
                controller.disableInput(g.getName() + "wird synchronisiert...");
            }
        });
        
        this.exportGalleryButton.setOnAction((ActionEvent event)  -> {
            GalleryNode g = this.controller.getActiveGallery();
            File origin = g.getLocation();
            File target = new File(controller.getRemoteGalleryLocation().getAbsolutePath() + "/" + origin.getAbsolutePath().replaceAll(controller.getLocalGalleryLocation().getAbsolutePath(), ""));
            Logger.getLogger("logfile").info("[export] " + g.getFileName() + ": " + origin + " -> " + target );
            try {
                FileUtils.copyDirectory(origin, target, new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().equals(GalleryManager.GALLERY_CONFIG_FILE_NAME)
                                || pathname.getName().matches(GalleryManager.IMAGE_FILE_REGEX);
                    }
                });
                g.setOrigin(target);
                g.saveConfigFile();
            } catch (IOException ex) {
                Logger.getLogger(GalleryRemoteViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.actualizeButtons();
        });
        
        this.addimgGalleryButton.setOnAction((ActionEvent event)  -> {
            this.controller.disableInput("Bilder zur Gallerie hinzufügen...");
            List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
            
            if (list != null) {
                for (File f : list) {
                    if (f.getName().matches(GalleryManager.IMAGE_FILE_REGEX)) {
                        try {
                            Files.copy(f.toPath(), new File(this.controller.getActiveGallery().getLocation() + "/" + f.getName()).toPath(), COPY_ATTRIBUTES);
                        } catch (IOException ex) {
                            Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            this.controller.reloadGalleryImages(this.controller.getActiveGallery());
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
