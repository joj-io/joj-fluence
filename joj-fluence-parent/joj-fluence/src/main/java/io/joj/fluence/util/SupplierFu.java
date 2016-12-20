package io.joj.fluence.util;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Makes your {@link Supplier}s more fluent.
 *
 * @author findepi
 * @since 2016-12-20
 */
public class SupplierFu {
	private SupplierFu() {
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
		return new MemoizingSupplier<>(supplier);
	}

	private static final class MemoizingSupplier<T> implements Supplier<T>, Serializable {
		private static final long serialVersionUID = -7346764451425702926L;

		private final Supplier<T> delegate;
		private transient volatile T memoizedValue = null;

		public MemoizingSupplier(Supplier<T> supplier) {
			super();
			this.delegate = requireNonNull(supplier, "supplier cannot be null");
		}

		@Override
		public T get() {
			T value = memoizedValue; // volatile
			if (value == null) {
				synchronized (this) {
					value = memoizedValue;
					if (value == null) {
						memoizedValue = value = requireNonNull(delegate.get(), "delegate supplier returned null " +
								"value");
					}
				}
			}
			return value;
		}

		@Override
		public String toString() {
			Object value = Optional.<Object>ofNullable(memoizedValue).orElse("no memoized value");
			return format("%s(%s, memoized=%s)", getClass().getSimpleName(), delegate, value);
		}
	}
}
