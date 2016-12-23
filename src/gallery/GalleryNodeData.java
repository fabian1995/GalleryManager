/*
 * asdf
 * Each line should be prefixed with  * 
 */
package gallery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author fabian
 */
public class GalleryNodeData {
    
    public static final String GALLERY_JSON_CONF_NAME = "name";
    public static final String GALLERY_JSON_CONF_LASTCHANGED = "lastChanged";
    
    public String name;
    public Date lastChanged;
    
    private GalleryNodeData(String name, Date lastChanged) {
        this.name = name;
        this.lastChanged = lastChanged;
    }
    
    public static GalleryNodeData createTrunkNodeData() {
        return new GalleryNodeData("Bilder auf diesem Computer", new Date(0));
    }
    
    public static GalleryNodeData createCalleryNodeData(String name) {
        return new GalleryNodeData(name, new Date());
    }
    
    public static GalleryNodeData readGalleryNodeData(File config) {
        String rawJSON = null;
        
        try {
            rawJSON = new String(Files.readAllBytes(config.toPath()));
        } catch (Exception  e) {
            // TODO error handling
            Logger.getLogger("logfile").log(Level.SEVERE,
                    "[node] Error reading File {0}",
                    e.getMessage());
            e.printStackTrace();
        }

        JSONObject rootObject = new JSONObject(rawJSON);

        // Gallery Node Data properties
        String name;
        Date lastChanged;
        
        // Read name
        try {
            name = rootObject.getString(GALLERY_JSON_CONF_NAME);
        } catch (JSONException e) {
            Logger.getLogger("logfile").log(Level.SEVERE,
                    "[node] File {0}: Property name not defined",
                    config.getPath());
            name = config.getParentFile().getName();
        }
        
        // Read lastChanged
        try {
            lastChanged = new Date(rootObject.getLong(GALLERY_JSON_CONF_LASTCHANGED));
        } catch (JSONException e) {
            Logger.getLogger("logfile").log(Level.SEVERE,
                    "[node] File {0}: Property lastChanged not defined",
                    config.getPath());
            lastChanged = new Date(0);
        }

        return new GalleryNodeData(name, lastChanged);
    }
    
    public static void saveGalleryNodeData(File config, GalleryNodeData data) {
        JSONObject configObject = new JSONObject();
        
        configObject.put(GALLERY_JSON_CONF_NAME, data.name);
        configObject.put(GALLERY_JSON_CONF_LASTCHANGED, data.lastChanged.getTime());

        try (FileWriter configFile = new FileWriter(config)) {
            configFile.write(configObject.toString());
        } catch (IOException ex) {
            Logger.getLogger("logfile").log(Level.SEVERE,
                    // TODO error handling
                    "[node] Can not write fo file '{0}'",
                    config.getPath());
        }
    }
}
