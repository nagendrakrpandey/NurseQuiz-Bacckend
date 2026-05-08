package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepo extends JpaRepository<Document, Long> {

    Optional<Document> findByUserId(Long userId);
}