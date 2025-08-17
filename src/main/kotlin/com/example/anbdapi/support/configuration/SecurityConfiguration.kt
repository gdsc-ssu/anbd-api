package com.example.anbdapi.support.configuration

import com.example.anbdapi.support.utils.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfiguration(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin {
                it.disable()
            }.httpBasic {
                it.disable()
            }.csrf {
                it.disable()
            }.headers {
                it.frameOptions { frameOptionsConfig ->
                    frameOptionsConfig.disable()
                }
            }.sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.authorizeHttpRequests {
                it.requestMatchers(
                    "/",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/dev/**",
                    "/v1/users/logout",
                    "/v1/auth/tokens/refresh",
                    "/v1/auth/mobile/google",
                    "/v1/auth/mobile/apple",
                    "/stomp/chat").permitAll()
                    .anyRequest().authenticated()
            }.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}
