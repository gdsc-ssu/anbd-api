package com.example.anbdapi.domain.dev.auth.global

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.hibernate.Session
import org.springframework.stereotype.Component

@Aspect
@Component
class HibernateFilterAspect {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Pointcut("execution(* com.example.anbdapi.domain.dev.auth.repository..*(..))")
    fun repositoryMethods() {}

    @Before("repositoryMethods()")
    fun enableDeletedFilter() {
        val session: Session = entityManager.unwrap(Session::class.java)
        if (session.getEnabledFilter("deletedFilter") == null) {
            session.enableFilter("deletedFilter")
        }
    }
}