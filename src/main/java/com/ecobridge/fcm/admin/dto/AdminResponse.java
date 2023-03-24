package com.ecobridge.fcm.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

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
