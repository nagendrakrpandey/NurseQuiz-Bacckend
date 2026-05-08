package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.DTO.CandidateResponseDTO;
import Tech.Nagendra.NurseQuiz.Entity.CandidateResponse;
import Tech.Nagendra.NurseQuiz.Entity.Question;
import Tech.Nagendra.NurseQuiz.Repository.CandidateResponseRepository;
import Tech.Nagendra.NurseQuiz.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CandidateResponseService {

    @Autowired
    private CandidateResponseRepository responseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // ✅ FIXED SAVE METHOD (NO CRASH)
    public CandidateResponse saveResponse(CandidateResponse request) {

        System.out.println("📩 Incoming Request: " + request);

        Question question = questionRepository.findById(request.getQuestionId())
                .orElse(null);

        boolean isCorrect = false;

        if (question == null) {
            System.out.println("❌ Question NOT FOUND: " + request.getQuestionId());
        } else {
            if (request.getAnsId() != null) {
                isCorrect = request.getAnsId()
                        .equals(String.valueOf(question.getCorrectOption()));
            }
        }

        // ✅ always set fields
        request.setIsCorrect(isCorrect);
        request.setSubmitTime(LocalDateTime.now());
        request.setIsActive(true);
        request.setCreatedOn(LocalDateTime.now());

        CandidateResponse saved = responseRepository.save(request);

        System.out.println("✅ Saved Response ID: " + saved.getRecordId());

        return saved;
    }

    // ✅ GET ALL
    public List<CandidateResponse> getAllResponses() {
        return responseRepository.findAll();
    }

    // ✅ GET BY CANDIDATE
    public List<CandidateResponse> getByCandidateId(Long candidateId) {
        return responseRepository.findByCandidateId(candidateId);
    }

    // ✅ GET BY BATCH
    public List<CandidateResponse> getByBatchCode(String batchCode) {
        return responseRepository.findByBatchCode(batchCode);
    }

    // ✅ UPDATE
    public CandidateResponse updateResponse(Long id, CandidateResponse updated) {

        CandidateResponse existing = responseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        existing.setAnsId(updated.getAnsId());
        existing.setTabSwitchCount(updated.getTabSwitchCount());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setLocationName(updated.getLocationName());
        existing.setModifiedOn(LocalDateTime.now());
        existing.setModifiedBy(updated.getModifiedBy());

        return responseRepository.save(existing);
    }

    // ✅ DELETE
    public void deleteResponse(Long id) {
        responseRepository.deleteById(id);
    }

    public List<CandidateResponseDTO> getCandidateResponses(Long candidateId) {
        return responseRepository.getCandidateResponseDetails(candidateId);
    }
}