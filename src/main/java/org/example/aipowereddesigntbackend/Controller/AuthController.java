package org.example.aipowereddesigntbackend.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.example.aipowereddesigntbackend.Model.Account;
import org.example.aipowereddesigntbackend.Repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication" , description = "this endpoint are for the authentication purpose by using only the third party like Google or Github")
public class AuthController {

    private final AccountRepository accountRepository;

    public AuthController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @GetMapping("/google")
    @Operation(
            summary = "Continue with Google",
            description = "Redirects the user to Google OAuth2 authentication."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirected to Google authentication"
            )
    })
    public void googleLogin(HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                "/oauth2/authorization/google"
        );
    }


    @GetMapping("/github")
    @Operation(
            summary = "Continue with GitHub",
            description = "Redirects the user to GitHub OAuth2 authentication."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirected to GitHub authentication"
            )
    })
    public void githubLogin(HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                "/oauth2/authorization/github"
        );
    }


    @GetMapping("/me")
    @Operation(
            summary = "View profile",
            description = "Authenticated user can return his/her  profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Unauthorized/Forbidden to view someone's profile")
    })
    public ResponseEntity<Account> getProfile(@AuthenticationPrincipal OAuth2User user) {

        String username = user.getAttribute("email");

        return ResponseEntity.ok(accountRepository.findAccountByUserName(username));

        }



}
