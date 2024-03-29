/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Standalone XML application context, taking the context definition files
 * from the file system or from URLs, interpreting plain paths as relative
 * file system locations (e.g. "mydir/myfile.txt"). Useful for test harnesses
 * as well as for standalone environments.
 *
 * <p><b>NOTE:</b> Plain paths will always be interpreted as relative
 * to the current VM working directory, even if they start with a slash.
 * (This is consistent with the semantics in a Servlet container.)
 * <b>Use an explicit "file:" prefix to enforce an absolute file path.</b>
 *
 * <p>The config location defaults can be overridden via {@link #getConfigLocations},
 * Config locations can either denote concrete files like "/myfiles/context.xml"
 * or Ant-style patterns like "/myfiles/*-context.xml" (see the
 * {@link org.springframework.util.AntPathMatcher} javadoc for pattern details).
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in earlier loaded files. This can be leveraged to
 * deliberately override certain bean definitions via an extra XML file.
 *
 * <p><b>This is a simple, one-stop shop convenience ApplicationContext.
 * Consider using the {@link GenericApplicationContext} class in combination
 * with an {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}
 * for more flexible context setup.</b>
 *
 * <p>
 * 独立的XML应用程序环境,从文件系统或URL中获取上下文定义文件,将普通路径解释为相对文件系统位置(例如"mydir / myfiletxt")对于测试用例以及独立环境很有用
 * 
 *  <p> <b>注意：</b>平滑路径将始终被解释为相对于当前VM工作目录,即使它们以斜杠开头(这与Servlet容器中的语义一致)<b>使用一个明确的"file："前缀来强制绝对文件路径</b>
 * 
 * <p>配置位置默认值可以通过{@link #getConfigLocations}重写,配置位置可以表示像"/ myfiles / contextxml"这样的具体文件或Ant样式的模式,如"/ myf
 * iles / *  -  contextxml"(参见{ @link orgspringframeworkutilAntPathMatcher}模式详细信息的javadoc)。
 * 
 *  注意：在多个配置位置的情况下,稍后的bean定义将覆盖在早期加载的文件中定义的定义。可以利用此方法通过额外的XML文件故意覆盖某些bean定义
 * 
 *  <p> <b>这是一个简单的一站式便利ApplicationContext考虑使用{@link GenericApplicationContext}类与{@link orgspringframeworkbeansfactoryxmlXmlBeanDefinitionReader}
 * 结合更灵活的上下文设置</b>。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getResource
 * @see #getResourceByPath
 * @see GenericApplicationContext
 */
public class FileSystemXmlApplicationContext extends AbstractXmlApplicationContext {

	/**
	 * Create a new FileSystemXmlApplicationContext for bean-style configuration.
	 * <p>
	 * 为bean样式配置创建一个新的FileSystemXmlApplicationContext
	 * 
	 * 
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public FileSystemXmlApplicationContext() {
	}

	/**
	 * Create a new FileSystemXmlApplicationContext for bean-style configuration.
	 * <p>
	 *  为bean样式配置创建一个新的FileSystemXmlApplicationContext
	 * 
	 * 
	 * @param parent the parent context
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public FileSystemXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}

	/**
	 * Create a new FileSystemXmlApplicationContext, loading the definitions
	 * from the given XML file and automatically refreshing the context.
	 * <p>
	 *  创建一个新的FileSystemXmlApplicationContext,从给定的XML文件加载定义并自动刷新上下文
	 * 
	 * 
	 * @param configLocation file path
	 * @throws BeansException if context creation failed
	 */
	public FileSystemXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}

	/**
	 * Create a new FileSystemXmlApplicationContext, loading the definitions
	 * from the given XML files and automatically refreshing the context.
	 * <p>
	 *  创建一个新的FileSystemXmlApplicationContext,从给定的XML文件加载定义并自动刷新上下文
	 * 
	 * 
	 * @param configLocations array of file paths
	 * @throws BeansException if context creation failed
	 */
	public FileSystemXmlApplicationContext(String... configLocations) throws BeansException {
		this(configLocations, true, null);
	}

	/**
	 * Create a new FileSystemXmlApplicationContext with the given parent,
	 * loading the definitions from the given XML files and automatically
	 * refreshing the context.
	 * <p>
	 *  与给定的父类创建一个新的FileSystemXmlApplicationContext,从给定的XML文件加载定义,并自动刷新上下文
	 * 
	 * 
	 * @param configLocations array of file paths
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 */
	public FileSystemXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
		this(configLocations, true, parent);
	}

	/**
	 * Create a new FileSystemXmlApplicationContext, loading the definitions
	 * from the given XML files.
	 * <p>
	 *  创建一个新的FileSystemXmlApplicationContext,从给定的XML文件加载定义
	 * 
	 * 
	 * @param configLocations array of file paths
	 * @param refresh whether to automatically refresh the context,
	 * loading all bean definitions and creating all singletons.
	 * Alternatively, call refresh manually after further configuring the context.
	 * @throws BeansException if context creation failed
	 * @see #refresh()
	 */
	public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		this(configLocations, refresh, null);
	}

	/**
	 * Create a new FileSystemXmlApplicationContext with the given parent,
	 * loading the definitions from the given XML files.
	 * <p>
	 * 与给定的父对象创建一个新的FileSystemXmlApplicationContext,从给定的XML文件中加载定义
	 * 
	 * 
	 * @param configLocations array of file paths
	 * @param refresh whether to automatically refresh the context,
	 * loading all bean definitions and creating all singletons.
	 * Alternatively, call refresh manually after further configuring the context.
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 * @see #refresh()
	 */
	public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
			throws BeansException {

		super(parent);
		setConfigLocations(configLocations);
		if (refresh) {
			refresh();
		}
	}


	/**
	 * Resolve resource paths as file system paths.
	 * <p>Note: Even if a given path starts with a slash, it will get
	 * interpreted as relative to the current VM working directory.
	 * This is consistent with the semantics in a Servlet container.
	 * <p>
	 *  解决资源路径作为文件系统路径<p>注意：即使给定的路径以斜杠开头,它将被解释为相对于当前VM工作目录这与Servlet容器中的语义一致
	 * 
	 * @param path path to the resource
	 * @return Resource handle
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#getResourceByPath
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		if (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemResource(path);
	}

}
