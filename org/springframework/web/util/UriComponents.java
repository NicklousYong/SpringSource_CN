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

package org.springframework.web.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * Represents an immutable collection of URI components, mapping component type to
 * String values. Contains convenience getters for all components. Effectively similar
 * to {@link java.net.URI}, but with more powerful encoding options and support for
 * URI template variables.
 *
 * <p>
 * 表示URI组件的不可变集合,将组件类型映射为字符串值包含所有组件的便利getter有效地类似于{@link javanetURI},但具有更强大的编码选项和对URI模板变量的支持
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.1
 * @see UriComponentsBuilder
 */
@SuppressWarnings("serial")
public abstract class UriComponents implements Serializable {

	private static final String DEFAULT_ENCODING = "UTF-8";

	/** Captures URI template variable names. */
	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");


	private final String scheme;

	private final String fragment;


	protected UriComponents(String scheme, String fragment) {
		this.scheme = scheme;
		this.fragment = fragment;
	}


	// component getters

	/**
	 * Returns the scheme. Can be {@code null}.
	 * <p>
	 *  返回方案可以{@code null}
	 * 
	 */
	public final String getScheme() {
		return this.scheme;
	}

	/**
	 * Returns the scheme specific part. Can be {@code null}.
	 * <p>
	 *  返回方案具体部分可以{@code null}
	 * 
	 */
	public abstract String getSchemeSpecificPart();

	/**
	 * Returns the user info. Can be {@code null}.
	 * <p>
	 *  返回用户信息可以{@code null}
	 * 
	 */
	public abstract String getUserInfo();

	/**
	 * Returns the host. Can be {@code null}.
	 * <p>
	 *  返回主机可以{@code null}
	 * 
	 */
	public abstract String getHost();

	/**
	 * Returns the port. Returns {@code -1} if no port has been set.
	 * <p>
	 *  如果没有设置端口,则返回端口Returns {@code -1}
	 * 
	 */
	public abstract int getPort();

	/**
	 * Returns the path. Can be {@code null}.
	 * <p>
	 *  返回路径可以{@code null}
	 * 
	 */
	public abstract String getPath();

	/**
	 * Returns the list of path segments. Empty if no path has been set.
	 * <p>
	 *  返回路径段列表如果没有设置路径,则为空
	 * 
	 */
	public abstract List<String> getPathSegments();

	/**
	 * Returns the query. Can be {@code null}.
	 * <p>
	 *  返回查询可以{@code null}
	 * 
	 */
	public abstract String getQuery();

	/**
	 * Returns the map of query parameters. Empty if no query has been set.
	 * <p>
	 *  返回查询参数的映射空如果没有设置查询
	 * 
	 */
	public abstract MultiValueMap<String, String> getQueryParams();

	/**
	 * Returns the fragment. Can be {@code null}.
	 * <p>
	 *  返回片段可以{@code null}
	 * 
	 */
	public final String getFragment() {
		return this.fragment;
	}


