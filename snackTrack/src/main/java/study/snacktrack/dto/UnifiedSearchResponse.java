package study.snacktrack.dto;

import java.util.List;
import study.snacktrack.entities.EssentialFood;

public class UnifiedSearchResponse {
    private List<EssentialFood> localResults;
    private List<ApiFoodResponse> apiResults;

    public UnifiedSearchResponse(List<EssentialFood> localResults, List<ApiFoodResponse> apiResults) {
        this.localResults = localResults;
        this.apiResults = apiResults;
    }

    // Gettery i Settery
    public List<EssentialFood> getLocalResults() {
        return localResults;
    }

    public List<ApiFoodResponse> getApiResults() {
        return apiResults;
    }
}