package Tech.Nagendra.NurseQuiz.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/api/auth/**",
                                "/api/register/**",
                                "/api/payment/**",
                                "/api/questions/**",
                                "/api/batches/**",
                                "/api/candidates/**",
                                "/api/proctor/**",
                                "/api/responses/**",
                                "/api/questionBank/**",
                                "/api/user/**",
                                "/uploads/**"
                        ).permitAll()

                        .anyRequest().permitAll()
                );

        return http.build();
    }

}