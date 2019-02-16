package net.brunomonteiro.rullion;

import net.brunomonteiro.rullion.exception.CyclicDependencyException;
import net.brunomonteiro.rullion.exception.TaskNotFoundException;
import net.brunomonteiro.rullion.model.TaskDependency;
import net.brunomonteiro.rullion.service.ExecutionOrderService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleApplication {
	private static final String ELEMENTS_SEPARATOR = ",";
	private static final String DEPENDENCY_SIGN = "=>";
	
	private List<String> tasks;
	private List<TaskDependency> dependencies;
	
	public ConsoleApplication(String tasksLine, String dependenciesLine) {
		this.setupTasks(tasksLine);
		this.setupDependencies(dependenciesLine);
	}

	private void setupTasks(String tasksLine) {
		tasksLine = removeSquareBrackets(removeSpaces(tasksLine));
		if(tasksLine.isEmpty()) {
			tasks = new ArrayList<>();
		} else {
			tasks = Arrays.asList(tasksLine.split(ELEMENTS_SEPARATOR));
		}
	}

	private void setupDependencies(String dependenciesLine) {
		dependencies = new ArrayList<>();
		
		dependenciesLine = removeSquareBrackets(removeSpaces(dependenciesLine));
		if(dependenciesLine.isEmpty()) return;

		String[] elements = dependenciesLine.split(ELEMENTS_SEPARATOR);
		for(String element : elements) {
			String[] params = element.split(DEPENDENCY_SIGN);
			if(params.length != 2) continue;

			String task = params[0];
			String dependency = params[1];
			dependencies.add(new TaskDependency(task, dependency));
		}
	}

	private String removeSquareBrackets(String line) {
		return line.replaceAll("\\[|\\]", "");
	}
	
	private String removeSpaces(String line) {
		return line.replace(" ", "");
	}

	public String getTasksExecutionOrder() {
		try {
			ExecutionOrderService parser = new ExecutionOrderService(tasks, dependencies);
			String result = parser.getExecutionOrder().toString();
			return removeSpaces(result);
			
		} catch(CyclicDependencyException | TaskNotFoundException ex) {
			return "Error - " + ex.getMessage();
		}
	}
	
	public static void main(String... args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("tasks: ");
		String tasks = scanner.nextLine();

		System.out.print("dependencies: ");
		String dependencies = scanner.nextLine();

		ConsoleApplication app = new ConsoleApplication(tasks, dependencies);
		System.out.println("result: " + app.getTasksExecutionOrder());
	}
}
