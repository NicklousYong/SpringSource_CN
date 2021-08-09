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

package org.springframework.web.servlet.support;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;

/**
 * Base class for {@link org.springframework.web.WebApplicationInitializer}
 * implementations that register a {@link DispatcherServlet} in the servlet context.
 *
 * <p>Concrete implementations are required to implement
 * {@link #createServletApplicationContext()}, as well as {@link #getServletMappings()},
 * both of which get invoked from {@link #registerDispatcherServlet(ServletContext)}.
 * Further customization can be achieved by overriding
 * {@link #customizeRegistration(ServletRegistration.Dynamic)}.
 *
 * <p>Because this class extends from {@link AbstractContextLoaderInitializer}, concrete
 * implementations are also required to implement {@link #createRootApplicationContext()}
 * to set up a parent "<strong>root</strong>" application context. If a root context is
 * not desired, implementations can simply return {@code null} in the
 * {@code createRootApplicationContext()} implementation.
 *
 * <p>
 *  {@link orgspringframeworkwebWebApplicationInitializer}实现的基类,在servlet上下文中注册一个{@link DispatcherServlet}
 * 。
 * 
 * <p>实现{@link #createServletApplicationContext()}和{@link #getServletMappings()}需要具体的实现,这两个都可以从{@link #registerDispatcherServlet(ServletContext)}
 * 调用)。
 * 可以实现进一步的定制通过覆盖{@link #customizeRegistration(ServletRegistrationDynamic)}。
 * 
 *  <p>因为这个类从{@link AbstractContextLoaderInitializer}扩展,所以需要具体的实现来实现{@link #createRootApplicationContext()}
 * 来设置父"<strong>根</strong>"应用程序上下文如果根上下文不需要,实现可以简单地返回{@code createRootApplicationContext()}实现中的{@code null}
 * 。
 * 
 * 
 * @author Arjen Poutsma
 * @author Chris Beams
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.2
 */
public abstract class AbstractDispatcherServletInitializer extends AbstractContextLoaderInitializer {

