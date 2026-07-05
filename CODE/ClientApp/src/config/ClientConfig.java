package config;

import java.io.InputStream;
import java.util.Properties;

public class ClientConfig {

    private static Properties properties = new Properties();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            InputStream input = ClientConfig.class.getResourceAsStream("config.properties");
            
            if (input == null) {
                System.out.println("[CONFIG] Cannot find client.properties. Using defaults.");
                return;
            }

            properties.load(input);
            input.close();
            System.out.println("[CONFIG] Loaded client.properties");

        } catch (Exception e) {
            System.out.println("[CONFIG] Error loading config: " + e.getMessage());
        }
    }

    public static String getString(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }

    public static int getInt(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return (value == null || value.trim().isEmpty()) ? defaultValue : Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}