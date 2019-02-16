package net.brunomonteiro.rullion.model;

public class TaskDependency {
	private String task;
	private String dependency;

	public TaskDependency(String task, String dependency) {
		this.task = task;
		this.dependency = dependency;
	}

	public String getTask() {
		return this.task;
	}

	public String getDependency() {
		return this.dependency;
	}
}
