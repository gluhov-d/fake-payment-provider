package com.github.gluhov.fakepaymentprovider.security;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@AllArgsConstructor
public class CustomAuthenticationConverter implements ServerAuthenticationConverter {
    private final BasicHandler basicHandler;
    private static final String HEADER_PREFIX = "Basic ";
    private static final Function<String, Mono<String>> getBasicValue = authValue -> Mono.justOrEmpty(authValue.substring(HEADER_PREFIX.length()));

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return extractHeader(exchange)
                .flatMap(getBasicValue)
                .flatMap(basicHandler::check)
                .flatMap(MerchantAuthentication::create);
    }

    private Mono<String> extractHeader(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION));
    }
}