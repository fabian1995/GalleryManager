/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryNode;
import gallerydemo.task.TaskController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author fabian
 */
public class DuplicateGalleryService extends Service {

    private final ServiceControllerInterface controller;
    private final GalleryNode origin;
    private final GalleryNode compareTrunk;
    private final TaskController task;
    private final ServiceCallbackInterface callback;
    
    public DuplicateGalleryService (ServiceControllerInterface controller, GalleryNode origin, GalleryNode compareTrunk, String description, ServiceCallbackInterface callback) {
        this.controller = controller;
        this.origin = origin;
        this.compareTrunk = compareTrunk;
        this.task = this.controller.registerNewTask(description, -1);
        this.callback = callback;
    }
    
    @Override
    protected Task createTask() {
        return new DuplicateGalleryTask(this.origin, this.compareTrunk, this.task, this.callback);
    }
    
}