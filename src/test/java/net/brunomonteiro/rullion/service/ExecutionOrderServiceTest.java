package net.brunomonteiro.rullion.service;

import net.brunomonteiro.rullion.exception.CyclicDependencyException;
import net.brunomonteiro.rullion.exception.TaskNotFoundException;
import net.brunomonteiro.rullion.model.TaskDependency;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class ExecutionOrderServiceTest {
	
	@Test
	public void testEmptyTasksReturnEmptyResult() {
		List<String> tasks = new ArrayList<>();
		List<TaskDependency> dependencies = new ArrayList<>();

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertTrue(executionOrder.isEmpty());
	}

	@Test
	public void testEmptyDependenciesKeepDeclaredOrder() {
		List<String> tasks = Arrays.asList("a", "b");
		List<TaskDependency> dependencies = new ArrayList<>();
		
		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("a", "b"), executionOrder);
	}

	@Test
	public void testDependencyOnBReturnsBFirst() {
		List<String> tasks = Arrays.asList("a", "b");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("a", "b"));
	
		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("b", "a"), executionOrder);
	}

	@Test
	public void testDependenciesRevertPairOrder() {
		List<String> tasks = Arrays.asList("a", "b", "c", "d");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("a", "b"));
		dependencies.add(new TaskDependency("c", "d"));

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("b", "a", "d", "c"), executionOrder);
	}

	@Test
	public void testSequentialDependencyRevertsTaskOrder() {
		List<String> tasks = Arrays.asList("a", "b", "c");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("a", "b"));
		dependencies.add(new TaskDependency("b", "c"));

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("c", "b", "a"), executionOrder);
	}
	
	@Test(expected = CyclicDependencyException.class)
	public void testCyclicDependencyThrowsException() {
		List<String> tasks = Arrays.asList("a", "b", "c", "d");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("a", "b"));
		dependencies.add(new TaskDependency("b", "c"));
		dependencies.add(new TaskDependency("c", "a"));

		new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullTasksThrowsIllegalArgumentException() {
		List<String> tasks = null;
		List<TaskDependency> dependencies = new ArrayList<>();

		new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
	}

	@Test
	public void testNullDependenciesReturnsOriginalTaskOrder() {
		List<String> tasks = Arrays.asList("a", "b", "c");
		List<TaskDependency> dependencies = null;

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("a", "b", "c"), executionOrder);
	}

	@Test
	public void testAllTasksDependOnCReturnsCFirstAndOnce() {
		List<String> tasks = Arrays.asList("a", "b", "c");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("a", "c"));
		dependencies.add(new TaskDependency("b", "c"));

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("c", "a", "b"), executionOrder);
	}

	@Test
	public void testTaskWithDoubleDependencyReturnsBothDependenciesFirst() {
		List<String> tasks = Arrays.asList("a", "b", "c");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("a", "b"));
		dependencies.add(new TaskDependency("a", "c"));

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("b", "c", "a"), executionOrder);
	}

	@Test
	public void testAlphabeticUnorderedTasksDontImpactResult() {
		List<String> tasks = Arrays.asList("b", "a", "d", "c");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("d", "c"));

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("b", "a", "c", "d"), executionOrder);
	}

	@Test
	public void testDuplicatedDependenciesDontImpactResult() {
		List<String> tasks = Arrays.asList("a", "b", "c");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("b", "c"));
		dependencies.add(new TaskDependency("b", "c"));
		dependencies.add(new TaskDependency("a", "b"));

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("c", "b", "a"), executionOrder);
	}

	@Test
	public void testDuplicatedTaskAreIgnored() {
		List<String> tasks = Arrays.asList("a", "b", "a");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("a", "b"));

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("b", "a"), executionOrder);
	}

	@Test(expected = TaskNotFoundException.class)
	public void testDependencyOnNonExistingTaskReturnsTaskNotFound() {
		List<String> tasks = Arrays.asList("a", "b");
		List<TaskDependency> dependencies = new ArrayList<>();
		dependencies.add(new TaskDependency("a", "c"));

		List<String> executionOrder = new ExecutionOrderService(tasks, dependencies).getExecutionOrder();
		assertEquals(Arrays.asList("a", "b"), executionOrder);
	}
}