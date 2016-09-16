package gallery;

import gallerydemo.message.MessageController;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

public class GalleryManager {

    private final File root;

    public static final String CACHE_FILE_NAME = ".cache.json";
    public static final String GALLERY_CONFIG_FILE_NAME = ".gallery.json";
    public static final String COLLECTION_CONFIG_FILE_NAME = ".collection.json";
    public static final String THUMBNAIL_FOLDER = ".thumbnails";
    public static final String IMAGE_FILE_REGEX = "^[^\\.][\\w\\s\\.#\\-]+\\.(jpg|JPG|png|PNG|jpeg|JPEG|bmp|BMP)$";
    public static final String VIDEO_FILE_REGEX = "^[^\\.][\\w\\s\\.#\\-]+\\.(mov|MOV|mp4|MP4|avi|AVI)$";
    
    private static final String JSON_CONF_CACHE = "cache";

    private GalleryNode trunk = null;
    private GalleryNode compareTrunk;

    public GalleryManager(File root) {
        this(root, null);
    }
    
    public GalleryManager(File root, GalleryNode compareTrunk) {
        this.root = root;
        this.trunk = new GalleryNode(this.root, false, false, "Bilder auf diesem Computer", true);
        this.compareTrunk = compareTrunk;
    }

    public GalleryNode getTrunk() {
        return this.trunk;
    }
    
    public GalleryNode getCompareTrunk() {
        return this.compareTrunk;
    }
    
    public void search() {
        List<String> path = new ArrayList<>();
        this.search(this.root, path);
        this.trunk.sortChildren();
    }

    private void search(File directory, List<String> path) {
        
        for (File f : directory.listFiles()) {

            if (f.isDirectory()) {
                path.add(f.getName());
                this.search(f, path);
                path.remove(f.getName());
            }
            else if (f.isFile() && f.getName().equals(GALLERY_CONFIG_FILE_NAME)
                    || f.isFile() && f.getName().equals(COLLECTION_CONFIG_FILE_NAME)) {
                this.insertGallery(f.getName(), path, true);
            }
        }
    }
    
    private void searchCache(JSONObject cache, List<String> path) {
        
        for (String k : cache.keySet()) {
            System.out.println("...reading cache: " + k);
            path.add(k);
            searchCache(cache.getJSONObject(k), path);
            path.remove(k);
        }
        if (cache.keySet().isEmpty()) {
            this.insertGallery(GALLERY_CONFIG_FILE_NAME, path, false);
        }
    }
    
    // TODO use this function for creating and importing galleries
    public void addGallery(String path, String name) {
        path = path.replace('\\', '/');
        List<String> pathList = new LinkedList<>(Arrays.asList(path.split("/")));
        System.out.println("" + Arrays.toString(pathList.toArray()));
        GalleryNode newNode = this.insertGallery(GALLERY_CONFIG_FILE_NAME, pathList, true);
        
        if (newNode != null)
            newNode.setName(name);
        
        if (this.compareTrunk != null) {
            this.writeCacheFile();
        }
    }

    private GalleryNode insertGallery(String configName, List<String> path, boolean createImageList) {

        GalleryNode position = this.trunk;
        GalleryNode comparison = this.compareTrunk;
        
        GalleryNode lastAdded = null;
        
        String pathToGallery = "";

        for (int i = 0; i < path.size(); i++) {
            String name = path.get(i);
            pathToGallery += name + "/" + (i+1 == path.size() ? configName : "");
            GalleryNode child = position.getChildNode(name);
            if (child != null) {
                position = child;
                if (comparison != null) {
                    comparison = comparison.getChildNode(name);
                }
            }
            else {
                boolean isImported = false;
                
                if (comparison != null) {
                    GalleryNode c = comparison.getChildNode(name);
                    if (c != null && c.isGallery()) {
                        isImported = true;
                        c.setOriginConfirmed();
                    }
                    if (c != null)
                        comparison = c;
                }
                
                lastAdded = new GalleryNode(new File(this.root.getAbsolutePath() + "/" + pathToGallery), isImported, createImageList);
                position.getChildren().add(lastAdded);
                position = lastAdded;
            }
        }
        return lastAdded;
    }
    
    public void findUnconfirmedGalleries(TreeView tree, VBox messageList) {
        this.findUnconfirmedGalleries(tree, messageList, this.trunk);
    }
    
    private void findUnconfirmedGalleries(TreeView tree, VBox messageList, GalleryNode g) {
        if (g.isGallery() && g.hasOrigin() && !g.isOriginConfirmed()) {
            MessageController m = new MessageController(messageList, "Galerie vom Server gelöscht", g.getName(), tree, g);
            messageList.getChildren().add(m);
        }
        for (Object n : g.getChildren()) {
            this.findUnconfirmedGalleries(tree, messageList, (GalleryNode)n);
        }
    }
    
    public void readCacheFile() {
        String rawJSON;
        JSONObject cache;

        Logger.getLogger("logfile").log(Level.INFO, "[log] starting to read cache file");
        
        try {
            rawJSON = new String(Files.readAllBytes(new File(this.root + "/" + CACHE_FILE_NAME).toPath()));
            JSONObject rootObject = new JSONObject(rawJSON);
            cache = rootObject.getJSONObject(JSON_CONF_CACHE);
            this.searchCache(cache, new LinkedList<>());
            System.out.println("Sorting...");
            this.trunk.sortChildren();
        } catch (IOException e) {
            Logger.getLogger("logfile").log(Level.SEVERE, "Error reading cache: {0} - {1}", new Object[]{e.getMessage()});
            Logger.getLogger("logfile").log(Level.INFO, "[log] reading cache failed!");
            this.search();
            this.writeCacheFile();
        }
    }
    
    public void writeCacheFile() {
        JSONObject configObject = new JSONObject();
        
        configObject.put(JSON_CONF_CACHE, this.trunk.toCache());

        try (FileWriter configFile = new FileWriter(this.root + "/" + CACHE_FILE_NAME)) {
            configFile.write(configObject.toString());
        } catch (IOException e) {
            Logger.getLogger("logfile").log(Level.SEVERE, null, e);
        }
    }
}
