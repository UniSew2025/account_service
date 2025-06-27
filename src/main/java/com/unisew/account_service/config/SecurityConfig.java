package com.unisew.account_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import java.util.Arrays;
import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/api/v1/auth/**",
                                "/api/v2/account/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayHeaderAuthenticationFilter(), RequestHeaderAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public RequestHeaderAuthenticationFilter gatewayHeaderAuthenticationFilter() {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setPrincipalRequestHeader("X-ID");
        filter.setCredentialsRequestHeader("X-Role");
        filter.setAuthenticationManager(authentication -> {
            String headerId = (String) authentication.getPrincipal();
            String headerRole = (String) authentication.getCredentials();

            if (headerId == null || headerId.isEmpty()) {
                throw new IllegalArgumentException("X-ID header is missing or empty");
            }

            if (headerRole == null || headerRole.isEmpty()) {
                throw new IllegalArgumentException("X-Role header is missing or empty");
            }


            Collection<SimpleGrantedAuthority> authorities = Arrays.stream(headerRole.split(","))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()))
                    .toList();

            UserDetails userDetails = new User(headerId, "", authorities);
            return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        });
        filter.setExceptionIfHeaderMissing(false);
        return filter;
    }

}
