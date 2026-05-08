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
@Table(name = "CandidateEvidance")

public class CandidateEvidance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long teamMemberId;
    @Column(name = "batch_code")
    private String batchCode;
    private String teamMemberName;
    private String type;
    private Integer docId;

    @Column(columnDefinition = "TEXT")
    private String imageData;
    private LocalDateTime uploadTime;
    @PrePersist
    public void onCreate() {
        this.uploadTime = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.uploadTime = LocalDateTime.now();
    }
}
