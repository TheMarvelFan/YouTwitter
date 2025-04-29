package com.mthree.backend.services.interfaces;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import com.mthree.backend.models.ChannelDetails;
import com.mthree.backend.models.User;

public interface UserService {
    User registerUser(String fullName, String email, String password, String userName, MultipartFile avatar, MultipartFile coverImage);
    String logoutUser(String token);
    Pair<User, String> loginUser(String username, String password);
    Pair<String, String> refreshToken(String refreshToken);
    User changePassword(String newPassword);
    User getCurrentUser();
    User updateAccount(User user);
    ChannelDetails getUserChannelProfile(String userName);
    User updateAvatar(MultipartFile avatar);
    User updateCoverImage(MultipartFile coverImage);
}
