import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EnvLoader {
    private EnvLoader() {
    }
    public static Properties load(){
        Properties properties = new Properties();
        try (InputStream input = EnvLoader.class.getClassLoader().getResourceAsStream("config.env")) {
            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;

    }





    public static Properties load22(){
        var props = new Properties();
        var envFile = Paths.get("src/config.env");
        try (var inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props;
    }


}


