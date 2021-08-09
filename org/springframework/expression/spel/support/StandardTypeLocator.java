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

package org.springframework.expression.spel.support;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.util.ClassUtils;

/**
 * A simple implementation of {@link TypeLocator} that uses the context ClassLoader
 * (or any ClassLoader set upon it). It supports 'well-known' packages: So if a
 * type cannot be found, it will try the registered imports to locate it.
 *
 * <p>
 * 使用上下文ClassLoader(或任何设置在其上的任何ClassLoader)的{@link TypeLocator}的简单实现它支持"知名"软件包：因此,如果找不到类型,它将尝试注册的导入来定位它。
 * 
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class StandardTypeLocator implements TypeLocator {

	private final ClassLoader classLoader;

	private final List<String> knownPackagePrefixes = new LinkedList<String>();


	/**
	 * Create a StandardTypeLocator for the default ClassLoader
	 * (typically, the thread context ClassLoader).
	 * <p>
	 *  为默认的ClassLoader创建一个StandardTypeLocator(通常是线程上下文ClassLoader)
	 * 
	 */
	public StandardTypeLocator() {
		this(ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a StandardTypeLocator for the given ClassLoader.
	 * <p>
	 *  为给定的ClassLoader创建一个StandardTypeLocator
	 * 
	 * 
	 * @param classLoader the ClassLoader to delegate to
	 */
	public StandardTypeLocator(ClassLoader classLoader) {
		this.classLoader = classLoader;
		// Similar to when writing regular Java code, it only knows about java.lang by default
		registerImport("java.lang");
	}


	/**
	 * Register a new import prefix that will be used when searching for unqualified types.
	 * Expected format is something like "java.lang".
	 * <p>
	 *  注册一个新的导入前缀,将在搜索不合格的类型时使用预期的格式是像"javalang"
	 * 
	 * 
	 * @param prefix the prefix to register
	 */
	public void registerImport(String prefix) {
		this.knownPackagePrefixes.add(prefix);
	}

	/**
	 * Remove that specified prefix from this locator's list of imports.
	 * <p>
	 *  从该定位器的导入列表中删除该指定的前缀
	 * 
	 * 
	 * @param prefix the prefix to remove
	 */
	public void removeImport(String prefix) {
		this.knownPackagePrefixes.remove(prefix);
	}

	/**
	 * Return a list of all the import prefixes registered with this StandardTypeLocator.
	 * <p>
	 *  返回使用此StandardTypeLocator注册的所有导入前缀的列表
	 * 
	 * 
	 * @return a list of registered import prefixes
	 */
	public List<String> getImportPrefixes() {
		return Collections.unmodifiableList(this.knownPackagePrefixes);
	}


	/**
	 * Find a (possibly unqualified) type reference - first using the type name as-is,
	 * then trying any registered prefixes if the type name cannot be found.
	 * <p>
	 * 查找(可能不合格)的类型引用 - 首先使用类型名称,然后尝试任何已注册的前缀,如果找不到类型名称
	 * 
	 * @param typeName the type to locate
	 * @return the class object for the type
	 * @throws EvaluationException if the type cannot be found
	 */
	@Override
	public Class<?> findType(String typeName) throws EvaluationException {
		String nameToLookup = typeName;
		try {
			return ClassUtils.forName(nameToLookup, this.classLoader);
		}
		catch (ClassNotFoundException ey) {
			// try any registered prefixes before giving up
		}
		for (String prefix : this.knownPackagePrefixes) {
			try {
				nameToLookup = prefix + "." + typeName;
				return ClassUtils.forName(nameToLookup, this.classLoader);
			}
			catch (ClassNotFoundException ex) {
				// might be a different prefix
			}
		}
		throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
	}

}
