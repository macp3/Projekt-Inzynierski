package study.snacktrack.dto;

import java.util.List;

public class AssignDietTypesRequest {

    private List<Integer> dietTypeIds;

    public List<Integer> getDietTypeIds() {
        return dietTypeIds;
    }

    public void setDietTypeIds(List<Integer> dietTypeIds) {
        this.dietTypeIds = dietTypeIds;
    }
}
