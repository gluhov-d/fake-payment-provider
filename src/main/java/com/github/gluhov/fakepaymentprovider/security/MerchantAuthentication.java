package com.github.gluhov.fakepaymentprovider.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public class MerchantAuthentication {

    public static Mono<Authentication> create(BasicHandler.VerificationResult verificationResult) {
            CustomPrincipal principal = new CustomPrincipal(null, verificationResult.merchantId, verificationResult.secretKey);
            return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, new ArrayList<>()));
        }
}