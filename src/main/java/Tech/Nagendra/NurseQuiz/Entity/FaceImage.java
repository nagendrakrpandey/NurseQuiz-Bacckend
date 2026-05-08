package Tech.Nagendra.NurseQuiz.Entity;

import jakarta.persistence.*;

@Entity
public class FaceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    // 🔥 getters & setters
    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public byte[] getImage() {
        return image;
    }
}