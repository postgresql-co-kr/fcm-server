package com.ecobridge.fcm.admin.vo;

import com.ecobridge.fcm.server.vo.FailureToken;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class AdminResponse implements Serializable {
    @Builder.Default
    private boolean isSuccessful = false;
    @Builder.Default
    private String errorCode = "";
    @Builder.Default
    private String message = "";

}
