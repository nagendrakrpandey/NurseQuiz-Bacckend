package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Service.CompetitionStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionStageRepository extends JpaRepository<CompetitionStage, Long> {
}
