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

package org.springframework.web.servlet.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.ContextExposingHttpServletRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;

/**
 * Abstract base class for {@link org.springframework.web.servlet.View}
 * implementations. Subclasses should be JavaBeans, to allow for
 * convenient configuration as Spring-managed bean instances.
 *
 * <p>Provides support for static attributes, to be made available to the view,
 * with a variety of ways to specify them. Static attributes will be merged
 * with the given dynamic attributes (the model that the controller returned)
 * for each render operation.
 *
 * <p>Extends {@link WebApplicationObjectSupport}, which will be helpful to
 * some views. Subclasses just need to implement the actual rendering.
 *
 * <p>
 *  {@link orgspringframeworkwebservletView}实现的抽象基类子类应为JavaBean,以便方便的配置为Spring管理的bean实例
 * 
 * <p>为静态属性提供支持,可通过多种方式指定静态属性静态属性将与给定的动态属性(控制器返回的模型)合并为每个渲染操作
 * 
 *  <p>扩展{@link WebApplicationObjectSupport},这将有助于一些视图子类只需要实现实际呈现
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setAttributes
 * @see #setAttributesMap
 * @see #renderMergedOutputModel
 */
public abstract class AbstractView extends WebApplicationObjectSupport implements View, BeanNameAware {

	/** Default content type. Overridable as bean property. */
	public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";

	/** Initial size for the temporary output byte array (if any) */
	private static final int OUTPUT_BYTE_ARRAY_INITIAL_SIZE = 4096;


	private String beanName;

	private String contentType = DEFAULT_CONTENT_TYPE;

	private String requestContextAttribute;

	private final Map<String, Object> staticAttributes = new LinkedHashMap<String, Object>();

	private boolean exposePathVariables = true;

	private boolean exposeContextBeansAsAttributes = false;

	private Set<String> exposedContextBeanNames;


	/**
	 * Set the view's name. Helpful for traceability.
	 * <p>Framework code must call this when constructing views.
	 * <p>
	 *  设置视图的名称有助于可追溯性<p>框架代码在构建视图时必须调用此视图
	 * 
	 */
	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Return the view's name. Should never be {@code null},
	 * if the view was correctly configured.
	 * <p>
	 *  返回视图的名称如果视图配置正确,则不应该是{@code null}
	 * 
	 */
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Set the content type for this view.
	 * Default is "text/html;charset=ISO-8859-1".
	 * <p>May be ignored by subclasses if the view itself is assumed
	 * to set the content type, e.g. in case of JSPs.
	 * <p>
	 *  设置此视图的内容类型默认为"text / html; charset = ISO-8859-1"<p>如果假定视图本身设置内容类型,则可能会被子类忽略,例如在JSP的情况下
	 * 
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Return the content type for this view.
	 * <p>
	 * 返回此视图的内容类型
	 * 
	 */
	@Override
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Set the name of the RequestContext attribute for this view.
	 * Default is none.
	 * <p>
	 *  设置此视图的RequestContext属性的名称Default is none
	 * 
	 */
	public void setRequestContextAttribute(String requestContextAttribute) {
		this.requestContextAttribute = requestContextAttribute;
	}

	/**
	 * Return the name of the RequestContext attribute, if any.
	 * <p>
	 *  返回RequestContext属性的名称(如果有)
	 * 
	 */
	public String getRequestContextAttribute() {
		return this.requestContextAttribute;
	}

