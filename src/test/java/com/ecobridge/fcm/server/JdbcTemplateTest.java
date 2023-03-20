package com.ecobridge.fcm.server;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class JdbcTemplateTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void queryTest() {
        String sql = """
                SELECT * 
                  FROM fcm_msg
                 WHERE app_name = 'ecobridgeapp'
                   AND msg_seq IN (1,2,3)
                """;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        log.debug("{}", list);
    }
}
