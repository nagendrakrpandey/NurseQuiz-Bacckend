package Tech.Nagendra.NurseQuiz.Controller;

import Tech.Nagendra.NurseQuiz.Service.PaymentService;
import Tech.Nagendra.NurseQuiz.Utitlty.JwtUtil;

import com.razorpay.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @Autowired
    private JwtUtil jwtUtil;

    // ==============================
    // 🔥 TOKEN VALIDATION (COMMON METHOD)
    // ==============================
    private boolean isValid(String token) {
        return token != null && token.startsWith("Bearer ") && jwtUtil.validateToken(token);
    }

    // ==============================
    // 🔥 CREATE ORDER
    // ==============================
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestHeader("Authorization") String token
    ) {
        try {

            if (!isValid(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            Order order = service.createOrder();

            // ✅ FIX HERE
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", new org.json.JSONObject(order.toString()).toMap()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", "Error creating order: " + e.getMessage()
                    ));
        }
    }

    // ==============================
    // 🔥 VERIFY PAYMENT
    // ==============================
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, Object> req,
            @RequestHeader("Authorization") String token
    ) {
        try {

            if (!isValid(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            String orderId = (String) req.get("orderId");
            String paymentId = (String) req.get("paymentId");
            String signature = (String) req.get("signature");
            Double amount = Double.valueOf(req.get("amount").toString());
            String organizationId = (String) req.get("organizationId");

            String result = service.verifyAndSavePayment(
                    orderId,
                    paymentId,
                    signature,
                    amount,
                    organizationId,
                    token.replace("Bearer ", "")
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", result
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "success", false, "message", "Payment verification failed: " + e.getMessage())
            );
        }
    }


    @GetMapping("/payments-with-org")
    public ResponseEntity<?> getPayments(
            @RequestHeader("Authorization") String token
    ) {
        try {

            if (!isValid(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            List<Map<String, Object>> data = service.getPaymentWithOrg();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", data
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    // ==============================
    // 🔥 GET ALL PAYMENTS (ADMIN)
    // ==============================


    @GetMapping("/get-all")
    public ResponseEntity<?> getAllPayments(
            @RequestHeader("Authorization") String token
    ) {
        try {

            if (!isValid(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            List<Map<String, Object>> data = service.getAllPayments();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", data
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }
}