package net.brunomonteiro.rullion;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConsoleApplicationTest {
	
	@Test
	public void testEmptyTasksReturnEmptyResult() {
		String tasks = "[]";
		String dependencies = "[]";

		String result = new ConsoleApplication(tasks, dependencies).getTasksExecutionOrder();
		assertEquals("[]", result);
	}

	@Test
	public void testEmptyDependenciesKeepDeclaredOrder() {
		String tasks = "[a,b]";
		String dependencies = "[]";

		String result = new ConsoleApplication(tasks, dependencies).getTasksExecutionOrder();
		assertEquals("[a,b]", result);
	}

	@Test
	public void testDependencyOnBReturnsBFirst() {
		String tasks = "[a,b]";
		String dependencies = "[a => b]";

		String result = new ConsoleApplication(tasks, dependencies).getTasksExecutionOrder();
		assertEquals("[b,a]", result);
	}

	@Test
	public void testDependenciesRevertPairOrder() {
		String tasks = "[a,b,c,d]";
		String dependencies = "[a => b,c => d]";

		String result = new ConsoleApplication(tasks, dependencies).getTasksExecutionOrder();
		assertEquals("[b,a,d,c]", result);
	}

	@Test
	public void testSequentialDependencyRevertsTaskOrder() {
		String tasks = "[a,b,c]";
		String dependencies = "[a => b,b => c]";

		String result = new ConsoleApplication(tasks, dependencies).getTasksExecutionOrder();
		assertEquals("[c,b,a]", result);
	}

	@Test
	public void testCyclicDependencyReturnsErrorMessage() {
		String tasks = "[a,b,c,d]";
		String dependencies = "[a => b,b => c,c => a]";

		String result = new ConsoleApplication(tasks, dependencies).getTasksExecutionOrder();
		assertEquals("Error - this is a cyclic dependency", result);
	}
}