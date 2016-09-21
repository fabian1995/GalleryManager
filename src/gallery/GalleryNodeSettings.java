/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery;

/**
 *
 * @author fabian
 */
public class GalleryNodeSettings {
    
    public final GalleryType type;
    
    // Default value is null
    public GalleryNode compareNode = null;
    
    // Default value is false
    public boolean processing = false;
    
    public enum GalleryType {
        GALLERY, COLLECTION, TRUNK
    }
    
    public enum GalleryStatus {
        OFFLINE, UPTODATE, SERVERNEWER, LOCALNEWER
    }

    public GalleryNodeSettings(GalleryType type) {
        this.type = type;
    }
}
