package gallery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.json.JSONObject;

public final class GalleryNode extends TreeItem {
    
    public static final String GALLERY_JSON_CONF_NAME = "name";
    public static final String GALLERY_JSON_CONF_ORIGIN = "origin";

    private String name;
    private boolean isImported;
    private File origin = null;

    private File config;
    
    private final List<GalleryImage> imageList = new LinkedList<>();
    
    public enum NodeType {
        COLLECTION, GALLERY, TRUNK
    }
    
    private NodeType type;
    
    public GalleryNode(File config) {
        this(config, false, null, false);
    }
    
    public GalleryNode(File config, boolean isImported) {
        this(config, isImported, null, false);
    }
    
    public GalleryNode(File config, boolean isImported, String name, boolean isTrunk) {
        this.isImported = isImported;
        this.config = config;
        
        Logger.getLogger("logfile").log(Level.INFO, "[log] Creating gallery node: {0}", config.getAbsolutePath());

        if (isTrunk)
            this.type = NodeType.TRUNK;
        
        if (config.exists() && config.isFile() && config.getName().endsWith(".json")) {
            if (config.getName().equals(GalleryManager.COLLECTION_CONFIG_FILE_NAME))
                this.type = NodeType.COLLECTION;
            else if (config.getName().equals(GalleryManager.GALLERY_CONFIG_FILE_NAME))
                this.type = NodeType.GALLERY;
            this.readConfigFile();
        }
        else if(name == null && config.exists()) {
            this.type = NodeType.COLLECTION;
            this.config = new File(config.getAbsolutePath() + "/" + GalleryManager.COLLECTION_CONFIG_FILE_NAME);
            if (this.config.exists()) {
                this.readConfigFile();
            }
            else {
                this.setName(config.getName());
                this.saveConfigFile();
            }
            
        }
        else {
            this.setName(name);
            this.origin = null;
            if (this.config != null && this.config.exists() && this.config.isFile())
                this.saveConfigFile();
        }
        
        ImageView icon = new ImageView();
        if (this.type == NodeType.TRUNK)
            icon.setImage(new Image(getClass().getResourceAsStream("icon_trunk.png")));
        else if (this.type == NodeType.COLLECTION)
            icon.setImage(new Image(getClass().getResourceAsStream("icon_folder.png")));
        else if (this.isImported)
            icon.setImage(new Image(getClass().getResourceAsStream("icon_imported.png")));
        else if (this.hasOrigin())
            icon.setImage(new Image(getClass().getResourceAsStream("icon_cloud.png")));
        else
            icon.setImage(new Image(getClass().getResourceAsStream("icon_gallery.png")));
        super.setGraphic(icon);
        
        this.createImageList();
    }
    
    public boolean contains(String nodeName) {
        for (Object s : this.getChildren().toArray()) {
            if (((GalleryNode)s).getFileName().equals(nodeName))
                return true;
        }
        return false;
    }
    
    public GalleryNode getChildNode(String nodeName) {
        for (Object s : this.getChildren().toArray()) {
            if (((GalleryNode)s).getFileName().equals(nodeName)) {
                return (GalleryNode)s;
            }
        }
        return null;
    }
    
    public void sortChildren() {
        this.getChildren().sort(Comparator.comparing((GalleryNode g) -> {
            return g.isGallery();
        }).thenComparing((GalleryNode g1, GalleryNode g2) -> g1.getName().compareTo(g2.getName())));
        this.getChildren().stream().forEach((g) -> {
            ((GalleryNode)g).sortChildren();
        });
    }

    @Override
    public String toString() {
        return name;
    }
    
    public boolean isImported() {
        return this.isImported;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
        super.setValue(name);
    }
    
    public String getFileName() {
        return this.config.isFile() ? this.config.getParentFile().getName() : this.config.getName();
    }

    public File getLocation() {
        return this.config.isFile() ? this.config.getParentFile() : this.config;
    }

    public File getOrigin() {
        return this.origin;
    }
    
    public boolean hasOrigin() {
        return this.origin != null && !this.origin.getPath().equals("");
    }
    
    public void setOrigin(File origin) {
        this.origin = origin;
        if (this.origin.exists()) {
            ImageView icon = new ImageView();
            icon.setImage(new Image(getClass().getResourceAsStream("icon_cloud.png")));
            super.setGraphic(icon);
        }
    }

    public boolean isGallery() {
        return this.type == NodeType.GALLERY;
    }
    
    public boolean isTrunk() {
        return this.type == NodeType.TRUNK;
    }
    
    @Deprecated
    public File[] listImages() {
        return this.getLocation().listFiles((File dir, String fileName) -> {
            return fileName.toLowerCase().matches(GalleryManager.IMAGE_FILE_REGEX);
        });
    }
    
    public void createImageList() {
        this.imageList.clear();
        
        File[] fileList = this.getLocation().listFiles((File dir, String fileName) -> {
            return fileName.toLowerCase().matches(GalleryManager.IMAGE_FILE_REGEX);
        });
        
        if (fileList != null) {
            for (File f : fileList) {
                try {
                    this.imageList.add(new GalleryImage(f));
                } catch (IOException ex) {
                    Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
                }
            }
        }
        
        Collections.sort(this.imageList);
    }
    
    public synchronized void addImage(File image) throws IOException {
        this.imageList.add(new GalleryImage(image));
    }
    
    public List<GalleryImage> getImageList(boolean reload) {
        if (reload)
            this.createImageList();
        return this.imageList;
    }

    public File getConfigFile() {
        return this.config;
    }
    
    public void createThumbnailFolder() {
        File folder = this.getLocation();
        File thumbnailFolder = new File(folder.getPath() + "/" + GalleryManager.THUMBNAIL_FOLDER + "/");

        if (!thumbnailFolder.isDirectory()) {
            thumbnailFolder.mkdir();
        }
    }

    private void readConfigFile() {

        String rawJSON = null;

        try {
            rawJSON = new String(Files.readAllBytes(this.config.toPath()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONObject rootObject = new JSONObject(rawJSON);

        this.setName(rootObject.getString(GALLERY_JSON_CONF_NAME));
        if (this.type == NodeType.GALLERY)
            this.origin = new File(rootObject.getString(GALLERY_JSON_CONF_ORIGIN));
    }
    
    public void saveConfigFile() {
        JSONObject configObject = new JSONObject();
        configObject.put(GALLERY_JSON_CONF_NAME, this.name);
        
        // TODO is this the best way to assign the node type?
        if (this.type == null) {
            if (this.config.getName().equals(GalleryManager.GALLERY_CONFIG_FILE_NAME))
                this.type = NodeType.GALLERY;
            else
                this.type = NodeType.COLLECTION;
        }
        
        if (this.type == NodeType.GALLERY) {
            if (this.origin == null)
                configObject.put(GALLERY_JSON_CONF_ORIGIN, "");
            else
                configObject.put(GALLERY_JSON_CONF_ORIGIN, this.origin.getAbsolutePath());
        }
        
        try (FileWriter configFile = new FileWriter(this.config)) {
            configFile.write(configObject.toString());
        } catch (IOException ex) {
            Logger.getLogger(GalleryNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
