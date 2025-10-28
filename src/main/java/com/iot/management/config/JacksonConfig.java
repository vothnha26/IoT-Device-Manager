package com.iot.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Cấu hình Hibernate5Module để xử lý lazy loading
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        // Cấu hình để force lazy loading
        hibernateModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);
        mapper.registerModule(hibernateModule);
        
        // Đăng ký module xử lý Java 8 date/time
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Cấu hình bỏ qua các thuộc tính không xác định
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return mapper;
    }
}