package com.mthree.backend.services;

import java.util.Date;
import java.util.regex.Pattern;

import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mthree.backend.models.ChannelDetails;
import com.mthree.backend.models.User;
import com.mthree.backend.repositories.SubscriptionRepository;
import com.mthree.backend.repositories.UserRepository;
import com.mthree.backend.services.interfaces.CloudinaryService;
import com.mthree.backend.services.interfaces.JWTBlacklistService;
import com.mthree.backend.services.interfaces.JWTService;
import com.mthree.backend.services.interfaces.UserService;
import com.mthree.backend.constants.Constants;
import com.mthree.backend.utils.ErrorType;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CloudinaryService cloudinaryService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final JWTBlacklistService tokenBlacklistService;

    public UserServiceImpl(
            UserRepository userRepository,
            SubscriptionRepository subscriptionRepository,
            CloudinaryService cloudinaryService,
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            JWTBlacklistServiceImpl tokenBlacklistService
    ) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.cloudinaryService = cloudinaryService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public User registerUser(String fullName, String email, String password, String userName, MultipartFile avatar, MultipartFile coverImage) {
        if (!Pattern.compile(Constants.EMAIL_REGEX).matcher(email).matches()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid email address"
            );
        }

        if (this.userRepository.existsByUsername(userName)) {
            throw new ErrorType(
                    400,
                    "User with this username already exists"
            );
        }

        String avatarUrl = cloudinaryService.upload(avatar);
        String coverImageUrl = null;

        if (avatarUrl == null || avatarUrl.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Error uploading avatar"
            );
        }

        if (coverImage != null) {
            coverImageUrl = cloudinaryService.upload(coverImage);
        }

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);

        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        user.setPassword(encryptedPassword);
        user.setUsername(userName);
        user.setAvatar(avatarUrl);
        user.setCoverImage(coverImageUrl);

        user.setRefreshToken(jwtService.generateRefreshToken(user));

        return userRepository.save(user);
    }

    public Pair<User, String> loginUser(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide valid credentials"
            );
        }


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(username);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        this.userRepository.save(user);

        return Pair.of(user, accessToken);
    }

    public String logoutUser(String token) {
        try {
            Date expiryDate = jwtService.extractExpiration(token);

            tokenBlacklistService.blacklistToken(token, expiryDate);
            return "Logged out successfully";
        } catch (Exception e) {
            throw new ErrorType(
                    400,
                    "Error logging out: " + e.getMessage()
            );
        }
    }

    public Pair<String, String> refreshToken(String refreshToken) {
        try {
            String username = jwtService.extractUsernameFromRefreshToken(refreshToken.trim());
            System.out.println("Refresh token is: " + refreshToken);
            User user = userRepository.findByUsername(username);

            if (user != null) {
                UserDetails userDetails = new UserDetailsServiceImpl(userRepository).loadUserByUsername(username);

                if (jwtService.validateToken(refreshToken, userDetails, "refresh")) {
                    String accessToken = jwtService.generateAccessTokenFromRefreshToken(refreshToken, user);
                    String newRefreshToken = jwtService.generateRefreshToken(user);
                    user.setRefreshToken(newRefreshToken);
                    userRepository.save(user);

                    return Pair.of(accessToken, refreshToken);
                }
            }
        } catch (Exception e) {
            throw new ErrorType(
                    400,
                    "Invalid refresh token " + e.getMessage()
            );
        }

        return null;
    }

    public User changePassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a new password"
            );
        }

        User user = getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    404,
                    "User not found"
            );
        }

        newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

        user.setPassword(newPassword);

        return userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new ErrorType(403, "User not authenticated");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new ErrorType(404, "User not found");
        }

        return user;
    }

    public User updateAccount(User user) {
        if (user == null) {
            throw new ErrorType(
                    400,
                    "Please provide details of user to update"
            );
        }

        if ((user.getFullName() == null || user.getFullName().isEmpty()) && (user.getEmail() == null || user.getEmail().isEmpty())) {
            throw new ErrorType(
                    400,
                    "Please provide at least one field to update"
            );
        }

        String fullName;
        String email;

        if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            fullName = user.getFullName();
        } else {
            fullName = null;
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            email = user.getEmail();
        } else {
            email = null;
        }

        User foundUser = getCurrentUser();

        if (foundUser == null) {
            throw new ErrorType(
                    404,
                    "User not found"
            );
        }

        if (fullName != null) {
            foundUser.setFullName(fullName);
        }
        if (email != null) {
            foundUser.setEmail(email);
        }

        return userRepository.save(foundUser);
    }

    public ChannelDetails getUserChannelProfile(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Please provide a valid username"
            );
        }

        User foundUser = this.userRepository.findByUsername(userName);

        if (foundUser == null) {
            throw new ErrorType(
                    404,
                    "User not found"
            );
        }

        ChannelDetails channelDetails = new ChannelDetails();

        channelDetails.setAvatar(foundUser.getAvatar());
        channelDetails.setUserName(foundUser.getUsername());
        channelDetails.setCoverImage(foundUser.getCoverImage());
        channelDetails.setEmail(foundUser.getEmail());
        channelDetails.setFullName(foundUser.getFullName());

        User user = getCurrentUser();

        if (user == null) {
            throw new ErrorType(
                    404,
                    "User not found"
            );
        }

        channelDetails.setSubscribed(
                subscriptionRepository.findBySubscriberAndChannel(user, foundUser) != null
        );

        long subscribersCount = this.subscriptionRepository.findAllByChannel(foundUser).size();
        long subscribedToCount = this.subscriptionRepository.findAllBySubscriber(foundUser).size();

        channelDetails.setSubscribersCount(subscribersCount);
        channelDetails.setSubscribedToCount(subscribedToCount);

        return channelDetails;
    }

    public User updateAvatar(MultipartFile avatar) {
        if (avatar == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid avatar"
            );
        }

        User foundUser = getCurrentUser();

        if (foundUser == null) {
            throw new ErrorType(
                    404,
                    "User not found"
            );
        }

        String avatarUrl = cloudinaryService.upload(avatar);

        if (avatarUrl == null || avatarUrl.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Error uploading avatar"
            );
        }

        foundUser.setAvatar(avatarUrl);

        return userRepository.save(foundUser);
    }

    public User updateCoverImage(MultipartFile coverImage) {
        if (coverImage == null) {
            throw new ErrorType(
                    400,
                    "Please provide a valid Cover Image"
            );
        }

        User foundUser = getCurrentUser();

        if (foundUser == null) {
            throw new ErrorType(
                    404,
                    "User not found"
            );
        }

        String coverImageUrl = cloudinaryService.upload(coverImage);

        if (coverImageUrl == null || coverImageUrl.isEmpty()) {
            throw new ErrorType(
                    400,
                    "Error uploading Cover Image"
            );
        }

        foundUser.setCoverImage(coverImageUrl);

        return userRepository.save(foundUser);
    }
}
