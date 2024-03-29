/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource.StubPropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.request.WebRequest;

/**
 * Convenience methods for retrieving the root {@link WebApplicationContext} for
 * a given {@link ServletContext}. This is useful for programmatically accessing
 * a Spring application context from within custom web views or MVC actions.
 *
 * <p>Note that there are more convenient ways of accessing the root context for
 * many web frameworks, either part of Spring or available as an external library.
 * This helper class is just the most generic way to access the root context.
 *
 * <p>
 * 检索给定{@link ServletContext}的根{@link WebApplicationContext}的方便方法这对于在自定义Web视图或MVC操作中以编程方式访问Spring应用程序上下文
 * 很有用。
 * 
 *  请注意,有更多方便的方式访问许多Web框架的根环境,这是Spring的一部分或作为外部库可用。此帮助器类只是最通用的方式来访问根上下文
 * 
 * 
 * @author Juergen Hoeller
 * @see org.springframework.web.context.ContextLoader
 * @see org.springframework.web.servlet.FrameworkServlet
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.jsf.FacesContextUtils
 * @see org.springframework.web.jsf.el.SpringBeanFacesELResolver
 */
public abstract class WebApplicationContextUtils {

	private static final boolean jsfPresent =
			ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());


	/**
	 * Find the root {@code WebApplicationContext} for this web app, typically
	 * loaded via {@link org.springframework.web.context.ContextLoaderListener}.
	 * <p>Will rethrow an exception that happened on root context startup,
	 * to differentiate between a failed context startup and no context at all.
	 * <p>
	 *  找到此网络应用程序的根{@code WebApplicationContext},通常通过{@link orgspringframeworkwebcontextContextLoaderListener}
	 * 加载<p>将重新启动根上下文启动时发生的异常,以区分失败的上下文启动和无上下文。
	 * 
	 * 
	 * @param sc ServletContext to find the web application context for
	 * @return the root WebApplicationContext for this web app
	 * @throws IllegalStateException if the root WebApplicationContext could not be found
	 * @see org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	public static WebApplicationContext getRequiredWebApplicationContext(ServletContext sc) throws IllegalStateException {
		WebApplicationContext wac = getWebApplicationContext(sc);
		if (wac == null) {
			throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
		}
		return wac;
	}

	/**
	 * Find the root {@code WebApplicationContext} for this web app, typically
	 * loaded via {@link org.springframework.web.context.ContextLoaderListener}.
	 * <p>Will rethrow an exception that happened on root context startup,
	 * to differentiate between a failed context startup and no context at all.
	 * <p>
	 * 找到此网络应用程序的根{@code WebApplicationContext},通常通过{@link orgspringframeworkwebcontextContextLoaderListener}
	 * 加载<p>将重新启动根上下文启动时发生的异常,以区分失败的上下文启动和无上下文。
	 * 
	 * 
	 * @param sc ServletContext to find the web application context for
	 * @return the root WebApplicationContext for this web app, or {@code null} if none
	 * @see org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	public static WebApplicationContext getWebApplicationContext(ServletContext sc) {
		return getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
	}

	/**
	 * Find a custom {@code WebApplicationContext} for this web app.
	 * <p>
	 *  查找此网络应用程序的自定义{@code WebApplicationContext}
	 * 
	 * 
	 * @param sc ServletContext to find the web application context for
	 * @param attrName the name of the ServletContext attribute to look for
	 * @return the desired WebApplicationContext for this web app, or {@code null} if none
	 */
	public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
		Assert.notNull(sc, "ServletContext must not be null");
		Object attr = sc.getAttribute(attrName);
		if (attr == null) {
			return null;
		}
		if (attr instanceof RuntimeException) {
			throw (RuntimeException) attr;
		}
		if (attr instanceof Error) {
			throw (Error) attr;
		}
		if (attr instanceof Exception) {
			throw new IllegalStateException((Exception) attr);
		}
		if (!(attr instanceof WebApplicationContext)) {
			throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
		}
		return (WebApplicationContext) attr;
	}

	/**
	 * Find a unique {@code WebApplicationContext} for this web app: either the
	 * root web app context (preferred) or a unique {@code WebApplicationContext}
	 * among the registered {@code ServletContext} attributes (typically coming
	 * from a single {@code DispatcherServlet} in the current web application).
	 * <p>Note that {@code DispatcherServlet}'s exposure of its context can be
	 * controlled through its {@code publishContext} property, which is {@code true}
	 * by default but can be selectively switched to only publish a single context
	 * despite multiple {@code DispatcherServlet} registrations in the web app.
	 * <p>
	 * 为此Web应用程序找到唯一的{@code WebApplicationContext}：注册的{@code ServletContext}属性中的根Web应用程序上下文(首选)或唯一的{@code WebApplicationContext}
	 * (通常来自单个{@code DispatcherServlet}在当前的Web应用程序中)<p>请注意,{@code DispatcherServlet}的上下文的曝光可以通过其{@code publishContext}
	 * 属性来控制,该属性默认为{@code true},但可以有选择地切换到尽管在网络应用程序中有多个{@code DispatcherServlet}注册,但发布单个上下文。
	 * 
	 * 
	 * @param sc ServletContext to find the web application context for
	 * @return the desired WebApplicationContext for this web app, or {@code null} if none
	 * @since 4.2
	 * @see #getWebApplicationContext(ServletContext)
	 * @see ServletContext#getAttributeNames()
	 */
	public static WebApplicationContext findWebApplicationContext(ServletContext sc) {
		WebApplicationContext wac = getWebApplicationContext(sc);
		if (wac == null) {
			Enumeration<String> attrNames = sc.getAttributeNames();
			while (attrNames.hasMoreElements()) {
				String attrName = attrNames.nextElement();
				Object attrValue = sc.getAttribute(attrName);
				if (attrValue instanceof WebApplicationContext) {
					if (wac != null) {
						throw new IllegalStateException("No unique WebApplicationContext found: more than one " +
								"DispatcherServlet registered with publishContext=true?");
					}
					wac = (WebApplicationContext) attrValue;
				}
			}
		}
		return wac;
	}


	/**
	 * Register web-specific scopes ("request", "session", "globalSession")
	 * with the given BeanFactory, as used by the WebApplicationContext.
	 * <p>
	 *  使用给定的BeanFactory注册特定于Web的范围("请求","会话","globalSession"),由WebApplicationContext
	 * 
	 * 
	 * @param beanFactory the BeanFactory to configure
	 */
	public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory) {
		registerWebApplicationScopes(beanFactory, null);
	}

	/**
	 * Register web-specific scopes ("request", "session", "globalSession", "application")
	 * with the given BeanFactory, as used by the WebApplicationContext.
	 * <p>
	 * 使用给定的BeanFactory注册特定于Web的范围("请求","会话","globalSession","应用程序"),由WebApplicationContext
	 * 
	 * 
	 * @param beanFactory the BeanFactory to configure
	 * @param sc the ServletContext that we're running within
	 */
	public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory, ServletContext sc) {
		beanFactory.registerScope(WebApplicationContext.SCOPE_REQUEST, new RequestScope());
		beanFactory.registerScope(WebApplicationContext.SCOPE_SESSION, new SessionScope(false));
		beanFactory.registerScope(WebApplicationContext.SCOPE_GLOBAL_SESSION, new SessionScope(true));
		if (sc != null) {
			ServletContextScope appScope = new ServletContextScope(sc);
			beanFactory.registerScope(WebApplicationContext.SCOPE_APPLICATION, appScope);
			// Register as ServletContext attribute, for ContextCleanupListener to detect it.
			sc.setAttribute(ServletContextScope.class.getName(), appScope);
		}

		beanFactory.registerResolvableDependency(ServletRequest.class, new RequestObjectFactory());
		beanFactory.registerResolvableDependency(ServletResponse.class, new ResponseObjectFactory());
		beanFactory.registerResolvableDependency(HttpSession.class, new SessionObjectFactory());
		beanFactory.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
		if (jsfPresent) {
			FacesDependencyRegistrar.registerFacesDependencies(beanFactory);
		}
	}

	/**
	 * Register web-specific environment beans ("contextParameters", "contextAttributes")
	 * with the given BeanFactory, as used by the WebApplicationContext.
	 * <p>
	 *  使用给定的BeanFactory注册特定于Web的环境bean("contextParameters","contextAttributes"),由WebApplicationContext使用
	 * 
	 * 
	 * @param bf the BeanFactory to configure
	 * @param sc the ServletContext that we're running within
	 */
	public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, ServletContext sc) {
		registerEnvironmentBeans(bf, sc, null);
	}

	/**
	 * Register web-specific environment beans ("contextParameters", "contextAttributes")
	 * with the given BeanFactory, as used by the WebApplicationContext.
	 * <p>
	 *  使用给定的BeanFactory注册特定于Web的环境bean("contextParameters","contextAttributes"),由WebApplicationContext使用
	 * 
	 * 
	 * @param bf the BeanFactory to configure
	 * @param servletContext the ServletContext that we're running within
	 * @param servletConfig the ServletConfig of the containing Portlet
	 */
	public static void registerEnvironmentBeans(
			ConfigurableListableBeanFactory bf, ServletContext servletContext, ServletConfig servletConfig) {

		if (servletContext != null && !bf.containsBean(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME)) {
			bf.registerSingleton(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME, servletContext);
		}

		if (servletConfig != null && !bf.containsBean(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME)) {
			bf.registerSingleton(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME, servletConfig);
		}

		if (!bf.containsBean(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME)) {
			Map<String, String> parameterMap = new HashMap<String, String>();
			if (servletContext != null) {
				Enumeration<?> paramNameEnum = servletContext.getInitParameterNames();
				while (paramNameEnum.hasMoreElements()) {
					String paramName = (String) paramNameEnum.nextElement();
					parameterMap.put(paramName, servletContext.getInitParameter(paramName));
				}
			}
			if (servletConfig != null) {
				Enumeration<?> paramNameEnum = servletConfig.getInitParameterNames();
				while (paramNameEnum.hasMoreElements()) {
					String paramName = (String) paramNameEnum.nextElement();
					parameterMap.put(paramName, servletConfig.getInitParameter(paramName));
				}
			}
			bf.registerSingleton(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME,
					Collections.unmodifiableMap(parameterMap));
		}

		if (!bf.containsBean(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME)) {
			Map<String, Object> attributeMap = new HashMap<String, Object>();
			if (servletContext != null) {
				Enumeration<?> attrNameEnum = servletContext.getAttributeNames();
				while (attrNameEnum.hasMoreElements()) {
					String attrName = (String) attrNameEnum.nextElement();
					attributeMap.put(attrName, servletContext.getAttribute(attrName));
				}
			}
			bf.registerSingleton(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME,
					Collections.unmodifiableMap(attributeMap));
		}
	}

	/**
	 * Convenient variant of {@link #initServletPropertySources(MutablePropertySources,
	 * ServletContext, ServletConfig)} that always provides {@code null} for the
	 * {@link ServletConfig} parameter.
	 * <p>
	 *  {@link ServletConfig}参数的{@link #initServletPropertySources(MutablePropertySources,ServletContext,ServletConfig)}
	 * 的方便变体,始终为{@link ServletConfig}参数提供{@code null}。
	 * 
	 * 
	 * @see #initServletPropertySources(MutablePropertySources, ServletContext, ServletConfig)
	 */
	public static void initServletPropertySources(MutablePropertySources propertySources, ServletContext servletContext) {
		initServletPropertySources(propertySources, servletContext, null);
	}

	/**
	 * Replace {@code Servlet}-based {@link StubPropertySource stub property sources} with
	 * actual instances populated with the given {@code servletContext} and
	 * {@code servletConfig} objects.
	 * <p>This method is idempotent with respect to the fact it may be called any number
	 * of times but will perform replacement of stub property sources with their
	 * corresponding actual property sources once and only once.
	 * <p>
	 * 使用给定的{@code servletContext}和{@code servletConfig}对象填充的实际实例替换{@code Servlet}的{@link StubPropertySource stub属性源}
	 * 该方法对于它可能是这样的事实是等幂的称为任何次数,但将执行存根属性源与其相应的实际属性源一次且仅一次的替换。
	 * 
	 * 
	 * @param propertySources the {@link MutablePropertySources} to initialize (must not
	 * be {@code null})
	 * @param servletContext the current {@link ServletContext} (ignored if {@code null}
	 * or if the {@link StandardServletEnvironment#SERVLET_CONTEXT_PROPERTY_SOURCE_NAME
	 * servlet context property source} has already been initialized)
	 * @param servletConfig the current {@link ServletConfig} (ignored if {@code null}
	 * or if the {@link StandardServletEnvironment#SERVLET_CONFIG_PROPERTY_SOURCE_NAME
	 * servlet config property source} has already been initialized)
	 * @see org.springframework.core.env.PropertySource.StubPropertySource
	 * @see org.springframework.core.env.ConfigurableEnvironment#getPropertySources()
	 */
	public static void initServletPropertySources(
			MutablePropertySources propertySources, ServletContext servletContext, ServletConfig servletConfig) {

		Assert.notNull(propertySources, "'propertySources' must not be null");
		if (servletContext != null && propertySources.contains(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME) &&
				propertySources.get(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME) instanceof StubPropertySource) {
			propertySources.replace(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME,
					new ServletContextPropertySource(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME, servletContext));
		}
		if (servletConfig != null && propertySources.contains(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME) &&
				propertySources.get(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME) instanceof StubPropertySource) {
			propertySources.replace(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME,
					new ServletConfigPropertySource(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME, servletConfig));
		}
	}

	/**
	 * Return the current RequestAttributes instance as ServletRequestAttributes.
	 * <p>
	 *  将当前RequestAttributes实例作为ServletRequestAttributes返回
	 * 
	 * 
	 * @see RequestContextHolder#currentRequestAttributes()
	 */
	private static ServletRequestAttributes currentRequestAttributes() {
		RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
		if (!(requestAttr instanceof ServletRequestAttributes)) {
			throw new IllegalStateException("Current request is not a servlet request");
		}
		return (ServletRequestAttributes) requestAttr;
	}


	/**
	 * Factory that exposes the current request object on demand.
	 * <p>
	 *  根据需要公开当前请求对象的工厂
	 * 
	 */
	@SuppressWarnings("serial")
	private static class RequestObjectFactory implements ObjectFactory<ServletRequest>, Serializable {

		@Override
		public ServletRequest getObject() {
			return currentRequestAttributes().getRequest();
		}

		@Override
		public String toString() {
			return "Current HttpServletRequest";
		}
	}


	/**
	 * Factory that exposes the current response object on demand.
	 * <p>
	 *  根据需要公开当前响应对象的工厂
	 * 
	 */
	@SuppressWarnings("serial")
	private static class ResponseObjectFactory implements ObjectFactory<ServletResponse>, Serializable {

		@Override
		public ServletResponse getObject() {
			ServletResponse response = currentRequestAttributes().getResponse();
			if (response == null) {
				throw new IllegalStateException("Current servlet response not available - " +
						"consider using RequestContextFilter instead of RequestContextListener");
			}
			return response;
		}

		@Override
		public String toString() {
			return "Current HttpServletResponse";
		}
	}


	/**
	 * Factory that exposes the current session object on demand.
	 * <p>
	 *  根据需要公开当前会话对象的工厂
	 * 
	 */
	@SuppressWarnings("serial")
	private static class SessionObjectFactory implements ObjectFactory<HttpSession>, Serializable {

		@Override
		public HttpSession getObject() {
			return currentRequestAttributes().getRequest().getSession();
		}

		@Override
		public String toString() {
			return "Current HttpSession";
		}
	}


	/**
	 * Factory that exposes the current WebRequest object on demand.
	 * <p>
	 *  根据需要公开当前WebRequest对象的工厂
	 * 
	 */
	@SuppressWarnings("serial")
	private static class WebRequestObjectFactory implements ObjectFactory<WebRequest>, Serializable {

		@Override
		public WebRequest getObject() {
			ServletRequestAttributes requestAttr = currentRequestAttributes();
			return new ServletWebRequest(requestAttr.getRequest(), requestAttr.getResponse());
		}

		@Override
		public String toString() {
			return "Current ServletWebRequest";
		}
	}


	/**
	 * Inner class to avoid hard-coded JSF dependency.
	 * <p>
	 *  内部类避免了硬编码的JSF依赖
 	 */
	private static class FacesDependencyRegistrar {

		public static void registerFacesDependencies(ConfigurableListableBeanFactory beanFactory) {
			beanFactory.registerResolvableDependency(FacesContext.class, new ObjectFactory<FacesContext>() {
				@Override
				public FacesContext getObject() {
					return FacesContext.getCurrentInstance();
				}
				@Override
				public String toString() {
					return "Current JSF FacesContext";
				}
			});
			beanFactory.registerResolvableDependency(ExternalContext.class, new ObjectFactory<ExternalContext>() {
				@Override
				public ExternalContext getObject() {
					return FacesContext.getCurrentInstance().getExternalContext();
				}
				@Override
				public String toString() {
					return "Current JSF ExternalContext";
				}
			});
		}
	}

}
