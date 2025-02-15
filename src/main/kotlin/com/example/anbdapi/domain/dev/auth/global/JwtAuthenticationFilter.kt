package com.example.anbdapi.domain.dev.auth.global

import com.example.anbdapi.domain.dev.auth.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization") ?: ""
        if (header.startsWith("Bearer ")) {
            val token = header.removePrefix("Bearer ").trim()
            jwtUtil.validateToken(token)
            val email = jwtUtil.getEmailFromToken(token)

            // 이 프로젝트에서는 ROLE 없으므로 빈값 반환
            val authorities = emptyList<SimpleGrantedAuthority>()
            val attributes = mapOf("email" to email)
            val principal = DefaultOAuth2User(authorities, attributes, "email")

            val authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}