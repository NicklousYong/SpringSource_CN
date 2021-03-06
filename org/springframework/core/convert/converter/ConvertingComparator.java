/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.convert.converter;

import java.util.Comparator;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.util.comparator.ComparableComparator;

/**
 * A {@link Comparator} that converts values before they are compared. The specified
 * {@link Converter} will be used to convert each value before it passed to the underlying
 * {@code Comparator}.
 *
 * <p>
 *  在比较值之前转换值的{@link比较器}指定的{@link转换器}将用于在传递给底层{@code比较器}之前转换每个值
 * 
 * 
 * @author Phillip Webb
 * @since 3.2
 * @param <S> the source type
 * @param <T> the target type
 */
public class ConvertingComparator<S, T> implements Comparator<S> {

	private Comparator<T> comparator;

	private Converter<S, T> converter;


	/**
	 * Create a new {@link ConvertingComparator} instance.
	 * <p>
	 * 创建一个新的{@link ConvertingComparator}实例
	 * 
	 * 
	 * @param converter the converter
	 */
	@SuppressWarnings("unchecked")
	public ConvertingComparator(Converter<S, T> converter) {
		this(ComparableComparator.INSTANCE, converter);
	}

	/**
	 * Create a new {@link ConvertingComparator} instance.
	 * <p>
	 *  创建一个新的{@link ConvertingComparator}实例
	 * 
	 * 
	 * @param comparator the underlying comparator used to compare the converted values
	 * @param converter the converter
	 */
	public ConvertingComparator(Comparator<T> comparator, Converter<S, T> converter) {
		Assert.notNull(comparator, "Comparator must not be null");
		Assert.notNull(converter, "Converter must not be null");
		this.comparator = comparator;
		this.converter = converter;
	}

	/**
	 * Create a new {@link ComparableComparator} instance.
	 * <p>
	 *  创建一个新的{@link ComparableComparator}实例
	 * 
	 * 
	 * @param comparator the underlying comparator
	 * @param conversionService the conversion service
	 * @param targetType the target type
	 */
	public ConvertingComparator(
			Comparator<T> comparator, ConversionService conversionService, Class<? extends T> targetType) {

		this(comparator, new ConversionServiceConverter<S, T>(conversionService, targetType));
	}


	@Override
	public int compare(S o1, S o2) {
		T c1 = this.converter.convert(o1);
		T c2 = this.converter.convert(o2);
		return this.comparator.compare(c1, c2);
	}

	/**
	 * Create a new {@link ConvertingComparator} that compares {@link java.util.Map.Entry
	 * map * entries} based on their {@link java.util.Map.Entry#getKey() keys}.
	 * <p>
	 *  创建一个新的{@link ConvertingComparator},根据{@link javautilMapEntry#getKey()键}将{@link javautilMapEntry map * entries}
	 * 进行比较}。
	 * 
	 * 
	 * @param comparator the underlying comparator used to compare keys
	 * @return a new {@link ConvertingComparator} instance
	 */
	public static <K, V> ConvertingComparator<Map.Entry<K, V>, K> mapEntryKeys(Comparator<K> comparator) {
		return new ConvertingComparator<Map.Entry<K,V>, K>(comparator, new Converter<Map.Entry<K, V>, K>() {
			@Override
			public K convert(Map.Entry<K, V> source) {
				return source.getKey();
			}
		});
	}

	/**
	 * Create a new {@link ConvertingComparator} that compares {@link java.util.Map.Entry
	 * map entries} based on their {@link java.util.Map.Entry#getValue() values}.
	 * <p>
	 *  创建一个新的{@link ConvertingComparator},根据{@link javautilMapEntry#getValue()值}将{@link javautilMapEntry map entries}
	 * 进行比较}。
	 * 
	 * 
	 * @param comparator the underlying comparator used to compare values
	 * @return a new {@link ConvertingComparator} instance
	 */
	public static <K, V> ConvertingComparator<Map.Entry<K, V>, V> mapEntryValues(Comparator<V> comparator) {
		return new ConvertingComparator<Map.Entry<K,V>, V>(comparator, new Converter<Map.Entry<K, V>, V>() {
			@Override
			public V convert(Map.Entry<K, V> source) {
				return source.getValue();
			}
		});
	}


	/**
	 * Adapts a {@link ConversionService} and <tt>targetType</tt> to a {@link Converter}.
	 * <p>
	 */
	private static class ConversionServiceConverter<S, T> implements Converter<S, T> {

		private final ConversionService conversionService;

		private final Class<? extends T> targetType;

		public ConversionServiceConverter(ConversionService conversionService,
			Class<? extends T> targetType) {
			Assert.notNull(conversionService, "ConversionService must not be null");
			Assert.notNull(targetType, "TargetType must not be null");
			this.conversionService = conversionService;
			this.targetType = targetType;
		}

		@Override
		public T convert(S source) {
			return this.conversionService.convert(source, this.targetType);
		}
	}

}
