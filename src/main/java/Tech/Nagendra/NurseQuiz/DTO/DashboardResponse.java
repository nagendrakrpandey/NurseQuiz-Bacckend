package Tech.Nagendra.NurseQuiz.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private String registrationStatus;
    private String currentStage;
    private String quizStatus;

    private List<String> competitionLevels;
    private Integer activeLevel;
    private Integer progressPercentage;

    private String nextExamDate;
    private Boolean quizAvailable;

    private Map<String, String> levelDates;
    private List<NotificationDto> notifications;
    private List<CertificateDto> certificates;
}