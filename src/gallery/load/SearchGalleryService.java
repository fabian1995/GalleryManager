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
    
    private GalleryDemoViewController controller;
    private final File root;
    private final GalleryNode compareTrunk;
    private final boolean useCache;
    private final TaskController task;
    private final ServiceCallbackInterface callback;
    
    public SearchGalleryService(GalleryDemoViewController controller, File root, GalleryNode compareTrunk, boolean useCache, ServiceCallbackInterface callback) {
        this.controller = controller;
        this.root = root;
        this.compareTrunk = compareTrunk;
        this.useCache = useCache;
        this.task = controller.registerNewTask("Searching Gallery: " + this.useCache, -1);
        this.callback = callback;
    }

    @Override
    protected Task createTask() {
        final Task t = new SearchGalleryTask(this.root, this.compareTrunk, this.useCache, this.task, this.callback);
        t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent e) {
                System.out.println("Handling finished task");
                Platform.runLater(() -> {
                    System.out.println("Assigning new value");
                    controller.setRemoteManager((GalleryManager)t.getValue());
                    task.delete();
                    callback.run();
                });
            }
        });
        return t;
    }
    
}
