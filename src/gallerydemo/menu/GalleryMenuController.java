/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo.menu;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallerycompare.GalleryCompareView;
import gallerydemo.GalleryDemoViewController;
import galleryremote.GalleryRemoteView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class GalleryMenuController extends BorderPane {

    private final GalleryDemoViewController controller;
    
    private final FileChooser fileChooser = new FileChooser();

    @FXML
    private Button newGalleryButton;
    
    @FXML
    private Button newFolderButton;

    @FXML
    private Button galleryPropertiesButton;
    
    @FXML
    private Button deleteGalleryButton;
    
    @FXML
    private Button addimgGalleryButton;
    
    @FXML
    private Button importGalleryButton;
    
    @FXML
    private Button exportGalleryButton;
    
    @FXML
    private Button syncGalleryButton;
    
    

    public GalleryMenuController(GalleryDemoViewController controller) {

        this.controller = controller;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GalleryMenu.fxml"));
        fxmlLoader.setRoot((BorderPane) this);
        fxmlLoader.setController((BorderPane) this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.actualizeButtons();

        // TODO Implement features for these buttons
        this.galleryPropertiesButton.setDisable(true);
        this.deleteGalleryButton.setDisable(true);
        this.exportGalleryButton.setDisable(true);
        
        // Add extension filter to file chooser
        this.fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Images", "*.*"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        
        this.newGalleryButton.setOnAction((ActionEvent event) -> {
            this.createGalleryOrFolder(true);
        });
        
        this.newFolderButton.setOnAction((ActionEvent event) -> {
            this.createGalleryOrFolder(false);
        });

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
        
        this.addimgGalleryButton.setOnAction((ActionEvent event)  -> {
            this.controller.disableInput("Bilder zur Gallerie hinzufügen...");
            List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
            
            if (list != null) {
                for (File f : list) {
                    if (f.getName().matches(GalleryManager.IMAGE_FILE_REGEX)) {
                        try {
                            System.out.println(":: " + f.toPath() + " -> " + new File(this.controller.getActiveGallery().getLocation() + "/" + f.getName()).toPath());
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
    
    public void actualizeButtons() {
        if (this.controller.getActiveGallery() == null
                || !this.controller.getActiveGallery().isGallery()
                || !this.controller.getActiveGallery().getOrigin().exists()) {
            this.syncGalleryButton.setDisable(true);
        }
        else
            this.syncGalleryButton.setDisable(false);
        
        if (this.controller.getActiveGallery() != null
                && this.controller.getActiveGallery().isGallery()) {
            this.addimgGalleryButton.setDisable(false);
        }
        else {
            this.addimgGalleryButton.setDisable(true);
        }

        if (!this.controller.getRemoteGalleryLocation().exists()) {
            this.importGalleryButton.setDisable(true);
        }
        else {
            this.importGalleryButton.setDisable(false);
        }
    }
    
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
                    Logger.getLogger(GalleryMenuController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.controller.reloadTreeItems();
        }

        this.controller.enableInput();
    }
}
