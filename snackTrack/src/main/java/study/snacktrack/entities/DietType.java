package study.snacktrack.entities;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import study.snacktrack.entities.enums.DietTypes;

@Entity
@Table(name = "diet_types")
public class DietType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DietTypes name;

    public DietType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DietTypes getName() {
        return name;
    }

    public void setName(DietTypes name) {
        this.name = name;
    }
}
