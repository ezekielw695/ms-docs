package com.ezekielwong.ms.docs.domain.request.client.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequesterInfo implements Serializable {

    private String id;

    private String name;

    private String email;

    private Manager manager;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Manager implements Serializable {

        private String id;

        private String name;

        private String email;
    }
}
