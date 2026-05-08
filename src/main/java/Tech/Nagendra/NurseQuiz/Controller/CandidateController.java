package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.DTO.ExamAccessDTO;
import Tech.Nagendra.NurseQuiz.Entity.Candidates;
import Tech.Nagendra.NurseQuiz.Service.CandidateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService service;

    // ==========================
    // GET BY BATCH ID
    // ==========================
    @GetMapping
    public ResponseEntity<?> getCandidates(@RequestParam Long batchId) {
        try {
            List<Candidates> data = service.getByBatchId(batchId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", data
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==========================
    // ADD SINGLE CANDIDATE
    // ==========================
    @PostMapping
    public ResponseEntity<?> addCandidate(@RequestBody Candidates c) {
        try {
            Candidates saved = service.addCandidate(c);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Candidate added successfully",
                    "data", saved
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==========================
    // BULK ADD BY BATCH ID
    // ==========================
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkAdd(@RequestBody Map<String, Object> req) {
        try {
            Object batchIdObj = req.get("batchId");

            if (batchIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "batchId is required"
                ));
            }

            Long batchId = Long.valueOf(batchIdObj.toString());

            List<Map<String, Object>> list =
                    (List<Map<String, Object>>) req.get("candidates");

            if (list == null || list.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Candidates list is empty"
                ));
            }

            List<Candidates> candidates = list.stream().map(c -> {
                Candidates obj = new Candidates();
                obj.setName((String) c.get("name"));
                obj.setEmail((String) c.get("email"));
                obj.setPhone((String) c.get("phone"));
                obj.setEnrollment_no((String) c.get("enrollment_no"));
                obj.setBatchId(batchId);
                return obj;
            }).toList();

            List<Candidates> saved = service.bulkAdd(candidates, batchId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", saved.size() + " candidates added successfully",
                    "data", saved
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==========================
    // UPDATE
    // ==========================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCandidate(
            @PathVariable Long id,
            @RequestBody Candidates c
    ) {
        try {
            Candidates updated = service.updateCandidate(id, c);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Candidate updated successfully",
                    "data", updated
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==========================
    // DELETE
    // ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        try {
            service.deleteCandidate(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==========================
    // VALIDATE EXAM ACCESS
    // ==========================
    @GetMapping("/validate-access")
    public ResponseEntity<?> validateExamAccess(
            @RequestParam Long userId,
            @RequestParam Long batchId
    ) {
        try {
            ExamAccessDTO data = service.validateExamAccess(userId, batchId);

            if (data == null) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "allowed", false,
                        "status", "USER_NOT_FOUND",
                        "message", "User not found"
                ));
            }

            boolean allowed = "ALLOWED".equalsIgnoreCase(data.getAccessStatus());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "allowed", allowed,
                    "status", data.getAccessStatus(),
                    "message", data.getMessage(),
                    "data", data
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "allowed", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==========================
    // AUTO ENROLL DISTRICT CANDIDATES
    // ==========================
    @PostMapping("/autoenroll")
    public ResponseEntity<?> autoEnrollDistrictCandidates() {
        try {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Auto enrollment completed successfully",
                    "data", service.autoEnrollDistrictCandidates()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}