package com.sam.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
	
	@Around("execution(public * *(.., @JsonParam (*), ..))")
	public Object jsonParamConvert(ProceedingJoinPoint jointPoint) throws Throwable{
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		String body = null;
		
		if ("POST".equalsIgnoreCase(request.getMethod())) 
		{
		   body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		}
		
		JSONObject reqBody = new JSONObject(body);
		
		Annotation[][] paraAnnos = ((MethodSignature)jointPoint.getSignature()).getMethod().getParameterAnnotations();
		
		String[] paraNames = ((CodeSignature)jointPoint.getSignature()).getParameterNames();
		
		Class[] paraTypes = ((MethodSignature)jointPoint.getSignature()).getMethod().getParameterTypes();
		
		Object[] args = jointPoint.getArgs();
		
		int i = 0;
		
		for(String paraName : paraNames) {
			System.out.println(paraName);
			System.out.println(paraTypes[i].getSimpleName());
			for(Annotation anno : paraAnnos[i]) {
				if(anno instanceof JsonParam && reqBody.has(paraName)) {
					
					
					if("Integer".equals(paraTypes[i].getSimpleName())) {
						try {
							args[i] = Integer.valueOf(reqBody.getString(paraName));
						} catch (NumberFormatException e) {
							args[i] = null;
						}
					}
					else if("Float".equals(paraTypes[i].getSimpleName())) {
						try {
							args[i] = Float.valueOf(reqBody.getString(paraName));
						} catch (NumberFormatException e) {
							args[i] = null;
						}
					}
					else if("Double".equals(paraTypes[i].getSimpleName())) {
						try {
							args[i] = Double.valueOf(reqBody.getString(paraName));
						} catch (NumberFormatException e) {
							args[i] = null;
						}
					}
					else if("String".equals(paraTypes[i].getSimpleName()))
						args[i] = reqBody.getString(paraName);
					else if("ArrayList".equals(paraTypes[i].getSimpleName())) {
						ArrayList<Object> list = new ArrayList<Object>();
						try {
							JSONArray arr = reqBody.getJSONArray(paraName);
							
							for(Object obj : arr)
								list.add(obj);
							
							args[i] = list;
							
						} catch (JSONException e) {
//							e.printStackTrace();
							args[i] = null;
						}
						
					}
				}
			}
			i++;
		}
		
		return jointPoint.proceed(args);
	}
	
	
}
