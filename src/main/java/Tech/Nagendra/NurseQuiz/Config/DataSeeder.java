package Tech.Nagendra.NurseQuiz.Config;

import Tech.Nagendra.NurseQuiz.Entity.user;
import Tech.Nagendra.NurseQuiz.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        createUserIfNotExists("Super Admin", "urbanites22@yopmail.com", 1, 1L);
        createUserIfNotExists("Admin", "pandeyankur804@gmail.com", 1, 2L);
    }

    private void createUserIfNotExists(
            String fullName,
            String email,
            int loginStatus,
            Long roleId
    ) {
        if (!userRepository.existsByEmail(email)) {
            user newUser = new user();

            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setLoginStatus(loginStatus);
            newUser.setRoleId(roleId);

            userRepository.save(newUser);
        }
    }
}