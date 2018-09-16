package com.xyt.timebooking.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xyt.timebooking.bean.Task;
//TODO: use log4j to refine log
@SpringBootApplication
public class TimeBookingApplication {

	private TimeSheetLoadingService timeSheetLoadService;
	private AutoBookTimeService autoBookTimeService;
	private String LOGIN_CONFIG_FILE = "Config.properties";
	
	public TimeBookingApplication() {
		Properties pro = new Properties();
		try {
			pro.load(new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + LOGIN_CONFIG_FILE)));
			String loginName = pro.getProperty("login.name");
			String loginPwd = pro.getProperty("login.pwd");
			timeSheetLoadService = new TimeSheetLoadingService();
			autoBookTimeService = new AutoBookTimeService(loginName, loginPwd);
			
			booking();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void booking() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String dateString = LocalDate.now().format(formatter);
		System.out.println(dateString);
		List<Task> taskList = timeSheetLoadService.getDailyWorkDetail(dateString);
		if(taskList ==  null && taskList.size() == 0) {
			return;
		}
		try {
			autoBookTimeService.book(taskList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		SpringApplication.run(TimeBookingApplication.class, args);
	}
}
