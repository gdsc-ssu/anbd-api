package com.example.anbdapi.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration(
    private val objectMapper: ObjectMapper
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
                it.anyRequest().permitAll() // 기호에 맞게 수정
            }
//            .addFilterBefore(
//                jwtAuthenticationFilter(),
//                UsernamePasswordAuthenticationFilter::class.java
//            )

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

//    @Bean
//    fun jwtAuthenticationFilter(): JwtAuthenticationFilter = JwtAuthenticationFilter(objectMapper, tokenAuthService)
}
