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

package org.springframework.web.multipart.support;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/**
 * Servlet Filter that resolves multipart requests via a {@link MultipartResolver}.
 * in the root web application context.
 *
 * <p>Looks up the MultipartResolver in Spring's root web application context.
 * Supports a "multipartResolverBeanName" filter init-param in {@code web.xml};
 * the default bean name is "filterMultipartResolver".
 *
 * <p>If no MultipartResolver bean is found, this filter falls back to a default
 * MultipartResolver: {@link StandardServletMultipartResolver} for Servlet 3.0,
 * based on a multipart-config section in {@code web.xml}.
 * Note however that at present the Servlet specification only defines how to
 * enable multipart configuration on a Servlet and as a result multipart request
 * processing is likely not possible in a Filter unless the Servlet container
 * provides a workaround such as Tomcat's "allowCasualMultipartParsing" property.
 *
 * <p>MultipartResolver lookup is customizable: Override this filter's
 * {@code lookupMultipartResolver} method to use a custom MultipartResolver
 * instance, for example if not using a Spring web application context.
 * Note that the lookup method should not create a new MultipartResolver instance
 * for each call but rather return a reference to a pre-built instance.
 *
 * <p>Note: This filter is an <b>alternative</b> to using DispatcherServlet's
 * MultipartResolver support, for example for web applications with custom web views
 * which do not use Spring's web MVC, or for custom filters applied before a Spring MVC
 * DispatcherServlet (e.g. {@link org.springframework.web.filter.HiddenHttpMethodFilter}).
 * In any case, this filter should not be combined with servlet-specific multipart resolution.
 *
 * <p>
 *  Servlet过滤器,可通过根Web应用程序上下文中的{@link MultipartResolver}解析多部分请求
 * 
 * <p>在Spring的根Web应用程序环境中查找MultipartResolver在{@code webxml}中支持"multipartResolverBeanName"过滤器init-param;默
 * 认的bean名称为"filterMultipartResolver"。
 * 
 *  <p>如果没有找到MultipartResolver bean,则基于{@code webxml}中的multipart-config部分,此过滤器将返回到Servlet 30的默认Multipart
 * Resolver：{@link StandardServletMultipartResolver}。
 * 但是,目前仅限Servlet规范定义了如何在Servlet上启用多部分配置,因此除非Servlet容器提供了解决方法,例如Tomcat的"allowCasualMultipartParsing"属性,否
 * 则多部分请求处理可能无法在"筛选器"中执行。
 * 
 * <p> MultipartResolver查找是可自定义的：覆盖此过滤器的{@code lookupMultipartResolver}方法以使用自定义MultipartResolver实例,例如,如果
 * 不使用Spring Web应用程序上下文,请注意,查找方法不应为每个调用创建一个新的MultipartResolver实例,而是返回对预构建实例的引用。
 * 
 *  <p>注意：此过滤器是使用DispatcherServlet的MultipartResolver支持的<b>备选方案,例如对于不使用Spring Web MVC的自定义Web视图的Web应用程序,或在
 * Spring MVC DispatcherServlet之前应用的自定义过滤器(例如{@link orgspringframeworkwebfilterHiddenHttpMethodFilter})在
 * 任何情况下,此过滤器不应与特定于servlet的多部分解决方案组合。
 * 
 * 
 * @author Juergen Hoeller
 * @since 08.10.2003
 * @see #setMultipartResolverBeanName
 * @see #lookupMultipartResolver
 * @see org.springframework.web.multipart.MultipartResolver
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public class MultipartFilter extends OncePerRequestFilter {

	public static final String DEFAULT_MULTIPART_RESOLVER_BEAN_NAME = "filterMultipartResolver";

	private final MultipartResolver defaultMultipartResolver = new StandardServletMultipartResolver();

	private String multipartResolverBeanName = DEFAULT_MULTIPART_RESOLVER_BEAN_NAME;


	/**
	 * Set the bean name of the MultipartResolver to fetch from Spring's
	 * root application context. Default is "filterMultipartResolver".
	 * <p>
	 */
	public void setMultipartResolverBeanName(String multipartResolverBeanName) {
		this.multipartResolverBeanName = multipartResolverBeanName;
	}

	/**
	 * Return the bean name of the MultipartResolver to fetch from Spring's
	 * root application context.
	 * <p>
	 * 设置从Spring的根应用程序上下文获取的MultipartResolver的bean名称默认为"filterMultipartResolver"
	 * 
	 */
	protected String getMultipartResolverBeanName() {
		return this.multipartResolverBeanName;
	}


	/**
	 * Check for a multipart request via this filter's MultipartResolver,
	 * and wrap the original request with a MultipartHttpServletRequest if appropriate.
	 * <p>All later elements in the filter chain, most importantly servlets, benefit
	 * from proper parameter extraction in the multipart case, and are able to cast to
	 * MultipartHttpServletRequest if they need to.
	 * <p>
	 *  返回从Spring的根应用程序上下文获取的MultipartResolver的bean名称
	 * 
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		MultipartResolver multipartResolver = lookupMultipartResolver(request);

		HttpServletRequest processedRequest = request;
		if (multipartResolver.isMultipart(processedRequest)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Resolving multipart request [" + processedRequest.getRequestURI() +
						"] with MultipartFilter");
			}
			processedRequest = multipartResolver.resolveMultipart(processedRequest);
		}
		else {
			// A regular request...
			if (logger.isDebugEnabled()) {
				logger.debug("Request [" + processedRequest.getRequestURI() + "] is not a multipart request");
			}
		}

		try {
			filterChain.doFilter(processedRequest, response);
		}
		finally {
			if (processedRequest instanceof MultipartHttpServletRequest) {
				multipartResolver.cleanupMultipart((MultipartHttpServletRequest) processedRequest);
			}
		}
	}

	/**
	 * Look up the MultipartResolver that this filter should use,
	 * taking the current HTTP request as argument.
	 * <p>The default implementation delegates to the {@code lookupMultipartResolver}
	 * without arguments.
	 * <p>
	 *  通过此过滤器的MultipartResolver检查多部分请求,并使用MultipartHttpServletRequest将原始请求包装(如果适用)<p>过滤器链中的所有后续元素(最重要的是serv
	 * let)将受益于多部分案例中适当的参数提取,并且能够投射到MultipartHttpServletRequest,如果他们需要。
	 * 
	 * 
	 * @return the MultipartResolver to use
	 * @see #lookupMultipartResolver()
	 */
	protected MultipartResolver lookupMultipartResolver(HttpServletRequest request) {
		return lookupMultipartResolver();
	}

	/**
	 * Look for a MultipartResolver bean in the root web application context.
	 * Supports a "multipartResolverBeanName" filter init param; the default
	 * bean name is "filterMultipartResolver".
	 * <p>This can be overridden to use a custom MultipartResolver instance,
	 * for example if not using a Spring web application context.
	 * <p>
	 *  查找此过滤器应使用的MultipartResolver,以当前HTTP请求作为参数<p>默认实现委托给不带参数的{@code lookupMultipartResolver}
	 * 
	 * 
	 * @return the MultipartResolver instance, or {@code null} if none found
	 */
	protected MultipartResolver lookupMultipartResolver() {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		String beanName = getMultipartResolverBeanName();
		if (wac != null && wac.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Using MultipartResolver '" + beanName + "' for MultipartFilter");
			}
			return wac.getBean(beanName, MultipartResolver.class);
		}
		else {
			return this.defaultMultipartResolver;
		}
	}

}
