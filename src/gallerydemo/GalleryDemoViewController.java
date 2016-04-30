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
import gallerydemo.fullView.FullSizeViewController;
import gallerydemo.menu.FileMenuController;
import gallerydemo.menu.ManagementMenuController;
import gallerydemo.menu.GalleryMenuController;
import gallerydemo.task.TaskController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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

    @FXML private ScrollPane scrollTreeContainer;
    
    @FXML private StackPane centerPanel;
    
    @FXML private TreeView<String> locationTreeView;

    @FXML private ScrollPane scrollImageContainer;
    
    @FXML private FlowPane imagePane;

    @FXML private HBox menuBar;
    
    @FXML private BorderPane fadeOutPane;
    
    @FXML private Text fadeOutText;
    
    @FXML private VBox taskList;

    private File localGalleryLocation;
    private File remoteGalleryLocation;
    
    private GalleryNode activeGallery;
    private final GalleryManager galleryManager;
    
    private FileMenuController fileMenuController;
    private ManagementMenuController managementMenuController;
    private GalleryMenuController galleryMenuController;
    
    private EventHandler<MouseEvent> exitFullScreenHandler;
    
    private ImageLoaderService currentTask = null;
    
    private FullSizeViewController fullSizeImageContainer;
    
    enum ViewState {
        BROWSE, IMAGE
    }
    
    private ViewState currentViewState;
    
    public GalleryDemoViewController() {
        this.readConfigFile();
        this.galleryManager = new GalleryManager(this.localGalleryLocation);
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
        
        this.scrollTreeContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollTreeContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollTreeContainer.setFitToHeight(true);
        this.scrollTreeContainer.setFitToWidth(true);

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
        
        this.fullSizeImageContainer = new FullSizeViewController(this);
        this.centerPanel.getChildren().add(this.fullSizeImageContainer);
        
        this.setViewState(ViewState.BROWSE);
    }

    public void setActiveGallery(GalleryNode g) {
        
        this.setViewState(ViewState.BROWSE);
        
        this.activeGallery = g;
        this.galleryMenuController.actualizeButtons();
        this.managementMenuController.actualizeButtons();
        
        if (this.activeGallery != null && this.activeGallery.isGallery()) {
            this.reloadGalleryImages(this.activeGallery, false);
        } else {
            this.imagePane.getChildren().clear();
        }
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
    
    public GalleryNode getRoot() {
        return (GalleryNode)locationTreeView.getRoot();
    }
    
    public GalleryManager getManager() {
        return this.galleryManager;
    }
    
    public TaskController registerNewTask(String titleText, int max) {
        TaskController task = new TaskController(this.taskList, titleText, max);
        this.taskList.getChildren().add(task);
        return task;
    }
    
    public void enableFullSizeImageView(GalleryImage galleryImage) {
        this.setViewState(ViewState.IMAGE);
        this.locationTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, this.exitFullScreenHandler);
        this.fullSizeImageContainer.imageSelected(this.activeGallery.getImageList(false), galleryImage);
    }
    
    public void disableFullSizeImageView() {
        this.setViewState(ViewState.BROWSE);
        this.locationTreeView.removeEventHandler(MouseEvent.MOUSE_CLICKED, this.exitFullScreenHandler);
    }
    
    private void setViewState(ViewState state) {
        this.currentViewState = state;
        switch(state) {
            case IMAGE:
                this.scrollImageContainer.setVisible(false);
                this.fullSizeImageContainer.setVisible(true);
                break;
            default:
                this.scrollImageContainer.setVisible(true);
                this.fullSizeImageContainer.setVisible(false);
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
    
    public void reloadGalleryImages(GalleryNode galleryNode, boolean reload) {
        
        if (this.currentTask != null) {
            this.currentTask.cancel();
        }
        this.imagePane.getChildren().clear();
        this.currentTask = new ImageLoaderService(this, galleryNode, imagePane, reload);
        this.currentTask.start();
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
