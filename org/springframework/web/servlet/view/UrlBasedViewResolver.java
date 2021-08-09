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

package org.springframework.web.servlet.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.View;

/**
 * Simple implementation of the {@link org.springframework.web.servlet.ViewResolver}
 * interface, allowing for direct resolution of symbolic view names to URLs,
 * without explicit mapping definition. This is useful if your symbolic names
 * match the names of your view resources in a straightforward manner
 * (i.e. the symbolic name is the unique part of the resource's filename),
 * without the need for a dedicated mapping to be defined for each view.
 *
 * <p>Supports {@link AbstractUrlBasedView} subclasses like {@link InternalResourceView},
 * {@link org.springframework.web.servlet.view.velocity.VelocityView} and
 * {@link org.springframework.web.servlet.view.freemarker.FreeMarkerView}.
 * The view class for all views generated by this resolver can be specified
 * via the "viewClass" property.
 *
 * <p>View names can either be resource URLs themselves, or get augmented by a
 * specified prefix and/or suffix. Exporting an attribute that holds the
 * RequestContext to all views is explicitly supported.
 *
 * <p>Example: prefix="/WEB-INF/jsp/", suffix=".jsp", viewname="test" ->
 * "/WEB-INF/jsp/test.jsp"
 *
 * <p>As a special feature, redirect URLs can be specified via the "redirect:"
 * prefix. E.g.: "redirect:myAction.do" will trigger a redirect to the given
 * URL, rather than resolution as standard view name. This is typically used
 * for redirecting to a controller URL after finishing a form workflow.
 *
 * <p>Furthermore, forward URLs can be specified via the "forward:" prefix. E.g.:
 * "forward:myAction.do" will trigger a forward to the given URL, rather than
 * resolution as standard view name. This is typically used for controller URLs;
 * it is not supposed to be used for JSP URLs - use logical view names there.
 *
 * <p>Note: This class does not support localized resolution, i.e. resolving
 * a symbolic view name to different resources depending on the current locale.
 *
 * <p><b>Note:</b> When chaining ViewResolvers, a UrlBasedViewResolver will check whether
 * the {@link AbstractUrlBasedView#checkResource specified resource actually exists}.
 * However, with {@link InternalResourceView}, it is not generally possible to
 * determine the existence of the target resource upfront. In such a scenario,
 * a UrlBasedViewResolver will always return View for any given view name;
 * as a consequence, it should be configured as the last ViewResolver in the chain.
 *
 * <p>
 * 简单实现{@link orgspringframeworkwebservletViewResolver}界面,允许将符号视图名称直接解析为URL,而无需显式映射定义如果符号名称以直观的方式与视图资源的名
 * 称相匹配,那么这一点很有用(即符号名称为资源文件名的唯一部分),而不需要为每个视图定义专用映射。
 * 
 *  <p>支持{@link AbstractUrlBasedView}子类,如{@link InternalResourceView},{@link orgspringframeworkwebservletviewvelocityVelocityView}
 * 和{@link orgspringframeworkwebservletviewfreemarkerFreeMarkerView}此解析器生成的所有视图的视图类可以通过"viewClass"属性指定。
 * 
 * <p>查看名称可以是资源URL本身,也可以通过指定的前缀和/或后缀进行扩充显式支持将持有RequestContext的属性导出到所有视图
 * 
 * <p>Example: prefix="/WEB-INF/jsp/", suffix=".jsp", viewname="test" ->
 *  "/ WEB-INF / JSP / testjsp"
 * 
 *  <p>作为一个特殊功能,可以通过"redirect："前缀指定重定向网址,例如："redirect：myActiondo"将触发重定向到给定的URL,而不是作为标准视图名称的解析通常用于重定向到完成表
 * 单工作流程后的控制器URL。
 * 
 * 此外,转发URL可以通过"forward："前缀指定,例如："forward：myActiondo"将触发转发给给定的URL,而不是作为标准视图名称的解析。
 * 这通常用于控制器URL;它不应该用于JSP URL  - 在那里使用逻辑视图名称。
 * 
 *  <p>注意：此类不支持本地化解决方案,即根据当前语言环境将符号视图名称解析为不同的资源
 * 
 * <p> <b>注意：</b>当链接ViewResolvers时,UrlBasedViewResolver将检查{@link AbstractUrlBasedView#checkResource指定的资源是否实际存在}
 * 但是,通过{@link InternalResourceView},通常不可能确定预先存在目标资源在这种情况下,UrlBasedViewResolver将始终为任何给定的视图名称返回View;因此,应将
 * 其配置为链中的最后一个ViewResolver。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 13.12.2003
 * @see #setViewClass
 * @see #setPrefix
 * @see #setSuffix
 * @see #setRequestContextAttribute
 * @see #REDIRECT_URL_PREFIX
 * @see AbstractUrlBasedView
 * @see InternalResourceView
 * @see org.springframework.web.servlet.view.velocity.VelocityView
 * @see org.springframework.web.servlet.view.freemarker.FreeMarkerView
 */
