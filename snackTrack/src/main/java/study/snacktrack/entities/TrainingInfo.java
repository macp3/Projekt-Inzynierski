package study.snacktrack.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

/**
 * Represents the high-level descriptive information for a complete training program or routine.
 * This entity serves as the header for a workout plan, containing its name, a detailed description, and the total intended duration.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "trainings_info")
public class TrainingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(name = "duration_time", nullable = false)
    private Integer durationTime;

    @OneToMany(mappedBy = "trainingInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    @OneToMany(mappedBy = "trainingInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTraining> userTrainings;



    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the TrainingInfo entity properties.
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }
}