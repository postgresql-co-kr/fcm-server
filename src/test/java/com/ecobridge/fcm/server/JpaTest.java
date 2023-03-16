package com.ecobridge.fcm.server;

import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import com.ecobridge.fcm.server.repository.FcmMsgEntityRepository;
import com.ecobridge.fcm.server.repository.FcmMsgQueryRepository;
import com.ecobridge.fcm.server.service.FcmDbService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
@Slf4j
public class JpaTest {

    @Autowired
    FcmMsgQueryRepository fcmMsgQueryRepository;

    @Autowired
    FcmMsgEntityRepository fcmMsgEntityRepository;

    @Autowired
    FcmDbService fcmDbService;

    @Autowired
    Environment env;

    @Test
    void queryTest() {
        log.info(env.getProperty("spring.datasource.url"));
        //@transactional 존재해야지 for update
//        List<FcmMsgEntity> list = fcmMsgQueryRepository.findTargetList("ecobridgeapp",
//                Timestamp.valueOf(LocalDateTime.now().minusSeconds(60)));

        fcmDbService.sendFcmFromDb("ecobridge");

//        List<FcmMsgEntity> list2 = fcmMsgQueryRepository.findNextList("ecobridgeapp",
//                list.get(list.size()-6).getMsgKey());

    }
}
