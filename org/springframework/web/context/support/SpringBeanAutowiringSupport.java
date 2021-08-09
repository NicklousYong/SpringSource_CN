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

package org.springframework.web.context.support;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Convenient base class for self-autowiring classes that gets constructed
 * within a Spring-based web application. Resolves {@code @Autowired}
 * annotations in the endpoint class against beans in the current Spring
 * root web application context (as determined by the current thread's
 * context ClassLoader, which needs to be the web application's ClassLoader).
 * Can alternatively be used as a delegate instead of as a base class.
 *
 * <p>A typical usage of this base class is a JAX-WS endpoint class:
 * Such a Spring-based JAX-WS endpoint implementation will follow the
 * standard JAX-WS contract for endpoint classes but will be 'thin'
 * in that it delegates the actual work to one or more Spring-managed
 * service beans - typically obtained using {@code @Autowired}.
 * The lifecycle of such an endpoint instance will be managed by the
 * JAX-WS runtime, hence the need for this base class to provide
 * {@code @Autowired} processing based on the current Spring context.
 *
 * <p><b>NOTE:</b> If there is an explicit way to access the ServletContext,
 * prefer such a way over using this class. The {@link WebApplicationContextUtils}
 * class allows for easy access to the Spring root web application context
 * based on the ServletContext.
 *
 * <p>
 * 在基于Spring的Web应用程序中构建的自动布线类的方便基类在当前Spring根Web应用程序上下文中针对bean解析端点类中的{@code @Autowired}注释(由当前线程的上下文ClassL
 * oader确定)这需要是Web应用程序的ClassLoader)可以替代地作为代理使用,而不是基类。
 * 
 * 这个基类的典型用法是一个JAX-WS端点类：这样一个基于Spring的JAX-WS端点实现将遵循用于端点类的标准JAX-WS合同,但是它将是"薄"的,因为它委托一个或多个Spring管理的服务bean的
 * 实际工作 - 通常使用{@code @Autowired}获取。
 * 这样一个端点实例的生命周期将由JAX-WS运行时管理,因此需要这个基类来提供{@code @Autowired}根据当前的Spring上下文进行处理。
 * 
 *  <p> <b>注意：</b>如果存在访问ServletContext的明确方式,则更倾向于使用此类。
 * {@link WebApplicationContextUtils}类允许轻松访问Spring根Web应用程序上下文在ServletContext上。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.1
 * @see WebApplicationObjectSupport
 */
public abstract class SpringBeanAutowiringSupport {

	private static final Log logger = LogFactory.getLog(SpringBeanAutowiringSupport.class);


	/**
	 * This constructor performs injection on this instance,
	 * based on the current web application context.
	 * <p>Intended for use as a base class.
	 * <p>
	 * 
	 * @see #processInjectionBasedOnCurrentContext
	 */
	public SpringBeanAutowiringSupport() {
		processInjectionBasedOnCurrentContext(this);
	}


	/**
	 * Process {@code @Autowired} injection for the given target object,
	 * based on the current web application context.
	 * <p>Intended for use as a delegate.
	 * <p>
	 * 该构造函数基于当前的Web应用程序上下文<p>作为基类使用,在此实例上执行注入
	 * 
	 * 
	 * @param target the target object to process
	 * @see org.springframework.web.context.ContextLoader#getCurrentWebApplicationContext()
	 */
	public static void processInjectionBasedOnCurrentContext(Object target) {
		Assert.notNull(target, "Target object must not be null");
		WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
		if (cc != null) {
			AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
			bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
			bpp.processInjection(target);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Current WebApplicationContext is not available for processing of " +
						ClassUtils.getShortName(target.getClass()) + ": " +
						"Make sure this class gets constructed in a Spring web application. Proceeding without injection.");
			}
		}
	}


	/**
	 * Process {@code @Autowired} injection for the given target object,
	 * based on the current root web application context as stored in the ServletContext.
	 * <p>Intended for use as a delegate.
	 * <p>
	 *  根据当前的Web应用程序上下文,为给定的目标对象处理{@code @Autowired}注入<p>旨在用作代理
	 * 
	 * 
	 * @param target the target object to process
	 * @param servletContext the ServletContext to find the Spring web application context in
	 * @see WebApplicationContextUtils#getWebApplicationContext(javax.servlet.ServletContext)
	 */
	public static void processInjectionBasedOnServletContext(Object target, ServletContext servletContext) {
		Assert.notNull(target, "Target object must not be null");
		WebApplicationContext cc = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
		bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
		bpp.processInjection(target);
	}

}
