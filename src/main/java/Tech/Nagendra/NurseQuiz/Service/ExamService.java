package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.DTO.CandidateResponseDTO;
import Tech.Nagendra.NurseQuiz.DTO.ExamResultDto;
import Tech.Nagendra.NurseQuiz.Entity.ExamAttempt;
import Tech.Nagendra.NurseQuiz.Enum.ExamStage;
import Tech.Nagendra.NurseQuiz.Enum.ExamStatus;
import Tech.Nagendra.NurseQuiz.Enum.ResultStatus;
import Tech.Nagendra.NurseQuiz.Repository.CandidateResponseRepository;
import Tech.Nagendra.NurseQuiz.Repository.CandidatesRepository;
import Tech.Nagendra.NurseQuiz.Repository.ExamAttemptRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ExamService {

    private final ExamAttemptRepository examAttemptRepository;
    private final CandidatesRepository candidatesRepository;
    private final CandidateResponseRepository candidateResponseRepository;

    public ExamService(
            ExamAttemptRepository examAttemptRepository,
            CandidatesRepository candidatesRepository,
            CandidateResponseRepository candidateResponseRepository
    ) {
        this.examAttemptRepository = examAttemptRepository;
        this.candidatesRepository = candidatesRepository;
        this.candidateResponseRepository = candidateResponseRepository;
    }

    private ExamStage getNextStage(ExamStage currentStage) {
        return switch (currentStage) {
            case DISTRICT -> ExamStage.STATE;
            case STATE -> ExamStage.REGIONAL;
            case REGIONAL -> null;
        };
    }

    @Transactional
    public String startExam(Long userId, ExamStage stage) {

        boolean exists = examAttemptRepository
                .findByUserIdAndExamStage(userId, stage)
                .isPresent();

        if (exists) {
            return "Exam already started";
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setUserId(userId);
        attempt.setExamStage(stage);
        attempt.setExamStatus(ExamStatus.IN_PROGRESS);
        attempt.setStartedAt(LocalDateTime.now());

        examAttemptRepository.save(attempt);

        return "Exam started successfully";
    }

    @Transactional
    public String submitExam(Long userId, ExamStage examStage) {

        Long candidateId = candidatesRepository.findCandidateIdByUserId(userId);

        if (candidateId == null) {
            throw new RuntimeException("Candidate not found for this user");
        }

        List<CandidateResponseDTO> responses =
                candidateResponseRepository.getCandidateResponseDetails(candidateId);

        if (responses == null || responses.isEmpty()) {
            throw new RuntimeException("No responses found for candidate");
        }

        ExamAttempt attempt = examAttemptRepository
                .findByUserIdAndExamStage(userId, examStage)
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        if (attempt.getExamStatus() == ExamStatus.COMPLETED) {
            throw new RuntimeException("Your exam is already submitted");
        }

        int totalQuestions = responses.size();

        int score = responses.stream()
                .mapToInt(r -> r.getObtMarks() != null ? r.getObtMarks().intValue() : 0)
                .sum();

        int totalMarks = responses.stream()
                .mapToInt(r -> r.getMarks() != null ? r.getMarks().intValue() : 1)
                .sum();

        int correctAnswers = (int) responses.stream()
                .filter(r -> r.getObtMarks() != null && r.getObtMarks().intValue() > 0)
                .count();

        int wrongAnswers = totalQuestions - correctAnswers;

        double percentage = totalMarks > 0
                ? (score * 100.0) / totalMarks
                : 0.0;

        int timeTakenSeconds = 0;

        if (attempt.getStartedAt() != null) {
            timeTakenSeconds = (int) Duration.between(
                    attempt.getStartedAt(),
                    LocalDateTime.now()
            ).getSeconds();
        }

        attempt.setScore(score);
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setWrongAnswers(wrongAnswers);
        attempt.setPercentage(percentage);
        attempt.setTimeTakenSeconds(timeTakenSeconds);
        attempt.setExamStatus(ExamStatus.COMPLETED);
        attempt.setSubmittedAt(LocalDateTime.now());

        if (percentage >= 40) {
            if (examStage == ExamStage.REGIONAL) {
                attempt.setResultStatus(ResultStatus.SELECTED);
                attempt.setQualifiedForNextStage(false);
                attempt.setNextStage(null);
            } else {
                attempt.setResultStatus(ResultStatus.PASSED);
                attempt.setQualifiedForNextStage(true);
                attempt.setNextStage(getNextStage(examStage));
            }
        } else {
            attempt.setResultStatus(ResultStatus.FAILED);
            attempt.setQualifiedForNextStage(false);
            attempt.setNextStage(null);
        }

        examAttemptRepository.save(attempt);

        if (attempt.getResultStatus() == ResultStatus.FAILED) {
            return "Exam submitted successfully. You are not qualified for next stage.";
        }

        if (attempt.getNextStage() != null) {
            return "Exam submitted successfully. You qualified for " + attempt.getNextStage() + " level.";
        }

        return "Exam submitted successfully.";
    }

    public ExamResultDto getExamResult(Long userId) {

        ExamResultDto dto = new ExamResultDto();

        List<ExamAttempt> attempts =
                examAttemptRepository.findByUserIdOrderByStartedAtAsc(userId);

        if (attempts.isEmpty()) {
            dto.setCurrentStage(ExamStage.DISTRICT.name());
            dto.setNextStage(null);
            dto.setExamStatus("NOT_STARTED");
            dto.setPercentage(null);
            dto.setScore(null);
            dto.setTimeTakenSeconds(null);
            dto.setMessage("Exam not started");
            return dto;
        }

        ExamAttempt latestAttempt = attempts.get(attempts.size() - 1);

        dto.setCurrentStage(latestAttempt.getExamStage().name());
        dto.setNextStage(
                latestAttempt.getNextStage() != null
                        ? latestAttempt.getNextStage().name()
                        : null
        );
        dto.setExamStatus(latestAttempt.getExamStatus().name());
        dto.setPercentage(latestAttempt.getPercentage());
        dto.setScore(latestAttempt.getScore());
        dto.setTimeTakenSeconds(latestAttempt.getTimeTakenSeconds());

        if (latestAttempt.getExamStatus() == ExamStatus.IN_PROGRESS) {
            dto.setMessage("Exam is in progress");
        } else if (latestAttempt.getResultStatus() == ResultStatus.FAILED) {
            dto.setMessage("You failed at " + latestAttempt.getExamStage() + " level");
        } else if (latestAttempt.getNextStage() != null) {
            dto.setMessage("You qualified for " + latestAttempt.getNextStage() + " level");
        } else {
            dto.setMessage("Exam completed");
        }

        return dto;
    }
}