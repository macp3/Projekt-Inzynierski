package com.example.demo.entities;

import java.io.Serializable;
import java.util.Objects;

public class UserTrainingId implements Serializable {

    private int userId;
    private int trainingId;

    public UserTrainingId() {}

    public UserTrainingId(int userId, int trainingId) {
        this.userId = userId;
        this.trainingId = trainingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTrainingId)) return false;
        UserTrainingId that = (UserTrainingId) o;
        return userId == that.userId && trainingId == that.trainingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, trainingId);
    }
}
