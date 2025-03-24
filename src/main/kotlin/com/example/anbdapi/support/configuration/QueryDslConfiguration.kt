package com.example.anbdapi.support.configuration

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class QueryDSLConfig {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(entityManager)
    }
}