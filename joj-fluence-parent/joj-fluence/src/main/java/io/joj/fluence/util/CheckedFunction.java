package io.joj.fluence.util;

import static java.util.Objects.requireNonNull;

/**
 * A variant of {@link java.util.function.Function} that allows checked exceptions.
 * <p>
 * Unless you need to expose  checked exceptions in your API, you should use the original interface. However, when
 * you're unlucky enough and you have to deal with {@link java.io.IOException}, {@link
 * java.sql.SQLException} or any other checked exception, this class might come useful.
 *
 * @param <E>
 * 		type of checked exception possibly thrown by the implementation. You can use e.g. {@link RuntimeException} if
 * 		particular implementation does not throw any checked exceptions.
 */
public interface CheckedFunction<T, R, E extends Exception> {

	/**
	 * @see java.util.function.Function#apply(Object)
	 */
	R apply(T arg) throws E;

	/**
	 * Returns a composed function that first applies {@code before} and then {@code this}.
	 */
	default <M> CheckedFunction<M, R, E> combine(CheckedFunction<M, T, ? extends E> before) {
		requireNonNull(before);
		return t -> this.apply(before.apply(t));
	}

	/**
	 * Returns a composed function that first applies {@code this} and then {@code after}.
	 */
	default <M> CheckedFunction<T, M, E> thenApply(CheckedFunction<R, M, ? extends E> after) {
		requireNonNull(after);
		return t -> after.apply(this.apply(t));
	}
}
