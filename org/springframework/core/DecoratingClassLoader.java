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

package org.springframework.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.UsesJava7;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Base class for decorating ClassLoaders such as {@link OverridingClassLoader}
 * and {@link org.springframework.instrument.classloading.ShadowingClassLoader},
 * providing common handling of excluded packages and classes.
 *
 * <p>
 *  用于装饰ClassLoaders的基类,例如{@link OverridingClassLoader}和{@link orgspringframeworkinstrumentclassloadingShadowingClassLoader}
 * ,提供排除的包和类的常见处理。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 2.5.2
 */
@UsesJava7
public abstract class DecoratingClassLoader extends ClassLoader {

	/**
	 * Java 7+ {@code ClassLoader.registerAsParallelCapable()} available?
	 * <p>
	 * Java 7+ {@code ClassLoaderregisterAsParallelCapable()}可用?
	 * 
	 * 
	 * @since 4.1.2
	 */
	protected static final boolean parallelCapableClassLoaderAvailable =
			ClassUtils.hasMethod(ClassLoader.class, "registerAsParallelCapable");

	static {
		if (parallelCapableClassLoaderAvailable) {
			ClassLoader.registerAsParallelCapable();
		}
	}


	private final Set<String> excludedPackages =
			Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(8));

	private final Set<String> excludedClasses =
			Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(8));


	/**
	 * Create a new DecoratingClassLoader with no parent ClassLoader.
	 * <p>
	 *  创建一个没有父级ClassLoader的新的DecoratingClassLoader
	 * 
	 */
	public DecoratingClassLoader() {
	}

	/**
	 * Create a new DecoratingClassLoader using the given parent ClassLoader
	 * for delegation.
	 * <p>
	 *  使用给定的父类ClassLoader创建一个新的DecoratingClassLoader进行委派
	 * 
	 */
	public DecoratingClassLoader(ClassLoader parent) {
		super(parent);
	}


	/**
	 * Add a package name to exclude from decoration (e.g. overriding).
	 * <p>Any class whose fully-qualified name starts with the name registered
	 * here will be handled by the parent ClassLoader in the usual fashion.
	 * <p>
	 *  添加要从装饰中排除的包名称(例如,覆盖)<p>任何以完全限定名称以此处注册的名称开头的类将由父级ClassLoader以通常的方式处理
	 * 
	 * 
	 * @param packageName the package name to exclude
	 */
	public void excludePackage(String packageName) {
		Assert.notNull(packageName, "Package name must not be null");
		this.excludedPackages.add(packageName);
	}

	/**
	 * Add a class name to exclude from decoration (e.g. overriding).
	 * <p>Any class name registered here will be handled by the parent
	 * ClassLoader in the usual fashion.
	 * <p>
	 *  添加一个类名称以排除装饰(例如覆盖)<p>这里注册的任何类名将由通常的父类ClassLoader处理
	 * 
	 * 
	 * @param className the class name to exclude
	 */
	public void excludeClass(String className) {
		Assert.notNull(className, "Class name must not be null");
		this.excludedClasses.add(className);
	}

	/**
	 * Determine whether the specified class is excluded from decoration
	 * by this class loader.
	 * <p>The default implementation checks against excluded packages and classes.
	 * <p>
	 *  确定指定的类是否被该类加载器从装饰中排除<p>默认实现检查排除的包和类
	 * 
	 * @param className the class name to check
	 * @return whether the specified class is eligible
	 * @see #excludePackage
	 * @see #excludeClass
	 */
	protected boolean isExcluded(String className) {
		if (this.excludedClasses.contains(className)) {
			return true;
		}
		for (String packageName : this.excludedPackages) {
			if (className.startsWith(packageName)) {
				return true;
			}
		}
		return false;
	}

}
