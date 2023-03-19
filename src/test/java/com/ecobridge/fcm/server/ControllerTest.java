package com.ecobridge.fcm.server;

import org.junit.jupiter.api.Test;
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
