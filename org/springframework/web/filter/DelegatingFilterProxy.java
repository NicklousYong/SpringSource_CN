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

package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Proxy for a standard Servlet Filter, delegating to a Spring-managed bean that
 * implements the Filter interface. Supports a "targetBeanName" filter init-param
 * in {@code web.xml}, specifying the name of the target bean in the Spring
 * application context.
 *
 * <p>{@code web.xml} will usually contain a {@code DelegatingFilterProxy} definition,
 * with the specified {@code filter-name} corresponding to a bean name in
 * Spring's root application context. All calls to the filter proxy will then
 * be delegated to that bean in the Spring context, which is required to implement
 * the standard Servlet Filter interface.
 *
 * <p>This approach is particularly useful for Filter implementation with complex
 * setup needs, allowing to apply the full Spring bean definition machinery to
 * Filter instances. Alternatively, consider standard Filter setup in combination
 * with looking up service beans from the Spring root application context.
 *
 * <p><b>NOTE:</b> The lifecycle methods defined by the Servlet Filter interface
 * will by default <i>not</i> be delegated to the target bean, relying on the
 * Spring application context to manage the lifecycle of that bean. Specifying
 * the "targetFilterLifecycle" filter init-param as "true" will enforce invocation
 * of the {@code Filter.init} and {@code Filter.destroy} lifecycle methods
 * on the target bean, letting the servlet container manage the filter lifecycle.
 *
 * <p>As of Spring 3.1, {@code DelegatingFilterProxy} has been updated to optionally accept
 * constructor parameters when using Servlet 3.0's instance-based filter registration
 * methods, usually in conjunction with Spring 3.1's
 * {@link org.springframework.web.WebApplicationInitializer} SPI. These constructors allow
 * for providing the delegate Filter bean directly, or providing the application context
 * and bean name to fetch, avoiding the need to look up the application context from the
 * ServletContext.
 *
 * <p>This class was originally inspired by Spring Security's {@code FilterToBeanProxy}
 * class, written by Ben Alex.
 *
 * <p>
 * 标准Servlet过滤器的代理委托给实现Filter接口的Spring管理的bean在{@code webxml}中支持"targetBeanName"过滤器init-param,在Spring应用程序
 * 上下文中指定目标bean的名称。
 * 
 *  通常,{@ code webxml}将包含一个{@code DelegatingFilterProxy}定义,其中指定的{@code filter-name}与Spring的根应用程序上下文中的bea
 * n名称相对应,所有对过滤器代理的调用将被委派为那个bean在Spring上下文中,这是实现标准Servlet Filter接口所必需的。
 * 
 * <p>这种方法对于具有复杂设置需求的Filter实现特别有用,允许将完整的Spring bean定义机制应用于Filter实例或者,考虑使用标准过滤器设置以及从Spring根应用程序上下文中查找服务be
 * an。
 * 
 *  <p> <b>注意：</b> Servlet Filter接口定义的生命周期方法默认情况下不会将<i>委托给目标bean,依靠Spring应用程序上下文管理生命周期该bean将"targetFilte
 * rLifecycle"过滤器init-param指定为"true"将强制在目标bean上调用{@code Filterinit}和{@code Filterdestroy}生命周期方法,让servlet
 * 容器管理过滤器生命周期。
 * 
 * <p>截至春季31,{@code DelegatingFilterProxy}已更新,以便在使用Servlet 30的基于实例的过滤器注册方法时可以接受构造函数参数,通常与Spring 31的{@link orgspringframeworkwebWebApplicationInitializer}
 *  SPI结合使用。
 * 这些构造函数允许提供直接委托Filter bean,或提供应用程序上下文和bean名称进行抓取,避免从ServletContext中查找应用程序上下文。
 * 
 *  <p>这个类最初来自Spring Security的{@code FilterToBeanProxy}类,由Ben Alex编写
 * 
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Chris Beams
 * @since 1.2
 * @see #setTargetBeanName
 * @see #setTargetFilterLifecycle
 * @see javax.servlet.Filter#doFilter
 * @see javax.servlet.Filter#init
 * @see javax.servlet.Filter#destroy
 * @see #DelegatingFilterProxy(Filter)
 * @see #DelegatingFilterProxy(String)
 * @see #DelegatingFilterProxy(String, WebApplicationContext)
 * @see javax.servlet.ServletContext#addFilter(String, Filter)
 * @see org.springframework.web.WebApplicationInitializer
 */
public class DelegatingFilterProxy extends GenericFilterBean {

	private String contextAttribute;

	private WebApplicationContext webApplicationContext;

	private String targetBeanName;

	private boolean targetFilterLifecycle = false;

	private volatile Filter delegate;

