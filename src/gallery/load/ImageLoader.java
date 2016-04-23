package gallery.load;

import gallery.GalleryImage;
import gallery.GalleryManager;
import java.io.File;

import gallery.GalleryNode;
import gallerydemo.imageView.ImageViewContainerController;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
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
        List<GalleryImage> images = gallery.getImageList();

        for (int i = 0; i < images.size() && !isCancelled(); i++) {
            GalleryImage image = images.get(i);
            ImageView iv = this.loadOrCreateThumbnail(image.file);
            
            Platform.runLater(() -> {
                if (!isCancelled())
                    imagePane.getChildren().add(new ImageViewContainerController(image, iv.getImage()));
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
