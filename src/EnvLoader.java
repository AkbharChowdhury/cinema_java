import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class EnvLoader {
    private EnvLoader() {
    }

    public static Properties load() {
        Properties props = new Properties();
        Path envFile = Paths.get("src/config.env");
        try (var inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);

        } catch (IOException ex) {
            System.err.println("There was an error fetching env data");
        }
        return props;
    }
}


