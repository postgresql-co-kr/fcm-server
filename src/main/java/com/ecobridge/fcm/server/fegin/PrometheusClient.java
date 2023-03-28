package com.ecobridge.fcm.server.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "prometheus", url = "${prometheus.api.url}")
public interface PrometheusClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/query_range")
    String queryRange(@RequestParam("query") String query,
                                  @RequestParam("start") String start,
                                  @RequestParam("end") String end,
                                  @RequestParam("step") String step);


    class QueryResult {
        String status;
        Data data;

        static class Data {
            String resultType;
            List<Result> result;
        }

        static class Result {
            String metric;
            List<List<Double>> value;
        }

        List<Result> getResults() {
            return data.result;
        }
    }
}
