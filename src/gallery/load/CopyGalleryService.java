/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallerydemo.task.TaskController;
import java.io.File;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author fabian
 */
public class CopyGalleryService extends Service {

    private final ServiceControllerInterface controller;
    private final File origin;
    private final File target;
    private final TaskController task;
    private final ServiceCallbackInterface callback;
    
    public CopyGalleryService (ServiceControllerInterface controller, File origin, File target, String description, ServiceCallbackInterface callback) {
        this.controller = controller;
        this.origin = origin;
        this.target = target;
        this.task = this.controller.registerNewTask(description, -1);
        this.callback = callback;
    }
    
    @Override
    protected Task createTask() {
        return new CopyGalleryTask(this.origin, this.target, this.task, this.callback);
    }
    
}