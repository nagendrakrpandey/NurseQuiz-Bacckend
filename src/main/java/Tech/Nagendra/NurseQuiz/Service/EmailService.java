package Tech.Nagendra.NurseQuiz.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        System.out.println("===== EMAIL SERVICE START =====");
        System.out.println("To: " + toEmail);
        System.out.println("OTP: " + otp);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 🔥 HTML EMAIL BODY
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding:20px;'>"
                    + "<h3>Dear Applicant,</h3>"
                    + "<p>Greetings!</p>"
                    + "<p>Thank you for registering with our platform.</p>"
                    + "<p>To complete your verification process, please use the OTP below:</p>"

                    // 🔥 OTP HIGHLIGHT
                    + "<div style='font-size:24px; font-weight:bold; color:#2c3e50; "
                    + "background:#f1f1f1; padding:10px; display:inline-block; border-radius:5px;'>"
                    + "🔐 " + otp +
                    "</div>"

                    + "<p style='margin-top:15px;'>This OTP is valid for 5 minutes.</p>"
                    + "<p><b>Do not share this code with anyone.</b></p>"
                    + "<p>If you did not request this, please ignore this email.</p>"

                    + "<br><p>Best regards,<br><b>Team Nurse Quiz</b></p>"
                    + "</div>";

            helper.setTo(toEmail);
            helper.setSubject("OTP Verification - Nurse Quiz");
            helper.setText(htmlContent, true); // 🔥 TRUE = HTML ENABLE

            mailSender.send(message);

            System.out.println("✅ Mail sent successfully");

        } catch (Exception e) {
            System.out.println("❌ Mail sending failed");
            e.printStackTrace();
        }
    }
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}