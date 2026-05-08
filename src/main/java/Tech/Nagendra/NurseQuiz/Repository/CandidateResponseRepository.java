package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.DTO.CandidateResponseDTO;
import Tech.Nagendra.NurseQuiz.DTO.ExamAccessDTO;
import Tech.Nagendra.NurseQuiz.Entity.CandidateResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidateResponseRepository extends JpaRepository<CandidateResponse, Long> {
    List<CandidateResponse> findByCandidateId(Long candidateId);

    List<CandidateResponse> findByBatchCode(String batchCode);
    @Query(value =
            "SELECT " +
                    "b.id AS questionId, " +
                    "b.text AS question, " +
                    "b.optiona AS optiona, " +
                    "b.optionb AS optionb, " +
                    "b.optionc AS optionc, " +
                    "b.optiond AS optiond, " +
                    "b.correct_option AS correctOption, " +
                    "b.marks AS marks, " +
                    "a.ans_id AS ansId, " +
                    "a.candidate_id AS candidateId, " +
                    "a.tab_switch_count AS tabSwitchCount, " +
                    "a.submit_time AS submitTime, " +
                    "CASE " +
                    "WHEN CAST(a.ans_id AS int) = b.correct_option THEN b.marks " +
                    "ELSE 0 " +
                    "END AS obtMarks " +
                    "FROM candidate_responses a " +
                    "LEFT JOIN questions b ON a.question_id = b.id " +
                    "WHERE a.candidate_id = :candidateId",
            nativeQuery = true)
    List<CandidateResponseDTO> getCandidateResponseDetails(@Param("candidateId") Long candidateId);

}
