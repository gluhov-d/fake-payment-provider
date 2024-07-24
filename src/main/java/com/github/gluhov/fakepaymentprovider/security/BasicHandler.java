package com.github.gluhov.fakepaymentprovider.security;

import com.github.gluhov.fakepaymentprovider.exception.UnauthorizedException;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Base64;

@NoArgsConstructor
public class BasicHandler {

    public Mono<VerificationResult> check(String accessToken) {
        return Mono.just(verify(accessToken))
                .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
    }

    private VerificationResult verify(String accessToken) {
        String[] credentials = getCredentials(accessToken);
        return new VerificationResult(credentials[0], credentials[1]);
    }

    private String[] getCredentials(String token) {
        String credentials = new String(Base64.getDecoder().decode(token));
        return credentials.split(":", 2);
    }

    public static class VerificationResult {
        public String merchantId;
        public String secretKey;

        public VerificationResult(String merchantId, String secretKey) {
            this.merchantId = merchantId;
            this.secretKey = secretKey;
        }
    }
}