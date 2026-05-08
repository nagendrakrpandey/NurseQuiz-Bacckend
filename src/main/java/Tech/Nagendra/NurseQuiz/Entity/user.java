package Tech.Nagendra.NurseQuiz.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String fullName;
    @Column(unique = true)
    private String contact;
    @Column(unique = true)
    private String email;
    private String otp;
    private LocalDateTime otpExpiry;
    @Column(nullable = false)
    private boolean verified = false;
    private String password;
    @Column(name = "login_status")
    private int loginStatus = 0;
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "enrollment_number", unique = true)
    private String enrollmentNumber;
    @Column(length = 1000)
    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpiry;
}