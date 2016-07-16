/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallerydemo.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author fabian
 */
public class GallerySettings {
    
    public static final String GLOBAL_CONFIG_FILE_NAME = "config.json";
    
    private static final String CONF_LOCAL_GALLERY = "localGalleryLocation";
    private static final String CONF_REMOTE_GALLERY = "remoteGalleryLocation";
    
    private File localGalleryLocation = null;
    private File remoteGalleryLocation = null;
    
    public File getLocalGalleryLocation() {
        return this.localGalleryLocation;
    }
    
    public void setLocalGalleryLocation(File f) {
        this.localGalleryLocation = f;
    }
    
    public File getRemoteGalleryLocation() {
        return this.remoteGalleryLocation;
    }
    
    public void setRemoteGalleryLocation(File f) {
        this.remoteGalleryLocation = f;
    }
    
    public boolean load() {

        File configFile = new File(GLOBAL_CONFIG_FILE_NAME);
        
        if (!configFile.exists()) {
            Logger.getLogger("logfile").warning("Config file not found");
            this.localGalleryLocation = new File("galleries");
            this.remoteGalleryLocation = new File("remote");
            return false;
        }
        
        String rawJSON = null;
        
        try {
            rawJSON = new String(Files.readAllBytes(configFile.toPath()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        JSONObject rootObject = new JSONObject(rawJSON);

        this.localGalleryLocation = new File(rootObject.getString(CONF_LOCAL_GALLERY));
        this.remoteGalleryLocation = new File(rootObject.getString(CONF_REMOTE_GALLERY));
        
        return this.localGalleryLocation.isDirectory();
    }
    
    public void save() {
        
        JSONObject configObject = new JSONObject();
        configObject.put(CONF_LOCAL_GALLERY, this.localGalleryLocation);
        configObject.put(CONF_REMOTE_GALLERY, this.remoteGalleryLocation);
        
        try (FileWriter configFile = new FileWriter(GLOBAL_CONFIG_FILE_NAME)) {
            configFile.write(configObject.toString());
        } catch (IOException ex) {
            Logger.getLogger("logfile").log(Level.SEVERE, null, ex);
        }
    }
}