public class UrlBasedViewResolver extends AbstractCachingViewResolver implements Ordered {

	/**
	 * Prefix for special view names that specify a redirect URL (usually
	 * to a controller after a form has been submitted and processed).
	 * Such view names will not be resolved in the configured default
	 * way but rather be treated as special shortcut.
	 * <p>
	 *  指定重定向网址的特殊视图名称的前缀(通常在表单提交和处理后通过控制器)此类视图名称不会以配置的默认方式解析,而应视为特殊快捷方式
	 * 
	 */
	public static final String REDIRECT_URL_PREFIX = "redirect:";

	/**
	 * Prefix for special view names that specify a forward URL (usually
	 * to a controller after a form has been submitted and processed).
	 * Such view names will not be resolved in the configured default
	 * way but rather be treated as special shortcut.
	 * <p>
	 * 指定转发URL的特殊视图名称的前缀(通常在表单提交和处理后通过控制器)此类视图名称将不会以配置的默认方式解析,而应视为特殊快捷方式
	 * 
	 */
	public static final String FORWARD_URL_PREFIX = "forward:";


	private Class<?> viewClass;

	private String prefix = "";

	private String suffix = "";

	private String contentType;

	private boolean redirectContextRelative = true;

	private boolean redirectHttp10Compatible = true;

	private String[] redirectHosts;

	private String requestContextAttribute;

	/** Map of static attributes, keyed by attribute name (String) */
	private final Map<String, Object> staticAttributes = new HashMap<String, Object>();

	private Boolean exposePathVariables;

	private Boolean exposeContextBeansAsAttributes;

	private String[] exposedContextBeanNames;

	private String[] viewNames;

	private int order = Integer.MAX_VALUE;


	/**
	 * Set the view class that should be used to create views.
	 * <p>
	 *  设置应用于创建视图的视图类
	 * 
	 * 
	 * @param viewClass class that is assignable to the required view class
	 * (by default, AbstractUrlBasedView)
	 * @see AbstractUrlBasedView
	 */
	public void setViewClass(Class<?> viewClass) {
		if (viewClass == null || !requiredViewClass().isAssignableFrom(viewClass)) {
			throw new IllegalArgumentException(
					"Given view class [" + (viewClass != null ? viewClass.getName() : null) +
					"] is not of type [" + requiredViewClass().getName() + "]");
		}
		this.viewClass = viewClass;
	}

	/**
	 * Return the view class to be used to create views.
	 * <p>
	 *  返回用于创建视图的视图类
	 * 
	 */
	protected Class<?> getViewClass() {
		return this.viewClass;
	}

