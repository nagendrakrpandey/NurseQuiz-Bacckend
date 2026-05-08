package Tech.Nagendra.NurseQuiz.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<FaceImage> faceImages;

    // 🔥 getters & setters
    public Long getId() { return id; }

    public String getName() { return name; }

    public List<FaceImage> getFaceImages() { return faceImages; }

    public void setFaceImages(List<FaceImage> faceImages) {
        this.faceImages = faceImages;
    }
}