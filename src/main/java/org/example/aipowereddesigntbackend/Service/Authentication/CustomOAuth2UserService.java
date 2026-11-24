
package org.example.aipowereddesigntbackend.Service.Authentication;

import org.example.aipowereddesigntbackend.Enum.Role;
import org.example.aipowereddesigntbackend.Model.Account;
import org.example.aipowereddesigntbackend.Repository.AccountRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final RestClient restClient = RestClient.create();
    private final AccountRepository repository;

    public CustomOAuth2UserService(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(request);

        String provider = request.getClientRegistration()
                .getRegistrationId()
                .toUpperCase();

        Map<String, Object> attributes =
                new HashMap<>(oauthUser.getAttributes());

        String providerId;
        String email;
        String name;
        String avatarUrl;

        switch (provider) {

            case "GOOGLE" -> {
                providerId = stringValue(attributes.get("sub"));
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                avatarUrl = (String) attributes.get("picture");
            }

            case "GITHUB" -> {
                providerId = stringValue(attributes.get("id"));
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                avatarUrl = (String) attributes.get("avatar_url");

                if (name == null || name.isBlank()) {
                    name = (String) attributes.get("login");
                }

                if (email == null || email.isBlank()) {
                    email = fetchGithubEmail(
                            request.getAccessToken().getTokenValue()
                    );
                }
            }

            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider"),
                    "Unsupported OAuth2 provider: " + provider
            );
        }

        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_provider_id"),
                    "Provider user ID is missing"
            );
        }

        // Find an existing account or create a new one
        Account account = repository
                .findByProvider(provider)
                .orElseGet(() -> {
                    Account newAccount = new Account();
                    newAccount.setProvider(provider);
                    newAccount.setRole(Role.OWNER);

                    return newAccount;
                });

        account.setUserName(email);
        account.setFullName(name);
        account.setAvatarUrl(avatarUrl);


        Account savedAccount = repository.save(account);


        attributes.put("provider", provider);
        attributes.put("providerId", providerId);
        attributes.put("accountId", savedAccount.getId().toString());
        attributes.put("email", email == null ? "" : email);
        attributes.put("name", name == null ? "" : name);
        attributes.put(
                "avatarUrl",
                avatarUrl == null ? "" : avatarUrl
        );

        return new DefaultOAuth2User(
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + savedAccount.getRole().name()
                        )
                ),
                attributes,
                "providerId"
        );
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private String fetchGithubEmail(String accessToken) {

        try {
            List<?> emails = restClient.get()
                    .uri("https://api.github.com/user/emails")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + accessToken
                    )
                    .header(
                            HttpHeaders.ACCEPT,
                            "application/vnd.github+json"
                    )
                    .retrieve()
                    .body(List.class);

            if (emails == null) {
                return null;
            }

            for (Object item : emails) {

                if (item instanceof Map<?, ?> entry
                        && Boolean.TRUE.equals(entry.get("primary"))
                        && Boolean.TRUE.equals(entry.get("verified"))) {

                    return (String) entry.get("email");
                }
            }

            for (Object item : emails) {

                if (item instanceof Map<?, ?> entry
                        && Boolean.TRUE.equals(entry.get("verified"))) {

                    return (String) entry.get("email");
                }
            }

            return null;

        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("github_email_error"),
                    "Unable to retrieve GitHub email"
            );
        }
    }
}
