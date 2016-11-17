package io.joj.fluence.util;

/**
 * A variant of {@link java.util.function.BiFunction} that allows checked exceptions.
 * <p>
 * Unless you need to expose  checked exceptions in your API, you should use the original interface. However, when
 * you're unlucky enough and you have to deal with {@link java.io.IOException}, {@link
 * java.sql.SQLException} or any other checked exception, this class might come useful.
 *
 * @param <E>
 * 		type of checked exception possibly thrown by the implementation. You can use e.g. {@link RuntimeException} if
 * 		particular implementation does not throw any checked exceptions.
 */
public interface CheckedBiFunction<T, U, R, E extends Exception> {

	/**
	 * @see java.util.function.BiFunction#apply(Object, Object)
	 */
	R apply(T firstArg, U secondArg) throws E;

}
