import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;


public class EnvLoader {
    private EnvLoader() {
    }


    public static Properties load() {
        try (InputStream input = EnvLoader.class.getClassLoader().getResourceAsStream("config.env")) {
            if (input == null) {
                throw new IllegalStateException("config.env not found on classpath");
            }

            Properties props = new Properties();
            props.load(input);
            return props;

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load config.env", e);
        }
    }


}


