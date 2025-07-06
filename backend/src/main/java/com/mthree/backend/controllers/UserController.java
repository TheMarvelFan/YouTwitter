package com.mthree.backend.controllers;

import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mthree.backend.models.ChannelDetails;
import com.mthree.backend.models.requests.LoginRequest;
import com.mthree.backend.models.User;
import com.mthree.backend.models.requests.StringRequest;
import com.mthree.backend.services.interfaces.JWTService;
import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.utils.ResponseType;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    UserService userService;
    JWTService jwtService;

    @Value("${security.jwt.expiration}")
    private long accessExpirationTime;

    @Value("${security.jwt.refresh_expiration}")
    private long refreshExpirationTime;

    @Value("${app.cookie.domain}")
    private String cookieDomain;

    public UserController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseType<User>> registerUser(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("userName") String username,
            @RequestPart("avatar") MultipartFile avatar,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        if (fullName == null || fullName.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Full Name"
            );
        }

        if (email == null || email.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Email"
            );
        }

        if (password == null || password.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Password"
            );
        }

        if (username == null || username.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Username"
            );
        }

        if (avatar == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Avatar Image"
            );
        }

        User newUser = userService.registerUser(
                fullName,
                email,
                password,
                username,
                avatar,
                coverImage
        );

        if (newUser == null) {
            throw new ErrorType(
                    400,
                    "Error creating user"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "User registered successfully",
                        newUser,
                        200
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseType<User>> loginUser(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        if (loginRequest == null) {
            throw new ErrorType(
                    400,
                    "Please provide credentials for logging in"
            );
        }

        if (
                (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty())
                        ||
                (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty())
        ) {
            throw new ErrorType(
                    400,
                    "Please provide valid username and password for logging in"
            );
        }

        Pair<User, String> userStringPair = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());

        if (userStringPair == null) {
            throw new ErrorType(
                    400,
                    "Error logging in user"
            );
        }

        User loggedInUser = userStringPair.getFirst();
        String accessToken = userStringPair.getSecond();

        setAccessTokenCookie(response, accessToken);
        System.out.println("Refresh token: " + loggedInUser.getRefreshToken());
        setRefreshTokenCookie(response, loggedInUser.getRefreshToken());

        return ResponseEntity.ok(
                new ResponseType<>(
                        "User logged in successfully",
                        loggedInUser,
                        200
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ) {
        System.out.println("Reaching controller for logout");

        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            // If no Authorization header, try to get from cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) { // Replace with your actual cookie name
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (token == null || token.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid User ID"
            );
        }

        String message = userService.logoutUser(token);

        clearAccessTokenCookie(response);
        clearRefreshTokenCookie(response);
        clearJSessionIdCookie(response);

        session.invalidate();

        return ResponseEntity.ok(
                Map.of(
                        "message", message
                )
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
//            @RequestBody String refreshTokenFromBody, // never do this
            @RequestBody(required = false) StringRequest refreshTokenFromBody,
            @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
            HttpServletResponse response
    ) {
        if (refreshTokenFromBody == null && refreshTokenFromCookie == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid refresh token"
            );
        }

        String token = "";

        if (refreshTokenFromBody != null && !refreshTokenFromBody.getField().isEmpty()) {
            token = refreshTokenFromBody.getField();
        } else if (!refreshTokenFromCookie.isEmpty()) {
            token = refreshTokenFromCookie;
        }

        Pair<String, String> accessAndRefreshTokens = userService.refreshToken(token);

        if (accessAndRefreshTokens == null) {
            throw new ErrorType(
                    400,
                    "Error refreshing token"
            );
        }

        setAccessTokenCookie(response, accessAndRefreshTokens.getFirst());
        setRefreshTokenCookie(response, accessAndRefreshTokens.getSecond());

        return ResponseEntity.ok(
                Map.of(
                        "accessToken", accessAndRefreshTokens.getFirst(),
                        "refreshToken", accessAndRefreshTokens.getSecond()
                )
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseType<User>> changePassword( // never accept string type request body when using JSON
//            @RequestBody String newPassword, // the whole damn JSON (from opening curly brace to closing) is turning into a string
            @RequestBody StringRequest newPassword
    ) {
        if (newPassword == null || newPassword.getField().isEmpty()) {
            throw new ErrorType(
                    400,
                    "New password cannot be null or empty"
            );
        }

        User user = userService.changePassword(newPassword.getField());

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Password changed successfully",
                        user,
                        200
                )
        );
    }

    @GetMapping("/current-user")
    public ResponseEntity<ResponseType<User>> getCurrentUser() {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            throw new ErrorType(
                    400,
                    "User not logged in"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Current user fetched successfully",
                        currentUser,
                        200
                )
        );
    }

    @PatchMapping("/update-account")
    public ResponseEntity<ResponseType<User>> updateAccount(
            @RequestBody User user
    ) {
        if (user == null) {
            throw new ErrorType(
                    400,
                    "User cannot be null"
            );
        }

        User updatedUser = userService.updateAccount(user);

        if (updatedUser == null) {
            throw new ErrorType(
                    400,
                    "Error updating user"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Account updated successfully",
                        updatedUser,
                        200
                )
        );
    }

    // implement watch history in future

    @GetMapping("/c/{userName}")
    public ResponseEntity<ResponseType<ChannelDetails>> getUserChannelProfile(
            @PathVariable String userName
    ) {
        if (userName == null || userName.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Username"
            );
        }

        ChannelDetails user = userService.getUserChannelProfile(userName);

        if (user == null) {
            throw new ErrorType(
                    400,
                    "User not found"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "User channel profile fetched successfully",
                        user,
                        200
                )
        );
    }

    @PatchMapping("/avatar")
    public ResponseEntity<ResponseType<User>> updateAvatar(
            @RequestPart("avatar") MultipartFile avatar
    ) {
        if (avatar == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Avatar"
            );
        }

        User updatedUser = userService.updateAvatar(avatar);

        if (updatedUser == null) {
            throw new ErrorType(
                    400,
                    "Error updating avatar"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Avatar updated successfully",
                        updatedUser,
                        200
                )
        );
    }

    @PatchMapping("/coverImage")
    public ResponseEntity<ResponseType<User>> updateCoverImage(
            @RequestPart("coverImage") MultipartFile coverImage
    ) {
        if (coverImage == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Cover Image"
            );
        }

        User updatedUser = userService.updateCoverImage(coverImage);

        if (updatedUser == null) {
            throw new ErrorType(
                    400,
                    "Error updating cover image"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Cover image updated successfully",
                        updatedUser,
                        200
                )
        );
    }

    private void setAccessTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessExpirationTime)
                .domain(cookieDomain)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshExpirationTime)
                .domain(cookieDomain)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .domain(cookieDomain)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/users/refresh-token")
                .maxAge(0)
                .domain(cookieDomain)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // remove JSESSIONID cookie

    private void clearJSessionIdCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .domain(cookieDomain)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
