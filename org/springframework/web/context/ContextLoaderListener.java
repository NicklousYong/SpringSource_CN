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

package org.springframework.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Bootstrap listener to start up and shut down Spring's root {@link WebApplicationContext}.
 * Simply delegates to {@link ContextLoader} as well as to {@link ContextCleanupListener}.
 *
 * <p>This listener should be registered after {@link org.springframework.web.util.Log4jConfigListener}
 * in {@code web.xml}, if the latter is used.
 *
 * <p>As of Spring 3.1, {@code ContextLoaderListener} supports injecting the root web
 * application context via the {@link #ContextLoaderListener(WebApplicationContext)}
 * constructor, allowing for programmatic configuration in Servlet 3.0+ environments.
 * See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
 *
 * <p>
 *  Bootstrap侦听器启动和关闭Spring的根{@link WebApplicationContext}只需委托{@link ContextLoader}以及{@link ContextCleanupListener}
 * 。
 * 
 * <p>如果使用后者被使用,该侦听器应该在{@code webxml}中的{@link orgspringframeworkwebutilLog4jConfigListener}后注册
 * 
 *  <p>截至Spring 31,{@code ContextLoaderListener}支持通过{@link #ContextLoaderListener(WebApplicationContext)}
 * 构造函数注入根Web应用程序上下文,允许在Servlet 30+环境中进行编程配置有关使用情况,请参阅{@link orgspringframeworkwebWebApplicationInitializer}
 * 例子。
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 17.02.2003
 * @see #setContextInitializers
 * @see org.springframework.web.WebApplicationInitializer
 * @see org.springframework.web.util.Log4jConfigListener
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

	/**
	 * Create a new {@code ContextLoaderListener} that will create a web application
	 * context based on the "contextClass" and "contextConfigLocation" servlet
	 * context-params. See {@link ContextLoader} superclass documentation for details on
	 * default values for each.
	 * <p>This constructor is typically used when declaring {@code ContextLoaderListener}
	 * as a {@code <listener>} within {@code web.xml}, where a no-arg constructor is
	 * required.
	 * <p>The created application context will be registered into the ServletContext under
	 * the attribute name {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * and the Spring application context will be closed when the {@link #contextDestroyed}
	 * lifecycle method is invoked on this listener.
	 * <p>
	 * 创建一个新的{@code ContextLoaderListener},它将基于"contextClass"和"contextConfigLocation"servlet context-params创
	 * 建一个Web应用程序上下文。
	 * 有关每个<p>的默认值的详细信息,请参阅{@link ContextLoader}超类文档。
	 * 在{@code webxml}中声明{@code ContextLoaderListener}作为{@code <listener>}时,通常使用它,其中需要无参数构造函数<p>创建的应用程序上下文将被
	 * 注册到属性名称下的ServletContext中当在此侦听器上调用{@link #contextDestroyed}生命周期方法时,{@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * 和Spring应用程序上下文将被关闭。
	 * 有关每个<p>的默认值的详细信息,请参阅{@link ContextLoader}超类文档。
	 * 
	 * 
	 * @see ContextLoader
	 * @see #ContextLoaderListener(WebApplicationContext)
	 * @see #contextInitialized(ServletContextEvent)
	 * @see #contextDestroyed(ServletContextEvent)
	 */
	public ContextLoaderListener() {
	}

	/**
	 * Create a new {@code ContextLoaderListener} with the given application context. This
	 * constructor is useful in Servlet 3.0+ environments where instance-based
	 * registration of listeners is possible through the {@link javax.servlet.ServletContext#addListener}
	 * API.
	 * <p>The context may or may not yet be {@linkplain
	 * org.springframework.context.ConfigurableApplicationContext#refresh() refreshed}. If it
	 * (a) is an implementation of {@link ConfigurableWebApplicationContext} and
	 * (b) has <strong>not</strong> already been refreshed (the recommended approach),
	 * then the following will occur:
	 * <ul>
	 * <li>If the given context has not already been assigned an {@linkplain
	 * org.springframework.context.ConfigurableApplicationContext#setId id}, one will be assigned to it</li>
	 * <li>{@code ServletContext} and {@code ServletConfig} objects will be delegated to
	 * the application context</li>
	 * <li>{@link #customizeContext} will be called</li>
	 * <li>Any {@link org.springframework.context.ApplicationContextInitializer ApplicationContextInitializer}s
	 * specified through the "contextInitializerClasses" init-param will be applied.</li>
	 * <li>{@link org.springframework.context.ConfigurableApplicationContext#refresh refresh()} will be called</li>
	 * </ul>
	 * If the context has already been refreshed or does not implement
	 * {@code ConfigurableWebApplicationContext}, none of the above will occur under the
	 * assumption that the user has performed these actions (or not) per his or her
	 * specific needs.
	 * <p>See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
	 * <p>In any case, the given application context will be registered into the
	 * ServletContext under the attribute name {@link
	 * WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE} and the Spring
	 * application context will be closed when the {@link #contextDestroyed} lifecycle
	 * method is invoked on this listener.
	 * <p>
	 * 使用给定的应用程序上下文创建一个新的{@code ContextLoaderListener}这个构造函数在Servlet 30+环境中很有用,通过{@link javaxservletServletContext#addListener}
	 *  API可以实现基于实例的监听器注册<p>上下文可能或不可能{@linkplain orgspringframeworkcontextConfigurableApplicationContext#refresh()刷新}
	 * 如果(a)是{@link ConfigurableWebApplicationContext}和(b)已经被刷新(推荐的方法)的实现,那么将会发生以下情况：。
	 * <ul>
	 * <li>如果给定的上下文尚未被分配一个{@linkplain orgspringframeworkcontextConfigurableApplicationContext#setId id},那么将分
	 * 配一个上下文</li> <li> {@ code ServletContext},并且{@code ServletConfig}对象将被委派应用程序上下文</li> <li> {@ link #customizeContext}
	 * 将被调用</li> <li>将通过"contextInitializerClasses"init-param指定的任何{@link orgspringframeworkcontextApplicationContextInitializer ApplicationContextInitializer}
	 * 将被应用</li > <li> {@ link orgspringframeworkcontextConfigurableApplicationContext#refresh refresh()}将被调
	 * 用</li>。
	 * </ul>
	 * 
	 * @param context the application context to manage
	 * @see #contextInitialized(ServletContextEvent)
	 * @see #contextDestroyed(ServletContextEvent)
	 */
	public ContextLoaderListener(WebApplicationContext context) {
		super(context);
	}


	/**
	 * Initialize the root web application context.
	 * <p>
	 * 如果上下文已经被刷新或者没有实现{@code ConfigurableWebApplicationContext},那么在假设用户根据他或她的具体需求执行这些操作(或不)的情况下,不会出现上述情况<p>
	 * 请参见{@link orgspringframeworkwebWebApplicationInitializer}用于使用示例<p>在任何情况下,给定的应用程序上下文将被注册到属性名为{@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * 的ServletContext中,并且当{@link #contextDestroyed}生命周期方法在此侦听器上被调用。
	 * 
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		initWebApplicationContext(event.getServletContext());
	}


	/**
	 * Close the root web application context.
	 * <p>
	 *  初始化根Web应用程序上下文
	 * 
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		closeWebApplicationContext(event.getServletContext());
		ContextCleanupListener.cleanupAttributes(event.getServletContext());
	}

}
