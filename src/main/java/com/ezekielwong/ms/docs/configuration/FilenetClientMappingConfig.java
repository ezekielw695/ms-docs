package com.ezekielwong.ms.docs.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Configuration
public class FilenetClientMappingConfig {

    @Value("#{'${filenet.doc-prop.prop-name-list}'.split(',')}")
    private List<String> propNameList;

    @Value("#{'${client.field-data.field_id-list}'.split(',')}")
    private List<String> fieldDataValueList;

    /**
     * Map each Filenet propName to its corresponding Client fieldId
     *
     * @return The mapped entries
     */
    @Bean
    public Map<String, String> mapping() {

        // Number of keys should match the number of values
        if (propNameList.size() != fieldDataValueList.size()) {

            log.error("Lists are of different sizes");
            throw new IllegalArgumentException(String.format("%d keys mapped to %d values",
                    propNameList.size(), fieldDataValueList.size()));
        }

        return IntStream
                .range(0, propNameList.size())
                .boxed()
                .collect(Collectors.toMap(propNameList::get, fieldDataValueList::get));
    }
}
