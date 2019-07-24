package com.sam.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sam.model.CalculateService;

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
	
	
	@PostMapping("/calc")
	public Map<String,Object> calculate(@RequestBody String body){
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
	
}
