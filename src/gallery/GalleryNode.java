package gallery;

import static gallery.GalleryNodeSettings.GalleryStatus;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public final class GalleryNode extends TreeItem {

    private final File config;
    private final GalleryNodeSettings settings;
    
    private final GalleryNodeData data;
    
    private List<GalleryImage> imageList = null;
    
    public GalleryNode (File config, GalleryNodeSettings settings) {
        this.config = config;
        this.settings = settings;
        
        if (this.settings.type != GalleryNodeSettings.GalleryType.TRUNK)
            this.data = GalleryNodeData.readGalleryNodeData(this.config);
        else
            this.data = GalleryNodeData.createTrunkNodeData();
        
        this.updateView();
    }
    
    public GalleryNode (File config, GalleryNodeData data, GalleryNodeSettings settings) {
        this.config = config;
        this.data = data;
        this.settings = settings;
        
        GalleryNodeData.saveGalleryNodeData(config, data);
        
        this.updateView();
    }
    
    public GalleryNodeData getData() {
        return this.data;
    }
    
    public GalleryNodeSettings getSettings() {
        return this.settings;
    }
    
    public void updateView() {
        super.setValue(this.data.name);
        
        ImageView icon = new ImageView();
        if (this.settings.processing)
            icon.setImage(new Image(getClass().getResourceAsStream("icon_pending.png")));
        
        else if (null != this.settings.type)
            switch (this.settings.type) {
            case TRUNK:
                icon.setImage(new Image(getClass().getResourceAsStream("icon_trunk.png")));
                break;
            case COLLECTION:
                icon.setImage(new Image(getClass().getResourceAsStream("icon_folder.png")));
                break;
            default:
                switch (this.getStatus()) {
                    case OFFLINE:      
                        icon.setImage(new Image(getClass().getResourceAsStream("icon_gallery.png")));
                        break;
                    case UPTODATE:
                        icon.setImage(new Image(getClass().getResourceAsStream("icon_imported.png")));
                        break;
                    case LOCALNEWER:
                        icon.setImage(new Image(getClass().getResourceAsStream("icon_localnewer.png")));
                        break;
                    case SERVERNEWER:
                        icon.setImage(new Image(getClass().getResourceAsStream("icon_servernewer.png"))); break;
                }   break;
        }
        //TODO only when necessary
        Platform.runLater(() -> {super.setGraphic(icon);});
    }
    
    public GalleryNodeSettings.GalleryStatus getStatus() {
        if (this.settings.compareNode == null)
            return GalleryStatus.OFFLINE;
        if (this.settings.compareNode.getData().lastChanged.getTime() == this.data.lastChanged.getTime())
            return GalleryStatus.UPTODATE;
        else if (this.settings.compareNode.getData().lastChanged.getTime() < this.data.lastChanged.getTime())
            return GalleryStatus.LOCALNEWER;
        else
            return GalleryStatus.SERVERNEWER;
    }
    
    public GalleryNode getChildNode(String nodeName) {
        for (Object s : this.getChildren().toArray()) {
            if (((GalleryNode)s).getLocation().getName().equals(nodeName)) {
                return (GalleryNode)s;
            }
        }
        return null;
    }
    
    public void sortChildren() {
        this.getChildren().sort(Comparator.comparing((GalleryNode g) -> {
            return g.isGallery();
        }).thenComparing((GalleryNode g1, GalleryNode g2) -> g1.data.name.compareTo(g2.data.name)));
        this.getChildren().stream().forEach((g) -> {
            ((GalleryNode)g).sortChildren();
        });
    }

    @Override
    public String toString() {
        return this.data.name;
    }
    
    public void galleryChanged() {
        this.data.lastChanged = new Date();
        GalleryNodeData.saveGalleryNodeData(this.config, this.data);
        this.updateView();
    }
    
    public File getConfigFile() {
        return this.config;
    }

    public File getLocation() {
        if (this.settings.type == GalleryNodeSettings.GalleryType.TRUNK)
            return this.config;
        return this.config.getParentFile();
    }

    public boolean isGallery() {
        return this.settings.type == GalleryNodeSettings.GalleryType.GALLERY;
    }
    
    public boolean isTrunk() {
        return this.settings.type == GalleryNodeSettings.GalleryType.TRUNK;
    }
    
    @Deprecated
    public File[] listImages() {
        return this.getLocation().listFiles((File dir, String fileName) -> {
            return (GalleryManager.IMAGE_FILE_PATTERN.matcher(fileName).matches()
                    || GalleryManager.VIDEO_FILE_PATTERN.matcher(fileName).matches());
        });
    }
    
    public void createImageList() {
        if (this.imageList == null)
            this.imageList = new ArrayList<>();
        
        this.imageList.clear();
        
        File[] fileList = this.getLocation().listFiles((File dir, String fileName) -> {
            return (GalleryManager.IMAGE_FILE_PATTERN.matcher(fileName).matches()
                    || GalleryManager.VIDEO_FILE_PATTERN.matcher(fileName).matches());
        });
        
        if (fileList != null) {
            for (File f : fileList) {
                try {
                    this.imageList.add(new GalleryImage(f));
                } catch (IOException ex) {
                    Logger.getLogger("logfile").log(Level.SEVERE, ex.getMessage());
                }
            }
        }
        
        Collections.sort(this.imageList);
    }
    
    public synchronized void addImage(File image) throws IOException {
        this.getImageList(false).add(new GalleryImage(image));
    }
    
    public List<GalleryImage> getImageList(boolean reload) {
        if (reload || this.imageList == null)
            this.createImageList();
        return this.imageList;
    }
    
    public void createThumbnailFolder() {
        File folder = this.getLocation();
        File thumbnailFolder = new File(folder.getPath() + "/" + GalleryManager.THUMBNAIL_FOLDER + "/");

        if (!thumbnailFolder.isDirectory()) {
            thumbnailFolder.mkdir();
        }
    }
    
    public List<GalleryNode> getLocationInTree() {
        List<GalleryNode> path = new LinkedList<>();
        this.getLocationInTree(path);
        return path;
    }
    
    private void getLocationInTree(List<GalleryNode> path) {
        if (this.getParent() != null && !((GalleryNode)this.getParent()).isTrunk())
            ((GalleryNode)this.getParent()).getLocationInTree(path);
        path.add(this);
    }

}
