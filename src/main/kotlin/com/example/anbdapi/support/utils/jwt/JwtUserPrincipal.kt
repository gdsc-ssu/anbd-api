package com.example.anbdapi.support.utils.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class JwtUserPrincipal(
    private val userId: String,
    private val authorities: Collection<GrantedAuthority> = emptyList()
) : UserDetails {

    fun getUserId(): String = userId

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String = ""  // 사용하지 않음

    override fun getUsername(): String = userId
}