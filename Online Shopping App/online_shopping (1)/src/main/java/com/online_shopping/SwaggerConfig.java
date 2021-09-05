package com.online_shopping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Configuration
@EnableSwagger2
public class SwaggerConfig {


    public static final Contact DEFAULT_CONTACT = new Contact(
            "Aman Pareek", "https://github.com/Aman-Pareek-TTN",
            "aman.pareek@tothenew.com");


    public static final ApiInfo DEFAULT_API_INFO =  new ApiInfoBuilder().title("Spring Boot REST API")
            .description("Spring Boot application for Online Shopping Store using REST APIs")
            .version("1.0")
            .termsOfServiceUrl("urn:tos")
            .contact(DEFAULT_CONTACT)
            .license("Apache 2.0")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
            .build();
    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMERS = new HashSet<String>(Arrays.asList("applicatioon/json","application/xml"));

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(DEFAULT_API_INFO)
                .produces(DEFAULT_PRODUCES_AND_CONSUMERS)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMERS);
    }
}
