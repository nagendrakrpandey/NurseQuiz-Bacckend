package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.Service.FaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/proctor")
public class ProctorController {

    @Autowired
    private FaceService faceService;

    // ==============================
    // 🔥 REGISTER FACE (WITH EXAM ID)
    // ==============================
    @PostMapping("/register-face")
    public ResponseEntity<?> registerFace(
            @RequestParam String name,
            @RequestParam String examId,
            @RequestParam MultipartFile file
    ) {
        try {

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "ERROR", "message", "File is empty "));
            }

            if (name == null || name.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "ERROR", "message", "Name is required "));
            }

            if (examId == null || examId.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "ERROR", "message", "ExamId is required "));
            }

            // 🔥 FIXED CALL
            Map<String, Object> response = faceService.registerFace(examId, name, file);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "ERROR", "message", "Registration failed "));
        }
    }

    // ==============================
    // 🔥 PROCESS FRAME (WITH EXAM ID)
    // ==============================
    @PostMapping("/check")
    public ResponseEntity<?> check(
            @RequestParam String examId,
            @RequestParam MultipartFile file
    ) {
        try {

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "ERROR", "message", "File is empty "));
            }

            if (examId == null || examId.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "ERROR", "message", "ExamId is required "));
            }

            // 🔥 FIXED CALL
            Map<String, Object> response = faceService.processFrame(examId, file);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("status", "ERROR", "message", "Processing failed "));
        }
    }

    // ==============================
    // 🔥 HEALTH CHECK API
    // ==============================
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok (Map.of("status", "UP", "service", "AI Proctoring Running "));
    }
}