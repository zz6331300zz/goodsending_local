package com.goodsending.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  private static final String JWT_SCHEME_NAME = "jwtAuth";
  private static final String BEARER = "bearer";
  private static final String JWT = "JWT";
  public static final String VERSION = "v1.0.0";
  public static final String TITLE = "개발 강의 서비스 API 명세서";
  public static final String DESCRIPTION = "개발 강의 서비스 API 명세서입니다.";

  @Bean
  public OpenAPI openAPI() {

    Info info = new Info()
        .version(VERSION)
        .title(TITLE)
        .description(DESCRIPTION);

    SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT_SCHEME_NAME);
    Components components = new Components()
        .addSecuritySchemes(JWT_SCHEME_NAME, new SecurityScheme()
            .name(JWT_SCHEME_NAME)
            .type(SecurityScheme.Type.HTTP) // HTTP 방식
            .scheme(BEARER)
            .bearerFormat(JWT)); // 토큰 형식을 지정하는 임의의 문자(Optional)

    return new OpenAPI()
        .info(info)
        .addSecurityItem(securityRequirement)
        .components(components);
  }
}