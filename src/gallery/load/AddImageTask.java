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
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author fabian
 */
public class AddImageTask extends Task {

    private final GalleryDemoViewController controller;
    private final GalleryNode gallery;
    private final List<File> fileList;
    private final TaskController task;

    public AddImageTask(GalleryDemoViewController controller, GalleryNode gallery, List<File> fileList, TaskController task) {
        this.controller = controller;
        this.gallery = gallery;
        this.fileList = fileList;
        this.task = task;
    }

    @Override
    protected Object call() throws Exception {
        
        Platform.runLater(() -> {
           this.task.setProgress(0, this.fileList.size());
        });
        
        for (int i = 0; i < this.fileList.size(); i++) {
            if (this.fileList.get(i).getName().matches(GalleryManager.IMAGE_FILE_REGEX)
                    || this.fileList.get(i).getName().matches(GalleryManager.VIDEO_FILE_REGEX)) {
                try {
                    File destinationFile = new File(this.controller.getActiveGallery().getLocation() + "/" + this.fileList.get(i).getName());
                    Files.copy(this.fileList.get(i).toPath(), destinationFile.toPath(), COPY_ATTRIBUTES);
                    this.gallery.addImage(destinationFile);
                } catch (IOException ex) {
                    Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
                }
            }
            final int progress = i;
            Platform.runLater(() -> {
                if (!isCancelled()) {
                    task.setProgress(progress, this.fileList.size());
                }
            });
        }
        
        Platform.runLater(() -> {
            this.task.delete();
            this.controller.reloadGalleryImages(this.controller.getActiveGallery(), false);
        });
        return null;
    }

}
