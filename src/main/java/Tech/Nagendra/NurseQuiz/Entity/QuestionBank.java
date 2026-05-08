package Tech.Nagendra.NurseQuiz.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_banks")
public class QuestionBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;

    @Column(length = 1000)
    private String description;

    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "questionBank", cascade = CascadeType.ALL)
    private List<Question> questions;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}