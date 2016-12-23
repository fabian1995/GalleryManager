/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package galleryremote;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallery.load.DuplicateGalleryService;
import gallery.load.ServiceControllerInterface;
import gallerydemo.task.TaskController;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class GalleryRemoteViewController implements Initializable, ServiceControllerInterface {
    
    private final GalleryManager localManager;
    private final GalleryManager remoteManager;
    
    @FXML
    private TreeView<String> locationTreeView;
    
    @FXML
    private Button buttonImport;
    
    @FXML
    private VBox taskList;

    public GalleryRemoteViewController(GalleryManager localManager, GalleryManager remoteManager) {
        //this.remoteLocation = remote;
        //this.baseTree = base;
        this.localManager = localManager;
        this.remoteManager = remoteManager;
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Logger.getLogger("logfile").info("[init] GalleryRemoteView");

        locationTreeView.setRoot(this.remoteManager.getTrunk());
        locationTreeView.setShowRoot(false);
        locationTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        this.buttonImport.setOnAction((ActionEvent event) -> {
            for (Object s : locationTreeView.getSelectionModel().getSelectedItems().toArray()) {
                final GalleryNode gallery = ((GalleryNode)s);
                if (gallery.getSettings().compareNode == null && !gallery.getSettings().processing && gallery.isGallery()) {
                    DuplicateGalleryService task = new DuplicateGalleryService(this, gallery, this.localManager.getTrunk(), "Importing gallery '" + gallery.getData().name + "'", null);
                    task.start();
                } else {
                    Logger.getLogger("logfile").log(Level.INFO, "[info] Did NOT import {0}", gallery.getLocation().getName());
                }
            }
        });
    }

    @Override
    public TaskController registerNewTask(String titleText, int max) {
        TaskController task = new TaskController(this.taskList, titleText, max);
        this.taskList.getChildren().add(task);
        return task;
    }
    
}
