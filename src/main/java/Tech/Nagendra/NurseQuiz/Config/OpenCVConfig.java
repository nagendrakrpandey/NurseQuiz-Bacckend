package Tech.Nagendra.NurseQuiz.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCVConfig {


    @PostConstruct
    public void init() {
        nu.pattern.OpenCV.loadLocally();
        System.out.println(" OpenCV Loaded (Maven)");
    }
}