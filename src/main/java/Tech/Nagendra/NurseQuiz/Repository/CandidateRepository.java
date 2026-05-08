package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}