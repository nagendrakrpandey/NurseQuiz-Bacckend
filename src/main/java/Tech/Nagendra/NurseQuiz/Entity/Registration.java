package Tech.Nagendra.NurseQuiz.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration")
@Getter
@Setter
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String organizationName;
    private String hospitalRegisteredId;
    private String spocName;
    private String hospitalCategory;
    private String pincode;
    private String state;
    private String district;
    private String orgEmail;
    private String orgPhone;
    @Column(length = 2000)
    private String address;

    private String hospitalRegistrationCertificatePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;
    @Column(name = "status")
    private Integer status;
    private String rejectionReason;
}