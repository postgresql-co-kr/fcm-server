package com.ecobridge.fcm.server;

import com.ecobridge.fcm.server.service.JasyptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(locations="classpath:test.properties")
public class ControllerTest {

    @LocalServerPort
    int port;

    @Test
    void multicastTest() {

    }
}
