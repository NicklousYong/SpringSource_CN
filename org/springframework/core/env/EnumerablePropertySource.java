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

package org.springframework.core.env;

import org.springframework.util.ObjectUtils;

/**
 * A {@link PropertySource} implementation capable of interrogating its
 * underlying source object to enumerate all possible property name/value
 * pairs. Exposes the {@link #getPropertyNames()} method to allow callers
 * to introspect available properties without having to access the underlying
 * source object. This also facilitates a more efficient implementation of
 * {@link #containsProperty(String)}, in that it can call {@link #getPropertyNames()}
 * and iterate through the returned array rather than attempting a call to
 * {@link #getProperty(String)} which may be more expensive. Implementations may
 * consider caching the result of {@link #getPropertyNames()} to fully exploit this
 * performance opportunity.
 *
 * <p>Most framework-provided {@code PropertySource} implementations are enumerable;
 * a counter-example would be {@code JndiPropertySource} where, due to the
 * nature of JNDI it is not possible to determine all possible property names at
 * any given time; rather it is only possible to try to access a property
 * (via {@link #getProperty(String)}) in order to evaluate whether it is present
 * or not.
 *
 * <p>
 * 能够询问其底层源对象以列举所有可能的属性名称/值对的{@link PropertySource}实现暴露了{@link #getPropertyNames()}方法,以允许调用者内省自己的可用属性,而不
 * 必访问底层的源对象。
 * 还有助于更有效地实现{@link #containsProperty(String)},因为它可以调用{@link #getPropertyNames()}并遍历返回的数组,而不是尝试调用{@link #getProperty(String) }
 * 这可能更昂贵实施可能会考虑缓存{@link #getPropertyNames()}的结果,以充分利用此性能机会。
 * 
 * 大多数框架提供的{@code PropertySource}实现是可枚举的;一个反例是{@code JndiPropertySource},由于JNDI的性质,在任何给定时间都不可能确定所有可能的属性名
 * 称;相反,只能尝试访问属性(通过{@link #getProperty(String)}),以评估是否存在。
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public abstract class EnumerablePropertySource<T> extends PropertySource<T> {

	public EnumerablePropertySource(String name, T source) {
		super(name, source);
	}

	protected EnumerablePropertySource(String name) {
		super(name);
	}


	/**
	 * Return whether this {@code PropertySource} contains a property with the given name.
	 * <p>This implementation checks for the presence of the given name within the
	 * {@link #getPropertyNames()} array.
	 * <p>
	 * 
	 * 
	 * @param name the name of the property to find
	 */
	@Override
	public boolean containsProperty(String name) {
		return ObjectUtils.containsElement(getPropertyNames(), name);
	}

	/**
	 * Return the names of all properties contained by the
	 * {@linkplain #getSource() source} object (never {@code null}).
	 * <p>
	 *  返回此{@code PropertySource}是否包含具有给定名称的属性<p>此实现检查{@link #getPropertyNames())数组中给定名称的存在
	 * 
	 */
	public abstract String[] getPropertyNames();

}
