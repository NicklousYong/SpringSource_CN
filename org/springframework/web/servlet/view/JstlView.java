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

package org.springframework.web.servlet.view;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;

/**
 * Specialization of {@link InternalResourceView} for JSTL pages,
 * i.e. JSP pages that use the JSP Standard Tag Library.
 *
 * <p>Exposes JSTL-specific request attributes specifying locale
 * and resource bundle for JSTL's formatting and message tags,
 * using Spring's locale and {@link org.springframework.context.MessageSource}.
 *
 * <p>Typical usage with {@link InternalResourceViewResolver} would look as follows,
 * from the perspective of the DispatcherServlet context definition:
 *
 * <pre class="code">
 * &lt;bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver"&gt;
 *   &lt;property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/&gt;
 *   &lt;property name="prefix" value="/WEB-INF/jsp/"/&gt;
 *   &lt;property name="suffix" value=".jsp"/&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource"&gt;
 *   &lt;property name="basename" value="messages"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * Every view name returned from a handler will be translated to a JSP
 * resource (for example: "myView" -> "/WEB-INF/jsp/myView.jsp"), using
 * this view class to enable explicit JSTL support.
 *
 * <p>The specified MessageSource loads messages from "messages.properties" etc
 * files in the class path. This will automatically be exposed to views as
 * JSTL localization context, which the JSTL fmt tags (message etc) will use.
 * Consider using Spring's ReloadableResourceBundleMessageSource instead of
 * the standard ResourceBundleMessageSource for more sophistication.
 * Of course, any other Spring components can share the same MessageSource.
 *
 * <p>This is a separate class mainly to avoid JSTL dependencies in
 * {@link InternalResourceView} itself. JSTL has not been part of standard
 * J2EE up until J2EE 1.4, so we can't assume the JSTL API jar to be
 * available on the class path.
 *
 * <p>Hint: Set the {@link #setExposeContextBeansAsAttributes} flag to "true"
 * in order to make all Spring beans in the application context accessible
 * within JSTL expressions (e.g. in a {@code c:out} value expression).
 * This will also make all such beans accessible in plain {@code ${...}}
 * expressions in a JSP 2.0 page.
 *
 * <p>
 *  JSTL页面的{@link InternalResourceView}的专业化,即使用JSP标准标签库的JSP页面
 * 
 * <p>使用Spring的语言环境和{@link orgspringframeworkcontextMessageSource},为JSTL的格式和消息标签使用JSTL特定的请求属性来指定区域设置和资源包
 * 。
 * 
 *  <p>从DispatcherServlet上下文定义的角度来看,使用{@link InternalResourceViewResolver}的典型用法如下所示：
 * 
 * <pre class="code">
 *  &lt; bean id ="viewResolver"class ="orgspringframeworkwebservletviewInternalResourceViewResolver"&gt
 * ; &lt; property name ="viewClass"value ="orgspringframeworkwebservletviewJstlView"/&gt; &lt; property
 *  name ="prefix"value ="/ WEB-INF / jsp /"/&gt; &lt; property name ="suffix"value ="jsp"/&gt; &LT; /豆腐
 * &GT;。
 * 
 * &lt; bean id ="messageSource"class ="orgspringframeworkcontextsupportResourceBundleMessageSource"&gt;
 *  &lt; property name ="basename"value ="messages"/&gt; &LT; /豆腐&GT; </PRE>。
 * 
 *  从处理程序返回的每个视图名称将被转换为JSP资源(例如："myView" - >"/ WEB-INF / jsp / myViewjsp"),使用此视图类启用显式JSTL支持
 * 
 *  <p>指定的MessageSource从类路径中的"messagesproperties"等文件中加载消息这将自动显示为JSTL本地化上下文的视图,JSTL fmt标签(消息等)将使用它考虑使用Spr
 * ing的ReloadableResourceBundleMessageSource而不是标准的Res​​ourceBundleMessageSource为了更复杂当然,任何其他的Spring组件都可以共
 * 享相同的MessageSource。
 * 
 * 
 * @author Juergen Hoeller
 * @since 27.02.2003
 * @see org.springframework.web.servlet.support.JstlUtils#exposeLocalizationContext
 * @see InternalResourceViewResolver
 * @see org.springframework.context.support.ResourceBundleMessageSource
 * @see org.springframework.context.support.ReloadableResourceBundleMessageSource
 */
public class JstlView extends InternalResourceView {

	private MessageSource messageSource;


	/**
	 * Constructor for use as a bean.
	 * <p>
	 * <p>这是一个单独的类,主要是为了避免JSTL依赖于{@link InternalResourceView}本身JSTL尚未成为标准J2EE的一部分,直到J2EE 14,所以我们不能假定JSTL API
	 *  jar在类路径上可用。
	 * 
	 *  提示：将{@link #setExposeContextBeansAsAttributes}标志设置为"true",以使应用程序上下文中的所有Spring bean都可以在JSTL表达式中访问(例如,
	 * 在{@code c：out}值表达式中)这也将使所有这些bean都可以在JSP 20页面的简单{@code $ {}}表达式中访问。
	 * 
	 * 
	 * @see #setUrl
	 */
	public JstlView() {
	}

	/**
	 * Create a new JstlView with the given URL.
	 * <p>
	 *  用作bean的构造方法
	 * 
	 * 
	 * @param url the URL to forward to
	 */
	public JstlView(String url) {
		super(url);
	}

	/**
	 * Create a new JstlView with the given URL.
	 * <p>
	 *  使用给定的URL创建一个新的JstlView
	 * 
	 * 
	 * @param url the URL to forward to
	 * @param messageSource the MessageSource to expose to JSTL tags
	 * (will be wrapped with a JSTL-aware MessageSource that is aware of JSTL's
	 * {@code javax.servlet.jsp.jstl.fmt.localizationContext} context-param)
	 * @see JstlUtils#getJstlAwareMessageSource
	 */
	public JstlView(String url, MessageSource messageSource) {
		this(url);
		this.messageSource = messageSource;
	}


	/**
	 * Wraps the MessageSource with a JSTL-aware MessageSource that is aware
	 * of JSTL's {@code javax.servlet.jsp.jstl.fmt.localizationContext}
	 * context-param.
	 * <p>
	 *  使用给定的URL创建一个新的JstlView
	 * 
	 * 
	 * @see JstlUtils#getJstlAwareMessageSource
	 */
	@Override
	protected void initServletContext(ServletContext servletContext) {
		if (this.messageSource != null) {
			this.messageSource = JstlUtils.getJstlAwareMessageSource(servletContext, this.messageSource);
		}
		super.initServletContext(servletContext);
	}

	/**
	 * Exposes a JSTL LocalizationContext for Spring's locale and MessageSource.
	 * <p>
	 * 使用感知JSTL的{@code javaxservletjspjstlfmtlocalizationContext}上下文参数的JSTL感知MessageSource包装MessageSource
	 * 
	 * 
	 * @see JstlUtils#exposeLocalizationContext
	 */
	@Override
	protected void exposeHelpers(HttpServletRequest request) throws Exception {
		if (this.messageSource != null) {
			JstlUtils.exposeLocalizationContext(request, this.messageSource);
		}
		else {
			JstlUtils.exposeLocalizationContext(new RequestContext(request, getServletContext()));
		}
	}

}
