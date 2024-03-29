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

package org.springframework.web.servlet.mvc;

import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring Controller implementation that wraps a servlet instance which it manages
 * internally. Such a wrapped servlet is not known outside of this controller;
 * its entire lifecycle is covered here (in contrast to {@link ServletForwardingController}).
 *
 * <p>Useful to invoke an existing servlet via Spring's dispatching infrastructure,
 * for example to apply Spring HandlerInterceptors to its requests.
 *
 * <p>Note that Struts has a special requirement in that it parses {@code web.xml}
 * to find its servlet mapping. Therefore, you need to specify the DispatcherServlet's
 * servlet name as "servletName" on this controller, so that Struts finds the
 * DispatcherServlet's mapping (thinking that it refers to the ActionServlet).
 *
 * <p><b>Example:</b> a DispatcherServlet XML context, forwarding "*.do" to the Struts
 * ActionServlet wrapped by a ServletWrappingController. All such requests will go
 * through the configured HandlerInterceptor chain (e.g. an OpenSessionInViewInterceptor).
 * From the Struts point of view, everything will work as usual.
 *
 * <pre class="code">
 * &lt;bean id="urlMapping" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping"&gt;
 *   &lt;property name="interceptors"&gt;
 *     &lt;list&gt;
 *       &lt;ref bean="openSessionInViewInterceptor"/&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 *   &lt;property name="mappings"&gt;
 *     &lt;props&gt;
 *       &lt;prop key="*.do"&gt;strutsWrappingController&lt;/prop&gt;
 *     &lt;/props&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="strutsWrappingController" class="org.springframework.web.servlet.mvc.ServletWrappingController"&gt;
 *   &lt;property name="servletClass"&gt;
 *     &lt;value&gt;org.apache.struts.action.ActionServlet&lt;/value&gt;
 *   &lt;/property&gt;
 *   &lt;property name="servletName"&gt;
 *     &lt;value&gt;action&lt;/value&gt;
 *   &lt;/property&gt;
 *   &lt;property name="initParameters"&gt;
 *     &lt;props&gt;
 *       &lt;prop key="config"&gt;/WEB-INF/struts-config.xml&lt;/prop&gt;
 *     &lt;/props&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * <p>
 * Spring控制器实现,它将内部管理的servlet实例包装起来这个封装的servlet在这个控制器之外是不知道的;它的整个生命周期在这里被覆盖(与{@link ServletForwardingController}
 * 相反)。
 * 
 *  <p>通过Spring调度基础架构调用现有的servlet很有用,例如将Spring HandlerInterceptors应用于其请求
 * 
 *  <p>请注意,Struts有一个特殊要求,它解析{@code webxml}以查找其servlet映射因此,您需要在此控制器上指定DispatcherServlet的servlet名称为"servle
 * tName",以便Struts找到DispatcherServlet的映射(认为它是指ActionServlet)。
 * 
 * 例如：</b>一个DispatcherServlet XML上下文,将"* do"转发到由ServletWrappingController包装的Struts ActionServlet中所有这些请求将
 * 通过配置的HandlerInterceptor链(例如OpenSessionInViewInterceptor)从Struts的一切都会像往常一样工作。
 * 
 * <pre class="code">
 *  &lt; bean id ="urlMapping"class ="orgspringframeworkwebservlethandlerSimpleUrlHandlerMapping"&gt; &l
 * t; property name ="interceptors"&gt; &LT;列表&gt; &lt; ref bean ="openSessionInViewInterceptor"/&gt; &L
 * T; /列表&gt; &LT; /性&gt; &lt; property name ="mappings"&gt; &LT;道具&GT; &lt; prop key ="* do"&gt; struts
 * WrappingController&lt; / prop&gt; &LT; /道具&GT; &LT; /性&gt; &LT; /豆腐&GT;。
 * 
 * &lt; bean id ="strutsWrappingController"class ="orgspringframeworkwebservletmvcServletWrappingControl
 * ler"&gt; &lt; property name ="servletClass"&gt; &LT;值GT; orgapachestrutsactionActionServlet&LT; /值GT;
 *  &LT; /性&gt; &lt; property name ="servletName"&gt; &LT;值GT;动作&LT; /值GT; &LT; /性&gt; &lt; property nam
 * e ="initParameters"&gt; &LT;道具&GT; &lt; prop key ="config"&gt; / WEB-INF / struts-configxml&lt; / pro
 * p&gt; &LT; /道具&GT; &LT; /性&gt; &LT; /豆腐&GT; </PRE>。
 * 
 * @author Juergen Hoeller
 * @since 1.1.1
 * @see ServletForwardingController
 */
public class ServletWrappingController extends AbstractController
		implements BeanNameAware, InitializingBean, DisposableBean {

	private Class<? extends Servlet> servletClass;

	private String servletName;

	private Properties initParameters = new Properties();

	private String beanName;

	private Servlet servletInstance;


	public ServletWrappingController() {
		super(false);
	}


	/**
	 * Set the class of the servlet to wrap.
	 * Needs to implement {@code javax.servlet.Servlet}.
	 * <p>
	 * 
	 * 
	 * @see javax.servlet.Servlet
	 */
	public void setServletClass(Class<? extends Servlet> servletClass) {
		this.servletClass = servletClass;
	}

	/**
	 * Set the name of the servlet to wrap.
	 * Default is the bean name of this controller.
	 * <p>
	 *  设置servlet的类来封装需要实现{@code javaxservletServlet}
	 * 
	 */
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	/**
	 * Specify init parameters for the servlet to wrap,
	 * as name-value pairs.
	 * <p>
	 *  设置servlet的名称为wrap默认是这个控制器的bean名称
	 * 
	 */
	public void setInitParameters(Properties initParameters) {
		this.initParameters = initParameters;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}


	/**
	 * Initialize the wrapped Servlet instance.
	 * <p>
	 *  为servlet指定初始化参数,作为名称值对
	 * 
	 * 
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.servletClass == null) {
			throw new IllegalArgumentException("'servletClass' is required");
		}
		if (this.servletName == null) {
			this.servletName = this.beanName;
		}
		this.servletInstance = this.servletClass.newInstance();
		this.servletInstance.init(new DelegatingServletConfig());
	}


	/**
	 * Invoke the wrapped Servlet instance.
	 * <p>
	 *  初始化包裹的Servlet实例
	 * 
	 * 
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		this.servletInstance.service(request, response);
		return null;
	}


	/**
	 * Destroy the wrapped Servlet instance.
	 * <p>
	 * 调用包装的Servlet实例
	 * 
	 * 
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
		this.servletInstance.destroy();
	}


	/**
	 * Internal implementation of the ServletConfig interface, to be passed
	 * to the wrapped servlet. Delegates to ServletWrappingController fields
	 * and methods to provide init parameters and other environment info.
	 * <p>
	 *  销毁包装的Servlet实例
	 * 
	 */
	private class DelegatingServletConfig implements ServletConfig {

		@Override
		public String getServletName() {
			return servletName;
		}

		@Override
		public ServletContext getServletContext() {
			return ServletWrappingController.this.getServletContext();
		}

		@Override
		public String getInitParameter(String paramName) {
			return initParameters.getProperty(paramName);
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Enumeration<String> getInitParameterNames() {
			return (Enumeration) initParameters.keys();
		}
	}

}
