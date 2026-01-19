//package com.hostel.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    
//    private final JwtAuthenticationFilter jwtAuthFilter;
//    
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
//        this.jwtAuthFilter = jwtAuthFilter;
//    }
//    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//               
//                .requestMatchers(
//                    "/api/v1/users/register",
//                    "/api/v1/users/login",
//                    "/api/v1/users/forgot-password",
//                    "/api/v1/users/reset-password",
//                    "/api/v1/hostels",
//                    "/api/v1/hostels/**",
//                    "/api/v1/rooms/**",
//                    "/api/v1/facilities"
//                ).permitAll()
//                
//             
//                .requestMatchers(
//                    "/api/v1/admin/**"
//                ).hasRole("ADMIN")
//                
//              
//                .requestMatchers(
//                    "/api/v1/hostels/add",
//                    "/api/v1/rooms/add"
//                ).hasAnyRole("OWNER", "ADMIN")
//                
//               
//                .requestMatchers(
//                    "/api/v1/bookings/**"
//                ).hasAnyRole("USER", "OWNER", "ADMIN")
//                
//                
////                .anyRequest().authenticated()
//            )
//            .sessionManagement(session -> 
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            )
//            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//        
//        return http.build();
//    }
//    
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
//



package com.hostel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
             
                .requestMatchers(
                    "/api/v1/users/register",
                    "/api/v1/users/login",
                    "/api/v1/users/forgot-password",
                    "/api/v1/users/reset-password",
                    "/api/v1/hostels",
                    "/api/v1/hostels/**",
                    "/api/v1/rooms/**",
                    "/api/v1/facilities"
                ).permitAll()

               
                .requestMatchers("/api/v1/users/admin/**").hasRole("ADMIN")

                
                .requestMatchers(
                    "/api/v1/hostels/add",
                    "/api/v1/rooms/add"
                ).hasAnyRole("OWNER", "ADMIN")

                
                .requestMatchers("/api/v1/users/bookings/**").hasAnyRole("USER", "OWNER", "ADMIN")

               
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

