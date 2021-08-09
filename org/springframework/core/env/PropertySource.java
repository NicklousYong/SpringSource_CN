/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Abstract base class representing a source of name/value property pairs. The underlying
 * {@linkplain #getSource() source object} may be of any type {@code T} that encapsulates
 * properties. Examples include {@link java.util.Properties} objects, {@link java.util.Map}
 * objects, {@code ServletContext} and {@code ServletConfig} objects (for access to init
 * parameters). Explore the {@code PropertySource} type hierarchy to see provided
 * implementations.
 *
 * <p>{@code PropertySource} objects are not typically used in isolation, but rather
 * through a {@link PropertySources} object, which aggregates property sources and in
 * conjunction with a {@link PropertyResolver} implementation that can perform
 * precedence-based searches across the set of {@code PropertySources}.
 *
 * <p>{@code PropertySource} identity is determined not based on the content of
 * encapsulated properties, but rather based on the {@link #getName() name} of the
 * {@code PropertySource} alone. This is useful for manipulating {@code PropertySource}
 * objects when in collection contexts. See operations in {@link MutablePropertySources}
 * as well as the {@link #named(String)} and {@link #toString()} methods for details.
 *
 * <p>Note that when working with @{@link
 * org.springframework.context.annotation.Configuration Configuration} classes that
 * the @{@link org.springframework.context.annotation.PropertySource PropertySource}
 * annotation provides a convenient and declarative way of adding property sources to the
 * enclosing {@code Environment}.
 *
 * <p>
 * 代表名称/值属性对的源的抽象基类底层{@linkplain #getSource()源对象}可以是封装属性的任何类型的{@code T}示例包括{@link javautilProperties}对象,
 * {@link javautilMap }对象,{@code ServletContext}和{@code ServletConfig}对象(用于访问init参数)浏览{@code PropertySource}
 * 类型层次结构以查看提供的实现。
 * 
 *  <p> {@ code PropertySource}对象通常不会孤立地使用,而是通过{@link PropertySources}对象,该对象聚合属性源,并结合使用可以执行基于优先级的搜索的{@link PropertyResolver}
 * 实现该集{@code PropertySources}。
 * 
 * <p> {@ code PropertySource}标识不是基于封装属性的内容,而是基于{@code PropertySource}的{@link #getName()名称}),这对于操纵{@code PropertySource}
 * 对象在收集上下文中查看{@link MutablePropertySources}中的操作以及{@link #named(String)}和{@link #toString()}方法的详细信息。
 * 
 *  <p>请注意,在使用@ {@ link orgspringframeworkcontextannotationConfiguration Configuration}类时,@ {@ link orgspringframeworkcontextannotationPropertySource PropertySource}
 * 注释提供了一种方便和声明性的方式,将附加属性源添加到封闭的{@code Environment}。
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @see PropertySources
 * @see PropertyResolver
 * @see PropertySourcesPropertyResolver
 * @see MutablePropertySources
 * @see org.springframework.context.annotation.PropertySource
 */
public abstract class PropertySource<T> {

	protected final Log logger = LogFactory.getLog(getClass());

	protected final String name;

	protected final T source;


	/**
	 * Create a new {@code PropertySource} with the given name and source object.
	 * <p>
	 * 使用给定的名称和源对象创建一个新的{@code PropertySource}
	 * 
	 */
	public PropertySource(String name, T source) {
		Assert.hasText(name, "Property source name must contain at least one character");
		Assert.notNull(source, "Property source must not be null");
		this.name = name;
		this.source = source;
	}

	/**
	 * Create a new {@code PropertySource} with the given name and with a new
	 * {@code Object} instance as the underlying source.
	 * <p>Often useful in testing scenarios when creating anonymous implementations
	 * that never query an actual source but rather return hard-coded values.
	 * <p>
	 *  创建一个具有给定名称的新{@code PropertySource}和一个新的{@code Object}实例作为基础源<p>在创建匿名实现时,在测试场景中通常很有用,从而不会查询实际源,而是返回硬编
	 * 码值。
	 * 
	 */
	@SuppressWarnings("unchecked")
	public PropertySource(String name) {
		this(name, (T) new Object());
	}


	/**
	 * Return the name of this {@code PropertySource}
	 * <p>
	 *  返回此{@code PropertySource}的名称
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the underlying source object for this {@code PropertySource}.
	 * <p>
	 *  返回此{@code PropertySource}的基础源对象
	 * 
	 */
	public T getSource() {
		return this.source;
	}

	/**
	 * Return whether this {@code PropertySource} contains the given name.
	 * <p>This implementation simply checks for a {@code null} return value
	 * from {@link #getProperty(String)}. Subclasses may wish to implement
	 * a more efficient algorithm if possible.
	 * <p>
	 *  返回此{@code PropertySource}是否包含给定的名称<p>此实现只需从{@link #getProperty(String))检查{@code null}返回值。
	 * 子类可能希望在可能的情况下实现更有效的算法。
	 * 
	 * 
	 * @param name the property name to find
	 */
	public boolean containsProperty(String name) {
		return (getProperty(name) != null);
	}

	/**
	 * Return the value associated with the given name,
	 * or {@code null} if not found.
	 * <p>
	 *  返回与给定名称相关联的值,如果未找到,则返回{@code null}
	 * 
	 * 
	 * @param name the property to find
	 * @see PropertyResolver#getRequiredProperty(String)
	 */
	public abstract Object getProperty(String name);


	/**
	 * This {@code PropertySource} object is equal to the given object if:
	 * <ul>
	 * <li>they are the same instance
	 * <li>the {@code name} properties for both objects are equal
	 * </ul>
	 * <p>No properties other than {@code name} are evaluated.
	 * <p>
	 * 此{@code PropertySource}对象等于给定对象,如果：
	 * <ul>
	 *  <li>它们是相同的实例<li>两个对象的{@code名称}属性相等
	 * </ul>
	 *  <p>不对{@code name}之外的任何属性进行评估
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj || (obj instanceof PropertySource &&
				ObjectUtils.nullSafeEquals(this.name, ((PropertySource<?>) obj).name)));
	}

	/**
	 * Return a hash code derived from the {@code name} property
	 * of this {@code PropertySource} object.
	 * <p>
	 *  返回从{@code PropertySource}对象的{@code name}属性派生的哈希码
	 * 
	 */
	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.name);
	}

	/**
	 * Produce concise output (type and name) if the current log level does not include
	 * debug. If debug is enabled, produce verbose output including the hash code of the
	 * PropertySource instance and every name/value property pair.
	 * <p>This variable verbosity is useful as a property source such as system properties
	 * or environment variables may contain an arbitrary number of property pairs,
	 * potentially leading to difficult to read exception and log messages.
	 * <p>
	 *  产生简明输出(类型和名称),如果如果调试启用当前日志级别不包括调试,产生详细的输出包括PropertySource实例的哈希码和每一个名称/值属性对<p>此变量冗长是作为有用的属性源(如系统属性或环境
	 * 变量)可能包含任意数量的属性对,可能导致难以读取异常和日志消息。
	 * 
	 * 
	 * @see Log#isDebugEnabled()
	 */
	@Override
	public String toString() {
		if (logger.isDebugEnabled()) {
			return String.format("%s@%s [name='%s', properties=%s]",
					getClass().getSimpleName(), System.identityHashCode(this), this.name, this.source);
		}
		else {
			return String.format("%s [name='%s']", getClass().getSimpleName(), this.name);
		}
	}


	/**
	 * Return a {@code PropertySource} implementation intended for collection comparison purposes only.
	 * <p>Primarily for internal use, but given a collection of {@code PropertySource} objects, may be
	 * used as follows:
	 * <pre class="code">
	 * {@code List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
	 * sources.add(new MapPropertySource("sourceA", mapA));
	 * sources.add(new MapPropertySource("sourceB", mapB));
	 * assert sources.contains(PropertySource.named("sourceA"));
	 * assert sources.contains(PropertySource.named("sourceB"));
	 * assert !sources.contains(PropertySource.named("sourceC"));
	 * }</pre>
	 * The returned {@code PropertySource} will throw {@code UnsupportedOperationException}
	 * if any methods other than {@code equals(Object)}, {@code hashCode()}, and {@code toString()}
	 * are called.
	 * <p>
	 * 返回仅用于集合比较的{@code PropertySource}实现<p>主要用于内部使用,但是给出{@code PropertySource}对象的集合可以如下使用：
	 * <pre class="code">
	 *  {@code List <PropertySource <?>> sources = new ArrayList <PropertySource <?>>(); sourcesadd(new MapPropertySource("sourceA",mapA)); sourcesadd(new MapPropertySource("sourceB",mapB)); assert sourcescontains(PropertySourcenamed("sourceA")); assert sourcescontains(PropertySourcenamed("sourceB")); assert！sourcescontains(PropertySourcenamed("sourceC")); }
	 * 如果{@code equals(Object)},{@code hashCode()}和{@code toString()}之外的任何方法都是,返回的{@code PropertySource}将抛出{@code UnsupportedOperationException}
	 * 叫。
	 * 
	 * 
	 * @param name the name of the comparison {@code PropertySource} to be created and returned.
	 */
	public static PropertySource<?> named(String name) {
		return new ComparisonPropertySource(name);
	}


	/**
	 * {@code PropertySource} to be used as a placeholder in cases where an actual
	 * property source cannot be eagerly initialized at application context
	 * creation time.  For example, a {@code ServletContext}-based property source
	 * must wait until the {@code ServletContext} object is available to its enclosing
	 * {@code ApplicationContext}.  In such cases, a stub should be used to hold the
	 * intended default position/order of the property source, then be replaced
	 * during context refresh.
	 * <p>
	 * {@code PropertySource}用作占位符,在实例属性源不能在应用程序上下文创建时刻被初始化的情况下,例如,基于{@code ServletContext}的属性源必须等到{@code ServletContext}
	 * 对象可用于其封闭的{@code ApplicationContext}在这种情况下,应使用存根来保存属性源的预期默认位置/顺序,然后在上下文刷新期间更换。
	 * 
	 * 
	 * @see org.springframework.context.support.AbstractApplicationContext#initPropertySources()
	 * @see org.springframework.web.context.support.StandardServletEnvironment
	 * @see org.springframework.web.context.support.ServletContextPropertySource
	 */
	public static class StubPropertySource extends PropertySource<Object> {

		public StubPropertySource(String name) {
			super(name, new Object());
		}

		/**
		 * Always returns {@code null}.
		 * <p>
		 *  始终返回{@code null}
		 * 
		 */
		@Override
		public String getProperty(String name) {
			return null;
		}
	}


	/**
	/* <p>
	/* 
	 * @see PropertySource#named(String)
	 */
	static class ComparisonPropertySource extends StubPropertySource {

		private static final String USAGE_ERROR =
				"ComparisonPropertySource instances are for use with collection comparison only";

		public ComparisonPropertySource(String name) {
			super(name);
		}

		@Override
		public Object getSource() {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}

		@Override
		public boolean containsProperty(String name) {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}

		@Override
		public String getProperty(String name) {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}

		@Override
		public String toString() {
			return String.format("%s [name='%s']", getClass().getSimpleName(), this.name);
		}
	}

}
