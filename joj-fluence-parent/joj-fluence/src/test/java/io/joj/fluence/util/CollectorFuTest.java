package io.joj.fluence.util;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.testng.Assert.assertEquals;

/**
 * @author findepi
 * @since 2016-12-20
 */
public class CollectorFuTest {

	@Test
	public void testIndexingUniquelyByOnEmpty() {
		Map<Integer, String> result = Stream.of(new String[0])
				.collect(CollectorFu.indexingUniquelyBy(String::length));
		assertEquals(result, Collections.emptyMap(), "result");
	}

	@Test
	public void testIndexingUniquelyBy() {
		Map<Integer, String> result = Stream.of("a", "bc", "def")
				.collect(CollectorFu.indexingUniquelyBy(String::length));
		Map<Integer, String> expected = new HashMap<>();
		expected.put(1, "a");
		expected.put(2, "bc");
		expected.put(3, "def");
		assertEquals(result, expected, "result");
	}

	@Test
	public void testIndexingUniquelyByRejectsDuplicates() {
		Stream<String> stream = Stream.of("a", "bc", "def", "gh");
		Collector<String, ?, Map<Integer, String>> collector = CollectorFu.indexingUniquelyBy(String::length);

		Assertions
				.assertThatThrownBy(() -> stream.collect(collector))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Duplicate key: 2");
	}

	@Test
	public void testIndexingBy() {
		Map<Integer, List<String>> result = Stream.of("a", "bc", "de")
				.collect(CollectorFu.indexingBy(String::length));
		Map<Integer, List<String>> expected = new HashMap<>();
		expected.put(1, singletonList("a"));
		expected.put(2, asList("bc", "de"));
		assertEquals(result, expected, "result");
	}
}
