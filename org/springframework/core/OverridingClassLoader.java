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

import java.io.IOException;
import java.io.InputStream;

import org.springframework.lang.UsesJava7;
import org.springframework.util.FileCopyUtils;

/**
 * {@code ClassLoader} that does <i>not</i> always delegate to the parent loader
 * as normal class loaders do. This enables, for example, instrumentation to be
 * forced in the overriding ClassLoader, or a "throwaway" class loading behavior
 * where selected application classes are temporarily loaded in the overriding
 * {@code ClassLoader} for introspection purposes before eventually loading an
 * instrumented version of the class in the given parent {@code ClassLoader}.
 *
 * <p>
 * <i>不</i>总是以普通类加载器的形式委托给父加载器。
 * 例如,可以在首选的ClassLoader中强制使用工具,或者是"一次性"的类加载行为,选定的应用程序类暂时加载到覆盖的{@code ClassLoader}中,用于内省目的,然后才能最终在给定父级{@code ClassLoader}
 * 中加载该类的检测版本。
 * <i>不</i>总是以普通类加载器的形式委托给父加载器。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0.1
 */
@UsesJava7
public class OverridingClassLoader extends DecoratingClassLoader {

	/** Packages that are excluded by default */
	public static final String[] DEFAULT_EXCLUDED_PACKAGES = new String[]
			{"java.", "javax.", "sun.", "oracle.", "javassist.", "org.aspectj.", "net.sf.cglib."};

	private static final String CLASS_FILE_SUFFIX = ".class";

	static {
		if (parallelCapableClassLoaderAvailable) {
			ClassLoader.registerAsParallelCapable();
		}
	}


	private final ClassLoader overrideDelegate;


	/**
	 * Create a new OverridingClassLoader for the given ClassLoader.
	 * <p>
	 *  为给定的ClassLoader创建一个新的OverridingClassLoader
	 * 
	 * 
	 * @param parent the ClassLoader to build an overriding ClassLoader for
	 */
	public OverridingClassLoader(ClassLoader parent) {
		this(parent, null);
	}

	/**
	 * Create a new OverridingClassLoader for the given ClassLoader.
	 * <p>
	 *  为给定的ClassLoader创建一个新的OverridingClassLoader
	 * 
	 * 
	 * @param parent the ClassLoader to build an overriding ClassLoader for
	 * @param overrideDelegate the ClassLoader to delegate to for overriding
	 * @since 4.3
	 */
	public OverridingClassLoader(ClassLoader parent, ClassLoader overrideDelegate) {
		super(parent);
		this.overrideDelegate = overrideDelegate;
		for (String packageName : DEFAULT_EXCLUDED_PACKAGES) {
			excludePackage(packageName);
		}
	}


	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (this.overrideDelegate != null && isEligibleForOverriding(name)) {
			return this.overrideDelegate.loadClass(name);
		}
		return super.loadClass(name);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (isEligibleForOverriding(name)) {
			Class<?> result = loadClassForOverriding(name);
			if (result != null) {
				if (resolve) {
					resolveClass(result);
				}
				return result;
			}
		}
		return super.loadClass(name, resolve);
	}

	/**
	 * Determine whether the specified class is eligible for overriding
	 * by this class loader.
	 * <p>
	 *  确定指定的类是否有资格被此类加载器覆盖
	 * 
	 * 
	 * @param className the class name to check
	 * @return whether the specified class is eligible
	 * @see #isExcluded
	 */
	protected boolean isEligibleForOverriding(String className) {
		return !isExcluded(className);
	}

	/**
	 * Load the specified class for overriding purposes in this ClassLoader.
	 * <p>The default implementation delegates to {@link #findLoadedClass},
	 * {@link #loadBytesForClass} and {@link #defineClass}.
	 * <p>
	 * 在此ClassLoader中加载指定的类用于重写目的<p>默认实现将委托给{@link #findLoadedClass},{@link #loadBytesForClass}和{@link #defineClass}
	 * 。
	 * 
	 * 
	 * @param name the name of the class
	 * @return the Class object, or {@code null} if no class defined for that name
	 * @throws ClassNotFoundException if the class for the given name couldn't be loaded
	 */
	protected Class<?> loadClassForOverriding(String name) throws ClassNotFoundException {
		Class<?> result = findLoadedClass(name);
		if (result == null) {
			byte[] bytes = loadBytesForClass(name);
			if (bytes != null) {
				result = defineClass(name, bytes, 0, bytes.length);
			}
		}
		return result;
	}

	/**
	 * Load the defining bytes for the given class,
	 * to be turned into a Class object through a {@link #defineClass} call.
	 * <p>The default implementation delegates to {@link #openStreamForClass}
	 * and {@link #transformIfNecessary}.
	 * <p>
	 *  加载给定类的定义字节,通过{@link #defineClass}调用将其转换为Class对象<p>默认实现将委托给{@link #openStreamForClass}和{@link #transformIfNecessary}
	 * 。
	 * 
	 * 
	 * @param name the name of the class
	 * @return the byte content (with transformers already applied),
	 * or {@code null} if no class defined for that name
	 * @throws ClassNotFoundException if the class for the given name couldn't be loaded
	 */
	protected byte[] loadBytesForClass(String name) throws ClassNotFoundException {
		InputStream is = openStreamForClass(name);
		if (is == null) {
			return null;
		}
		try {
			// Load the raw bytes.
			byte[] bytes = FileCopyUtils.copyToByteArray(is);
			// Transform if necessary and use the potentially transformed bytes.
			return transformIfNecessary(name, bytes);
		}
		catch (IOException ex) {
			throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
		}
	}

	/**
	 * Open an InputStream for the specified class.
	 * <p>The default implementation loads a standard class file through
	 * the parent ClassLoader's {@code getResourceAsStream} method.
	 * <p>
	 *  为指定的类打开InputStream <p>默认实现通过父ClassLoader的{@code getResourceAsStream}方法加载标准类文件
	 * 
	 * 
	 * @param name the name of the class
	 * @return the InputStream containing the byte code for the specified class
	 */
	protected InputStream openStreamForClass(String name) {
		String internalName = name.replace('.', '/') + CLASS_FILE_SUFFIX;
		return getParent().getResourceAsStream(internalName);
	}


	/**
	 * Transformation hook to be implemented by subclasses.
	 * <p>The default implementation simply returns the given bytes as-is.
	 * <p>
	 *  转换钩子由子类实现<p>默认实现只是以原样返回给定的字节
	 * 
	 * @param name the fully-qualified name of the class being transformed
	 * @param bytes the raw bytes of the class
	 * @return the transformed bytes (never {@code null};
	 * same as the input bytes if the transformation produced no changes)
	 */
	protected byte[] transformIfNecessary(String name, byte[] bytes) {
		return bytes;
	}

}