	/**
	 * Return the required type of view for this resolver.
	 * This implementation returns AbstractUrlBasedView.
	 * <p>
	 *  为此解析器返回所需的视图类型此实现返回AbstractUrlBasedView
	 * 
	 * 
	 * @see AbstractUrlBasedView
	 */
	protected Class<?> requiredViewClass() {
		return AbstractUrlBasedView.class;
	}

	/**
	 * Set the prefix that gets prepended to view names when building a URL.
	 * <p>
	 *  设置前缀,以便在构建URL时查看名称
	 * 
	 */
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Return the prefix that gets prepended to view names when building a URL.
	 * <p>
	 *  返回前缀,以便在构建URL时查看名称
	 * 
	 */
	protected String getPrefix() {
		return this.prefix;
	}

	/**
	 * Set the suffix that gets appended to view names when building a URL.
	 * <p>
	 *  设置附加后缀以在构建URL时查看名称
	 * 
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	/**
	 * Return the suffix that gets appended to view names when building a URL.
	 * <p>
	 *  返回附加的后缀以在构建URL时查看名称
	 * 
	 */
	protected String getSuffix() {
		return this.suffix;
	}

	/**
	 * Set the content type for all views.
	 * <p>May be ignored by view classes if the view itself is assumed
	 * to set the content type, e.g. in case of JSPs.
	 * <p>
	 * 设置所有视图的内容类型<p>如果假定视图本身设置内容类型,则视图类可能会被忽略,例如在JSP的情况下
	 * 
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Return the content type for all views, if any.
	 * <p>
	 *  返回所有视图的内容类型(如果有)
	 * 
	 */
	protected String getContentType() {
		return this.contentType;
	}

	/**
	 * Set whether to interpret a given redirect URL that starts with a
	 * slash ("/") as relative to the current ServletContext, i.e. as
	 * relative to the web application root.
	 * <p>Default is "true": A redirect URL that starts with a slash will be
	 * interpreted as relative to the web application root, i.e. the context
	 * path will be prepended to the URL.
	 * <p><b>Redirect URLs can be specified via the "redirect:" prefix.</b>
	 * E.g.: "redirect:myAction.do"
	 * <p>
	 *  设置是否解释以斜杠("/")开头的给定的重定向URL相对于当前的ServletContext,即相对于Web应用程序根目录<p>默认值为"true"：以斜杠开头的重定向网址将被解释为相对于Web应用程
	 * 序根,即上下文路径将被添加到URL <p> <b>重定向URL可以通过"redirect："前缀指定</b>例如："redirect：myActiondo"。
	 * 
	 * 
	 * @see RedirectView#setContextRelative
	 * @see #REDIRECT_URL_PREFIX
	 */
	public void setRedirectContextRelative(boolean redirectContextRelative) {
		this.redirectContextRelative = redirectContextRelative;
	}

	/**
	 * Return whether to interpret a given redirect URL that starts with a
	 * slash ("/") as relative to the current ServletContext, i.e. as
	 * relative to the web application root.
	 * <p>
	 * 返回是否解释以斜杠("/")开头的给定的重定向URL相对于当前的ServletContext,即相对于Web应用程序根
	 * 
	 */
	protected boolean isRedirectContextRelative() {
		return this.redirectContextRelative;
	}

