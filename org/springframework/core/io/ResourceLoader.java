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

package org.springframework.core.io;

import org.springframework.util.ResourceUtils;

/**
 * Strategy interface for loading resources (e.. class path or file system
 * resources). An {@link org.springframework.context.ApplicationContext}
 * is required to provide this functionality, plus extended
 * {@link org.springframework.core.io.support.ResourcePatternResolver} support.
 *
 * <p>{@link DefaultResourceLoader} is a standalone implementation that is
 * usable outside an ApplicationContext, also used by {@link ResourceEditor}.
 *
 * <p>Bean properties of type Resource and Resource array can be populated
 * from Strings when running in an ApplicationContext, using the particular
 * context's resource loading strategy.
 *
 * <p>
 * 用于加载资源(e类路径或文件系统资源)的策略界面需要一个{@link orgspringframeworkcontextApplicationContext}来提供此功能,以及扩展的{@link orgspringframeworkcoreiosupportResourcePatternResolver}
 * 支持。
 * 
 *  <p> {@ link DefaultResourceLoader}是一个独立的实现,可以在ApplicationContext之外使用,也由{@link ResourceEditor}使用
 * 
 *  <p>资源和资源数组类型的Bean属性可以在使用特定上下文的资源加载策略在ApplicationContext中运行时从字符串填充
 * 
 * 
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see Resource
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


	/**
	 * Return a Resource handle for the specified resource.
	 * The handle should always be a reusable resource descriptor,
	 * allowing for multiple {@link Resource#getInputStream()} calls.
	 * <p><ul>
	 * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
	 * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
	 * <li>Should support relative file paths, e.g. "WEB-INF/test.dat".
	 * (This will be implementation-specific, typically provided by an
	 * ApplicationContext implementation.)
	 * </ul>
	 * <p>Note that a Resource handle does not imply an existing resource;
	 * you need to invoke {@link Resource#exists} to check for existence.
	 * <p>
	 * 返回指定资源的资源句柄句柄应该始终是可重用的资源描述符,允许多个{@link Resource#getInputStream()}调用<p> <ul> <li>必须支持完全限定的URL,例如"file：
	 *  C：/ testdat"<li>必须支持类路径伪URL,例如"classpath：testdat"<li>应该支持相对文件路径,例如"WEB-INF / testdat"(这将是实现特定的,通常由Ap
	 * plicationContext实现)。
	 * </ul>
	 *  注意资源句柄并不意味着现有资源;您需要调用{@link资源#存在}来检查是否存在
	 * 
	 * @param location the resource location
	 * @return a corresponding Resource handle
	 * @see #CLASSPATH_URL_PREFIX
	 * @see org.springframework.core.io.Resource#exists
	 * @see org.springframework.core.io.Resource#getInputStream
	 */
	Resource getResource(String location);

	/**
	 * Expose the ClassLoader used by this ResourceLoader.
	 * <p>Clients which need to access the ClassLoader directly can do so
	 * in a uniform manner with the ResourceLoader, rather than relying
	 * on the thread context ClassLoader.
	 * <p>
	 * 
	 * 
	 * @return the ClassLoader (only {@code null} if even the system
	 * ClassLoader isn't accessible)
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	ClassLoader getClassLoader();

}
