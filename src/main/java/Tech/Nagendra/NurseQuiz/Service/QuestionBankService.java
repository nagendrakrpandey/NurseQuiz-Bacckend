package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.Entity.QuestionBank;
import Tech.Nagendra.NurseQuiz.Repository.QuestionBankRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionBankService {

    @Autowired
    private QuestionBankRepository questionBankRepository;

    // ==========================
    // CREATE QUESTION BANK
    // ==========================
    public QuestionBank createQuestionBank(QuestionBank questionBank) {

        if (questionBank == null) {
            throw new RuntimeException("Question bank data is required");
        }

        if (questionBank.getBankName() == null || questionBank.getBankName().trim().isEmpty()) {
            throw new RuntimeException("Question bank name is required");
        }

        return questionBankRepository.save(questionBank);
    }

    // ==========================
    // GET ALL QUESTION BANKS
    // ==========================
    public List<QuestionBank> getAllQuestionBanks() {
        return questionBankRepository.findAll();
    }

    // ==========================
    // GET QUESTION BANK BY ID
    // ==========================
    public QuestionBank getQuestionBankById(Long id) {

        if (id == null) {
            throw new RuntimeException("Question bank id is required");
        }

        return questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + id));
    }

    // ==========================
    // UPDATE QUESTION BANK
    // ==========================
    public QuestionBank updateQuestionBank(Long id, QuestionBank questionBank) {

        if (id == null) {
            throw new RuntimeException("Question bank id is required");
        }

        if (questionBank == null) {
            throw new RuntimeException("Question bank data is required");
        }

        QuestionBank existing = questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + id));

        if (questionBank.getBankName() != null && !questionBank.getBankName().trim().isEmpty()) {
            existing.setBankName(questionBank.getBankName());
        }

        if (questionBank.getDescription() != null) {
            existing.setDescription(questionBank.getDescription());
        }

        return questionBankRepository.save(existing);
    }

    // ==========================
    // DELETE QUESTION BANK
    // ==========================
    public void deleteQuestionBank(Long id) {

        if (id == null) {
            throw new RuntimeException("Question bank id is required");
        }

        if (!questionBankRepository.existsById(id)) {
            throw new RuntimeException("Question bank not found with id: " + id);
        }

        questionBankRepository.deleteById(id);
    }
}