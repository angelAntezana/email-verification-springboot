package com.angel.antezana.emailverificationdemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class UserRegistrationSecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder (){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception{
         
        return http
                .csrf(csrf->
                    csrf.disable())
                .authorizeHttpRequests(authRequest->
                        authRequest
                        .requestMatchers("/api/v1/register").permitAll()
                        .requestMatchers("/api/v1/users").hasAnyAuthority("USER","ADMIN"))
                .formLogin(Customizer.withDefaults())
                .build();
    }
}
