package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Prasad on 21-09-2018.
 */
public class FileUtility {

    final static Logger logger = Logger.getLogger("FileUtility".getClass().getName());
    private static Properties prop = new Properties();

    static {

        try {
            loadProperties();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "unable to load configuration properties", e);
            throw new RuntimeException();
        }
    }

    private static Properties loadProperties() throws IOException {

        System.out.println(new File("").getAbsolutePath());
        URL url = "FileUtility".getClass().getResource("/config.properties");
        try (InputStream input = new FileInputStream(url.getPath())) {

            prop.load(input);
        }

        return prop;
    }

    public static String transactionsFile() {

        return "FileUtility".getClass().getResource(prop.getProperty("transactionFileLocation")).getPath();
    }

    public static String outputFile() {
        return "FileUtility".getClass().getResource(prop.getProperty("endofDaypositionLocation")).getPath();
    }

    public static String getInputPath() {
        return "FileUtility".getClass().getResource(prop.getProperty("startofdaypositionlocation")).getPath();
    }
}
