package gallery.load;

import gallery.GalleryManager;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import gallery.GalleryNode;
import gallerydemo.imageView.ImageViewContainerController;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javax.imageio.ImageIO;

public class ImageLoader extends Task {

    private final FlowPane imagePane;
    private final GalleryNode gallery;

    public ImageLoader (GalleryNode gallery, FlowPane imagePane) {
        this.imagePane = imagePane;
        this.gallery = gallery;
    }

    @Override
    protected Object call() throws Exception {
        
        /*Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    imagePane.getChildren().clear();
                }
        });*/
        gallery.createThumbnailFolder();
        File[] files = gallery.listImages();

        for (int i = 0; i < files.length && !isCancelled(); i++) {
            File image = files[i];
            ImageView iv = this.loadOrCreateThumbnail(image);
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (!isCancelled())
                    imagePane.getChildren().add(new ImageViewContainerController(iv.getImage(), 1200, 800));
                }
            });
        }
        return null;
    }

    public ImageView loadOrCreateThumbnail(File imagePath) {

        File thumbnailPath = new File(imagePath.getParent() + "/" + GalleryManager.THUMBNAIL_FOLDER + "/" + imagePath.getName());

        if (thumbnailPath.exists() && thumbnailPath.isFile()) {
            return new ImageView(new Image("file:" + thumbnailPath.getPath()));
        }

        Image image = new Image("file:" + imagePath.getPath(), -1, 100, true, false, false);

        ImageView imageView = new ImageView(image);
        //imageView.setFitHeight(100);
        //imageView.setPreserveRatio(true);

        BufferedImage bImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
        try {
            ImageIO.write(bImage, "png", thumbnailPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return imageView;
    }
}
