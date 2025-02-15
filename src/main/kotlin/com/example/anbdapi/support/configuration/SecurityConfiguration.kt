package com.example.anbdapi.support.configuration

import com.example.anbdapi.support.global.JwtAuthenticationFilter
import com.example.anbdapi.support.global.OAuth2AuthenticationSuccessHandler
import com.example.anbdapi.support.utils.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfiguration(
    private val objectMapper: ObjectMapper,
    private val customOAuth2UserServiceImpl: com.example.anbdapi.domain.auth.service.CustomOAuth2UserServiceImpl,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val jwtUtil: JwtUtil
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
                it.requestMatchers("/", "/swagger-ui/index.html", "/oauth2/**", "/api/v1/auth/refresh", "/api/v1/users/logout").permitAll()
                    .anyRequest().authenticated()
            }.oauth2Login {
                it.userInfoEndpoint { userInfo -> userInfo.userService(customOAuth2UserServiceImpl) }
                it.successHandler(oAuth2AuthenticationSuccessHandler)
            }.addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtUtil)
    }
}
