package Tech.Nagendra.NurseQuiz.Entity;

import Tech.Nagendra.NurseQuiz.Enum.ExamStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "candidates")
public class Candidates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidate_id;

    private String name;
    private String email;
    private String phone;
    private String enrollment_no;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "user_id")
    private Long userId;

    private String status;

    private Double score;
    @Enumerated(EnumType.STRING)
    private ExamStage currentStage;
    @Column(name = "hospital_id")
    private Long hospitalId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "region_id")
    private Long regionId;
}