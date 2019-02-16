package net.brunomonteiro.rullion.exception;

public class CyclicDependencyException extends RuntimeException {
	public CyclicDependencyException() {
		super("this is a cyclic dependency");
	}
}
