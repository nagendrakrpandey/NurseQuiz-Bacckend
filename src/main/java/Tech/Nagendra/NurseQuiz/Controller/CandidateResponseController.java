package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.DTO.CandidateResponseDTO;
import Tech.Nagendra.NurseQuiz.Entity.CandidateResponse;
import Tech.Nagendra.NurseQuiz.Service.CandidateResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/responses")
@CrossOrigin(origins = "http://localhost:8080")
public class CandidateResponseController {

    @Autowired
    private CandidateResponseService service;

    // ✅ SAVE RESPONSE
    @PostMapping("/save")
    public ResponseEntity<?> saveResponse(@RequestBody CandidateResponse request) {
        try {
            System.out.println("🔥 API HIT: " + request);

            CandidateResponse saved = service.saveResponse(request);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            System.out.println(" ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ GET ALL
    @GetMapping("/all")
    public ResponseEntity<List<CandidateResponse>> getAllResponses() {
        return ResponseEntity.ok(service.getAllResponses());
    }

    // ✅ GET BY CANDIDATE
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CandidateResponse>> getByCandidateId(@PathVariable Long candidateId) {
        return ResponseEntity.ok(service.getByCandidateId(candidateId));
    }

    // ✅ GET BY BATCH
    @GetMapping("/batch/{batchCode}")
    public ResponseEntity<List<CandidateResponse>> getByBatchCode(@PathVariable String batchCode) {
        return ResponseEntity.ok(service.getByBatchCode(batchCode));
    }

    // ✅ UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateResponse(
            @PathVariable Long id,
            @RequestBody CandidateResponse updated
    ) {
        try {
            return ResponseEntity.ok(service.updateResponse(id, updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteResponse(@PathVariable Long id) {
        try {
            service.deleteResponse(id);
            return ResponseEntity.ok("Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{candidateId}")
    public ResponseEntity<?> getCandidateResponses(@PathVariable Long candidateId) {
        try {
            List<CandidateResponseDTO> data = service.getCandidateResponses(candidateId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", data.size(),
                    "data", data
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}