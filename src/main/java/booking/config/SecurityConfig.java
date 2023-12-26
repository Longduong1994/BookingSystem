package booking.config;

import booking.security.jwt.JwtEntryPoint;
import booking.security.jwt.JwtTokenFilter;
import booking.security.user_principle.UserDetailService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailService userDetailService;
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtEntryPoint jwtEntryPoint;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(auth -> auth.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("*"));
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests((auth) ->
                        auth.requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/users/**").hasAuthority("ADMIN")
                                .requestMatchers("/categories/**").hasAuthority("ADMIN")
                                .requestMatchers("/dishes/**").hasAuthority("ADMIN")
                                .requestMatchers("/tables/**").hasAuthority("ADMIN")
                                .requestMatchers("/reservation/**").hasAuthority("ADMIN")
                                .requestMatchers("/menus/**").hasAuthority("ADMIN")
                                .anyRequest().authenticated())
                .exceptionHandling((auth) ->
                        auth.authenticationEntryPoint(jwtEntryPoint))
                .sessionManagement((auth) ->
                        auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build()
                ;
    }
}
