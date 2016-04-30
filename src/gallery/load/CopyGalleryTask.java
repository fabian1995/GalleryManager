/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryManager;
import gallerydemo.task.TaskController;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author fabian
 */
public class CopyGalleryTask extends Task {

    private final File origin;
    private final File target;
    private final TaskController task;
    private final ServiceCallbackInterface callback;

    public CopyGalleryTask(File origin, File target, TaskController task, ServiceCallbackInterface callback) {
        this.origin = origin;
        this.target = target;
        this.task = task;
        this.callback = callback;
    }

    @Override
    protected Object call() throws Exception {
        
        try {
            FileUtils.copyDirectory(origin, target, (File pathname) ->
                    pathname.getName().equals(GalleryManager.GALLERY_CONFIG_FILE_NAME)
                    || pathname.getName().matches(GalleryManager.IMAGE_FILE_REGEX));
        } catch (IOException ex) {
            Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
        }
        Platform.runLater(() -> {
            this.task.delete();
            this.callback.run();
        });
        return null;
    }
}