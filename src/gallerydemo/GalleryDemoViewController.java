/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallery.load.ImageLoader;
import gallery.load.ImageLoaderService;
import gallerydemo.imageView.ImageViewContainerController;
import gallerydemo.menu.GalleryMenuController;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
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
    private GalleryMenuController menuBarController;
    
    private ImageLoaderService currentTask = null;
    
    public GalleryDemoViewController() {
        this.readConfigFile();
        this.galleryManager = new GalleryManager(this.localGalleryLocation);
    }

    public void setActiveGallery(GalleryNode g) {
        this.activeGallery = g;
        this.menuBarController.actualizeButtons();
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

    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.enableInput();
        reloadTreeItems();
        this.activeGallery = null;
        
        this.scrollImageContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollImageContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollImageContainer.setFitToHeight(true);
        this.scrollImageContainer.setFitToWidth(true);
        
        this.locationTreeView.setShowRoot(false);

        this.locationTreeView.getSelectionModel().selectedItemProperty()
                .addListener(new GalleryDemoViewListener(this));
        
        this.menuBarController = new GalleryMenuController(this);
        this.menuBar.getChildren().add(this.menuBarController);
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
        /*galleryNode.createThumbnailFolder();
        File[] files = galleryNode.listImages();

        for (int i = 0; i < files.length; i++) {
            File image = files[i];
            //System.out.println("[load] " + image.getPath());
            ImageView iv = this.loadOrCreateThumbnail(image);
            this.imagePane.getChildren().add(new ImageViewContainerController(iv.getImage(), 1200, 800));
        }*/
    }
    
    public GalleryNode getRoot() {
        return (GalleryNode)locationTreeView.getRoot();
    }

    public void buildCollectionMenu() {
        menuBar.getChildren().removeIf((Node t) -> {
            return !t.getStyleClass().contains("alwaysActive");
        });

        /*try {
            menuBar.getChildren().add(getTemplate("menu/CollectionMenu.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(GalleryDemoViewController.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    public void buildGalleryMenu() {

        menuBar.getChildren().removeIf((Node t) -> {
            return !t.getStyleClass().contains("alwaysActive");
        });
        
        /*try {
            menuBar.getChildren().add(new GalleryMenuController(this));
            menuBar.getChildren().add(getTemplate("menu/ViewMenu.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(GalleryDemoViewController.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    /*public ImageView loadOrCreateThumbnail(File imagePath) {

        File thumbnailPath = new File(imagePath.getParent() + "/" + GalleryManager.THUMBNAIL_FOLDER + "/" + imagePath.getName());

        if (thumbnailPath.exists() && thumbnailPath.isFile()) {
            return new ImageView(new Image("file:" + thumbnailPath.getPath()));
        }

        Image image = new Image("file:" + imagePath.getPath(), -1, 100, true, false, false);

        ImageView imageView = new ImageView(image);
        //imageView.setFitHeight(100);
        //imageView.setPreserveRatio(true);

        BufferedImage bImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
        try {
            ImageIO.write(bImage, "png", thumbnailPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return imageView;
    }*/

    private BorderPane getTemplate(String _template) throws IOException {
        BorderPane templatePage;
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(this.getClass().getResource(_template));
        templatePage = (BorderPane) loader.load(this.getClass().getResourceAsStream(_template));
        return templatePage;
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
