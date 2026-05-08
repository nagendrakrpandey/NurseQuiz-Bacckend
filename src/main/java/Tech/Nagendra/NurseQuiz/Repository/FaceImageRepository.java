package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.FaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaceImageRepository extends JpaRepository<FaceImage, Long> {


    List<FaceImage> findByCandidateId(Long candidateId);
}