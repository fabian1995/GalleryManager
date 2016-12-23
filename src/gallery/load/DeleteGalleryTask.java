/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryNode;
import gallery.GalleryNodeSettings;
import gallerydemo.task.TaskController;
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
public class DeleteGalleryTask extends Task {

    private final GalleryNode target;
    private final TaskController task;
    private final ServiceCallbackInterface callback;

    public DeleteGalleryTask(GalleryNode target, TaskController task, ServiceCallbackInterface callback) {
        this.target = target;
        this.task = task;
        this.callback = callback;
    }

    @Override
    protected Object call() throws Exception {

        // Indicate that the service is processing its task
        Platform.runLater(() -> {
            this.task.setProgress(-1, -1);
        });

        try {
            System.out.println("delete gallery");
            this.deleteGallery(this.target);
        } catch (IOException e) {
            // TODO error handling
            Logger.getLogger("logfile").log(Level.SEVERE, "[DELETE GALLERY] Failed: {0}", e.toString());
        }

        // Indicate with the GUI that the task is finished
        Platform.runLater(() -> {
            this.task.delete();
            if (this.callback != null) {
                this.callback.run();
            }
        });

        return null;
    }

    private void deleteGallery(GalleryNode g) throws IOException {
        if (g.getSettings().type == GalleryNodeSettings.GalleryType.COLLECTION) {
            for (Object o : g.getChildren().toArray()) {
                this.deleteGallery((GalleryNode) o);
            }
        }

        Platform.runLater(() -> {
            if (g.getSettings().compareNode != null) {
                g.getSettings().compareNode.getSettings().compareNode = null;
                g.getSettings().compareNode.updateView();
            }
            g.getParent().getChildren().remove(g);
        });

        FileUtils.deleteDirectory(g.getLocation());

    }

}
