package com.ecobridge.fcm.server.service;

import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import com.ecobridge.fcm.server.repository.FcmMsgQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FcmDbService {
    private final FcmMsgQueryRepository fcmMsgQueryRepository;

    @Autowired
    private Environment env;

    public FcmDbService(FcmMsgQueryRepository fcmMsgQueryRepository) {
        this.fcmMsgQueryRepository = fcmMsgQueryRepository;
    }

    @Transactional
    public List<FcmMsgEntity> findTargetList(String appName) {
        Timestamp scrapTime = Timestamp.valueOf(LocalDateTime.now().minusSeconds(env.getProperty("db.send.scrap.minus.seconds", Integer.class, 5)));
        return fcmMsgQueryRepository.findTargetList(appName, scrapTime);
    }

}
