/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Strategy interface for {@code String}-based path matching.
 *
 * <p>Used by {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver},
 * {@link org.springframework.web.servlet.handler.AbstractUrlHandlerMapping},
 * {@link org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver},
 * and {@link org.springframework.web.servlet.mvc.WebContentInterceptor}.
 *
 * <p>The default implementation is {@link AntPathMatcher}, supporting the
 * Ant-style pattern syntax.
 *
 * <p>
 *  基于{@code String}的路径匹配的策略界面
 * 
 * <p>由{@link orgspringframeworkcoreiosupportPathMatchingResourcePatternResolver}使用,{@link orgspringframeworkwebservlethandlerAbstractUrlHandlerMapping}
 * ,{@link orgspringframeworkwebservletmvcmultiactionPropertiesMethodNameResolver}和{@link orgspringframeworkwebservletmvcWebContentInterceptor}
 * 。
 * 
 *  <p>默认实现是{@link AntPathMatcher},支持Ant样式模式语法
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2
 * @see AntPathMatcher
 */
public interface PathMatcher {

	/**
	 * Does the given {@code path} represent a pattern that can be matched
	 * by an implementation of this interface?
	 * <p>If the return value is {@code false}, then the {@link #match}
	 * method does not have to be used because direct equality comparisons
	 * on the static path Strings will lead to the same result.
	 * <p>
	 *  给定的{@code路径}是否表示可以通过此接口的实现来匹配的模式? <p>如果返回值为{@code false},则不必使用{@link #match}方法,因为静态路径上的直接等式比较将导致相同的结
	 * 果。
	 * 
	 * 
	 * @param path the path String to check
	 * @return {@code true} if the given {@code path} represents a pattern
	 */
	boolean isPattern(String path);

	/**
	 * Match the given {@code path} against the given {@code pattern},
	 * according to this PathMatcher's matching strategy.
	 * <p>
	 * 根据该PathMatcher的匹配策略,将给定的{@code路径}与给定的{@code模式}匹配
	 * 
	 * 
	 * @param pattern the pattern to match against
	 * @param path the path String to test
	 * @return {@code true} if the supplied {@code path} matched,
	 * {@code false} if it didn't
	 */
	boolean match(String pattern, String path);

	/**
	 * Match the given {@code path} against the corresponding part of the given
	 * {@code pattern}, according to this PathMatcher's matching strategy.
	 * <p>Determines whether the pattern at least matches as far as the given base
	 * path goes, assuming that a full path may then match as well.
	 * <p>
	 *  根据该PathMatcher的匹配策略,将给定的{@code路径}与给定{@code模式}的相应部分进行匹配<p>确定模式是否至少匹配到指定基本路径的距离,假设一个完整的路径也可以匹配
	 * 
	 * 
	 * @param pattern the pattern to match against
	 * @param path the path String to test
	 * @return {@code true} if the supplied {@code path} matched,
	 * {@code false} if it didn't
	 */
	boolean matchStart(String pattern, String path);

	/**
	 * Given a pattern and a full path, determine the pattern-mapped part.
	 * <p>This method is supposed to find out which part of the path is matched
	 * dynamically through an actual pattern, that is, it strips off a statically
	 * defined leading path from the given full path, returning only the actually
	 * pattern-matched part of the path.
	 * <p>For example: For "myroot/*.html" as pattern and "myroot/myfile.html"
	 * as full path, this method should return "myfile.html". The detailed
	 * determination rules are specified to this PathMatcher's matching strategy.
	 * <p>A simple implementation may return the given full path as-is in case
	 * of an actual pattern, and the empty String in case of the pattern not
	 * containing any dynamic parts (i.e. the {@code pattern} parameter being
	 * a static path that wouldn't qualify as an actual {@link #isPattern pattern}).
	 * A sophisticated implementation will differentiate between the static parts
	 * and the dynamic parts of the given path pattern.
	 * <p>
	 * 给定模式和完整路径,确定模式映射部分<p>该方法应该通过实际模式动态地找出路径的哪个部分,也就是剥离静态定义的引导路径给出完整路径,只返回路径<p>的实际模式匹配部分例如：对于"myroot / * h
	 * tml"作为模式和"myroot / myfilehtml"作为完整路径,此方法应返回"myfilehtml"详细确定规则被指定给这个PathMatcher的匹配策略<p>一个简单的实现可以在实际模式的
	 * 情况下按原样返回给定的完整路径,并且在该模式不包含任何动态部分的情况下为空的String(即{@code pattern}参数是一个不符合实际{@link #isPattern模式}的静态路径)。
	 * 复杂的实现将区分给定路径模式的静态部分和动态部分。
	 * 
	 * 
	 * @param pattern the path pattern
	 * @param path the full path to introspect
	 * @return the pattern-mapped part of the given {@code path}
	 * (never {@code null})
	 */
	String extractPathWithinPattern(String pattern, String path);

	/**
	 * Given a pattern and a full path, extract the URI template variables. URI template
	 * variables are expressed through curly brackets ('{' and '}').
	 * <p>For example: For pattern "/hotels/{hotel}" and path "/hotels/1", this method will
	 * return a map containing "hotel"->"1".
	 * <p>
	 * 给定一个模式和一个完整路径,提取URI模板变量URI模板变量通过大括号('{'和'}'表示)<p>例如：对于模式"/ hotels / {hotel}"和路径"/酒店/ 1",此方法将返回包含"hote
	 * l" - >"1"的地图。
	 * 
	 * 
	 * @param pattern the path pattern, possibly containing URI templates
	 * @param path the full path to extract template variables from
	 * @return a map, containing variable names as keys; variables values as values
	 */
	Map<String, String> extractUriTemplateVariables(String pattern, String path);

	/**
	 * Given a full path, returns a {@link Comparator} suitable for sorting patterns
	 * in order of explicitness for that path.
	 * <p>The full algorithm used depends on the underlying implementation, but generally,
	 * the returned {@code Comparator} will
	 * {@linkplain java.util.Collections#sort(java.util.List, java.util.Comparator) sort}
	 * a list so that more specific patterns come before generic patterns.
	 * <p>
	 *  给定一个完整的路径,返回一个适用于排序模式的{@link比较器},以按照该路径的显性顺序排列。
	 * 完整的算法依赖于底层实现,但通常返回的{@code比较器}将{@linkplain javautilCollections#sort(javautilList,javautilComparator)sort}
	 * 一个列表,以便在通用模式之前有更多的具体模式。
	 *  给定一个完整的路径,返回一个适用于排序模式的{@link比较器},以按照该路径的显性顺序排列。
	 * 
	 * @param path the full path to use for comparison
	 * @return a comparator capable of sorting patterns in order of explicitness
	 */
	Comparator<String> getPatternComparator(String path);

	/**
	 * Combines two patterns into a new pattern that is returned.
	 * <p>The full algorithm used for combining the two pattern depends on the underlying implementation.
	 * <p>
	 * 
	 * 
	 * @param pattern1 the first pattern
	 * @param pattern2 the second pattern
	 * @return the combination of the two patterns
	 * @throws IllegalArgumentException when the two patterns cannot be combined
	 */
	String combine(String pattern1, String pattern2);

}
