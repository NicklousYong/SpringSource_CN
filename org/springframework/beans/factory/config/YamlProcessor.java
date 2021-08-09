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

package org.springframework.beans.factory.config;

import java.io.IOException;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.reader.UnicodeReader;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Base class for YAML factories.
 *
 * <p>
 *  YAML工厂的基础类
 * 
 * 
 * @author Dave Syer
 * @since 4.1
 */
public abstract class YamlProcessor {

	private final Log logger = LogFactory.getLog(getClass());

	private ResolutionMethod resolutionMethod = ResolutionMethod.OVERRIDE;

	private Resource[] resources = new Resource[0];

	private List<DocumentMatcher> documentMatchers = Collections.emptyList();

	private boolean matchDefault = true;


	/**
	 * A map of document matchers allowing callers to selectively use only
	 * some of the documents in a YAML resource. In YAML documents are
	 * separated by <code>---<code> lines, and each document is converted
	 * to properties before the match is made. E.g.
	 * <pre class="code">
	 * environment: dev
	 * url: http://dev.bar.com
	 * name: Developer Setup
	 * ---
	 * environment: prod
	 * url:http://foo.bar.com
	 * name: My Cool App
	 * </pre>
	 * when mapped with
	 * <code>documentMatchers = YamlProcessor.mapMatcher({"environment": "prod"})</code>
	 * would end up as
	 * <pre class="code">
	 * environment=prod
	 * url=http://foo.bar.com
	 * name=My Cool App
	 * url=http://dev.bar.com
	 * </pre>
	 * <p>
	 * 文档匹配器的映射,允许呼叫者选择性地仅使用YAML资源中的一些文档在YAML文档中由<code> --- <code>行分隔,并且在匹配之前将每个文档转换为属性例如
	 * <pre class="code">
	 *  环境：dev url：http：// devbarcom name：Developer Setup --- environment：prod url：http：// foobarcom name：My
	 *  Cool App。
	 * </pre>
	 *  当使用<code> documentMatchers = YamlProcessormapMatcher({"environment"："prod"})映射时,最终将以
	 * <pre class="code">
	 *  环境= prod url = http：// foobarcom name =我的酷App url = http：// devbarcom
	 * </pre>
	 * 
	 * @param matchers a map of keys to value patterns (regular expressions)
	 */
	public void setDocumentMatchers(DocumentMatcher... matchers) {
		this.documentMatchers = Arrays.asList(matchers);
	}

	/**
	 * Flag indicating that a document for which all the
	 * {@link #setDocumentMatchers(DocumentMatcher...) document matchers} abstain will
	 * nevertheless match.
	 * <p>
	 *  指示所有{@link #setDocumentMatchers(DocumentMatcher)文档匹配器}弃权的文档将不会匹配的文档
	 * 
	 * 
	 * @param matchDefault the flag to set (default true)
	 */
	public void setMatchDefault(boolean matchDefault) {
		this.matchDefault = matchDefault;
	}

	/**
	 * Method to use for resolving resources. Each resource will be converted to a Map,
	 * so this property is used to decide which map entries to keep in the final output
	 * from this factory.
	 * <p>
	 * 用于解决资源的方法每个资源都将转换为地图,因此此属性用于确定要保存在该工厂的最终输出中的哪些地图条目
	 * 
	 * 
	 * @param resolutionMethod the resolution method to set (defaults to
	 * {@link ResolutionMethod#OVERRIDE}).
	 */
	public void setResolutionMethod(ResolutionMethod resolutionMethod) {
		Assert.notNull(resolutionMethod, "ResolutionMethod must not be null");
		this.resolutionMethod = resolutionMethod;
	}

	/**
	 * Set locations of YAML {@link Resource resources} to be loaded.
	 * <p>
	 *  设置要加载的YAML {@link资源资源}的位置
	 * 
	 * 
	 * @see ResolutionMethod
	 */
	public void setResources(Resource... resources) {
		this.resources = resources;
	}


