package Tech.Nagendra.NurseQuiz.Service;


import Tech.Nagendra.NurseQuiz.Entity.Question;
import Tech.Nagendra.NurseQuiz.Entity.QuestionBank;
import Tech.Nagendra.NurseQuiz.Repository.BatchRepository;
import Tech.Nagendra.NurseQuiz.Repository.QuestionBankRepository;
import Tech.Nagendra.NurseQuiz.Repository.QuestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository repo;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private BatchRepository batchRepository;

    public List<Question> getByLevel(String level) {
        return repo.findByLevel(level);
    }

    public List<Question> getByBatchId(Long batchId) {
        if (batchId == null) {
            throw new RuntimeException("Batch id is required");
        }

        batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));

        return repo.findByBatchId(batchId);
    }

    public List<Question> getByQuestionBankId(Long qbankId) {
        if (qbankId == null) {
            throw new RuntimeException("Question bank id is required");
        }

        questionBankRepository.findById(qbankId)
                .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + qbankId));

        return repo.findByQuestionBank_Id(qbankId);
    }

    public List<Question> getByQuestionBankIdAndBatchId(Long qbankId, Long batchId) {
        if (qbankId == null) {
            throw new RuntimeException("Question bank id is required");
        }

        if (batchId == null) {
            throw new RuntimeException("Batch id is required");
        }

        questionBankRepository.findById(qbankId)
                .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + qbankId));

        batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));

        return repo.findByQuestionBank_IdAndBatchId(qbankId, batchId);
    }

    public Question addQuestion(Long qbankId, Long batchId, Question q) {
        QuestionBank questionBank = questionBankRepository.findById(qbankId)
                .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + qbankId));

        batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));

        validateQuestion(q);

        q.setQuestionBank(questionBank);
        q.setBatchId(batchId);

        return repo.save(q);
    }

    public Question addQuestionToBankOnly(Long qbankId, Question q) {
        QuestionBank questionBank = questionBankRepository.findById(qbankId)
                .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + qbankId));

        validateQuestion(q);

        q.setQuestionBank(questionBank);
        q.setBatchId(null);

        return repo.save(q);
    }

    public Question updateQuestion(Long id, Long qbankId, Long batchId, Question q) {
        if (id == null) {
            throw new RuntimeException("Question id is required");
        }

        Question existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        if (q.getText() != null && !q.getText().trim().isEmpty()) {
            existing.setText(q.getText());
        }

        if (q.getOptions() != null && q.getOptions().size() == 4) {
            existing.setOptions(q.getOptions());
        }

        if (q.getCorrectOption() >= 0 && q.getCorrectOption() <= 3) {
            existing.setCorrectOption(q.getCorrectOption());
        }

        if (q.getExplanation() != null) {
            existing.setExplanation(q.getExplanation());
        }

        if (q.getMarks() > 0) {
            existing.setMarks(q.getMarks());
        }

        if (q.getLevel() != null && !q.getLevel().trim().isEmpty()) {
            existing.setLevel(q.getLevel());
        }

        if (q.getDifficulty() != null && !q.getDifficulty().trim().isEmpty()) {
            existing.setDifficulty(q.getDifficulty());
        }

        if (qbankId != null) {
            QuestionBank questionBank = questionBankRepository.findById(qbankId)
                    .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + qbankId));
            existing.setQuestionBank(questionBank);
        }

        if (batchId != null) {
            batchRepository.findById(batchId)
                    .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));
            existing.setBatchId(batchId);
        }

        validateQuestion(existing);

        return repo.save(existing);
    }

    public void deleteQuestion(Long id) {
        if (id == null) {
            throw new RuntimeException("Question id is required");
        }

        if (!repo.existsById(id)) {
            throw new RuntimeException("Question not found with id: " + id);
        }

        repo.deleteById(id);
    }

    public List<Question> bulkSave(Long qbankId, Long batchId, List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("Question list is empty");
        }

        QuestionBank questionBank = questionBankRepository.findById(qbankId)
                .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + qbankId));

        batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));

        List<Question> validQuestions = new ArrayList<>();

        for (Question q : questions) {
            try {
                validateQuestion(q);
                q.setQuestionBank(questionBank);
                q.setBatchId(batchId);
                validQuestions.add(q);
            } catch (Exception e) {
                System.out.println("Skipping invalid question: " + e.getMessage());
            }
        }

        if (validQuestions.isEmpty()) {
            throw new RuntimeException("No valid questions to save");
        }

        return repo.saveAll(validQuestions);
    }

    public List<Question> bulkSaveToBankOnly(Long qbankId, List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("Question list is empty");
        }

        QuestionBank questionBank = questionBankRepository.findById(qbankId)
                .orElseThrow(() -> new RuntimeException("Question bank not found with id: " + qbankId));

        List<Question> validQuestions = new ArrayList<>();

        for (Question q : questions) {
            try {
                validateQuestion(q);
                q.setQuestionBank(questionBank);
                q.setBatchId(null);
                validQuestions.add(q);
            } catch (Exception e) {
                System.out.println("Skipping invalid question: " + e.getMessage());
            }
        }

        if (validQuestions.isEmpty()) {
            throw new RuntimeException("No valid questions to save");
        }

        return repo.saveAll(validQuestions);
    }

    private void validateQuestion(Question q) {
        if (q == null) {
            throw new RuntimeException("Question cannot be null");
        }

        if (q.getText() == null || q.getText().trim().isEmpty()) {
            throw new RuntimeException("Question text is required");
        }

        if (q.getOptions() == null || q.getOptions().size() != 4) {
            throw new RuntimeException("Exactly 4 options are required");
        }

        for (String opt : q.getOptions()) {
            if (opt == null || opt.trim().isEmpty()) {
                throw new RuntimeException("Options cannot be empty");
            }
        }

        if (q.getCorrectOption() < 0 || q.getCorrectOption() > 3) {
            q.setCorrectOption(0);
        }

        if (q.getMarks() <= 0) {
            q.setMarks(1);
        }

        if (q.getDifficulty() == null || q.getDifficulty().trim().isEmpty()) {
            q.setDifficulty("medium");
        }

        if (q.getLevel() == null || q.getLevel().trim().isEmpty()) {
            q.setLevel("basic");
        }
    }
}