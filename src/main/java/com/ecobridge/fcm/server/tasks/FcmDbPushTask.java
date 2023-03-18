package com.ecobridge.fcm.server.tasks;

import com.ecobridge.fcm.server.config.FcmPropsConfig;
import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import com.ecobridge.fcm.server.repository.FcmMsgQueryRepository;
import com.ecobridge.fcm.server.service.FcmApiService;
import com.ecobridge.fcm.server.utils.IntervalParser;
import com.ecobridge.fcm.server.vo.FailureToken;
import com.ecobridge.fcm.server.vo.FcmApp;
import com.ecobridge.fcm.server.vo.FcmMessage;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
@EnableScheduling
public class FcmDbPushTask {
    private final FcmMsgQueryRepository fcmMsgQueryRepository;
    private final FcmPropsConfig fcmPropsConfig;
    private final FcmApiService fcmApiService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(10); // TODO: schedule pool size 설정 으로 고려
    private Map<String, Future<?>> futures = new HashMap<>();

    public FcmDbPushTask(FcmMsgQueryRepository fcmMsgQueryRepository, FcmPropsConfig fcmPropsConfig, FcmApiService fcmApiService) {
        this.fcmMsgQueryRepository = fcmMsgQueryRepository;
        this.fcmPropsConfig = fcmPropsConfig;
        this.fcmApiService = fcmApiService;
    }

    @Scheduled(fixedRate = 5000)
    public void scheduleFcmFromDb() throws ExecutionException, InterruptedException {
        List<FcmApp> fcmApps = fcmPropsConfig.getFcmApps();

        for (FcmApp fcmApp : fcmApps) {

            if (!fcmApp.isDbPush()) { // DB push false
                continue;
            }

            String appName = fcmApp.getName();
            Future<?> future = futures.get(appName);

            if (future == null || future.isDone()) { // 신규, 이전 작업이 끝난 경우

                Callable<Void> callable = () -> {
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            // 실행할 작업 내용
                            log.info("{} push fcm task started", appName);
                            long minusSeconds = IntervalParser.parse(fcmApp.getDbMinusTime());
                            try {
                                pushFcmFromDb(appName, minusSeconds); // push
                            } catch (FirebaseMessagingException e) {
                                log.error("==================================");
                                log.error("{} :", appName, e);
                                log.error("==================================");
                            }  catch (Exception e) {
                                log.error("push db:", e);
                            }

                            log.info("{} push fcm task finished", appName);
                        }
                    });

                    return null;
                };

                future = executor.submit(callable);
                futures.put(appName, future);


            } else {
                // 실행 중인 작업이 있으므로 스케줄링을 건너뜁니다.
                log.info("{} push fcm task is running..", appName);
            }
        }
    }


    private void pushFcmFromDb(String appName, long minusSeconds) throws FirebaseMessagingException {
        Timestamp scrapTime = Timestamp.valueOf(LocalDateTime.now().minusSeconds(minusSeconds));
        List<FcmMsgEntity> targetList = fcmMsgQueryRepository.findTargetList(appName, scrapTime);

        if (targetList.isEmpty()) {
            log.info("The number of Fcm push targets(db) is zero.");
            return;
        }

        List<FcmMessage> msgs = targetList.stream().map(vo -> {
            return FcmMessage.builder().appName(vo.getAppName())
                             .title(vo.getTitle())
                             .body(vo.getBody())
                             .image(vo.getImage())
                             .token(vo.getFcmToken())
                             .build();
        }).toList();
        // PUSH Results
        List<FailureToken> results = fcmApiService.sendAll(msgs);
        // DB update
        List<FcmMsgEntity> fcmMsgEntities = new ArrayList<>();
        for (FcmMsgEntity fcmMsgEntity: targetList) {
            boolean isFailed = results.stream().anyMatch(vo -> vo.getToken().equals(fcmMsgEntity.getFcmToken()));
            if (isFailed) {
                fcmMsgEntity.setSuccessYn("N");
            } else {
                fcmMsgEntity.setSuccessYn("Y");
            }
            fcmMsgEntity.setPushTime(Timestamp.valueOf(LocalDateTime.now()));
            fcmMsgEntity.setPushYn("Y");
            fcmMsgEntities.add(fcmMsgEntity); // add list
        }
        // db update
        fcmMsgQueryRepository.batchUpdate(fcmMsgEntities);
    }

}
