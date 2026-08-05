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

        System.setProperty(
                "JWT_SECRET",
                dotenv.get("JWT_SECRET")
        );

        System.setProperty(
                "JWT_EXPIRATION",
                dotenv.get("JWT_EXPIRATION")
        );

        SpringApplication.run(
                HennovoBackendApplication.class,
                args
        );
    }
}
