package Tech.Nagendra.NurseQuiz.DTO;

import java.time.LocalDateTime;

public interface CandidateResponseDTO {

    Long getQbId();

    Long getQuestionId();

    String getQuestion();

    String getOptiona();

    String getOptionb();

    String getOptionc();

    String getOptiond();

    Integer getCorrectOption();

    Integer getMarks();

    String getAnsId();

    Long getCandidateId();

    Integer getObtMarks();
    Integer getTabSwitchCount();
    LocalDateTime getSubmitTime();

}
