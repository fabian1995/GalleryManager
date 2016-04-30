/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.task;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class TaskController extends BorderPane {
    
    private final VBox container;
    
    @FXML private ProgressBar progressBar;
    
    @FXML private Text title;
    
    @FXML private Text progressText;

    public TaskController(VBox container, String titleText, int max) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Task.fxml"));
        fxmlLoader.setRoot((BorderPane) this);
        fxmlLoader.setController((BorderPane) this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        this.container = container;
        
        this.title.setText(titleText);
        
        this.setProgress(1, max);
    }
    
    public final void setProgress(int progress, int max) {
        this.progressBar.setProgress((double)progress/max);
        if (max >= 0)
            this.progressText.setText(progress + "/" + max);
        else
            this.progressText.setText("");
    }
    
    public void delete() {
        this.container.getChildren().remove(this);
    }
}
