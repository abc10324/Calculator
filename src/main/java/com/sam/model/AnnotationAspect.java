package com.sam.model;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AnnotationAspect {
	
	@Around("@annotation(LogExecuteTime)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
	    System.out.println(joinPoint.getSignature() + " executed start in " + start + "ms");
 
	    Object proceed = joinPoint.proceed();
	 
	    long executionTime = System.currentTimeMillis() - start;
	 
	    System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");
	
	    return proceed;
	    
	}
	
	@Around("execution(public * *(.., @ParamCheck (*), ..))")
	public Object paramCheck(ProceedingJoinPoint joinPoint) throws Throwable{
		
		Object[] args = joinPoint.getArgs();
		Annotation[][] paraAnnoArr = ((MethodSignature)joinPoint.getSignature()).getMethod().getParameterAnnotations();
		
		int i = 0;
		
		for(Object arg : args) {
			for(Annotation anno : paraAnnoArr[i]) {
				if(anno instanceof ParamCheck) {
					if(arg instanceof String) {
						System.out.println(arg);
						if("".equals((String)arg) || arg == null) {
							Map<String,String> map = new HashMap<>();
							map.put("error", "empty input");
							
							return map;
						}
					} else {
						if(arg == null) {
							Map<String,String> map = new HashMap<>();
							map.put("error", "empty input");
							
							return map;
						}
					}
				}
			}
			
			i++;
		}
		
		// if everything is alright ,then go proceed
		Object proceed = joinPoint.proceed();
		
		
		// at the end of method , if no other request , then go proceed
		return proceed;
	}
	
}
