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

package org.springframework.aop.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Regular expression pointcut based on the {@code java.util.regex} package.
 * Supports the following JavaBean properties:
 * <ul>
 * <li>pattern: regular expression for the fully-qualified method names to match
 * <li>patterns: alternative property taking a String array of patterns. The result will
 * be the union of these patterns.
 * </ul>
 *
 * <p>Note: the regular expressions must be a match. For example,
 * {@code .*get.*} will match com.mycom.Foo.getBar().
 * {@code get.*} will not.
 *
 * <p>
 *  基于{@code javautilregex}包的正则表达式切入点支持以下JavaBean属性：
 * <ul>
 * <li> pattern：用于匹配<li>模式的完全限定方法名称的正则表达式：使用String数组模式的alternative属性结果将是这些模式的并集
 * </ul>
 * 
 *  <p>注意：正则表达式必须是一个匹配例如,{@code * get *}将匹配commycomFoogetBar(){@code get *}不会
 * 
 * 
 * @author Dmitriy Kopylenko
 * @author Rob Harrop
 * @since 1.1
 */
@SuppressWarnings("serial")
public class JdkRegexpMethodPointcut extends AbstractRegexpMethodPointcut {

	/**
	 * Compiled form of the patterns.
	 * <p>
	 *  汇编形式的图案
	 * 
	 */
	private Pattern[] compiledPatterns = new Pattern[0];

	/**
	 * Compiled form of the exclusion patterns.
	 * <p>
	 *  编排形式的排除模式
	 * 
	 */
	private Pattern[] compiledExclusionPatterns = new Pattern[0];


	/**
	 * Initialize {@link Pattern Patterns} from the supplied {@code String[]}.
	 * <p>
	 *  从提供的{@code String []}初始化{@link Pattern Patterns}
	 * 
	 */
	@Override
	protected void initPatternRepresentation(String[] patterns) throws PatternSyntaxException {
		this.compiledPatterns = compilePatterns(patterns);
	}

	/**
	 * Initialize exclusion {@link Pattern Patterns} from the supplied {@code String[]}.
	 * <p>
	 *  从提供的{@code String []}初始化排除{@link Pattern Patterns}
	 * 
	 */
	@Override
	protected void initExcludedPatternRepresentation(String[] excludedPatterns) throws PatternSyntaxException {
		this.compiledExclusionPatterns = compilePatterns(excludedPatterns);
	}

	/**
	 * Returns {@code true} if the {@link Pattern} at index {@code patternIndex}
	 * matches the supplied candidate {@code String}.
	 * <p>
	 *  如果索引{@code patternIndex}上的{@link Pattern}与提供的候选项{@code String}匹配,则返回{@code true}
	 * 
	 */
	@Override
	protected boolean matches(String pattern, int patternIndex) {
		Matcher matcher = this.compiledPatterns[patternIndex].matcher(pattern);
		return matcher.matches();
	}

	/**
	 * Returns {@code true} if the exclusion {@link Pattern} at index {@code patternIndex}
	 * matches the supplied candidate {@code String}.
	 * <p>
	 * 如果索引{@code patternIndex}排除{@link Pattern}与提供的候选项{@code String}匹配,返回{@code true}
	 * 
	 */
	@Override
	protected boolean matchesExclusion(String candidate, int patternIndex) {
		Matcher matcher = this.compiledExclusionPatterns[patternIndex].matcher(candidate);
		return matcher.matches();
	}


	/**
	 * Compiles the supplied {@code String[]} into an array of
	 * {@link Pattern} objects and returns that array.
	 * <p>
	 *  将提供的{@code String []}编译成{@link Pattern}对象的数组,并返回该数组
	 */
	private Pattern[] compilePatterns(String[] source) throws PatternSyntaxException {
		Pattern[] destination = new Pattern[source.length];
		for (int i = 0; i < source.length; i++) {
			destination[i] = Pattern.compile(source[i]);
		}
		return destination;
	}

}
