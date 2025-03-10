package com.example.anbdapi.support.global

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.hibernate.Session
import org.springframework.stereotype.Component

@Deprecated("임시로 @Where 쓰고 추후 개선 예정")
@Aspect
@Component
class HibernateFilterAspect {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Pointcut("execution(* com.example.anbdapi.domain..repository..*(..))")
    fun repositoryMethods() {}

    @Before("repositoryMethods()")
    fun enableDeletedFilter() {
        val session: Session = entityManager.unwrap(Session::class.java)
        if (session.getEnabledFilter("deletedFilter") == null) {
            session.enableFilter("deletedFilter")
        }
    }

    @After("repositoryMethods()")
    fun disableDeletedFilter() {
        val session: Session = entityManager.unwrap(Session::class.java)
        session.disableFilter("deletedFilter")
    }
}