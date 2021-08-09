/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.web.servlet.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ObjectUtils;

/**
 * Abstract implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * interface, detecting URL mappings for handler beans through introspection of all
 * defined beans in the application context.
 *
 * <p>
 *  抽象实现{@link orgspringframeworkwebservletHandlerMapping}接口,通过反思应用程序上下文中所有定义的bean来检测处理程序bean的URL映射
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see #determineUrlsForHandler
 */
public abstract class AbstractDetectingUrlHandlerMapping extends AbstractUrlHandlerMapping {

	private boolean detectHandlersInAncestorContexts = false;


	/**
	 * Set whether to detect handler beans in ancestor ApplicationContexts.
	 * <p>Default is "false": Only handler beans in the current ApplicationContext
	 * will be detected, i.e. only in the context that this HandlerMapping itself
	 * is defined in (typically the current DispatcherServlet's context).
	 * <p>Switch this flag on to detect handler beans in ancestor contexts
	 * (typically the Spring root WebApplicationContext) as well.
	 * <p>
	 * 设置是否在祖先中检测处理程序Bean Applicationposition <p>默认值为"false"：仅检测当前ApplicationContext中的处理程序bean,即仅在此HandlerMa
	 * pping本身定义的上下文中(通常为当前DispatcherServlet的上下文)<p >打开此标志以检测祖先上下文中的处理程序bean(通常是Spring根WebApplicationContext
	 * )。
	 * 
	 */
	public void setDetectHandlersInAncestorContexts(boolean detectHandlersInAncestorContexts) {
		this.detectHandlersInAncestorContexts = detectHandlersInAncestorContexts;
	}


	/**
	 * Calls the {@link #detectHandlers()} method in addition to the
	 * superclass's initialization.
	 * <p>
	 *  调用{@link #detectHandlers()}方法除了超类的初始化
	 * 
	 */
	@Override
	public void initApplicationContext() throws ApplicationContextException {
		super.initApplicationContext();
		detectHandlers();
	}

	/**
	 * Register all handlers found in the current ApplicationContext.
	 * <p>The actual URL determination for a handler is up to the concrete
	 * {@link #determineUrlsForHandler(String)} implementation. A bean for
	 * which no such URLs could be determined is simply not considered a handler.
	 * <p>
	 *  注册在当前ApplicationContext中找到的所有处理程序<p>处理程序的实际URL确定取决于具体的{@link #determineUrlsForHandler(String))实现不能确定此类URL的bean根本不被视为处理程序。
	 * 
	 * 
	 * @throws org.springframework.beans.BeansException if the handler couldn't be registered
	 * @see #determineUrlsForHandler(String)
	 */
	protected void detectHandlers() throws BeansException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for URL mappings in application context: " + getApplicationContext());
		}
		String[] beanNames = (this.detectHandlersInAncestorContexts ?
				BeanFactoryUtils.beanNamesForTypeIncludingAncestors(getApplicationContext(), Object.class) :
				getApplicationContext().getBeanNamesForType(Object.class));

		// Take any bean name that we can determine URLs for.
		for (String beanName : beanNames) {
			String[] urls = determineUrlsForHandler(beanName);
			if (!ObjectUtils.isEmpty(urls)) {
				// URL paths found: Let's consider it a handler.
				registerHandler(urls, beanName);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Rejected bean name '" + beanName + "': no URL paths identified");
				}
			}
		}
	}


	/**
	 * Determine the URLs for the given handler bean.
	 * <p>
	 * 确定给定处理程序bean的URL
	 * 
	 * @param beanName the name of the candidate bean
	 * @return the URLs determined for the bean,
	 * or {@code null} or an empty array if none
	 */
	protected abstract String[] determineUrlsForHandler(String beanName);

}
