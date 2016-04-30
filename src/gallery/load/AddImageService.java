/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

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
public class AddImageService extends Service {

    private final GalleryDemoViewController controller;
    private final List<File> fileList;
    private final TaskController task;
    
    public AddImageService (GalleryDemoViewController controller, List<File> fileList, String galleryName) {
        this.controller = controller;
        this.fileList = fileList;
        this.task = this.controller.registerNewTask("Copying images to '" + galleryName + "'", 1);
    }
    
    @Override
    protected Task createTask() {
        return new AddImageTask(this.controller, this.fileList, this.task);
    }
    
}
