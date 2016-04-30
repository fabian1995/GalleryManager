/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallerydemo.GalleryDemoViewController;
import gallerydemo.task.TaskController;
import galleryremote.GalleryRemoteViewController;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author fabian
 */
public class ExportTask extends Task {

    private final GalleryDemoViewController controller;
    private final GalleryNode gallery;
    private final TaskController task;

    public ExportTask(GalleryDemoViewController controller, GalleryNode gallery, TaskController task) {
        this.controller = controller;
        this.gallery = gallery;
        this.task = task;
    }

    @Override
    protected Object call() throws Exception {
        
        File origin = this.gallery.getLocation();
        String absOriginPath = origin.getAbsolutePath().replace('\\', '/');
        String absLocalPath = controller.getLocalGalleryLocation().getAbsolutePath().replace('\\', '/');
        String absRemotePath = controller.getRemoteGalleryLocation().getAbsolutePath().replace('\\', '/');

        Logger.getLogger("logfile").log(Level.INFO, "[export] ORIGIN " + absOriginPath);
        Logger.getLogger("logfile").log(Level.INFO, "[export] LOCAL  " + absLocalPath);
        Logger.getLogger("logfile").log(Level.INFO, "[export] REMOTE " + absRemotePath);

        File target = new File(absRemotePath + "/" + absOriginPath.replaceFirst(absLocalPath, ""));
        Logger.getLogger("logfile").info("[export] " + this.gallery.getFileName() + ": " + origin + " -> " + target );
        
        try {
            FileUtils.copyDirectory(origin, target, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().equals(GalleryManager.GALLERY_CONFIG_FILE_NAME)
                            || pathname.getName().matches(GalleryManager.IMAGE_FILE_REGEX);
                }
            });
            Platform.runLater(() -> {
                this.gallery.setOrigin(target);
                this.gallery.saveConfigFile();
            });
        } catch (IOException ex) {
            Logger.getLogger(GalleryRemoteViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Platform.runLater(() -> {
            this.task.delete();
        });
        return null;
    }
}