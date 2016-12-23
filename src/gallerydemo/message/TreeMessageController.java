/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.message;

import gallery.GalleryNode;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class TreeMessageController extends BorderPane {
    
    private final VBox container;
    private final TreeView tree;
    private final GalleryNode target;
    
    @FXML private Label title;
    
    @FXML private Label desc;
    
    @FXML private Button button;
    
    @FXML private Button close;

    public TreeMessageController(VBox container, String title, String desc, TreeView tree, GalleryNode target) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Message.fxml"));
        fxmlLoader.setRoot((BorderPane) this);
        fxmlLoader.setController((BorderPane) this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        this.container = container;
        this.tree = tree;
        this.target = target;
        
        this.title.setText(title);
        this.title.setWrapText(true);
        this.title.setTextFill(Paint.valueOf("#8a8a8a"));
        
        this.desc.setText(desc);
        this.desc.setWrapText(true);
        this.desc.setTextFill(Paint.valueOf("#729FCF"));
        
        if (this.tree != null && this.target != null) {
            this.button.setOnAction((ActionEvent) -> {
                tree.getSelectionModel().select(this.target);
            });
        }
        
        this.close.setOnAction((ActionEvent) -> {
            this.container.getChildren().remove(this);
        });
    }
}