	/**
	 * The default servlet name. Can be customized by overriding {@link #getServletName}.
	 * <p>
	 * 默认的servlet名称可以通过覆盖{@link #getServletName}进行自定义
	 * 
	 */
	public static final String DEFAULT_SERVLET_NAME = "dispatcher";


	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		registerDispatcherServlet(servletContext);
	}

	/**
	 * Register a {@link DispatcherServlet} against the given servlet context.
	 * <p>This method will create a {@code DispatcherServlet} with the name returned by
	 * {@link #getServletName()}, initializing it with the application context returned
	 * from {@link #createServletApplicationContext()}, and mapping it to the patterns
	 * returned from {@link #getServletMappings()}.
	 * <p>Further customization can be achieved by overriding {@link
	 * #customizeRegistration(ServletRegistration.Dynamic)} or
	 * {@link #createDispatcherServlet(WebApplicationContext)}.
	 * <p>
	 *  根据给定的servlet上下文注册{@link DispatcherServlet} <p>此方法将使用{@link #getServletName()}返回的名称创建一个{@code DispatcherServlet}
	 * ,并使用从{@link返回的应用程序上下文进行初始化#createServletApplicationContext()},并将其映射到从{@link #getServletMappings())返回的模式<p>可以通过覆盖{@link #customizeRegistration(ServletRegistrationDynamic)}
	 * 或{@link #createDispatcherServlet(WebApplicationContext))来实现进一步的自定义}。
	 * 
	 * 
	 * @param servletContext the context to register the servlet against
	 */
	protected void registerDispatcherServlet(ServletContext servletContext) {
		String servletName = getServletName();
		Assert.hasLength(servletName, "getServletName() must not return empty or null");

		WebApplicationContext servletAppContext = createServletApplicationContext();
		Assert.notNull(servletAppContext,
				"createServletApplicationContext() did not return an application " +
				"context for servlet [" + servletName + "]");

		FrameworkServlet dispatcherServlet = createDispatcherServlet(servletAppContext);
		dispatcherServlet.setContextInitializers(getServletApplicationContextInitializers());

		ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, dispatcherServlet);
		Assert.notNull(registration,
				"Failed to register servlet with name '" + servletName + "'." +
				"Check if there is another servlet registered under the same name.");

		registration.setLoadOnStartup(1);
		registration.addMapping(getServletMappings());
		registration.setAsyncSupported(isAsyncSupported());

		Filter[] filters = getServletFilters();
		if (!ObjectUtils.isEmpty(filters)) {
			for (Filter filter : filters) {
				registerServletFilter(servletContext, filter);
			}
		}

		customizeRegistration(registration);
	}

	/**
	 * Return the name under which the {@link DispatcherServlet} will be registered.
	 * Defaults to {@link #DEFAULT_SERVLET_NAME}.
	 * <p>
	 *  返回{@link DispatcherServlet}将被注册的名称默认为{@link #DEFAULT_SERVLET_NAME}
	 * 
	 * 
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected String getServletName() {
		return DEFAULT_SERVLET_NAME;
	}

	/**
	 * Create a servlet application context to be provided to the {@code DispatcherServlet}.
	 * <p>The returned context is delegated to Spring's
	 * {@link DispatcherServlet#DispatcherServlet(WebApplicationContext)}. As such,
	 * it typically contains controllers, view resolvers, locale resolvers, and other
	 * web-related beans.
	 * <p>
	 * 创建一个要提供给{@code DispatcherServlet}的servlet应用程序上下文<p>将返回的上下文委派给Spring的{@link DispatcherServlet#DispatcherServlet(WebApplicationContext)}
	 * 。
	 * 因此,它通常包含控制器,视图解析器,区域设置解析器和其他web相关的bean。
	 * 
	 * 
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected abstract WebApplicationContext createServletApplicationContext();

	/**
	 * Create a {@link DispatcherServlet} (or other kind of {@link FrameworkServlet}-derived
	 * dispatcher) with the specified {@link WebApplicationContext}.
	 * <p>Note: This allows for any {@link FrameworkServlet} subclass as of 4.2.3.
	 * Previously, it insisted on returning a {@link DispatcherServlet} or subclass thereof.
	 * <p>
	 *  创建一个{@link DispatcherServlet}(或其他类型的{@link FrameworkServlet}派生调度程序)与指定的{@link WebApplicationContext}
	 *  <p>注意：这允许任何{@link FrameworkServlet}子类为423以前,它坚持要返回一个{@link DispatcherServlet}或其子类。
	 * 
	 */
	protected FrameworkServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
		return new DispatcherServlet(servletAppContext);
	}

	/**
	 * Specify application context initializers to be applied to the servlet-specific
	 * application context that the {@code DispatcherServlet} is being created with.
	 * <p>
	 *  指定应用于正在创建的{@code DispatcherServlet}的特定于servlet的应用程序上下文的应用程序上下文初始值设置
	 * 
	 * 
	 * @since 4.2
	 * @see #createServletApplicationContext()
	 * @see DispatcherServlet#setContextInitializers
	 * @see #getRootApplicationContextInitializers()
	 */
	protected ApplicationContextInitializer<?>[] getServletApplicationContextInitializers() {
		return null;
	}

	/**
	 * Specify the servlet mapping(s) for the {@code DispatcherServlet} &mdash;
	 * for example {@code "/"}, {@code "/app"}, etc.
	 * <p>
	 * 指定{@code DispatcherServlet}&mdash的servlet映射;例如{@code"/"},{@code"/ app"}等
	 * 
	 * 
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected abstract String[] getServletMappings();

	/**
	 * Specify filters to add and map to the {@code DispatcherServlet}.
	 * <p>
	 *  指定过滤器以添加和映射到{@code DispatcherServlet}
	 * 
	 * 
	 * @return an array of filters or {@code null}
	 * @see #registerServletFilter(ServletContext, Filter)
	 */
	protected Filter[] getServletFilters() {
		return null;
	}

	/**
	 * Add the given filter to the ServletContext and map it to the
	 * {@code DispatcherServlet} as follows:
	 * <ul>
	 * <li>a default filter name is chosen based on its concrete type
	 * <li>the {@code asyncSupported} flag is set depending on the
	 * return value of {@link #isAsyncSupported() asyncSupported}
	 * <li>a filter mapping is created with dispatcher types {@code REQUEST},
	 * {@code FORWARD}, {@code INCLUDE}, and conditionally {@code ASYNC} depending
	 * on the return value of {@link #isAsyncSupported() asyncSupported}
	 * </ul>
	 * <p>If the above defaults are not suitable or insufficient, override this
	 * method and register filters directly with the {@code ServletContext}.
	 * <p>
	 *  将给定的过滤器添加到ServletContext中,并将其映射到{@code DispatcherServlet},如下所示：
	 * <ul>
	 *  <li>根据具体类型<li>,根据{@link #isAsyncSupported()asyncSupported}的返回值设置{@code asyncSupported}标志来选择默认过滤器名称} 
	 * <li>使用派生类型{@code REQUEST},{@code FORWARD},{@code INCLUDE}和有条件{@code ASYNC},具体取决于{@link #isAsyncSupported()asyncSupported的返回值}
	 * 。
	 * </ul>
	 * <p>如果上述默认值不合适或不足,请覆盖此方法并直接使用{@code ServletContext}注册过滤器
	 * 
	 * @param servletContext the servlet context to register filters with
	 * @param filter the filter to be registered
	 * @return the filter registration
	 */
	protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext, Filter filter) {
		String filterName = Conventions.getVariableName(filter);
		Dynamic registration = servletContext.addFilter(filterName, filter);
		if (registration == null) {
			int counter = -1;
			while (counter == -1 || registration == null) {
				counter++;
				registration = servletContext.addFilter(filterName + "#" + counter, filter);
				Assert.isTrue(counter < 100,
						"Failed to register filter '" + filter + "'." +
						"Could the same Filter instance have been registered already?");
			}
		}
		registration.setAsyncSupported(isAsyncSupported());
		registration.addMappingForServletNames(getDispatcherTypes(), false, getServletName());
		return registration;
	}

	private EnumSet<DispatcherType> getDispatcherTypes() {
		return (isAsyncSupported() ?
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) :
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE));
	}

	/**
	 * A single place to control the {@code asyncSupported} flag for the
	 * {@code DispatcherServlet} and all filters added via {@link #getServletFilters()}.
	 * <p>The default value is "true".
	 * <p>
	 * 
	 */
	protected boolean isAsyncSupported() {
		return true;
	}

	/**
	 * Optionally perform further registration customization once
	 * {@link #registerDispatcherServlet(ServletContext)} has completed.
	 * <p>
	 *  控制{@code DispatcherServlet}的{@code asyncSupported}标志和通过{@link #getServletFilters())添加的所有过滤器的单一位置<p>默认值为"true"。
	 * 
	 * 
	 * @param registration the {@code DispatcherServlet} registration to be customized
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
	}

}
