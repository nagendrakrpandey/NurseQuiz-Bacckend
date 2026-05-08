package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.Entity.Document;
import Tech.Nagendra.NurseQuiz.Repository.DocumentRepo;
import Tech.Nagendra.NurseQuiz.Utitlty.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepo repo;

    @Autowired
    private JwtUtil jwtUtil;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    // ==============================
    // 🔥 SAVE OR UPDATE DOCUMENTS
    // ==============================
    public Document saveDocuments(
            MultipartFile registrationCert,
            MultipartFile teamLeadId,
            MultipartFile nursingCouncilReg,
            String organizationId,
            String token
    ) throws Exception {

        Long userId = jwtUtil.extractUserId(token);

        // ✅ Create folder if not exists
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // ✅ Get existing document OR create new
        Document doc = repo.findByUserId(userId).orElse(new Document());

        doc.setUserId(userId);
        doc.setOrganizationId(organizationId);

        // ==============================
        // 🔥 HANDLE EACH FILE (UPDATE ONLY IF PRESENT)
        // ==============================

        if (registrationCert != null && !registrationCert.isEmpty()) {
            validateZip(registrationCert);
            String regPath = saveFile(registrationCert);
            doc.setRegistrationCertPath(regPath);
        }

        if (teamLeadId != null && !teamLeadId.isEmpty()) {
            validateZip(teamLeadId);
            String teamPath = saveFile(teamLeadId);
            doc.setTeamLeadIdPath(teamPath);
        }

        if (nursingCouncilReg != null && !nursingCouncilReg.isEmpty()) {
            validateZip(nursingCouncilReg);
            String councilPath = saveFile(nursingCouncilReg);
            doc.setNursingCouncilRegPath(councilPath);
        }

        return repo.save(doc);
    }

    // ==============================
    // 🔥 FILE SAVE METHOD
    // ==============================
    private String saveFile(MultipartFile file) throws Exception {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File dest = new File(UPLOAD_DIR + fileName);
        file.transferTo(dest);

        return fileName;
    }

    // ==============================
    // 🔥 ZIP VALIDATION (SAFE)
    // ==============================
    private void validateZip(MultipartFile file) {

        String name = file.getOriginalFilename();

        if (name == null || !name.toLowerCase().endsWith(".zip")) {
            throw new RuntimeException("Only ZIP files allowed: " + name);
        }
    }

    // ==============================
    // 🔥 GET DOCUMENTS
    // ==============================
    public Document getDocuments(String token) {

        Long userId = jwtUtil.extractUserId(token);

        return repo.findByUserId(userId).orElse(null);
    }
    public Document getByUserId(Long userId) {
        return repo.findByUserId(userId).orElse(null);
    }
}