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

package org.springframework.aop.aspectj;

import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.TypePatternMatcher;

import org.springframework.aop.ClassFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Spring AOP {@link ClassFilter} implementation using AspectJ type matching.
 *
 * <p>
 *  Spring AOP {@link ClassFilter}实现使用AspectJ类型匹配
 * 
 * 
 * @author Rod Johnson
 * @since 2.0
 */
public class TypePatternClassFilter implements ClassFilter {

	private String typePattern;

	private TypePatternMatcher aspectJTypePatternMatcher;


	/**
	 * Creates a new instance of the {@link TypePatternClassFilter} class.
	 * <p>This is the JavaBean constructor; be sure to set the
	 * {@link #setTypePattern(String) typePattern} property, else a
	 * no doubt fatal {@link IllegalStateException} will be thrown
	 * when the {@link #matches(Class)} method is first invoked.
	 * <p>
	 * 创建一个{@link TypePatternClassFilter}类的新实例<p>这是JavaBean构造函数;请确保设置{@link #setTypePattern(String)typePattern}
	 * 属性,否则无疑将会首先调用{@link #matches(Class))方法时会引发{@link IllegalStateException}。
	 * 
	 */
	public TypePatternClassFilter() {
	}

	/**
	 * Create a fully configured {@link TypePatternClassFilter} using the
	 * given type pattern.
	 * <p>
	 *  使用给定的类型模式创建一个完全配置的{@link TypePatternClassFilter}
	 * 
	 * 
	 * @param typePattern the type pattern that AspectJ weaver should parse
	 * @throws IllegalArgumentException if the supplied {@code typePattern} is {@code null}
	 * or is recognized as invalid
	 */
	public TypePatternClassFilter(String typePattern) {
		setTypePattern(typePattern);
	}


	/**
	 * Set the AspectJ type pattern to match.
	 * <p>Examples include:
	 * <code class="code">
	 * org.springframework.beans.*
	 * </code>
	 * This will match any class or interface in the given package.
	 * <code class="code">
	 * org.springframework.beans.ITestBean+
	 * </code>
	 * This will match the {@code ITestBean} interface and any class
	 * that implements it.
	 * <p>These conventions are established by AspectJ, not Spring AOP.
	 * <p>
	 *  设置AspectJ类型模式以匹配<p>示例包括：
	 * <code class="code">
	 *  orgspringframeworkbeans *
	 * </code>
	 *  这将匹配给定包中的任何类或接口
	 * <code class="code">
	 *  orgspringframeworkbeansITestBean +
	 * </code>
	 *  这将匹配{@code ITestBean}接口和实现它的任何类<p>这些约定是由AspectJ建立的,而不是Spring AOP
	 * 
	 * @param typePattern the type pattern that AspectJ weaver should parse
	 * @throws IllegalArgumentException if the supplied {@code typePattern} is {@code null}
	 * or is recognized as invalid
	 */
	public void setTypePattern(String typePattern) {
		Assert.notNull(typePattern);
		this.typePattern = typePattern;
		this.aspectJTypePatternMatcher =
				PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution().
				parseTypePattern(replaceBooleanOperators(typePattern));
	}

	public String getTypePattern() {
		return typePattern;
	}

	/**
	 * Should the pointcut apply to the given interface or target class?
	 * <p>
	 * 
	 * 
	 * @param clazz candidate target class
	 * @return whether the advice should apply to this candidate target class
	 * @throws IllegalStateException if no {@link #setTypePattern(String)} has been set
	 */
	@Override
	public boolean matches(Class<?> clazz) {
		if (this.aspectJTypePatternMatcher == null) {
			throw new IllegalStateException("No 'typePattern' has been set via ctor/setter.");
		}
		return this.aspectJTypePatternMatcher.matches(clazz);
	}

	/**
	 * If a type pattern has been specified in XML, the user cannot
	 * write {@code and} as "&&" (though &amp;&amp; will work).
	 * We also allow {@code and} between two sub-expressions.
	 * <p>This method converts back to {@code &&} for the AspectJ pointcut parser.
	 * <p>
	 *  该切入点是否适用于给定的接口或目标类?
	 * 
	 */
	private String replaceBooleanOperators(String pcExpr) {
		pcExpr = StringUtils.replace(pcExpr," and "," && ");
		pcExpr = StringUtils.replace(pcExpr, " or ", " || ");
		pcExpr = StringUtils.replace(pcExpr, " not ", " ! ");
		return pcExpr;
	}
}
