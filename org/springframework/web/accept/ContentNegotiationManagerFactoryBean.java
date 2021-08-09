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

package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * Factory to create a {@code ContentNegotiationManager} and configure it with
 * one or more {@link ContentNegotiationStrategy} instances via simple setters.
 * The following table shows setters, resulting strategy instances, and if in
 * use by default:
 *
 * <table>
 * <tr>
 * <th>Property Setter</th>
 * <th>Underlying Strategy</th>
 * <th>Default Setting</th>
 * </tr>
 * <tr>
 * <td>{@link #setFavorPathExtension}</td>
 * <td>{@link PathExtensionContentNegotiationStrategy Path Extension strategy}</td>
 * <td>On</td>
 * </tr>
 * <tr>
 * <td>{@link #setFavorParameter favorParameter}</td>
 * <td>{@link ParameterContentNegotiationStrategy Parameter strategy}</td>
 * <td>Off</td>
 * </tr>
 * <tr>
 * <td>{@link #setIgnoreAcceptHeader ignoreAcceptHeader}</td>
 * <td>{@link HeaderContentNegotiationStrategy Header strategy}</td>
 * <td>On</td>
 * </tr>
 * <tr>
 * <td>{@link #setDefaultContentType defaultContentType}</td>
 * <td>{@link FixedContentNegotiationStrategy Fixed content strategy}</td>
 * <td>Not set</td>
 * </tr>
 * <tr>
 * <td>{@link #setDefaultContentTypeStrategy defaultContentTypeStrategy}</td>
 * <td>{@link ContentNegotiationStrategy}</td>
 * <td>Not set</td>
 * </tr>
 * </table>
 *
 * <p>The order in which strategies are configured is fixed. Setters may only
 * turn individual strategies on or off. If you need a custom order for any
 * reason simply instantiate {@code ContentNegotiationManager} directly.
 *
 * <p>For the path extension and parameter strategies you may explicitly add
 * {@link #setMediaTypes MediaType mappings}. This will be used to resolve path
 * extensions or a parameter value such as "json" to a media type such as
 * "application/json".
 *
 * <p>The path extension strategy will also use {@link ServletContext#getMimeType}
 * and the Java Activation framework (JAF), if available, to resolve a path
 * extension to a MediaType. You may {@link #setUseJaf suppress} the use of JAF.
 *
 * <p>
 * 创建一个{@code ContentNegotiationManager}并通过简单的设置器使用一个或多个{@link ContentNegotiationStrategy}实例进行配置下表显示了set
 * ter,生成的策略实例,如果默认使用的话：。
 * 
 * <table>
 * <tr>
 *  <th>属性设置器</th> <th>底层策略</th> <th>默认设置</th>
 * </tr>
 * <tr>
 *  <td> {@ link #setFavorPathExtension} </td> <td> {@ link PathExtensionContentNegotiationStrategy Path Extension strategy}
 *  </td> <td> On </td>。
 * </tr>
 * <tr>
 *  <td> {@ link #setFavorParameter favorParameter} </td> <td> {@ link ParameterContentNegotiationStrategy Parameter strategy}
 *  </td> <td>关闭</td>。
 * </tr>
 * <tr>
 *  <td> {@ link #setIgnoreAcceptHeader ignoreAcceptHeader} </td> <td> {@ link HeaderContentNegotiationStrategy Header strategy}
 *  </td> <td> On </td>。
 * </tr>
 * <tr>
 * <td> {@ link #setDefaultContentType defaultContentType} </td> <td> {@ link FixedContentNegotiationStrategy固定内容策略}
 *  </td> <td>未设置</td>。
 * </tr>
 * <tr>
 *  <td> {@ link #setDefaultContentTypeStrategy defaultContentTypeStrategy} </td> <td> {@ link ContentNegotiationStrategy}
 *  </td> <td>未设置</td>。
 * </tr>
 * </table>
 * 
 *  <p>配置策略的顺序是固定的Setters可能只会打开或关闭个别策略如果您需要任何原因的自定义订单,只需直接实例化{@code ContentNegotiationManager}
 * 
 *  <p>对于路径扩展和参数策略,您可以明确添加{@link #setMediaTypes MediaType映射}这将用于将路径扩展或参数值(如json)解析为媒体类型,如"application / 
 * json"。
 * 
 * <p>路径扩展策略还将使用{@link ServletContext#getMimeType}和Java Activation框架(JAF)(如果可用)来解析MediaType的路径扩展您可以{@link #setUseJaf抑制使用JAF。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class ContentNegotiationManagerFactoryBean
		implements FactoryBean<ContentNegotiationManager>, ServletContextAware, InitializingBean {

	private boolean favorPathExtension = true;

	private boolean favorParameter = false;

	private boolean ignoreAcceptHeader = false;

	private Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>();

	private boolean ignoreUnknownPathExtensions = true;

	private Boolean useJaf;

	private String parameterName = "format";

	private ContentNegotiationStrategy defaultNegotiationStrategy;

	private ContentNegotiationManager contentNegotiationManager;

	private ServletContext servletContext;


	/**
	 * Whether the path extension in the URL path should be used to determine
	 * the requested media type.
	 * <p>By default this is set to {@code true} in which case a request
	 * for {@code /hotels.pdf} will be interpreted as a request for
	 * {@code "application/pdf"} regardless of the 'Accept' header.
	 * <p>
	 *  是否应使用URL路径中的路径扩展来确定所请求的媒体类型<p>默认情况下,这被设置为{@code true},在这种情况下,{@code / hotelspdf}的请求将被解释为{@code"application / pdf"}
	 * ,而不管'Accept'标题。
	 * 
	 */
	public void setFavorPathExtension(boolean favorPathExtension) {
		this.favorPathExtension = favorPathExtension;
	}

	/**
	 * Add a mapping from a key, extracted from a path extension or a query
	 * parameter, to a MediaType. This is required in order for the parameter
	 * strategy to work. Any extensions explicitly registered here are also
	 * whitelisted for the purpose of Reflected File Download attack detection
	 * (see Spring Framework reference documentation for more details on RFD
	 * attack protection).
	 * <p>The path extension strategy will also try to use
	 * {@link ServletContext#getMimeType} and JAF (if present) to resolve path
	 * extensions. To change this behavior see the {@link #useJaf} property.
	 * <p>
	 * 从路径扩展或查询参数提取的密钥添加映射到MediaType这是为了使参数策略工作所必需的。
	 * 此处明确注册的任何扩展也将被列入白名单,用于"反射文件下载"攻击检测(请参阅Spring Framework参考文档了解有关RFD攻击保护的更多详细信息)路由扩展策略还将尝试使用{@link ServletContext#getMimeType}
	 * 和JAF(如果存在)来解决路径扩展要更改此行为,请参阅{@link #useJaf}属性。
	 * 从路径扩展或查询参数提取的密钥添加映射到MediaType这是为了使参数策略工作所必需的。
	 * 
	 * 
	 * @param mediaTypes media type mappings
	 * @see #addMediaType(String, MediaType)
	 * @see #addMediaTypes(Map)
	 */
	public void setMediaTypes(Properties mediaTypes) {
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			for (Entry<Object, Object> entry : mediaTypes.entrySet()) {
				String extension = ((String)entry.getKey()).toLowerCase(Locale.ENGLISH);
				MediaType mediaType = MediaType.valueOf((String) entry.getValue());
				this.mediaTypes.put(extension, mediaType);
			}
		}
	}

	/**
	 * An alternative to {@link #setMediaTypes} for use in Java code.
	 * <p>
	 *  用于Java代码的{@link #setMediaTypes}的替代方法
	 * 
	 * 
	 * @see #setMediaTypes
	 * @see #addMediaTypes
	 */
	public void addMediaType(String fileExtension, MediaType mediaType) {
		this.mediaTypes.put(fileExtension, mediaType);
	}

	/**
	 * An alternative to {@link #setMediaTypes} for use in Java code.
	 * <p>
	 *  用于Java代码的{@link #setMediaTypes}的替代方法
	 * 
	 * 
	 * @see #setMediaTypes
	 * @see #addMediaType
	 */
	public void addMediaTypes(Map<String, MediaType> mediaTypes) {
		if (mediaTypes != null) {
			this.mediaTypes.putAll(mediaTypes);
		}
	}

	/**
	 * Whether to ignore requests with path extension that cannot be resolved
	 * to any media type. Setting this to {@code false} will result in an
	 * {@code HttpMediaTypeNotAcceptableException} if there is no match.
	 * <p>By default this is set to {@code true}.
	 * <p>
	 * 是否忽略无法解析为任何媒体类型的路径扩展的请求将此设置为{@code false}将导致{@code HttpMediaTypeNotAcceptableException},如果没有匹配<p>默认情况
	 * 下设置为{@code true }。
	 * 
	 */
	public void setIgnoreUnknownPathExtensions(boolean ignore) {
		this.ignoreUnknownPathExtensions = ignore;
	}

	/**
	 * When {@link #setFavorPathExtension favorPathExtension} is set, this
	 * property determines whether to allow use of JAF (Java Activation Framework)
	 * to resolve a path extension to a specific MediaType.
	 * <p>By default this is not set in which case
	 * {@code PathExtensionContentNegotiationStrategy} will use JAF if available.
	 * <p>
	 *  当设置了{@link #setFavorPathExtension favorPathExtension}时,此属性决定是否允许使用JAF(Java Activation Framework)来解析到
	 * 特定MediaType <p>的路径扩展。
	 * 默认情况下,不设置{@code PathExtensionContentNegotiationStrategy }将使用JAF(如果可用)。
	 * 
	 */
	public void setUseJaf(boolean useJaf) {
		this.useJaf = useJaf;
	}

	private boolean isUseJafTurnedOff() {
		return (this.useJaf != null && !this.useJaf);
	}

	/**
	 * Whether a request parameter ("format" by default) should be used to
	 * determine the requested media type. For this option to work you must
	 * register {@link #setMediaTypes media type mappings}.
	 * <p>By default this is set to {@code false}.
	 * <p>
	 *  应该使用请求参数("格式"默认情况下)来确定所请求的媒体类型要使用此选项,您必须注册{@link #setMediaTypes媒体类型映射} <p>默认情况下,这被设置为{@code false }。
	 * 
	 * 
	 * @see #setParameterName
	 */
	public void setFavorParameter(boolean favorParameter) {
		this.favorParameter = favorParameter;
	}

	/**
	 * Set the query parameter name to use when {@link #setFavorParameter} is on.
	 * <p>The default parameter name is {@code "format"}.
	 * <p>
	 * 设置{@link #setFavorParameter}在<p>上使用的查询参数名称默认参数名称为{@code"格式"}
	 * 
	 */
	public void setParameterName(String parameterName) {
		Assert.notNull(parameterName, "parameterName is required");
		this.parameterName = parameterName;
	}

	/**
	 * Whether to disable checking the 'Accept' request header.
	 * <p>By default this value is set to {@code false}.
	 * <p>
	 *  是否禁用检查"接受"请求标头<p>默认情况下,此值设置为{@code false}
	 * 
	 */
	public void setIgnoreAcceptHeader(boolean ignoreAcceptHeader) {
		this.ignoreAcceptHeader = ignoreAcceptHeader;
	}

	/**
	 * Set the default content type to use when no content type is requested.
	 * <p>By default this is not set.
	 * <p>
	 *  设置未请求内容类型时使用的默认内容类型<p>默认情况下,此设置未设置
	 * 
	 * 
	 * @see #setDefaultContentTypeStrategy
	 */
	public void setDefaultContentType(MediaType contentType) {
		this.defaultNegotiationStrategy = new FixedContentNegotiationStrategy(contentType);
	}

	/**
	 * Set a custom {@link ContentNegotiationStrategy} to use to determine
	 * the content type to use when no content type is requested.
	 * <p>By default this is not set.
	 * <p>
	 *  设置自定义{@link ContentNegotiationStrategy}以用于确定在不请求内容类型时使用的内容类型<p>默认情况下,该设置不设置
	 * 
	 * 
	 * @see #setDefaultContentType
	 * @since 4.1.2
	 */
	public void setDefaultContentTypeStrategy(ContentNegotiationStrategy strategy) {
		this.defaultNegotiationStrategy = strategy;
	}

	/**
	 * Invoked by Spring to inject the ServletContext.
	 * <p>
	 *  由Spring调用注入ServletContext
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}


	@Override
	public void afterPropertiesSet() {
		List<ContentNegotiationStrategy> strategies = new ArrayList<ContentNegotiationStrategy>();

		if (this.favorPathExtension) {
			PathExtensionContentNegotiationStrategy strategy;
			if (this.servletContext != null && !isUseJafTurnedOff()) {
				strategy = new ServletPathExtensionContentNegotiationStrategy(
						this.servletContext, this.mediaTypes);
			}
			else {
				strategy = new PathExtensionContentNegotiationStrategy(this.mediaTypes);
			}
			strategy.setIgnoreUnknownExtensions(this.ignoreUnknownPathExtensions);
			if (this.useJaf != null) {
				strategy.setUseJaf(this.useJaf);
			}
			strategies.add(strategy);
		}

		if (this.favorParameter) {
			ParameterContentNegotiationStrategy strategy =
					new ParameterContentNegotiationStrategy(this.mediaTypes);
			strategy.setParameterName(this.parameterName);
			strategies.add(strategy);
		}

		if (!this.ignoreAcceptHeader) {
			strategies.add(new HeaderContentNegotiationStrategy());
		}

		if (this.defaultNegotiationStrategy != null) {
			strategies.add(this.defaultNegotiationStrategy);
		}

		this.contentNegotiationManager = new ContentNegotiationManager(strategies);
	}

	@Override
	public ContentNegotiationManager getObject() {
		return this.contentNegotiationManager;
	}

	@Override
	public Class<?> getObjectType() {
		return ContentNegotiationManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
