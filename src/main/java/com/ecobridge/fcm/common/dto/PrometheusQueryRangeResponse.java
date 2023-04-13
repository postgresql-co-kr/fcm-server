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
package com.ecobridge.fcm.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
public class PrometheusQueryRangeResponse {
    @JsonProperty("code")
    @Builder.Default
    private String code = "00000";

    private String message;

    @JsonProperty("data")
    private Data data;

    public PrometheusQueryRangeResponse(String code, String message, Data data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @lombok.Data
    public static class Data {
        @JsonProperty("resultType")
        private String resultType;

        @JsonProperty("results")
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
