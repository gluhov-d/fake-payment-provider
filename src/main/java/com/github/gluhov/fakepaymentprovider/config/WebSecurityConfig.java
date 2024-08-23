package com.github.gluhov.fakepaymentprovider.config;

import com.github.gluhov.fakepaymentprovider.security.AuthenticationManager;
import com.github.gluhov.fakepaymentprovider.security.BasicHandler;
import com.github.gluhov.fakepaymentprovider.security.CustomAuthenticationConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((authz) -> authz
                        .pathMatchers("/api/v1/api-docs/**").permitAll()
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint((swe, e) -> {
                            log.error("IN securityWebFilterChain - unauthorized error: {}", e.getMessage());
                            return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                        })
                        .accessDeniedHandler((swe, e) -> {
                            log.error("IN securityWebFilterChain - access denied: {}", e.getMessage());
                            return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                        }))
                .addFilterAt(basicAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private AuthenticationWebFilter basicAuthenticationFilter(AuthenticationManager authenticationManager) {
        AuthenticationWebFilter basicAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        basicAuthenticationFilter.setServerAuthenticationConverter(new CustomAuthenticationConverter(new BasicHandler()));
        basicAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

        return basicAuthenticationFilter;
    }
}