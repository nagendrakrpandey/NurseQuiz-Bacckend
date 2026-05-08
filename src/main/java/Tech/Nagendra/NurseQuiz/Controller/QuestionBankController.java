package Tech.Nagendra.NurseQuiz.Controller;


import Tech.Nagendra.NurseQuiz.Entity.QuestionBank;
import Tech.Nagendra.NurseQuiz.Service.QuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/questionBank")
public class QuestionBankController {

    @Autowired
    private QuestionBankService questionBankService;

    // ==========================
    // CREATE QUESTION BANK
    // ==========================
    @PostMapping("/create")
    public ResponseEntity<?> createQuestionBank(@RequestBody QuestionBank questionBank) {
        try {
            QuestionBank savedBank = questionBankService.createQuestionBank(questionBank);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Question bank created successfully",
                            "data", savedBank
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }

    // ==========================
    // GET ALL QUESTION BANKS
    // ==========================
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllQuestionBanks() {
        try {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "data", questionBankService.getAllQuestionBanks()
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }

    // ==========================
    // GET QUESTION BANK BY ID
    // ==========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionBankById(@PathVariable Long id) {
        try {
            QuestionBank questionBank = questionBankService.getQuestionBankById(id);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "data", questionBank
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }

    // ==========================
    // UPDATE QUESTION BANK
    // ==========================
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuestionBank(
            @PathVariable Long id,
            @RequestBody QuestionBank questionBank
    ) {
        try {
            QuestionBank updatedBank = questionBankService.updateQuestionBank(id, questionBank);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Question bank updated successfully",
                            "data", updatedBank
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }

    // ==========================
    // DELETE QUESTION BANK
    // ==========================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuestionBank(@PathVariable Long id) {
        try {
            questionBankService.deleteQuestionBank(id);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Question bank deleted successfully"
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }
}