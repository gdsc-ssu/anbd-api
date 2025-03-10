package com.example.anbdapi.support.configuration

import com.example.anbdapi.domain.auth.service.CustomOAuth2UserService
import com.example.anbdapi.support.utils.jwt.JwtAuthenticationFilter
import com.example.anbdapi.support.global.OAuth2AuthenticationSuccessHandler
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.utils.jwt.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfiguration(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val traceIdResolver: TraceIdResolver
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
                it.requestMatchers("/", "/swagger-ui/**", "/oauth2/**", "/v3/api-docs/**", "/v1/auth/refresh", "/v1/users/logout", "/dev/**").permitAll()
                    .anyRequest().authenticated()
            }.oauth2Login {
                it.userInfoEndpoint { userInfo -> userInfo.userService(customOAuth2UserService) }
                it.successHandler(oAuth2AuthenticationSuccessHandler)
            }.addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtUtil, objectMapper, traceIdResolver)
    }
}
