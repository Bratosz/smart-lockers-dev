package pl.bratosz.smartlockers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.bratosz.smartlockers.property.FileStorageProperties;


@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class,
})
public class SmartLockersApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartLockersApplication.class, args);
    }

}