	/**
	 * Encode all URI components using their specific encoding rules, and returns the
	 * result as a new {@code UriComponents} instance. This method uses UTF-8 to encode.
	 * <p>
	 * 使用其特定编码规则编码所有URI组件,并将结果作为新的{@code UriComponents}实例返回此方法使用UTF-8进行编码
	 * 
	 * 
	 * @return the encoded URI components
	 */
	public final UriComponents encode() {
		try {
			return encode(DEFAULT_ENCODING);
		}
		catch (UnsupportedEncodingException ex) {
			// should not occur
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Encode all URI components using their specific encoding rules, and
	 * returns the result as a new {@code UriComponents} instance.
	 * <p>
	 *  使用其特定的编码规则编码所有URI组件,并将结果作为新的{@code UriComponents}实例返回
	 * 
	 * 
	 * @param encoding the encoding of the values contained in this map
	 * @return the encoded URI components
	 * @throws UnsupportedEncodingException if the given encoding is not supported
	 */
	public abstract UriComponents encode(String encoding) throws UnsupportedEncodingException;

	/**
	 * Replace all URI template variables with the values from a given map.
	 * <p>The given map keys represent variable names; the corresponding values
	 * represent variable values. The order of variables is not significant.
	 * <p>
	 *  使用给定地图中的值替换所有URI模板变量<p>给定的地图键表示变量名称;相应的值表示变量值变量的顺序不重要
	 * 
	 * 
	 * @param uriVariables the map of URI variables
	 * @return the expanded URI components
	 */
	public final UriComponents expand(Map<String, ?> uriVariables) {
		Assert.notNull(uriVariables, "'uriVariables' must not be null");
		return expandInternal(new MapTemplateVariables(uriVariables));
	}

	/**
	 * Replace all URI template variables with the values from a given array.
	 * <p>The given array represents variable values. The order of variables is significant.
	 * <p>
	 *  使用给定数组中的值替换所有URI模板变量<p>给定的数组表示变量值变量的顺序是重要的
	 * 
	 * 
	 * @param uriVariableValues the URI variable values
	 * @return the expanded URI components
	 */
	public final UriComponents expand(Object... uriVariableValues) {
		Assert.notNull(uriVariableValues, "'uriVariableValues' must not be null");
		return expandInternal(new VarArgsTemplateVariables(uriVariableValues));
	}

	/**
	 * Replace all URI template variables with the values from the given
	 * {@link UriTemplateVariables}.
	 * <p>
	 *  将所有URI模板变量替换为给定的{@link UriTemplateVariables}
	 * 
	 * 
	 * @param uriVariables the URI template values
	 * @return the expanded URI components
	 */
	public final UriComponents expand(UriTemplateVariables uriVariables) {
		Assert.notNull(uriVariables, "'uriVariables' must not be null");
		return expandInternal(uriVariables);
	}

	/**
	 * Replace all URI template variables with the values from the given {@link
	 * UriTemplateVariables}
	 * <p>
	 * 将所有URI模板变量替换为给定的{@link UriTemplateVariables}
	 * 
	 * 
	 * @param uriVariables URI template values
	 * @return the expanded uri components
	 */
	abstract UriComponents expandInternal(UriTemplateVariables uriVariables);

	/**
	 * Normalize the path removing sequences like "path/..".
	 * <p>
	 *  归一化路径删除序列,如"path /"
	 * 
	 * 
	 * @see org.springframework.util.StringUtils#cleanPath(String)
	 */
	public abstract UriComponents normalize();

	/**
	 * Return a URI string from this {@code UriComponents} instance.
	 * <p>
	 *  从这个{@code UriComponents}实例返回URI字符串
	 * 
	 */
	public abstract String toUriString();

	/**
	 * Return a {@code URI} from this {@code UriComponents} instance.
	 * <p>
	 *  从{@code UriComponents}实例返回{@code URI}
	 * 
	 */
	public abstract URI toUri();

	@Override
	public final String toString() {
		return toUriString();
	}

	/**
	 * Set all components of the given UriComponentsBuilder.
	 * <p>
	 *  设置给定UriComponentsBuilder的所有组件
	 * 
	 * 
	 * @since 4.2
	 */
	protected abstract void copyToUriComponentsBuilder(UriComponentsBuilder builder);


	// static expansion helpers

	static String expandUriComponent(String source, UriTemplateVariables uriVariables) {
		if (source == null) {
			return null;
		}
		if (source.indexOf('{') == -1) {
			return source;
		}
		if (source.indexOf(':') != -1) {
			source = sanitizeSource(source);
		}
		Matcher matcher = NAMES_PATTERN.matcher(source);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String match = matcher.group(1);
			String variableName = getVariableName(match);
			Object variableValue = uriVariables.getValue(variableName);
			if (UriTemplateVariables.SKIP_VALUE.equals(variableValue)) {
				continue;
			}
			String variableValueString = getVariableValueAsString(variableValue);
			String replacement = Matcher.quoteReplacement(variableValueString);
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Remove nested "{}" such as in URI vars with regular expressions.
	 * <p>
	 *  删除嵌套的"{}",例如使用正则表达式的URI vars
	 * 
	 */
	private static String sanitizeSource(String source) {
		int level = 0;
		StringBuilder sb = new StringBuilder();
		for (char c : source.toCharArray()) {
			if (c == '{') {
				level++;
			}
			if (c == '}') {
				level--;
			}
			if (level > 1 || (level == 1 && c == '}')) {
				continue;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	private static String getVariableName(String match) {
		int colonIdx = match.indexOf(':');
		return (colonIdx != -1 ? match.substring(0, colonIdx) : match);
	}

	private static String getVariableValueAsString(Object variableValue) {
		return (variableValue != null ? variableValue.toString() : "");
	}


	/**
	 * Defines the contract for URI Template variables
	 * <p>
	 *  定义URI模板变量的合同
	 * 
	 * 
	 * @see HierarchicalUriComponents#expand
	 */
	public interface UriTemplateVariables {

		Object SKIP_VALUE = UriTemplateVariables.class;

		/**
		 * Get the value for the given URI variable name.
		 * If the value is {@code null}, an empty String is expanded.
		 * If the value is {@link #SKIP_VALUE}, the URI variable is not expanded.
		 * <p>
		 *  获取给定URI变量名的值如果值为{@code null},则扩展一个空字符串如果值为{@link #SKIP_VALUE},则URI变量不会展开
		 * 
		 * 
		 * @param name the variable name
		 * @return the variable value, possibly {@code null} or {@link #SKIP_VALUE}
		 */
		Object getValue(String name);
	}


	/**
	 * URI template variables backed by a map.
	 * <p>
	 *  由地图支持的URI模板变量
	 * 
	 */
	private static class MapTemplateVariables implements UriTemplateVariables {

		private final Map<String, ?> uriVariables;

		public MapTemplateVariables(Map<String, ?> uriVariables) {
			this.uriVariables = uriVariables;
		}

		@Override
		public Object getValue(String name) {
			if (!this.uriVariables.containsKey(name)) {
				throw new IllegalArgumentException("Map has no value for '" + name + "'");
			}
			return this.uriVariables.get(name);
		}
	}


	/**
	 * URI template variables backed by a variable argument array.
	 * <p>
	 *  URI模板变量由变量参数数组支持
	 */
	private static class VarArgsTemplateVariables implements UriTemplateVariables {

		private final Iterator<Object> valueIterator;

		public VarArgsTemplateVariables(Object... uriVariableValues) {
			this.valueIterator = Arrays.asList(uriVariableValues).iterator();
		}

		@Override
		public Object getValue(String name) {
			if (!this.valueIterator.hasNext()) {
				throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
			}
			return this.valueIterator.next();
		}
	}

}
