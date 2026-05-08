package Tech.Nagendra.NurseQuiz.Model;

import org.opencv.core.Mat;

public class CandidateFace {

    private String name;
    private Mat faceImage;

    public CandidateFace(String name, Mat faceImage) {
        this.name = name;
        this.faceImage = faceImage;
    }

    public String getName() { return name; }
    public Mat getFaceImage() { return faceImage; }
}