	/**
	 * Provide an opportunity for subclasses to process the Yaml parsed from the supplied
	 * resources. Each resource is parsed in turn and the documents inside checked against
	 * the {@link #setDocumentMatchers(DocumentMatcher...) matchers}. If a document
	 * matches it is passed into the callback, along with its representation as Properties.
	 * Depending on the {@link #setResolutionMethod(ResolutionMethod)} not all of the
	 * documents will be parsed.
	 * <p>
	 *  提供子类处理从提供的资源解析的Yaml的机会每个资源依次被解析,内部检查的文档与{@link #setDocumentMatchers(DocumentMatcher)匹配器)匹配}如果文档匹配,它将
	 * 被传递到回调中其表示为属性取决于{@link #setResolutionMethod(ResolutionMethod)),并不是所有的文档都将被解析。
	 * 
	 * 
	 * @param callback a callback to delegate to once matching documents are found
	 * @see #createYaml()
	 */
	protected void process(MatchCallback callback) {
		Yaml yaml = createYaml();
		for (Resource resource : this.resources) {
			boolean found = process(callback, yaml, resource);
			if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND && found) {
				return;
			}
		}
	}

	/**
	 * Create the {@link Yaml} instance to use.
	 * <p>
	 *  创建要使用的{@link Yaml}实例
	 * 
	 */
	protected Yaml createYaml() {
		return new Yaml(new StrictMapAppenderConstructor());
	}

	private boolean process(MatchCallback callback, Yaml yaml, Resource resource) {
		int count = 0;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Loading from YAML: " + resource);
			}
			Reader reader = new UnicodeReader(resource.getInputStream());
			try {
				for (Object object : yaml.loadAll(reader)) {
					if (object != null && process(asMap(object), callback)) {
						count++;
						if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND) {
							break;
						}
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Loaded " + count + " document" + (count > 1 ? "s" : "") +
							" from YAML resource: " + resource);
				}
			}
			finally {
				reader.close();
			}
		}
		catch (IOException ex) {
			handleProcessError(resource, ex);
		}
		return (count > 0);
	}

	private void handleProcessError(Resource resource, IOException ex) {
		if (this.resolutionMethod != ResolutionMethod.FIRST_FOUND &&
				this.resolutionMethod != ResolutionMethod.OVERRIDE_AND_IGNORE) {
			throw new IllegalStateException(ex);
		}
		if (logger.isWarnEnabled()) {
			logger.warn("Could not load map from " + resource + ": " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> asMap(Object object) {
		// YAML can have numbers as keys
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		if (!(object instanceof Map)) {
			// A document can be a text literal
			result.put("document", object);
			return result;
		}

		Map<Object, Object> map = (Map<Object, Object>) object;
		for (Entry<Object, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Map) {
				value = asMap(value);
			}
			Object key = entry.getKey();
			if (key instanceof CharSequence) {
				result.put(key.toString(), value);
			}
			else {
				// It has to be a map key in this case
				result.put("[" + key.toString() + "]", value);
			}
		}
		return result;
	}

	private boolean process(Map<String, Object> map, MatchCallback callback) {
		Properties properties = new Properties();
		properties.putAll(getFlattenedMap(map));

		if (this.documentMatchers.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Merging document (no matchers set): " + map);
			}
			callback.process(properties, map);
			return true;
		}

		MatchStatus result = MatchStatus.ABSTAIN;
		for (DocumentMatcher matcher : this.documentMatchers) {
			MatchStatus match = matcher.matches(properties);
			result = MatchStatus.getMostSpecific(match, result);
			if (match == MatchStatus.FOUND) {
				if (logger.isDebugEnabled()) {
					logger.debug("Matched document with document matcher: " + properties);
				}
				callback.process(properties, map);
				return true;
			}
		}

		if (result == MatchStatus.ABSTAIN && this.matchDefault) {
			if (logger.isDebugEnabled()) {
				logger.debug("Matched document with default matcher: " + map);
			}
			callback.process(properties, map);
			return true;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Unmatched document: " + map);
		}
		return false;
	}

	/**
	 * Return a flattened version of the given map, recursively following any nested Map
	 * or Collection values. Entries from the resulting map retain the same order as the
	 * source. When called with the Map from a {@link MatchCallback} the result will
	 * contain the same values as the {@link MatchCallback} Properties.
	 * <p>
	 * 返回给定地图的扁平版本,递归地跟随任何嵌套的Map或Collection值从结果映射中输入的Entries与源保持相同的顺序当使用Map从{@link MatchCallback}调用时,结果将包含与{@link MatchCallback}
	 * 属性。
	 * 
	 * 
	 * @param source the source map
	 * @return a flattened map
	 * @since 4.1.3
	 */
	protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		buildFlattenedMap(result, source, null);
		return result;
	}

	private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
		for (Entry<String, Object> entry : source.entrySet()) {
			String key = entry.getKey();
			if (StringUtils.hasText(path)) {
				if (key.startsWith("[")) {
					key = path + key;
				}
				else {
					key = path + "." + key;
				}
			}
			Object value = entry.getValue();
			if (value instanceof String) {
				result.put(key, value);
			}
			else if (value instanceof Map) {
				// Need a compound key
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) value;
				buildFlattenedMap(result, map, key);
			}
			else if (value instanceof Collection) {
				// Need a compound key
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) value;
				int count = 0;
				for (Object object : collection) {
					buildFlattenedMap(result,
							Collections.singletonMap("[" + (count++) + "]", object), key);
				}
			}
			else {
				result.put(key, value != null ? value : "");
			}
		}
	}


	/**
	 * Callback interface used to process properties in a resulting map.
	 * <p>
	 *  回调界面用于处理结果图中的属性
	 * 
	 */
	public interface MatchCallback {

		/**
		 * Process the properties.
		 * <p>
		 *  处理属性
		 * 
		 * 
		 * @param properties the properties to process
		 * @param map a mutable result map
		 */
		void process(Properties properties, Map<String, Object> map);
	}


	/**
	 * Strategy interface used to test if properties match.
	 * <p>
	 *  用于测试属性是否匹配的策略界面
	 * 
	 */
	public interface DocumentMatcher {

		/**
		 * Test if the given properties match.
		 * <p>
		 *  测试给定属性是否匹配
		 * 
		 * 
		 * @param properties the properties to test
		 * @return the status of the match
		 */
		MatchStatus matches(Properties properties);
	}


	/**
	 * Status returned from {@link DocumentMatcher#matches(java.util.Properties)}
	 * <p>
	 *  从{@link DocumentMatcher#matches(javautilProperties)}返回的状态
	 * 
	 */
	public enum MatchStatus {

		/**
		 * A match was found.
		 * <p>
		 *  发现了一场比赛
		 * 
		 */
		FOUND,

		/**
		 * No match was found.
		 * <p>
		 *  没有找到匹配
		 * 
		 */
		NOT_FOUND,

		/**
		 * The matcher should not be considered.
		 * <p>
		 *  不应该考虑匹配器
		 * 
		 */
		ABSTAIN;

		/**
		 * Compare two {@link MatchStatus} items, returning the most specific status.
		 * <p>
		 *  比较两个{@link MatchStatus}项,返回最具体的状态
		 * 
		 */
		public static MatchStatus getMostSpecific(MatchStatus a, MatchStatus b) {
			return (a.ordinal() < b.ordinal() ? a : b);
		}
	}


	/**
	 * Method to use for resolving resources.
	 * <p>
	 *  用于解决资源的方法
	 * 
	 */
	public enum ResolutionMethod {

		/**
		 * Replace values from earlier in the list.
		 * <p>
		 * 从列表中替换较早的值
		 * 
		 */
		OVERRIDE,

		/**
		 * Replace values from earlier in the list, ignoring any failures.
		 * <p>
		 *  替换列表中较早的值,忽略任何故障
		 * 
		 */
		OVERRIDE_AND_IGNORE,

		/**
		 * Take the first resource in the list that exists and use just that.
		 * <p>
		 *  拿出存在的列表中的第一个资源,并使用它
		 * 
		 */
		FIRST_FOUND
	}


	/**
	 * A specialized {@link Constructor} that checks for duplicate keys.
	 * <p>
	 *  一个专门的{@link构造函数}来检查重复的键
	 */
	protected static class StrictMapAppenderConstructor extends Constructor {

		// Declared as public for use in subclasses
		public StrictMapAppenderConstructor() {
			super();
		}

		@Override
		protected Map<Object, Object> constructMapping(MappingNode node) {
			try {
				return super.constructMapping(node);
			}
			catch (IllegalStateException ex) {
				throw new ParserException("while parsing MappingNode",
						node.getStartMark(), ex.getMessage(), node.getEndMark());
			}
		}

		@Override
		protected Map<Object, Object> createDefaultMap() {
			final Map<Object, Object> delegate = super.createDefaultMap();
			return new AbstractMap<Object, Object>() {
				@Override
				public Object put(Object key, Object value) {
					if (delegate.containsKey(key)) {
						throw new IllegalStateException("Duplicate key: " + key);
					}
					return delegate.put(key, value);
				}
				@Override
				public Set<Entry<Object, Object>> entrySet() {
					return delegate.entrySet();
				}
			};
		}
	}

}
