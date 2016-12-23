/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallerydemo.GalleryDemoViewController;
import gallerydemo.task.TaskController;
import java.io.File;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 *
 * @author fabian
 */
public class SearchGalleryService extends Service {
    
    private final GalleryDemoViewController controller;
    private final File root;
    private final GalleryNode compareTrunk;
    private final TaskController task;
    private final ServiceCallbackInterface callback;
    
    public SearchGalleryService(GalleryDemoViewController controller, File root, GalleryNode compareTrunk, ServiceCallbackInterface callback) {
        this.controller = controller;
        this.root = root;
        this.compareTrunk = compareTrunk;
        this.task = controller.registerNewTask("Server wird durchsucht", -1);
        this.callback = callback;
    }

    @Override
    protected Task createTask() {
        return new SearchGalleryTask(this.controller, this.root, this.compareTrunk, this.task, this.callback);
    }
    
}
