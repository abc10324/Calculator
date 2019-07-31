package com.sam.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.javacrumbs.shedlock.core.SchedulerLock;

@Service
public class CalculateService {
	
	public Float calculate(String input) {
		if(!bracketCheck(input))
			return null;
			
		StringBuilder stb = new StringBuilder(input);
		while(stb.indexOf("(") != -1) {
			Pair<Integer,Integer> result = bracketAnalyze(stb.toString());
			
			Float sum = calculate(analyze(stb.substring(result.getLeft() + 1, result.getRight())));
			stb.delete(result.getLeft(), result.getRight()+1);
			
			if(sum != null)
				stb.insert(result.getLeft(), sum.toString());
			
		}
		
		return calculate(analyze(stb.toString()));
	}
	
	private List<Object> analyze(String input){
		char[] charArr = input.toCharArray();
		StringBuilder stb = new StringBuilder();
		List<Object> list = new ArrayList<>();
		boolean flag = false;
		boolean numFlag = true; // num = true , +-*/ = false
		
		for(char c : charArr) {
			switch(c) {
				case '+':
					if(numFlag)
						numFlag = false;
					else
						return null;
					
					if(flag) {
						list.add(Float.valueOf(stb.toString()));
						stb.delete(0, stb.length());
						flag = false;
					}
					list.add(c);
					break;
				
				case '-':
					if(numFlag)
						numFlag = false;
					else
						return null;
					
					if(flag) {
						list.add(Float.valueOf(stb.toString()));
						stb.delete(0, stb.length());
						flag = false;
					}
					list.add(c);
					break;
					
				case '*':
					if(numFlag)
						numFlag = false;
					else
						return null;
					
					if(flag) {
						list.add(Float.valueOf(stb.toString()));
						stb.delete(0, stb.length());
						flag = false;
					}
					list.add(c);
					break;
					
				case '/':
					if(numFlag)
						numFlag = false;
					else
						return null;
					
					if(flag) {
						list.add(Float.valueOf(stb.toString()));
						stb.delete(0, stb.length());
						flag = false;
					}
					list.add(c);
					break;
					
				case ' ':
					if(flag) {
						list.add(Float.valueOf(stb.toString()));
						stb.delete(0, stb.length());
						flag = false;
					}
					break;
				default:
					numFlag = true;
					
					stb.append(c);
					flag = true;
					break;
			}
			
		}
		
		if(stb.length() != 0)
			list.add(Float.valueOf(stb.toString()));
		
		return list;
	}
	
	private Float calculate(List<Object> list) {
		// empty check
		if(list == null || list.size() == 0)
			return null;
		
		// check invalid position's operator
		if(list.get(0) instanceof Character 
		|| list.get(list.size()-1) instanceof Character)
			return null;
		
		while(list.contains('*')) {
			for(int i=0,len=list.size() ; i<len ; i++) {
				if(list.get(i) instanceof Character) 
					if(((Character)list.get(i)) == '*' && i != 0 && i != len-1) {
						Float total = ((Float)list.get(i-1)) * ((Float)list.get(i+1));
						list.set(i-1, total);
						list.remove(i);
						list.remove(i);
						break;
					}
			}
		}
		
		while(list.contains('/')) {
			for(int i=0,len=list.size() ; i<len ; i++) {
				if(list.get(i) instanceof Character)
					if(((Character)list.get(i)) == '/' && i != 0 && i != len-1) {
						Float total = (Float)list.get(i-1) / (Float)list.get(i+1);
						list.set(i-1, total);
						list.remove(i);
						list.remove(i);
						break;
					}
			}
		}
		
		Float result = 0.0f;
		char operator = '+';
		
		for(int i=0,len=list.size() ; i<len ; i++) {
			if(list.get(i) instanceof Character)
				operator = (Character) list.get(i);
			else {
				if(operator == '+')
					result += (Float)list.get(i);
				else
					result -= (Float)list.get(i); 
			}
				
		}
		
		return result;
	}
	
	private boolean bracketCheck(String input) {
		char[] charArr = input.toCharArray();
		Stack<Character> stack = new Stack<>();
		
		for(char c : charArr) {
			if(c == '(')
				stack.push(c);
			else if(c == ')')
				if(stack.size() != 0 && stack.peek() == '(')
					stack.pop();
				else
					return false;
		}
		
		return stack.size() == 0;
	}
	
	private Pair<Integer,Integer> bracketAnalyze(String input) {
		char[] charArr = input.toCharArray();
		Stack<Pair<Character,Integer>> stack = new Stack<>();
		
		int i=0;
		for(char c : charArr) {
			if(c == '(')
				stack.push(ImmutablePair.of(c,i));
			else if(c == ')')
				if(stack.size() != 0 && stack.peek().getKey() == '(') {
					return ImmutablePair.of(stack.peek().getValue(),i);
				}
				else
					return null;
			i++;
		}
		
		return null;
	}
	
//	@Scheduled(cron="0/5 * * * * ?")
//	@SchedulerLock(name="Task1",lockAtLeastForString="PT2S",lockAtMostForString="PT3S")
//	public void scheduledTask() {
//		System.out.println(System.currentTimeMillis());
//		System.out.println("Hello Calc");
//	}
//	
//	@Scheduled(cron="0/5 * * * * ?")
//	@SchedulerLock(name="Task1",lockAtLeastForString="PT2S",lockAtMostForString="PT3S")
//	public void scheduledTask2() {
//		System.out.println(System.currentTimeMillis());
//		System.out.println("Hello Calc2");
//	}
	
}
