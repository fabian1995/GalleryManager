package gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryManager {

    private final File root;

    public static final String GALLERY_CONFIG_FILE_NAME = "gallery.json";
    public static final String COLLECTION_CONFIG_FILE_NAME = "collection.json";
    public static final String THUMBNAIL_FOLDER = ".thumbnails";
    public static final String IMAGE_FILE_REGEX = "[\\w-]+(.jpg|.JPG|.png|.PNG|.jpeg|.JPEG|.bmp|.BMP)$";

    private GalleryNode trunk = null;
    private GalleryNode compareTrunk;

    public GalleryManager(File root) {
        this(root, null);
    }
    
    public GalleryManager(File root, GalleryNode compareTrunk) {
        this.root = root;
        this.trunk = new GalleryNode(this.root, false, "Bilder auf diesem Computer", true);
        this.compareTrunk = compareTrunk;
    }

    public GalleryNode getTrunk() {
        return this.trunk;
    }
    
    public void search() {
        List<String> path = new ArrayList<>();
        this.search(this.root, path, root.getAbsolutePath());
        this.trunk.sortChildren();
    }

    private void search(File directory, List<String> path, String rootName) {
        
        for (File f : directory.listFiles()) {

            if (f.isDirectory()) {
                path.add(f.getName());
                this.search(f, path, rootName);
                path.remove(f.getName());
            }
            else if (f.isFile() && f.getName().equals(GALLERY_CONFIG_FILE_NAME)
                    || f.isFile() && f.getName().equals(COLLECTION_CONFIG_FILE_NAME)) {
                this.insertGallery(f, path, rootName);
            }
        }
    }

    private void insertGallery(File config, List<String> path, String rootName) {

        GalleryNode position = this.trunk;
        GalleryNode comparison = this.compareTrunk;
        
        String pathToGallery = rootName + "/";

        for (int i = 0; i < path.size(); i++) {
            String name = path.get(i);
            pathToGallery += name + "/" + (i+1 == path.size() ? config.getName() : "");
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
                    if (c != null && c.isGallery())
                        isImported = true;
                    if (c != null)
                        comparison = c;
                }
                
                GalleryNode g = new GalleryNode(new File(pathToGallery), isImported);
                position.getChildren().add(g);
                position = g;
            }
        }
    }
}
