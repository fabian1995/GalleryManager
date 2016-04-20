package gallery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TreeItem;

import org.json.JSONObject;

public class GalleryNode extends TreeItem {
    
    public static final String GALLERY_JSON_CONF_NAME = "name";
    public static final String GALLERY_JSON_CONF_ORIGIN = "origin";

    private String name;
    private boolean isImported;
    private File origin = null;

    private File config;
    
    public enum NodeType {
        COLLECTION, GALLERY
    }
    
    private NodeType type;
    
    public GalleryNode(File config) {
        this(config, false, null);
    }
    
    public GalleryNode(File config, boolean isImported) {
        this(config, isImported, null);
    }
    
    public GalleryNode(File config, boolean isImported, String name) {
        this.isImported = isImported;
        this.config = config;
        
        Logger.getLogger("logfile").log(Level.INFO, "[log] Creating gallery node: " + config.getAbsolutePath());

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
                this.name = config.getName();
                this.saveConfigFile();
            }
            
        }
        else {
            this.name = name;
            this.origin = null;
            if (this.config != null && this.config.exists() && this.config.isFile())
                this.saveConfigFile();
        }
        super.setValue((this.isImported ? "[OK] " : "") + this.name);
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
    
    public String getFileName() {
        return this.config.isFile() ? this.config.getParentFile().getName() : this.config.getName();
    }

    public File getLocation() {
        return this.config.getParentFile();
    }

    public File getOrigin() {
        return this.origin;
    }
    
    public void setOrigin(File origin) {
        this.origin = origin;
    }

    public boolean isGallery() {
        return this.type == NodeType.GALLERY;
    }
    
    public File[] listImages() {
        return this.getLocation().listFiles((File dir, String fileName) -> {
            return fileName.toLowerCase().matches(GalleryManager.IMAGE_FILE_REGEX);
        });
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

        this.name = rootObject.getString(GALLERY_JSON_CONF_NAME);
        if (this.type == NodeType.GALLERY)
            this.origin = new File(rootObject.getString(GALLERY_JSON_CONF_ORIGIN));
    }
    
    public void saveConfigFile() {
        JSONObject configObject = new JSONObject();
        configObject.put(GALLERY_JSON_CONF_NAME, this.name);
        
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
