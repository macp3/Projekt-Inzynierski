package study.snacktrack.dto;

import java.util.List;
import study.snacktrack.entities.EssentialFood;

/**
 * Data Transfer Object (DTO) used to standardize the response when searching for food items from multiple sources.
 * This structure aggregates results from both the application's internal database and external food APIs into one object.
 */
public class UnifiedSearchResponse {
    private List<EssentialFood> localResults;
    private List<ApiFoodResponse> apiResults;

    /**
     * Constructs a new UnifiedSearchResponse object containing results from both local and external sources.
     * This constructor is utilized by the food search service to bundle the heterogeneous results for client consumption.
     *
     * @param localResults A list of food entities found in the application's internal database.
     * @param apiResults A list of food DTOs retrieved from the external food API.
     */
    public UnifiedSearchResponse(List<EssentialFood> localResults, List<ApiFoodResponse> apiResults) {
        this.localResults = localResults;
        this.apiResults = apiResults;
    }

    /**
     * Getters and setters for the DTO fields.
     * These methods provide standard access and modification capabilities for the two distinct result lists.
     */
    public List<EssentialFood> getLocalResults() {
        return localResults;
    }

    public List<ApiFoodResponse> getApiResults() {
        return apiResults;
    }
}