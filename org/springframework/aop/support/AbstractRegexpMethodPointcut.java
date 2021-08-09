/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Abstract base regular expression pointcut bean. JavaBean properties are:
 * <ul>
 * <li>pattern: regular expression for the fully-qualified method names to match.
 * The exact regexp syntax will depend on the subclass (e.g. Perl5 regular expressions)
 * <li>patterns: alternative property taking a String array of patterns.
 * The result will be the union of these patterns.
 * </ul>
 *
 * <p>Note: the regular expressions must be a match. For example,
 * {@code .*get.*} will match com.mycom.Foo.getBar().
 * {@code get.*} will not.
 *
 * <p>This base class is serializable. Subclasses should declare all fields transient;
 * the {@link #initPatternRepresentation} method will be invoked again on deserialization.
 *
 * <p>
 *  抽象基础正则表达式切菜豆JavaBean属性有：
 * <ul>
 * <li> pattern：完全限定方法名称匹配的正则表达式准确的正则表达式语法将取决于子类(例如Perl5正则表达式)<li>模式：替换属性采用String数组的模式结果将是union的这些模式
 * </ul>
 * 
 *  <p>注意：正则表达式必须是一个匹配例如,{@code * get *}将匹配commycomFoogetBar(){@code get *}不会
 * 
 *  <p>这个基类是可序列化的子类应该声明所有的字段transient;将在反序列化时再次调用{@link #initPatternRepresentation}方法
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 1.1
 * @see JdkRegexpMethodPointcut
 */
@SuppressWarnings("serial")
public abstract class AbstractRegexpMethodPointcut extends StaticMethodMatcherPointcut
		implements Serializable {

	/**
	 * Regular expressions to match.
	 * <p>
	 *  正则表达式匹配
	 * 
	 */
	private String[] patterns = new String[0];

	/**
	 * Regular expressions <strong>not</strong> to match.
	 * <p>
	 *  正则表达式<strong>不</strong>匹配
	 * 
	 */
	private String[] excludedPatterns = new String[0];


	/**
	 * Convenience method when we have only a single pattern.
	 * Use either this method or {@link #setPatterns}, not both.
	 * <p>
	 *  只有单个模式的便利方法使用此方法或{@link #setPatterns},而不是两者
	 * 
	 * 
	 * @see #setPatterns
	 */
	public void setPattern(String pattern) {
		setPatterns(pattern);
	}

	/**
	 * Set the regular expressions defining methods to match.
	 * Matching will be the union of all these; if any match, the pointcut matches.
	 * <p>
	 * 设置正则表达式定义方法匹配匹配将是所有这些的联合;如果有任何匹配,则切入点匹配
	 * 
	 * 
	 * @see #setPattern
	 */
	public void setPatterns(String... patterns) {
		Assert.notEmpty(patterns, "'patterns' must not be empty");
		this.patterns = new String[patterns.length];
		for (int i = 0; i < patterns.length; i++) {
			this.patterns[i] = StringUtils.trimWhitespace(patterns[i]);
		}
		initPatternRepresentation(this.patterns);
	}

	/**
	 * Return the regular expressions for method matching.
	 * <p>
	 *  返回方法匹配的正则表达式
	 * 
	 */
	public String[] getPatterns() {
		return this.patterns;
	}

	/**
	 * Convenience method when we have only a single exclusion pattern.
	 * Use either this method or {@link #setExcludedPatterns}, not both.
	 * <p>
	 *  只有单个排除模式时的便利方法使用此方法或{@link #setExcludedPatterns},而不是两者
	 * 
	 * 
	 * @see #setExcludedPatterns
	 */
	public void setExcludedPattern(String excludedPattern) {
		setExcludedPatterns(excludedPattern);
	}

	/**
	 * Set the regular expressions defining methods to match for exclusion.
	 * Matching will be the union of all these; if any match, the pointcut matches.
	 * <p>
	 *  设置定义匹配排除方法的正则表达式匹配将是所有这些的联合;如果有任何匹配,则切入点匹配
	 * 
	 * 
	 * @see #setExcludedPattern
	 */
	public void setExcludedPatterns(String... excludedPatterns) {
		Assert.notEmpty(excludedPatterns, "'excludedPatterns' must not be empty");
		this.excludedPatterns = new String[excludedPatterns.length];
		for (int i = 0; i < excludedPatterns.length; i++) {
			this.excludedPatterns[i] = StringUtils.trimWhitespace(excludedPatterns[i]);
		}
		initExcludedPatternRepresentation(this.excludedPatterns);
	}

	/**
	 * Returns the regular expressions for exclusion matching.
	 * <p>
	 *  返回排除匹配的正则表达式
	 * 
	 */
	public String[] getExcludedPatterns() {
		return this.excludedPatterns;
	}


	/**
	 * Try to match the regular expression against the fully qualified name
	 * of the target class as well as against the method's declaring class,
	 * plus the name of the method.
	 * <p>
	 *  尝试将正则表达式与目标类的完全限定名称以及方法的声明类加上方法的名称相匹配
	 * 
	 */
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return ((targetClass != null && matchesPattern(targetClass.getName() + "." + method.getName())) ||
				matchesPattern(method.getDeclaringClass().getName() + "." + method.getName()));
	}

	/**
	 * Match the specified candidate against the configured patterns.
	 * <p>
	 *  根据配置的模式匹配指定的候选
	 * 
	 * 
	 * @param signatureString "java.lang.Object.hashCode" style signature
	 * @return whether the candidate matches at least one of the specified patterns
	 */
	protected boolean matchesPattern(String signatureString) {
		for (int i = 0; i < this.patterns.length; i++) {
			boolean matched = matches(signatureString, i);
			if (matched) {
				for (int j = 0; j < this.excludedPatterns.length; j++) {
					boolean excluded = matchesExclusion(signatureString, j);
					if (excluded) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}


	/**
	 * Subclasses must implement this to initialize regexp pointcuts.
	 * Can be invoked multiple times.
	 * <p>This method will be invoked from the {@link #setPatterns} method,
	 * and also on deserialization.
	 * <p>
	 * 子类必须实现这一点来初始化正则表达式切入点可以多次调用<p>该方法将从{@link #setPatterns}方法调用,并且还将反序列化
	 * 
	 * 
	 * @param patterns the patterns to initialize
	 * @throws IllegalArgumentException in case of an invalid pattern
	 */
	protected abstract void initPatternRepresentation(String[] patterns) throws IllegalArgumentException;

	/**
	 * Subclasses must implement this to initialize regexp pointcuts.
	 * Can be invoked multiple times.
	 * <p>This method will be invoked from the {@link #setExcludedPatterns} method,
	 * and also on deserialization.
	 * <p>
	 *  子类必须实现这一点来初始化正则表达式切入点可以多次调用<p>该方法将从{@link #setExcludedPatterns}方法调用,并且还将反序列化
	 * 
	 * 
	 * @param patterns the patterns to initialize
	 * @throws IllegalArgumentException in case of an invalid pattern
	 */
	protected abstract void initExcludedPatternRepresentation(String[] patterns) throws IllegalArgumentException;

	/**
	 * Does the pattern at the given index match the given String?
	 * <p>
	 *  给定索引中的模式是否与给定的String匹配?
	 * 
	 * 
	 * @param pattern the {@code String} pattern to match
	 * @param patternIndex index of pattern (starting from 0)
	 * @return {@code true} if there is a match, {@code false} otherwise
	 */
	protected abstract boolean matches(String pattern, int patternIndex);

	/**
	 * Does the exclusion pattern at the given index match the given String?
	 * <p>
	 *  给定索引的排除模式是否与给定的String匹配?
	 * 
	 * @param pattern the {@code String} pattern to match
	 * @param patternIndex index of pattern (starting from 0)
	 * @return {@code true} if there is a match, {@code false} otherwise
	 */
	protected abstract boolean matchesExclusion(String pattern, int patternIndex);


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractRegexpMethodPointcut)) {
			return false;
		}
		AbstractRegexpMethodPointcut otherPointcut = (AbstractRegexpMethodPointcut) other;
		return (Arrays.equals(this.patterns, otherPointcut.patterns) &&
				Arrays.equals(this.excludedPatterns, otherPointcut.excludedPatterns));
	}

	@Override
	public int hashCode() {
		int result = 27;
		for (String pattern : this.patterns) {
			result = 13 * result + pattern.hashCode();
		}
		for (String excludedPattern : this.excludedPatterns) {
			result = 13 * result + excludedPattern.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		return getClass().getName() + ": patterns " + ObjectUtils.nullSafeToString(this.patterns) +
				", excluded patterns " + ObjectUtils.nullSafeToString(this.excludedPatterns);
	}

}
