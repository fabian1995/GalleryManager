/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallerydemo.task.TaskController;

/**
 *
 * @author fabian
 */
public interface ServiceControllerInterface {
    public TaskController registerNewTask(String titleText, int max);
}
