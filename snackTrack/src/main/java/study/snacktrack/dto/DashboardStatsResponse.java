package study.snacktrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long bannedUsers;
    private long premiumUsers;
    private long totalTrainings;
    private long totalExercises;

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public long getBannedUsers() {
        return bannedUsers;
    }

    public long getPremiumUsers() {
        return premiumUsers;
    }

    public long getTotalTrainings() {
        return totalTrainings;
    }

    public long getTotalExercises() {
        return totalExercises;
    }
}