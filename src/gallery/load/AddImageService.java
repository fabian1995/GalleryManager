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
public class AddImageService extends Service {

    private final GalleryDemoViewController controller;
    private final GalleryNode gallery;
    private final List<File> fileList;
    private final TaskController task;
    
    public AddImageService (GalleryDemoViewController controller, GalleryNode gallery, List<File> fileList) {
        this.controller = controller;
        this.gallery = gallery;
        this.fileList = fileList;
        this.task = this.controller.registerNewTask("Copying images to '" + gallery.getName() + "'", 1);
    }
    
    @Override
    protected Task createTask() {
        return new AddImageTask(this.controller, this.gallery, this.fileList, this.task);
    }
    
}
