package Tech.Nagendra.NurseQuiz.Entity;

import Tech.Nagendra.NurseQuiz.Enum.ExamStage;
import Tech.Nagendra.NurseQuiz.Enum.ExamStatus;
import Tech.Nagendra.NurseQuiz.Enum.ResultStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "exam_attempts", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "exam_stage"})})
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name="exam_stage", nullable = false)
    private ExamStage examStage;

    @Enumerated(EnumType.STRING)
    @Column(name="exam_status", nullable = false)
    private ExamStatus examStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="result_status")
    private ResultStatus resultStatus;

    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer score;

    private Boolean qualifiedForNextStage = false;

    @Enumerated(EnumType.STRING)
    private ExamStage nextStage;

    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

    private Integer totalMarks;
    private Double percentage;
    private Integer timeTakenSeconds;

}