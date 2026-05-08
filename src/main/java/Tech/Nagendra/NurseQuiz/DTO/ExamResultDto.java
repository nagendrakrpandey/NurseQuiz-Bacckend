package Tech.Nagendra.NurseQuiz.DTO;

import lombok.Data;

@Data
public class ExamResultDto {

    private String currentStage;
    private String nextStage;
    private String examStatus;

    private Double percentage;
    private Integer score;
    private Integer timeTakenSeconds;

    private String message;
}
