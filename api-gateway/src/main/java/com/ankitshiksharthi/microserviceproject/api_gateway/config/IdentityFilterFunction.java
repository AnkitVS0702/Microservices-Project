package com.ankitshiksharthi.microserviceproject.api_gateway.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

public class IdentityFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            String keycloakId = jwt.getSubject();

            ServerRequest modifiedRequest = ServerRequest.from(request)
                    .headers(httpHeaders -> {
                        if (email != null) {
                            httpHeaders.add("X-User-Email", email);
                        }
                        if (keycloakId != null) {
                            httpHeaders.add("X-User-Keycloak-Id", keycloakId);
                        }
                    })
                    .build();

            return next.handle(modifiedRequest);
        }
        return next.handle(request);
    }
}
