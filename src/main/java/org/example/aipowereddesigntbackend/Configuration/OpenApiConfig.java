package org.example.aipowereddesigntbackend.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("ArchPilot")
                        .version("1.0")
                        .description("""
                                Have wondered how to get the free space either restaurant ,offices , weeding gardens on time .
                                Space sync provides the ready-to consume API for reservation
                                
                                [Continue with Google](http://localhost:8082/oauth2/authorization/google)
                                [Continue with Github](http://localhost:8082/oauth2/authorization/github)
                                
                                """)
                        .contact(new Contact()
                                .url("https://archPilot.vercel.app")
                                .name("ArchPilot."))
                        .license(new License()
                                .name("Apache 2.0")));

    }
}
