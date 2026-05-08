package Tech.Nagendra.NurseQuiz.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
    private Long id;
    private String fullName;
    private String email;
    private String contact;
    private int loginStatus;
    private Long roleId;
    private Long batchId;
    private String batchCode;
    private String level;


}