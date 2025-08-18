package com.example.anbdapi.support.utils.jwt

import com.example.anbdapi.domain.auth.exception.TokenExpiredException
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import com.example.anbdapi.support.response.AuthResponseCode
import com.example.anbdapi.support.response.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val traceIdResolver: TraceIdResolver
) : OncePerRequestFilter() {

    private val excludedUrls = listOf(
        "/v1/auth/mobile/google",
        "/v1/auth/mobile/apple",
        "/v1/auth/tokens/refresh",
        "/v1/users/logout"
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return excludedUrls.contains(request.requestURI)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val header = request.getHeader("Authorization")

            if (!header.isNullOrBlank() && header.startsWith("Bearer ")) {
                val token = header.removePrefix("Bearer ").trim()

                val claims = jwtUtil.validateAndExtractClaims(token)
                val userId = claims.subject ?: throw IllegalArgumentException("UserId not found in token")

                val authorities = emptyList<SimpleGrantedAuthority>()
                val principal = JwtUserPrincipal(userId, authorities)

                val authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
            }
            filterChain.doFilter(request, response)

        } catch (ex: TokenExpiredException) {
            handleTokenExpiredException(response, ex)
            return
        } catch (ex: JwtException) {
            handleJwtException(response, ex)
            return
        } catch (ex: Exception) {
            filterChain.doFilter(request, response)
        } finally {
            SecurityContextHolder.clearContext()
        }
    }

    private fun handleTokenExpiredException(response: HttpServletResponse, ex: TokenExpiredException) {
        val errorResponse = AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_ACCEPTABLE,
            code = AuthResponseCode.AUTH_01,
            body = ErrorResponse(ex.message ?: "Token has expired.")
        )

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpStatus.NOT_ACCEPTABLE.value()
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

    private fun handleJwtException(response: HttpServletResponse, ex: JwtException) {
        val errorResponse = AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.UNAUTHORIZED,
            code = AuthResponseCode.AUTH_02,
            body = ErrorResponse(ex.message ?: "Invalid token.")
        )

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}