	private final Object delegateMonitor = new Object();


	/**
	 * Create a new {@code DelegatingFilterProxy}. For traditional (pre-Servlet 3.0) use
	 * in {@code web.xml}.
	 * <p>
	 *  创建一个新的{@code DelegatingFilterProxy}对于{@code webxml}中的传统(pre-Servlet 30)
	 * 
	 * 
	 * @see #setTargetBeanName(String)
	 */
	public DelegatingFilterProxy() {
	}

	/**
	 * Create a new {@code DelegatingFilterProxy} with the given {@link Filter} delegate.
	 * Bypasses entirely the need for interacting with a Spring application context,
	 * specifying the {@linkplain #setTargetBeanName target bean name}, etc.
	 * <p>For use in Servlet 3.0+ environments where instance-based registration of
	 * filters is supported.
	 * <p>
	 * 使用给定的{@link Filter}委托创建新的{@code DelegatingFilterProxy},完全需要与Spring应用程序上下文交互,指定{@linkplain #setTargetBeanName目标bean名称}
	 * 等。
	 * <p>在Servlet 30中使用+支持基于实例的过滤器注册环境。
	 * 
	 * 
	 * @param delegate the {@code Filter} instance that this proxy will delegate to and
	 * manage the lifecycle for (must not be {@code null}).
	 * @see #doFilter(ServletRequest, ServletResponse, FilterChain)
	 * @see #invokeDelegate(Filter, ServletRequest, ServletResponse, FilterChain)
	 * @see #destroy()
	 * @see #setEnvironment(org.springframework.core.env.Environment)
	 */
	public DelegatingFilterProxy(Filter delegate) {
		Assert.notNull(delegate, "delegate Filter object must not be null");
		this.delegate = delegate;
	}

	/**
	 * Create a new {@code DelegatingFilterProxy} that will retrieve the named target
	 * bean from the Spring {@code WebApplicationContext} found in the {@code ServletContext}
	 * (either the 'root' application context or the context named by
	 * {@link #setContextAttribute}).
	 * <p>For use in Servlet 3.0+ environments where instance-based registration of
	 * filters is supported.
	 * <p>The target bean must implement the standard Servlet Filter.
	 * <p>
	 *  创建一个新的{@code DelegatingFilterProxy},它将从{@code ServletContext}("root"应用程序上下文)或由{@link #setContextAttribute}
	 * 命名的上下文中的Spring {@code WebApplicationContext}中检索到命名的目标bean。
	 *  )<p>用于支持基于实例的过滤器注册的Servlet 30+环境中的目标bean必须实现标准的Servlet过滤器。
	 * 
	 * 
	 * @param targetBeanName name of the target filter bean to look up in the Spring
	 * application context (must not be {@code null}).
	 * @see #findWebApplicationContext()
	 * @see #setEnvironment(org.springframework.core.env.Environment)
	 */
	public DelegatingFilterProxy(String targetBeanName) {
		this(targetBeanName, null);
	}

	/**
	 * Create a new {@code DelegatingFilterProxy} that will retrieve the named target
	 * bean from the given Spring {@code WebApplicationContext}.
	 * <p>For use in Servlet 3.0+ environments where instance-based registration of
	 * filters is supported.
	 * <p>The target bean must implement the standard Servlet Filter interface.
	 * <p>The given {@code WebApplicationContext} may or may not be refreshed when passed
	 * in. If it has not, and if the context implements {@link ConfigurableApplicationContext},
	 * a {@link ConfigurableApplicationContext#refresh() refresh()} will be attempted before
	 * retrieving the named target bean.
	 * <p>This proxy's {@code Environment} will be inherited from the given
	 * {@code WebApplicationContext}.
	 * <p>
	 * 创建一个新的{@code DelegatingFilterProxy},它将从给定的Spring {@code WebApplicationContext}中检索命名的目标bean。
	 * 对于在支持基于实例的过滤器注册的Servlet 30+环境中使用<p>目标bean必须实现标准的Servlet过滤器接口<p>当传递给定的{@code WebApplicationContext}可能会
	 * 被刷新或不被刷新,如果没有,如果上下文实现{@link ConfigurableApplicationContext},一个{@link ConfigurableApplicationContext#refresh()将在检索命名的目标bean之前尝试refresh()}
	 *  <p>此代理的{@code Environment}将继承自给定的{@code WebApplicationContext}。
	 * 创建一个新的{@code DelegatingFilterProxy},它将从给定的Spring {@code WebApplicationContext}中检索命名的目标bean。
	 * 
	 * 
	 * @param targetBeanName name of the target filter bean in the Spring application
	 * context (must not be {@code null}).
	 * @param wac the application context from which the target filter will be retrieved;
	 * if {@code null}, an application context will be looked up from {@code ServletContext}
	 * as a fallback.
	 * @see #findWebApplicationContext()
	 * @see #setEnvironment(org.springframework.core.env.Environment)
	 */
	public DelegatingFilterProxy(String targetBeanName, WebApplicationContext wac) {
		Assert.hasText(targetBeanName, "target Filter bean name must not be null or empty");
		this.setTargetBeanName(targetBeanName);
		this.webApplicationContext = wac;
		if (wac != null) {
			this.setEnvironment(wac.getEnvironment());
		}
	}

