/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor}
 * implementation that passes the ServletContext to beans that implement
 * the {@link ServletContextAware} interface.
 *
 * <p>Web application contexts will automatically register this with their
 * underlying bean factory. Applications do not use this directly.
 *
 * <p>
 *  {@link orgspringframeworkbeansfactoryconfigBeanPostProcessor}实现将ServletContext传递给实现{@link ServletContextAware}
 * 接口的bean。
 * 
 * <p> Web应用程序上下文将自动将其注册到其基础bean工厂。应用程序不直接使用它
 * 
 * 
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 12.03.2004
 * @see org.springframework.web.context.ServletContextAware
 * @see org.springframework.web.context.support.XmlWebApplicationContext#postProcessBeanFactory
 */
public class ServletContextAwareProcessor implements BeanPostProcessor {

	private ServletContext servletContext;

	private ServletConfig servletConfig;


	/**
	 * Create a new ServletContextAwareProcessor without an initial context or config.
	 * When this constructor is used the {@link #getServletContext()} and/or
	 * {@link #getServletConfig()} methods should be overridden.
	 * <p>
	 *  创建一个没有初始上下文或配置的新ServletContextAwareProcessor当使用此构造函数时,应该覆盖{@link #getServletContext()}和/或{@link #getServletConfig()}
	 * 方法)。
	 * 
	 */
	protected ServletContextAwareProcessor() {
	}

	/**
	 * Create a new ServletContextAwareProcessor for the given context.
	 * <p>
	 *  为给定的上下文创建一个新的ServletContextAwareProcessor
	 * 
	 */
	public ServletContextAwareProcessor(ServletContext servletContext) {
		this(servletContext, null);
	}

	/**
	 * Create a new ServletContextAwareProcessor for the given config.
	 * <p>
	 *  为给定的配置创建一个新的ServletContextAwareProcessor
	 * 
	 */
	public ServletContextAwareProcessor(ServletConfig servletConfig) {
		this(null, servletConfig);
	}

	/**
	 * Create a new ServletContextAwareProcessor for the given context and config.
	 * <p>
	 *  为给定的上下文和配置创建一个新的ServletContextAwareProcessor
	 * 
	 */
	public ServletContextAwareProcessor(ServletContext servletContext, ServletConfig servletConfig) {
		this.servletContext = servletContext;
		this.servletConfig = servletConfig;
	}


	/**
	 * Returns the {@link ServletContext} to be injected or {@code null}. This method
	 * can be overridden by subclasses when a context is obtained after the post-processor
	 * has been registered.
	 * <p>
	 *  返回要注入的{@link ServletContext}或{@code null}在后处理程序注册后获取上下文时,此方法可以被子类覆盖
	 * 
	 */
	protected ServletContext getServletContext() {
		if (this.servletContext == null && getServletConfig() != null) {
			return getServletConfig().getServletContext();
		}
		return this.servletContext;
	}

	/**
	 * Returns the {@link ServletContext} to be injected or {@code null}. This method
	 * can be overridden by subclasses when a context is obtained after the post-processor
	 * has been registered.
	 * <p>
	 * 返回要注入的{@link ServletContext}或{@code null}在后处理程序注册后获取上下文时,此方法可以被子类覆盖
	 */
	protected ServletConfig getServletConfig() {
		return this.servletConfig;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (getServletContext() != null && bean instanceof ServletContextAware) {
			((ServletContextAware) bean).setServletContext(getServletContext());
		}
		if (getServletConfig() != null && bean instanceof ServletConfigAware) {
			((ServletConfigAware) bean).setServletConfig(getServletConfig());
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}

}
