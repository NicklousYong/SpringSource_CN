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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link PropertySources} interface.
 * Allows manipulation of contained property sources and provides a constructor
 * for copying an existing {@code PropertySources} instance.
 *
 * <p>Where <em>precedence</em> is mentioned in methods such as {@link #addFirst}
 * and {@link #addLast}, this is with regard to the order in which property sources
 * will be searched when resolving a given property with a {@link PropertyResolver}.
 *
 * <p>
 *  {@link PropertySources}接口的默认实现允许处理包含的属性源,并提供一个构造函数来复制现有的{@code PropertySources}实例
 * 
 * <p>在{@link #addFirst}和{@link #addLast}等方法中提到了<em>优先级</em>,这是关于在解析给定的属性源时搜索属性源的顺序属性与{@link PropertyResolver}
 * 。
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertySourcesPropertyResolver
 */
public class MutablePropertySources implements PropertySources {

	private final Log logger;

	private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<PropertySource<?>>();


	/**
	 * Create a new {@link MutablePropertySources} object.
	 * <p>
	 *  创建一个新的{@link MutablePropertySources}对象
	 * 
	 */
	public MutablePropertySources() {
		this.logger = LogFactory.getLog(getClass());
	}

	/**
	 * Create a new {@code MutablePropertySources} from the given propertySources
	 * object, preserving the original order of contained {@code PropertySource} objects.
	 * <p>
	 *  从给定的propertySources对象创建一个新的{@code MutablePropertySources},保留包含的{@code PropertySource}对象的原始顺序
	 * 
	 */
	public MutablePropertySources(PropertySources propertySources) {
		this();
		for (PropertySource<?> propertySource : propertySources) {
			addLast(propertySource);
		}
	}

	/**
	 * Create a new {@link MutablePropertySources} object and inherit the given logger,
	 * usually from an enclosing {@link Environment}.
	 * <p>
	 *  创建一个新的{@link MutablePropertySources}对象并继承给定的记录器,通常来自封闭的{@link Environment}
	 * 
	 */
	MutablePropertySources(Log logger) {
		this.logger = logger;
	}


	@Override
	public boolean contains(String name) {
		return this.propertySourceList.contains(PropertySource.named(name));
	}

	@Override
	public PropertySource<?> get(String name) {
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		return (index != -1 ? this.propertySourceList.get(index) : null);
	}

	@Override
	public Iterator<PropertySource<?>> iterator() {
		return this.propertySourceList.iterator();
	}

	/**
	 * Add the given property source object with highest precedence.
	 * <p>
	 *  添加具有最高优先级的给定属性源对象
	 * 
	 */
	public void addFirst(PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding [%s] PropertySource with highest search precedence",
					propertySource.getName()));
		}
		removeIfPresent(propertySource);
		this.propertySourceList.add(0, propertySource);
	}

	/**
	 * Add the given property source object with lowest precedence.
	 * <p>
	 *  添加给定的属性源对象的优先级最低
	 * 
	 */
	public void addLast(PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding [%s] PropertySource with lowest search precedence",
					propertySource.getName()));
		}
		removeIfPresent(propertySource);
		this.propertySourceList.add(propertySource);
	}

	/**
	 * Add the given property source object with precedence immediately higher
	 * than the named relative property source.
	 * <p>
	 * 添加给定的属性源对象,其优先级高于命名的相对属性源
	 * 
	 */
	public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding [%s] PropertySource with search precedence immediately higher than [%s]",
					propertySource.getName(), relativePropertySourceName));
		}
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		removeIfPresent(propertySource);
		int index = assertPresentAndGetIndex(relativePropertySourceName);
		addAtIndex(index, propertySource);
	}

	/**
	 * Add the given property source object with precedence immediately lower
	 * than the named relative property source.
	 * <p>
	 *  将给定的属性源对象的优先级立即低于命名的相对属性源
	 * 
	 */
	public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding [%s] PropertySource with search precedence immediately lower than [%s]",
					propertySource.getName(), relativePropertySourceName));
		}
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		removeIfPresent(propertySource);
		int index = assertPresentAndGetIndex(relativePropertySourceName);
		addAtIndex(index + 1, propertySource);
	}

	/**
	 * Return the precedence of the given property source, {@code -1} if not found.
	 * <p>
	 *  返回给定属性源的优先级{@code -1}(如果未找到)
	 * 
	 */
	public int precedenceOf(PropertySource<?> propertySource) {
		return this.propertySourceList.indexOf(propertySource);
	}

	/**
	 * Remove and return the property source with the given name, {@code null} if not found.
	 * <p>
	 *  删除并返回具有给定名称的属性源,如果没有找到,则返回{@code null}
	 * 
	 * 
	 * @param name the name of the property source to find and remove
	 */
	public PropertySource<?> remove(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Removing [%s] PropertySource", name));
		}
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		return (index != -1 ? this.propertySourceList.remove(index) : null);
	}

	/**
	 * Replace the property source with the given name with the given property source object.
	 * <p>
	 *  用给定的属性源对象替换给定名称的属性源
	 * 
	 * 
	 * @param name the name of the property source to find and replace
	 * @param propertySource the replacement property source
	 * @throws IllegalArgumentException if no property source with the given name is present
	 * @see #contains
	 */
	public void replace(String name, PropertySource<?> propertySource) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Replacing [%s] PropertySource with [%s]",
					name, propertySource.getName()));
		}
		int index = assertPresentAndGetIndex(name);
		this.propertySourceList.set(index, propertySource);
	}

	/**
	 * Return the number of {@link PropertySource} objects contained.
	 * <p>
	 *  返回包含的{@link PropertySource}对象的数量
	 * 
	 */
	public int size() {
		return this.propertySourceList.size();
	}

	@Override
	public String toString() {
		String[] names = new String[this.size()];
		for (int i = 0; i < size(); i++) {
			names[i] = this.propertySourceList.get(i).getName();
		}
		return String.format("[%s]", StringUtils.arrayToCommaDelimitedString(names));
	}

	/**
	 * Ensure that the given property source is not being added relative to itself.
	 * <p>
	 *  确保给定的属性源不是相对于自身添加的
	 * 
	 */
	protected void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource) {
		String newPropertySourceName = propertySource.getName();
		if (relativePropertySourceName.equals(newPropertySourceName)) {
			throw new IllegalArgumentException(
					String.format("PropertySource named [%s] cannot be added relative to itself", newPropertySourceName));
		}
	}

	/**
	 * Remove the given property source if it is present.
	 * <p>
	 *  如果存在,请删除给定的属性源
	 * 
	 */
	protected void removeIfPresent(PropertySource<?> propertySource) {
		this.propertySourceList.remove(propertySource);
	}

	/**
	 * Add the given property source at a particular index in the list.
	 * <p>
	 *  在列表中的特定索引处添加给定的属性源
	 * 
	 */
	private void addAtIndex(int index, PropertySource<?> propertySource) {
		removeIfPresent(propertySource);
		this.propertySourceList.add(index, propertySource);
	}

	/**
	 * Assert that the named property source is present and return its index.
	 * <p>
	 * 声明命名的属性源存在并返回其索引
	 * 
	 * @param name the {@linkplain PropertySource#getName() name of the property source}
	 * to find
	 * @throws IllegalArgumentException if the named property source is not present
	 */
	private int assertPresentAndGetIndex(String name) {
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		if (index == -1) {
			throw new IllegalArgumentException(String.format("PropertySource named [%s] does not exist", name));
		}
		return index;
	}

}
