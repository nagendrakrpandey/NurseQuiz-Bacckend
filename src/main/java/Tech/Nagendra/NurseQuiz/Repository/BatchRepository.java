package Tech.Nagendra.NurseQuiz.Repository;


import Tech.Nagendra.NurseQuiz.Entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByLevel(String level);
    Optional<Batch> findByBatchCode(String batchCode);

    @Query(value = "SELECT * FROM batches " + "WHERE LOWER(level) = LOWER(:level) " + "AND LOWER(status) = LOWER(:status) " + "ORDER BY batch_id DESC " + "LIMIT 1", nativeQuery = true)
    Optional<Batch> findLatestBatchByLevelAndStatus(@Param("level") String level, @Param("status") String status);

    @Query(value = "SELECT COUNT(*) FROM questions WHERE batch_id = :batchId", nativeQuery = true)
    Long countQuestionsByBatchId(@Param("batchId") Long batchId);

    @Query(value = "SELECT COUNT(*) FROM candidates WHERE batch_id = :batchId", nativeQuery = true)
    Long countCandidatesByBatchId(@Param("batchId") Long batchId);
}
