package config;

import java.io.FileInputStream;
import java.util.Properties;

public class ClientConfig {

    private static Properties properties = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("CODE/ClientApp/src/config/config.properties");
            properties.load(fis);
            fis.close();

            System.out.println("[CONFIG] Loaded config.properties");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[CONFIG] Cannot load config.properties. Using default values.");
        }
    }

    public static String getString(String key, String defaultValue) {
        String value = properties.getProperty(key);

        if (value == null || value.trim().length() == 0) {
            return defaultValue;
        }

        return value;
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}