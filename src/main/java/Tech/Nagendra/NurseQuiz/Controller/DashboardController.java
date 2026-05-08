package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.DTO.CertificateDto;
import Tech.Nagendra.NurseQuiz.DTO.DashboardExamStatusDto;
import Tech.Nagendra.NurseQuiz.DTO.DashboardResponse;
import Tech.Nagendra.NurseQuiz.DTO.NotificationDto;
import Tech.Nagendra.NurseQuiz.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable Long userId) {

        Map<String, String> levelDates = dashboardService.getLevelDates();

        DashboardExamStatusDto examStatus =
                dashboardService.getCandidateExamStatus(userId);

        String currentStage = examStatus.getCurrentStage() != null
                ? examStatus.getCurrentStage()
                : "DISTRICT";

        String quizStatus = examStatus.getQuizStatus() != null
                ? examStatus.getQuizStatus()
                : "NOT_STARTED";

        String nextExamDate = levelDates.getOrDefault(
                currentStage,
                LocalDate.now().toString()
        );

        int activeLevel = getActiveLevel(currentStage);
        int progressPercentage = getProgressPercentage(activeLevel);

        List<NotificationDto> notifications = List.of(
                new NotificationDto(
                        "Quiz Status",
                        "Your quiz status is " + quizStatus,
                        "info",
                        "Now"
                ),
                new NotificationDto(
                        "Current Stage",
                        "Your current stage is " + currentStage,
                        "success",
                        "Now"
                )
        );

        List<CertificateDto> certificates = List.of(
                new CertificateDto(
                        "Participation Certificate",
                        quizStatus.equals("COMPLETED") ? "Available" : "Locked",
                        quizStatus.equals("COMPLETED")
                                ? "/api/certificates/download/" + userId + "/participation"
                                : null
                ),
                new CertificateDto(
                        "Qualification Certificate",
                        "SELECTED".equals(examStatus.getResultStatus()) ? "Available" : "Locked",
                        "SELECTED".equals(examStatus.getResultStatus())
                                ? "/api/certificates/download/" + userId + "/qualification"
                                : null
                ),
                new CertificateDto(
                        "Winner Certificate",
                        "WINNER".equals(examStatus.getResultStatus()) ? "Available" : "Locked",
                        "WINNER".equals(examStatus.getResultStatus())
                                ? "/api/certificates/download/" + userId + "/winner"
                                : null
                )
        );

        DashboardResponse response = new DashboardResponse(
                "Approved",
                currentStage,
                quizStatus,
                List.of("DISTRICT", "STATE", "REGIONAL"),
                activeLevel,
                progressPercentage,
                nextExamDate,
                true,
                levelDates,
                notifications,
                certificates
        );

        return ResponseEntity.ok(response);
    }

    private int getActiveLevel(String currentStage) {
        if ("STATE".equalsIgnoreCase(currentStage)) {
            return 1;
        }

        if ("REGIONAL".equalsIgnoreCase(currentStage)) {
            return 2;
        }

        return 0;
    }

    private int getProgressPercentage(int activeLevel) {
        if (activeLevel == 0) {
            return 33;
        }

        if (activeLevel == 1) {
            return 66;
        }

        return 100;
    }
}