package com.sam.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CalculateService {
	
	public static void main(String[] args) {
		String str = "1+  1*12/30  ";
		List<Object> list = analyze(str);
		System.out.println(list);
		
		for(Object obj : list) {
			if(obj instanceof Integer) 
				System.out.println("number = " + (Integer) obj);
			else if(obj instanceof Character)
				System.out.println("operator = " + (Character) obj);
			else
				System.out.println("not valid type = " + obj);
		}
	}
	
	public static List<Object> analyze(String input){
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
						list.add(Integer.valueOf(stb.toString()));
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
						list.add(Integer.valueOf(stb.toString()));
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
						list.add(Integer.valueOf(stb.toString()));
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
						list.add(Integer.valueOf(stb.toString()));
						stb.delete(0, stb.length());
						flag = false;
					}
					list.add(c);
					break;
					
				case ' ':
					if(flag) {
						list.add(Integer.valueOf(stb.toString()));
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
			list.add(Integer.valueOf(stb.toString()));
		
		return list;
	}
	
}
