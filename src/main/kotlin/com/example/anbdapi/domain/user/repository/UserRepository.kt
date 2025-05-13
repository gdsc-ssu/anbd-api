package com.example.anbdapi.domain.user.repository

import com.example.anbdapi.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    fun findByEmailIncludingDeleted(@Param("email") email: String): User?

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    fun restoreById(@Param("id") id: Long)
}