package com.sam.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sam.model.AuthList;
import com.sam.model.CalculateService;
import com.sam.model.JsonParam;
import com.sam.model.LogExecuteTime;
import com.sam.model.ParamCheck;

@Controller
@RestController
public class CalculatorController {
	
	@Autowired
	private CalculateService calculateService;
	
	@PostMapping("/plus")
	public Map<String,Integer> plus(@RequestBody String body){
		System.out.println(body);
		
		JSONObject obj = new JSONObject(body);
		JSONArray jsonArr = (JSONArray) obj.get("numbers");
		
		List<Object> list = jsonArr.toList();
		
		int total = 0;
		
		for(Object o : list)
			total += (Integer) o;
		
		Map<String,Integer> map = new HashMap<>();
		map.put("total",total);
		
		return map;
	}
	
	@LogExecuteTime
	@Cacheable("calcResult")
	@PostMapping("/calc")
	public Map<String,Object> calculate(@ParamCheck @RequestBody String body){
		System.out.println("In the method");
		
		JSONObject obj = new JSONObject(body);
		
		Map<String,Object> map = new HashMap<>();
		
		// data check
		String input = obj.getString("formula");
		
		if(input == null || input.trim().length() == 0) {
			map.put("error","Invalid input,input is empty");
			return map;
		}
		
		if(!input.matches("[0-9()+-/*// ]+")) {
			map.put("error","Invalid input,Contains not only numbers and operators");
			return map;
		}
		
		Float total = calculateService.calculate(obj.getString("formula"));
		
		
		
		if(total != null)
			map.put("total",total);
		else
			map.put("error","Invalid input,please check operator's position");
		
		return map;
	}
	
	@LogExecuteTime
	@GetMapping("/regist")
	public Map<String,String> getApiKey(){
		Map<String,String> map = new HashMap<>();
		String apiKey = UUID.randomUUID().toString().replace("-", "");
		
		AuthList.setAuth(apiKey);
		map.put("API key", apiKey);
		
		return map;
	}
	
	@PostMapping("/triPlus")
	public Map<String,Object> triplePlus(@JsonParam Double number1
									 ,@RequestParam Integer number2
									 	,@JsonParam ArrayList<Object> list){
		System.out.println("number1 = " + number1);
		System.out.println("number2 = " + number2);
		System.out.println("list = " + list);
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		if(number1 != null && number2 != null && list != null) {
			map.put("result1", number1);
			map.put("result2", number2);
			map.put("result3", list);
		}
		else
			map.put("error", "invalid input");
		
		return map;
	}
	
	@RequestMapping("/unauth")
	public Map<String,String> unauthorizedRequest(HttpServletResponse res){
		res.setStatus(401);
		
		Map<String,String> map = new HashMap<>();
		map.put("error", "unauthorized API key");
		
		return map;
	}
	
}
