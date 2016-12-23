/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerysettings;

import gallery.GalleryManager;
import gallery.GalleryNode;
import gallery.GalleryNodeSettings;
import gallerydemo.settings.GallerySettings;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author fabian
 */
public class GallerySettingsViewController implements Initializable {

    private final GallerySettings settings;

    @FXML
    private TextField localDirField;
    @FXML
    private Button localDirButton;
    @FXML
    private CheckBox createGalleriesCheckbox;

    @FXML
    private TextField remoteDirField;
    @FXML
    private Button remoteDirButton;

    @FXML
    private Button saveButton;
    //@FXML private Button cancelButton;

    public GallerySettingsViewController(GallerySettings settings) {
        this.settings = settings;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Logger.getLogger("logfile").info("[init] GallerySettingsView");

        this.saveButton.setDisable(true);

        this.localDirField.setText(settings.getLocalGalleryLocation().getPath());
        this.remoteDirField.setText(settings.getRemoteGalleryLocation().getPath());

        this.localDirButton.setOnAction((ActionEvent) -> {
            DirectoryChooser fileChooser = new DirectoryChooser();
            fileChooser.setTitle("Ordner für Bilder auswählen");
            File dir = fileChooser.showDialog(null);
            if (dir != null) {
                this.saveButton.setDisable(!dir.isDirectory());
                settings.setLocalGalleryLocation(dir);
                this.localDirField.setText(dir.getPath());
            }
        });

        this.remoteDirButton.setOnAction((ActionEvent) -> {
            DirectoryChooser fileChooser = new DirectoryChooser();
            fileChooser.setTitle("Ordner zum Synchronisieren auswählen");
            File dir = fileChooser.showDialog(null);
            if (dir != null) {
                settings.setRemoteGalleryLocation(dir);
                this.remoteDirField.setText(dir.getPath());
            }
        });

        this.saveButton.setOnAction((ActionEvent) -> {
            this.saveButton.setDisable(true);

            this.settings.save();

            if (this.createGalleriesCheckbox.isSelected()) {
                System.out.println("Searching...");
                this.search(this.settings.getLocalGalleryLocation());
            }

            Stage stage = (Stage) this.saveButton.getScene().getWindow();
            stage.close();
        });
    }

    public void search(File directory) {
        List<String> path = new ArrayList<>();
        this.search(directory, path);
    }

    private void search(File directory, List<String> path) {

        boolean containsDirectory = false;

        System.out.println(" -> " + directory.getName());

        for (File f : directory.listFiles()) {

            if (f.isDirectory()) {
                path.add(f.getName());
                this.search(f, path);
                path.remove(f.getName());
                containsDirectory = true;
            }
        }

        if (!containsDirectory) {
            System.out.println(directory.getName() + " is gallery");
            // TODO Test this function
            GalleryNode newGallery = new GalleryNode(
                    new File(directory.getAbsolutePath() + "/" + GalleryManager.GALLERY_CONFIG_FILE_NAME),
                    new GalleryNodeSettings(GalleryNodeSettings.GalleryType.GALLERY)
            );
        }
    }
}
