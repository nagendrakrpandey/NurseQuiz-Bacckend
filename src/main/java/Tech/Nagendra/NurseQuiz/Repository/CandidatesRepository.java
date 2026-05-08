package Tech.Nagendra.NurseQuiz.Repository;


import Tech.Nagendra.NurseQuiz.DTO.ExamAccessDTO;
import Tech.Nagendra.NurseQuiz.Entity.Candidates;
import Tech.Nagendra.NurseQuiz.Enum.ExamStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidatesRepository extends JpaRepository<Candidates, Long> {
    List<Candidates> findByBatchId(Long batchId);
    Optional<Candidates> findByEmail(String email);
    boolean existsByUserIdAndBatchId(Long userId, Long batchId);

    List<Candidates> findByBatchIdAndCurrentStage(Long batchId, ExamStage currentStage);


    @Query(
            value = "SELECT c.*, r.user_id AS user_id " +
                    "FROM candidates c " +
                    "LEFT JOIN registration r ON LOWER(c.email) = LOWER(r.email) " +
                    "WHERE c.batch_id = :batchId",
            nativeQuery = true
    )
    List<Candidates> findByBatchIdWithUserId(@Param("batchId") Long batchId);
    @Query(value =
            "SELECT " +
                    "u.id AS userId, " +
                    "c.candidate_id AS candidateId, " +
                    "c.status AS candidateStatus, " +
                    "u.login_status AS loginStatus, " +
                    "r.status AS registrationStatus, " +
                    "CASE " +
                    "WHEN c.candidate_id IS NULL THEN 'NOT_ENROLLED' " +
                    "WHEN LOWER(c.status) <> 'enrolled' THEN 'NOT_ENROLLED' " +
                    "WHEN r.status <> 1 THEN 'PENDING' " +
                    "WHEN u.login_status <> 1 THEN 'LOGIN_PENDING' " +
                    "ELSE 'ALLOWED' " +
                    "END AS accessStatus, " +
                    "CASE " +
                    "WHEN c.candidate_id IS NULL THEN 'Candidate is not enrolled in this batch' " +
                    "WHEN LOWER(c.status) <> 'enrolled' THEN 'Candidate status is not enrolled' " +
                    "WHEN r.status <> 1 THEN 'Registration approval is pending' " +
                    "WHEN u.login_status <> 1 THEN 'User login approval is pending' " +
                    "ELSE 'Candidate can start exam' " +
                    "END AS message " +
                    "FROM users u " +
                    "LEFT JOIN candidates c ON c.user_id = u.id AND c.batch_id = :batchId " +
                    "LEFT JOIN registration r ON r.user_id = u.id " +
                    "WHERE u.id = :userId",
            nativeQuery = true)
    ExamAccessDTO validateExamAccess(
            @Param("userId") Long userId,
            @Param("batchId") Long batchId
    );

    @Query(value = "SELECT candidate_id FROM candidates WHERE user_id = :userId LIMIT 1", nativeQuery = true)
    Long findCandidateIdByUserId(@Param("userId") Long userId);
}
