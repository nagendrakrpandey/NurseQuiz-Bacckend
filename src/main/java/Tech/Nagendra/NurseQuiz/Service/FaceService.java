package Tech.Nagendra.NurseQuiz.Service;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.*;
import java.util.*;

@Service
public class FaceService {

    private CascadeClassifier faceDetector;

    // 🔥 examId → (candidateName → faces)
    private Map<String, Map<String, List<Mat>>> examData = new HashMap<>();

    // ============================
    // LOAD CASCADE
    // ============================
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("haarcascade_frontalface_default.xml");

            File tempFile = File.createTempFile("face", ".xml");
            tempFile.deleteOnExit();

            try (InputStream is = resource.getInputStream();
                 OutputStream os = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            faceDetector = new CascadeClassifier(tempFile.getAbsolutePath());

            System.out.println(faceDetector.empty() ? "XML Load Failed " : "XML Loaded ✅");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================
    // REGISTER FACE
    // ============================
    public Map<String, Object> registerFace(String examId, String name, MultipartFile file) throws Exception {

        Mat img = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_GRAYSCALE);

        if (isBlur(img)) {
            return Map.of("status", "WARNING", "message", "Blurry Face ⚠️");
        }

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(img, faces, 1.1, 5);

        if (faces.toArray().length != 1) {
            return Map.of("status", "WARNING", "message", "Provide single clear face ");
        }

        Rect rect = faces.toArray()[0];
        Mat face = preprocessFace(new Mat(img, rect));

        // 🔥 exam map init
        examData.putIfAbsent(examId, new HashMap<>());
        Map<String, List<Mat>> candidateMap = examData.get(examId);

        // 🔥 max 3 candidates
        if (!candidateMap.containsKey(name) && candidateMap.size() >= 3) {
            return Map.of("status", "WARNING", "message", "Only 3 candidates allowed ");
        }

        candidateMap.putIfAbsent(name, new ArrayList<>());

        // 🔥 max 3 faces per candidate
        if (candidateMap.get(name).size() >= 3) {
            return Map.of("status", "WARNING", "message", "Max 3 faces allowed");
        }

        candidateMap.get(name).add(face);

        return Map.of("status", "SUCCESS", "message", "Face Registered ✅");
    }

    // ============================
    // PROCESS FRAME
    // ============================
    public Map<String, Object> processFrame(String examId, MultipartFile file) throws Exception {

        Map<String, List<Mat>> candidateMap = examData.get(examId);

        if (candidateMap == null || candidateMap.isEmpty()) {
            return Map.of("status", "ERROR", "message", "No candidates registered ❌");
        }

        Mat frame = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_GRAYSCALE);

        if (isBlur(frame)) {
            return Map.of("status", "WARNING", "message", "Blurry Frame ⚠️");
        }

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(frame, faces, 1.1, 5);

        Rect[] detectedFaces = faces.toArray();

        int detected = detectedFaces.length;
        int expected = candidateMap.size();

        if (detected == 0) {
            return Map.of("status", "WARNING", "message", "No Face Detected ⚠️");
        }

        if (detected > expected) {
            return Map.of("status", "WARNING",
                    "message", (detected - expected) + " Extra Candidate Detected ⚠️");
        }

        Set<String> matched = new HashSet<>();

        // 🔥 FACE → MATCH
        for (Rect rect : detectedFaces) {

            Mat detectedFace = preprocessFace(new Mat(frame, rect));

            String bestMatch = null;
            double bestScore = Double.MAX_VALUE;

            for (String name : candidateMap.keySet()) {

                for (Mat stored : candidateMap.get(name)) {

                    double score = compareFaces(stored, detectedFace);

                    if (score < bestScore) {
                        bestScore = score;
                        bestMatch = name;
                    }
                }
            }

            if (bestScore < 40) {
                matched.add(bestMatch);
            }
        }

        List<String> missing = new ArrayList<>();

        for (String name : candidateMap.keySet()) {
            if (!matched.contains(name)) {
                missing.add(name);
            }
        }

        if (matched.size() == expected) {
            return Map.of("status", "SUCCESS", "message", "All Candidates Verified ✅");
        }

        if (detected < expected) {
            return Map.of("status", "WARNING",
                    "message", "Missing Candidates ⚠️",
                    "missing", missing);
        }

        return Map.of("status", "WARNING",
                "message", "Face Not Matched ❌",
                "notMatched", missing);
    }

    // ============================
    // COMPARE
    // ============================
    private double compareFaces(Mat img1, Mat img2) {
        Mat diff = new Mat();
        Core.absdiff(img1, img2, diff);
        Scalar sum = Core.sumElems(diff);
        return sum.val[0] / (img1.rows() * img1.cols());
    }

    // ============================
    // PREPROCESS
    // ============================
    private Mat preprocessFace(Mat face) {
        Mat resized = new Mat();
        Imgproc.resize(face, resized, new Size(200, 200));
        Mat equalized = new Mat();
        Imgproc.equalizeHist(resized, equalized);
        return equalized;
    }

    // ============================
    // BLUR CHECK
    // ============================
    private boolean isBlur(Mat image) {
        Mat lap = new Mat();
        Imgproc.Laplacian(image, lap, CvType.CV_64F);
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(lap, new MatOfDouble(), std);
        double variance = Math.pow(std.get(0, 0)[0], 2);
        return variance < 100;
    }
}