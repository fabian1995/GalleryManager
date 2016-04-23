/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallery.load;

import gallery.GalleryNode;
import gallerydemo.GalleryDemoViewController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.layout.FlowPane;

/**
 *
 * @author fabian
 */
public class ImageLoaderService extends Service {

    private final GalleryDemoViewController controller;
    private final FlowPane imagePane;
    private final GalleryNode gallery;
    
    public ImageLoaderService (GalleryDemoViewController controller, GalleryNode gallery, FlowPane imagePane) {
        this.controller = controller;
        this.imagePane = imagePane;
        this.gallery = gallery;
    }
    
    @Override
    protected Task createTask() {
        return new ImageLoader(controller, gallery, imagePane);
    }
    
}
