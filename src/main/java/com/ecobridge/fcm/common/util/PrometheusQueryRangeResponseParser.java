package com.ecobridge.fcm.common.util;

import com.ecobridge.fcm.common.dto.PrometheusQueryRangeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class PrometheusQueryRangeResponseParser {
    private ObjectMapper mapper;

    public PrometheusQueryRangeResponseParser() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public PrometheusQueryRangeResponse parse(String json) throws IOException {
        return mapper.readValue(json, PrometheusQueryRangeResponse.class);
    }
}
