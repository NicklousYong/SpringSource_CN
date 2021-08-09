/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

/**
 * Resolves request paths containing a version string that can be used as part
 * of an HTTP caching strategy in which a resource is cached with a date in the
 * distant future (e.g. 1 year) and cached until the version, and therefore the
 * URL, is changed.
 *
 * <p>Different versioning strategies exist, and this resolver must be configured
 * with one or more such strategies along with path mappings to indicate which
 * strategy applies to which resources.
 *
 * <p>{@code ContentVersionStrategy} is a good default choice except in cases
 * where it cannot be used. Most notably the {@code ContentVersionStrategy}
 * cannot be combined with JavaScript module loaders. For such cases the
 * {@code FixedVersionStrategy} is a better choice.
 *
 * <p>Note that using this resolver to serve CSS files means that the
 * {@link CssLinkResourceTransformer} should also be used in order to modify
 * links within CSS files to also contain the appropriate versions generated
 * by this resolver.
 *
 * <p>
 * 解决包含可用作HTTP缓存策略的一部分的版本字符串的请求路径,其中资源在遥远的将来被缓存有日期(例如1年),并缓存直到版本,因此URL被更改
 * 
 *  <p>存在不同的版本化策略,此解析器必须配置一个或多个此类策略以及路径映射,以指示哪种策略适用于哪些资源
 * 
 *  除了不能使用的情况下,<p> {@ code ContentVersionStrategy}是一个很好的默认选择。
 * 最引人注目的是,{@code ContentVersionStrategy}不能与JavaScript模块加载器组合在这种情况下,{@code FixedVersionStrategy}是一个更好的选择
 * 。
 *  除了不能使用的情况下,<p> {@ code ContentVersionStrategy}是一个很好的默认选择。
 * 
 * 请注意,使用此解析器提供CSS文件意味着还应使用{@link CssLinkResourceTransformer}来修改CSS文件中的链接,以便还包含此解析器生成的适当版本
 * 
 * 
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see VersionStrategy
 */
public class VersionResourceResolver extends AbstractResourceResolver {

	private AntPathMatcher pathMatcher = new AntPathMatcher();

	/** Map from path pattern -> VersionStrategy */
	private final Map<String, VersionStrategy> versionStrategyMap = new LinkedHashMap<String, VersionStrategy>();


	/**
	 * Set a Map with URL paths as keys and {@code VersionStrategy} as values.
	 * <p>Supports direct URL matches and Ant-style pattern matches. For syntax
	 * details, see the {@link org.springframework.util.AntPathMatcher} javadoc.
	 * <p>
	 *  设置一个具有URL路径的地图作为键和{@code VersionStrategy}作为值<p>支持直接URL匹配和Ant样式模式匹配有关语法详细信息,请参阅{@link orgspringframeworkutilAntPathMatcher}
	 *  javadoc。
	 * 
	 * 
	 * @param map map with URLs as keys and version strategies as values
	 */
	public void setStrategyMap(Map<String, VersionStrategy> map) {
		this.versionStrategyMap.clear();
		this.versionStrategyMap.putAll(map);
	}

	/**
	 * Return the map with version strategies keyed by path pattern.
	 * <p>
	 *  用路径模式键入的版本策略返回地图
	 * 
	 */
	public Map<String, VersionStrategy> getStrategyMap() {
		return this.versionStrategyMap;
	}

	/**
	 * Insert a content-based version in resource URLs that match the given path
	 * patterns. The version is computed from the content of the file, e.g.
	 * {@code "css/main-e36d2e05253c6c7085a91522ce43a0b4.css"}. This is a good
	 * default strategy to use except when it cannot be, for example when using
	 * JavaScript module loaders, use {@link #addFixedVersionStrategy} instead
	 * for serving JavaScript files.
	 * <p>
	 * 在符合给定路径模式的资源URL中插入基于内容的版本该版本是从文件的内容计算的,例如{@code"css / main-e36d2e05253c6c7085a91522ce43a0b4css"}这是一个很
	 * 好的默认策略,除非它不能例如,当使用JavaScript模块加载器时,请使用{@link #addFixedVersionStrategy}来投放JavaScript文件。
	 * 
	 * 
	 * @param pathPatterns one or more resource URL path patterns
	 * @return the current instance for chained method invocation
	 * @see ContentVersionStrategy
	 */
	public VersionResourceResolver addContentVersionStrategy(String... pathPatterns) {
		addVersionStrategy(new ContentVersionStrategy(), pathPatterns);
		return this;
	}

