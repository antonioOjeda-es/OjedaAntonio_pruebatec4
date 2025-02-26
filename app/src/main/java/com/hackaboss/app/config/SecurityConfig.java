package com.hackaboss.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //metodo para que funcione como un beans de spring
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //para que pueda usarse con postman y el navegador
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        //voy a permitir que no se autentique las solicitudes de tipo GET
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                        //voy a permitir que el cliente pueda reservar habitación, es cliente de la empresa
                        .requestMatchers(HttpMethod.POST, "/room-booking/new",
                                "/flight-booking/new").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form.permitAll())
                .httpBasic(httpBasic -> httpBasic.realmName("app"))
                .build();
    }


}
