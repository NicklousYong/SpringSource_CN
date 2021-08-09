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

package org.springframework.web.servlet.mvc.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

/**
 * A logical disjunction (' || ') request condition that matches a request
 * against a set of URL path patterns.
 *
 * <p>
 *  一个逻辑分离('||')请求条件,将请求与一组URL路径模式相匹配
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class PatternsRequestCondition extends AbstractRequestCondition<PatternsRequestCondition> {

	private final Set<String> patterns;

	private final UrlPathHelper pathHelper;

	private final PathMatcher pathMatcher;

	private final boolean useSuffixPatternMatch;

	private final boolean useTrailingSlashMatch;

	private final List<String> fileExtensions = new ArrayList<String>();


	/**
	 * Creates a new instance with the given URL patterns.
	 * Each pattern that is not empty and does not start with "/" is prepended with "/".
	 * <p>
	 * 使用给定的URL模式创建一个新的实例每个不是空的模式并不以"/"开头都加上"/"
	 * 
	 * 
	 * @param patterns 0 or more URL patterns; if 0 the condition will match to every request.
	 */
	public PatternsRequestCondition(String... patterns) {
		this(asList(patterns), null, null, true, true, null);
	}

	/**
	 * Additional constructor with flags for using suffix pattern (.*) and
	 * trailing slash matches.
	 * <p>
	 *  带有标志的附加构造函数,用于使用后缀模式(*)和尾部斜杠匹配
	 * 
	 * 
	 * @param patterns the URL patterns to use; if 0, the condition will match to every request.
	 * @param urlPathHelper for determining the lookup path of a request
	 * @param pathMatcher for path matching with patterns
	 * @param useSuffixPatternMatch whether to enable matching by suffix (".*")
	 * @param useTrailingSlashMatch whether to match irrespective of a trailing slash
	 */
	public PatternsRequestCondition(String[] patterns, UrlPathHelper urlPathHelper, PathMatcher pathMatcher,
			boolean useSuffixPatternMatch, boolean useTrailingSlashMatch) {

		this(asList(patterns), urlPathHelper, pathMatcher, useSuffixPatternMatch, useTrailingSlashMatch, null);
	}

	/**
	 * Creates a new instance with the given URL patterns.
	 * Each pattern that is not empty and does not start with "/" is pre-pended with "/".
	 * <p>
	 *  使用给定的URL模式创建新实例每个不为空且不以"/"开头的模式都是以"/"开头的
	 * 
	 * 
	 * @param patterns the URL patterns to use; if 0, the condition will match to every request.
	 * @param urlPathHelper a {@link UrlPathHelper} for determining the lookup path for a request
	 * @param pathMatcher a {@link PathMatcher} for pattern path matching
	 * @param useSuffixPatternMatch whether to enable matching by suffix (".*")
	 * @param useTrailingSlashMatch whether to match irrespective of a trailing slash
	 * @param fileExtensions a list of file extensions to consider for path matching
	 */
	public PatternsRequestCondition(String[] patterns, UrlPathHelper urlPathHelper,
			PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch,
			List<String> fileExtensions) {

		this(asList(patterns), urlPathHelper, pathMatcher, useSuffixPatternMatch, useTrailingSlashMatch, fileExtensions);
	}

	/**
	 * Private constructor accepting a collection of patterns.
	 * <p>
	 *  接受模式集合的私有构造函数
	 * 
	 */
	private PatternsRequestCondition(Collection<String> patterns, UrlPathHelper urlPathHelper,
			PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch,
			List<String> fileExtensions) {

		this.patterns = Collections.unmodifiableSet(prependLeadingSlash(patterns));
		this.pathHelper = (urlPathHelper != null ? urlPathHelper : new UrlPathHelper());
		this.pathMatcher = (pathMatcher != null ? pathMatcher : new AntPathMatcher());
		this.useSuffixPatternMatch = useSuffixPatternMatch;
		this.useTrailingSlashMatch = useTrailingSlashMatch;
		if (fileExtensions != null) {
			for (String fileExtension : fileExtensions) {
				if (fileExtension.charAt(0) != '.') {
					fileExtension = "." + fileExtension;
				}
				this.fileExtensions.add(fileExtension);
			}
		}
	}


	private static List<String> asList(String... patterns) {
		return (patterns != null ? Arrays.asList(patterns) : Collections.<String>emptyList());
	}

	private static Set<String> prependLeadingSlash(Collection<String> patterns) {
		if (patterns == null) {
			return Collections.emptySet();
		}
		Set<String> result = new LinkedHashSet<String>(patterns.size());
		for (String pattern : patterns) {
			if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
				pattern = "/" + pattern;
			}
			result.add(pattern);
		}
		return result;
	}

	public Set<String> getPatterns() {
		return this.patterns;
	}

	@Override
	protected Collection<String> getContent() {
		return this.patterns;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	/**
	 * Returns a new instance with URL patterns from the current instance ("this") and
	 * the "other" instance as follows:
	 * <ul>
	 * <li>If there are patterns in both instances, combine the patterns in "this" with
	 * the patterns in "other" using {@link PathMatcher#combine(String, String)}.
	 * <li>If only one instance has patterns, use them.
	 * <li>If neither instance has patterns, use an empty String (i.e. "").
	 * </ul>
	 * <p>
	 *  从当前实例("this")和"other"实例返回一个具有URL模式的新实例,如下所示：
	 * <ul>
	 * 如果两个实例中都有模式,则使用{@link PathMatcher#combine(String,String)} <li>将"this"中的模式与"other"中的模式相结合如果只有一个实例具有模式,
	 * 请使用它们<li>如果两个实例都不具有模式,请使用空字符串(即"")。
	 * </ul>
	 */
	@Override
	public PatternsRequestCondition combine(PatternsRequestCondition other) {
		Set<String> result = new LinkedHashSet<String>();
		if (!this.patterns.isEmpty() && !other.patterns.isEmpty()) {
			for (String pattern1 : this.patterns) {
				for (String pattern2 : other.patterns) {
					result.add(this.pathMatcher.combine(pattern1, pattern2));
				}
			}
		}
		else if (!this.patterns.isEmpty()) {
			result.addAll(this.patterns);
		}
		else if (!other.patterns.isEmpty()) {
			result.addAll(other.patterns);
		}
		else {
			result.add("");
		}
		return new PatternsRequestCondition(result, this.pathHelper, this.pathMatcher, this.useSuffixPatternMatch,
				this.useTrailingSlashMatch, this.fileExtensions);
	}

	/**
	 * Checks if any of the patterns match the given request and returns an instance
	 * that is guaranteed to contain matching patterns, sorted via
	 * {@link PathMatcher#getPatternComparator(String)}.
	 * <p>A matching pattern is obtained by making checks in the following order:
	 * <ul>
	 * <li>Direct match
	 * <li>Pattern match with ".*" appended if the pattern doesn't already contain a "."
	 * <li>Pattern match
	 * <li>Pattern match with "/" appended if the pattern doesn't already end in "/"
	 * </ul>
	 * <p>
	 *  检查任何模式是否匹配给定的请求,并返回一个保证包含匹配模式的实例,通过{@link PathMatcher#getPatternComparator(String)}排序)通过按以下顺序进行检查获得匹
	 * 配模式：。
	 * <ul>
	 *  <li>直接匹配<li>如果模式不包含"",则模式匹配与"*"附加模式匹配<li>如果模式尚未结束,则匹配"/" "/"
	 * </ul>
	 * 
	 * @param request the current request
	 * @return the same instance if the condition contains no patterns;
	 * or a new condition with sorted matching patterns;
	 * or {@code null} if no patterns match.
	 */
	@Override
	public PatternsRequestCondition getMatchingCondition(HttpServletRequest request) {

		if (this.patterns.isEmpty()) {
			return this;
		}

		String lookupPath = this.pathHelper.getLookupPathForRequest(request);
		List<String> matches = getMatchingPatterns(lookupPath);

		return matches.isEmpty() ? null :
			new PatternsRequestCondition(matches, this.pathHelper, this.pathMatcher, this.useSuffixPatternMatch,
					this.useTrailingSlashMatch, this.fileExtensions);
	}

	/**
	 * Find the patterns matching the given lookup path. Invoking this method should
	 * yield results equivalent to those of calling
	 * {@link #getMatchingCondition(javax.servlet.http.HttpServletRequest)}.
	 * This method is provided as an alternative to be used if no request is available
	 * (e.g. introspection, tooling, etc).
	 * <p>
	 * 查找匹配给定查找路径的模式调用此方法应该产生与调用{@link #getMatchingCondition(javaxservlethttpHttpServletRequest))相同的结果}如果没有请
	 * 求可用,则提供此方法作为替代方法(例如内省,工具等) )。
	 * 
	 * 
	 * @param lookupPath the lookup path to match to existing patterns
	 * @return a collection of matching patterns sorted with the closest match at the top
	 */
	public List<String> getMatchingPatterns(String lookupPath) {
		List<String> matches = new ArrayList<String>();
		for (String pattern : this.patterns) {
			String match = getMatchingPattern(pattern, lookupPath);
			if (match != null) {
				matches.add(match);
			}
		}
		Collections.sort(matches, this.pathMatcher.getPatternComparator(lookupPath));
		return matches;
	}

	private String getMatchingPattern(String pattern, String lookupPath) {
		if (pattern.equals(lookupPath)) {
			return pattern;
		}
		if (this.useSuffixPatternMatch) {
			if (!this.fileExtensions.isEmpty() && lookupPath.indexOf('.') != -1) {
				for (String extension : this.fileExtensions) {
					if (this.pathMatcher.match(pattern + extension, lookupPath)) {
						return pattern + extension;
					}
				}
			}
			else {
				boolean hasSuffix = pattern.indexOf('.') != -1;
				if (!hasSuffix && this.pathMatcher.match(pattern + ".*", lookupPath)) {
					return pattern + ".*";
				}
			}
		}
		if (this.pathMatcher.match(pattern, lookupPath)) {
			return pattern;
		}
		if (this.useTrailingSlashMatch) {
			if (!pattern.endsWith("/") && this.pathMatcher.match(pattern + "/", lookupPath)) {
				return pattern +"/";
			}
		}
		return null;
	}

	/**
	 * Compare the two conditions based on the URL patterns they contain.
	 * Patterns are compared one at a time, from top to bottom via
	 * {@link PathMatcher#getPatternComparator(String)}. If all compared
	 * patterns match equally, but one instance has more patterns, it is
	 * considered a closer match.
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} to ensure they
	 * contain only patterns that match the request and are sorted with
	 * the best matches on top.
	 * <p>
	 * 比较两种条件基于它们包含的URL模式模式通过{@link PathMatcher#getPatternComparator(String)}从上到下逐一比较如果所有比较模式都匹配,但是一个实例有更多的模
	 * 式,它是考虑到更接近的匹配<p>假设这两个实例都是通过{@link #getMatchingCondition(HttpServletRequest)}获得的,以确保它们只包含与请求匹配的模式,并按顶部
	 */
	@Override
	public int compareTo(PatternsRequestCondition other, HttpServletRequest request) {
		String lookupPath = this.pathHelper.getLookupPathForRequest(request);
		Comparator<String> patternComparator = this.pathMatcher.getPatternComparator(lookupPath);
		Iterator<String> iterator = this.patterns.iterator();
		Iterator<String> iteratorOther = other.patterns.iterator();
		while (iterator.hasNext() && iteratorOther.hasNext()) {
			int result = patternComparator.compare(iterator.next(), iteratorOther.next());
			if (result != 0) {
				return result;
			}
		}
		if (iterator.hasNext()) {
			return -1;
		}
		else if (iteratorOther.hasNext()) {
			return 1;
		}
		else {
			return 0;
		}
	}

}
