package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.ExamAttempt;
import Tech.Nagendra.NurseQuiz.Enum.ExamStage;
import Tech.Nagendra.NurseQuiz.Enum.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    Optional<ExamAttempt> findByUserIdAndExamStage(
            Long userId,
            ExamStage examStage
    );

    Optional<ExamAttempt> findTopByUserIdAndExamStageOrderByIdDesc(
            Long userId,
            ExamStage examStage
    );

    boolean existsByUserIdAndExamStageAndExamStatus(
            Long userId,
            ExamStage examStage,
            ExamStatus examStatus
    );

    List<ExamAttempt> findByUserIdOrderByStartedAtAsc(Long userId);

    List<ExamAttempt> findByExamStageAndExamStatus(
            ExamStage examStage,
            ExamStatus examStatus
    );
}