/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package galleryremote;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallery.load.CopyGalleryService;
import gallery.load.ServiceControllerInterface;
import gallerydemo.task.TaskController;
import java.io.File;
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
    
    //private final File remoteLocation;
    //private final GalleryNode baseTree;
    
    private final GalleryManager remoteManager;
    
    @FXML
    private TreeView<String> locationTreeView;
    
    @FXML
    private Button buttonImport;
    
    @FXML
    private VBox taskList;

    public GalleryRemoteViewController(GalleryManager remoteManager) {
        //this.remoteLocation = remote;
        //this.baseTree = base;
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
        
        //GalleryManager g = new GalleryManager(this.remoteLocation, this.baseTree);
        //g.search();
        //g.readCacheFile();
        //g.writeCacheFile();

        locationTreeView.setRoot(this.remoteManager.getTrunk());
        locationTreeView.setShowRoot(false);
        locationTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        this.buttonImport.setOnAction((ActionEvent event) -> {
            for (Object s : locationTreeView.getSelectionModel().getSelectedItems().toArray()) {
                final GalleryNode gallery = ((GalleryNode)s);
                if (!gallery.isImported() && gallery.isGallery()) {
                    File origin = gallery.getLocation();
                    
                    File target = new File(
                            remoteManager.getCompareTrunk().getConfigFile().getAbsolutePath().replace('\\', '/') +
                            "/" +
                            origin.toString().replace('\\', '/').replaceAll(
                                    remoteManager.getTrunk().getLocation().getAbsolutePath().replace('\\', '/'), ""
                            )
                    );
                    Logger.getLogger("logfile").log(Level.INFO, "[import] {0}: {1} -> {2}", new Object[]{gallery.getFileName(), origin, target});
                    gallery.setImportedTrue(false);
                    
                    CopyGalleryService task = new CopyGalleryService(this, origin, target,
                            "Importing gallery '" + gallery.getName() + "'",
                            () -> {
                                GalleryNode importedGallery = new GalleryNode(new File(target + "/" + GalleryManager.GALLERY_CONFIG_FILE_NAME), false, true, gallery.getName(), false);
                                importedGallery.setOrigin(gallery.getConfigFile());
                                importedGallery.saveConfigFile();
                                gallery.setImportedTrue(true);
                            }
                    );
                    task.start();
                } else {
                    Logger.getLogger("logfile").log(Level.INFO, "[info] Did NOT import {0}", gallery.getFileName());
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
