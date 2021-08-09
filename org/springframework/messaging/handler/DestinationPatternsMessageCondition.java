/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.messaging.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.messaging.Message;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

/**
 * A {@link MessageCondition} for matching the destination of a Message against one or
 * more destination patterns using a {@link PathMatcher}.
 *
 * <p>
 *  使用{@link PathMatcher}将邮件的目的地与一个或多个目标模式进行匹配的{@link MessageCondition}
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class DestinationPatternsMessageCondition extends AbstractMessageCondition<DestinationPatternsMessageCondition> {

	public static final String LOOKUP_DESTINATION_HEADER = "lookupDestination";


	private final Set<String> patterns;

	private final PathMatcher pathMatcher;


	/**
	 * Creates a new instance with the given destination patterns.
	 * Each pattern that is not empty and does not start with "/" is prepended with "/".
	 * <p>
	 * 使用给定的目标模式创建新实例每个不为空且不以"/"开头的模式都以"/"开头
	 * 
	 * 
	 * @param patterns 0 or more URL patterns; if 0 the condition will match to every request.
	 */
	public DestinationPatternsMessageCondition(String... patterns) {
		this(patterns, null);
	}

	/**
	 * Alternative constructor accepting a custom PathMatcher.
	 * <p>
	 *  接受自定义PathMatcher的替代构造函数
	 * 
	 * 
	 * @param patterns the URL patterns to use; if 0, the condition will match to every request.
	 * @param pathMatcher the PathMatcher to use
	 */
	public DestinationPatternsMessageCondition(String[] patterns, PathMatcher pathMatcher) {
		this(asList(patterns), pathMatcher);
	}

	private DestinationPatternsMessageCondition(Collection<String> patterns, PathMatcher pathMatcher) {
		this.pathMatcher = (pathMatcher != null ? pathMatcher : new AntPathMatcher());
		this.patterns = Collections.unmodifiableSet(prependLeadingSlash(patterns, this.pathMatcher));
	}


	private static List<String> asList(String... patterns) {
		return (patterns != null ? Arrays.asList(patterns) : Collections.<String>emptyList());
	}

	private static Set<String> prependLeadingSlash(Collection<String> patterns, PathMatcher pathMatcher) {
		if (patterns == null) {
			return Collections.emptySet();
		}
		boolean slashSeparator = pathMatcher.combine("a", "a").equals("a/a");
		Set<String> result = new LinkedHashSet<String>(patterns.size());
		for (String pattern : patterns) {
			if (slashSeparator) {
				if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
					pattern = "/" + pattern;
				}
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
	 * the patterns in "other" using {@link org.springframework.util.PathMatcher#combine(String, String)}.
	 * <li>If only one instance has patterns, use them.
	 * <li>If neither instance has patterns, use an empty String (i.e. "").
	 * </ul>
	 * <p>
	 *  从当前实例("this")和"other"实例返回一个具有URL模式的新实例,如下所示：
	 * <ul>
	 *  如果两个实例中都有模式,则使用{@link orgspringframeworkutilPathMatcher#combine(String,String)} <li>将"this"中的模式与"oth
	 * er"中的模式相结合如果只有一个实例具有模式,请使用它们<li>如果两个实例都不具有模式,请使用空字符串(即"")。
	 * </ul>
	 */
	@Override
	public DestinationPatternsMessageCondition combine(DestinationPatternsMessageCondition other) {
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
		return new DestinationPatternsMessageCondition(result, this.pathMatcher);
	}

	/**
	 * Check if any of the patterns match the given Message destination and return an instance
	 * that is guaranteed to contain matching patterns, sorted via
	 * {@link org.springframework.util.PathMatcher#getPatternComparator(String)}.
	 * <p>
	 * 检查任何模式是否匹配给定的消息目标,并返回一个保证包含匹配模式的实例,通过{@link orgspringframeworkutilPathMatcher#getPatternComparator(String)}
	 * 排序。
	 * 
	 * 
	 * @param message the message to match to
	 * @return the same instance if the condition contains no patterns;
	 * or a new condition with sorted matching patterns;
	 * or {@code null} either if a destination can not be extracted or there is no match
	 */
	@Override
	public DestinationPatternsMessageCondition getMatchingCondition(Message<?> message) {
		String destination = (String) message.getHeaders().get(LOOKUP_DESTINATION_HEADER);
		if (destination == null) {
			return null;
		}

		if (this.patterns.isEmpty()) {
			return this;
		}

		List<String> matches = new ArrayList<String>();
		for (String pattern : patterns) {
			if (pattern.equals(destination) || this.pathMatcher.match(pattern, destination)) {
				matches.add(pattern);
			}
		}

		if (matches.isEmpty()) {
			return null;
		}

		Collections.sort(matches, this.pathMatcher.getPatternComparator(destination));
		return new DestinationPatternsMessageCondition(matches, this.pathMatcher);
	}

	/**
	 * Compare the two conditions based on the destination patterns they contain.
	 * Patterns are compared one at a time, from top to bottom via
	 * {@link org.springframework.util.PathMatcher#getPatternComparator(String)}.
	 * If all compared patterns match equally, but one instance has more patterns,
	 * it is considered a closer match.
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(Message)} to ensure they
	 * contain only patterns that match the request and are sorted with
	 * the best matches on top.
	 * <p>
	 *  根据它们包含的目标模式比较两个条件模式通过{@link orgspringframeworkutilPathMatcher#getPatternComparator(String)}从上到下逐个比较如
	 * 果所有比较模式都匹配,但一个实例有更多的模式,它是考虑了一个更接近的匹配<p>假设这两个实例都是通过{@link #getMatchingCondition(Message)}获得的,以确保它们只包含与
	 */
	@Override
	public int compareTo(DestinationPatternsMessageCondition other, Message<?> message) {
		String destination = (String) message.getHeaders().get(LOOKUP_DESTINATION_HEADER);
		Comparator<String> patternComparator = this.pathMatcher.getPatternComparator(destination);

		Iterator<String> iterator = patterns.iterator();
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
