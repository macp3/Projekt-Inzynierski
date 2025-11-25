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
}