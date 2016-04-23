/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo;

import gallery.GalleryImage;
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
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private BorderPane fullScreenImageContainer;
    
    @FXML
    private VBox fullScreenPane;
    
    @FXML
    private ImageView fullScreenImage;
    
    @FXML
    private FlowPane imagePane;

    @FXML
    private HBox menuBar;
    
    @FXML
    private BorderPane fadeOutPane;
    
    @FXML
    private Text fadeOutText;
    
    @FXML
    private Button fullScreenCloseButton;
    
    @FXML
    private Button fullScreenPrevButton;
    
    @FXML
    private Button fullScreenNextButton;

    private File localGalleryLocation;
    private File remoteGalleryLocation;
    
    private GalleryNode activeGallery;
    private final GalleryManager galleryManager;
    
    private FileMenuController fileMenuController;
    private ManagementMenuController managementMenuController;
    private GalleryMenuController galleryMenuController;
    
    private EventHandler<MouseEvent> exitFullScreenHandler;
    
    private ImageLoaderService currentTask = null;
    
    enum ViewState {
        BROWSE, IMAGE
    }
    
    private ViewState currentViewState;
    
    public GalleryDemoViewController() {
        this.readConfigFile();
        this.galleryManager = new GalleryManager(this.localGalleryLocation);
    }

    public void setActiveGallery(GalleryNode g) {
        
        this.setViewState(ViewState.BROWSE);
        
        this.activeGallery = g;
        this.galleryMenuController.actualizeButtons();
        this.managementMenuController.actualizeButtons();
        
        if (this.activeGallery != null && this.activeGallery.isGallery()) {
            this.reloadGalleryImages(this.activeGallery);
        } else {
            this.imagePane.getChildren().clear();
        }
    }
    
    public void enableFullImageView(GalleryImage g) {
        this.setViewState(ViewState.IMAGE);
        this.fullScreenImage.setImage(new Image("file:" + g.file));
        this.locationTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, this.exitFullScreenHandler);
    }
    
    public void disableFullImageView() {
        this.setViewState(ViewState.BROWSE);
        this.locationTreeView.removeEventHandler(MouseEvent.MOUSE_CLICKED, this.exitFullScreenHandler);
    }
    
    public GalleryNode getActiveGallery() {
        return this.activeGallery;
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
        refreshTreeItems();
        this.activeGallery = null;
        
        this.scrollImageContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollImageContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollImageContainer.setFitToHeight(true);
        this.scrollImageContainer.setFitToWidth(true);

        this.locationTreeView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) -> {
                    this.setActiveGallery((GalleryNode) newValue);
                });
        
        this.exitFullScreenHandler = (MouseEvent) -> {this.setViewState(ViewState.BROWSE);};
        
        this.locationTreeView.getRoot().setExpanded(true);
        
        this.fileMenuController = new FileMenuController(this);
        this.menuBar.getChildren().add(this.fileMenuController);
        
        this.managementMenuController = new ManagementMenuController(this);
        this.menuBar.getChildren().add(this.managementMenuController);
        
        this.galleryMenuController = new GalleryMenuController(this);
        this.menuBar.getChildren().add(this.galleryMenuController);
        
        this.setViewState(ViewState.BROWSE);
        
        this.fullScreenImage.fitWidthProperty().bind(this.fullScreenPane.widthProperty());
        this.fullScreenImage.fitHeightProperty().bind(this.fullScreenPane.heightProperty());
        this.fullScreenImage.setPreserveRatio(true);
        
        this.fullScreenCloseButton.setOnAction((ActionEvent) -> {
            this.disableFullImageView();
        });
        
        this.fullScreenNextButton.setDisable(true);
        this.fullScreenPrevButton.setDisable(true);
    }
    
    private void setViewState(ViewState state) {
        this.currentViewState = state;
        switch(state) {
            case IMAGE:
                this.scrollImageContainer.setVisible(false);
                this.fullScreenImageContainer.setVisible(true);
                break;
            default:
                this.scrollImageContainer.setVisible(true);
                this.fullScreenImageContainer.setVisible(false);
                break;
        }
    }
    
    public void disableInput(String message) {
        this.fadeOutPane.setVisible(true);
        this.fadeOutText.setText(message);
    }
    
    public void enableInput() {
        this.fadeOutPane.setVisible(false);
    }

    public void refreshTreeItems() {
        this.galleryManager.search();
        locationTreeView.setRoot(this.galleryManager.getTrunk());
    }
    
    public void reloadGalleryImages(GalleryNode galleryNode) {
        
        if (this.currentTask != null) {
            this.currentTask.cancel();
        }
        this.imagePane.getChildren().clear();
        this.currentTask = new ImageLoaderService(this, galleryNode, imagePane);
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
