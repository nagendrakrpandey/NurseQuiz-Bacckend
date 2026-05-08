package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.Entity.Question;
import Tech.Nagendra.NurseQuiz.Service.QuestionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService service;

    // ==========================
    // GET QUESTIONS BY BATCH ID
    // ==========================
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<?> getQuestionsByBatchId(@PathVariable Long batchId) {

        if (batchId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "batchId is required")
            );
        }

        List<Question> data = service.getByBatchId(batchId);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "count", data.size(),
                        "data", data
                )
        );
    }

    // ==========================
    // GET QUESTIONS BY QUESTION BANK ID
    // ==========================
    @GetMapping("/bank/{qbankId}")
    public ResponseEntity<?> getQuestionsByQuestionBankId(@PathVariable Long qbankId) {

        if (qbankId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "qbankId is required")
            );
        }

        List<Question> data = service.getByQuestionBankId(qbankId);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "count", data.size(),
                        "data", data
                )
        );
    }

    // ==========================
    // GET QUESTIONS BY QUESTION BANK ID + BATCH ID
    // ==========================
    @GetMapping("/bank/{qbankId}/batch/{batchId}")
    public ResponseEntity<?> getQuestionsByQuestionBankAndBatch(
            @PathVariable Long qbankId,
            @PathVariable Long batchId
    ) {

        if (qbankId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "qbankId is required")
            );
        }

        if (batchId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "batchId is required")
            );
        }

        List<Question> data = service.getByQuestionBankIdAndBatchId(qbankId, batchId);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "count", data.size(),
                        "data", data
                )
        );
    }

    // ==========================
    // ADD QUESTION WITH QBANK ID + BATCH ID
    // ==========================
    @PostMapping("/add/bank/{qbankId}")
    public ResponseEntity<?> addQuestionByBankOnly(
            @PathVariable Long qbankId,
            @RequestBody Question q
    ) {
        if (qbankId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "qbankId is required")
            );
        }

        if (q.getText() == null || q.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Question text is required")
            );
        }

        Question saved = service.addQuestionToBankOnly(qbankId, q);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Question saved in question bank successfully",
                        "data", saved
                )
        );
    }

    // ==========================
    // UPDATE QUESTION WITH QBANK ID + BATCH ID
    // ==========================
    @PostMapping("/bulk/bank/{qbankId}")
    public ResponseEntity<?> bulkUploadByBankOnly(
            @PathVariable Long qbankId,
            @RequestBody List<Question> questions
    ) {
        if (qbankId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "qbankId is required")
            );
        }

        if (questions == null || questions.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Questions list cannot be empty")
            );
        }

        List<Question> saved = service.bulkSaveToBankOnly(qbankId, questions);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", saved.size() + " questions saved in question bank successfully",
                        "data", saved
                )
        );
    }

    // ==========================
    // DELETE QUESTION
    // ==========================
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {

        service.deleteQuestion(questionId);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Deleted successfully"
                )
        );
    }

    // ==========================
    // BULK UPLOAD WITH QBANK ID + BATCH ID
    // ==========================

    @PostMapping("/bulk/{qbankId}/{batchId}")
    public ResponseEntity<?> bulkUpload(
            @PathVariable Long qbankId,
            @PathVariable Long batchId,
            @RequestBody List<Question> questions
    ) {

        if (qbankId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "qbankId is required")
            );
        }

        if (batchId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "batchId is required")
            );
        }

        if (questions == null || questions.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Questions list cannot be empty")
            );
        }

        List<Question> saved = service.bulkSave(qbankId, batchId, questions);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", saved.size() + " questions saved successfully",
                        "data", saved
                )
        );
    }
}