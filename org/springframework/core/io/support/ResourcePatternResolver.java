/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.core.io.support;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Strategy interface for resolving a location pattern (for example,
 * an Ant-style path pattern) into Resource objects.
 *
 * <p>This is an extension to the {@link org.springframework.core.io.ResourceLoader}
 * interface. A passed-in ResourceLoader (for example, an
 * {@link org.springframework.context.ApplicationContext} passed in via
 * {@link org.springframework.context.ResourceLoaderAware} when running in a context)
 * can be checked whether it implements this extended interface too.
 *
 * <p>{@link PathMatchingResourcePatternResolver} is a standalone implementation
 * that is usable outside an ApplicationContext, also used by
 * {@link ResourceArrayPropertyEditor} for populating Resource array bean properties.
 *
 * <p>Can be used with any sort of location pattern (e.g. "/WEB-INF/*-context.xml"):
 * Input patterns have to match the strategy implementation. This interface just
 * specifies the conversion method rather than a specific pattern format.
 *
 * <p>This interface also suggests a new resource prefix "classpath*:" for all
 * matching resources from the class path. Note that the resource location is
 * expected to be a path without placeholders in this case (e.g. "/beans.xml");
 * JAR files or classes directories can contain multiple files of the same name.
 *
 * <p>
 *  用于将位置模式(例如,Ant样式路径模式)解析为资源对象的策略界面
 * 
 * <p>这是{@link orgspringframeworkcoreioResourceLoader}接口的扩展可以检查传入的ResourceLoader(例如,在上下文中运行时通过{@link orgspringframeworkcontextResourceLoaderAware}
 * 传递的{@link orgspringframeworkcontextApplicationContext})是否被实现这个扩展接口也是。
 * 
 *  <p> {@ link PathMatchingResourcePatternResolver}是一个独立的实现,可在ApplicationContext外部使用,也由{@link ResourceArrayPropertyEditor}
 * 用于填充资源阵列bean属性。
 * 
 * <p>可以与任何种类的位置模式一起使用(例如"/ WEB-INF / *  -  contextxml")：输入模式必须与策略实现相匹配此接口只是指定转换方法而不是特定的模式格式
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.ResourceLoader
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourcePatternResolver extends ResourceLoader {

	/**
	 * Pseudo URL prefix for all matching resources from the class path: "classpath*:"
	 * This differs from ResourceLoader's classpath URL prefix in that it
	 * retrieves all matching resources for a given name (e.g. "/beans.xml"),
	 * for example in the root of all deployed JAR files.
	 * <p>
	 *  <p>此接口还为类路径中的所有匹配资源提供了一个新的资源前缀"classpath *："。
	 * 请注意,在这种情况下,资源位置预期是没有占位符的路径(例如"/ beansxml"); JAR文件或类目录可以包含相同名称的多个文件。
	 * 
	 * 
	 * @see org.springframework.core.io.ResourceLoader#CLASSPATH_URL_PREFIX
	 */
	String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

	/**
	 * Resolve the given location pattern into Resource objects.
	 * <p>Overlapping resource entries that point to the same physical
	 * resource should be avoided, as far as possible. The result should
	 * have set semantics.
	 * <p>
	 * 来自类路径的所有匹配资源的伪URL前缀："classpath *："与ResourceLoader的类路径URL前缀不同之处在于它检索给定名称的所有匹配资源(例如"/ beansxml"),例如所有的部
	 * 署了JAR文件。
	 * 
	 * 
	 * @param locationPattern the location pattern to resolve
	 * @return the corresponding Resource objects
	 * @throws IOException in case of I/O errors
	 */
	Resource[] getResources(String locationPattern) throws IOException;

}
