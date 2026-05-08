package Tech.Nagendra.NurseQuiz.DTO;

import Tech.Nagendra.NurseQuiz.Entity.Registration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {

    private String message;
    private Registration data;
}