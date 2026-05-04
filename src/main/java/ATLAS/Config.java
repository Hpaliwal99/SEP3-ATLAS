package ATLAS;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = new java.io.FileInputStream("src/main/java/ATLAS/config.properties")) {
            if (is == null) throw new RuntimeException("config.properties not found");
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public static String kbPath()              { return props.getProperty("kb.path"); }
    public static String rulesPath()           { return props.getProperty("rules.path"); }
    public static int defaultN()               { return Integer.parseInt(props.getProperty("analogy.default_n")); }
    public static double inferencePower()   { return Double.parseDouble(props.getProperty("inference_power")); }
}
