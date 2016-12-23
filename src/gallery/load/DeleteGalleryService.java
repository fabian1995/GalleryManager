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
public class DeleteGalleryService extends Service {

    private final ServiceControllerInterface controller;
    private final GalleryNode target;
    private final TaskController task;
    private final ServiceCallbackInterface callback;
    
    public DeleteGalleryService (ServiceControllerInterface controller, GalleryNode target, String description, ServiceCallbackInterface callback) {
        this.controller = controller;
        this.target = target;
        this.task = this.controller.registerNewTask(description, -1);
        this.callback = callback;
    }
    
    @Override
    protected Task createTask() {
        return new DeleteGalleryTask(this.target, this.task, this.callback);
    }
    
}
