package com.xyt.timebooking.bean;

public class Task {
	
	private String projectName;
	private String taskName;
	private String workType = "缺省";
	private float workingHours;
	private String workLog = "这是一个默认值";
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public float getWorkingHours() {
		return workingHours;
	}
	public void setWorkingHours(float workingHours) {
		this.workingHours = workingHours;
	}
	
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	
	public String getWorkLog() {
		if(workLog == null)
			return taskName;
		else
			return workLog;
	}
	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}
	@Override
	public String toString() {
		return "Task [projectName=" + projectName + ", taskName=" + taskName + ", workType=" + workType
				+ ", workingHours=" + workingHours + ", workLog=" + workLog + "]";
	}
}
