package ca.myasir.linksqueeze.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@EnableWebSecurity
@Configuration
class SecurityConfig  {

    @Bean
    fun securityConfiguration(http: HttpSecurity): SecurityFilterChain {
        val config = CorsConfiguration().apply {
            allowedOrigins = mutableListOf("http://localhost:4200")
            allowedMethods = mutableListOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = mutableListOf("Authorization", "Content-Type")
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)

        return http.authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.configurationSource(source) }
            .build()
    }
}
