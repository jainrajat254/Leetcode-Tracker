package com.example.LeetCode.Service;

import com.example.LeetCode.Model.*;
import com.example.LeetCode.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RestTemplate restTemplate;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (!doesLeetCodeUserExist(user.getUsername())) {
            throw new IllegalArgumentException("LeetCode username not found");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private boolean doesLeetCodeUserExist(String username) {
        String url = "https://leetcode.com/graphql";
        String query = "{\"query\":\"{ matchedUser(username: \\\"" + username + "\\\") { username }}\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(query, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            return response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody().contains("\"matchedUser\":{\"username\":\"" + username + "\"}");
        } catch (Exception e) {
            return false;
        }
    }

//    private boolean doesGitHubUserExist(String username) {
//        String url = "https://api.github.com/users/" + username;
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        try {
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//            return response.getStatusCode() == HttpStatus.OK;
//        } catch (HttpClientErrorException.NotFound e) {
//            // 404 - User does not exist
//            return false;
//        } catch (Exception e) {
//            // Other errors (network issues, rate limiting, etc.)
//            return false;
//        }
//    }


    public String isValidUser(String username) {
        if (userRepository.findByUsername(username) == null) {
            return "Username not found";
        }
        if (!doesLeetCodeUserExist(username)) {
            return "Leetcode username not found";
        }
        return "User Found";
    }

    public LoginResponse login(LoginCredentials credentials) {
        Users user = userRepository.findByUsername(credentials.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("Username does not exist");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Incorrect password");
        }
        String token = jwtService.generateToken(credentials.getUsername());
        return new LoginResponse(user.getId(), user.getName(), user.getSelectedLanguage(), user.getYear(), user.getUsername(), token);
    }

    public List<String> getAllUsernames() {
        return userRepository.findAllUsernames();
    }

    public String editPassword(EditPassword request, UUID id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password.");
        }
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password updated successfully";
    }

    public EditDetailsResponse editDetails(EditDetails request, UUID id) {
        Users users = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        users.setName(request.getName());
        users.setUsername(request.getUsername());
        users.setYear(request.getYear());
        users.setSelectedLanguage(request.getSelectedLanguage());
        Users updatedUser = userRepository.save(users);
        return new EditDetailsResponse(
                updatedUser.getName(),
                updatedUser.getSelectedLanguage(),
                updatedUser.getYear(),
                updatedUser.getUsername()
        );
    }
}
