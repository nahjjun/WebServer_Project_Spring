package com.example.webserver_project.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // application.properties의 context-path 값을 주입받음 (없으면 기본값 "")
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    // Swagger 문서의 전반적인 구조를 설정하는 Bean
    @Bean
    public OpenAPI customOpenAPI() {
        // 1. 서버 URL 설정
        Server localServer = new Server();
        localServer.setUrl(contextPath);
        localServer.setDescription("Local Server");

        OpenAPI info = new OpenAPI()
                .addServersItem(localServer) // 서버 정보 추가
                // 2. JWT 인증 방식 설정
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP) // HTTP 인증 방식
                                                .scheme("bearer")               // bearer 스킴 사용: 토근을 가진 사람이 권한을 가짐
                                                .bearerFormat("JWT")            // JWT 토큰 사용 형식
                                )
                )
                // 3. 기본 문서 정보 설정
                .info(new Info()
                        .title("Swagger API 명세서")
                        .version("1.0")
                        .description("My Swagger"));
        return info;
    }

    // Swagger 문서를 그룹화하여 구분해서 보여주는 설정
    @Bean
    public GroupedOpenApi customGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("api")           // 그룹 이름 -> 단순 이름만 지정
                // *:api로 시작하는 바로 아래 단계만, **: api로 시작하는 모든 것들
                .pathsToMatch("/**")    // 전체 경로 포함
                .build();
    }

    // 회원 관련 API를 그룹화하여 확인
    @Bean
    public GroupedOpenApi memberGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("api-users")
                .pathsToMatch("/api/users/**")
                .build();
    }
}
