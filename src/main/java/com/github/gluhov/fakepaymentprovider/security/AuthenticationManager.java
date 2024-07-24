package com.github.gluhov.fakepaymentprovider.security;

import com.github.gluhov.fakepaymentprovider.exception.UnauthorizedException;
import com.github.gluhov.fakepaymentprovider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final MerchantService merchantService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return merchantService.findByIdAndSecretKey(principal.getMerchantId(), principal.getSecretKey())
                .switchIfEmpty(Mono.error(new UnauthorizedException("Authorization error")))
                .map(merchant -> {
                    principal.setUuid(merchant.getId());
                    return new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials(), authentication.getAuthorities());
                });
    }
}