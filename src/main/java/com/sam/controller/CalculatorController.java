package com.sam.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class CalculatorController {
	
	
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
	
}
