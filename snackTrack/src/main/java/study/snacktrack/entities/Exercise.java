package study.snacktrack.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a single exercise definition stored in the fitness plan database.
 * This entity details the exercise's name, description, type, difficulty rating, and execution parameters like sets and repetitions.
 */
@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private Integer difficulty;

    @Column(name = "number_of_sets", nullable = false)
    private Integer numberOfSets;

    @Column(name = "repetitions_per_set", nullable = false)
    private Integer repetitionsPerSet;

    /**
     * Getters and setters for all entity fields.
     * These methods provide standard access and modification capabilities for the Exercise entity properties.
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
    public void setNumberOfSets(Integer numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public void setRepetitionsPerSet(Integer repetitionsPerSet) {
        this.repetitionsPerSet = repetitionsPerSet;
    }
}