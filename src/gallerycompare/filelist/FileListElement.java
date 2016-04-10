/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerycompare.filelist;

import gallery.comparison.GalleryComparison;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class FileListElement extends BorderPane {
    
    private final GalleryComparison comparison;
    private final int locationID;
    
    private final ObservableList<String> fileList;
    private String selectedImageName = null;
    
    @FXML
    private Text titleElement;
    
    @FXML
    private ListView fileListView;
    
    @FXML
    private Button buttonCopy;
    
    @FXML
    private Button buttonDelete;
    
    @FXML
    private Button buttonCopyAll;
    
    @FXML
    private Button buttonDeleteAll;
    
    @FXML
    private ImageView imagePreview;
    
    public FileListElement (GalleryComparison comparison, int locationID) {
        
        this.comparison = comparison;
        this.locationID = locationID;
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileList.fxml"));
        fxmlLoader.setRoot((BorderPane)this);
        fxmlLoader.setController((BorderPane)this);
        
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        this.fileList = FXCollections.observableArrayList();
        this.fileList.addAll(this.comparison.getNewFiles(this.locationID));
        
        this.fileListView.setItems(this.fileList);
        this.fileListView.setEditable(false);
        
        this.titleElement.setText("Neu in " + this.comparison.getGallery(locationID).getName());
        
        this.buttonCopy.setDisable(true);
        this.buttonDelete.setDisable(true);
        
        this.fileListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                imageSelected(newValue);
            }
        });
        
        this.buttonCopy.setOnAction((ActionEvent e) -> {
            if (selectedImageName != null) {
                try {
                    comparison.copy(selectedImageName, locationID);
                } catch (IOException ex) {
                    Logger.getLogger(FileListElement.class.getName()).log(Level.SEVERE, null, ex);
                }
                fileList.remove(selectedImageName);
                imageSelected((String)fileListView.getSelectionModel().getSelectedItem());
            }
        });
        
        this.buttonDelete.setOnAction((ActionEvent e) -> {
            if (selectedImageName != null) {
                try {
                    comparison.delete(selectedImageName, locationID);
                } catch (IOException ex) {
                    Logger.getLogger(FileListElement.class.getName()).log(Level.SEVERE, null, ex);
                }
                fileList.remove(selectedImageName);
                imageSelected((String)fileListView.getSelectionModel().getSelectedItem());
            }
        });
        
        this.buttonCopyAll.setOnAction((ActionEvent e) -> {
            try {
                comparison.copyAll(locationID);
            } catch (IOException ex) {
                Logger.getLogger(FileListElement.class.getName()).log(Level.SEVERE, null, ex);
            }
            fileList.clear();
            imageSelected(null);
        });
        
        this.buttonDeleteAll.setOnAction((ActionEvent e) -> {
            comparison.deleteAll(locationID);
            fileList.clear();
            imageSelected(null);
        });
    }
    
    private void imageSelected(String name) {
        if (name != null) {
            this.imagePreview.setImage(
                    new Image("file:" + comparison.getGallery(locationID).getLocation() + "/" + name, -1, 150, true, false, false)
            );
            buttonCopy.setDisable(false);
            buttonDelete.setDisable(false);
        }
        else {
            this.imagePreview.setVisible(false);
            buttonCopy.setDisable(true);
            buttonDelete.setDisable(true);
        }
        this.selectedImageName = name;
    }
    
}
