/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.context;

/**
 * Callback interface for initializing a Spring {@link ConfigurableApplicationContext}
 * prior to being {@linkplain ConfigurableApplicationContext#refresh() refreshed}.
 *
 * <p>Typically used within web applications that require some programmatic initialization
 * of the application context. For example, registering property sources or activating
 * profiles against the {@linkplain ConfigurableApplicationContext#getEnvironment()
 * context's environment}. See {@code ContextLoader} and {@code FrameworkServlet} support
 * for declaring a "contextInitializerClasses" context-param and init-param, respectively.
 *
 * <p>{@code ApplicationContextInitializer} processors are encouraged to detect
 * whether Spring's {@link org.springframework.core.Ordered Ordered} interface has been
 * implemented or if the @{@link org.springframework.core.annotation.Order Order}
 * annotation is present and to sort instances accordingly if so prior to invocation.
 *
 * <p>
 *  在{@linkplain ConfigurableApplicationContext#refresh()刷新之前初始化Spring {@link ConfigurableApplicationContext}
 * 的回调接口}。
 * 
 * 通常在Web应用程序中使用,需要对应用程序上下文进行一些编程初始化例如,根据{@linkplain ConfigurableApplicationContext#getEnvironment()上下文的环境注册属性源或激活配置文件}
 * 参见{@code ContextLoader}和{@code FrameworkServlet }支持分别声明"contextInitializerClasses"上下文参数和init-param。
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @see org.springframework.web.context.ContextLoader#customizeContext
 * @see org.springframework.web.context.ContextLoader#CONTEXT_INITIALIZER_CLASSES_PARAM
 * @see org.springframework.web.servlet.FrameworkServlet#setContextInitializerClasses
 * @see org.springframework.web.servlet.FrameworkServlet#applyInitializers
 */
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {

	/**
	 * Initialize the given application context.
	 * <p>
	 *  鼓励{@code ApplicationContextInitializer}处理器检测是否已经实现了Spring的{@link orgspringframeworkcoreOrdered Ordered}
	 * 接口,或者是否存在@ {@ link orgspringframeworkcoreannotationOrder Order}注释,并在调用之前相应地对实例进行排序。
	 * 
	 * 
	 * @param applicationContext the application to configure
	 */
	void initialize(C applicationContext);

}
