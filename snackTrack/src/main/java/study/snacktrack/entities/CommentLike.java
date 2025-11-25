package study.snacktrack.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "comment_id" })
})
@Data
@NoArgsConstructor
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    // Relacja do komentarza, żeby Hibernate wiedział jak to mapować
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public CommentLike(int userId, Comment comment) {
        this.userId = userId;
        this.comment = comment;
    }
}