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
import gallery.load.SearchGalleryService;
import gallery.load.ServiceControllerInterface;
import gallerydemo.fullView.FullSizeViewController;
import gallerydemo.menu.FileMenuController;
import gallerydemo.menu.ManagementMenuController;
import gallerydemo.menu.GalleryMenuController;
import gallerydemo.message.CallbackMessageController;
import gallerydemo.settings.GallerySettings;
import gallerydemo.task.TaskController;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class GalleryDemoViewController implements Initializable, ServiceControllerInterface {

    @FXML private ScrollPane scrollTreeContainer;
    
    @FXML private StackPane centerPanel;
    
    @FXML private TreeView<String> locationTreeView;

    @FXML private ScrollPane scrollImageContainer;
    
    @FXML private FlowPane imagePane;

    @FXML private HBox menuBar;
    
    @FXML private BorderPane fadeOutPane;
    
    @FXML private Text fadeOutText;
    
    @FXML private VBox taskList;
    
    @FXML private VBox messageList;
    
    @FXML private Text infoLastChanged;
    
    @FXML private ImageView infoStatusIcon;
    
    @FXML private Text infoStatusText;
    
    private GalleryNode activeGallery;
    private final GalleryManager galleryManager;
    private GalleryManager remoteManager = null;
    
    public final GallerySettings settings;
    
    private FileMenuController fileMenuController;
    private ManagementMenuController managementMenuController;
    private GalleryMenuController galleryMenuController;
    
    private EventHandler<MouseEvent> exitFullScreenHandler;
    
    private ImageLoaderService currentTask = null;
    
    private FullSizeViewController fullSizeImageContainer;
    
    private boolean searching = false;
    
    enum ViewState {
        BROWSE, IMAGE
    }
    
    private ViewState currentViewState;
    
    public GalleryDemoViewController(GallerySettings settings) {
        this.settings = settings;
        this.galleryManager = new GalleryManager(this.settings.getLocalGalleryLocation());
        this.galleryManager.search();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.enableInput();
        this.locationTreeView.setRoot(this.galleryManager.getTrunk());
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
        
        this.remoteManager = null;
        
        if (this.settings.getRemoteGalleryLocation() != null
                && this.settings.getRemoteGalleryLocation().exists()) {
            this.startSearch(null);
        } else {
            
            CallbackMessageController msgControl = new CallbackMessageController(
                            this.messageList,
                            "Nicht verbunden",
                            "Verbindung zum Server konnte nicht hergestellt werden",
                            "Aktualisieren",
                            false, null
                    );
            
            // Add messagt that the remote location could not be found
            this.messageList.getChildren().add(msgControl);
            
            msgControl.setCallback(() -> {
                if (settings.getRemoteGalleryLocation() != null
                    && settings.getRemoteGalleryLocation().exists()) {
                    msgControl.setButtonDisable(true);
                    startSearch(msgControl);
                }
            });
        }
    }
    
    public void startSearch(final CallbackMessageController initiator) {
        // Disable "Export gallery" buttons
        this.searching = true;
        this.galleryMenuController.actualizeButtons();

        // Search server galleries
        SearchGalleryService task = new SearchGalleryService(this, this.settings.getRemoteGalleryLocation(), this.galleryManager.getTrunk(), () -> {
            this.searching = false;
            this.galleryManager.setCompareTrunk(this.remoteManager.getTrunk());
            this.fileMenuController.actualizeButtons();
            this.galleryMenuController.actualizeButtons();
            if (initiator != null)
                messageList.getChildren().remove(initiator);
        });
            
        task.start();
    }
    
    public boolean isSearching() {
        return this.searching;
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
        
        this.refreshGalleryInfo();
    }
    
    public GalleryNode getActiveGallery() {
        return this.activeGallery;
    }
    
    public void refreshGalleryInfo() {
        if (this.activeGallery.isGallery()) {
            if (this.activeGallery.getData().lastChanged.getTime() > 0) {
                SimpleDateFormat df = new SimpleDateFormat("dd. MM. yyyy, HH:mm");
                this.infoLastChanged.setText(df.format(this.activeGallery.getData().lastChanged));
            }
            else
                this.infoLastChanged.setText("Unbekannt");
            
            switch(this.activeGallery.getStatus()) {
                case OFFLINE: this.infoStatusText.setText("Nur am Computer"); break;
                case UPTODATE: this.infoStatusText.setText("Gesichert, aktuell"); break;
                case LOCALNEWER: this.infoStatusText.setText("Nicht gesicherte Änderungen"); break;
                case SERVERNEWER: this.infoStatusText.setText("Server aktueller"); break;
            }
        }
        else {
            this.infoLastChanged.setText("");
            this.infoStatusText.setText("");
        }
    }
    
    public GalleryManager getLocalManager() {
        return this.galleryManager;
    }
    
    public GalleryManager getRemoteManager() {
        return this.remoteManager;
    }
    
    public void setRemoteManager(GalleryManager r) {
        this.remoteManager = r;
    }
    
    @Override
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
    
    public void reloadGalleryImages(GalleryNode galleryNode, boolean reload) {
        
        if (this.currentTask != null) {
            this.currentTask.cancel();
        }
        this.imagePane.getChildren().clear();
        this.currentTask = new ImageLoaderService(this, galleryNode, imagePane, reload);
        this.currentTask.start();
    }

}
