/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallery.load.ImageLoaderService;
import gallerydemo.menu.FileMenuController;
import gallerydemo.menu.ManagementMenuController;
import gallerydemo.menu.GalleryMenuController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class GalleryDemoViewController implements Initializable {
    
    public static final String GLOBAL_CONFIG_FILE_NAME = "config.json";

    // the FXML annotation tells the loader to inject this variable before invoking initialize.
    @FXML
    private TreeView<String> locationTreeView;

    @FXML
    private ScrollPane scrollImageContainer;
    
    @FXML
    private FlowPane imagePane;

    @FXML
    private HBox menuBar;
    
    @FXML
    private BorderPane fadeOutPane;
    
    @FXML
    private Text fadeOutText;

    private File localGalleryLocation;
    private File remoteGalleryLocation;
    
    private GalleryNode activeGallery;
    private final GalleryManager galleryManager;
    
    private FileMenuController fileMenuController;
    private ManagementMenuController managementMenuController;
    private GalleryMenuController galleryMenuController;
    
    private ImageLoaderService currentTask = null;
    
    public GalleryDemoViewController() {
        this.readConfigFile();
        this.galleryManager = new GalleryManager(this.localGalleryLocation);
    }

    public void setActiveGallery(GalleryNode g) {
        this.activeGallery = g;
        this.galleryMenuController.actualizeButtons();
    }

    public GalleryNode getActiveGallery() {
        return this.activeGallery;
    }

    public FlowPane getImagePane() {
        return this.imagePane;
    }
    
    public File getLocalGalleryLocation() {
        return this.localGalleryLocation;
    }
    
    public File getRemoteGalleryLocation() {
        return this.remoteGalleryLocation;
    }
    
    public GalleryManager getManager() {
        return this.galleryManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.enableInput();
        reloadTreeItems();
        this.activeGallery = null;
        
        this.scrollImageContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollImageContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollImageContainer.setFitToHeight(true);
        this.scrollImageContainer.setFitToWidth(true);

        this.locationTreeView.getSelectionModel().selectedItemProperty()
                .addListener(new GalleryDemoViewListener(this));
        
        this.locationTreeView.getRoot().setExpanded(true);
        
        this.fileMenuController = new FileMenuController(this);
        this.menuBar.getChildren().add(this.fileMenuController);
        
        this.managementMenuController = new ManagementMenuController(this);
        this.menuBar.getChildren().add(this.managementMenuController);
        
        this.galleryMenuController = new GalleryMenuController(this);
        this.menuBar.getChildren().add(this.galleryMenuController);
    }
    
    public void disableInput(String message) {
        this.fadeOutPane.setVisible(true);
        this.fadeOutText.setText(message);
    }
    
    public void enableInput() {
        this.fadeOutPane.setVisible(false);
    }

    // loads some strings into the tree in the application UI.
    public void reloadTreeItems() {
        this.galleryManager.search();
        locationTreeView.setRoot(this.galleryManager.getTrunk());
    }
    
    public void reloadGalleryImages(GalleryNode galleryNode) {
        
        if (this.currentTask != null) {
            this.currentTask.cancel();
        }
        imagePane.getChildren().clear();
        this.currentTask = new ImageLoaderService(galleryNode, imagePane);
        this.currentTask.start();
    }
    
    public GalleryNode getRoot() {
        return (GalleryNode)locationTreeView.getRoot();
    }

    private void readConfigFile() {

        File configFile = new File(GLOBAL_CONFIG_FILE_NAME);
        
        if (!configFile.exists()) {
            Logger.getLogger("logfile").warning("Config file not found");
            this.localGalleryLocation = new File("galleries");
            this.remoteGalleryLocation = new File("remote");
            return;
        }
        
        String rawJSON = null;
        
        try {
            rawJSON = new String(Files.readAllBytes(configFile.toPath()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONObject rootObject = new JSONObject(rawJSON);

        this.localGalleryLocation = new File(rootObject.getString("localGalleryLocation"));
        this.remoteGalleryLocation = new File(rootObject.getString("remoteGalleryLocation"));
    }
    
}
