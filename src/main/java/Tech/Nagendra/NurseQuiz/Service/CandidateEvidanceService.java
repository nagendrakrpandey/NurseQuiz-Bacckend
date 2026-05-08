package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.Entity.CandidateEvidance;
import Tech.Nagendra.NurseQuiz.Repository.CandidateEvidanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateEvidanceService {

    @Autowired
    private CandidateEvidanceRepository repository;

    // ✅ Save multiple
    public List<CandidateEvidance> saveAll(List<CandidateEvidance> evidences) {
        if (evidences == null || evidences.isEmpty()) {
            throw new RuntimeException("Evidence list is empty");
        }
        return repository.saveAll(evidences);
    }

    // ✅ Get all (avoid using in large data)
    public List<CandidateEvidance> getAll() {
        return repository.findAll();
    }

    // ✅ Better: DB level filtering
    public List<CandidateEvidance> getByTeamMemberId(Long teamMemberId) {
        if (teamMemberId == null) {
            throw new RuntimeException("teamMemberId cannot be null");
        }
        return repository.findByTeamMemberId(teamMemberId);
    }

    // 🔥 CORE LOGIC (STRONG VERSION)
    public CandidateEvidance saveOrUpdate(CandidateEvidance evidence) {

        // ✅ BASIC VALIDATION
        if (evidence == null) {
            throw new RuntimeException("Evidence cannot be null");
        }

        if (evidence.getTeamMemberId() == null ||
                evidence.getBatchCode() == null ||
                evidence.getDocId() == null) {

            throw new RuntimeException("teamMemberId, batchCode, docId required");
        }

        // normalize batchCode (avoid mismatch bug)
        evidence.setBatchCode(evidence.getBatchCode().trim());

        // 🔥 UPDATE ONLY FOR docId 1 & 2
        if (evidence.getDocId() == 1 || evidence.getDocId() == 2) {

            Optional<CandidateEvidance> existing =
                    repository.findByTeamMemberIdAndBatchCodeAndDocId(
                            evidence.getTeamMemberId(),
                            evidence.getBatchCode(),
                            evidence.getDocId()
                    );

            if (existing.isPresent()) {

                CandidateEvidance old = existing.get();

                // 🔥 SAFE UPDATE (only required fields)
                old.setImageData(evidence.getImageData());
                old.setType(evidence.getType());
                old.setTeamMemberName(evidence.getTeamMemberName());

                // ✅ update time manually (backup safety)
                old.setUploadTime(LocalDateTime.now());

                return repository.save(old);
            }
        }

        evidence.setUploadTime(LocalDateTime.now());

        return repository.save(evidence);
    }
    public List<CandidateEvidance> getEvidenceByUserId(Long userId) {
        return repository.findEvidenceByUserId(userId);
    }
    public List<CandidateEvidance> getRandomPhotoVideoByTeamMemberId(Long teamMemberId) {
        if (teamMemberId == null) {
            throw new RuntimeException("teamMemberId cannot be null");
        }

        return repository.findRandomPhotoVideoByTeamMemberId(teamMemberId);
    }
}