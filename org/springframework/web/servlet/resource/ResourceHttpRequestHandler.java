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

package org.springframework.web.servlet.resource;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.accept.ServletPathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.WebContentGenerator;

/**
 * {@code HttpRequestHandler} that serves static resources in an optimized way
 * according to the guidelines of Page Speed, YSlow, etc.
 *
 * <p>The {@linkplain #setLocations "locations"} property takes a list of Spring
 * {@link Resource} locations from which static resources are allowed to
 * be served by this handler. Resources could be served from a classpath location,
 * e.g. "classpath:/META-INF/public-web-resources/", allowing convenient packaging
 * and serving of resources such as .js, .css, and others in jar files.
 *
 * <p>This request handler may also be configured with a
 * {@link #setResourceResolvers(List) resourcesResolver} and
 * {@link #setResourceTransformers(List) resourceTransformer} chains to support
 * arbitrary resolution and transformation of resources being served. By default a
 * {@link PathResourceResolver} simply finds resources based on the configured
 * "locations". An application can configure additional resolvers and
 * transformers such as the {@link VersionResourceResolver} which can resolve
 * and prepare URLs for resources with a version in the URL.
 *
 * <p>This handler also properly evaluates the {@code Last-Modified} header (if
 * present) so that a {@code 304} status code will be returned as appropriate,
 * avoiding unnecessary overhead for resources that are already cached by the
 * client.
 *
 * <p>
 *  {@code HttpRequestHandler},根据Page Speed,YSlow等的准则,以优化的方式提供静态资源
 * 
 * <p> {@linkplain #setLocations"locations"}属性具有Spring {@link Resource}位置的列表,静态资源可由该处理程序提供。
 * 资源可以从类路径位置提供,例如"classpath： / META-INF / public-web-resources /",允许在jar文件中方便地打包和服务诸如js,css等的资源。
 * 
 * <p>此请求处理程序还可以使用{@link #setResourceResolvers(List)resourcesResolver}和{@link #setResourceTransformers(List)resourceTransformer}
 * 链来配置,以支持任意解析和转换正在提供的资源。
 * 默认情况下,{@link PathResourceResolver}只是根据已配置的"位置"查找资源应用程序可以配置其他解析器和变量,例如{@link VersionResourceResolver},
 * 可以解析和准备URL中的URL,其中包含URL中的版本。
 * 
 *  <p>此处理程序也适当地评估{@code Last-Modified}标题(如果存在),以便适当地返回{@code 304}状态代码,避免客户端已经缓存的资源的不必要的开销
 * 
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 3.0.4
 */