	/**
	 * Set whether redirects should stay compatible with HTTP 1.0 clients.
	 * <p>In the default implementation, this will enforce HTTP status code 302
	 * in any case, i.e. delegate to {@code HttpServletResponse.sendRedirect}.
	 * Turning this off will send HTTP status code 303, which is the correct
	 * code for HTTP 1.1 clients, but not understood by HTTP 1.0 clients.
	 * <p>Many HTTP 1.1 clients treat 302 just like 303, not making any
	 * difference. However, some clients depend on 303 when redirecting
	 * after a POST request; turn this flag off in such a scenario.
	 * <p><b>Redirect URLs can be specified via the "redirect:" prefix.</b>
	 * E.g.: "redirect:myAction.do"
	 * <p>
	 * 设置重定向是否应该与HTTP 10客户端保持兼容<p>在默认实现中,这将在任何情况下强制执行HTTP状态代码302,即委托给{@code HttpServletResponsesendRedirect}
	 * 关闭将发送HTTP状态代码303,这是HTTP 11客户端的正确代码,但不被HTTP 10客户端所理解。
	 * 许多HTTP 11客户端对待302就像303一样,没有任何区别但是,一些客户端在POST请求之后重定向时依赖于303;在这种情况下关闭此标志<p> <b>重定向网址可以通过"redirect："前缀指定
	 * </b>例如："redirect：myActiondo"。
	 * 
	 * 
	 * @see RedirectView#setHttp10Compatible
	 * @see #REDIRECT_URL_PREFIX
	 */
	public void setRedirectHttp10Compatible(boolean redirectHttp10Compatible) {
		this.redirectHttp10Compatible = redirectHttp10Compatible;
	}

	/**
	 * Return whether redirects should stay compatible with HTTP 1.0 clients.
	 * <p>
	 *  返回重定向是否应与HTTP 10客户端保持兼容
	 * 
	 */
	protected boolean isRedirectHttp10Compatible() {
		return this.redirectHttp10Compatible;
	}

	/**
	 * Configure one or more hosts associated with the application.
	 * All other hosts will be considered external hosts.
	 * <p>In effect, this property provides a way turn off encoding on redirect
	 * via {@link HttpServletResponse#encodeRedirectURL} for URLs that have a
	 * host and that host is not listed as a known host.
	 * <p>If not set (the default) all URLs are encoded through the response.
	 * <p>
	 * 配置与应用程序关联的一个或多个主机所有其他主机将被视为外部主机<p>实际上,此属性提供了通过{@link HttpServletResponse#encodeRedirectURL}重定向的方式关闭具有
	 * 主机和主机的URL的方式未列为已知主机<p>如果未设置(默认),则所有URL都将通过响应进行编码。
	 * 
	 * 
	 * @param redirectHosts one or more application hosts
	 * @since 4.3
	 */
	public void setRedirectHosts(String... redirectHosts) {
		this.redirectHosts = redirectHosts;
	}

	/**
	 * Return the configured application hosts for redirect purposes.
	 * <p>
	 *  返回配置的应用程序主机以进行重定向
	 * 
	 * 
	 * @since 4.3
	 */
	public String[] getRedirectHosts() {
		return this.redirectHosts;
	}

	/**
	 * Set the name of the RequestContext attribute for all views.
	 * <p>
	 *  为所有视图设置RequestContext属性的名称
	 * 
	 * 
	 * @param requestContextAttribute name of the RequestContext attribute
	 * @see AbstractView#setRequestContextAttribute
	 */
	public void setRequestContextAttribute(String requestContextAttribute) {
		this.requestContextAttribute = requestContextAttribute;
	}

	/**
	 * Return the name of the RequestContext attribute for all views, if any.
	 * <p>
	 *  返回所有视图的RequestContext属性的名称(如果有)
	 * 
	 */
	protected String getRequestContextAttribute() {
		return this.requestContextAttribute;
	}

	/**
	 * Set static attributes from a {@code java.util.Properties} object,
	 * for all views returned by this resolver.
	 * <p>This is the most convenient way to set static attributes. Note that
	 * static attributes can be overridden by dynamic attributes, if a value
	 * with the same name is included in the model.
	 * <p>Can be populated with a String "value" (parsed via PropertiesEditor)
	 * or a "props" element in XML bean definitions.
	 * <p>
	 * 从{@code javautilProperties}对象设置静态属性,对于此解析器返回的所有视图<p>这是设置静态属性最方便的方法注意,静态属性可以被动态属性覆盖,如果具有相同名称的值包含在模型中<p>
	 * 可以填充一个String"value"(通过PropertiesEditor解析)或XML bean定义中的"props"元素。
	 * 
	 * 
	 * @see org.springframework.beans.propertyeditors.PropertiesEditor
	 * @see AbstractView#setAttributes
	 */
	public void setAttributes(Properties props) {
		CollectionUtils.mergePropertiesIntoMap(props, this.staticAttributes);
	}

