package com.github.gluhov.fakepaymentprovider.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPrincipal implements Principal {
    private UUID uuid;
    private String merchantId;
    private String secretKey;

    @Override
    public String getName() {
        return merchantId;
    }
}