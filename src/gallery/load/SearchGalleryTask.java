/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallerydemo.task.TaskController;
import java.io.File;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author fabian
 */
public class SearchGalleryTask extends Task {
    
    private GalleryManager newGallery;
    private final File root;
    private final GalleryNode compareTrunk;
    private final boolean useCache;
    private final TaskController task;
    private final ServiceCallbackInterface callback;

    public SearchGalleryTask(File root, GalleryNode compareTrunk, boolean useCache, TaskController task, ServiceCallbackInterface callback) {
        this.newGallery = null;
        this.root = root;
        this.compareTrunk = compareTrunk;
        this.useCache = useCache;
        this.task = task;
        this.callback = callback;
    }

    @Override
    protected Object call() throws Exception {
        
        this.newGallery = new GalleryManager(root, compareTrunk);
        
        if (this.useCache)
            this.newGallery.readCacheFile();
        else
            this.newGallery.search();
        
        System.out.println("Search Task finished");
        
        return this.newGallery;
    }

}
