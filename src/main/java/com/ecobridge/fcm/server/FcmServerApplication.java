package com.ecobridge.fcm.server;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableEncryptableProperties
@EnableAsync
public class FcmServerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(FcmServerApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);
    }

}
