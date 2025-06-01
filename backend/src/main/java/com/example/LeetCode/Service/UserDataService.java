package com.example.LeetCode.Service;

import com.example.LeetCode.Model.*;
import com.example.LeetCode.Repository.UserDataRepository;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserDataService {

    @Autowired
    private UserDataRepository userDataRepository;

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    @Async
    public CompletableFuture<Void> fetchAndSaveUserData(String username) {
        UserData newData = fetchUserDataFromLeetCode(username);
        if (newData != null) {
            saveOrUpdateUserData(newData);
        }
        return CompletableFuture.completedFuture(null);
    }

    public ResponseEntity<String> updateUser(String username) {
        UserData newData = fetchUserDataFromLeetCode(username);
        if (newData != null) {
            boolean isUpdated = saveOrUpdateUserData(newData);
            return isUpdated ? ResponseEntity.ok("User data updated successfully")
                    : ResponseEntity.ok("No changes detected");
        }
        return ResponseEntity.badRequest().body("Failed to fetch user data");
    }

    private boolean saveOrUpdateUserData(UserData newData) {
        UserData existingData = userDataRepository.findByUsername(newData.getUsername());
        if (existingData != null && existingData.equals(newData)) {
            return false;
        }
        userDataRepository.save(newData);
        return true;
    }

    private UserData fetchUserDataFromLeetCode(String username) {
        String query = String.format(
                "{ \"query\": \"query getUserStats($username: String!) { matchedUser(username: $username) { githubUrl twitterUrl linkedinUrl profile { ranking userAvatar school } submissionCalendar submitStats { acSubmissionNum { difficulty count submissions } totalSubmissionNum { difficulty count submissions } } } }\", \"variables\": {\"username\": \"%s\"} }",
                username
        );

        String queryContest = String.format(
                "{ \"query\": \"query getContestRanking($username: String!) { userContestRanking(username: $username) { attendedContestsCount rating globalRanking topPercentage } }\", \"variables\": {\"username\": \"%s\"} }",
                username
        );

        RequestBody body = RequestBody.create(JSON, query);
        RequestBody bodyContest = RequestBody.create(JSON, queryContest);

        Request request = new Request.Builder()
                .url("https://leetcode.com/graphql/")
                .post(body)
                .addHeader("referer", "https://leetcode.com/" + username + "/")
                .addHeader("Content-Type", "application/json")
                .build();

        Request requestContest = new Request.Builder()
                .url("https://leetcode.com/graphql/")
                .post(bodyContest)
                .addHeader("referer", "https://leetcode.com/" + username + "/")
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute();
             Response responseContest = client.newCall(requestContest).execute()) {

            if (response.isSuccessful() && responseContest.isSuccessful()) {
                String responseString = response.body().string();
                String contestResponseString = responseContest.body().string();

                JSONObject jsonResponse = new JSONObject(responseString);
                JSONObject contestJsonResponse = new JSONObject(contestResponseString);

                System.out.println(contestJsonResponse);

                return parseUserData(username, jsonResponse, contestJsonResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UserData parseUserData(String username, JSONObject json, JSONObject jsonContest) {
        JSONObject data = json.getJSONObject("data").getJSONObject("matchedUser");
        JSONObject profile = data.getJSONObject("profile");
        JSONObject submitStats = data.getJSONObject("submitStats");

        // Extract contest data safely
        JSONObject dataContest = jsonContest.optJSONObject("data") != null ?
                jsonContest.optJSONObject("data").optJSONObject("userContestRanking") : null;

        // Extract contest values
        int contestsCount = dataContest != null ? dataContest.optInt("attendedContestsCount", 0) : 0;
        double rating = dataContest != null ? dataContest.optDouble("rating", 0.0) : 0.0;
        int globalRanking = dataContest != null ? dataContest.optInt("globalRanking", 0) : 0;
        double topPercentage = dataContest != null ? dataContest.optDouble("topPercentage", 0.0) : 0.0;

        // Extract user profile details
        int ranking = profile.getInt("ranking");
        String githubUrl = data.optString("githubUrl", null);
        String twitterUrl = data.optString("twitterUrl", null);
        String linkedinUrl = data.optString("linkedinUrl", null);
        String userAvatar = profile.optString("userAvatar", null);
        String school = profile.optString("school", null);

        // Extract submission calendar
        JSONObject submissionCalendarJson = new JSONObject(data.getString("submissionCalendar"));
        Map<Long, Integer> submissionCalendar = new HashMap<>();
        for (String key : submissionCalendarJson.keySet()) {
            try {
                long timestamp = Long.parseLong(key);
                int count = submissionCalendarJson.getInt(key);
                submissionCalendar.put(timestamp, count);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Handle invalid key format
            }
        }

        // Extract submission stats
        int totalSolved = submitStats.getJSONArray("acSubmissionNum").getJSONObject(0).getInt("count");
        int easySolved = submitStats.getJSONArray("acSubmissionNum").getJSONObject(1).getInt("count");
        int mediumSolved = submitStats.getJSONArray("acSubmissionNum").getJSONObject(2).getInt("count");
        int hardSolved = submitStats.getJSONArray("acSubmissionNum").getJSONObject(3).getInt("count");
        float acceptanceRate = (float) submitStats.getJSONArray("acSubmissionNum").getJSONObject(0).getInt("submissions") /
                submitStats.getJSONArray("totalSubmissionNum").getJSONObject(0).getInt("submissions") * 100;

        // Return UserData with contest fields
        return new UserData(username, totalSolved, easySolved, mediumSolved, hardSolved, acceptanceRate, ranking, submissionCalendar,
                githubUrl, twitterUrl, linkedinUrl, userAvatar, school,
                contestsCount, rating, globalRanking, topPercentage);
    }


    @Cacheable(value = "clubLeaderBoard", key = "'all_users'", cacheManager = "redisCacheManager")
    public List<LeaderboardEntry> getClubLeaderBoard() {
        return userDataRepository.clubLeaderBoard();
    }

    @Cacheable(value = "languageLeaderBoard", key = "#selectedLanguage", cacheManager = "redisCacheManager")
    public List<LeaderboardEntry> getLanguageLeaderBoard(String selectedLanguage) {
        return userDataRepository.languageLeaderBoard(selectedLanguage);
    }

    public List<StatsEntry> questionsCount(String selectedLanguage) {
        return userDataRepository.getStats(selectedLanguage);
    }

    public List<StreakContent> hasAttemptedToday(String selectedLanguage) {
        return userDataRepository.hasAttemptedToday(selectedLanguage);
    }

    public List<Boolean> lastThirtyDays(String username) {
        List<String> result = userDataRepository.lastThirtyDays(username);
        if (!result.isEmpty() && result.getFirst() != null) {
            List<Boolean> booleanList = Arrays.stream(result.getFirst().split(","))
                    .map(Boolean::parseBoolean)
                    .collect(Collectors.toList());

            // Reverse the list
            Collections.reverse(booleanList);
            return booleanList;
        }
        return Collections.emptyList();
    }


    public List<String> questionsSolved(String username) {
        List<String> result = userDataRepository.questionsSolved(username);
        if (!result.isEmpty() && result.getFirst() != null) {
            return Arrays.stream(result.getFirst().split(","))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<String> nameAndLanguage(String username) {
        List<String> result = userDataRepository.nameAndLanguage(username);
        if (!result.isEmpty() && result.getFirst() != null) {
            return Arrays.stream(result.getFirst().split(","))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public UserProfileDTO getUserSocials(String username) {
        return userDataRepository.getUserSocials(username);
    }

    public UserProfileDTO getUserProfile(String username) {
        return userDataRepository.getUserProfile(username);
    }

    public Contest getContestInfo(String username) {
        return userDataRepository.getContestInfo(username);
    }
}
