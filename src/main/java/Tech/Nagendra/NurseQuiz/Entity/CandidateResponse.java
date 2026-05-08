package Tech.Nagendra.NurseQuiz.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "candidate_responses")
public class CandidateResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(name = "question_id")
    private Long questionId;

    private Long candidateId;

    private String ansId;
    private Boolean isCorrect;

    private LocalDateTime submitTime;

    private Boolean isActive;
    private Integer tabSwitchCount;

    private String batchCode;

    private Integer questionCount;

    private Double latitude;
    private Double longitude;
    private String locationName;

    private LocalDateTime createdOn;
    private String createdBy;

    private LocalDateTime modifiedOn;
    private String modifiedBy;
}