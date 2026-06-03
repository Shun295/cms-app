package com.cms.config;

import com.cms.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtFilter jwtFilter;

    /*@Bean
    public UserDetailsService users() {
        UserDetails user1 = User.builder()
                .username("head_rajesh")
                .password("{noop}123")
                .roles("STATION_HEAD")
                .build();
        UserDetails user2 = User.builder()
                .username("officer_arun")
                .password("{noop}123")
                .roles("OFFICER")
                .build();
        return new InMemoryUserDetailsManager(user1, user2);
    }*/


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/api/auth/signIn").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/login").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/incident/all/v2").hasAuthority("OFFICER")
                        .requestMatchers(HttpMethod.GET, "/api/incident/get-one/{id}").hasAnyAuthority("OFFICER", "STATION_HEAD")
                        .requestMatchers(HttpMethod.GET, "/api/incident/officer/{officerId}").hasAuthority("STATION_HEAD")
                        .requestMatchers(HttpMethod.POST, "/api/incident/add/v2/{officerId}").hasAuthority("STATION_HEAD")
                        .requestMatchers(HttpMethod.GET, "/api/incident/suspect/by-incident/{incidentId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/station/by-incident").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/officer/by-incident/stat").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/incident/by-type").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/officer/id/upload").hasAuthority("OFFICER")
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider dao=new DaoAuthenticationProvider(userService);
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
