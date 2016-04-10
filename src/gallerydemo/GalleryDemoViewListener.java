/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo;

import gallery.GalleryNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

/**
 *
 * @author fabian
 */
public class GalleryDemoViewListener implements ChangeListener<TreeItem<String>> {

    private final GalleryDemoViewController controller;
    
    public GalleryDemoViewListener(GalleryDemoViewController controller) {
        this.controller = controller;
    }
    
    @Override
    public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
        GalleryNode galleryNode = (GalleryNode) newValue;
        
        this.controller.setActiveGallery(galleryNode);

        if (galleryNode.isGallery()) {
            //this.controller.buildGalleryMenu();
            this.controller.reloadGalleryImages(galleryNode);
        } else {
            this.controller.getImagePane().getChildren().clear();
            //this.controller.buildCollectionMenu();
        }
    }

}