	/**
	 * Set static attributes from a Map, for all views returned by this resolver.
	 * This allows to set any kind of attribute values, for example bean references.
	 * <p>Can be populated with a "map" or "props" element in XML bean definitions.
	 * <p>
	 *  从Map的所有视图设置静态属性此允许设置任何种类的属性值,例如bean引用<p>可以在XML bean定义中填充"map"或"props"元素
	 * 
	 * 
	 * @param attributes Map with name Strings as keys and attribute objects as values
	 * @see AbstractView#setAttributesMap
	 */
	public void setAttributesMap(Map<String, ?> attributes) {
		if (attributes != null) {
			this.staticAttributes.putAll(attributes);
		}
	}

	/**
	 * Allow Map access to the static attributes for views returned by
	 * this resolver, with the option to add or override specific entries.
	 * <p>Useful for specifying entries directly, for example via
	 * "attributesMap[myKey]". This is particularly useful for
	 * adding or overriding entries in child view definitions.
	 * <p>
	 * 允许映射访问此解析器返回的视图的静态属性,并添加或覆盖特定条目<p>可用于直接指定条目,例如通过"attributesMap [myKey]"这对添加或覆盖条目非常有用在子视图定义
	 * 
	 */
	public Map<String, Object> getAttributesMap() {
		return this.staticAttributes;
	}

	/**
	 * Specify whether views resolved by this resolver should add path variables to the model or not.
	 * <p>>The default setting is to let each View decide (see {@link AbstractView#setExposePathVariables}.
	 * However, you can use this property to override that.
	 * <p>
	 *  指定此解析器解析的视图是否应该向模型添加路径变量<p >>默认设置是让每个View决定(参见{@link AbstractView#setExposePathVariables})但是,您可以使用此属
	 * 性来覆盖。
	 * 
	 * 
	 * @param exposePathVariables
	 * <ul>
	 * <li>{@code true} - all Views resolved by this resolver will expose path variables
	 * <li>{@code false} - no Views resolved by this resolver will expose path variables
	 * <li>{@code null} - individual Views can decide for themselves (this is used by the default)
	 * <ul>
	 * @see AbstractView#setExposePathVariables
	 */
	public void setExposePathVariables(Boolean exposePathVariables) {
		this.exposePathVariables = exposePathVariables;
	}

	/**
	 * Return whether views resolved by this resolver should add path variables to the model or not.
	 * <p>
	 *  返回此解析器解析的视图是否应该向模型添加路径变量
	 * 
	 */
	protected Boolean getExposePathVariables() {
		return this.exposePathVariables;
	}

