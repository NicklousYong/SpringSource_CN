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
package org.springframework.web.servlet.config.annotation;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;

import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;

/**
 * Creates a {@code ContentNegotiationManager} and configures it with
 * one or more {@link ContentNegotiationStrategy} instances. The following shows
 * the resulting strategy instances, the methods used to configured them, and
 * whether enabled by default:
 *
 * <table>
 * <tr>
 *     <th>Configurer Property</th>
 *     <th>Underlying Strategy</th>
 *     <th>Default Setting</th>
 * </tr>
 * <tr>
 *     <td>{@link #favorPathExtension}</td>
 *     <td>{@link PathExtensionContentNegotiationStrategy Path Extension strategy}</td>
 *     <td>On</td>
 * </tr>
 * <tr>
 *     <td>{@link #favorParameter}</td>
 *     <td>{@link ParameterContentNegotiationStrategy Parameter strategy}</td>
 *     <td>Off</td>
 * </tr>
 * <tr>
 *     <td>{@link #ignoreAcceptHeader}</td>
 *     <td>{@link HeaderContentNegotiationStrategy Header strategy}</td>
 *     <td>On</td>
 * </tr>
 * <tr>
 *     <td>{@link #defaultContentType}</td>
 *     <td>{@link FixedContentNegotiationStrategy Fixed content strategy}</td>
 *     <td>Not set</td>
 * </tr>
 * <tr>
 *     <td>{@link #defaultContentTypeStrategy}</td>
 *     <td>{@link ContentNegotiationStrategy}</td>
 *     <td>Not set</td>
 * </tr>
 * </table>
 *
 * <p>The order in which strategies are configured is fixed. You can only turn
 * them on or off.
 *
 * <p>For the path extension and parameter strategies you may explicitly add
 * {@link #mediaType MediaType mappings}. Those will be used to resolve path
 * extensions and/or a query parameter value such as "json" to a concrete media
 * type such as "application/json".
 *
 * <p>The path extension strategy will also use {@link ServletContext#getMimeType}
 * and the Java Activation framework (JAF), if available, to resolve a path
 * extension to a MediaType. You may however {@link #useJaf suppress} the use
 * of JAF.
 *
 * <p>
 * 创建一个{@code ContentNegotiationManager}并使用一个或多个{@link ContentNegotiationStrategy}实例进行配置以下显示了生成的策略实例,用于配
 * 置它们的方法,以及是否默认启用：。
 * 
 * <table>
 * <tr>
 *  <th>配置属性</th> <th>底层策略</th> <th>默认设置</th>
 * </tr>
 * <tr>
 *  <td> {@ link #favorPathExtension} </td> <td> {@ link PathExtensionContentNegotiationStrategy Path Extension strategy}
 *  </td> <td> On </td>。
 * </tr>
 * <tr>
 *  <td> {@ link #favorParameter} </td> <td> {@ link ParameterContentNegotiationStrategy Parameter strategy}
 *  </td> <td>关闭</td>。
 * </tr>
 * <tr>
 *  <td> {@ link #ignoreAcceptHeader} </td> <td> {@ link HeaderContentNegotiationStrategy Header strategy}
 *  </td> <td>在</td>。
 * </tr>
 * <tr>
 * <td> {@ link #defaultContentType} </td> <td> {@ link FixedContentNegotiationStrategy固定内容策略} </td> <td>
 * 未设置</td>。
 * </tr>
 * <tr>
 *  <td> {@ link #defaultContentTypeStrategy} </td> <td> {@ link ContentNegotiationStrategy} </td> <td>未
 * 设置</td>。
 * </tr>
 * </table>
 * 
 *  <p>策略的配置顺序是固定的您只能打开或关闭它们
 * 
 *  <p>对于路径扩展和参数策略,您可以明确添加{@link #mediaType MediaType映射}这些将用于将路径扩展和/或查询参数值(如"json")解析为具体的媒体类型,例如"应用程序/ J
 * SON"。
 * 
 * <p>路径扩展策略还将使用{@link ServletContext#getMimeType}和Java Activation框架(JAF)(如果可用)来解析MediaType的路径扩展您可能{@link #useJaf suppress}
 * 使用JAF。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class ContentNegotiationConfigurer {

	private final ContentNegotiationManagerFactoryBean factory =
			new ContentNegotiationManagerFactoryBean();

	private final Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>();


	/**
	 * Class constructor with {@link javax.servlet.ServletContext}.
	 * <p>
	 *  具有{@link javaxservletServletContext}的类构造函数
	 * 
	 */
	public ContentNegotiationConfigurer(ServletContext servletContext) {
		this.factory.setServletContext(servletContext);
	}


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
	public ContentNegotiationConfigurer favorPathExtension(boolean favorPathExtension) {
		this.factory.setFavorPathExtension(favorPathExtension);
		return this;
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
	 * @param extension the key to look up
	 * @param mediaType the media type
	 * @see #mediaTypes(Map)
	 * @see #replaceMediaTypes(Map)
	 */
	public ContentNegotiationConfigurer mediaType(String extension, MediaType mediaType) {
		this.mediaTypes.put(extension, mediaType);
		return this;
	}

	/**
	 * An alternative to {@link #mediaType}.
	 * <p>
	 *  {@link #mediaType}的替代方案
	 * 
	 * 
	 * @see #mediaType(String, MediaType)
	 * @see #replaceMediaTypes(Map)
	 */
	public ContentNegotiationConfigurer mediaTypes(Map<String, MediaType> mediaTypes) {
		if (mediaTypes != null) {
			this.mediaTypes.putAll(mediaTypes);
		}
		return this;
	}

	/**
	 * Similar to {@link #mediaType} but for replacing existing mappings.
	 * <p>
	 *  与{@link #mediaType}类似,但用于替换现有的映射
	 * 
	 * 
	 * @see #mediaType(String, MediaType)
	 * @see #mediaTypes(Map)
	 */
	public ContentNegotiationConfigurer replaceMediaTypes(Map<String, MediaType> mediaTypes) {
		this.mediaTypes.clear();
		mediaTypes(mediaTypes);
		return this;
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
	public ContentNegotiationConfigurer ignoreUnknownPathExtensions(boolean ignore) {
		this.factory.setIgnoreUnknownPathExtensions(ignore);
		return this;
	}

	/**
	 * When {@link #favorPathExtension} is set, this property determines whether
	 * to allow use of JAF (Java Activation Framework) to resolve a path
	 * extension to a specific MediaType.
	 * <p>By default this is not set in which case
	 * {@code PathExtensionContentNegotiationStrategy} will use JAF if available.
	 * <p>
	 *  当设置了{@link #favorPathExtension}时,此属性决定是否允许使用JAF(Java Activation Framework)来解析到特定MediaType <p>的路径扩展。
	 * 默认情况下,这不是{@code PathExtensionContentNegotiationStrategy}将使用JAF(如果可用)。
	 * 
	 */
	public ContentNegotiationConfigurer useJaf(boolean useJaf) {
		this.factory.setUseJaf(useJaf);
		return this;
	}

	/**
	 * Whether a request parameter ("format" by default) should be used to
	 * determine the requested media type. For this option to work you must
	 * register {@link #mediaType(String, MediaType) media type mappings}.
	 * <p>By default this is set to {@code false}.
	 * <p>
	 *  应该使用请求参数(默认为"格式")来确定所请求的媒体类型要使用此选项,您必须注册{@link #mediaType(String,MediaType)媒体类型映射} <p>默认情况下,这是设置的到{@code false}
	 * 。
	 * 
	 * 
	 * @see #parameterName(String)
	 */
	public ContentNegotiationConfigurer favorParameter(boolean favorParameter) {
		this.factory.setFavorParameter(favorParameter);
		return this;
	}

	/**
	 * Set the query parameter name to use when {@link #favorParameter} is on.
	 * <p>The default parameter name is {@code "format"}.
	 * <p>
	 * 设置{@link #favorParameter}在<p>上使用的查询参数名称默认参数名称为{@code"格式"}
	 * 
	 */
	public ContentNegotiationConfigurer parameterName(String parameterName) {
		this.factory.setParameterName(parameterName);
		return this;
	}

	/**
	 * Whether to disable checking the 'Accept' request header.
	 * <p>By default this value is set to {@code false}.
	 * <p>
	 *  是否禁用检查"接受"请求标头<p>默认情况下,此值设置为{@code false}
	 * 
	 */
	public ContentNegotiationConfigurer ignoreAcceptHeader(boolean ignoreAcceptHeader) {
		this.factory.setIgnoreAcceptHeader(ignoreAcceptHeader);
		return this;
	}

	/**
	 * Set the default content type to use when no content type is requested.
	 * <p>By default this is not set.
	 * <p>
	 *  设置未请求内容类型时使用的默认内容类型<p>默认情况下,此设置未设置
	 * 
	 * 
	 * @see #defaultContentTypeStrategy
	 */
	public ContentNegotiationConfigurer defaultContentType(MediaType defaultContentType) {
		this.factory.setDefaultContentType(defaultContentType);
		return this;
	}

	/**
	 * Set a custom {@link ContentNegotiationStrategy} to use to determine
	 * the content type to use when no content type is requested.
	 * <p>By default this is not set.
	 * <p>
	 *  设置自定义{@link ContentNegotiationStrategy}以用于确定在不请求内容类型时使用的内容类型<p>默认情况下,该设置不设置
	 * 
	 * @see #defaultContentType
	 * @since 4.1.2
	 */
	public ContentNegotiationConfigurer defaultContentTypeStrategy(ContentNegotiationStrategy defaultStrategy) {
		this.factory.setDefaultContentTypeStrategy(defaultStrategy);
		return this;
	}

	protected ContentNegotiationManager getContentNegotiationManager() throws Exception {
		this.factory.addMediaTypes(this.mediaTypes);
		this.factory.afterPropertiesSet();
		return this.factory.getObject();
	}

}
