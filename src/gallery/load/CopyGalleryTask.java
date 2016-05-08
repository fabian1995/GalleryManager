/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryManager;
import gallerydemo.task.TaskController;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;

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
        
        final File[] fileList = origin.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().matches(GalleryManager.IMAGE_FILE_REGEX)
                        || name.equals(GalleryManager.GALLERY_CONFIG_FILE_NAME);
            }
        });

        Platform.runLater(() -> {
           this.task.setProgress(0, fileList.length);
        });
        
        if (!target.exists())
            target.mkdirs();
        
        for (int i = 0; i < fileList.length; i++) {
            try {
                Files.copy(fileList[i].toPath(), new File(target.toPath() + "/" + fileList[i].getName()).toPath(), COPY_ATTRIBUTES);
            } catch (IOException ex) {
                Logger.getLogger("logfile").log(Level.SEVERE, ex.getMessage());
            }
            
            final int progress = i;
            Platform.runLater(() -> {
                if (!isCancelled()) {
                    task.setProgress(progress, fileList.length);
                }
            });
        }
        
        
        Platform.runLater(() -> {
            this.task.delete();
            this.callback.run();
        });
        return null;
    }
}