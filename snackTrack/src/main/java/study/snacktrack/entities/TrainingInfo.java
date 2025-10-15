package study.snacktrack.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

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

    //ewentualnie do wypierdolenia xd
    @OneToMany(mappedBy = "trainingInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    //to tez
    @OneToMany(mappedBy = "trainingInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTraining> userTrainings;



    // Gettery i settery
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
