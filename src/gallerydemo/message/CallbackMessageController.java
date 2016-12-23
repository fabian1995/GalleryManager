/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.message;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

/**
 *
 * @author fabian
 */
public final class CallbackMessageController extends BorderPane {
    
    private final VBox container;
    private MessageCallbackInterface callback;
    
    @FXML private Label title;
    
    @FXML private Label desc;
    
    @FXML private Button button;
    
    @FXML private Button close;

    public CallbackMessageController(VBox container, String title, String desc, String buttonText, boolean closeable, MessageCallbackInterface callback) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Message.fxml"));
        fxmlLoader.setRoot((BorderPane) this);
        fxmlLoader.setController((BorderPane) this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        this.container = container;
        
        this.title.setText(title);
        this.title.setWrapText(true);
        this.title.setTextFill(Paint.valueOf("#8a8a8a"));
        
        this.desc.setText(desc);
        this.desc.setWrapText(true);
        this.desc.setTextFill(Paint.valueOf("#729FCF"));
        
        this.button.setText(buttonText);
        
        this.setCallback(callback);
        
        if (closeable) {
            this.close.setOnAction((ActionEvent) -> {
                this.container.getChildren().remove(this);
            });
        } else {
            this.close.setVisible(false);
        }
    }
    
    public void setCallback(MessageCallbackInterface callback) {
        
        this.callback = callback;
        
        if (this.callback != null) {
            this.button.setOnAction((ActionEvent) -> {
                callback.run();
            });
        }
    }
    
    public void setButtonDisable(boolean state) {
        this.button.setDisable(state);
    }
}
