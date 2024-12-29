package sg.edu.nus.iss.vttp5a_ssf_mini_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(form -> form
                    .loginPage("/login")
                    .permitAll())
            .authorizeHttpRequests(req -> req
                    .requestMatchers("/profiles/**", "/login/**", "/api/**", "/restDemo", "/error")
                    .permitAll()
                    .requestMatchers("/SVG/**", "/Images/**", "/main.css")
                    .permitAll())
            .authorizeHttpRequests(req -> req.anyRequest().authenticated())
            .logout(logout -> logout
                    .invalidateHttpSession(true));
            
        return http.build();
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean 
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }
}
