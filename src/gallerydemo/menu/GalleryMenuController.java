/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo.menu;

import gallery.GalleryNode;
import gallerycompare.GalleryCompareView;
import gallerydemo.GalleryDemoViewController;
import galleryremote.GalleryRemoteView;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class GalleryMenuController extends BorderPane {

    private final GalleryDemoViewController controller;

    @FXML
    private Button newGalleryButton;

    @FXML
    private Button deleteGalleryButton;

    @FXML
    private Button galleryPropertiesButton;

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

        this.newGalleryButton.setOnAction((ActionEvent event) -> {
            Parent root1 = new GalleryRemoteView(controller.getRemoteGalleryLocation(), controller.getRoot());
            Stage stage = new Stage();
            stage.setTitle("Gallerie hinzufügen");
            stage.setScene(new Scene(root1));
            stage.show();
            stage.setOnCloseRequest((WindowEvent we) -> {
                controller.enableInput();
                this.controller.reloadTreeItems();
            });
            controller.disableInput("Gallerien werden hinzugefügt...");
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

        if (!this.controller.getRemoteGalleryLocation().exists()) {
            this.newGalleryButton.setDisable(true);
        }
        else {
            this.newGalleryButton.setDisable(false);
        }
    }
}
