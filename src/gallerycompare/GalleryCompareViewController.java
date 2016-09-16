/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerycompare;

import gallery.GalleryNode;
import gallery.comparison.GalleryComparison;
import gallerycompare.filelist.FileListElement;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
    
    private GalleryComparison comparison;
    
    public GalleryCompareViewController(GalleryNode g1, GalleryNode g2) {
        this.gallery1 = g1;
        this.gallery2 = g2;
    }

    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.comparison = new GalleryComparison(gallery1, gallery2);
        this.comparison.compare();
        //comp.printStats();
        Logger.getLogger("logfile").info("[init] GalleryCompareView");
        
        this.compareSplitPane.getItems().add(new FileListElement(this.comparison, 0, "Neu auf diesem Computer"));
        this.compareSplitPane.getItems().add(new FileListElement(this.comparison, 1, "Neu am Server"));
    }
    
    public void updateTimestamps () {
        boolean[] changeFlags = this.comparison.getChangeFlags();
        
        if (changeFlags[0] && changeFlags[1]) {
            Logger.getLogger("logfile").log(Level.INFO, "[Compare] Both galleries have been changed.");
            this.gallery1.galleryChanged();
            this.gallery2.setLastChanged(this.gallery1.getLastChanged());
        }
        else if (changeFlags[0] && !changeFlags[1]) {
            Logger.getLogger("logfile").log(Level.INFO, "[Compare] Local gallery has been changed.");
            if (this.gallery2.getLastChanged().getTime() == 0)
                this.gallery2.galleryChanged();
            this.gallery1.setLastChanged(this.gallery2.getLastChanged());
        }
        else if (!changeFlags[0] && changeFlags[1]) {
            Logger.getLogger("logfile").log(Level.INFO, "[Compare] Remote Gallery has been changed.");
            if (this.gallery1.getLastChanged().getTime() == 0)
                this.gallery1.galleryChanged();
            this.gallery2.setLastChanged(this.gallery1.getLastChanged());
        }
        else {
            Logger.getLogger("logfile").log(Level.INFO, "[Compare] No changes registered.");
        }
    }
}
