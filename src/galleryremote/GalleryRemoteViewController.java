/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package galleryremote;

import gallery.GalleryManager;
import gallery.GalleryNode;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import org.apache.commons.io.FileUtils;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class GalleryRemoteViewController implements Initializable {
    
    private final File remoteLocation;
    private final GalleryNode baseTree;
    
    @FXML
    private TreeView<String> locationTreeView;
    
    @FXML
    private Button buttonImport;

    public GalleryRemoteViewController(File remote, GalleryNode base) {
        this.remoteLocation = remote;
        this.baseTree = base;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Logger.getLogger("logfile").info("[init] GalleryRemoteView");
        
        GalleryManager g = new GalleryManager(this.remoteLocation, this.baseTree);
        g.search();

        locationTreeView.setRoot(g.getTrunk());
        locationTreeView.setShowRoot(false);
        locationTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //locationTreeView.
        
        this.buttonImport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Object s : locationTreeView.getSelectionModel().getSelectedItems().toArray()) {
                    GalleryNode gallery = ((GalleryNode)s);
                    if (!gallery.isImported() && gallery.isGallery()) {
                        File origin = gallery.getLocation();
                        File target = new File(baseTree.getConfigFile().getAbsolutePath() + "/" + origin.toString().replaceAll(remoteLocation.getAbsolutePath(), ""));
                        Logger.getLogger("logfile").info("[import] " + gallery.getFileName() + ": " + origin + " -> " + target );
                        try {
                            FileUtils.copyDirectory(origin, target, new FileFilter() {
                                @Override
                                public boolean accept(File pathname) {
                                    return pathname.getName().equals(GalleryManager.GALLERY_CONFIG_FILE_NAME)
                                            || pathname.getName().matches(GalleryManager.IMAGE_FILE_REGEX);
                                }
                            });
                        } catch (IOException ex) {
                            Logger.getLogger(GalleryRemoteViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        GalleryNode importedGallery = new GalleryNode(new File(target + "/" + GalleryManager.GALLERY_CONFIG_FILE_NAME));
                        importedGallery.setOrigin(gallery.getConfigFile());
                        importedGallery.saveConfig();
                    } else {
                        Logger.getLogger("logfile").info("Did NOT import " + gallery.getFileName() );
                    }
                }
            }
        });
    }    
    
}
