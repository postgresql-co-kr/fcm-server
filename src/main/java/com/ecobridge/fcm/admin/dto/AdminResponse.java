package com.ecobridge.fcm.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse implements Serializable {
    @Builder.Default
    private boolean isSuccessful = false;
    @Builder.Default
    private String errorCode = "";
    @Builder.Default
    private String message = "";

}
