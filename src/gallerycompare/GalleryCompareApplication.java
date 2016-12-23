/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerycompare;

import gallery.GalleryNode;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author fabian
 */
public class GalleryCompareApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        
        /*GalleryNode gallery1 = new GalleryNode(new File("galleries/gallery1/gallery.json"));
        GalleryNode gallery2 = new GalleryNode(new File("/home/fabian/Dokumente/Projekte/Java/FileCompare/remote/gallery1/gallery.json"));

        Parent root = new GalleryCompareView(gallery1, gallery2);//FXMLLoader.load(getClass().getResource("GalleryCompareView.fxml"));

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Gallery Comparison");
        primaryStage.setScene(scene);
        primaryStage.show();*/
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //launch(args);
        
        String regex = "[^\\.][\\w\\s\\.#\\-]+\\.(mov|MOV|mp4|MP4|avi|AVI)$";
        String fileName = "FILE0011.MOV";
        
        System.out.println("" + fileName.matches(regex));
        
        System.out.println(" -> " + new Date().getTime());
        
        System.exit(0);
    }

}
