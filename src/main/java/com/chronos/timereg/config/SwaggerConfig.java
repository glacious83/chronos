package com.chronos.timereg.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "OAuthPasswordFlow";

        return new OpenAPI()
                .info(new Info()
                        .title("Chronos API")
                        .description("Sample: OAuth2 password flow for user login in Swagger")
                        .version("1.0")
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .password(new OAuthFlow()
                                                        // The token endpoint that accepts user/password as ROPC
                                                        .tokenUrl("/oauth/token")
                                                        .scopes(new Scopes())
                                                )
                                        )
                        )
                );
    }
}