	/**
	 * Set the name of the ServletContext attribute which should be used to retrieve the
	 * {@link WebApplicationContext} from which to load the delegate {@link Filter} bean.
	 * <p>
	 * 设置ServletContext属性的名称,该属性应用于检索从中加载委托{@link Filter} bean的{@link WebApplicationContext}
	 * 
	 */
	public void setContextAttribute(String contextAttribute) {
		this.contextAttribute = contextAttribute;
	}

	/**
	 * Return the name of the ServletContext attribute which should be used to retrieve the
	 * {@link WebApplicationContext} from which to load the delegate {@link Filter} bean.
	 * <p>
	 *  返回ServletContext属性的名称,该属性应用于检索{@link WebApplicationContext},从中加载代理{@link Filter} bean
	 * 
	 */
	public String getContextAttribute() {
		return this.contextAttribute;
	}

	/**
	 * Set the name of the target bean in the Spring application context.
	 * The target bean must implement the standard Servlet Filter interface.
	 * <p>By default, the {@code filter-name} as specified for the
	 * DelegatingFilterProxy in {@code web.xml} will be used.
	 * <p>
	 *  在Spring应用程序上下文中设置目标bean的名称目标bean必须实现标准Servlet过滤器接口<p>默认情况下,将使用{@code webxml}中的DelegatingFilterProxy指
	 * 定的{@code filter-name}。
	 * 
	 */
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	/**
	 * Return the name of the target bean in the Spring application context.
	 * <p>
	 *  在Spring应用程序上下文中返回目标bean的名称
	 * 
	 */
	protected String getTargetBeanName() {
		return this.targetBeanName;
	}

	/**
	 * Set whether to invoke the {@code Filter.init} and
	 * {@code Filter.destroy} lifecycle methods on the target bean.
	 * <p>Default is "false"; target beans usually rely on the Spring application
	 * context for managing their lifecycle. Setting this flag to "true" means
	 * that the servlet container will control the lifecycle of the target
	 * Filter, with this proxy delegating the corresponding calls.
	 * <p>
	 * 设置是否在目标bean上调用{@code Filterinit}和{@code Filterdestroy}生命周期方法<p> Default is"false";目标bean通常依赖于Spring应用
	 * 程序上下文来管理其生命周期将此标志设置为"true"表示servlet容器将控制目标过滤器的生命周期,此代理委托相应的调用。
	 * 
	 */
	public void setTargetFilterLifecycle(boolean targetFilterLifecycle) {
		this.targetFilterLifecycle = targetFilterLifecycle;
	}

	/**
	 * Return whether to invoke the {@code Filter.init} and
	 * {@code Filter.destroy} lifecycle methods on the target bean.
	 * <p>
	 *  返回是否在目标bean上调用{@code Filterinit}和{@code Filterdestroy}生命周期方法
	 * 
	 */
	protected boolean isTargetFilterLifecycle() {
		return this.targetFilterLifecycle;
	}


