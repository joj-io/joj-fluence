package io.joj.fluence.guava;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author findepi
 * @since Oct 24, 2016
 */
public class GuavaCollectorsTest {

	@SuppressWarnings("unused")
	private class Builder {
		// prevent ambiguous import of ImmutableSomething.Builder
	}

	@Test(dataProvider = "testCollectToImmutableListDataProvider")
	public void testCollectToImmutableList(List<?> inputList) {
		// Given inputList
		// When
		ImmutableList<?> collected = inputList.stream()
				.collect(GuavaCollectors.toImmutableList());
		// Then
		assertEquals(collected, inputList);
	}

	@DataProvider
	public Object[][] testCollectToImmutableListDataProvider() throws Exception {
		return new Object[][] {
				{ emptyList() },
				{ singletonList("a") },
				{ asList("a", 2L, new Object()) },
				{ asList("a", "a", new String("a")) },
		};
	}

	@Test
	public void testToImmutableListResultContainer() {
		// Given
		Collector<Object, ?, ImmutableList<Object>> collector = GuavaCollectors.toImmutableList();

		// When
		Object resultContainer = collector.supplier().get();
		Set<Characteristics> characteristics = collector.characteristics();

		// Then
		Assertions.assertThat(resultContainer).as("result container")
				.isInstanceOf(ImmutableList.Builder.class);

		assertEquals(((ImmutableList.Builder<?>) resultContainer).build(), emptyList(),
				"initially the result container should be empty");

		assertNotSame(resultContainer, collector.supplier().get(),
				"every time new result container should be returned");

		Assertions.assertThat(characteristics).as("characteristics")
				.doesNotContain(Collector.Characteristics.CONCURRENT) // ImmutableList.Builder is not concurrent
				.doesNotContain(Collector.Characteristics.IDENTITY_FINISH) // ImmutableList.Builder is not an
																			// ImmutableList
		;
	}

	@Test
	public void testToImmutableListPreservesOrder() {
		// Given
		Collector<Object, ?, ImmutableList<Object>> collector = GuavaCollectors.toImmutableList();

		// When
		List<Long> inputList = newRandomList(100);
		ImmutableList<Long> collected = inputList.stream()
				.collect(GuavaCollectors.toImmutableList());
		Set<Characteristics> characteristics = collector.characteristics();

		// Then
		assertEquals(collected, inputList);
		Assertions.assertThat(characteristics).as("characteristics")
				.doesNotContain(Collector.Characteristics.UNORDERED) // collecting list naturally should preserve order
		;
	}

	@Test
	public void testToImmutableListRejectNullEarly() {
		// Given
		Collector<Object, ImmutableList.Builder<Object>, ImmutableList<Object>> collector = GuavaCollectors
				.toImmutableListImpl();

		// When
		BiConsumer<ImmutableList.Builder<Object>, Object> accumulator = collector.accumulator();
		ImmutableList.Builder<Object> resultContainer = collector.supplier().get();

		Assertions.assertThatThrownBy(() -> {
			accumulator.accept(resultContainer, null);
		})
				// Then
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void testToImmutableListCombineResults() {
		// Given
		Collector<Object, ImmutableList.Builder<Object>, ImmutableList<Object>> collector = GuavaCollectors
				.toImmutableListImpl();

		ImmutableList.Builder<Object> firstPartialResult = ImmutableList.builder().add(1, 2, 3, "a");
		ImmutableList.Builder<Object> secondPartialResult = ImmutableList.builder().add(4, 5);

		// When
		ImmutableList.Builder<Object> combined = collector.combiner().apply(firstPartialResult, secondPartialResult);

		// Then
		assertEquals(combined.build(), asList(1, 2, 3, "a", 4, 5),
				"list built from combined partial result containers");
	}

	@Test(dataProvider = "testToImmutableMapDataProvider")
	public <T> void testToImmutableMap(Supplier<Stream<T>> stream, Function<T, ?> keyMapper, Function<T, ?> valueMapper,
			Map<?, ?> expected) {
		// Given all the above
		// When
		ImmutableMap<?, ?> collected = stream.get().collect(GuavaCollectors.toImmutableMap(keyMapper, valueMapper));
		// Then
		assertEquals(collected, expected);
	}

	@DataProvider
	public Object[][] testToImmutableMapDataProvider() throws Exception {
		return new Object[][] {
				{
						(Supplier<Stream<?>>) emptyList()::stream,
						Function.identity(),
						Function.identity(),
						emptyMap(),
				},
				{
						(Supplier<Stream<?>>) emptyList()::stream,
						throwingFunction(),
						throwingFunction(),
						emptyMap(),
				},
				{
						(Supplier<Stream<?>>) asList("a")::stream,
						Function.identity(),
						Function.identity(),
						singletonMap("a", "a")
				},
				{
						(Supplier<Stream<?>>) asList("a", "bc", "def", "")::stream,
						(Function<String, ?>) String::length,
						(Function<String, ?>) String::toUpperCase,
						ImmutableMap.of(
								0, "",
								1, "A",
								2, "BC",
								3, "DEF"),
				},
		};
	}

	@Test(dataProvider = "testToImmutableMapRejectNullEarlyDataProvider")
	public void testToImmutableMapRejectNullEarly(String input, Function<String, Integer> keyMapper,
			Function<String, String> valueMapper) {

		// Given
		Collector<String, ImmutableMap.Builder<Integer, Object>, ImmutableMap<Integer, Object>> collector = GuavaCollectors
				.toImmutableMapImpl(keyMapper, valueMapper);

		// When
		BiConsumer<ImmutableMap.Builder<Integer, Object>, String> accumulator = collector.accumulator();
		ImmutableMap.Builder<Integer, Object> resultContainer = collector.supplier().get();

		Assertions.assertThatThrownBy(() -> {
			accumulator.accept(resultContainer, input);
		})
				// Then
				.isInstanceOf(NullPointerException.class);
	}

	@DataProvider
	public Object[][] testToImmutableMapRejectNullEarlyDataProvider() throws Exception {
		return new Object[][] {
				// null element
				{ null, (Function<String, Integer>) String::length, (Function<String, String>) String::toUpperCase },
				{ "null key", (Function<String, Integer>) (a -> null), (Function<String, String>) String::toUpperCase },
				{ "null value", (Function<String, Integer>) String::length, (Function<String, String>) (a -> null) },
		};
	}

	@Test
	public void testToImmutableMapRejectDuplicates() {
		// Given
		Collection<Integer> collection = asList(1, 2, 3);
		// When
		Assertions.assertThatThrownBy(() -> {
			collection.stream()
					.collect(GuavaCollectors.toImmutableMap(
							// Produces same key for inputs 1, 3
							i -> i % 2,
							Function.identity()));
		})
				// Then
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageStartingWith("Multiple entries with same key");
	}

	private List<Long> newRandomList(int size) {
		Random r = ThreadLocalRandom.current();
		List<Long> inputList = LongStream.range(0, size)
				.mapToObj(x -> r.nextLong())
				.collect(Collectors.toList());
		return inputList;
	}

	private static Function<?, ?> throwingFunction() {
		return (Function<?, ?>) a -> {
			throw new IllegalStateException("should not be called");
		};
	}
}
