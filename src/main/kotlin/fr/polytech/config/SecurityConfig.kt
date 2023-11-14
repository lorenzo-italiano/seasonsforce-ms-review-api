package fr.polytech.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    private val jwtAuthConverter = JwtAuthConverter()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
            .oauth2ResourceServer { oauth2 -> oauth2.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter) } }
            .sessionManagement { session -> session.sessionCreationPolicy(STATELESS) }

        return http.build()
    }
}
