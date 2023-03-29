package com.ecobridge.fcm.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class PrometheusQueryRangeResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Data data;


    @lombok.Data
    public static class Data {
        @JsonProperty("resultType")
        private String resultType;

        @JsonProperty("result")
        private List<Result> results;

    }

    @lombok.Data
    public static class Result {
        @JsonProperty("metric")
        private Map<String, Object> metric;

        @JsonProperty("values")
        private List<List<String>> values;

    }
}
