package util;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Prasad on 21-09-2018.
 */
public class FileUtility {

     static final Logger logger = Logger.getLogger("FileUtility".getClass().getName());
    private static Properties prop = new Properties();

    static {
        try {
            loadProperties();
        } catch (IOException e) {
            logger.error("unable to load configuration properties", e);
            throw new RuntimeException();
        }
    }

    private FileUtility() {
    }

    private static Properties loadProperties() throws IOException {

        URL url = getResource("/config.properties");
        try (InputStream input = new FileInputStream(url.getPath())) {
            prop.load(input);
        }

        return prop;
    }

    public static String transactionsFile() {

        return getPath("transactionFileLocation");
    }

    public static String outputFile() {
        return getPath("endofDaypositionLocation");
    }

    public static String getInputPath() {
        return getPath("startofdaypositionlocation");
    }

    public static String getPath(String fileType) {
        return "FileUtility".getClass().getResource(prop.getProperty(fileType)).getPath();

    }

    private static URL getResource(String loc) {
        return "FileUtility".getClass().getResource(loc);
    }
}
