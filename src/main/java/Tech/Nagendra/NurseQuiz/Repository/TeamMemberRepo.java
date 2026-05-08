package Tech.Nagendra.NurseQuiz.Repository;

import Tech.Nagendra.NurseQuiz.Entity.TeamMember;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface TeamMemberRepo extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByUserId(Long userId);
    @Transactional
    @Modifying
    void deleteByUserId(Long userId);
}
