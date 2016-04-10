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
    private String fileName;
    private boolean isImported;
    private File origin = null;

    //private final Map<String, GalleryNode> children = new TreeMap<>();

    private File config;

    /*public GalleryNode(String name) {
        super(name);
        this.name = this.fileName = name;
    }*/
    
    public GalleryNode(File config) {
        //this.name = this.fileName = name;
        this(config, false);
    }
    
    public GalleryNode(File config, boolean isImported) {
        //this.name = this.fileName = name;
        this.isImported = isImported;
        this.setConfigFile(config);
    }
    
    public boolean contains(String nodeName) {
        for (Object s : this.getChildren().toArray()) {
            if (((GalleryNode)s).getFileName().equals(nodeName))
                return true;
        }
        return false;
    }
    
    public GalleryNode getChildNode(String nodeName) {
        //System.out.println("Searching for: " + nodeName);
        for (Object s : this.getChildren().toArray()) {
            //System.out.println("...found: " + ((GalleryNode)s).getFileName());
            if (((GalleryNode)s).getFileName().equals(nodeName)) {
                //System.out.println("MATCHES");
                return (GalleryNode)s;
            }
        }
        return null;
    }
    
    public void sortChildren() {
        this.getChildren().sort(Comparator.comparing((GalleryNode g) -> {
            return g.isGallery();
        }).thenComparing((GalleryNode g1, GalleryNode g2) -> g1.getName().compareTo(g2.getName())));
        for (Object g : this.getChildren()) {
            ((GalleryNode)g).sortChildren();
        }
    }

    @Override
    public String toString() {
        return name;
    }
    
    public boolean isImported() {
        return this.isImported;
    }

    /*public void addChild(GalleryNode child) {
        this.children.put(child.name, child);
    }*/

    /*public Map<String, GalleryNode> getChildren() {
        return this.children;
    }*/

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
        return this.config.isFile();
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

    private void setConfigFile(File config) {
        this.config = config;

        Logger.getLogger("logfile").info("[log] Reading config: " + config.getAbsolutePath());
        
        if (config.exists() && config.isFile() && config.getName().endsWith(".json")) {
            this.readConfigFile();
        }
        else {
            this.name = config.getName();
        }
        super.setValue((this.isImported ? "[OK] " : "") + this.name);
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
        this.origin = new File(rootObject.getString(GALLERY_JSON_CONF_ORIGIN));
    }
    
    public void saveConfig() {
        JSONObject config = new JSONObject();
        config.put(GALLERY_JSON_CONF_NAME, this.name);
        config.put(GALLERY_JSON_CONF_ORIGIN, this.origin.getAbsolutePath());
        
        try (FileWriter configFile = new FileWriter(this.config)) {
            configFile.write(config.toString());
        } catch (IOException ex) {
            Logger.getLogger(GalleryNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
