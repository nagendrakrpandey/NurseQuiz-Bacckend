package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByLevel(String level);
    List<Question> findByBatchId(Long batchId);
    List<Question> findByQuestionBank_Id(Long qbankId);
    List<Question> findByQuestionBank_IdAndBatchId(Long qbankId, Long batchId);
}