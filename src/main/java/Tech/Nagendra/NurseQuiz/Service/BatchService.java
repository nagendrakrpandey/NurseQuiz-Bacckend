package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.Entity.Batch;
import Tech.Nagendra.NurseQuiz.Repository.BatchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    @Autowired
    private BatchRepository repo;

    // ==========================
    // GET BATCHES BY LEVEL
    // ==========================
    public List<Batch> getByLevel(String level) {

        if (level == null || level.trim().isEmpty()) {
            throw new RuntimeException("Level is required");
        }

        List<Batch> batches = repo.findByLevel(level);

        for (Batch b : batches) {
            Long batchId = b.getBatch_id();

            Long questionCount = repo.countQuestionsByBatchId(batchId);
            b.setTotal_questions(questionCount != null ? questionCount.intValue() : 0);

            Long candidateCount = repo.countCandidatesByBatchId(batchId);
            b.setEnrolled_students(candidateCount != null ? candidateCount.intValue() : 0);

            // Old data ke liye agar batchCode null/blank hai to auto-generate karke save karo
            if (b.getBatchCode() == null || b.getBatchCode().trim().isEmpty()) {
                b.setBatchCode(generateBatchCode(b));
                repo.save(b);
            }
        }

        return batches;
    }

    // ==========================
    // CREATE BATCH
    // ==========================
    public Batch createBatch(Batch batch) {

        if (batch == null) {
            throw new RuntimeException("Batch data is required");
        }

        if (batch.getLevel() == null || batch.getLevel().trim().isEmpty()) {
            throw new RuntimeException("Level is required");
        }

        if (batch.getDuration() <= 0) {
            throw new RuntimeException("Duration is required");
        }

        if (batch.getStatus() == null || batch.getStatus().trim().isEmpty()) {
            batch.setStatus("upcoming");
        }

        if (batch.getMax_tab_switches() < 0) {
            batch.setMax_tab_switches(0);
        }

        // Agar frontend se batchCode aaya hai
        if (batch.getBatchCode() != null && !batch.getBatchCode().trim().isEmpty()) {
            String batchCode = batch.getBatchCode().trim();

            Optional<Batch> existing = repo.findByBatchCode(batchCode);
            if (existing.isPresent()) {
                throw new RuntimeException("Batch code already exists: " + batchCode);
            }

            batch.setBatchCode(batchCode);
            batch.setTotal_questions(0);
            batch.setEnrolled_students(0);

            return repo.save(batch);
        }

        // Agar frontend se batchCode nahi aaya, pehle save karke id generate karo
        batch.setBatchCode(null);
        batch.setTotal_questions(0);
        batch.setEnrolled_students(0);

        Batch saved = repo.save(batch);

        saved.setBatchCode(generateBatchCode(saved));

        return repo.save(saved);
    }

    // ==========================
    // UPDATE BATCH
    // ==========================
    public Batch updateBatch(Long id, Batch batch) {

        if (id == null) {
            throw new RuntimeException("Batch id is required");
        }

        if (batch == null) {
            throw new RuntimeException("Batch data is required");
        }

        Batch existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + id));

        // Batch code update optional hai
        if (batch.getBatchCode() != null && !batch.getBatchCode().trim().isEmpty()) {
            String newBatchCode = batch.getBatchCode().trim();

            Optional<Batch> duplicate = repo.findByBatchCode(newBatchCode);

            if (duplicate.isPresent() && !duplicate.get().getBatch_id().equals(id)) {
                throw new RuntimeException("Batch code already exists: " + newBatchCode);
            }

            existing.setBatchCode(newBatchCode);
        }

        // Agar old record me batchCode null hai to generate kar do
        if (existing.getBatchCode() == null || existing.getBatchCode().trim().isEmpty()) {
            existing.setBatchCode(generateBatchCode(existing));
        }

        if (batch.getLevel() != null && !batch.getLevel().trim().isEmpty()) {
            existing.setLevel(batch.getLevel());
        }

        if (batch.getStart_date() != null) {
            existing.setStart_date(batch.getStart_date());
        }

        if (batch.getStart_time() != null) {
            existing.setStart_time(batch.getStart_time());
        }

        if (batch.getEnd_date() != null) {
            existing.setEnd_date(batch.getEnd_date());
        }

        if (batch.getEnd_time() != null) {
            existing.setEnd_time(batch.getEnd_time());
        }

        if (batch.getDuration() > 0) {
            existing.setDuration(batch.getDuration());
        }

        if (batch.getStatus() != null && !batch.getStatus().trim().isEmpty()) {
            existing.setStatus(batch.getStatus());
        }

        existing.setRandom_photo(batch.isRandom_photo());
        existing.setRandom_video(batch.isRandom_video());
        existing.setAi_monitoring(batch.isAi_monitoring());
        existing.setTab_switch_detection(batch.isTab_switch_detection());

        if (batch.getMax_tab_switches() >= 0) {
            existing.setMax_tab_switches(batch.getMax_tab_switches());
        }

        Long questionCount = repo.countQuestionsByBatchId(existing.getBatch_id());
        existing.setTotal_questions(questionCount != null ? questionCount.intValue() : 0);

        Long candidateCount = repo.countCandidatesByBatchId(existing.getBatch_id());
        existing.setEnrolled_students(candidateCount != null ? candidateCount.intValue() : 0);

        return repo.save(existing);
    }

    // ==========================
    // DELETE BATCH
    // ==========================
    public void delete(Long id) {

        if (id == null) {
            throw new RuntimeException("Batch id is required");
        }

        if (!repo.existsById(id)) {
            throw new RuntimeException("Batch not found with id: " + id);
        }

        repo.deleteById(id);
    }

    // ==========================
    // AUTO BATCH CODE GENERATOR
    // Example: DISTRICT_BATCH_001
    // ==========================
    private String generateBatchCode(Batch batch) {

        String level = batch.getLevel() != null && !batch.getLevel().trim().isEmpty()
                ? batch.getLevel().trim().toUpperCase()
                : "BATCH";

        Long id = batch.getBatch_id();

        return level + "_BATCH_" + String.format("%03d", id);
    }

    // ==========================
// GET SINGLE BATCH BY ID
// ==========================
    public Batch getById(Long id) {

        if (id == null) {
            throw new RuntimeException("Batch id is required");
        }

        Batch batch = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Batch not found with id: " + id)
                );

        // total questions
        Long questionCount = repo.countQuestionsByBatchId(batch.getBatch_id());

        batch.setTotal_questions(
                questionCount != null
                        ? questionCount.intValue()
                        : 0
        );

        // enrolled students
        Long candidateCount = repo.countCandidatesByBatchId(batch.getBatch_id());

        batch.setEnrolled_students(
                candidateCount != null
                        ? candidateCount.intValue()
                        : 0
        );

        // auto batch code generate if null
        if (batch.getBatchCode() == null ||
                batch.getBatchCode().trim().isEmpty()) {

            batch.setBatchCode(generateBatchCode(batch));

            repo.save(batch);
        }

        return batch;
    }
}