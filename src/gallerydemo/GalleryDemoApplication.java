/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gallerydemo;

import utils.StdoutConsoleHandler;
import utils.LogFormatter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author fabian
 */
public class GalleryDemoApplication extends Application {

    public static String VERSION = "dev";
    public static String BUILD = "build";

    @Override
    public void start(Stage primaryStage) throws IOException {

        Logger.getLogger("logfile").info("[init] GalleryDemoApplication");

        Parent root = FXMLLoader.load(getClass().getResource("GalleryDemoView.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("GalleryManager [" + VERSION + " " + BUILD + "]");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Enumeration<URL> resources = GalleryDemoApplication.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            try {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                
                Attributes attr = manifest.getMainAttributes();
                
                String mainClass = attr.getValue("Implementation-Use-Version-No");
                
                if (mainClass != null && mainClass.equals("Yes")) {
                    GalleryDemoApplication:VERSION = attr.getValue("Implementation-Version");
                    GalleryDemoApplication:BUILD = "Build " + attr.getValue("Implementation-Build-No");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Get logger
        Logger logger = Logger.getLogger("logfile");
        logger.setUseParentHandlers(false);

        // Logging to console
        StdoutConsoleHandler sh = new StdoutConsoleHandler();
        sh.setFormatter(new LogFormatter(LogFormatter.MESSAGES_ONLY));
        logger.addHandler(sh);

        // Logging to logfile (if not prevented with console parameter)
        if (args.length > 0 && args[0].equals("nologfile")) {
            FileHandler fh;
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd_HHmm");
            try {
                // This block configure the logger with handler and formatter  
                fh = new FileHandler("logs/log_" + format.format(Calendar.getInstance().getTime()) + ".log");

                //SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(new LogFormatter(LogFormatter.MESSAGES_AND_TIME));
                logger.addHandler(fh);
            } catch (SecurityException | IOException e) {
                e.printStackTrace();
            }
        }

        launch(args);
    }

}