	/**
	 * Insert a fixed, prefix-based version in resource URLs that match the given
	 * path patterns, for example: <code>"{version}/js/main.js"</code>. This is useful (vs.
	 * content-based versions) when using JavaScript module loaders.
	 * <p>The version may be a random number, the current date, or a value
	 * fetched from a git commit sha, a property file, or environment variable
	 * and set with SpEL expressions in the configuration (e.g. see {@code @Value}
	 * in Java config).
	 * <p>If not done already, variants of the given {@code pathPatterns}, prefixed with
	 * the {@code version} will be also configured. For example, adding a {@code "/js/**"} path pattern
	 * will also cofigure automatically a {@code "/v1.0.0/js/**"} with {@code "v1.0.0"} the
	 * {@code version} String given as an argument.
	 * <p>
	 * 在符合给定路径模式的资源网址中插入一个固定的基于前缀的版本,例如：<code>"{version} / js / mainjs"</code>在使用JavaScript时,这非常有用(与基于内容的版本)
	 * 模块加载程序<p>版本可以是随机数,当前日期,或从git commit sha,属性文件或环境变量中提取的值,并在配置中使用SpEL表达式设置(例如,参见{@code @Value }在Java配置中)
	 * <p>如果还没有完成,还将配置给定{@code pathPatterns}的变体,前缀为{@code version}。
	 * 例如,添加{@code"/ js / **" }路径模式还将自动配置{@code"/ v100 / js / **"}与{@code"v100"}作为参数给出的{@code version}字符串。
	 * 
	 * 
	 * @param version a version string
	 * @param pathPatterns one or more resource URL path patterns
	 * @return the current instance for chained method invocation
	 * @see FixedVersionStrategy
	 */
	public VersionResourceResolver addFixedVersionStrategy(String version, String... pathPatterns) {
		List<String> patternsList = Arrays.asList(pathPatterns);
		List<String> prefixedPatterns = new ArrayList<String>(pathPatterns.length);
		String versionPrefix = "/" + version;
		for (String pattern : patternsList) {
			prefixedPatterns.add(pattern);
			if (!pattern.startsWith(versionPrefix) && !patternsList.contains(versionPrefix + pattern)) {
				prefixedPatterns.add(versionPrefix + pattern);
			}
		}
		return addVersionStrategy(new FixedVersionStrategy(version), prefixedPatterns.toArray(new String[0]));
	}

