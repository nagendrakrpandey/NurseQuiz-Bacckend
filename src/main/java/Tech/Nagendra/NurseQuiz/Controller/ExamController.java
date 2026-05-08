package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.Enum.ExamStage;
import Tech.Nagendra.NurseQuiz.Service.ExamService;
import Tech.Nagendra.NurseQuiz.Utitlty.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private JwtUtil jwtUtil;

    // ===============================
    // 🔥 SUBMIT EXAM (NO SCORE FROM FRONTEND)
    // ===============================
    @PostMapping("/submit")
    public Object submitExam(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> req
    ) {
        try {

            // 🔐 Token validation
            if (token == null || !token.startsWith("Bearer ")) {
                return Map.of("success", false, "message", "Invalid token");
            }

            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));

            // 🔍 Validate stage
            if (req.get("stage") == null) {
                return Map.of("success", false, "message", "stage is required");
            }

            ExamStage stage;
            try {
                stage = ExamStage.valueOf(req.get("stage").toString().toUpperCase());
            } catch (Exception e) {
                return Map.of("success", false, "message", "Invalid stage value");
            }

            // 🚀 Service call (NO SCORE)
            String result = examService.submitExam(userId, stage);

            return Map.of(
                    "success", true,
                    "message", result,
                    "stage", stage.name()
            );

        } catch (RuntimeException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "Something went wrong"
            );
        }
    }

    // ===============================
    // 🔥 START EXAM
    // ===============================
    @PostMapping("/start")
    public Object startExam(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> req
    ) {
        try {

            if (token == null || !token.startsWith("Bearer ")) {
                return Map.of("success", false, "message", "Invalid token");
            }

            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));

            if (req.get("stage") == null) {
                return Map.of("success", false, "message", "stage is required");
            }

            ExamStage stage = ExamStage.valueOf(req.get("stage").toString().toUpperCase());

            String result = examService.startExam(userId, stage);

            return Map.of(
                    "success", true,
                    "message", result,
                    "stage", stage.name()
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }

    // ===============================
    // 🔥 GET RESULT (FULL STATUS)
    // ===============================
    @GetMapping("/result")
    public Object getResult(
            @RequestHeader("Authorization") String token
    ) {
        try {

            if (token == null || !token.startsWith("Bearer ")) {
                return Map.of("success", false, "message", "Invalid token");
            }

            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));

            return Map.of(
                    "success", true,
                    "data", examService.getExamResult(userId)
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }
}