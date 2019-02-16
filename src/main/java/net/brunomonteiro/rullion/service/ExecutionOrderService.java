package net.brunomonteiro.rullion.service;

import net.brunomonteiro.rullion.exception.CyclicDependencyException;
import net.brunomonteiro.rullion.exception.TaskNotFoundException;
import net.brunomonteiro.rullion.model.TaskDependency;

import java.util.ArrayList;
import java.util.List;

public class ExecutionOrderService {
	private List<String> tasks;
	private List<TaskDependency> dependencies;
	private List<String> executionOrder;
	
	public ExecutionOrderService(List<String> tasks, List<TaskDependency> dependencies) {
		if(tasks == null) {
			throw new IllegalArgumentException("Tasks can't be null");
		}
		
		this.tasks = tasks;
		setupDependencies(dependencies);
		parseTasks();
	}
	
	private void setupDependencies(List<TaskDependency> dependencies) {
		if(dependencies == null) {
			this.dependencies = new ArrayList<>();
			return;
		}
		
		for(TaskDependency taskDependency : dependencies) {
			if(!tasks.contains(taskDependency.getDependency())) {
				throw new TaskNotFoundException("\"" + taskDependency.getDependency()
					+ "\" it's not a task but was provided as a dependency of task \"" 
					+ taskDependency.getTask() + "\".");
			}
		}

		this.dependencies = dependencies;
	}
	
	private void parseTasks() {
		executionOrder = new ArrayList<>();
		for(String task : tasks) {
			if(isTaskParsed(task)) continue;

			List<String> stackPath = new ArrayList<>();
			stackPath.add(task);
			
			addTaskToExecution(task, stackPath);
		}
	}
	
	private boolean isTaskParsed(String task) {
		return executionOrder.contains(task);
	}
	
	private void addTaskToExecution(String task, List<String> stackPath) {
		List<String> taskDependencies = getTaskDependencies(task);
		for(String dependencyTask : taskDependencies) {
			if(isTaskParsed(dependencyTask)) continue;

			if(stackPath.contains(dependencyTask)) {
				throw new CyclicDependencyException();
			}

			stackPath.add(dependencyTask);
			addTaskToExecution(dependencyTask, stackPath);
		}

		executionOrder.add(task);
	}
	
	private List<String> getTaskDependencies(String task) {
		List<String> result = new ArrayList<>();
		for(TaskDependency taskDependency : dependencies) {
			if(taskDependency.getTask().equals(task)) {
				result.add(taskDependency.getDependency());
			}
		}
		return result;
	}
	
	public List<String> getExecutionOrder() {
		return executionOrder;
	}
}
