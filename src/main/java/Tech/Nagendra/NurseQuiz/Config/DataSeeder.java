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
        createUserIfNotExists("Super Admin", "urbanites22@yopmail.com",   "123456", 1, 1L);
        createUserIfNotExists("Admin", "pandeyankur804@gmail.com",   "123456", 1, 2L);
    }

    private void createUserIfNotExists(
            String fullName,
            String email,
            String password,
            int loginStatus,
            Long roleId
    ) {
        if (!userRepository.existsByEmail(email)) {
            user newUser = new user();

            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setLoginStatus(loginStatus);
            newUser.setRoleId(roleId);

            userRepository.save(newUser);
        }
    }
}