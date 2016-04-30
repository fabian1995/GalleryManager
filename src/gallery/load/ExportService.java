/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryNode;
import gallerydemo.GalleryDemoViewController;
import gallerydemo.task.TaskController;
import java.io.File;
import java.util.List;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author fabian
 */
public class ExportService extends Service {

    private final GalleryDemoViewController controller;
    private final GalleryNode gallery;
    private final TaskController task;
    
    public ExportService (GalleryDemoViewController controller, GalleryNode gallery) {
        this.controller = controller;
        this.gallery = gallery;
        this.task = this.controller.registerNewTask("Exporting gallery '" + gallery.getName() + "'", -1);
    }
    
    @Override
    protected Task createTask() {
        return new ExportTask(this.controller, this.gallery, this.task);
    }
    
}