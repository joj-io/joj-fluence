package io.joj.fluence.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Makes your {@link Collector}s more fluent.
 *
 * @author findepi
 * @since 2016-12-20
 */
public class CollectorFu {
	private CollectorFu() {
	}

	/**
	 * Returns a {@code Collector} that accumulates input elements into a
	 * {@code Map}  using the result of applying the provided
	 * function to the input elements as the map keys.
	 * <p>
	 * If the mapped keys contains duplicates (according to
	 * {@link Object#equals(Object)}), an {@code IllegalStateException} is
	 * thrown when the collection operation is performed. If the mapped keys
	 * may have duplicates, use {@link Collectors#toMap(Function, Function, BinaryOperator)} instead.
	 * <p>
	 * Returned {@link Collector} does not support {@code null} input elements.
	 */
	public static <T, K> Collector<T, ?, Map<K, T>> indexingUniquelyBy(
			Function<? super T, ? extends K> keyFunction) {

		/*
		* Equivalent to Collectors.toMap(keyFunction, Function.identity()) but (currently) gives better exception
		  message on duplicate keys: "Duplcate key <key>" rather than "Duplicate key <input element>".
		  */

		return Collector.of(
				HashMap::new,
				(map, el) -> {
					requireNonNull(el, "element is null");
					K key = keyFunction.apply(el);
					map.merge(key, el, (oldEl, newEl) -> {
						throw new IllegalStateException(format("Duplicate key: %s", key));
					});
				},
				(m1, m2) -> {
					for (Map.Entry<K, T> e : m2.entrySet()) {
						K key = e.getKey();
						m1.merge(key, e.getValue(), (oldEl, newEl) -> {
							throw new IllegalStateException(format("Duplicate key: %s", key));
						});
					}
					return m1;
				},
				Collector.Characteristics.IDENTITY_FINISH
		);
	}

	/**
	 * Returns a {@code Collector} that accumulates input elements into a
	 * {@code Map}  using the result of applying the provided
	 * function to the input elements as the map keys, while allowing multiple input elements to be mapped to one key.
	 */
	public static <T, K> Collector<T, ?, Map<K, List<T>>> indexingBy(Function<? super T, ? extends K> keyFunction) {

		return Collectors.toMap(
				keyFunction,
				t -> Collections.singletonList(t),
				(listA, listB) -> {
					return Stream.concat(listA.stream(), listB.stream())
							.collect(Collectors.toList());
				});
	}
}
