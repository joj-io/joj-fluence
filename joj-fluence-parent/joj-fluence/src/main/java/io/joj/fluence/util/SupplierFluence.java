package io.joj.fluence.util;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Makes your {@link Supplier}s more fluent.
 *
 * @author findepi
 * @since Oct 27, 2016
 * @deprecated Use {@link SupplierFu} instead.
 */
@Deprecated
public class SupplierFluence {
	private SupplierFluence() {
	}

	/**
	 * Returns a <a href="https://en.wikipedia.org/wiki/Memoization"><em>memoizing</em></a> supplier. The original
	 * {@code supplier} will be called at most once, unless the call fails or returns {@code null}. Then the call will
	 * be repeated.
	 * <p>
	 * The returned supplier does not allow {@code null} values, i.e. if the {@code supplier} returns {@code null}, a
	 * {@link NullPointerException} will be raised.
	 * <p>
	 * The returned supplier is {@link Serializable} iff the {@code supplier} also is. The memoized value will not be
	 * serialized.
	 */
	public static <T> Supplier<T> memoize(Supplier<T> supplier) {
		return SupplierFu.memoize(supplier);
	}
}
