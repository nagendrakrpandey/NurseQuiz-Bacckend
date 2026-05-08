package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.Entity.CandidateEvidance;
import Tech.Nagendra.NurseQuiz.Service.CandidateEvidanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/evidence")
public class CandidateEvidanceController {

    @Autowired
    private CandidateEvidanceService service;

    private static final String UPLOAD_DIR = "uploads/";

    private Integer getDocIdByType(String type) {
        if (type == null) return 0;

        switch (type.toLowerCase()) {
            case "selfie": return 1;
            case "document": return 2;
            case "photo": return 3;
            case "video": return 4;
            default: return 0;
        }
    }

    // 🔥 FILE EXTENSION RESOLVE
    private String getExtension(String contentType, String type) {
        if (type.equalsIgnoreCase("video")) {
            return ".mp4";
        }

        if (contentType == null) return ".jpg";

        if (contentType.contains("png")) return ".png";
        if (contentType.contains("jpeg")) return ".jpeg";
        if (contentType.contains("jpg")) return ".jpg";

        return ".jpg";
    }

    // ✅ GET EVIDENCE BY USER ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEvidenceByUserId(@PathVariable Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "userId is required"
                ));
            }

            List<CandidateEvidance> evidenceList = service.getEvidenceByUserId(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", evidenceList.size(),
                    "data", evidenceList
            ));

        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Evidence fetch failed: " + ex.getMessage()
            ));
        }
    }

    // ✅ SINGLE UPLOAD
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamMemberId") Long teamMemberId,
            @RequestParam(value = "teamMemberName", required = false) String teamMemberName,
            @RequestParam("type") String type,
            @RequestParam("batchCode") String batchCode
    ) {
        try {

            // ✅ VALIDATION
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "file is required"
                ));
            }

            if (teamMemberId == null || batchCode == null || batchCode.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "teamMemberId and batchCode are required"
                ));
            }

            // ✅ CREATE FOLDER IF NOT EXISTS
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 🔥 FILE NAME GENERATE
            String extension = getExtension(file.getContentType(), type);
            String fileName = type + "_" + teamMemberId + "_" + System.currentTimeMillis() + extension;

            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // ✅ SAVE FILE
            Files.write(filePath, file.getBytes());

            // ✅ SAVE DB
            CandidateEvidance e = new CandidateEvidance();
            e.setTeamMemberId(teamMemberId);
            e.setTeamMemberName(teamMemberName != null ? teamMemberName : "Candidate");
            e.setBatchCode(batchCode);
            e.setType(type.toLowerCase());
            e.setDocId(getDocIdByType(type));

            // 🔥 IMPORTANT: store file path
            e.setImageData(filePath.toString());

            CandidateEvidance saved = service.saveOrUpdate(e);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "id", saved.getId(),
                    "filePath", filePath.toString(),
                    "message", "File uploaded successfully"
            ));

        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "File save failed: " + ex.getMessage()
            ));
        }
    }

    @GetMapping("/random-media/team/{teamMemberId}")
    public ResponseEntity<?> getRandomPhotoVideoByTeamMemberId(@PathVariable Long teamMemberId) {
        try {
            List<CandidateEvidance> mediaList =
                    service.getRandomPhotoVideoByTeamMemberId(teamMemberId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", mediaList.size(),
                    "data", mediaList
            ));

        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Random media fetch failed: " + ex.getMessage()
            ));
        }
    }
}