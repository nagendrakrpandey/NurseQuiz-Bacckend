package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.Entity.Batch;
import Tech.Nagendra.NurseQuiz.Service.BatchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    @Autowired
    private BatchService service;

    @GetMapping
    public ResponseEntity<?> getBatches(@RequestParam String level) {
        try {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", service.getByLevel(level)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Batch batch) {
        try {
            Batch saved = service.createBatch(batch);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Batch created successfully",
                    "data", saved
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Batch batch
    ) {
        try {
            Batch updated = service.updateBatch(id, batch);

            return ResponseEntity.ok(Map.of("success", true, "message", "Batch updated successfully", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Batch deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==========================
    // GET SINGLE BATCH BY ID
    // ==========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getBatchById(@PathVariable Long id) {
        try {
            Batch batch = service.getById(id);
            return ResponseEntity.ok(Map.of("success", true, "data", batch));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}