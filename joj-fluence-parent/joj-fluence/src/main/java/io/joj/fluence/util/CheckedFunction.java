package io.joj.fluence.util;

import java.util.function.Consumer;

/**
 * A variant of {@link java.util.function.Function} that allows checked exceptions.
 * <p>
 * Unless you need to expose  checked exceptions in your API, you should use the original interface. However, when
 * you're unlucky enough and you have to deal with {@link java.io.java.io.IOException}, {@link
 * java.sql.java.sql.SQLException} or any other checked exception, this class might come useful.
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

}
