package com.xyt.timebooking.service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.xyt.timebooking.bean.Task;
/////TODO list:
//// 3. remove the alert window after save success
//// 4. logout
//// 5. refine(move xpath to String)


public class AutoBookTimeService {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
  private float taskStartTime;
  private float taskEndTime;
  
  private int task_number = 1;
  
  private String selinume_chrome_driver_name = "chromedriver.exe";
  
  private String system_login_name = "";
  private String system_login_pwd = "";
  
  public AutoBookTimeService(String system_login_name, String system_login_pwd) {
	this.system_login_name = system_login_name;
	this.system_login_pwd = system_login_pwd;
	System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + selinume_chrome_driver_name);
    driver = new ChromeDriver();
    baseUrl = "";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  public void book(List<Task> taskList) throws Exception {
    loginSystem();
    initial();
    bookTime(taskList);
    logoutSystem();
  }
  
  private void initial() {
	  taskStartTime = 8.5f;
	  taskEndTime = 9.5f;
	  
  }
  private List<Task> mockUpTaskList(){
	  List<Task> taskList = new ArrayList<Task>();
	  
	  Task t1 = new Task();
	  t1.setProjectName("缺省");
	  t1.setTaskName("无");
	  t1.setWorkLog("项目-设计系统测试用例");
	  t1.setWorkType("项目-设计系统测试用例");
	  t1.setWorkingHours(2.5f);
	  
	  Task t2 = new Task();
	  t2.setProjectName("缺省");
	  t2.setTaskName("无");
	  t2.setWorkLog("项目-设计系统测试用例");
	  t2.setWorkType("项目-设计系统测试用例");
	  t2.setWorkingHours(5.5f);
	  
	  Task t3 = new Task();
	  t3.setProjectName("缺省");
	  t3.setTaskName("无");
	  t3.setWorkLog("项目-设计系统测试用例");
	  t3.setWorkType("项目-设计系统测试用例");
	  t3.setWorkingHours(2);
	  
	  taskList.add(t1);
	  taskList.add(t2);
	  taskList.add(t3);
	  return taskList;
  }
  
