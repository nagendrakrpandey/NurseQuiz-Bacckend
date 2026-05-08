package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.Entity.TeamMember;
import Tech.Nagendra.NurseQuiz.Entity.user;
import Tech.Nagendra.NurseQuiz.Repository.TeamMemberRepo;
import Tech.Nagendra.NurseQuiz.Repository.UserRepository;
import Tech.Nagendra.NurseQuiz.Utitlty.JwtUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class TeamMemberService {

    @Autowired
    private TeamMemberRepo repo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public List<TeamMember> saveTeamMultipart(
            String membersJson,
            MultipartFile employeeDocument1,
            MultipartFile employeeDocument2,
            MultipartFile employeeDocument3,
            String token
    ) {
        try {
            if (membersJson == null || membersJson.isBlank()) {
                throw new RuntimeException("Team members data is missing");
            }

            ObjectMapper mapper = new ObjectMapper();

            List<TeamMember> members = mapper.readValue(
                    membersJson,
                    new TypeReference<List<TeamMember>>() {}
            );

            if (members.size() != 3) {
                throw new RuntimeException("Exactly 3 team members are required");
            }

            MultipartFile[] files = {
                    employeeDocument1,
                    employeeDocument2,
                    employeeDocument3
            };

            for (int i = 0; i < members.size(); i++) {
                MultipartFile file = files[i];

                if (file != null && !file.isEmpty()) {
                    String filePath = saveEmployeeDocument(file);
                    members.get(i).setEvidencePath(filePath);
                }
            }

            return saveTeam(members, token);

        } catch (Exception e) {
            throw new RuntimeException("Error saving team multipart: " + e.getMessage());
        }
    }

    @Transactional
    public List<TeamMember> saveTeam(List<TeamMember> members, String token) {

        try {
            String cleanToken = jwtUtil.cleanToken(token);

            Long userId = jwtUtil.extractUserId(cleanToken);

            if (userId == null) {
                throw new RuntimeException("Invalid token");
            }

            user loggedInUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            repo.deleteByUserId(userId);

            for (TeamMember member : members) {

                member.setId(null);
                member.setUser(loggedInUser);

                if (member.getName() != null) {
                    member.setName(member.getName().trim());
                }

                if (member.getEmail() != null) {
                    member.setEmail(member.getEmail().trim().toLowerCase());
                }

                if (member.getHospitalEmployeeId() != null) {
                    member.setHospitalEmployeeId(member.getHospitalEmployeeId().trim());
                }

                if (member.getRole() != null) {
                    member.setRole(member.getRole().trim());
                }

                if (member.getCustomRole() != null) {
                    member.setCustomRole(member.getCustomRole().trim());
                }

                if (member.getRoleType() != null) {
                    member.setRoleType(member.getRoleType().trim());
                }
            }

            return repo.saveAll(members);

        } catch (Exception e) {
            throw new RuntimeException("Error saving team: " + e.getMessage());
        }
    }

    private String saveEmployeeDocument(MultipartFile file) throws Exception {
        String uploadDir = "uploads/team-documents/";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "document.pdf";

        String fileName = System.currentTimeMillis() + "_" + originalFileName;
        Path path = Paths.get(uploadDir + fileName);

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return uploadDir + fileName;
    }

    public List<TeamMember> getTeam(String token) {
        String cleanToken = jwtUtil.cleanToken(token);
        Long userId = jwtUtil.extractUserId(cleanToken);

        return repo.findByUserId(userId);
    }

    public List<TeamMember> getByUserId(Long userId) {
        return repo.findByUserId(userId);
    }
    public List<TeamMember> getAllTeams() {
        return repo.findAll();
    }
}