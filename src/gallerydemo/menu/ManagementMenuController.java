/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo.menu;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallerydemo.GalleryDemoViewController;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public final class ManagementMenuController extends AbstractMenu {

    @FXML
    private Button newGalleryButton;
    
    @FXML
    private Button newFolderButton;

    @FXML
    private Button galleryPropertiesButton;
    
    @FXML
    private Button deleteGalleryButton;


    public ManagementMenuController(GalleryDemoViewController controller) {

        super(controller, "ManagementMenu.fxml");

        this.actualizeButtons();

        // TODO Implement features for these buttons
        this.galleryPropertiesButton.setDisable(true);
        this.deleteGalleryButton.setDisable(true);
        
        this.newGalleryButton.setOnAction((ActionEvent event) -> {
            this.createGalleryOrFolder(true);
        });
        
        this.newFolderButton.setOnAction((ActionEvent event) -> {
            this.createGalleryOrFolder(false);
        });
    }
    
    @Override
    public void actualizeButtons () {}
    
    private void createGalleryOrFolder(boolean isGallery) {
        GalleryNode g = this.controller.getActiveGallery();
        File base;
        if (g == null)
            base = this.controller.getLocalGalleryLocation();
        else if (g.isGallery())
            base = g.getLocation().getParentFile();
        else
            base = new File(g.getLocation().getAbsolutePath() + "/" + g.getFileName());

        TextInputDialog dialog = new TextInputDialog(
                isGallery ? "Neue Galerie" : "Neuer Ordner");
        dialog.setTitle(
                isGallery ? "Neue Galerie erstellen" : "Neuen Ordner erstellen");
        dialog.setHeaderText(
                isGallery ? "Bitte geben Sie den Namen der neuen Galerie ein"
                        : "Bitte geben Sie den Namen des neuen Ordners ein");
        dialog.setContentText("Name: ");

        this.controller.disableInput(
                isGallery ? "Galerie wird erstellt...\nBitte einen Namen eingeben."
                        : "Ordner wird erstellt...\nBitte einen Namen eingeben.");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            File newFolder = new File(base.getAbsolutePath() + "/" + result.get());
            newFolder.mkdir();
            if (isGallery) {
                GalleryNode newGallery = new GalleryNode(new File(newFolder.getAbsolutePath() + "/" + GalleryManager.GALLERY_CONFIG_FILE_NAME), false, result.get(), false);
                newGallery.saveConfigFile();
            }
            else {
                try {
                    new File(newFolder.getAbsolutePath() + "/" + GalleryManager.COLLECTION_CONFIG_FILE_NAME).createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(ManagementMenuController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.controller.reloadTreeItems();
        }

        this.controller.enableInput();
    }
}
