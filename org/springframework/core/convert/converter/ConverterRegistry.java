/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

/**
 * For registering converters with a type conversion system.
 *
 * <p>
 *  用于注册具有类型转换系统的转换器
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface ConverterRegistry {

	/**
	 * Add a plain converter to this registry.
	 * The convertible source/target type pair is derived from the Converter's parameterized types.
	 * <p>
	 *  将简单的转换器添加到此注册表可转换源/目标类型对派生自转换器的参数化类型
	 * 
	 * 
	 * @throws IllegalArgumentException if the parameterized types could not be resolved
	 */
	void addConverter(Converter<?, ?> converter);

	/**
	 * Add a plain converter to this registry.
	 * The convertible source/target type pair is specified explicitly.
	 * <p>Allows for a Converter to be reused for multiple distinct pairs without
	 * having to create a Converter class for each pair.
	 * <p>
	 * 将一个简单的转换器添加到此注册表明确指定可转换源/目标类型对<p>允许将转换器重用于多个不同对,而无需为每对创建一个Converter类
	 * 
	 * 
	 * @since 3.1
	 */
	<S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter);

	/**
	 * Add a generic converter to this registry.
	 * <p>
	 *  将通用转换器添加到此注册表
	 * 
	 */
	void addConverter(GenericConverter converter);

	/**
	 * Add a ranged converter factory to this registry.
	 * The convertible source/target type pair is derived from the ConverterFactory's parameterized types.
	 * <p>
	 *  将一个远程转换器工厂添加到此注册表可转换源/目标类型对派生自ConverterFactory的参数化类型
	 * 
	 * 
	 * @throws IllegalArgumentException if the parameterized types could not be resolved.
	 */
	void addConverterFactory(ConverterFactory<?, ?> converterFactory);

	/**
	 * Remove any converters from sourceType to targetType.
	 * <p>
	 *  将所有转换器从sourceType移除到targetType
	 * 
	 * @param sourceType the source type
	 * @param targetType the target type
	 */
	void removeConvertible(Class<?> sourceType, Class<?> targetType);

}