public class ResourceHttpRequestHandler extends WebContentGenerator
		implements HttpRequestHandler, InitializingBean, SmartInitializingSingleton, CorsConfigurationSource {

	// Servlet 3.1 setContentLengthLong(long) available?
	private static final boolean contentLengthLongAvailable =
			ClassUtils.hasMethod(ServletResponse.class, "setContentLengthLong", long.class);

	private static final Log logger = LogFactory.getLog(ResourceHttpRequestHandler.class);


	private final List<Resource> locations = new ArrayList<Resource>(4);

	private final List<ResourceResolver> resourceResolvers = new ArrayList<ResourceResolver>(4);

	private final List<ResourceTransformer> resourceTransformers = new ArrayList<ResourceTransformer>(4);

	private ResourceHttpMessageConverter resourceHttpMessageConverter;

	private ResourceRegionHttpMessageConverter resourceRegionHttpMessageConverter;

	private ContentNegotiationManager contentNegotiationManager;

	private PathExtensionContentNegotiationStrategy pathExtensionStrategy;

	private ServletContext servletContext;

	private CorsConfiguration corsConfiguration;


	public ResourceHttpRequestHandler() {
		super(HttpMethod.GET.name(), HttpMethod.HEAD.name());
	}


	/**
	 * Set the {@code List} of {@code Resource} paths to use as sources
	 * for serving static resources.
	 * <p>
	 * 将{@code资源}路径的{@code列表}设置为用作服务静态资源的源
	 * 
	 */
	public void setLocations(List<Resource> locations) {
		Assert.notNull(locations, "Locations list must not be null");
		this.locations.clear();
		this.locations.addAll(locations);
	}

	/**
	 * Return the {@code List} of {@code Resource} paths to use as sources
	 * for serving static resources.
	 * <p>
	 *  返回{@code资源}路径的{@code列表}以用作服务静态资源的源
	 * 
	 */
	public List<Resource> getLocations() {
		return this.locations;
	}

	/**
	 * Configure the list of {@link ResourceResolver}s to use.
	 * <p>By default {@link PathResourceResolver} is configured. If using this property,
	 * it is recommended to add {@link PathResourceResolver} as the last resolver.
	 * <p>
	 *  配置{@link ResourceResolver}的列表以使用<p>默认情况下配置{@link PathResourceResolver}如果使用此属性,建议将{@link PathResourceResolver}
	 * 添加为最后一个解析器。
	 * 
	 */
	public void setResourceResolvers(List<ResourceResolver> resourceResolvers) {
		this.resourceResolvers.clear();
		if (resourceResolvers != null) {
			this.resourceResolvers.addAll(resourceResolvers);
		}
	}

	/**
	 * Return the list of configured resource resolvers.
	 * <p>
	 *  返回配置的资源解析器列表
	 * 
	 */
	public List<ResourceResolver> getResourceResolvers() {
		return this.resourceResolvers;
	}

	/**
	 * Configure the list of {@link ResourceTransformer}s to use.
	 * <p>By default no transformers are configured for use.
	 * <p>
	 *  配置{@link ResourceTransformer}的列表以使用<p>默认情况下,没有变压器配置为使用
	 * 
	 */
	public void setResourceTransformers(List<ResourceTransformer> resourceTransformers) {
		this.resourceTransformers.clear();
		if (resourceTransformers != null) {
			this.resourceTransformers.addAll(resourceTransformers);
		}
	}

	/**
	 * Return the list of configured resource transformers.
	 * <p>
	 *  返回配置的资源变压器列表
	 * 
	 */
	public List<ResourceTransformer> getResourceTransformers() {
		return this.resourceTransformers;
	}

	/**
	 * Configure the {@link ResourceHttpMessageConverter} to use.
	 * <p>By default a {@link ResourceHttpMessageConverter} will be configured.
	 * <p>
	 *  配置{@link ResourceHttpMessageConverter}以使用<p>默认情况下,将配置{@link ResourceHttpMessageConverter}
	 * 
	 * 
	 * @since 4.3
	 */
	public void setResourceHttpMessageConverter(ResourceHttpMessageConverter resourceHttpMessageConverter) {
		this.resourceHttpMessageConverter = resourceHttpMessageConverter;
	}

	/**
	 * Return the list of configured resource converters.
	 * <p>
	 * 返回配置的资源转换器列表
	 * 
	 * 
	 * @since 4.3
	 */
	public ResourceHttpMessageConverter getResourceHttpMessageConverter() {
		return this.resourceHttpMessageConverter;
	}

	/**
	 * Configure the {@link ResourceRegionHttpMessageConverter} to use.
	 * <p>By default a {@link ResourceRegionHttpMessageConverter} will be configured.
	 * <p>
	 *  配置{@link ResourceRegionHttpMessageConverter}以使用<p>默认情况下,将配置{@link ResourceRegionHttpMessageConverter}
	 * 。
	 * 
	 * 
	 * @since 4.3
	 */
	public void setResourceRegionHttpMessageConverter(ResourceRegionHttpMessageConverter resourceRegionHttpMessageConverter) {
		this.resourceRegionHttpMessageConverter = resourceRegionHttpMessageConverter;
	}

	/**
	 * Return the list of configured resource region converters.
	 * <p>
	 *  返回配置的资源区域转换器列表
	 * 
	 * 
	 * @since 4.3
	 */
	public ResourceRegionHttpMessageConverter getResourceRegionHttpMessageConverter() {
		return this.resourceRegionHttpMessageConverter;
	}

	/**
	 * Configure a {@code ContentNegotiationManager} to help determine the
	 * media types for resources being served. If the manager contains a path
	 * extension strategy it will be checked for registered file extension.
	 * <p>
	 *  配置{@code ContentNegotiationManager}以帮助确定正在投放的资源的媒体类型如果管理员包含路径扩展策略,则会检查注册的文件扩展名
	 * 
	 * 
	 * @param contentNegotiationManager the manager in use
	 * @since 4.3
	 */
	public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}

	/**
	 * Return the configured content negotiation manager.
	 * <p>
	 *  返回配置的内容协商管理器
	 * 
	 * 
	 * @since 4.3
	 */
	public ContentNegotiationManager getContentNegotiationManager() {
		return this.contentNegotiationManager;
	}

	/**
	 * Specify the CORS configuration for resources served by this handler.
	 * <p>By default this is not set in which allows cross-origin requests.
	 * <p>
	 *  指定此处理程序提供的资源的CORS配置<p>默认情况下,这不允许跨源请求
	 * 
	 */
	public void setCorsConfiguration(CorsConfiguration corsConfiguration) {
		this.corsConfiguration = corsConfiguration;
	}

	/**
	 * Return the specified CORS configuration.
	 * <p>
	 *  返回指定的CORS配置
	 * 
	 */
	@Override
	public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
		return this.corsConfiguration;
	}

	@Override
	protected void initServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		if (logger.isWarnEnabled() && CollectionUtils.isEmpty(this.locations)) {
			logger.warn("Locations list is empty. No resources will be served unless a " +
					"custom ResourceResolver is configured as an alternative to PathResourceResolver.");
		}
		if (this.resourceResolvers.isEmpty()) {
			this.resourceResolvers.add(new PathResourceResolver());
		}
		initAllowedLocations();
		if (this.resourceHttpMessageConverter == null) {
			this.resourceHttpMessageConverter = new ResourceHttpMessageConverter();
		}
		if (this.resourceRegionHttpMessageConverter == null) {
			this.resourceRegionHttpMessageConverter = new ResourceRegionHttpMessageConverter();
		}
	}

	/**
	 * Look for a {@code PathResourceResolver} among the configured resource
	 * resolvers and set its {@code allowedLocations} property (if empty) to
	 * match the {@link #setLocations locations} configured on this class.
	 * <p>
	 * 在配置的资源解析器中查找{@code PathResourceResolver},并设置其{@code allowedLocations}属性(如果为空)以匹配此类上配置的{@link #setLocations位置}
	 * 。
	 * 
	 */
	protected void initAllowedLocations() {
		if (CollectionUtils.isEmpty(this.locations)) {
			return;
		}
		for (int i = getResourceResolvers().size() - 1; i >= 0; i--) {
			if (getResourceResolvers().get(i) instanceof PathResourceResolver) {
				PathResourceResolver pathResolver = (PathResourceResolver) getResourceResolvers().get(i);
				if (ObjectUtils.isEmpty(pathResolver.getAllowedLocations())) {
					pathResolver.setAllowedLocations(getLocations().toArray(new Resource[getLocations().size()]));
				}
				break;
			}
		}
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.pathExtensionStrategy = initPathExtensionStrategy();
	}

	protected PathExtensionContentNegotiationStrategy initPathExtensionStrategy() {
		Map<String, MediaType> mediaTypes = null;
		if (getContentNegotiationManager() != null) {
			PathExtensionContentNegotiationStrategy strategy =
					getContentNegotiationManager().getStrategy(PathExtensionContentNegotiationStrategy.class);
			if (strategy != null) {
				mediaTypes = new HashMap<String, MediaType>(strategy.getMediaTypes());
			}
		}
		return (getServletContext() != null) ?
				new ServletPathExtensionContentNegotiationStrategy(getServletContext(), mediaTypes) :
				new PathExtensionContentNegotiationStrategy(mediaTypes);
	}


	/**
	 * Processes a resource request.
	 * <p>Checks for the existence of the requested resource in the configured list of locations.
	 * If the resource does not exist, a {@code 404} response will be returned to the client.
	 * If the resource exists, the request will be checked for the presence of the
	 * {@code Last-Modified} header, and its value will be compared against the last-modified
	 * timestamp of the given resource, returning a {@code 304} status code if the
	 * {@code Last-Modified} value  is greater. If the resource is newer than the
	 * {@code Last-Modified} value, or the header is not present, the content resource
	 * of the resource will be written to the response with caching headers
	 * set to expire one year in the future.
	 * <p>
	 * 处理资源请求<p>检查已配置的位置列表中是否存在请求的资源如果资源不存在,将向客户端返回{@code 404}响应。
	 * 如果资源存在,请求将为检查{@code Last-Modified}标题的存在,并将其值与给定资源的最后修改的时间戳进行比较,如果{@code Last-Modified}返回{@code 304}状态
	 * 码,值更大如果资源比{@code Last-Modified}值更新,或者标题不存在,则资源的内容资源将被写入响应,缓存头将来将设置为过期一年。
	 * 处理资源请求<p>检查已配置的位置列表中是否存在请求的资源如果资源不存在,将向客户端返回{@code 404}响应。
	 * 
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// For very general mappings (e.g. "/") we need to check 404 first
		Resource resource = getResource(request);
		if (resource == null) {
			logger.trace("No matching resource found - returning 404");
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		if (HttpMethod.OPTIONS.matches(request.getMethod())) {
			response.setHeader("Allow", getAllowHeader());
			return;
		}

		// Supported methods and required session
		checkRequest(request);

		// Header phase
		if (new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
			logger.trace("Resource not modified - returning 304");
			return;
		}

		// Apply cache settings, if any
		prepareResponse(response);

		// Check the media type for the resource
		MediaType mediaType = getMediaType(request, resource);
		if (mediaType != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Determined media type '" + mediaType + "' for " + resource);
			}
		}
		else {
			if (logger.isTraceEnabled()) {
				logger.trace("No media type found for " + resource + " - not sending a content-type header");
			}
		}

		// Content phase
		if (METHOD_HEAD.equals(request.getMethod())) {
			setHeaders(response, resource, mediaType);
			logger.trace("HEAD request - skipping content");
			return;
		}

		ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
		if (request.getHeader(HttpHeaders.RANGE) == null) {
			setHeaders(response, resource, mediaType);
			this.resourceHttpMessageConverter.write(resource, mediaType, outputMessage);
		}
		else {
			response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
			ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
			try {
				List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				if (httpRanges.size() == 1) {
					ResourceRegion resourceRegion = httpRanges.get(0).toResourceRegion(resource);
					this.resourceRegionHttpMessageConverter.write(resourceRegion, mediaType, outputMessage);
				}
				else {
					this.resourceRegionHttpMessageConverter.write(
							HttpRange.toResourceRegions(httpRanges, resource), mediaType, outputMessage);
				}
			}
			catch (IllegalArgumentException ex) {
				response.setHeader("Content-Range", "bytes */" + resource.contentLength());
				response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
			}
		}
	}

	protected Resource getResource(HttpServletRequest request) throws IOException {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		if (path == null) {
			throw new IllegalStateException("Required request attribute '" +
					HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE + "' is not set");
		}
		path = processPath(path);
		if (!StringUtils.hasText(path) || isInvalidPath(path)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Ignoring invalid resource path [" + path + "]");
			}
			return null;
		}
		if (path.contains("%")) {
			try {
				// Use URLDecoder (vs UriUtils) to preserve potentially decoded UTF-8 chars
				if (isInvalidPath(URLDecoder.decode(path, "UTF-8"))) {
					if (logger.isTraceEnabled()) {
						logger.trace("Ignoring invalid resource path with escape sequences [" + path + "].");
					}
					return null;
				}
			}
			catch (IllegalArgumentException ex) {
				// ignore
			}
		}
		ResourceResolverChain resolveChain = new DefaultResourceResolverChain(getResourceResolvers());
		Resource resource = resolveChain.resolveResource(request, path, getLocations());
		if (resource == null || getResourceTransformers().isEmpty()) {
			return resource;
		}
		ResourceTransformerChain transformChain =
				new DefaultResourceTransformerChain(resolveChain, getResourceTransformers());
		resource = transformChain.transform(request, resource);
		return resource;
	}

	/**
	 * Process the given resource path to be used.
	 * <p>The default implementation replaces any combination of leading '/' and
	 * control characters (00-1F and 7F) with a single "/" or "". For example
	 * {@code "  // /// ////  foo/bar"} becomes {@code "/foo/bar"}.
	 * <p>
	 * 处理要使用的给定资源路径<p>默认实现使用单个"/"或""替换前导"/"和控制字符(00-1F和7F)的任何组合。
	 * 例如{@code"// / // //// foo / bar"}成为{@code"/ foo / bar"}。
	 * 
	 * 
	 * @since 3.2.12
	 */
	protected String processPath(String path) {
		boolean slash = false;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '/') {
				slash = true;
			}
			else if (path.charAt(i) > ' ' && path.charAt(i) != 127) {
				if (i == 0 || (i == 1 && slash)) {
					return path;
				}
				path = slash ? "/" + path.substring(i) : path.substring(i);
				if (logger.isTraceEnabled()) {
					logger.trace("Path after trimming leading '/' and control characters: " + path);
				}
				return path;
			}
		}
		return (slash ? "/" : "");
	}

	/**
	 * Identifies invalid resource paths. By default rejects:
	 * <ul>
	 * <li>Paths that contain "WEB-INF" or "META-INF"
	 * <li>Paths that contain "../" after a call to
	 * {@link org.springframework.util.StringUtils#cleanPath}.
	 * <li>Paths that represent a {@link org.springframework.util.ResourceUtils#isUrl
	 * valid URL} or would represent one after the leading slash is removed.
	 * </ul>
	 * <p><strong>Note:</strong> this method assumes that leading, duplicate '/'
	 * or control characters (e.g. white space) have been trimmed so that the
	 * path starts predictably with a single '/' or does not have one.
	 * <p>
	 *  标识无效资源路径默认拒绝：
	 * <ul>
	 *  <li>包含"WEB-INF"或"META-INF"的路径<li>调用{@link orgspringframeworkutilStringUtils#cleanPath}后包含"/"的路径<li>表
	 * 示{@link orgspringframeworkutilResourceUtils#isUrl的路径有效的URL},或者在删除主要的斜杠之后代表一个。
	 * </ul>
	 * <p> <strong>注意：</strong>这个方法假定前导,复制'/'或控制字符(例如空格)已被修整,以便路径以单个'/'可预测地开始,或者没有一个
	 * 
	 * 
	 * @param path the path to validate
	 * @return {@code true} if the path is invalid, {@code false} otherwise
	 */
	protected boolean isInvalidPath(String path) {
		if (logger.isTraceEnabled()) {
			logger.trace("Applying \"invalid path\" checks to path: " + path);
		}
		if (path.contains("WEB-INF") || path.contains("META-INF")) {
			if (logger.isTraceEnabled()) {
				logger.trace("Path contains \"WEB-INF\" or \"META-INF\".");
			}
			return true;
		}
		if (path.contains(":/")) {
			String relativePath = (path.charAt(0) == '/' ? path.substring(1) : path);
			if (ResourceUtils.isUrl(relativePath) || relativePath.startsWith("url:")) {
				if (logger.isTraceEnabled()) {
					logger.trace("Path represents URL or has \"url:\" prefix.");
				}
				return true;
			}
		}
		if (path.contains("..")) {
			path = StringUtils.cleanPath(path);
			if (path.contains("../")) {
				if (logger.isTraceEnabled()) {
					logger.trace("Path contains \"../\" after call to StringUtils#cleanPath.");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine the media type for the given request and the resource matched
	 * to it. This implementation tries to determine the MediaType based on the
	 * file extension of the Resource via
	 * {@link ServletPathExtensionContentNegotiationStrategy#getMediaTypeForResource}.
	 * <p>
	 *  确定给定请求的媒体类型和与之匹配的资源此实现尝试通过{@link ServletPathExtensionContentNegotiationStrategy#getMediaTypeForResource}
	 * 根据资源的文件扩展名确定MediaType。
	 * 
	 * 
	 * @param request the current request
	 * @param resource the resource to check
	 * @return the corresponding media type, or {@code null} if none found
	 */
	@SuppressWarnings("deprecation")
	protected MediaType getMediaType(HttpServletRequest request, Resource resource) {
		// For backwards compatibility
		MediaType mediaType = getMediaType(resource);
		if (mediaType != null) {
			return mediaType;
		}
		return this.pathExtensionStrategy.getMediaTypeForResource(resource);
	}

	/**
	 * Determine an appropriate media type for the given resource.
	 * <p>
	 *  确定给定资源的适当媒体类型
	 * 
	 * 
	 * @param resource the resource to check
	 * @return the corresponding media type, or {@code null} if none found
	 * @deprecated as of 4.3 this method is deprecated; please override
	 * {@link #getMediaType(HttpServletRequest, Resource)} instead.
	 */
	@Deprecated
	protected MediaType getMediaType(Resource resource) {
		return null;
	}

	/**
	 * Set headers on the given servlet response.
	 * Called for GET requests as well as HEAD requests.
	 * <p>
	 *  在给定的servlet响应中设置头部调用GET请求以及HEAD请求
	 * 
	 * @param response current servlet response
	 * @param resource the identified resource (never {@code null})
	 * @param mediaType the resource's media type (never {@code null})
	 * @throws IOException in case of errors while setting the headers
	 */
	protected void setHeaders(HttpServletResponse response, Resource resource, MediaType mediaType) throws IOException {
		long length = resource.contentLength();
		if (length > Integer.MAX_VALUE) {
			if (contentLengthLongAvailable) {
				response.setContentLengthLong(length);
			}
			else {
				response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(length));
			}
		}
		else {
			response.setContentLength((int) length);
		}

		if (mediaType != null) {
			response.setContentType(mediaType.toString());
		}
		if (resource instanceof EncodedResource) {
			response.setHeader(HttpHeaders.CONTENT_ENCODING, ((EncodedResource) resource).getContentEncoding());
		}
		if (resource instanceof VersionedResource) {
			response.setHeader(HttpHeaders.ETAG, "\"" + ((VersionedResource) resource).getVersion() + "\"");
		}
		response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
	}


	@Override
	public String toString() {
		return "ResourceHttpRequestHandler [locations=" + getLocations() + ", resolvers=" + getResourceResolvers() + "]";
	}

}
