package gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GalleryManager {

    private File root;

    public static final String CACHE_FILE_NAME = ".cache.json";
    public static final String GALLERY_CONFIG_FILE_NAME = ".gallery.json";
    public static final String COLLECTION_CONFIG_FILE_NAME = ".collection.json";
    public static final String THUMBNAIL_FOLDER = ".thumbnails";
    private static final String IMAGE_FILE_REGEX = "^[^\\.].*?\\.(jpg|JPG|png|PNG|jpeg|JPEG|bmp|BMP)$";
    private static final String VIDEO_FILE_REGEX = "^[^\\.].*?\\.(mov|MOV|mp4|MP4|avi|AVI)$";
    public static final Pattern IMAGE_FILE_PATTERN = Pattern.compile(IMAGE_FILE_REGEX, Pattern.DOTALL);
    public static final Pattern VIDEO_FILE_PATTERN = Pattern.compile(VIDEO_FILE_REGEX, Pattern.DOTALL);

    private GalleryNode trunk = null;
    private GalleryNode compareTrunk;

    public GalleryManager(File root) {
        this(root, null);
    }

    public GalleryManager(File root, GalleryNode compareTrunk) {
        this.root = root;
        this.trunk = new GalleryNode(this.root, new GalleryNodeSettings(GalleryNodeSettings.GalleryType.TRUNK));
        this.compareTrunk = compareTrunk;
    }

    public GalleryNode getTrunk() {
        return this.trunk;
    }

    public GalleryNode getCompareTrunk() {
        return this.compareTrunk;
    }

    public void setCompareTrunk(GalleryNode compareTrunk) {
        this.compareTrunk = compareTrunk;
    }

    public void search() {
        List<String> path = new ArrayList<>();
        this.search(this.root, this.trunk, this.compareTrunk);
        this.trunk.sortChildren();
    }

    private void search(File directory, GalleryNode parent, GalleryNode compareNode) {

        for (File f : directory.listFiles()) {

            if (f.isDirectory()) {
                File dirConfig = new File(f.getAbsolutePath() + "/" + COLLECTION_CONFIG_FILE_NAME);
                File galConfig = new File(f.getAbsolutePath() + "/" + GALLERY_CONFIG_FILE_NAME);
                if (dirConfig.exists()) {
                    this.addGallery(f, dirConfig, false, parent, compareNode);
                } else if (galConfig.exists()) {
                    this.addGallery(f, galConfig, true, parent, compareNode);
                }
            }
        }
    }

    private void addGallery(File dir, File config, boolean isGallery, GalleryNode parent, GalleryNode compareNode) {
        GalleryNodeSettings settings = new GalleryNodeSettings(isGallery
                ? GalleryNodeSettings.GalleryType.GALLERY : GalleryNodeSettings.GalleryType.COLLECTION);
        GalleryNode compareChild = null;
        if (compareNode != null) {
            compareChild = compareNode.getChildNode(dir.getName());
        }
        settings.compareNode = compareChild;
        GalleryNode newNode = new GalleryNode(config, settings);
        parent.getChildren().add(newNode);
        if (compareChild != null) {
            compareChild.getSettings().compareNode = newNode;
            compareChild.updateView();
        }
        if (!isGallery) {
            this.search(dir, newNode, compareChild);
        }
    }
}
