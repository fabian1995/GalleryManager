/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 *
 * @author fabian
 */
public class GalleryImage implements Comparable{
    
    public final File file;
    public final FileTime creationTime;
    public final FileTime lastModifiedTime;
    public final GalleryImageType type;
    
    public static enum GalleryImageType {
        IMAGE, VIDEO
    };
    
    public GalleryImage(File file) throws IOException {
        this.file = file;
        
        if (this.file.getName().matches(GalleryManager.IMAGE_FILE_REGEX))
            this.type = GalleryImageType.IMAGE;
        else
            this.type = GalleryImageType.VIDEO;
        
        BasicFileAttributes attr = Files.readAttributes(this.file.toPath(), BasicFileAttributes.class);
        this.creationTime = attr.creationTime();
        this.lastModifiedTime = attr.lastModifiedTime();
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof GalleryImage) {
            return this.creationTime.compareTo(((GalleryImage) o).creationTime);
        }
        else
            throw new UnsupportedOperationException(
                    "GalleryImage can not be compared to object or variable with value " + o
            );
    }
}
