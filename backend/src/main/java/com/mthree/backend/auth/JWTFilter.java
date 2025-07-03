package com.mthree.backend.auth;

import java.io.IOException;

import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mthree.backend.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.mthree.backend.services.interfaces.JWTService;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final ApplicationContext applicationContext;

    public JWTFilter(JWTService jwtService, ApplicationContext applicationContext) {
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Bearer token
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // if the user is not already authenticated
            // then authenticate the user
            // and set the authentication in the security context
            // so that the user is authenticated

            UserDetails userDetails = applicationContext.getBean(UserDetailsServiceImpl.class).loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails, "access")) {
                // In the above line, we are getting the UserDetailsServiceImpl bean from the application context because we cannot autowire it here
                // because it is not a component, it is a configuration class
                // getting this bean is necessary for the validation process because we need to get the user details from the database

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
