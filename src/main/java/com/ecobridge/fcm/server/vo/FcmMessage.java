package com.ecobridge.fcm.server.vo;

import com.ecobridge.fcm.server.enums.FcmDevice;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class FcmMessage implements Serializable {
    /**
     * 복수건 Token
     */
    private List<String> tokens;
    /**
     * 단건 Fcm Token
     */
    private String token;
    /**
     * 사용자 데이터
     */
    private Map<String, String> data;  // 단말기에서 데이터를 취득하여 별도 표시(제목,내용,이미지를 다시 담아서 보내줌)

    // ---------------------
    // Notification
    // ---------------------
    private String title; //알림 제목
    private String body; //알림 본문
    private String image; //알림 이미지 IOS
    private FcmDevice device; // APN 오류
}
