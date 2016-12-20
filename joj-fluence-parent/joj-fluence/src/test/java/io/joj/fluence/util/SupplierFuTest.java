package io.joj.fluence.util;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.testng.Assert.assertEquals;

import java.util.function.Supplier;

import org.testng.annotations.Test;

/**
 * @author findepi
 * @since Oct 27, 2016
 */
public class SupplierFuTest {

	@Test
	public void testMemoizeMemoizes() {
		// Given
		@SuppressWarnings("unchecked")
		Supplier<String> delegate = mock(Supplier.class);
		when(delegate.get()).thenReturn("a.");

		// When
		Supplier<String> memoizing = SupplierFluence.memoize(delegate);

		// Then
		assertEquals(memoizing.get(), "a.", "memoizing returned wrong value");
		assertEquals(memoizing.get(), "a.", "memoizing returned wrong value");

		verify(delegate, times(1)).get();
	}

	@Test
	public void testDontMemoizeNull() {
		// Given
		@SuppressWarnings("unchecked")
		Supplier<String> delegate = mock(Supplier.class);
		when(delegate.get())
				.thenReturn(null)
				.thenReturn("b");

		// When
		Supplier<String> memoizing = SupplierFluence.memoize(delegate);

		// Then
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(memoizing::get)
				.withMessage("delegate supplier returned null value");

		assertEquals(memoizing.get(), "b", "null value should not be memoized and get() should call delegate again");
		assertEquals(memoizing.get(), "b", "memoizing returned wrong value");

		verify(delegate, times(2)).get();
	}
}
