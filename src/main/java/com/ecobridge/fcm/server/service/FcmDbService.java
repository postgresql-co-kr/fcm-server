package com.ecobridge.fcm.server.service;

import com.ecobridge.fcm.server.config.FcmPropsConfig;
import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import com.ecobridge.fcm.server.repository.FcmMsgQueryRepository;
import com.ecobridge.fcm.server.utils.IntervalParser;
import com.ecobridge.fcm.server.vo.FcmApp;
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

    private final FcmPropsConfig fcmPropsConfig;


    public FcmDbService(FcmMsgQueryRepository fcmMsgQueryRepository, FcmPropsConfig fcmPropsConfig) {
        this.fcmMsgQueryRepository = fcmMsgQueryRepository;
        this.fcmPropsConfig = fcmPropsConfig;
    }

    @Transactional
    public void sendFcmFromDb(String appName) {
        List<FcmApp> fcmApps = fcmPropsConfig.getFcmApps();
        FcmApp fcmApp = fcmApps.stream().filter(vo -> vo.getName().equalsIgnoreCase(appName)).findFirst().orElse(null);
        if (fcmApp == null || !fcmApp.isDbPush()) {
            return;
        }
        long minusSeconds = IntervalParser.parse(fcmApp.getDbMinusTime());
        Timestamp scrapTime = Timestamp.valueOf(LocalDateTime.now().minusSeconds(minusSeconds));
        List<FcmMsgEntity> targetList = fcmMsgQueryRepository.findTargetList(appName, scrapTime);
    }

}
