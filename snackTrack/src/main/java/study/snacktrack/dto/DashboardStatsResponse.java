package study.snacktrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) used for carrying overall statistical data for an administrative dashboard.
 * This structure summarizes key counts regarding users and fitness content within the entire application.
 */
@Data
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long bannedUsers;
    private long premiumUsers;
    private long totalTrainings;
    private long totalExercises;

    /**
     * Getters and constructors are automatically generated or explicitly defined for this DTO.
     * These methods provide read-only access to the various aggregated statistics for display on the admin dashboard.
     */
}