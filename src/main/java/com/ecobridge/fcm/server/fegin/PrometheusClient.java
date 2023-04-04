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
package com.ecobridge.fcm.server.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "prometheus")
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
