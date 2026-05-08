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
import java.util.Optional;

@Service
public class CandidateResponseService {

    @Autowired
    private CandidateResponseRepository responseRepository;

    @Autowired
    private QuestionRepository questionRepository;

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

        Optional<CandidateResponse> existingResponse =
                responseRepository.findByCandidateIdAndQuestionId(
                        request.getCandidateId(),
                        request.getQuestionId()
                );

        CandidateResponse response;

        if (existingResponse.isPresent()) {
            response = existingResponse.get();

            response.setAnsId(request.getAnsId());
            response.setTabSwitchCount(request.getTabSwitchCount());
            response.setLatitude(request.getLatitude());
            response.setLongitude(request.getLongitude());
            response.setLocationName(request.getLocationName());
            response.setIsCorrect(isCorrect);
            response.setSubmitTime(LocalDateTime.now());
            response.setModifiedOn(LocalDateTime.now());
            response.setModifiedBy(request.getModifiedBy());

        } else {
            response = request;

            response.setIsCorrect(isCorrect);
            response.setSubmitTime(LocalDateTime.now());
            response.setIsActive(true);
            response.setCreatedOn(LocalDateTime.now());
        }

        CandidateResponse saved = responseRepository.save(response);

        System.out.println("✅ Saved Response ID: " + saved.getRecordId());

        return saved;
    }

    public List<CandidateResponse> getAllResponses() {
        return responseRepository.findAll();
    }

    public List<CandidateResponse> getByCandidateId(Long candidateId) {
        return responseRepository.findByCandidateId(candidateId);
    }

    public List<CandidateResponse> getByBatchCode(String batchCode) {
        return responseRepository.findByBatchCode(batchCode);
    }

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

    public void deleteResponse(Long id) {
        responseRepository.deleteById(id);
    }

    public List<CandidateResponseDTO> getCandidateResponses(Long candidateId) {
        return responseRepository.getCandidateResponseDetails(candidateId);
    }
}