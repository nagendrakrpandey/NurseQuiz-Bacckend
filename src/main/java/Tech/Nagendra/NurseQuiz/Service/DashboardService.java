package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.DTO.DashboardExamStatusDto;

import Tech.Nagendra.NurseQuiz.Entity.ExamAttempt;
import Tech.Nagendra.NurseQuiz.Enum.ExamStage;
import Tech.Nagendra.NurseQuiz.Repository.CandidatesRepository;
import Tech.Nagendra.NurseQuiz.Repository.CompetitionStageRepository;
import Tech.Nagendra.NurseQuiz.Repository.ExamAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    private CompetitionStageRepository stageRepository;

    @Autowired
    private CandidatesRepository candidateRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    public Map<String, String> getLevelDates() {

        List<CompetitionStage> stages = stageRepository.findAll();

        Map<String, String> levelDates = new HashMap<>();

        for (CompetitionStage stage : stages) {
            if (stage.getStage() != null && stage.getExamDate() != null) {
                levelDates.put(stage.getStage(), stage.getExamDate().toString());
            }
        }

        return levelDates;
    }

    public DashboardExamStatusDto getCandidateExamStatus(Long userId) {

        List<ExamAttempt> attempts =
                examAttemptRepository.findByUserIdOrderByStartedAtAsc(userId);

        DashboardExamStatusDto dto = new DashboardExamStatusDto();

        if (attempts.isEmpty()) {
            dto.setCurrentStage(ExamStage.DISTRICT.name());
            dto.setQuizStatus("NOT_STARTED");
            dto.setResultStatus(null);
            dto.setPercentage(null);
            dto.setTimeTakenSeconds(null);
            return dto;
        }

        ExamAttempt latestAttempt = attempts.get(attempts.size() - 1);

        dto.setCurrentStage(
                latestAttempt.getExamStage() != null
                        ? latestAttempt.getExamStage().name()
                        : ExamStage.DISTRICT.name()
        );

        dto.setQuizStatus(
                latestAttempt.getExamStatus() != null
                        ? latestAttempt.getExamStatus().name()
                        : "NOT_STARTED"
        );

        dto.setResultStatus(
                latestAttempt.getResultStatus() != null
                        ? latestAttempt.getResultStatus().name()
                        : null
        );

        dto.setPercentage(latestAttempt.getPercentage());
        dto.setTimeTakenSeconds(latestAttempt.getTimeTakenSeconds());

        return dto;
    }
}