package Tech.Nagendra.NurseQuiz.DTO;

public interface ExamAccessDTO {

    Long getUserId();

    Long getCandidateId();

    String getCandidateStatus();

    Integer getLoginStatus();

    Integer getRegistrationStatus();

    String getAccessStatus();

    String getMessage();
}