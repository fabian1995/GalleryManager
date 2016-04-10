package gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryManager {

    private final File root;

    public static final String GALLERY_CONFIG_FILE_NAME = "gallery.json";
    public static final String THUMBNAIL_FOLDER = ".thumbnails";
    public static final String IMAGE_FILE_REGEX = "[\\w-]+(.jpg|.JPG|.png|.PNG|.jpeg|.JPEG|.bmp|.BMP)$";

    private GalleryNode trunk = null;
    private GalleryNode compareTrunk;

    public GalleryManager(File root) {
        this(root, null);
    }
    
    public GalleryManager(File root, GalleryNode compareTrunk) {
        this.root = root;
        this.trunk = new GalleryNode(this.root);
        this.compareTrunk = compareTrunk;
    }

    public GalleryNode getTrunk() {
        return this.trunk;
    }

    public void search() {
        List<String> path = new ArrayList<>();
        //path.add(root.getName());
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
            else if (f.isFile() && f.getName().equals(GALLERY_CONFIG_FILE_NAME)) {
                /*for (String s : path) {
                    System.out.print(s + "/");
                }*/
                //System.out.println(f.getName());
                //path.add(0, rootName);
                this.insertGallery(f, path, rootName);
                //path.remove(rootName);
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
                /*if (comparison != null && comparison.getChildren().contains(name)) {
                    comparison = (GalleryNode) comparison.getChildren().get(comparison.getChildren().indexOf((Object)name));
                }
                else
                    comparison = null;*/
                }
            } else {
                /*if (comparison != null && comparison.getChildren().contains(name))
                    name = "[OK] " + name;*/
                /*if (comparison != null) {
                    for (Object s : comparison.getChildren().toArray()) {
                        System.out.print("" + ((GalleryNode)s).getFileName() + ", ");
                        if (((GalleryNode)s).getFileName().equals(name) && ((GalleryNode)s).isGallery())
                            name = "[OK] " + name;
                    }
                    System.out.println(" -> " + name);
                }*/
                boolean isImported = false;
                
                if (comparison != null) {
                    GalleryNode c = comparison.getChildNode(name);
                    if (c != null && c.isGallery())
                        isImported = true;
                    if (c != null)
                        comparison = c;
                }
                
                //System.out.println("Adding: " + pathToGallery);
                GalleryNode g = new GalleryNode(new File(pathToGallery), isImported);
                position.getChildren().add(g);
                position = g;
            }
        }

        //position.setConfigFile(config);
    }
}
