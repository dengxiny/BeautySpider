package com.feiqu.beautyspider.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feiqu.beautyspider.service.StartService;

@RestController
public class SpiderController {
	public  static Logger logger = LoggerFactory.getLogger(SpiderController.class);
	@Autowired
	private  StartService startService;
	@Value("#{'${tag-list}'.split(',')}")
	private List<String> tagList;
	
	@RequestMapping("/start")
	public String start() {
		startService.start();
		return "ok";
	}
	
	@RequestMapping("/test")
	public String test() {
		for (String tagID : tagList) {
			logger.info("加载标签"+tagID);
		}
		//startService.start();
		return "ok";
	}
}