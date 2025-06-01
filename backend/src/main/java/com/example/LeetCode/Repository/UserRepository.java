package com.example.LeetCode.Repository;

import com.example.LeetCode.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {

    Users findByUsername(String username);

    @Query("SELECT u.username FROM Users u WHERE u.selectedLanguage = :selectedLanguage")
    List<String> findNamesBySelectedLanguage(@Param("selectedLanguage") String selectedLanguage);

    @Query("SELECT u.username FROM Users u")
    List<String> findAllUsernames();
}
