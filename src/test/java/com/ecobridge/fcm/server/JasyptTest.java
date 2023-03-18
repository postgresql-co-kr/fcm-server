package com.ecobridge.fcm.server;

import com.ecobridge.fcm.server.service.JasyptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(locations="classpath:test.properties")
public class JasyptTest  {

    @Autowired
    JasyptService jasyptService;
    @Test
    void encryptTest() {
       
        System.out.println(jasyptService.encrypt("jdbc:postgresql://localhost:5432/fcm_db"));
        System.out.println(jasyptService.encrypt("fcm_app"));
        System.out.println(jasyptService.encrypt("fcm_app123#@!"));


    }
}
