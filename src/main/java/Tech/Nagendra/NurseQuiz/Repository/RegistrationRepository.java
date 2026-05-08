package Tech.Nagendra.NurseQuiz.Repository;


import Tech.Nagendra.NurseQuiz.Entity.Registration;
import Tech.Nagendra.NurseQuiz.Entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Registration findByOrgEmail(String orgEmail);
    Registration findByUser(user user);
  Registration findByUserId(Long userId);

    Optional<Registration> findByUser_Id(Long userId);
}
