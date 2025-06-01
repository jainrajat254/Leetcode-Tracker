package com.example.LeetCode.Repository;

import com.example.LeetCode.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, String> {
    UserData findByUsername(String username);

    @Query("SELECT new com.example.LeetCode.Model.LeaderboardEntry(u.name, ud.username, ud.totalSolved, ud.userAvatar, u.year) " +
            "FROM UserData ud " +
            "JOIN Users u ON u.username = ud.username " +
            "ORDER BY ud.totalSolved DESC")
    List<LeaderboardEntry> clubLeaderBoard();

    @Query("SELECT new com.example.LeetCode.Model.StatsEntry(u.name, ud.username, ud.totalSolved, ud.submissionCalendar, u.year) " +
            "FROM UserData ud " +
            "JOIN Users u ON u.username = ud.username " +
            "WHERE u.selectedLanguage = :selectedLanguage " +
            "ORDER BY ud.totalSolved DESC")
    List<StatsEntry> getStats(@Param("selectedLanguage") String selectedLanguage);

    @Query("SELECT new com.example.LeetCode.Model.LeaderboardEntry(u.name, ud.username, ud.totalSolved, ud.userAvatar, u.year) " +
            "FROM UserData ud " +
            "JOIN Users u ON u.username = ud.username " +
            "WHERE u.selectedLanguage = :selectedLanguage " +
            "ORDER BY ud.totalSolved DESC")
    List<LeaderboardEntry> languageLeaderBoard(@Param("selectedLanguage") String selectedLanguage);


    @Query("SELECT new com.example.LeetCode.Model.StreakContent(u.name, ud.username, ud.submittedToday, ud.userAvatar) " +
            "FROM UserData ud " +
            "JOIN Users u ON u.username = ud.username " +
            "WHERE u.selectedLanguage = :selectedLanguage " +
            "ORDER BY ud.submittedToday")
    List<StreakContent> hasAttemptedToday(@Param("selectedLanguage") String selectedLanguage);

    @Query("SELECT u.submissionCalendar FROM UserData u WHERE u.username = :username")
    List<String> lastThirtyDays(@Param("username") String username);

    @Query("SELECT ud.totalSolved, ud.easySolved, ud.mediumSolved, ud.hardSolved, ud.ranking, ud.acceptanceRate FROM UserData ud WHERE ud.username = :username")
    List<String> questionsSolved(@Param("username") String username);

    @Query("SELECT u.name, u.selectedLanguage FROM Users u WHERE u.username = :username")
    List<String> nameAndLanguage(@Param("username") String username);

    @Query("SELECT new com.example.LeetCode.Model.UserProfileDTO(u.githubUrl, u.twitterUrl, u.linkedinUrl, u.school) FROM UserData u WHERE u.username = :username")
    UserProfileDTO getUserSocials(@Param("username") String username);

    @Query("SELECT new com.example.LeetCode.Model.UserProfileDTO(u.userAvatar) FROM UserData u WHERE u.username = :username")
    UserProfileDTO getUserProfile(@Param("username") String username);

    @Query("SELECT new com.example.LeetCode.Model.Contest(u.attendedContestsCount, u.rating, u.globalRanking, u.topPercentage) FROM UserData u WHERE u.username = :username")
    Contest getContestInfo(@Param("username") String username);
}
