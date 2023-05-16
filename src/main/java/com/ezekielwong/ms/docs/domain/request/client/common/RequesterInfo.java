package com.ezekielwong.ms.docs.domain.request.client.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequesterInfo implements Serializable {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Manager")
    private Manager manager;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Manager implements Serializable {

        @JsonProperty("Id")
        private String id;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Email")
        private String email;
    }
}
