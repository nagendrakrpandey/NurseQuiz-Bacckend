package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.DTO.LoginResponse;
import Tech.Nagendra.NurseQuiz.DTO.ResetPasswordRequest;
import Tech.Nagendra.NurseQuiz.DTO.SignupRequest;
import Tech.Nagendra.NurseQuiz.Entity.Batch;
import Tech.Nagendra.NurseQuiz.Entity.Candidates;
import Tech.Nagendra.NurseQuiz.Entity.user;
import Tech.Nagendra.NurseQuiz.Repository.BatchRepository;
import Tech.Nagendra.NurseQuiz.Repository.CandidatesRepository;
import Tech.Nagendra.NurseQuiz.Repository.UserRepository;
import Tech.Nagendra.NurseQuiz.Utitlty.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CandidatesRepository candidateRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private JwtUtil jwtUtil;

    public String sendOtpOnly(String email) {
        if (email == null || email.isEmpty()) {
            return "Email is required";
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        user existingUser = userRepository.findByEmail(email).orElse(null);

        if (existingUser != null) {
            if (existingUser.isVerified()) {
                return "User already registered. Please login.";
            }

            existingUser.setOtp(otp);
            existingUser.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            userRepository.save(existingUser);

            emailService.sendOtp(email, otp);
            return "OTP Sent";
        }

        user newUser = new user();
        newUser.setEmail(email);
        newUser.setOtp(otp);
        newUser.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        newUser.setVerified(false);

        userRepository.save(newUser);
        emailService.sendOtp(email, otp);

        return "OTP Sent";
    }

    public String verifyOtp(String email, String userOtp) {
        user user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "User not found";
        }

        if (user.isVerified()) {
            return "User already verified";
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return "OTP Expired";
        }

        if (user.getOtp() == null || !user.getOtp().equals(userOtp)) {
            return "Invalid OTP";
        }

        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return "OTP_VERIFIED";
    }

    public String registerUser(SignupRequest request) {
        user user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "Please verify email first";
        }

        if (!user.isVerified()) {
            return "Email not verified";
        }

        if (request.getFullName() == null || request.getFullName().trim().length() < 2) {
            return "Full name must be at least 2 characters.";
        }

        if (!request.getContact().matches("\\d{10}")) {
            return "Contact must be exactly 10 digits.";
        }

        if (request.getPassword().length() < 6) {
            return "Password must be at least 6 characters.";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Passwords do not match.";
        }

        user.setFullName(request.getFullName());
        user.setContact(request.getContact());
        user.setPassword(request.getPassword());
        user.setRoleId(3L);

        userRepository.save(user);

        return "SUCCESS";
    }

    public LoginResponse loginUser(String email, String password) {
        user user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new LoginResponse("INVALID CREDENTIALS", null, null, null, null, null, 0, null, null, null, null);
        }

        if (!user.isVerified()) {
            return new LoginResponse("EMAIL NOT VERIFIED", null, null, null, null, null, 0, null, null, null, null);
        }

        if (!user.getPassword().equals(password)) {
            return new LoginResponse("INVALID CREDENTIALS", null, null, null, null, null, 0, null, null, null, null);
        }

        String token = jwtUtil.generateToken(user);

        Candidates candidate = candidateRepository.findByEmail(user.getEmail()).orElse(null);

        Long batchId = null;
        String batchCode = null;
        String level = null;

        if (candidate != null && candidate.getBatchId() != null) {
            batchId = candidate.getBatchId();
            Optional<Batch> batchOptional = batchRepository.findById(batchId);

            if (batchOptional.isPresent()) {
                Batch batch = batchOptional.get();
                batchCode = batch.getBatchCode();
                level = batch.getLevel();
            }
        }

        return new LoginResponse(
                "LOGIN_SUCCESS",
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getContact(),
                user.getLoginStatus(),
                user.getRoleId(),
                batchId,
                batchCode,
                level
        );
    }

    public void forgotPassword(String email, HttpServletRequest request ) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required.");
        }

        user existingUser = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("No account found with this email."));

        String token = UUID.randomUUID().toString();

        existingUser.setResetPasswordToken(token);
        existingUser.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(existingUser);

       // String resetLink = "http://localhost:8080/reset-password/" + token;
        String resetLink = "https://nursequia-ui.vercel.app/reset-password/" + token;
        String subject = "NurseQuiz Password Reset";
        String body = "Click this link to reset your password:\n\n" + resetLink + "\n\nThis link will expire in 15 minutes.";
        emailService.sendSimpleEmail(existingUser.getEmail(), subject, body);
    }
    public void resetPassword(String token, ResetPasswordRequest request) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Invalid reset token.");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match.");
        }

        user existingUser = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token."));

        if (existingUser.getResetPasswordTokenExpiry() == null ||
                existingUser.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset link expired.");
        }

        existingUser.setPassword(request.getPassword());
        existingUser.setResetPasswordToken(null);
        existingUser.setResetPasswordTokenExpiry(null);

        userRepository.save(existingUser);
        String subject = "Password Reset Successful";

        String body = "Hello " + existingUser.getFullName() + ",\n\n" + "Your NurseQuiz account password has been reset successfully.\n\n" + "If you did not perform this action, please contact support immediately.\n\n" + "Regards,\n" + "NurseQuiz Team";

        emailService.sendSimpleEmail(existingUser.getEmail(), subject, body);

    }
}