package config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static Properties properties = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            InputStream input = ConfigManager.class.getResourceAsStream("config.properties");

            if (input == null) {
                System.out.println("[CONFIG] Cannot find config.properties. Using default values.");
                return;
            }

            properties.load(input);
            input.close();

            System.out.println("[CONFIG] Loaded /config/config.properties");

        } catch (Exception e) {
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
            String value = properties.getProperty(key);

            if (value == null || value.trim().length() == 0) {
                return defaultValue;
            }

            return Integer.parseInt(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }
}