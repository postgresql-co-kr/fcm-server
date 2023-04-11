/*
 * Copyright 2023 jinyoonoh@gmail.com (postgresql.co.kr, ecobridge.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecobridge.fcm.server.task;

import com.ecobridge.fcm.common.config.FcmPropsConfig;
import com.ecobridge.fcm.common.util.EnumFinder;
import com.ecobridge.fcm.common.util.IntervalParser;
import com.ecobridge.fcm.server.dto.FailureToken;
import com.ecobridge.fcm.server.dto.FcmMessage;
import com.ecobridge.fcm.server.entity.FcmMsgEntity;
import com.ecobridge.fcm.server.enums.FcmDevice;
import com.ecobridge.fcm.server.repository.FcmMsgQueryRepository;
import com.ecobridge.fcm.server.service.FcmApiService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class FcmDbPushTask {

    private final FcmMsgQueryRepository fcmMsgQueryRepository;
    private final FcmPropsConfig fcmPropsConfig;
    private final FcmApiService fcmApiService;

    private final PlatformTransactionManager transactionManager;
    private final Environment env;

    private ScheduledExecutorService executor;
    private Map<String, Future<?>> futures = new HashMap<>();


    @Scheduled(fixedRateString = "${fcm.db-scrape.scheduled-fixed-rate:5000}")
    public void scheduleFcmFromDb() throws ExecutionException, InterruptedException {
        List<FcmPropsConfig.FcmApp> fcmApps = fcmPropsConfig.getApps();

        for (FcmPropsConfig.FcmApp fcmApp : fcmApps) {

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
