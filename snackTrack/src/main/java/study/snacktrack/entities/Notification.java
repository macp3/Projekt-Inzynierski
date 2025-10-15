package study.snacktrack.entities;

import java.time.LocalDate;

import study.snacktrack.entities.enums.Recipients;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Admin author;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "recipients", nullable = false)
    private Recipients recipients;
    @NotNull
    @Column(name = "sending_time")
    private LocalDate sendingTime;

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    @NotNull
    public Recipients getRecipients() {
        return recipients;
    }

    public void setRecipients(@NotNull Recipients recipients) {
        this.recipients = recipients;
    }

    @NotNull
    public LocalDate getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(@NotNull LocalDate sendingTime) {
        this.sendingTime = sendingTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Admin getAuthor() {
        return author;
    }

    public void setAuthor(Admin author) {
        this.author = author;
    }
}
