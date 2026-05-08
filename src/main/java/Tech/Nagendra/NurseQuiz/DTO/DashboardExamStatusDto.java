package Tech.Nagendra.NurseQuiz.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardExamStatusDto {

    private String currentStage;
    private String quizStatus;
    private String resultStatus;
    private Double percentage;
    private Integer timeTakenSeconds;
}
