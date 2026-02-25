package pl.miken.electionhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ElectionHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectionHandlerApplication.class, args);
    }

}
