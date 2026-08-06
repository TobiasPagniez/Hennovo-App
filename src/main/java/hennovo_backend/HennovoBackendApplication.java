package hennovo_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class HennovoBackendApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        setPropertyFromDotenvIfAbsent(dotenv, "JWT_SECRET");
        setPropertyFromDotenvIfAbsent(dotenv, "JWT_EXPIRATION");
        setPropertyFromDotenvIfAbsent(dotenv, "DB_URL");
        setPropertyFromDotenvIfAbsent(dotenv, "DB_USERNAME");
        setPropertyFromDotenvIfAbsent(dotenv, "DB_PASSWORD");

        SpringApplication.run(
                HennovoBackendApplication.class,
                args
        );
    }

    private static void setPropertyFromDotenvIfAbsent(
            Dotenv dotenv,
            String propertyName) {

        if (System.getProperty(propertyName) != null) {
            return;
        }

        String value = dotenv.get(propertyName);
        if (value != null) {
            System.setProperty(propertyName, value);
        }
    }
}