	/**
	 * Set whether to make all Spring beans in the application context accessible
	 * as request attributes, through lazy checking once an attribute gets accessed.
	 * <p>This will make all such beans accessible in plain {@code ${...}}
	 * expressions in a JSP 2.0 page, as well as in JSTL's {@code c:out}
	 * value expressions.
	 * <p>Default is "false".
	 * <p>
	 * 设置是否使应用程序上下文中的所有Spring bean都可以作为请求属性访问,通过一次访问属性进行懒惰检查<p>这将使所有这些bean都可以在JSP 20页面的简单{@code $ {}}表达式中访问,
	 * 以及JSTL的{@code c：out}值表达式<p>默认值为"false"。
	 * 
	 * 
	 * @see AbstractView#setExposeContextBeansAsAttributes
	 */
	public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
		this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
	}

	protected Boolean getExposeContextBeansAsAttributes() {
		return this.exposeContextBeansAsAttributes;
	}

	/**
	 * Specify the names of beans in the context which are supposed to be exposed.
	 * If this is non-null, only the specified beans are eligible for exposure as
	 * attributes.
	 * <p>
	 *  指定应该公开的上下文中的bean的名称如果这是非空值,则只有指定的bean才有资格作为属性
	 * 
	 * 
	 * @see AbstractView#setExposedContextBeanNames
	 */
	public void setExposedContextBeanNames(String... exposedContextBeanNames) {
		this.exposedContextBeanNames = exposedContextBeanNames;
	}

	protected String[] getExposedContextBeanNames() {
		return this.exposedContextBeanNames;
	}

	/**
	 * Set the view names (or name patterns) that can be handled by this
	 * {@link org.springframework.web.servlet.ViewResolver}. View names can contain
	 * simple wildcards such that 'my*', '*Report' and '*Repo*' will all match the
	 * view name 'myReport'.
	 * <p>
	 *  设置可以由此{@link orgspringframeworkwebservletViewResolver}处理的视图名称(或名称模式)查看名称可以包含简单的通配符,使得'我的*','*报告'和'* 
	 * Repo *'都将匹配视图名称' myReport"。
	 * 
	 * 
	 * @see #canHandle
	 */
	public void setViewNames(String... viewNames) {
		this.viewNames = viewNames;
	}

	/**
	 * Return the view names (or name patterns) that can be handled by this
	 * {@link org.springframework.web.servlet.ViewResolver}.
	 * <p>
	 * 返回可以由此{@link orgspringframeworkwebservletViewResolver}处理的视图名称(或名称模式)
	 * 
	 */
	protected String[] getViewNames() {
		return this.viewNames;
	}

	/**
	 * Set the order in which this {@link org.springframework.web.servlet.ViewResolver}
	 * is evaluated.
	 * <p>
	 *  设置此{@link orgspringframeworkwebservletViewResolver}的评估顺序
	 * 
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * Return the order in which this {@link org.springframework.web.servlet.ViewResolver}
	 * is evaluated.
	 * <p>
	 *  返回此{@link orgspringframeworkwebservletViewResolver}的评估顺序
	 * 
	 */
	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	protected void initApplicationContext() {
		super.initApplicationContext();
		if (getViewClass() == null) {
			throw new IllegalArgumentException("Property 'viewClass' is required");
		}
	}


	/**
	 * This implementation returns just the view name,
	 * as this ViewResolver doesn't support localized resolution.
	 * <p>
	 *  此实现只返回视图名称,因为ViewResolver不支持本地化解决方案
	 * 
	 */
	@Override
	protected Object getCacheKey(String viewName, Locale locale) {
		return viewName;
	}

	/**
	 * Overridden to implement check for "redirect:" prefix.
	 * <p>Not possible in {@code loadView}, since overridden
	 * {@code loadView} versions in subclasses might rely on the
	 * superclass always creating instances of the required view class.
	 * <p>
	 *  被覆盖执行检查"redirect："前缀<p>在{@code loadView}中不可能,因为子类中的重载{@code loadView}版本可能依赖于超类,始终创建所需视图类的实例
	 * 
	 * 
	 * @see #loadView
	 * @see #requiredViewClass
	 */
	@Override
	protected View createView(String viewName, Locale locale) throws Exception {
		// If this resolver is not supposed to handle the given view,
		// return null to pass on to the next resolver in the chain.
		if (!canHandle(viewName, locale)) {
			return null;
		}
		// Check for special "redirect:" prefix.
		if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
			String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
			RedirectView view = new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
			view.setHosts(getRedirectHosts());
			return applyLifecycleMethods(viewName, view);
		}
		// Check for special "forward:" prefix.
		if (viewName.startsWith(FORWARD_URL_PREFIX)) {
			String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length());
			return new InternalResourceView(forwardUrl);
		}
		// Else fall back to superclass implementation: calling loadView.
		return super.createView(viewName, locale);
	}

	/**
	 * Indicates whether or not this {@link org.springframework.web.servlet.ViewResolver} can
	 * handle the supplied view name. If not, {@link #createView(String, java.util.Locale)} will
	 * return {@code null}. The default implementation checks against the configured
	 * {@link #setViewNames view names}.
	 * <p>
	 * 指示此{@link orgspringframeworkwebservletViewResolver}是否可以处理提供的视图名称如果不是,{@link #createView(String,javautilLocale)}
	 * 将返回{@code null}默认实现针对配置的{@link #setViewNames视图名}。
	 * 
	 * 
	 * @param viewName the name of the view to retrieve
	 * @param locale the Locale to retrieve the view for
	 * @return whether this resolver applies to the specified view
	 * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
	 */
	protected boolean canHandle(String viewName, Locale locale) {
		String[] viewNames = getViewNames();
		return (viewNames == null || PatternMatchUtils.simpleMatch(viewNames, viewName));
	}

	/**
	 * Delegates to {@code buildView} for creating a new instance of the
	 * specified view class, and applies the following Spring lifecycle methods
	 * (as supported by the generic Spring bean factory):
	 * <ul>
	 * <li>ApplicationContextAware's {@code setApplicationContext}
	 * <li>InitializingBean's {@code afterPropertiesSet}
	 * </ul>
	 * <p>
	 *  委托{@code buildView}创建指定视图类的新实例,并应用以下Spring生命周期方法(由通用的Spring bean工厂支持)：
	 * <ul>
	 *  <li> ApplicationContextAware的{@code setApplicationContext} <li> InitializingBean的{@code afterPropertiesSet}
	 * 。
	 * </ul>
	 * 
	 * @param viewName the name of the view to retrieve
	 * @return the View instance
	 * @throws Exception if the view couldn't be resolved
	 * @see #buildView(String)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		AbstractUrlBasedView view = buildView(viewName);
		View result = applyLifecycleMethods(viewName, view);
		return (view.checkResource(locale) ? result : null);
	}

	private View applyLifecycleMethods(String viewName, AbstractView view) {
		return (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
	}

	/**
	 * Creates a new View instance of the specified view class and configures it.
	 * Does <i>not</i> perform any lookup for pre-defined View instances.
	 * <p>Spring lifecycle methods as defined by the bean container do not have to
	 * be called here; those will be applied by the {@code loadView} method
	 * after this method returns.
	 * <p>Subclasses will typically call {@code super.buildView(viewName)}
	 * first, before setting further properties themselves. {@code loadView}
	 * will then apply Spring lifecycle methods at the end of this process.
	 * <p>
	 * 
	 * @param viewName the name of the view to build
	 * @return the View instance
	 * @throws Exception if the view couldn't be resolved
	 * @see #loadView(String, java.util.Locale)
	 */
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		AbstractUrlBasedView view = (AbstractUrlBasedView) BeanUtils.instantiateClass(getViewClass());
		view.setUrl(getPrefix() + viewName + getSuffix());

		String contentType = getContentType();
		if (contentType != null) {
			view.setContentType(contentType);
		}

		view.setRequestContextAttribute(getRequestContextAttribute());
		view.setAttributesMap(getAttributesMap());

		Boolean exposePathVariables = getExposePathVariables();
		if (exposePathVariables != null) {
			view.setExposePathVariables(exposePathVariables);
		}
		Boolean exposeContextBeansAsAttributes = getExposeContextBeansAsAttributes();
		if (exposeContextBeansAsAttributes != null) {
			view.setExposeContextBeansAsAttributes(exposeContextBeansAsAttributes);
		}
		String[] exposedContextBeanNames = getExposedContextBeanNames();
		if (exposedContextBeanNames != null) {
			view.setExposedContextBeanNames(exposedContextBeanNames);
		}

		return view;
	}

}
