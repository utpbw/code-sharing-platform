package platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/** Entry point for the code-sharing web service. */
@SpringBootApplication
@RestController
public class CodeSharingPlatform {

    /** Launches the embedded server so the service is reachable without an external container. */
    public static void main(String[] args) {
        SpringApplication.run(CodeSharingPlatform.class, args);
    }

}
