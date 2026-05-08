package Tech.Nagendra.NurseQuiz.Entity;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String text;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private int correctOption;

    @Column(length = 2000)
    private String explanation;

    @Column(name = "batch_id")
    private Long batchId;

    private int marks;
    private String level;
    private String difficulty;

    @ManyToOne
    @JoinColumn(name = "qbank_id")
    private QuestionBank questionBank;

    @Transient
    private List<String> options;

    public Question() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    public void setQuestionBank(QuestionBank questionBank) {
        this.questionBank = questionBank;
    }

    public List<String> getOptions() {
        return Arrays.asList(
                optionA != null ? optionA : "",
                optionB != null ? optionB : "",
                optionC != null ? optionC : "",
                optionD != null ? optionD : ""
        );
    }

    public void setOptions(List<String> options) {
        if (options != null && options.size() == 4) {
            this.optionA = options.get(0);
            this.optionB = options.get(1);
            this.optionC = options.get(2);
            this.optionD = options.get(3);
        }
    }
}