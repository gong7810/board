package board.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggerAspect {

	LoggerAspect loggerAspect;
	@Around("execution(* board..controller.*.*(..)) or execution(* board..service.*.*(..)) or execution(* board..mapper.*.*(..))")
	public Object logPrint(ProceedingJoinPoint joinPoint) throws Throwable {
		String type = "";
		String name = joinPoint.getSignature().getDeclaringTypeName();
		if (name.indexOf("Controller") > -1) {
			type = "Controller  \t:  ";
		}
		else if (name.indexOf("Service") > -1) {
			type = "ServiceImpl  \t:  ";
		}
		else if (name.indexOf("Mapper") > -1) {
			type = "Mapper  \t\t:  ";
		}
		log.debug(type + name + "." + joinPoint.getSignature().getName() + "()");
		return joinPoint.proceed();
	}
	@AfterThrowing(pointcut = "execution(* board..service.*Impl.*(..))", throwing = "exception")
	public void afterThrowingAdvice(JoinPoint joinPoint, DataAccessException exception) {
		System.out.println("After method execution, throwing exception: " + exception.getMessage());
	}
	@AfterThrowing(pointcut = "execution(* board..service.*Impl.*(..))", throwing = "exception")
	public void afterThrowingAdvice(JoinPoint joinPoint, Exception exception) {
		System.out.println("After method execution, throwing exception: " + exception.getMessage());
	}
}
