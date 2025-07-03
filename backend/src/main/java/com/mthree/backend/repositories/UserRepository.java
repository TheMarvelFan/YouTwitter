package com.mthree.backend.repositories;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mthree.backend.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

//    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

//    User findByEmailOrUsername(String email, String username);

    User findByUsername(String username);

//    User findByEmail(String email);
}
