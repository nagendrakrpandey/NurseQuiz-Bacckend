package Tech.Nagendra.NurseQuiz.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "team_members")
@Getter
@Setter
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private String email;
    private String role;
    private String roleType;
    private String hospitalEmployeeId;
    private String customRole;
    private String evidenceFileName;
    private String evidenceField;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;
    private String evidencePath;
}