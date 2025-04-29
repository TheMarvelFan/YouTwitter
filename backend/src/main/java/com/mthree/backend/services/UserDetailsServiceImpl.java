package com.mthree.backend.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mthree.backend.repositories.UserRepository;
import com.mthree.backend.models.User;
import com.mthree.backend.models.UserPrincipal;
import com.mthree.backend.utils.ErrorType;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user: " + username);

        User userOne = this.userRepository.findByUsername(username);

        if (userOne == null) {
            System.out.println("User not found: " + username);
            throw new ErrorType(
                    404,
                    "User with this username does not exist"
            );
        }

        System.out.println("Found user: " + username + " with password: " + userOne.getPassword());
        return new UserPrincipal(userOne);
    }
}

