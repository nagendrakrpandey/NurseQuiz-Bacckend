package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.DTO.*;
import Tech.Nagendra.NurseQuiz.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // ==============================
    // 🔷 STEP 1: SEND OTP (ONLY EMAIL)
    // ==============================
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email) {
        return userService.sendOtpOnly(email);
    }

    // ==============================
    // 🔷 STEP 2: VERIFY OTP
    // ==============================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp) {
        return userService.verifyOtp(email, otp);
    }

    // ==============================
    // 🔷 STEP 3: FINAL SIGNUP
    // ==============================
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.loginUser(request.getEmail(), request.getPassword());
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        userService.forgotPassword(request.getEmail(), httpRequest);

        return ResponseEntity.ok(Map.of(
                "message", "Password reset instructions have been sent to your registered email."
        ));
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(
            @PathVariable String token,
            @RequestBody ResetPasswordRequest request
    ) {
        userService.resetPassword(token, request);

        return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully."
        ));
    }

}