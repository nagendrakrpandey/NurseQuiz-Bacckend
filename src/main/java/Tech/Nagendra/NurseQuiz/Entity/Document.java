package Tech.Nagendra.NurseQuiz.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String organizationId;

    private String registrationCertPath;
    private String teamLeadIdPath;
    private String nursingCouncilRegPath;
}