	/**
	 * Register a custom VersionStrategy to apply to resource URLs that match the
	 * given path patterns.
	 * <p>
	 * 注册一个自定义的VersionStrategy以应用于与给定路径模式匹配的资源URL
	 * 
	 * 
	 * @param strategy the custom strategy
	 * @param pathPatterns one or more resource URL path patterns
	 * @return the current instance for chained method invocation
	 * @see VersionStrategy
	 */
	public VersionResourceResolver addVersionStrategy(VersionStrategy strategy, String... pathPatterns) {
		for (String pattern : pathPatterns) {
			getStrategyMap().put(pattern, strategy);
		}
		return this;
	}


	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
			List<? extends Resource> locations, ResourceResolverChain chain) {

		Resource resolved = chain.resolveResource(request, requestPath, locations);
		if (resolved != null) {
			return resolved;
		}

		VersionStrategy versionStrategy = getStrategyForPath(requestPath);
		if (versionStrategy == null) {
			return null;
		}

		String candidateVersion = versionStrategy.extractVersion(requestPath);
		if (StringUtils.isEmpty(candidateVersion)) {
			if (logger.isTraceEnabled()) {
				logger.trace("No version found in path \"" + requestPath + "\"");
			}
			return null;
		}

		String simplePath = versionStrategy.removeVersion(requestPath, candidateVersion);
		if (logger.isTraceEnabled()) {
			logger.trace("Extracted version from path, re-resolving without version: \"" + simplePath + "\"");
		}

		Resource baseResource = chain.resolveResource(request, simplePath, locations);
		if (baseResource == null) {
			return null;
		}

		String actualVersion = versionStrategy.getResourceVersion(baseResource);
		if (candidateVersion.equals(actualVersion)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Resource matches extracted version [" + candidateVersion + "]");
			}
			return new FileNameVersionedResource(baseResource, candidateVersion);
		}
		else {
			if (logger.isTraceEnabled()) {
				logger.trace("Potential resource found for \"" + requestPath + "\", but version [" +
						candidateVersion + "] does not match");
			}
			return null;
		}
	}

	@Override
	protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
		String baseUrl = chain.resolveUrlPath(resourceUrlPath, locations);
		if (StringUtils.hasText(baseUrl)) {
			VersionStrategy versionStrategy = getStrategyForPath(resourceUrlPath);
			if (versionStrategy == null) {
				return null;
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Getting the original resource to determine version for path \"" + resourceUrlPath + "\"");
			}
			Resource resource = chain.resolveResource(null, baseUrl, locations);
			String version = versionStrategy.getResourceVersion(resource);
			if (logger.isTraceEnabled()) {
				logger.trace("Determined version [" + version + "] for " + resource);
			}
			return versionStrategy.addVersion(baseUrl, version);
		}
		return baseUrl;
	}

	/**
	 * Find a {@code VersionStrategy} for the request path of the requested resource.
	 * <p>
	 *  查找所请求资源的请求路径的{@code VersionStrategy}
	 * 
	 * @return an instance of a {@code VersionStrategy} or null if none matches that request path
	 */
	protected VersionStrategy getStrategyForPath(String requestPath) {
		String path = "/".concat(requestPath);
		List<String> matchingPatterns = new ArrayList<String>();
		for (String pattern : this.versionStrategyMap.keySet()) {
			if (this.pathMatcher.match(pattern, path)) {
				matchingPatterns.add(pattern);
			}
		}
		if (!matchingPatterns.isEmpty()) {
			Comparator<String> comparator = this.pathMatcher.getPatternComparator(path);
			Collections.sort(matchingPatterns, comparator);
			return this.versionStrategyMap.get(matchingPatterns.get(0));
		}
		return null;
	}


	private class FileNameVersionedResource extends AbstractResource implements VersionedResource {

		private final Resource original;

		private final String version;

		public FileNameVersionedResource(Resource original, String version) {
			this.original = original;
			this.version = version;
		}

		@Override
		public boolean exists() {
			return this.original.exists();
		}

		@Override
		public boolean isReadable() {
			return this.original.isReadable();
		}

		@Override
		public boolean isOpen() {
			return this.original.isOpen();
		}

		@Override
		public URL getURL() throws IOException {
			return this.original.getURL();
		}

		@Override
		public URI getURI() throws IOException {
			return this.original.getURI();
		}

		@Override
		public File getFile() throws IOException {
			return this.original.getFile();
		}

		@Override
		public String getFilename() {
			return this.original.getFilename();
		}

		@Override
		public long contentLength() throws IOException {
			return this.original.contentLength();
		}

		@Override
		public long lastModified() throws IOException {
			return this.original.lastModified();
		}

		@Override
		public Resource createRelative(String relativePath) throws IOException {
			return this.original.createRelative(relativePath);
		}

		@Override
		public String getDescription() {
			return original.getDescription();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return original.getInputStream();
		}

		@Override
		public String getVersion() {
			return this.version;
		}
	}

}
