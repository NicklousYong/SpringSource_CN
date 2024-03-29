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

package org.springframework.web.servlet.mvc.method;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.util.UrlPathHelper;

/**
 * A {@link RequestCondition} that consists of the following other conditions:
 * <ol>
 * <li>{@link PatternsRequestCondition}
 * <li>{@link RequestMethodsRequestCondition}
 * <li>{@link ParamsRequestCondition}
 * <li>{@link HeadersRequestCondition}
 * <li>{@link ConsumesRequestCondition}
 * <li>{@link ProducesRequestCondition}
 * <li>{@code RequestCondition} (optional, custom request condition)
 * </ol>
 *
 * <p>
 *  {@link RequestCondition}由以下其他条件组成：
 * <ol>
 * <li> {@ link PatternsRequestCondition} <li> {@ link RequestMethodsRequestCondition} <li> {@ link ParamsRequestCondition}
 *  <li> {@ link HeadersRequestCondition} <li> {@ link ConsumesRequestCondition} <li> {@ link ProducesRequestCondition}
 *  <li > {@ code RequestCondition}(可选,自定义请求条件)。
 * </ol>
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class RequestMappingInfo implements RequestCondition<RequestMappingInfo> {

	private final String name;

	private final PatternsRequestCondition patternsCondition;

	private final RequestMethodsRequestCondition methodsCondition;

	private final ParamsRequestCondition paramsCondition;

	private final HeadersRequestCondition headersCondition;

	private final ConsumesRequestCondition consumesCondition;

	private final ProducesRequestCondition producesCondition;

	private final RequestConditionHolder customConditionHolder;


	public RequestMappingInfo(String name, PatternsRequestCondition patterns, RequestMethodsRequestCondition methods,
			ParamsRequestCondition params, HeadersRequestCondition headers, ConsumesRequestCondition consumes,
			ProducesRequestCondition produces, RequestCondition<?> custom) {

		this.name = (StringUtils.hasText(name) ? name : null);
		this.patternsCondition = (patterns != null ? patterns : new PatternsRequestCondition());
		this.methodsCondition = (methods != null ? methods : new RequestMethodsRequestCondition());
		this.paramsCondition = (params != null ? params : new ParamsRequestCondition());
		this.headersCondition = (headers != null ? headers : new HeadersRequestCondition());
		this.consumesCondition = (consumes != null ? consumes : new ConsumesRequestCondition());
		this.producesCondition = (produces != null ? produces : new ProducesRequestCondition());
		this.customConditionHolder = new RequestConditionHolder(custom);
	}

	/**
	 * Creates a new instance with the given request conditions.
	 * <p>
	 *  使用给定的请求条件创建一个新的实例
	 * 
	 */
	public RequestMappingInfo(PatternsRequestCondition patterns, RequestMethodsRequestCondition methods,
			ParamsRequestCondition params, HeadersRequestCondition headers, ConsumesRequestCondition consumes,
			ProducesRequestCondition produces, RequestCondition<?> custom) {

		this(null, patterns, methods, params, headers, consumes, produces, custom);
	}

	/**
	 * Re-create a RequestMappingInfo with the given custom request condition.
	 * <p>
	 *  使用给定的自定义请求条件重新创建一个RequestMappingInfo
	 * 
	 */
	public RequestMappingInfo(RequestMappingInfo info, RequestCondition<?> customRequestCondition) {
		this(info.name, info.patternsCondition, info.methodsCondition, info.paramsCondition, info.headersCondition,
				info.consumesCondition, info.producesCondition, customRequestCondition);
	}


	/**
	 * Return the name for this mapping, or {@code null}.
	 * <p>
	 *  返回此映射的名称,或{@code null}
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the URL patterns of this {@link RequestMappingInfo};
	 * or instance with 0 patterns, never {@code null}.
	 * <p>
	 *  返回此{@link RequestMappingInfo}的URL模式;或具有0个模式的实例,从不{@code null}
	 * 
	 */
	public PatternsRequestCondition getPatternsCondition() {
		return this.patternsCondition;
	}

	/**
	 * Returns the HTTP request methods of this {@link RequestMappingInfo};
	 * or instance with 0 request methods, never {@code null}.
	 * <p>
	 *  返回此{@link RequestMappingInfo}的HTTP请求方法;或具有0请求方法的实例,从不{@code null}
	 * 
	 */
	public RequestMethodsRequestCondition getMethodsCondition() {
		return this.methodsCondition;
	}

	/**
	 * Returns the "parameters" condition of this {@link RequestMappingInfo};
	 * or instance with 0 parameter expressions, never {@code null}.
	 * <p>
	 * 返回此{@link RequestMappingInfo}的"参数"条件;或具有0个参数表达式的实例,从不{@code null}
	 * 
	 */
	public ParamsRequestCondition getParamsCondition() {
		return this.paramsCondition;
	}

	/**
	 * Returns the "headers" condition of this {@link RequestMappingInfo};
	 * or instance with 0 header expressions, never {@code null}.
	 * <p>
	 *  返回此{@link RequestMappingInfo}的"headers"条件;或具有0标题表达式的实例,从不{@code null}
	 * 
	 */
	public HeadersRequestCondition getHeadersCondition() {
		return this.headersCondition;
	}

	/**
	 * Returns the "consumes" condition of this {@link RequestMappingInfo};
	 * or instance with 0 consumes expressions, never {@code null}.
	 * <p>
	 *  返回此{@link RequestMappingInfo}的"消耗"条件;或具有0的实例消耗表达式,从不{@code null}
	 * 
	 */
	public ConsumesRequestCondition getConsumesCondition() {
		return this.consumesCondition;
	}

	/**
	 * Returns the "produces" condition of this {@link RequestMappingInfo};
	 * or instance with 0 produces expressions, never {@code null}.
	 * <p>
	 *  返回此{@link RequestMappingInfo}的"生成"条件;或具有0的实例产生表达式,从不{@code null}
	 * 
	 */
	public ProducesRequestCondition getProducesCondition() {
		return this.producesCondition;
	}

	/**
	 * Returns the "custom" condition of this {@link RequestMappingInfo}; or {@code null}.
	 * <p>
	 *  返回此{@link RequestMappingInfo}的"自定义"条件;或{@code null}
	 * 
	 */
	public RequestCondition<?> getCustomCondition() {
		return this.customConditionHolder.getCondition();
	}


	/**
	 * Combines "this" request mapping info (i.e. the current instance) with another request mapping info instance.
	 * <p>Example: combine type- and method-level request mappings.
	 * <p>
	 *  将"此"请求映射信息(即当前实例)与另一个请求映射信息实例相结合<p>示例：组合类型和方法级请求映射
	 * 
	 * 
	 * @return a new request mapping info instance; never {@code null}
	 */
	@Override
	public RequestMappingInfo combine(RequestMappingInfo other) {
		String name = combineNames(other);
		PatternsRequestCondition patterns = this.patternsCondition.combine(other.patternsCondition);
		RequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
		ParamsRequestCondition params = this.paramsCondition.combine(other.paramsCondition);
		HeadersRequestCondition headers = this.headersCondition.combine(other.headersCondition);
		ConsumesRequestCondition consumes = this.consumesCondition.combine(other.consumesCondition);
		ProducesRequestCondition produces = this.producesCondition.combine(other.producesCondition);
		RequestConditionHolder custom = this.customConditionHolder.combine(other.customConditionHolder);

		return new RequestMappingInfo(name, patterns,
				methods, params, headers, consumes, produces, custom.getCondition());
	}

	private String combineNames(RequestMappingInfo other) {
		if (this.name != null && other.name != null) {
			String separator = RequestMappingInfoHandlerMethodMappingNamingStrategy.SEPARATOR;
			return this.name + separator + other.name;
		}
		else if (this.name != null) {
			return this.name;
		}
		else {
			return (other.name != null ? other.name : null);
		}
	}

	/**
	 * Checks if all conditions in this request mapping info match the provided request and returns
	 * a potentially new request mapping info with conditions tailored to the current request.
	 * <p>For example the returned instance may contain the subset of URL patterns that match to
	 * the current request, sorted with best matching patterns on top.
	 * <p>
	 * 检查此请求映射信息中的所有条件是否与提供的请求匹配,并返回具有针对当前请求的条件的潜在新请求映射信息<p>例如,返回的实例可能包含与当前请求匹配的URL模式子集,按最佳匹配模式排序
	 * 
	 * 
	 * @return a new instance in case all conditions match; or {@code null} otherwise
	 */
	@Override
	public RequestMappingInfo getMatchingCondition(HttpServletRequest request) {
		RequestMethodsRequestCondition methods = this.methodsCondition.getMatchingCondition(request);
		ParamsRequestCondition params = this.paramsCondition.getMatchingCondition(request);
		HeadersRequestCondition headers = this.headersCondition.getMatchingCondition(request);
		ConsumesRequestCondition consumes = this.consumesCondition.getMatchingCondition(request);
		ProducesRequestCondition produces = this.producesCondition.getMatchingCondition(request);

		if (methods == null || params == null || headers == null || consumes == null || produces == null) {
			return null;
		}

		PatternsRequestCondition patterns = this.patternsCondition.getMatchingCondition(request);
		if (patterns == null) {
			return null;
		}

		RequestConditionHolder custom = this.customConditionHolder.getMatchingCondition(request);
		if (custom == null) {
			return null;
		}

		return new RequestMappingInfo(this.name, patterns,
				methods, params, headers, consumes, produces, custom.getCondition());
	}

	/**
	 * Compares "this" info (i.e. the current instance) with another info in the context of a request.
	 * <p>Note: It is assumed both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} to ensure they have conditions with
	 * content relevant to current request.
	 * <p>
	 *  将"this"信息(即当前实例)与请求上下文中的另一个信息进行比较<p>注意：假设两个实例已通过{@link #getMatchingCondition(HttpServletRequest)}获取,
	 * 以确保它们具有内容条件与当前请求相关。
	 * 
	 */
	@Override
	public int compareTo(RequestMappingInfo other, HttpServletRequest request) {
		int result;
		// Automatic vs explicit HTTP HEAD mapping
		if (HttpMethod.HEAD.matches(request.getMethod())) {
			result = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
			if (result != 0) {
				return result;
			}
		}
		result = this.patternsCondition.compareTo(other.getPatternsCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.paramsCondition.compareTo(other.getParamsCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.headersCondition.compareTo(other.getHeadersCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.consumesCondition.compareTo(other.getConsumesCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.producesCondition.compareTo(other.getProducesCondition(), request);
		if (result != 0) {
			return result;
		}
		// Implicit (no method) vs explicit HTTP method mappings
		result = this.methodsCondition.compareTo(other.getMethodsCondition(), request);
		if (result != 0) {
			return result;
		}
		result = this.customConditionHolder.compareTo(other.customConditionHolder, request);
		if (result != 0) {
			return result;
		}
		return 0;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RequestMappingInfo)) {
			return false;
		}
		RequestMappingInfo otherInfo = (RequestMappingInfo) other;
		return (this.patternsCondition.equals(otherInfo.patternsCondition) &&
				this.methodsCondition.equals(otherInfo.methodsCondition) &&
				this.paramsCondition.equals(otherInfo.paramsCondition) &&
				this.headersCondition.equals(otherInfo.headersCondition) &&
				this.consumesCondition.equals(otherInfo.consumesCondition) &&
				this.producesCondition.equals(otherInfo.producesCondition) &&
				this.customConditionHolder.equals(otherInfo.customConditionHolder));
	}

	@Override
	public int hashCode() {
		return (this.patternsCondition.hashCode() * 31 +  // primary differentiation
				this.methodsCondition.hashCode() + this.paramsCondition.hashCode() +
				this.headersCondition.hashCode() + this.consumesCondition.hashCode() +
				this.producesCondition.hashCode() + this.customConditionHolder.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		builder.append(this.patternsCondition);
		if (!this.methodsCondition.isEmpty()) {
			builder.append(",methods=").append(this.methodsCondition);
		}
		if (!this.paramsCondition.isEmpty()) {
			builder.append(",params=").append(this.paramsCondition);
		}
		if (!this.headersCondition.isEmpty()) {
			builder.append(",headers=").append(this.headersCondition);
		}
		if (!this.consumesCondition.isEmpty()) {
			builder.append(",consumes=").append(this.consumesCondition);
		}
		if (!this.producesCondition.isEmpty()) {
			builder.append(",produces=").append(this.producesCondition);
		}
		if (!this.customConditionHolder.isEmpty()) {
			builder.append(",custom=").append(this.customConditionHolder);
		}
		builder.append('}');
		return builder.toString();
	}


	/**
	 * Create a new {@code RequestMappingInfo.Builder} with the given paths.
	 * <p>
	 *  使用给定的路径创建一个新的{@code RequestMappingInfoBuilder}
	 * 
	 * 
	 * @param paths the paths to use
	 * @since 4.2
	 */
	public static Builder paths(String... paths) {
		return new DefaultBuilder(paths);
	}


	/**
	 * Defines a builder for creating a RequestMappingInfo.
	 * <p>
	 *  定义用于创建RequestMappingInfo的构建器
	 * 
	 * 
	 * @since 4.2
	 */
	public interface Builder {

		/**
		 * Set the path patterns.
		 * <p>
		 *  设置路径模式
		 * 
		 */
		Builder paths(String... paths);

		/**
		 * Set the request method conditions.
		 * <p>
		 * 设置请求方法条件
		 * 
		 */
		Builder methods(RequestMethod... methods);

		/**
		 * Set the request param conditions.
		 * <p>
		 *  设置请求参数条件
		 * 
		 */
		Builder params(String... params);

		/**
		 * Set the header conditions.
		 * <p>By default this is not set.
		 * <p>
		 *  设置标题条件<p>默认情况下未设置
		 * 
		 */
		Builder headers(String... headers);

		/**
		 * Set the consumes conditions.
		 * <p>
		 *  设置消耗条件
		 * 
		 */
		Builder consumes(String... consumes);

		/**
		 * Set the produces conditions.
		 * <p>
		 *  设置生产条件
		 * 
		 */
		Builder produces(String... produces);

		/**
		 * Set the mapping name.
		 * <p>
		 *  设置映射名称
		 * 
		 */
		Builder mappingName(String name);

		/**
		 * Set a custom condition to use.
		 * <p>
		 *  设置使用的自定义条件
		 * 
		 */
		Builder customCondition(RequestCondition<?> condition);

		/**
		 * Provide additional configuration needed for request mapping purposes.
		 * <p>
		 *  提供请求映射所需的附加配置
		 * 
		 */
		Builder options(BuilderConfiguration options);

		/**
		 * Build the RequestMappingInfo.
		 * <p>
		 *  构建RequestMappingInfo
		 * 
		 */
		RequestMappingInfo build();
	}


	private static class DefaultBuilder implements Builder {

		private String[] paths;

		private RequestMethod[] methods;

		private String[] params;

		private String[] headers;

		private String[] consumes;

		private String[] produces;

		private String mappingName;

		private RequestCondition<?> customCondition;

		private BuilderConfiguration options = new BuilderConfiguration();

		public DefaultBuilder(String... paths) {
			this.paths = paths;
		}

		@Override
		public Builder paths(String... paths) {
			this.paths = paths;
			return this;
		}

		@Override
		public DefaultBuilder methods(RequestMethod... methods) {
			this.methods = methods;
			return this;
		}

		@Override
		public DefaultBuilder params(String... params) {
			this.params = params;
			return this;
		}

		@Override
		public DefaultBuilder headers(String... headers) {
			this.headers = headers;
			return this;
		}

		@Override
		public DefaultBuilder consumes(String... consumes) {
			this.consumes = consumes;
			return this;
		}

		@Override
		public DefaultBuilder produces(String... produces) {
			this.produces = produces;
			return this;
		}

		@Override
		public DefaultBuilder mappingName(String name) {
			this.mappingName = name;
			return this;
		}

		@Override
		public DefaultBuilder customCondition(RequestCondition<?> condition) {
			this.customCondition = condition;
			return this;
		}

		@Override
		public Builder options(BuilderConfiguration options) {
			this.options = options;
			return this;
		}

		@Override
		public RequestMappingInfo build() {
			ContentNegotiationManager manager = this.options.getContentNegotiationManager();

			PatternsRequestCondition patternsCondition = new PatternsRequestCondition(
					this.paths, this.options.getUrlPathHelper(), this.options.getPathMatcher(),
					this.options.useSuffixPatternMatch(), this.options.useTrailingSlashMatch(),
					this.options.getFileExtensions());

			return new RequestMappingInfo(this.mappingName, patternsCondition,
					new RequestMethodsRequestCondition(methods),
					new ParamsRequestCondition(this.params),
					new HeadersRequestCondition(this.headers),
					new ConsumesRequestCondition(this.consumes, this.headers),
					new ProducesRequestCondition(this.produces, this.headers, manager),
					this.customCondition);
		}
	}


	/**
	 * Container for configuration options used for request mapping purposes.
	 * Such configuration is required to create RequestMappingInfo instances but
	 * is typically used across all RequestMappingInfo instances.
	 * <p>
	 *  用于请求映射目的的配置选项的容器创建RequestMappingInfo实例需要这样的配置,但通常在所有RequestMappingInfo实例中使用
	 * 
	 * 
	 * @since 4.2
	 * @see Builder#options
	 */
	public static class BuilderConfiguration {

		private UrlPathHelper urlPathHelper;

		private PathMatcher pathMatcher;

		private boolean trailingSlashMatch = true;

		private boolean suffixPatternMatch = true;

		private boolean registeredSuffixPatternMatch = false;

		private ContentNegotiationManager contentNegotiationManager;

		/**
		/* <p>
		/* 
		 * @deprecated as of Spring 4.2.8, in favor of {@link #setUrlPathHelper}
		 */
		@Deprecated
		public void setPathHelper(UrlPathHelper pathHelper) {
			this.urlPathHelper = pathHelper;
		}

		/**
		 * Set a custom UrlPathHelper to use for the PatternsRequestCondition.
		 * <p>By default this is not set.
		 * <p>
		 *  设置一个自定义的UrlPathHelper用于PatternsRequestCondition <p>默认情况下,这不是设置
		 * 
		 * 
		 * @since 4.2.8
		 */
		public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
			this.urlPathHelper = urlPathHelper;
		}

		/**
		 * Return a custom UrlPathHelper to use for the PatternsRequestCondition, if any.
		 * <p>
		 *  返回一个用于PatternRequestCondition的自定义UrlPathHelper(如果有)
		 * 
		 */
		public UrlPathHelper getUrlPathHelper() {
			return this.urlPathHelper;
		}

		/**
		 * Set a custom PathMatcher to use for the PatternsRequestCondition.
		 * <p>By default this is not set.
		 * <p>
		 * 设置一个自定义PathMatcher以用于PatternsRequestCondition <p>默认情况下未设置
		 * 
		 */
		public void setPathMatcher(PathMatcher pathMatcher) {
			this.pathMatcher = pathMatcher;
		}

		/**
		 * Return a custom PathMatcher to use for the PatternsRequestCondition, if any.
		 * <p>
		 *  返回一个用于PatternRequestCondition的自定义PathMatcher(如果有)
		 * 
		 */
		public PathMatcher getPathMatcher() {
			return this.pathMatcher;
		}

		/**
		 * Set whether to apply trailing slash matching in PatternsRequestCondition.
		 * <p>By default this is set to 'true'.
		 * <p>
		 *  设置是否在PatternsRequestCondition <p>中应用尾部斜杠匹配默认情况下设置为"true"
		 * 
		 */
		public void setTrailingSlashMatch(boolean trailingSlashMatch) {
			this.trailingSlashMatch = trailingSlashMatch;
		}

		/**
		 * Return whether to apply trailing slash matching in PatternsRequestCondition.
		 * <p>
		 *  返回是否在PatternsRequestCondition中应用尾部斜杠匹配
		 * 
		 */
		public boolean useTrailingSlashMatch() {
			return this.trailingSlashMatch;
		}

		/**
		 * Set whether to apply suffix pattern matching in PatternsRequestCondition.
		 * <p>By default this is set to 'true'.
		 * <p>
		 *  设置是否在PatternsRequestCondition <p>中应用后缀模式匹配默认情况下设置为"true"
		 * 
		 * 
		 * @see #setRegisteredSuffixPatternMatch(boolean)
		 */
		public void setSuffixPatternMatch(boolean suffixPatternMatch) {
			this.suffixPatternMatch = suffixPatternMatch;
		}

		/**
		 * Return whether to apply suffix pattern matching in PatternsRequestCondition.
		 * <p>
		 *  返回是否在PatternsRequestCondition中应用后缀模式匹配
		 * 
		 */
		public boolean useSuffixPatternMatch() {
			return this.suffixPatternMatch;
		}

		/**
		 * Set whether suffix pattern matching should be restricted to registered
		 * file extensions only. Setting this property also sets
		 * {@code suffixPatternMatch=true} and requires that a
		 * {@link #setContentNegotiationManager} is also configured in order to
		 * obtain the registered file extensions.
		 * <p>
		 * 设置后缀模式匹配是否仅限于注册的文件扩展名设置此属性还设置{@code suffixPatternMatch = true},并且还要配置{@link #setContentNegotiationManager}
		 * 以获取注册的文件扩展名。
		 * 
		 */
		public void setRegisteredSuffixPatternMatch(boolean registeredSuffixPatternMatch) {
			this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
			this.suffixPatternMatch = (registeredSuffixPatternMatch || this.suffixPatternMatch);
		}

		/**
		 * Return whether suffix pattern matching should be restricted to registered
		 * file extensions only.
		 * <p>
		 *  返回后缀模式匹配是否应仅限于注册的文件扩展名
		 * 
		 */
		public boolean useRegisteredSuffixPatternMatch() {
			return this.registeredSuffixPatternMatch;
		}

		/**
		 * Return the file extensions to use for suffix pattern matching. If
		 * {@code registeredSuffixPatternMatch=true}, the extensions are obtained
		 * from the configured {@code contentNegotiationManager}.
		 * <p>
		 *  返回用于后缀模式匹配的文件扩展名如果{@code registeredSuffixPatternMatch = true},扩展名是从配置的{@code contentNegotiationManager}
		 * 。
		 * 
		 */
		public List<String> getFileExtensions() {
			if (useRegisteredSuffixPatternMatch() && getContentNegotiationManager() != null) {
				return this.contentNegotiationManager.getAllFileExtensions();
			}
			return null;
		}

		/**
		 * Set the ContentNegotiationManager to use for the ProducesRequestCondition.
		 * <p>By default this is not set.
		 * <p>
		 *  将ContentNegotiationManager设置为用于ProducesRequestCondition <p>默认情况下未设置
		 * 
		 */
		public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
			this.contentNegotiationManager = contentNegotiationManager;
		}

		/**
		 * Return the ContentNegotiationManager to use for the ProducesRequestCondition,
		 * if any.
		 * <p>
		 *  返回ContentNegotiationManager以用于ProducesRequestCondition(如果有的话)
		 */
		public ContentNegotiationManager getContentNegotiationManager() {
			return this.contentNegotiationManager;
		}
	}

}
