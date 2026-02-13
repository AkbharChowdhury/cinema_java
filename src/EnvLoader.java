import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading environment properties from config.env.
 */
public final class EnvLoader {

    private EnvLoader() {}

    /**
     * Loads properties from the "config.env" file in the classpath.
     *
     * @return Properties loaded from config.env
     * @throws RuntimeException if the file is missing or cannot be read
     */
    public static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = EnvLoader.class.getClassLoader().getResourceAsStream("config.env")) {
            if (input == null) {
                throw new RuntimeException("config.env not found in classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.env", e);
        }

        return properties;
    }
}
