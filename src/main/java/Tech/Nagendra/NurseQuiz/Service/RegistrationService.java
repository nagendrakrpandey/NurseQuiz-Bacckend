package Tech.Nagendra.NurseQuiz.Service;
import Tech.Nagendra.NurseQuiz.Entity.Registration;
import Tech.Nagendra.NurseQuiz.Entity.user;
import Tech.Nagendra.NurseQuiz.Repository.RegistrationRepository;
import Tech.Nagendra.NurseQuiz.Repository.UserRepository;
import Tech.Nagendra.NurseQuiz.Utitlty.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository repository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate;

    public RegistrationService(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    // ==============================
    // STATUS VALIDATION
    // ==============================

    private void validateStatus(int status) {
        if (status < 1 || status > 3) {
            throw new RuntimeException("Invalid status value");
        }
    }

    // ==============================
    //  STATUS TEXT (Frontend friendly)
    // ==============================
    public String getStatusText(int status) {
        switch (status) {
            case 2: return "approved";
            case 3: return "rejected";
            default: return "pending";
        }
    }

    // ==============================
    // 🔥 PINCODE AUTO FILL
    // ==============================
    private void fillStateDistrictFromPincode(Registration req) {
        try {
            if (req.getPincode() == null || req.getPincode().length() != 6) return;

            String url = "https://api.postalpincode.in/pincode/" + req.getPincode();

            Map[] response = restTemplate.getForObject(url, Map[].class);

            if (response != null && response.length > 0) {

                Map data = response[0];

                if (!"Success".equals(data.get("Status"))) return;

                List<Map<String, Object>> postOfficeList =
                        (List<Map<String, Object>>) data.get("PostOffice");

                if (postOfficeList != null && !postOfficeList.isEmpty()) {

                    Map<String, Object> po = postOfficeList.get(0);

                    req.setState((String) po.get("State"));
                    req.setDistrict((String) po.get("District"));
                }
            }

        } catch (Exception e) {
            System.out.println(" Pincode API failed: " + e.getMessage());
        }
    }

    // ==============================
    // 🔥 SAVE / UPDATE
    // ==============================
    public Registration save(Registration req, String token, MultipartFile certificate, String existingCertificatePath
    ) {
        try {
            token = jwtUtil.cleanToken(token);
            Long userId = jwtUtil.extractUserId(token);

            user user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            fillStateDistrictFromPincode(req);

            Registration existing = repository.findByUser(user);

            String certificatePath = existingCertificatePath;

            if (certificate != null && !certificate.isEmpty()) {
                certificatePath = saveFile(certificate);
            }

            if (existing == null) {
                req.setUser(user);
                req.setStatus(1);
                req.setHospitalRegistrationCertificatePath(certificatePath);
                return repository.save(req);
            }

            existing.setOrganizationName(req.getOrganizationName());
            existing.setHospitalRegisteredId(req.getHospitalRegisteredId());
            existing.setSpocName(req.getSpocName());
            existing.setHospitalCategory(req.getHospitalCategory());
            existing.setAddress(req.getAddress());
            existing.setPincode(req.getPincode());
            existing.setState(req.getState());
            existing.setDistrict(req.getDistrict());
            existing.setOrgEmail(req.getOrgEmail());
            existing.setOrgPhone(req.getOrgPhone());
            existing.setHospitalRegistrationCertificatePath(certificatePath);
            existing.setStatus(1);

            return repository.save(existing);

        } catch (Exception e) {
            throw new RuntimeException("Error saving registration: " + e.getMessage());
        }
    }
    // ==============================
    // 🔥 GET BY TOKEN
    // ==============================
    public Registration getByToken(String token) {

        Long userId = jwtUtil.extractUserId(token);
        return repository.findByUserId(userId);
    }

    // ==============================
    // 🔥 GET ALL (RAW)
    // ==============================
    public List<Registration> getAll() {
        return repository.findAll();
    }

    // ==============================
    // 🔥 GET ALL (FORMATTED FOR UI)
    // ==============================
    public List<Map<String, Object>> getAllFormatted() {

        return repository.findAll().stream().map(reg -> {

            Map<String, Object> map = new HashMap<>();

            map.put("id", reg.getId());
            map.put("userId", reg.getUser() != null ? reg.getUser().getId() : null);
            map.put("organizationName", reg.getOrganizationName());
            map.put("hospitalregisteredid", reg.getHospitalRegisteredId());
            map.put("spocname",reg.getSpocName());
            map.put("hospitalcategoery",reg.getHospitalCategory());
            map.put("hospitalregistrationcertificate",reg.getHospitalRegistrationCertificatePath());
            map.put("orgEmail", reg.getOrgEmail());
            map.put("state", reg.getState());
            map.put("district", reg.getDistrict());
            map.put("status", getStatusText(reg.getStatus()));

            return map;

        }).collect(Collectors.toList());
    }
    // ==============================
    // 🔥 UPDATE STATUS
    // ==============================
    public Registration updateStatusByUserId(Long userId, int status) {

        Registration reg = repository.findByUser_Id(userId)
                .orElseThrow(() ->
                        new RuntimeException("Registration not found for userId: " + userId)
                );

        reg.setStatus(status);

        return repository.save(reg);
    }
    // ==============================
    // 🔥 APPROVE
    // ==============================
    public Registration approveByUserId(Long userId) {
        return updateStatusByUserId(userId, 2);
    }

    // ==============================
    // 🔥 REJECT (WITH REASON)
    // ==============================
    public Registration rejectByUserId(Long userId, String reason) {

        Registration reg = repository.findByUserId(userId);

        if (reg == null) {
            throw new RuntimeException("Registration not found");
        }

        reg.setStatus(3);
        try {
            reg.setRejectionReason(reason);
        } catch (Exception ignored) {}

        return repository.save(reg);
    }

    // ==============================
    //  STATS
    // ==============================
    public Map<String, Long> getStats() {
        List<Registration> list = repository.findAll();
        long pending = list.stream().filter(r -> r.getStatus() == 1).count();
        long approved = list.stream().filter(r -> r.getStatus() == 2).count();
        long rejected = list.stream().filter(r -> r.getStatus() == 3).count();
        return Map.of("total", (long) list.size(), "pending", pending, "approved", approved, "rejected", rejected);
    }

    private String saveFile(MultipartFile file) throws Exception {
        String uploadDir = "uploads/registration/";

        java.io.File dir = new java.io.File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);

        java.nio.file.Files.copy(
                file.getInputStream(),
                path,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );

        return uploadDir + fileName;
    }
}