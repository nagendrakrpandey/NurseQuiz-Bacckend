package Tech.Nagendra.NurseQuiz.DTO;
import lombok.Data;

@Data
public class SignupRequest {
    private String fullName;
    private String contact;
    private String email;
    private String password;
    private String confirmPassword;
}