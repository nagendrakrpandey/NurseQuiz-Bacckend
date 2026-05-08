package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.CandidateEvidance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateEvidanceRepository extends JpaRepository<CandidateEvidance, Long> {

    List<CandidateEvidance> findByTeamMemberId(Long teamMemberId);

    Optional<CandidateEvidance> findByTeamMemberIdAndBatchCodeAndDocId(
            Long teamMemberId,
            String batchCode,
            Integer docId
    );

    @Query(
            value = "SELECT * FROM candidate_evidance " + "WHERE team_member_id IN (" + "SELECT id FROM team_members WHERE user_id = :userId" + ")",
            nativeQuery = true
    )
    List<CandidateEvidance> findEvidenceByUserId(@Param("userId") Long userId);

    @Query(
            value = "SELECT * FROM candidate_evidance " +
                    "WHERE team_member_id = :teamMemberId " +
                    "AND doc_id IN (3, 4) " +
                    "ORDER BY upload_time DESC",
            nativeQuery = true
    )
    List<CandidateEvidance> findRandomPhotoVideoByTeamMemberId(
            @Param("teamMemberId") Long teamMemberId
    );

}