package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.DTO.ExamAccessDTO;
import Tech.Nagendra.NurseQuiz.Entity.Batch;
import Tech.Nagendra.NurseQuiz.Entity.Candidates;
import Tech.Nagendra.NurseQuiz.Entity.user;
import Tech.Nagendra.NurseQuiz.Enum.ExamStage;
import Tech.Nagendra.NurseQuiz.Repository.BatchRepository;
import Tech.Nagendra.NurseQuiz.Repository.CandidatesRepository;


import Tech.Nagendra.NurseQuiz.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CandidateService {

    @Autowired
    private CandidatesRepository repo;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private UserRepository userRepository;

    // ==========================
    // GET BY BATCH ID
    // ==========================
    public List<Candidates> getByBatchId(Long batchId) {

        if (batchId == null) {
            throw new RuntimeException("batchId is required");
        }

        batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));

        return repo.findByBatchId(batchId);
    }

    // ==========================
    // ADD SINGLE
    // ==========================
    public Candidates addCandidate(Candidates c) {

        if (c == null) {
            throw new RuntimeException("Candidate data is required");
        }

        if (c.getName() == null || c.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }

        if (c.getEmail() == null || c.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (c.getBatchId() == null) {
            throw new RuntimeException("batchId is required");
        }

        Batch batch = batchRepository.findById(c.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + c.getBatchId()));

        c.setBatchId(batch.getBatch_id());
        c.setStatus("enrolled");

        return repo.save(c);
    }

    // ==========================
    // BULK ADD BY BATCH ID
    // ==========================
    public List<Candidates> bulkAdd(List<Candidates> list, Long batchId) {

        if (batchId == null) {
            throw new RuntimeException("batchId is required");
        }

        if (list == null || list.isEmpty()) {
            throw new RuntimeException("Candidate list is empty");
        }

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));

        for (Candidates c : list) {

            if (c.getName() == null || c.getName().trim().isEmpty()) {
                throw new RuntimeException("Candidate name is required");
            }

            if (c.getEmail() == null || c.getEmail().trim().isEmpty()) {
                throw new RuntimeException("Candidate email is required");
            }

            c.setBatchId(batch.getBatch_id());
            c.setStatus("enrolled");
        }

        return repo.saveAll(list);
    }

    // ==========================
    // UPDATE
    // ==========================
    public Candidates updateCandidate(Long id, Candidates c) {

        Candidates existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (c.getName() != null) {
            existing.setName(c.getName());
        }

        if (c.getEmail() != null) {
            existing.setEmail(c.getEmail());
        }

        if (c.getPhone() != null) {
            existing.setPhone(c.getPhone());
        }

        if (c.getEnrollment_no() != null) {
            existing.setEnrollment_no(c.getEnrollment_no());
        }

        if (c.getBatchId() != null) {
            Batch batch = batchRepository.findById(c.getBatchId())
                    .orElseThrow(() -> new RuntimeException("Batch not found with id: " + c.getBatchId()));

            existing.setBatchId(batch.getBatch_id());
        }

        if (c.getStatus() != null) {
            existing.setStatus(c.getStatus());
        }

        if (c.getScore() != null) {
            existing.setScore(c.getScore());
        }

        if (c.getCurrentStage() != null) {
            existing.setCurrentStage(c.getCurrentStage());
        }

        return repo.save(existing);
    }

    // ==========================
    // DELETE
    // ==========================
    public void deleteCandidate(Long id) {

        if (id == null) {
            throw new RuntimeException("Candidate id is required");
        }

        if (!repo.existsById(id)) {
            throw new RuntimeException("Candidate not found");
        }

        repo.deleteById(id);
    }

    // ==========================
    // VALIDATE EXAM ACCESS
    // ==========================
    public ExamAccessDTO validateExamAccess(Long userId, Long batchId) {
        return repo.validateExamAccess(userId, batchId);
    }

    // ==========================
    // AUTO ENROLL DISTRICT CANDIDATES
    // ==========================
    @Transactional
    public Map<String, Object> autoEnrollDistrictCandidates() {
        Batch districtBatch = batchRepository
                .findLatestBatchByLevelAndStatus("district", "upcoming")
                .orElseThrow(() -> new RuntimeException("District batch not found"));

        List<user> users = userRepository.findByLoginStatus(1);

        int enrolled = 0;
        int skipped = 0;

        for (user u : users) {

            boolean alreadyExists = repo.existsByUserIdAndBatchId(
                    u.getId(),
                    districtBatch.getBatch_id()
            );

            if (alreadyExists) {
                skipped++;
                continue;
            }

            Candidates candidate = new Candidates();

            candidate.setName(u.getFullName());
            candidate.setEmail(u.getEmail());
            candidate.setPhone(u.getContact());
            candidate.setEnrollment_no(u.getEnrollmentNumber());

            candidate.setUserId(u.getId());
            candidate.setBatchId(districtBatch.getBatch_id());

            candidate.setStatus("enrolled");
            candidate.setScore(0.0);
            candidate.setCurrentStage(ExamStage.DISTRICT);

            repo.save(candidate);
            enrolled++;
        }

        return Map.of(
                "message", "Auto enrollment done",
                "batchId", districtBatch.getBatch_id(),
                "batchCode", districtBatch.getBatchCode(),
                "enrolled", enrolled,
                "skipped", skipped
        );
    }

    @Transactional
    public void autoEnrollSingleUser(user u) {

        if (u == null || u.getLoginStatus() != 1) {
            return;
        }

        Batch districtBatch = batchRepository
                .findLatestBatchByLevelAndStatus("district", "upcoming")
                .orElseThrow(() -> new RuntimeException("District batch not found"));

        boolean alreadyExists = repo.existsByUserIdAndBatchId(
                u.getId(),
                districtBatch.getBatch_id()
        );

        if (alreadyExists) {
            return;
        }

        Candidates candidate = new Candidates();

        candidate.setName(u.getFullName());
        candidate.setEmail(u.getEmail());
        candidate.setPhone(u.getContact());
        candidate.setEnrollment_no(u.getEnrollmentNumber());

        candidate.setUserId(u.getId());
        candidate.setBatchId(districtBatch.getBatch_id());

        candidate.setStatus("enrolled");
        candidate.setScore(0.0);
        candidate.setCurrentStage(ExamStage.DISTRICT);

        repo.save(candidate);
    }
}