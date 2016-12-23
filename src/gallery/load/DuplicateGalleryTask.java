/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery.load;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallery.GalleryNodeSettings;
import gallerydemo.task.TaskController;
import java.io.File;
import java.io.FilenameFilter;
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
public class DuplicateGalleryTask extends Task {

    private final GalleryNode origin;
    private final GalleryNode targetTrunk;
    private final TaskController task;
    private final ServiceCallbackInterface callback;

    public DuplicateGalleryTask(GalleryNode origin, GalleryNode targetTrunk, TaskController task, ServiceCallbackInterface callback) {
        this.origin = origin;
        this.targetTrunk = targetTrunk;
        this.task = task;
        this.callback = callback;
    }

    @Override
    protected Object call() throws Exception {
        
        // Indicate that the service is processing its task
        Platform.runLater(() -> {
            this.task.setProgress(-1, 1);
            origin.getSettings().processing = true;
            origin.updateView();
        });
        
        // Iteration variables
        List<GalleryNode> path = origin.getLocationInTree();
        GalleryNode currentNode = targetTrunk, nextNode;
        
        // Insert the new gallery into the tree, with the same path
        // Eventually, more than one node has to be created on the way
        for (GalleryNode node : path) {
          
            // CurrentNode and next node are in the tree which will contain a
            // copy of the target node
            nextNode = currentNode.getChildNode(node.getLocation().getName());
            
            // A new node has to be created (can be the target node or one of
            // its parents)
            if (nextNode == null) {
                // Copy all files of that node
                File copyTarget = new File(currentNode.getLocation().toPath() + "/" + node.getLocation().getName());
                this.copyAssets(node.getLocation(), copyTarget, node.isGallery());
                // Create the node
                nextNode = new GalleryNode(
                        new File(copyTarget.getPath() + "/" + node.getConfigFile().getName()),
                        new GalleryNodeSettings(node.getSettings().type)
                );
                // Add it to the tree
                currentNode.getChildren().add(nextNode);
                currentNode.sortChildren();
                // Create references between the riginal node and its duplicate
                // in the other tree
                nextNode.getSettings().compareNode = node;
                node.getSettings().compareNode = nextNode;
            }
            currentNode = nextNode;
        }
        
        // Create a copy of the duplicate node so that it can be accessed from
        // another thread (see below)
        final GalleryNode targetNode = currentNode;
        
        // Indicate with the GUI that the task is finished
        Platform.runLater(() -> {
            this.task.delete();
            this.origin.getSettings().processing = false;
            this.origin.updateView();
            targetNode.updateView();
            
            if (this.callback != null)
                this.callback.run();
        });
        return null;
    }
    
    private void copyAssets(File from, File target, boolean showProgress) {
        final File[] fileList = from.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.matches(GalleryManager.IMAGE_FILE_REGEX)
                        || name.matches(GalleryManager.VIDEO_FILE_REGEX)
                        || name.matches(GalleryManager.GALLERY_CONFIG_FILE_NAME)
                        || name.matches(GalleryManager.COLLECTION_CONFIG_FILE_NAME));
            }
        });

        if (showProgress)
            Platform.runLater(() -> {this.task.setProgress(0, fileList.length);});
        
        if (!target.exists())
            target.mkdirs();
        
        for (int i = 0; i < fileList.length; i++) {
            try {
                Files.copy(fileList[i].toPath(), new File(target.toPath() + "/" + fileList[i].getName()).toPath(), COPY_ATTRIBUTES);
            } catch (IOException ex) {
                Logger.getLogger("logfile").log(Level.SEVERE, ex.getLocalizedMessage());
            }
            
            if (showProgress) {
                final int progress = i;
                Platform.runLater(() -> {
                    if (!isCancelled()) {
                        task.setProgress(progress, fileList.length);
                    }
                });
            }
        }
    }
}