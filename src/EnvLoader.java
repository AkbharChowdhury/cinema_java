import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvLoader {
    private EnvLoader() {
    }

    public static Properties load() {
        Properties properties = new Properties();
        try (InputStream input = EnvLoader.class.getClassLoader().getResourceAsStream("config.env")) {
            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;

    }
}


