package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.Entity.Document;
import Tech.Nagendra.NurseQuiz.Entity.Registration;
import Tech.Nagendra.NurseQuiz.Entity.TeamMember;
import Tech.Nagendra.NurseQuiz.Service.DocumentService;
import Tech.Nagendra.NurseQuiz.Service.RegistrationService;
import Tech.Nagendra.NurseQuiz.Service.TeamMemberService;
import Tech.Nagendra.NurseQuiz.Utitlty.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/register")
//@CrossOrigin("*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private TeamMemberService teamService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private JwtUtil jwtUtil;

    private boolean isValid(String token) {
        if (token == null || token.isBlank()) return false;
        return jwtUtil.validateToken(token);
    }

    private String cleanToken(String token) {
        return token.replace("Bearer ", "").trim();
    }

    // ==============================
    // SAVE ORGANIZATION - STEP 1
    // ==============================

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(
            @RequestHeader("Authorization") String token,
            @RequestParam String organizationName,
            @RequestParam String hospitalRegisteredId,
            @RequestParam String spocName,
            @RequestParam String hospitalCategory,
            @RequestParam String address,
            @RequestParam String pincode,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam String orgEmail,
            @RequestParam String orgPhone,
            @RequestParam(required = false) MultipartFile hospitalRegistrationCertificate,
            @RequestParam(required = false) String hospitalRegistrationCertificatePath
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid or missing token"));
            }
            Registration req = new Registration();
            req.setOrganizationName(organizationName);
            req.setHospitalRegisteredId(hospitalRegisteredId);
            req.setSpocName(spocName);
            req.setHospitalCategory(hospitalCategory);
            req.setAddress(address);
            req.setPincode(pincode);
            req.setState(state);
            req.setDistrict(district);
            req.setOrgEmail(orgEmail);
            req.setOrgPhone(orgPhone);

            Registration saved = registrationService.save(
                    req,
                    cleanToken(token),
                    hospitalRegistrationCertificate,
                    hospitalRegistrationCertificatePath
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Organization details saved successfully",
                    "data", saved
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==============================
    // GET ORGANIZATION - AUTO FILL
    // ==============================

    @GetMapping("/get")
    public ResponseEntity<?> get(
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Invalid or missing token"
                ));
            }

            Registration data = registrationService.getByToken(cleanToken(token));

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==============================
    // SAVE TEAM - STEP 2
    // ==============================

    @PostMapping(value = "/team", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveTeam(
            @RequestParam(value = "members", required = false) String membersJson,
            @RequestParam(value = "teamMembers", required = false) String teamMembersJson,
            @RequestParam(value = "employeeDocument1", required = false) MultipartFile employeeDocument1,
            @RequestParam(value = "employeeDocument2", required = false) MultipartFile employeeDocument2,
            @RequestParam(value = "employeeDocument3", required = false) MultipartFile employeeDocument3,
            @RequestHeader("Authorization") String token
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Invalid or missing token"
                ));
            }

            String finalMembersJson = membersJson != null ? membersJson : teamMembersJson;

            List<TeamMember> saved = teamService.saveTeamMultipart(finalMembersJson, employeeDocument1, employeeDocument2, employeeDocument3,
                    cleanToken(token)
            );
            return ResponseEntity.ok(Map.of("success", true, "message", "Team members saved successfully", "data", saved));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==============================
    // GET TEAM - STEP 2 AUTO FILL
    // ==============================

    @GetMapping("/get/team")
    public ResponseEntity<?> getTeam(
            @RequestHeader("Authorization") String token
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid or missing token"));
            }

            List<TeamMember> team = teamService.getTeam(cleanToken(token));

            return ResponseEntity.ok(Map.of("success", true, "message", team.isEmpty() ? "No team members found" : "Team fetched successfully", "data", team));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==============================
    // GET DOCUMENTS
    // ==============================

    @GetMapping("/get/documents")
    public ResponseEntity<?> getDocuments(
            @RequestHeader("Authorization") String token
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid or missing token"));
            }

            Document doc = documentService.getDocuments(cleanToken(token));

            return ResponseEntity.ok(Map.of("success", true, "message", doc == null ? "No documents found" : "Documents fetched successfully", "data", doc));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ==============================
    // GET ALL - ADMIN
    // ==============================

    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(
            @RequestHeader("Authorization") String token
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid or missing token"));
            }
            return ResponseEntity.ok(Map.of("success", true, "data", registrationService.getAll()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==============================
    // APPROVE
    // ==============================
    @PutMapping("/approve/{userId}")
    public ResponseEntity<?> approve(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid or missing token"));
            }
            Registration updated = registrationService.approveByUserId(userId);return ResponseEntity.ok(Map.of("success", true, "message", "Approved successfully", "data", updated));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==============================
    // REJECT
    // ==============================
    @PutMapping("/reject/{userId}")
    public ResponseEntity<?> reject(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, String> req,
            @RequestHeader("Authorization") String token
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid or missing token"));
            }

            String reason = req != null ? req.get("rejectionReason") : null;

            Registration updated = registrationService.rejectByUserId(userId, reason);return ResponseEntity.ok(Map.of("success", true, "message", "Rejected successfully", "data", updated));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==============================
    // UPDATE STATUS
    // ==============================
    @PutMapping("/update-status")
    public ResponseEntity<?> updateStatus(
            @RequestBody Map<String, Object> req,
            @RequestHeader("Authorization") String token
    ) {
        try {
            if (!isValid(token)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid or missing token"));
            }

            Long userId = Long.valueOf(req.get("userId").toString());
            int status = Integer.parseInt(req.get("status").toString());

            Registration updated = registrationService.updateStatusByUserId(userId, status);

            return ResponseEntity.ok(Map.of("success", true, "message", "Status updated successfully", "data", updated));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
        // ==============================
        // GET TEAM BY USER ID - PUBLIC / ADMIN VIEW
        // ==============================
    @GetMapping("/get/team/public/{userId}")
    public ResponseEntity<?> getTeamByUserIdPublic(@PathVariable Long userId) {
        try {
            List<TeamMember> team = teamService.getByUserId(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", team.isEmpty() ? "No team members found" : "Team fetched successfully",
                    "data", team
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}