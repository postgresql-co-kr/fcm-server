package com.ecobridge.fcm.server;

import com.ecobridge.fcm.server.entity.FcmLogEntity;
import com.ecobridge.fcm.server.enums.FcmDevice;
import com.ecobridge.fcm.server.repository.FcmLogEntityRepository;
import com.ecobridge.fcm.server.repository.FcmMsgEntityRepository;
import com.ecobridge.fcm.server.repository.FcmMsgQueryRepository;
import com.ecobridge.fcm.server.task.FcmDbPushTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class JpaTransactionTest {

    @Autowired
    FcmMsgQueryRepository fcmMsgQueryRepository;
    @Autowired
    FcmLogEntityRepository fcmLogEntityRepository;
    @Autowired
    FcmMsgEntityRepository fcmMsgEntityRepository;

    @Autowired
    FcmDbPushTask fcmDbService;

    @Autowired
    Environment env;

    @Test
    void queryTest() {
        transactionTest();
    }


    @Transactional
    void transactionTest() {
        FcmLogEntity fcmlog = FcmLogEntity.builder().appName("ecobridgeapp")
                                          .deviceType(FcmDevice.AOS.name())
                                          .successYn("Y")
                                          .fcmToken("sdkflasd")
                                          .build();
        fcmLogEntityRepository.saveAndFlush(fcmlog);
        log.debug("{}", fcmLogEntityRepository.findAll());
    }

    @Transactional
    void tr1() {
        log.debug("tr1 ==========================");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void tr2() {
        log.debug("tr2 ==========================");
    }

    @Transactional
    void tr3() {
        log.debug("tr3 ==========================");
    }

}
