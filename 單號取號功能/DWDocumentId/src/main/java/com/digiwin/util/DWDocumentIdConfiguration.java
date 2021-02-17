package com.digiwin.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DWDocumentIdConfiguration {

    @Bean
    public DocumentIdGenerator documentIdGenerator() {
        return new DocumentIdGenerator();
    }

    @Bean
    public DocumentIdFinder documentIdFinder() {
        return new DocumentIdFinder();
    }
}
