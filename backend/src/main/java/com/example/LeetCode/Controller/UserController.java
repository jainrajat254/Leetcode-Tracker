package com.example.LeetCode.Controller;

import com.example.LeetCode.Model.*;
import com.example.LeetCode.Service.UserDataService;
import com.example.LeetCode.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataService userDataService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user) {
        try {
            Users registeredUser = userService.register(user);
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCredentials credentials) {
        try {
            LoginResponse userResponse = userService.login(credentials);
            return ResponseEntity.ok(userResponse);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/isValidUser/{username}")
    public ResponseEntity<Map<String, String>> isValidUser(@PathVariable String username) {
        String result = userService.isValidUser(username);

        if ("User Found".equals(result)) {
            return ResponseEntity.ok(Map.of("message", result));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", result));
        }
    }

    @GetMapping("/clubLeaderBoard")
    public List<LeaderboardEntry> clubLeaderBoard() {
        return userDataService.getClubLeaderBoard();
    }

    @GetMapping("/languageLeaderBoard/{selectedLanguage}")
    public List<LeaderboardEntry> languageLeaderBoard(@PathVariable String selectedLanguage) {
        return userDataService.getLanguageLeaderBoard(selectedLanguage);
    }

    @GetMapping("/hasAttemptedToday/{selectedLanguage}")
    public List<StreakContent> hasAttemptedToday(@PathVariable String selectedLanguage) {
        return userDataService.hasAttemptedToday(selectedLanguage);
    }

    @GetMapping("/lastThirtyDays/{username}")
    public List<Boolean> lastSevenDays(@PathVariable String username) {
        return userDataService.lastThirtyDays(username);
    }

    @GetMapping("/data/updateAll")
    public ResponseEntity<String> updateAllUsersData() {
        List<String> allUsers = userService.getAllUsernames();
        for (String username : allUsers) {
            userDataService.fetchAndSaveUserData(username);
        }
        return ResponseEntity.ok("Data updated for all users");
    }

    @GetMapping("/data/updateUser/{username}")
    public ResponseEntity<String> updateUser(@PathVariable String username) {
        return userDataService.updateUser(username);
    }

    @GetMapping("/questionsSolved/{username}")
    public List<String> questionsSolved(@PathVariable String username) {
        return userDataService.questionsSolved(username);
    }

    @GetMapping("/nameAndLanguage/{username}")
    public List<String> nameAndLanguage(@PathVariable String username) {
        return userDataService.nameAndLanguage(username);
    }

    @GetMapping("/questionsCount/{selectedLanguage}")
    public List<StatsEntry> questionsCount(@PathVariable String selectedLanguage) {
        return userDataService.questionsCount(selectedLanguage);
    }

    @GetMapping("/getUserSocials/{username}")
    public UserProfileDTO getUserSocialsAndProfile(@PathVariable String username) {
        return userDataService.getUserSocials(username);
    }

    @GetMapping("/getUserProfile/{username}")
    public UserProfileDTO getUserProfile(@PathVariable String username) {
        return userDataService.getUserProfile(username);
    }

    @PutMapping("/editPassword/{id}")
    public ResponseEntity<String> editPassword(@RequestBody EditPassword request, @PathVariable UUID id) {
        try {
            String response = userService.editPassword(request, id);
            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect current password.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PutMapping("/editDetails/{id}")
    public EditDetailsResponse editDetails(@RequestBody EditDetails request, @PathVariable UUID id) {
        return userService.editDetails(request, id);
    }

    @GetMapping("/getContestInfo/{username}")
    public Contest getContestInfo(@PathVariable String username) {
        return userDataService.getContestInfo(username);
    }
}
