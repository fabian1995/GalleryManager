/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerycompare;

import gallery.GalleryNode;
import gallery.comparison.GalleryComparison;
import gallerycompare.filelist.FileListElement;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class GalleryCompareViewController implements Initializable {

    @FXML
    private SplitPane compareSplitPane;
    
    private final GalleryNode gallery1;
    private final GalleryNode gallery2;
    
    public GalleryCompareViewController(GalleryNode g1, GalleryNode g2) {
        this.gallery1 = g1;
        this.gallery2 = g2;
    }

    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        GalleryComparison comp = new GalleryComparison(gallery1, gallery2);
        comp.compare();
        //comp.printStats();
        Logger.getLogger("logfile").info("[init] GalleryCompareView");
        
        this.compareSplitPane.getItems().add(new FileListElement(comp, 0));
        this.compareSplitPane.getItems().add(new FileListElement(comp, 1));
    }
}
