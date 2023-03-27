package com.ecobridge.fcm.server.task;

import com.ecobridge.fcm.common.util.EnumFinder;
import com.ecobridge.fcm.common.util.IntervalParser;
import com.ecobridge.fcm.server.config.FcmPropsConfig;
import com.ecobridge.fcm.server.dto.FailureToken;
import com.ecobridge.fcm.server.dto.FcmApp;
import com.ecobridge.fcm.server.dto.FcmMessage;
import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import com.ecobridge.fcm.server.enums.FcmDevice;
import com.ecobridge.fcm.server.repository.FcmMsgEntityRepository;
import com.ecobridge.fcm.server.repository.FcmMsgQueryRepository;
import com.ecobridge.fcm.server.service.FcmApiService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

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

    private final PlatformTransactionManager transactionManager;
    private final Environment env;

    private ScheduledExecutorService executor;
    private Map<String, Future<?>> futures = new HashMap<>();

    public FcmDbPushTask(FcmMsgQueryRepository fcmMsgQueryRepository, FcmPropsConfig fcmPropsConfig, FcmApiService fcmApiService, FcmMsgEntityRepository fcmMsgEntityRepository, PlatformTransactionManager transactionManager, Environment env) {
        this.fcmMsgQueryRepository = fcmMsgQueryRepository;
        this.fcmPropsConfig = fcmPropsConfig;
        this.fcmApiService = fcmApiService;
        this.transactionManager = transactionManager;
        this.env = env;
        this.executor = Executors.newScheduledThreadPool(env.getProperty("db.scrape.thread.pool.size", Integer.class, 5));

    }

    @Scheduled(fixedRateString = "${db.scheduled.fixed-rate:5000}")
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
                            } catch (Exception e) {
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
        List<FcmMsgEntity> targetList = fcmMsgQueryRepository.findTargetList(appName, LocalDateTime.now().minusSeconds(minusSeconds));
        send(targetList);
    }

    private void send(List<FcmMsgEntity> targetList) throws FirebaseMessagingException {
        if (targetList == null || targetList.isEmpty()) {
            log.info("The number of Fcm push targets(db) is zero.");
            return;
        }

        List<FcmMessage> msgs = targetList.stream().map(vo -> {
            FcmMessage.FcmMessageBuilder builder = FcmMessage.builder()
                                                             .appName(vo.getAppName())
                                                             .title(vo.getTitle())
                                                             .body(vo.getBody())
                                                             .image(vo.getImage())
                                                             .token(vo.getFcmToken());
            if (StringUtils.hasLength(vo.getDeviceType())) {
                FcmDevice device = EnumFinder.findEnum(vo.getDeviceType(), FcmDevice.class);
                if (device != null) {
                    builder.device(device);
                }
            }
            return builder.build();

        }).toList();
        // PUSH Results
        List<FailureToken> results = fcmApiService.sendAll(msgs);
        // DB update
        List<FcmMsgEntity> fcmMsgEntities = new ArrayList<>();
        for (FcmMsgEntity fcmMsgEntity : targetList) {
            boolean isFailed = results.stream().anyMatch(vo -> vo.getToken().equals(fcmMsgEntity.getFcmToken()));
            if (isFailed) {
                fcmMsgEntity.setSuccessYn("N");
            } else {
                fcmMsgEntity.setSuccessYn("Y");
            }
            fcmMsgEntity.setPushTime(LocalDateTime.now());
            fcmMsgEntity.setPushYn("Y");
            fcmMsgEntities.add(fcmMsgEntity); // add list
        }

        // db update
        fcmMsgQueryRepository.batchUpdateMsg(fcmMsgEntities);

        // next 500
        FcmMsgEntity lastFcmMsg = targetList.get(targetList.size() - 1);
        List<FcmMsgEntity> nextTagetList = fcmMsgQueryRepository.findNextList(lastFcmMsg.getAppName(), lastFcmMsg.getMsgKey());
        send(nextTagetList);
    }

}
