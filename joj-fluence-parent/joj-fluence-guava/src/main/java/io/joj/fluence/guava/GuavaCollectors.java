package io.joj.fluence.guava;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * {@link Collector}-s for Guava types.
 * 
 * @author findepi
 * @since Oct 24, 2016
 */
public class GuavaCollectors {

	/**
	 * Returns a {@code Collector} that accumulates the input elements into an {@code ImmutableList}, preserving the
	 * order.
	 * <p>
	 * This works similar to {@link Collectors#toList()} except that return value is guaranteed to be immutable and the
	 * return type proves that.
	 *
	 * @param <T>
	 *            the type of the input elements
	 */
	public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
		return toImmutableListImpl();
	}

	@VisibleForTesting
	static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> toImmutableListImpl() {

		return Collector.<T, ImmutableList.Builder<T>, ImmutableList<T>> of(
				ImmutableList::builder,
				ImmutableList.Builder::add,
				(builder1, builder2) -> builder1.addAll(builder2.build()),
				ImmutableList.Builder::build);
	}

	/**
	 * Returns a {@code Collector} that accumulates elements into an {@code ImmutableMap} whose keys and values are the
	 * result of applying the provided mapping functions to the input elements.
	 * <p>
	 * This works similar to {@link Collectors#toMap(Function, Function)} except that return value is guaranteed to be
	 * immutable and the return type proves that.
	 * 
	 * @param <T>
	 *            the type of the input elements
	 * @param <K>
	 *            the type of the keys in the produced map
	 * @param <V>
	 *            the type of the values in the produced map
	 */
	public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
			Function<? super T, ? extends K> keyMapper,
			Function<? super T, ? extends V> valueMapper) {

		return toImmutableMapImpl(keyMapper, valueMapper);
	}

	@VisibleForTesting
	static <T, K, V> Collector<T, ImmutableMap.Builder<K, V>, ImmutableMap<K, V>> toImmutableMapImpl(
			Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {

		return Collector.<T, ImmutableMap.Builder<K, V>, ImmutableMap<K, V>> of(
				ImmutableMap::builder,
				(builder, element) -> builder.put(keyMapper.apply(element), valueMapper.apply(element)),
				(builder1, builder2) -> builder1.putAll(builder2.build()),
				ImmutableMap.Builder::build,
				Characteristics.UNORDERED);
	}
}
