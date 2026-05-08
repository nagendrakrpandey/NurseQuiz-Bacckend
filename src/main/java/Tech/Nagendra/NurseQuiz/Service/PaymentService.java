package Tech.Nagendra.NurseQuiz.Service;

import Tech.Nagendra.NurseQuiz.Entity.Payment;
import Tech.Nagendra.NurseQuiz.Entity.Registration;
import Tech.Nagendra.NurseQuiz.Entity.user;
import Tech.Nagendra.NurseQuiz.Repository.PaymentRepo;
import Tech.Nagendra.NurseQuiz.Repository.RegistrationRepository;
import Tech.Nagendra.NurseQuiz.Repository.UserRepository;
import Tech.Nagendra.NurseQuiz.Utitlty.JwtUtil;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepo repo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${razorpay.key.id}")
    private String key;

    @Value("${razorpay.key.secret}")
    private String secret;

    // ==============================
    // CREATE ORDER
    // ==============================
    public Order createOrder() throws Exception {

        RazorpayClient client = new RazorpayClient(key, secret);

        JSONObject options = new JSONObject();
        options.put("amount", 295000);
        options.put("currency", "INR");
        options.put("receipt", "txn_" + System.currentTimeMillis());

        return client.orders.create(options);
    }

    // ==============================
    // VERIFY + SAVE + ACTIVATE USER + AUTO ENROLL
    // ==============================
    public String verifyAndSavePayment(String orderId, String paymentId, String signature,
           Double amount,
            String organizationId,
            String token
    ) throws Exception {

        Long userId = jwtUtil.extractUserId(token);

        String data = orderId + "|" + paymentId;
        boolean isValid = Utils.verifySignature(data, signature, secret);

        if (!isValid) {
            throw new RuntimeException("Payment verification failed");
        }

        user u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Payment> existing = repo.findByPaymentId(paymentId);

        if (existing.isPresent()) {

            if (u.getEnrollmentNumber() == null || u.getEnrollmentNumber().isBlank()) {
                String enrollmentNumber = generateEnrollmentNumber();
                u.setEnrollmentNumber(enrollmentNumber);
            }

            u.setLoginStatus(1);

            user savedUser = userRepository.save(u);

            candidateService.autoEnrollSingleUser(savedUser);

            return "Payment already recorded. Enrollment Number: " + savedUser.getEnrollmentNumber();
        }

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setOrderId(orderId);
        payment.setPaymentId(paymentId);
        payment.setSignature(signature);
        payment.setAmount(amount);
        payment.setStatus("SUCCESS");
        payment.setOrganizationId(organizationId);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        repo.save(payment);

        u.setLoginStatus(1);

        if (u.getEnrollmentNumber() == null || u.getEnrollmentNumber().isBlank()) {
            String enrollmentNumber = generateEnrollmentNumber();
            u.setEnrollmentNumber(enrollmentNumber);
        }

        user savedUser = userRepository.save(u);

        candidateService.autoEnrollSingleUser(savedUser);

        return "Payment verified, saved & user activated. Enrollment Number: " + savedUser.getEnrollmentNumber();
    }

    // ==============================
    // GET ALL PAYMENTS FOR ADMIN UI
    // ==============================
    public List<Map<String, Object>> getAllPayments() {

        List<Payment> payments = repo.findAll();

        return payments.stream().map(p -> {

            Map<String, Object> map = new HashMap<>();

            map.put("id", p.getId());
            map.put("userId", p.getUserId());
            map.put("orderId", p.getOrderId());
            map.put("paymentId", p.getPaymentId());
            map.put("amount", p.getAmount());
            map.put("status", p.getStatus());
            map.put("organizationId", p.getOrganizationId());
            map.put("createdAt", p.getCreatedAt());
            map.put("updatedAt", p.getUpdatedAt());

            user u = userRepository.findById(p.getUserId()).orElse(null);

            if (u != null) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("fullName", u.getFullName());
                userMap.put("email", u.getEmail());
                map.put("user", userMap);
            }

            Registration reg = registrationRepository
                    .findByUser_Id(p.getUserId())
                    .orElse(null);

            if (reg != null) {
                Map<String, Object> orgMap = new HashMap<>();
                orgMap.put("organizationName", reg.getOrganizationName());
                orgMap.put("registrationNumber", reg.getHospitalRegisteredId());
                map.put("organization", orgMap);
            }

            return map;

        }).toList();
    }

    public List<Map<String, Object>> getPaymentWithOrg() {

        List<Object[]> list = repo.getPaymentsWithOrgName();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] obj : list) {
            Payment p = (Payment) obj[0];
            String orgName = (String) obj[1];

            Map<String, Object> map = new HashMap<>();
            map.put("paymentId", p.getId());
            map.put("organizationId", p.getOrganizationId());
            map.put("organizationName", orgName);

            result.add(map);
        }

        return result;
    }

    private String generateEnrollmentNumber() {

        Integer maxNumber = repo.findMaxEnrollmentNumber();

        if (maxNumber == null) {
            maxNumber = 0;
        }

        int nextNumber = maxNumber + 1;

        return String.format("ENR_%03d", nextNumber);
    }
}