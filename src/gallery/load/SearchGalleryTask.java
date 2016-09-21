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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author fabian
 */
public class SearchGalleryTask extends Task {

    private final GalleryDemoViewController controller;
    private final File root;
    private final GalleryNode compareTrunk;
    private final TaskController task;
    private final ServiceCallbackInterface callback;

    public SearchGalleryTask(GalleryDemoViewController controller, File root, GalleryNode compareTrunk, TaskController task, ServiceCallbackInterface callback) {
        this.controller = controller;
        this.root = root;
        this.compareTrunk = compareTrunk;
        this.task = task;
        this.callback = callback;
    }

    @Override
    protected Object call() throws Exception {

        long start, end;

        start = System.nanoTime();
        final GalleryManager newGallery = new GalleryManager(this.root, this.compareTrunk);
        newGallery.search();
        end = System.nanoTime();

        Logger.getLogger("logfile").log(Level.INFO, " Search Gallery Task for path '{0}' took {1}ms.", new Object[]{root.getAbsolutePath(), (double) (end - start) / 1000000});

        Platform.runLater(() -> {
            controller.setRemoteManager(newGallery);
            task.delete();
            callback.run();
        });

        return null;
    }

}
