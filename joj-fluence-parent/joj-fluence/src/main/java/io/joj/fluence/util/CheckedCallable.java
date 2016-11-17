package io.joj.fluence.util;

import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNull;

/**
 * A variant of {@link java.util.concurrent.Callable} that allows checked exceptions.
 * <p>
 * Unless you need to expose  checked exceptions in your API, you should use the original interface. However, when
 * you're unlucky enough and you have to deal with {@link java.io.IOException}, {@link
 * java.sql.SQLException} or any other checked exception, this class might come useful.
 *
 * @param <E>
 * 		type of checked exception possibly thrown by the implementation. You can use e.g. {@link RuntimeException} if
 * 		particular implementation does not throw any checked exceptions.
 */
public interface CheckedCallable<T, E extends Exception> {

	/**
	 * @see Callable#call()
	 */
	T call() throws E;

	default <R> CheckedCallable<R, E> map(CheckedFunction<T, R, ? extends E> func) {
		requireNonNull(func);
		return () -> func.apply(this.call());
	}
}
