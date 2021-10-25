package com.example.demo.restTemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigBean {

    /**
     * RestTemplateオブジェクトを作成する
     * @return RestTemplateオブジェクト
     */
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}