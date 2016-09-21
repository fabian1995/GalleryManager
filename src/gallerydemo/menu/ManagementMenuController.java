/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo.menu;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallery.GalleryNodeData;
import gallery.GalleryNodeSettings;
import gallery.load.DeleteGalleryService;
import gallerydemo.GalleryDemoViewController;
import java.io.File;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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

            TextInputDialog dialog = new TextInputDialog(g.getData().name);
            dialog.setTitle("Umbenennen");
            dialog.setHeaderText(g.isGallery() ? "Die ausgewählte Galerie umbenennen"
                    : "Den ausgewählten Ordner umbenennen");
            dialog.setContentText("Neuer Name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                g.getData().name = result.get();
                GalleryNodeData.saveGalleryNodeData(g.getConfigFile(), g.getData());
                g.updateView();
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

            // Info depending on gallery Status
            if (g.isGallery()) {
                switch (g.getStatus()) {
                    case LOCALNEWER:
                        alert.setContentText("Äderungen, die nicht am Server gespeichert wurden gehen unwiederruflich verloren!");
                        break;
                    case OFFLINE:
                        alert.setContentText("Sämtliche Inhalte gehen unwiederruflich verloren!");
                        break;
                    default:
                        alert.setContentText("Die Galerie wird nur von diesem Computer entfernt. Inhalte am Server sind nicht betroffen.");
                        break;
                }
            } else {
                alert.setContentText("Der Ordner wird mitsamt seinem Inhalt gelöscht. Galerien, die nicht auf den Server kopiert wurden, gehen unwiederruflich verloren!");
            }

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                DeleteGalleryService task = new DeleteGalleryService(
                        this.controller,
                        g,
                        (g.isGallery() ? "Galerie " : "Ordner ") + g.getData().name + " wird gelöscht",
                        null
                );
                task.start();
            }
        });
    }

    @Override
    public void actualizeButtons() {
        if (this.controller.getActiveGallery() == null
                || this.controller.getActiveGallery().isTrunk()) {
            this.galleryPropertiesButton.setDisable(true);
            this.deleteGalleryButton.setDisable(true);
        } else {
            this.galleryPropertiesButton.setDisable(false);
            this.deleteGalleryButton.setDisable(false);
        }
    }

    private void createGalleryOrFolder(boolean isGallery) {
        GalleryNode g = this.controller.getActiveGallery();
        GalleryNode base;
        // No gallery selected
        if (g == null) {
            base = this.controller.getLocalManager().getTrunk();
        }
        // Create outside gallery folder
        else if (g.isGallery()) {
            base = (GalleryNode) g.getParent();
        }
        // Create insode collection folder
        else {
            base = g;
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
            // Check if gallery already exists (check names only)
            for (Object o : base.getChildren().toArray()) {
                if (((GalleryNode)o).getData().name.equals(result.get())) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Galerie konnte nicht erstellt werden");
                    alert.setHeaderText(null);
                    alert.setContentText("Der angegebene Name existiert bereits in diesem Ordner!");
                    alert.showAndWait();
                    this.controller.enableInput();
                    return;
                }
            }
            // If file name exists, create new file name
            File newFolder;
            int i = 0;
            do {
                newFolder = new File(base.getLocation().getPath() + "/" + result.get() + 
                        (i > 0 ? ("-" + i) : ""));
                i++;
            } while(newFolder.exists());
            
            newFolder.mkdir();
            GalleryNode newGallery = new GalleryNode(
                    new File(newFolder.getAbsolutePath() + "/" + (isGallery
                            ? GalleryManager.GALLERY_CONFIG_FILE_NAME
                            : GalleryManager.COLLECTION_CONFIG_FILE_NAME)
                    ),
                    GalleryNodeData.createCalleryNodeData(result.get()),
                    new GalleryNodeSettings(isGallery
                            ? GalleryNodeSettings.GalleryType.GALLERY
                            : GalleryNodeSettings.GalleryType.COLLECTION
                    )
            );
            base.getChildren().add(newGallery);
            base.sortChildren();
            //this.controller.refreshTreeItems();
            base.setExpanded(true);
        }

        this.controller.enableInput();
    }
}
