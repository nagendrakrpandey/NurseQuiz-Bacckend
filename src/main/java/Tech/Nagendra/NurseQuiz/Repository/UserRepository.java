package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.user;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<user, Long> {

    Optional<user> findByEmail(String email);
    List<user> findByLoginStatus(Integer loginStatus);
    Optional<user> findByResetPasswordToken(String resetPasswordToken);


    Optional<user> findByContact(String contact);
    Optional<user> findTopByEnrollmentNumberIsNotNullOrderByIdDesc();


    @Modifying
    @Transactional
    @Query("UPDATE user u SET u.loginStatus = 1 WHERE u.Id = :userId")
    void activateUser(@Param("userId") Long userId);
}