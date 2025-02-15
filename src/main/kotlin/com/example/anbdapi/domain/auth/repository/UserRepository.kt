package com.example.anbdapi.domain.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<com.example.anbdapi.domain.auth.entity.User, Long> {
    fun findByEmail(email: String): com.example.anbdapi.domain.auth.entity.User?
}