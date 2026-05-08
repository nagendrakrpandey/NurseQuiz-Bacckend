package Tech.Nagendra.NurseQuiz.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionRequest {

    private Long qbankId;
    private String text;
    private List<String> options;
    private int correctOption;
    private String explanation;
    private int marks;
    private String level;
    private String difficulty;

    public Long getQbankId() {
        return qbankId;
    }
}