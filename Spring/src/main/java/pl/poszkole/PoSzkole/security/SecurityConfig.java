package pl.poszkole.PoSzkole.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/registration/manager")
                        .hasRole("OWNER")

                        .requestMatchers("/registration/teacher", "/request/create", "/course/create",
                                "/course/edit/", "/course/delete/", "/course/add-student", "/course/open/**",
                                "/course/finish/**", "/user/all-students")
                        .hasAnyRole("MANAGER", "OWNER")

                        .requestMatchers("/class/add-student", "/class/create", "/class/student-list/**",
                                "/user/edit/subjects/**")
                        .hasAnyRole("MANAGER", "TEACHER")

                        .requestMatchers("/request/list/**", "/request/admit/**", "/attendance/list/**",
                                "/attendance/check/**", "/schedule/edit/**", "/attendance/create/**",
                                "/attendance/exists/**", "/schedule/my-classes/teacher",
                                "/room-reservation/free-rooms", "/room-reservation/reserve/**")
                        .hasRole("TEACHER")

                        .requestMatchers("/busy-days/create", "/busy-days/edit/**", "/busy-days/delete/**")
                        .hasAnyRole("TEACHER", "STUDENT")

                        .requestMatchers("/class/my-classes", "/course/bought-courses", "/schedule/my-classes/student",
                                "/schedule/cancel/**", "/attendance/presence", "/attendance/absence")
                        .hasRole("STUDENT")

                        .requestMatchers("/course/available-courses", "/busy-days/list/**", "/subject/all", "/user/my-profile",
                                "/user/profile/**", "/user/edit/my-profile")
                        .hasAnyRole("OWNER", "MANAGER", "TEACHER", "STUDENT")

                        .requestMatchers("/login", "/register", "/details")
                        .permitAll()

                        .anyRequest()
                        .authenticated())
                .userDetailsService(userDetailsService)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