	/**
	 * Set static attributes as a CSV string.
	 * Format is: attname0={value1},attname1={value1}
	 * <p>"Static" attributes are fixed attributes that are specified in
	 * the View instance configuration. "Dynamic" attributes, on the other hand,
	 * are values passed in as part of the model.
	 * <p>
	 *  将静态属性设置为CSV字符串格式为：attname0 = {value1},attname1 = {value1} <p>"静态"属性是在View实例配置"动态"属性中指定的固定属性,另一方面作为模型
	 * 的一部分传递的值。
	 * 
	 */
	public void setAttributesCSV(String propString) throws IllegalArgumentException {
		if (propString != null) {
			StringTokenizer st = new StringTokenizer(propString, ",");
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				int eqIdx = tok.indexOf("=");
				if (eqIdx == -1) {
					throw new IllegalArgumentException("Expected = in attributes CSV string '" + propString + "'");
				}
				if (eqIdx >= tok.length() - 2) {
					throw new IllegalArgumentException(
							"At least 2 characters ([]) required in attributes CSV string '" + propString + "'");
				}
				String name = tok.substring(0, eqIdx);
				String value = tok.substring(eqIdx + 1);

				// Delete first and last characters of value: { and }
				value = value.substring(1);
				value = value.substring(0, value.length() - 1);

				addStaticAttribute(name, value);
			}
		}
	}

	/**
	 * Set static attributes for this view from a
	 * {@code java.util.Properties} object.
	 * <p>"Static" attributes are fixed attributes that are specified in
	 * the View instance configuration. "Dynamic" attributes, on the other hand,
	 * are values passed in as part of the model.
	 * <p>This is the most convenient way to set static attributes. Note that
	 * static attributes can be overridden by dynamic attributes, if a value
	 * with the same name is included in the model.
	 * <p>Can be populated with a String "value" (parsed via PropertiesEditor)
	 * or a "props" element in XML bean definitions.
	 * <p>
	 * 从{@code javautilProperties}对象设置此视图的静态属性<p>"静态"属性是在View实例配置"动态"属性中指定的固定属性,另一方面是作为模型<p>这是设置静态属性的最方便的方法注
	 * 意,静态属性可以被动态属性覆盖,如果模型中包含相同名称的值<p>可以使用字符串"value"(通过PropertiesEditor解析)或XML bean定义中的"props"元素。
	 * 
	 * 
	 * @see org.springframework.beans.propertyeditors.PropertiesEditor
	 */
	public void setAttributes(Properties attributes) {
		CollectionUtils.mergePropertiesIntoMap(attributes, this.staticAttributes);
	}

	/**
	 * Set static attributes for this view from a Map. This allows to set
	 * any kind of attribute values, for example bean references.
	 * <p>"Static" attributes are fixed attributes that are specified in
	 * the View instance configuration. "Dynamic" attributes, on the other hand,
	 * are values passed in as part of the model.
	 * <p>Can be populated with a "map" or "props" element in XML bean definitions.
	 * <p>
	 * 从Map设置此视图的静态属性允许设置任何种类的属性值,例如bean引用<p>"静态"属性是在View实例配置"动态"属性中指定的固定属性,另一方面,是作为模型一部分传递的值<p>可以在XML bean定
	 * 义中填充"map"或"props"元素。
	 * 
	 * 
	 * @param attributes Map with name Strings as keys and attribute objects as values
	 */
	public void setAttributesMap(Map<String, ?> attributes) {
		if (attributes != null) {
			for (Map.Entry<String, ?> entry : attributes.entrySet()) {
				addStaticAttribute(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Allow Map access to the static attributes of this view,
	 * with the option to add or override specific entries.
	 * <p>Useful for specifying entries directly, for example via
	 * "attributesMap[myKey]". This is particularly useful for
	 * adding or overriding entries in child view definitions.
	 * <p>
	 *  允许映射访问此视图的静态属性,并添加或覆盖特定条目<p>可用于直接指定条目,例如通过"attributesMap [myKey]"这对于添加或覆盖子视图中的条目非常有用定义
	 * 
	 */
	public Map<String, Object> getAttributesMap() {
		return this.staticAttributes;
	}

	/**
	 * Add static data to this view, exposed in each view.
	 * <p>"Static" attributes are fixed attributes that are specified in
	 * the View instance configuration. "Dynamic" attributes, on the other hand,
	 * are values passed in as part of the model.
	 * <p>Must be invoked before any calls to {@code render}.
	 * <p>
	 * 将静态数据添加到此视图中,在每个视图中公开<p>"静态"属性是在View实例配置"Dynamic"属性中指定的固定属性,另一方面是作为模型<p >在任何调用{@code render}之前必须调用它
	 * 
	 * 
	 * @param name the name of the attribute to expose
	 * @param value the attribute value to expose
	 * @see #render
	 */
	public void addStaticAttribute(String name, Object value) {
		this.staticAttributes.put(name, value);
	}

	/**
	 * Return the static attributes for this view. Handy for testing.
	 * <p>Returns an unmodifiable Map, as this is not intended for
	 * manipulating the Map but rather just for checking the contents.
	 * <p>
	 *  返回此视图的静态属性方便测试<p>返回一个不可修改的Map,因为这不是为了操作Map而是用于检查内容
	 * 
	 * 
	 * @return the static attributes in this view
	 */
	public Map<String, Object> getStaticAttributes() {
		return Collections.unmodifiableMap(this.staticAttributes);
	}

	/**
	 * Specify whether to add path variables to the model or not.
	 * <p>Path variables are commonly bound to URI template variables through the {@code @PathVariable}
	 * annotation. They're are effectively URI template variables with type conversion applied to
	 * them to derive typed Object values. Such values are frequently needed in views for
	 * constructing links to the same and other URLs.
	 * <p>Path variables added to the model override static attributes (see {@link #setAttributes(Properties)})
	 * but not attributes already present in the model.
	 * <p>By default this flag is set to {@code true}. Concrete view types can override this.
	 * <p>
	 * 指定是否向模型添加路径变量<p>路径变量通常通过{@code @PathVariable}注释绑定到URI模板变量他们实际上是URI模板变量,其类型转换应用于它们以导出类型的对象值这些值通常用于构建到相
	 * 同URL和其他URL的链接的视图<p>添加到模型的路径变量覆盖静态属性(请参阅{@link #setAttributes(Properties)}),但不存在模型中的属性< p>默认情况下,此标志设置为
	 * {@code true}具体视图类型可以覆盖此。
	 * 
	 * 
	 * @param exposePathVariables {@code true} to expose path variables, and {@code false} otherwise
	 */
	public void setExposePathVariables(boolean exposePathVariables) {
		this.exposePathVariables = exposePathVariables;
	}

	/**
	 * Return whether to add path variables to the model or not.
	 * <p>
	 *  返回是否向模型添加路径变量
	 * 
	 */
	public boolean isExposePathVariables() {
		return this.exposePathVariables;
	}

	/**
	 * Set whether to make all Spring beans in the application context accessible
	 * as request attributes, through lazy checking once an attribute gets accessed.
	 * <p>This will make all such beans accessible in plain {@code ${...}}
	 * expressions in a JSP 2.0 page, as well as in JSTL's {@code c:out}
	 * value expressions.
	 * <p>Default is "false". Switch this flag on to transparently expose all
	 * Spring beans in the request attribute namespace.
	 * <p><b>NOTE:</b> Context beans will override any custom request or session
	 * attributes of the same name that have been manually added. However, model
	 * attributes (as explicitly exposed to this view) of the same name will
	 * always override context beans.
	 * <p>
	 * 设置是否使应用程序上下文中的所有Spring bean都可以作为请求属性访问,通过一次访问属性进行懒惰检查<p>这将使所有这些bean都可以在JSP 20页面的简单{@code $ {}}表达式中访问,
	 * 以及JSTL的{@code c：out}值表达式<p>默认值为"false"将此标志打开以透明地公开请求属性命名空间中的所有Spring bean <p> <b>注意：</b>上下文bean将覆盖手动添
	 * 加的同名的任何自定义请求或会话属性然而,同名的模型属性(显式暴露给此视图)将始终覆盖上下文bean。
	 * 
	 * 
	 * @see #getRequestToExpose
	 */
	public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
		this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
	}

	/**
	 * Specify the names of beans in the context which are supposed to be exposed.
	 * If this is non-null, only the specified beans are eligible for exposure as
	 * attributes.
	 * <p>If you'd like to expose all Spring beans in the application context, switch
	 * the {@link #setExposeContextBeansAsAttributes "exposeContextBeansAsAttributes"}
	 * flag on but do not list specific bean names for this property.
	 * <p>
	 * 指定应该暴露的上下文中的bean的名称如果这是非空值,则只有指定的bean可以作为属性<p>进行曝光。
	 * 如果要在应用程序上下文中公开所有Spring bean,请切换{@link #setExposeContextBeansAsAttributes"exposeContextBeansAsAttributes"}
	 * 标志,但不列出此属性的特定bean名称。
	 * 指定应该暴露的上下文中的bean的名称如果这是非空值,则只有指定的bean可以作为属性<p>进行曝光。
	 * 
	 */
	public void setExposedContextBeanNames(String... exposedContextBeanNames) {
		this.exposedContextBeanNames = new HashSet<String>(Arrays.asList(exposedContextBeanNames));
	}


	/**
	 * Prepares the view given the specified model, merging it with static
	 * attributes and a RequestContext attribute, if necessary.
	 * Delegates to renderMergedOutputModel for the actual rendering.
	 * <p>
	 *  准备给定指定模型的视图,将其与静态属性和RequestContext属性合并(如果需要)代理renderMergedOutputModel进行实际渲染
	 * 
	 * 
	 * @see #renderMergedOutputModel
	 */
	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("Rendering view with name '" + this.beanName + "' with model " + model +
				" and static attributes " + this.staticAttributes);
		}

		Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);
		prepareResponse(request, response);
		renderMergedOutputModel(mergedModel, getRequestToExpose(request), response);
	}

	/**
	 * Creates a combined output Map (never {@code null}) that includes dynamic values and static attributes.
	 * Dynamic values take precedence over static attributes.
	 * <p>
	 *  创建包含动态值和静态属性的组合输出Map(从不{@code null})动态值优先于静态属性
	 * 
	 */
	protected Map<String, Object> createMergedOutputModel(Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) {

		@SuppressWarnings("unchecked")
		Map<String, Object> pathVars = (this.exposePathVariables ?
				(Map<String, Object>) request.getAttribute(View.PATH_VARIABLES) : null);

		// Consolidate static and dynamic model attributes.
		int size = this.staticAttributes.size();
		size += (model != null ? model.size() : 0);
		size += (pathVars != null ? pathVars.size() : 0);

		Map<String, Object> mergedModel = new LinkedHashMap<String, Object>(size);
		mergedModel.putAll(this.staticAttributes);
		if (pathVars != null) {
			mergedModel.putAll(pathVars);
		}
		if (model != null) {
			mergedModel.putAll(model);
		}

		// Expose RequestContext?
		if (this.requestContextAttribute != null) {
			mergedModel.put(this.requestContextAttribute, createRequestContext(request, response, mergedModel));
		}

		return mergedModel;
	}

	/**
	 * Create a RequestContext to expose under the specified attribute name.
	 * <p>The default implementation creates a standard RequestContext instance for the
	 * given request and model. Can be overridden in subclasses for custom instances.
	 * <p>
	 * 创建一个RequestContext以在指定的属性名称下公开<p>默认实现为给定的请求和模型创建一个标准的RequestContext实例可以在子类中覆盖自定义实例
	 * 
	 * 
	 * @param request current HTTP request
	 * @param model combined output Map (never {@code null}),
	 * with dynamic values taking precedence over static attributes
	 * @return the RequestContext instance
	 * @see #setRequestContextAttribute
	 * @see org.springframework.web.servlet.support.RequestContext
	 */
	protected RequestContext createRequestContext(
			HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {

		return new RequestContext(request, response, getServletContext(), model);
	}

	/**
	 * Prepare the given response for rendering.
	 * <p>The default implementation applies a workaround for an IE bug
	 * when sending download content via HTTPS.
	 * <p>
	 *  为渲染准备给定的响应<p>当通过HTTPS发送下载内容时,默认实现应用于IE错误的解决方法
	 * 
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 */
	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
		if (generatesDownloadContent()) {
			response.setHeader("Pragma", "private");
			response.setHeader("Cache-Control", "private, must-revalidate");
		}
	}

	/**
	 * Return whether this view generates download content
	 * (typically binary content like PDF or Excel files).
	 * <p>The default implementation returns {@code false}. Subclasses are
	 * encouraged to return {@code true} here if they know that they are
	 * generating download content that requires temporary caching on the
	 * client side, typically via the response OutputStream.
	 * <p>
	 *  返回此视图是否生成下载内容(通常为PDF或Excel文件的二进制内容)<p>默认实现返回{@code false}如果子视图生成下载内容,则鼓励子类返回{@code true}通常需要通过响应Outp
	 * utStream在客户端进行临时缓存。
	 * 
	 * 
	 * @see #prepareResponse
	 * @see javax.servlet.http.HttpServletResponse#getOutputStream()
	 */
	protected boolean generatesDownloadContent() {
		return false;
	}

	/**
	 * Get the request handle to expose to {@link #renderMergedOutputModel}, i.e. to the view.
	 * <p>The default implementation wraps the original request for exposure of Spring beans
	 * as request attributes (if demanded).
	 * <p>
	 * 获取请求句柄以暴露给{@link #renderMergedOutputModel},即视图<p>默认实现将原始请求暴露给Spring bean作为请求属性(如果需要)
	 * 
	 * 
	 * @param originalRequest the original servlet request as provided by the engine
	 * @return the wrapped request, or the original request if no wrapping is necessary
	 * @see #setExposeContextBeansAsAttributes
	 * @see #setExposedContextBeanNames
	 * @see org.springframework.web.context.support.ContextExposingHttpServletRequest
	 */
	protected HttpServletRequest getRequestToExpose(HttpServletRequest originalRequest) {
		if (this.exposeContextBeansAsAttributes || this.exposedContextBeanNames != null) {
			return new ContextExposingHttpServletRequest(
					originalRequest, getWebApplicationContext(), this.exposedContextBeanNames);
		}
		return originalRequest;
	}

	/**
	 * Subclasses must implement this method to actually render the view.
	 * <p>The first step will be preparing the request: In the JSP case,
	 * this would mean setting model objects as request attributes.
	 * The second step will be the actual rendering of the view,
	 * for example including the JSP via a RequestDispatcher.
	 * <p>
	 *  子类必须实现此方法来实际呈现视图<p>第一步将准备请求：在JSP案例中,这意味着将模型对象设置为请求属性第二步将是视图的实际呈现,例如包括通过RequestDispatcher的JSP
	 * 
	 * 
	 * @param model combined output Map (never {@code null}),
	 * with dynamic values taking precedence over static attributes
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception if rendering failed
	 */
	protected abstract void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;


	/**
	 * Expose the model objects in the given map as request attributes.
	 * Names will be taken from the model Map.
	 * This method is suitable for all resources reachable by {@link javax.servlet.RequestDispatcher}.
	 * <p>
	 *  在给定地图中显示模型对象作为请求属性名称将从模型中获取Map该方法适用于{@link javaxservletRequestDispatcher}可访问的所有资源,
	 * 
	 * 
	 * @param model Map of model objects to expose
	 * @param request current HTTP request
	 */
	protected void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			String modelName = entry.getKey();
			Object modelValue = entry.getValue();
			if (modelValue != null) {
				request.setAttribute(modelName, modelValue);
				if (logger.isDebugEnabled()) {
					logger.debug("Added model object '" + modelName + "' of type [" + modelValue.getClass().getName() +
							"] to request in view with name '" + getBeanName() + "'");
				}
			}
			else {
				request.removeAttribute(modelName);
				if (logger.isDebugEnabled()) {
					logger.debug("Removed model object '" + modelName +
							"' from request in view with name '" + getBeanName() + "'");
				}
			}
		}
	}

	/**
	 * Create a temporary OutputStream for this view.
	 * <p>This is typically used as IE workaround, for setting the content length header
	 * from the temporary stream before actually writing the content to the HTTP response.
	 * <p>
	 * 为此视图创建一个临时OutputStream <p>这通常用作IE解决方法,用于在将内容实际写入HTTP响应之前从临时流中设置内容长度头
	 * 
	 */
	protected ByteArrayOutputStream createTemporaryOutputStream() {
		return new ByteArrayOutputStream(OUTPUT_BYTE_ARRAY_INITIAL_SIZE);
	}

	/**
	 * Write the given temporary OutputStream to the HTTP response.
	 * <p>
	 *  将给定的临时OutputStream写入HTTP响应
	 * 
	 * 
	 * @param response current HTTP response
	 * @param baos the temporary OutputStream to write
	 * @throws IOException if writing/flushing failed
	 */
	protected void writeToResponse(HttpServletResponse response, ByteArrayOutputStream baos) throws IOException {
		// Write content type and also length (determined via byte array).
		response.setContentType(getContentType());
		response.setContentLength(baos.size());

		// Flush byte array to servlet output stream.
		ServletOutputStream out = response.getOutputStream();
		baos.writeTo(out);
		out.flush();
	}

	/**
	 * Set the content type of the response to the configured
	 * {@link #setContentType(String) content type} unless the
	 * {@link View#SELECTED_CONTENT_TYPE} request attribute is present and set
	 * to a concrete media type.
	 * <p>
	 *  将响应的内容类型设置为配置的{@link #setContentType(String)内容类型},除非存在{@link View#SELECTED_CONTENT_TYPE}请求属性并设置为具体的媒
	 * 体类型。
	 */
	protected void setResponseContentType(HttpServletRequest request, HttpServletResponse response) {
		MediaType mediaType = (MediaType) request.getAttribute(View.SELECTED_CONTENT_TYPE);
		if (mediaType != null && mediaType.isConcrete()) {
			response.setContentType(mediaType.toString());
		}
		else {
			response.setContentType(getContentType());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		if (getBeanName() != null) {
			sb.append(": name '").append(getBeanName()).append("'");
		}
		else {
			sb.append(": unnamed");
		}
		return sb.toString();
	}

}
