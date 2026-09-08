package hennovo_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
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
        setPropertyFromDotenvIfAbsent(dotenv, "LOGHUB_URL");
        setPropertyFromDotenvIfAbsent(dotenv, "LOGHUB_API_KEY");
        setPropertyFromDotenvIfAbsent(dotenv, "LOGHUB_APP_ID");

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
