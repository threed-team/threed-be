package com.example.threedbe.common.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;

@Aspect
@Component
public class FilterAspect {

	@Autowired
	private EntityManager entityManager;

	@Pointcut("within(@org.springframework.stereotype.Service *)")
	public void serviceClass() {
	}

	@Before("serviceClass()")
	public void enableFilter() {
		Session session = entityManager.unwrap(Session.class);
		session.enableFilter("publishedPostFilter");
		session.enableFilter("deletedPostFilter");
	}

}