	@Override
	protected void initFilterBean() throws ServletException {
		synchronized (this.delegateMonitor) {
			if (this.delegate == null) {
				// If no target bean name specified, use filter name.
				if (this.targetBeanName == null) {
					this.targetBeanName = getFilterName();
				}
				// Fetch Spring root application context and initialize the delegate early,
				// if possible. If the root application context will be started after this
				// filter proxy, we'll have to resort to lazy initialization.
				WebApplicationContext wac = findWebApplicationContext();
				if (wac != null) {
					this.delegate = initDelegate(wac);
				}
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Lazily initialize the delegate if necessary.
		Filter delegateToUse = this.delegate;
		if (delegateToUse == null) {
			synchronized (this.delegateMonitor) {
				if (this.delegate == null) {
					WebApplicationContext wac = findWebApplicationContext();
					if (wac == null) {
						throw new IllegalStateException("No WebApplicationContext found: " +
								"no ContextLoaderListener or DispatcherServlet registered?");
					}
					this.delegate = initDelegate(wac);
				}
				delegateToUse = this.delegate;
			}
		}

		// Let the delegate perform the actual doFilter operation.
		invokeDelegate(delegateToUse, request, response, filterChain);
	}

	@Override
	public void destroy() {
		Filter delegateToUse = this.delegate;
		if (delegateToUse != null) {
			destroyDelegate(delegateToUse);
		}
	}


	/**
	 * Return the {@code WebApplicationContext} passed in at construction time, if available.
	 * Otherwise, attempt to retrieve a {@code WebApplicationContext} from the
	 * {@code ServletContext} attribute with the {@linkplain #setContextAttribute
	 * configured name} if set. Otherwise look up a {@code WebApplicationContext} under
	 * the well-known "root" application context attribute. The
	 * {@code WebApplicationContext} must have already been loaded and stored in the
	 * {@code ServletContext} before this filter gets initialized (or invoked).
	 * <p>Subclasses may override this method to provide a different
	 * {@code WebApplicationContext} retrieval strategy.
	 * <p>
	 * 返回在构建时传递的{@code WebApplicationContext}(如果可用)否则,尝试从{@code ServletContext}属性中获取{@code WebApplicationContext}
	 * ,并设置{@linkplain #setContextAttribute配置名称}否则查找{@code WebApplicationContext}在众所周知的"根"应用程序上下文属性之前{@code WebApplicationContext}
	 * 必须已经加载并存储在{@code ServletContext}中,此过滤器初始化(或调用)之前子类可以覆盖此方法以提供不同的{@code WebApplicationContext}检索策略。
	 * 
	 * 
	 * @return the {@code WebApplicationContext} for this proxy, or {@code null} if not found
	 * @see #DelegatingFilterProxy(String, WebApplicationContext)
	 * @see #getContextAttribute()
	 * @see WebApplicationContextUtils#getWebApplicationContext(javax.servlet.ServletContext)
	 * @see WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	protected WebApplicationContext findWebApplicationContext() {
		if (this.webApplicationContext != null) {
			// The user has injected a context at construction time -> use it...
			if (this.webApplicationContext instanceof ConfigurableApplicationContext) {
				ConfigurableApplicationContext cac = (ConfigurableApplicationContext) this.webApplicationContext;
				if (!cac.isActive()) {
					// The context has not yet been refreshed -> do so before returning it...
					cac.refresh();
				}
			}
			return this.webApplicationContext;
		}
		String attrName = getContextAttribute();
		if (attrName != null) {
			return WebApplicationContextUtils.getWebApplicationContext(getServletContext(), attrName);
		}
		else {
			return WebApplicationContextUtils.findWebApplicationContext(getServletContext());
		}
	}

	/**
	 * Initialize the Filter delegate, defined as bean the given Spring
	 * application context.
	 * <p>The default implementation fetches the bean from the application context
	 * and calls the standard {@code Filter.init} method on it, passing
	 * in the FilterConfig of this Filter proxy.
	 * <p>
	 * 初始化Filter委托,定义为给定的Spring应用程序上下文的bean <p>默认实现从应用程序上下文中获取bean,并调用标准的{@code Filterinit}方法,传递此Filter代理的Fi
	 * lterConfig。
	 * 
	 * 
	 * @param wac the root application context
	 * @return the initialized delegate Filter
	 * @throws ServletException if thrown by the Filter
	 * @see #getTargetBeanName()
	 * @see #isTargetFilterLifecycle()
	 * @see #getFilterConfig()
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
		Filter delegate = wac.getBean(getTargetBeanName(), Filter.class);
		if (isTargetFilterLifecycle()) {
			delegate.init(getFilterConfig());
		}
		return delegate;
	}

	/**
	 * Actually invoke the delegate Filter with the given request and response.
	 * <p>
	 *  实际上使用给定的请求和响应调用委托Filter
	 * 
	 * 
	 * @param delegate the delegate Filter
	 * @param request the current HTTP request
	 * @param response the current HTTP response
	 * @param filterChain the current FilterChain
	 * @throws ServletException if thrown by the Filter
	 * @throws IOException if thrown by the Filter
	 */
	protected void invokeDelegate(
			Filter delegate, ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		delegate.doFilter(request, response, filterChain);
	}

	/**
	 * Destroy the Filter delegate.
	 * Default implementation simply calls {@code Filter.destroy} on it.
	 * <p>
	 *  破坏Filter代理默认实现只需调用{@code Filterdestroy}就可以了
	 * 
	 * @param delegate the Filter delegate (never {@code null})
	 * @see #isTargetFilterLifecycle()
	 * @see javax.servlet.Filter#destroy()
	 */
	protected void destroyDelegate(Filter delegate) {
		if (isTargetFilterLifecycle()) {
			delegate.destroy();
		}
	}

}
