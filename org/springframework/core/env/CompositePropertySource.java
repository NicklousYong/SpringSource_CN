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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

/**
 * Composite {@link PropertySource} implementation that iterates over a set of
 * {@link PropertySource} instances. Necessary in cases where multiple property sources
 * share the same name, e.g. when multiple values are supplied to {@code @PropertySource}.
 *
 * <p>As of Spring 4.1.2, this class extends {@link EnumerablePropertySource} instead
 * of plain {@link PropertySource}, exposing {@link #getPropertyNames()} based on the
 * accumulated property names from all contained sources (as far as possible).
 *
 * <p>
 * 复合{@link PropertySource}实现,迭代一组{@link PropertySource}实例在多个属性源共享相同名称的情况下是必需的,例如,如果将多个值提供给{@code @PropertySource}
 * 。
 * 
 *  <p>从Spring 412开始,该类扩展了{@link EnumerablePropertySource}而不是纯{@link PropertySource},根据所有包含的来源(尽可能地)累积的属
 * 性名称展开{@link #getPropertyNames()})。
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 3.1.1
 */
public class CompositePropertySource extends EnumerablePropertySource<Object> {

	private final Set<PropertySource<?>> propertySources = new LinkedHashSet<PropertySource<?>>();


	/**
	 * Create a new {@code CompositePropertySource}.
	 * <p>
	 *  创建一个新的{@code CompositePropertySource}
	 * 
	 * 
	 * @param name the name of the property source
	 */
	public CompositePropertySource(String name) {
		super(name);
	}


	@Override
	public Object getProperty(String name) {
		for (PropertySource<?> propertySource : this.propertySources) {
			Object candidate = propertySource.getProperty(name);
			if (candidate != null) {
				return candidate;
			}
		}
		return null;
	}

	@Override
	public boolean containsProperty(String name) {
		for (PropertySource<?> propertySource : this.propertySources) {
			if (propertySource.containsProperty(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] getPropertyNames() {
		Set<String> names = new LinkedHashSet<String>();
		for (PropertySource<?> propertySource : this.propertySources) {
			if (!(propertySource instanceof EnumerablePropertySource)) {
				throw new IllegalStateException(
						"Failed to enumerate property names due to non-enumerable property source: " + propertySource);
			}
			names.addAll(Arrays.asList(((EnumerablePropertySource<?>) propertySource).getPropertyNames()));
		}
		return StringUtils.toStringArray(names);
	}


	/**
	 * Add the given {@link PropertySource} to the end of the chain.
	 * <p>
	 *  将给定的{@link PropertySource}添加到链的末尾
	 * 
	 * 
	 * @param propertySource the PropertySource to add
	 */
	public void addPropertySource(PropertySource<?> propertySource) {
		this.propertySources.add(propertySource);
	}

	/**
	 * Add the given {@link PropertySource} to the start of the chain.
	 * <p>
	 *  将给定的{@link PropertySource}添加到链的开头
	 * 
	 * 
	 * @param propertySource the PropertySource to add
	 * @since 4.1
	 */
	public void addFirstPropertySource(PropertySource<?> propertySource) {
		List<PropertySource<?>> existing = new ArrayList<PropertySource<?>>(this.propertySources);
		this.propertySources.clear();
		this.propertySources.add(propertySource);
		this.propertySources.addAll(existing);
	}

	/**
	 * Return all property sources that this composite source holds.
	 * <p>
	 *  返回此复合源所拥有的所有属性源
	 * 
	 * @since 4.1.1
	 */
	public Collection<PropertySource<?>> getPropertySources() {
		return this.propertySources;
	}


	@Override
	public String toString() {
		return String.format("%s [name='%s', propertySources=%s]",
				getClass().getSimpleName(), this.name, this.propertySources);
	}

}
