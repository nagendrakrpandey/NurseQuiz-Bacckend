package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);

    @Query(value = """
    SELECT COALESCE(MAX(CAST(REPLACE(enrollment_number, 'ENR_', '') AS INTEGER)),0)
    FROM users WHERE enrollment_number IS NOT NULL""", nativeQuery = true)
    Integer findMaxEnrollmentNumber();
    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    List<Payment> findAllByOrderByCreatedAtDesc();
    @Query("SELECT p, r.organizationName FROM Payment p JOIN Registration r ON p.organizationId = r.hospitalRegisteredId")
    List<Object[]> getPaymentsWithOrgName();

}