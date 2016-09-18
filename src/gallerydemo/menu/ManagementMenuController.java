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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import org.apache.commons.io.FileUtils;

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

        this.newGalleryButton.setOnAction((ActionEvent event) -> {
            this.createGalleryOrFolder(true);
        });

        this.newFolderButton.setOnAction((ActionEvent event) -> {
            this.createGalleryOrFolder(false);
        });

        this.galleryPropertiesButton.setOnAction((ActionEvent event) -> {
            GalleryNode g = this.controller.getActiveGallery();

            if (g.isTrunk()) {
                return;
            }

            TextInputDialog dialog = new TextInputDialog(g.getName());
            dialog.setTitle("Umbenennen");
            dialog.setHeaderText(g.isGallery() ? "Die ausgewählte Galerie umbenennen"
                    : "Den ausgewählten Ordner umbenennen");
            dialog.setContentText("Neuer Name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                g.setName(result.get());
                g.saveConfigFile();
            }
        });

        this.deleteGalleryButton.setOnAction((ActionEvent event) -> {
            GalleryNode g = this.controller.getActiveGallery();

            if (g.isTrunk()) {
                return;
            }

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Löschen");
            alert.setHeaderText(g.isGallery() ? "Die ausgewählte Galerie löschen?"
                    : "Den ausgewählten Ordner löschen?");
            alert.setContentText("Unwiederruflicher Vorgang!");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                GalleryNode parent = (GalleryNode)g.getParent();
                Logger.getLogger("logfile").log(Level.INFO, "[delete] {0}", g.getLocation());
                try {
                    FileUtils.deleteDirectory(g.getLocation());
                } catch (IOException ex) {
                    Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
                } finally {
                    // Remove deleted gallery from tree view
                    parent.getChildren().remove(g);
                    this.controller.setActiveGallery(parent);
                }
            }
        });
    }

    @Override
    public void actualizeButtons() {
        if (this.controller.getActiveGallery() == null
                || this.controller.getActiveGallery().isTrunk()) {
            this.galleryPropertiesButton.setDisable(true);
            this.deleteGalleryButton.setDisable(true);
        }
        else {
            this.galleryPropertiesButton.setDisable(false);
            this.deleteGalleryButton.setDisable(false);
        }
    }

    private void createGalleryOrFolder(boolean isGallery) {
        GalleryNode g = this.controller.getActiveGallery();
        File base;
        if (g == null) {
            base = this.controller.settings.getLocalGalleryLocation();
        }
        // Create outside gallery folder
        else if (g.isGallery()) {
            base = g.getLocation().getParentFile();
        }
        // Create insode collection folder
        else {
            base = g.getLocation();
        }

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
            System.out.println("mkdir " + newFolder.getPath());
            if (isGallery) {
                GalleryNode newGallery = new GalleryNode(new File(newFolder.getAbsolutePath() + "/" + GalleryManager.GALLERY_CONFIG_FILE_NAME), false, true, result.get(), false);
                newGallery.galleryChanged();
                //newGallery.saveConfigFile();
            } else {
                GalleryNode newGallery = new GalleryNode(new File(newFolder.getAbsolutePath() + "/" + GalleryManager.COLLECTION_CONFIG_FILE_NAME), false, true, result.get(), false);
                newGallery.saveConfigFile();
            }
            this.controller.refreshTreeItems();
            if (g != null)
                g.setExpanded(true);
        }

        this.controller.enableInput();
    }
}
