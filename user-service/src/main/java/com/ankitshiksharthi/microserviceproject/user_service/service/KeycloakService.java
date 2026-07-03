package com.ankitshiksharthi.microserviceproject.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakService {

    private final WebClient webClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public KeycloakService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Gets an admin access token using the service account (client credentials grant).
     */
    private String getAdminToken() {
        log.info("Requesting admin token from Keycloak...");

        Map<String, Object> response = webClient.post()
                .uri(keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("access_token")) {
            throw new RuntimeException("Failed to obtain admin token from Keycloak");
        }

        return (String) response.get("access_token");
    }

    /**
     * Creates a user in Keycloak and returns the Keycloak user ID.
     */
    public String createUser(String email, String password, String firstName, String lastName) {
        String adminToken = getAdminToken();

        Map<String, Object> userRepresentation = Map.of(
                "username", email,
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false
                ))
        );

        log.info("Creating user in Keycloak with email: {}", email);

        try {
            webClient.post()
                    .uri(keycloakServerUrl + "/admin/realms/" + realm + "/users")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(userRepresentation)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Failed to create user in Keycloak: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 409) {
                throw new RuntimeException("User with email " + email + " already exists in Keycloak");
            }
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage());
        }

        // Fetch the created user to get the Keycloak ID
        String keycloakUserId = getUserIdByEmail(adminToken, email);
        log.info("User created in Keycloak with ID: {}", keycloakUserId);

        // Assign the CUSTOMER role
        assignRealmRole(adminToken, keycloakUserId, "CUSTOMER");

        return keycloakUserId;
    }

    /**
     * Looks up a user by email and returns their Keycloak user ID.
     */
    private String getUserIdByEmail(String adminToken, String email) {
        List<Map> users = webClient.get()
                .uri(keycloakServerUrl + "/admin/realms/" + realm + "/users?email=" + email + "&exact=true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .block();

        if (users == null || users.isEmpty()) {
            throw new RuntimeException("User not found in Keycloak after creation");
        }

        return (String) users.get(0).get("id");
    }

    /**
     * Assigns a realm role to a user.
     */
    private void assignRealmRole(String adminToken, String userId, String roleName) {
        log.info("Assigning role '{}' to user '{}'", roleName, userId);

        // First get the role representation
        Map roleRepresentation = webClient.get()
                .uri(keycloakServerUrl + "/admin/realms/" + realm + "/roles/" + roleName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (roleRepresentation == null) {
            throw new RuntimeException("Role '" + roleName + "' not found in Keycloak");
        }

        // Assign the role to the user
        webClient.post()
                .uri(keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(roleRepresentation))
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Role '{}' assigned successfully to user '{}'", roleName, userId);
    }
}