  private void bookTime(List<Task> taskList) throws InterruptedException {
	driver.findElement(By.xpath("//i")).click();
	Thread.sleep(1000);
	driver.findElement(By.xpath("//div[@id='mCSB_2_container']/ul/li[2]/a/div[2]")).click();
	Thread.sleep(1000);
	
//	taskList = mockUpTaskList();
	for (Task task : taskList) {
		BookTask(task);
		Thread.sleep(1000);
		//enable next book
		
		WebElement nextTaskBox = driver.findElement(By.xpath("(//div[@class='list'])[" + task_number + "]"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextTaskBox);
	    Thread.sleep(2000);
		
		if(task_number <= taskList.size()) {
			driver.findElement(By.xpath("//div[@class='time-add page-box-con' and contains(@style, 'display: inline-block')]")).click();
		}
	}
    //submit
    driver.findElement(By.xpath("(//div[@class='box-btn box-btn-save'])[" + (task_number -1) + "]")).click();
    try {
		Thread.sleep(5000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }
  
	private void BookTask(Task task) throws InterruptedException {
		String today = String.valueOf(determineIntraday());
	    Thread.sleep(1000);
	    taskEndTime = taskStartTime + task.getWorkingHours();
	    if(taskEndTime > 12) {
	    	taskEndTime ++;
	    }
	    
	    inputBeginTime(today);
	    inputEndTime(today);
	    
	    String projectName = task.getProjectName();
	    String subProjectName = task.getTaskName();
	    String workType = task.getWorkType();
	    String workLog = task.getWorkLog();
	    
	//    driver.findElement(By.xpath("(//select[@id='projectname'])[" + task_number + "]")).click();
	    new Select(driver.findElement(By.xpath("(//select[@id='projectname'])[" + task_number + "]"))).selectByVisibleText(projectName);
	    
	//    driver.findElement(By.xpath("(//select[@id='subprojectname'])[" + task_number + "]")).click();
	    new Select(driver.findElement(By.xpath("(//select[@id='subprojectname'])[" + task_number + "]"))).selectByVisibleText(subProjectName);
	    
	//    driver.findElement(By.xpath("(//select[@id='workType'])[" + task_number + "]")).click();
	    new Select(driver.findElement(By.xpath("(//select[@id='workType'])[" + task_number + "]"))).selectByVisibleText(workType);
	    
	    WebElement workLogElement = driver.findElement(By.xpath("(//textarea[@id='workLog'])["+ task_number +"]"));
	    
	    JavascriptExecutor executor = (JavascriptExecutor)driver;
	    executor.executeScript("arguments[0].scrollIntoView(true)", workLogElement);
	    
	    Thread.sleep(1000);
	    workLogElement.sendKeys(workLog);
	//    driver.findElement(By.xpath("(//textarea[@id='workLog'])["+ task_number +"]")).click();
	//    driver.findElement(By.xpath("(//textarea[@id='workLog'])["+ task_number +"]")).sendKeys(workLog);
	    
	    taskStartTime = taskEndTime;
	    task_number ++;
	}

  private int determineIntraday() {
	  return LocalDate.now().getDayOfMonth();
  }
  
  private void inputBeginTime(String inputDay) throws InterruptedException {
	driver.findElement(By.xpath("(//input[@id='beginTime'])[" + task_number + "]")).click();
	Thread.sleep(1000);
	chooseDay(inputDay);
    chooseHour(taskStartTime);
    chooseMinutes(taskStartTime);
  }

  private void chooseMinutes(float taskTime) {
	int minPos = 1;
    String minutes = determineMinutes(taskTime);
    if("HALF".equals(minutes)) {
    	minPos = 2;
    } else {
    	minPos = 1;
    }
    driver.findElement(By.xpath("//div[@class='datetimepicker datetimepicker-dropdown-bottom-right dropdown-menu' and contains(@style,'display: block')]/div[@class='datetimepicker-minutes']/table/tbody/tr/td/fieldset/span["+minPos +"]")).click();
  }

  private void inputEndTime(String inputDay) throws InterruptedException {
	driver.findElement(By.xpath("(//input[@id='endTime'])[" + task_number + "]")).click();
	Thread.sleep(1000);
	chooseDay(inputDay);
    chooseHour(taskEndTime);
    chooseMinutes(taskEndTime);
  }

  private void chooseHour(float taskTime) {
	String morningOrAfternoon = determineMorningOrAfternoon(taskTime);
    int fieldset = 1;
    if ("MORNING".equals(morningOrAfternoon)) {
    	fieldset = 1;
    } else {
    	fieldset = 2;
    }
    
    String hours = determineHours(taskTime);
    int hourPos = locateHourPosition(hours);
    
    driver.findElement(By.xpath("//div[@class='datetimepicker datetimepicker-dropdown-bottom-right dropdown-menu' and contains(@style,'display: block')]/div[@class='datetimepicker-hours']/table/tbody/tr/td/fieldset[" + fieldset + "]/span[" + hourPos + "]")).click();
  }

  private void chooseDay(String inputDay) {
	for (int i = 1; i <= 6; i++) {
		for (int j = 1; j <= 7; j++) {
			String calDay =driver.findElement(By.xpath("//div[@class='datetimepicker datetimepicker-dropdown-bottom-right dropdown-menu' and contains(@style,'display: block')]/div[@class='datetimepicker-days']/table/tbody/tr["+i+"]/td["+j+"]")).getText();
			if(inputDay.equals(calDay)) {
				driver.findElement(By.xpath("//div[@class='datetimepicker datetimepicker-dropdown-bottom-right dropdown-menu' and contains(@style,'display: block')]/div[@class='datetimepicker-days']/table/tbody/tr["+i+"]/td["+j+"]")).click();
				return;
			}
		}
	}
  }

  private String determineMorningOrAfternoon(float taskTime) {
	if(taskTime < 12)
		return "MORNING";
	else return "AFTERNOON";
  }


  private int locateHourPosition(String hour) {
	if("12".equals(hour)) {
		return 13;
	} else if ("24".equals(hour)) {
		return 1;
	} else {
		int hourNum = Integer.valueOf(hour);
		
		if( hourNum < 12) {
			return hourNum + 1;
		} else {
			return hourNum - 12 + 1;
		}
	}
  }

  private String determineMinutes(float taskTime) {
	if(taskTime > Math.floor(taskTime))
		return "HALF";
	else
		return "CLOCK";
  }

  private String determineHours(float taskTime) {
	double hourDouble = Math.floor(taskTime);
	return StringUtils.substringBefore(String.valueOf(hourDouble), ".");
	
  }

  private void loginSystem() throws InterruptedException {
	driver.manage().window().maximize();
	driver.get("http://wh.runlin.cn:8383/workinghours/login.html?backto=%2Fworkinghours%2F%2FaddWorkingHours.html");
    driver.findElement(By.id("loginname")).click();
    driver.findElement(By.id("loginname")).clear();
    driver.findElement(By.id("loginname")).sendKeys(system_login_name);
    driver.findElement(By.id("pwd")).clear();
    driver.findElement(By.id("pwd")).sendKeys(system_login_pwd);
    driver.findElement(By.linkText("登 录")).click();
    Thread.sleep(1000);
  }
  
  private void logoutSystem() {
	  if(driver != null) {
		  driver.quit();
	  }
  }

}
