package gallery.load;

import gallery.GalleryImage;
import gallery.GalleryManager;
import java.io.File;

import gallery.GalleryNode;
import gallerydemo.GalleryDemoViewController;
import gallerydemo.imageView.ImageViewContainerController;
import gallerydemo.task.TaskController;
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
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

public class ImageLoader extends Task {

    private final GalleryDemoViewController controller;
    private final FlowPane imagePane;
    private final GalleryNode gallery;
    private final TaskController task;
    private final boolean reload;

    public ImageLoader (GalleryDemoViewController controller, GalleryNode gallery, FlowPane imagePane, TaskController task, boolean reload) {
        this.controller = controller;
        this.imagePane = imagePane;
        this.gallery = gallery;
        this.task = task;
        this.reload = reload;
    }

    @Override
    protected Object call() throws Exception {

        gallery.createThumbnailFolder();
        List<GalleryImage> images = gallery.getImageList(this.reload);
        
        Platform.runLater(() -> {
           this.task.setProgress(0, images.size());
        });

        for (int i = 0; i < images.size() && !isCancelled(); i++) {
            GalleryImage image = images.get(i);
            Image iv = this.loadOrCreateThumbnail(image.file);
            
            System.out.println("Loading image: " + image.file.getName());
            
            final int progress = i;
            Platform.runLater(() -> {
                if (!isCancelled()) {
                    imagePane.getChildren().add(new ImageViewContainerController(controller, image, iv));
                    task.setProgress(progress, images.size());
                }
            });
        }
        
        Platform.runLater(() -> {
            this.task.delete();
        });
        return null;
    }

    public Image loadOrCreateThumbnail(File imagePath) {
        
        if (!GalleryManager.IMAGE_FILE_PATTERN.matcher(imagePath.getName()).matches()) {
            ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(imagePath);
            BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
            icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        }

        File thumbnailPath = new File(imagePath.getParent() + "/" + GalleryManager.THUMBNAIL_FOLDER + "/" + imagePath.getName());

        if (thumbnailPath.exists() && thumbnailPath.isFile()) {
            return new Image("file:" + thumbnailPath.getPath());
        }

        Image image = new Image("file:" + imagePath.getPath(), -1, 100, true, false, false);

        ImageView imageView = new ImageView(image);

        BufferedImage bImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
        try {
            ImageIO.write(bImage, "png", thumbnailPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return image;
    }
}
