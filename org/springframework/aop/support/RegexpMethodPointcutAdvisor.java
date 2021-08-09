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

package org.springframework.aop.support;

import java.io.Serializable;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Pointcut;
import org.springframework.util.ObjectUtils;

/**
 * Convenient class for regexp method pointcuts that hold an Advice,
 * making them an {@link org.springframework.aop.Advisor}.
 *
 * <p>Configure this class using the "pattern" and "patterns"
 * pass-through properties. These are analogous to the pattern
 * and patterns properties of {@link AbstractRegexpMethodPointcut}.
 *
 * <p>Can delegate to any {@link AbstractRegexpMethodPointcut} subclass.
 * By default, {@link JdkRegexpMethodPointcut} will be used. To choose
 * a specific one, override the {@link #createPointcut} method.
 *
 * <p>
 *  方便的类regexp方法切入点持有一个忠告,使他们成为{@link orgspringframeworkaopAdvisor}
 * 
 * <p>使用"模式"和"模式"传递属性配置此类别类似于{@link AbstractRegexpMethodPointcut}的模式和模式属性
 * 
 *  <p>可以委派给任何{@link AbstractRegexpMethodPointcut}子类默认情况下,将使用{@link JdkRegexpMethodPointcut}选择一个特定的{@link #createPointcut}
 * 方法。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setPattern
 * @see #setPatterns
 * @see JdkRegexpMethodPointcut
 */
@SuppressWarnings("serial")
public class RegexpMethodPointcutAdvisor extends AbstractGenericPointcutAdvisor {

	private String[] patterns;

	private AbstractRegexpMethodPointcut pointcut;

	private final Object pointcutMonitor = new SerializableMonitor();


	/**
	 * Create an empty RegexpMethodPointcutAdvisor.
	 * <p>
	 *  创建一个空的RegexpMethodPointcutAdvisor
	 * 
	 * 
	 * @see #setPattern
	 * @see #setPatterns
	 * @see #setAdvice
	 */
	public RegexpMethodPointcutAdvisor() {
	}

	/**
	 * Create a RegexpMethodPointcutAdvisor for the given advice.
	 * The pattern still needs to be specified afterwards.
	 * <p>
	 *  为给定的建议创建一个RegexpMethodPointcutAdvisor该模式仍然需要在之后指定
	 * 
	 * 
	 * @param advice the advice to use
	 * @see #setPattern
	 * @see #setPatterns
	 */
	public RegexpMethodPointcutAdvisor(Advice advice) {
		setAdvice(advice);
	}

	/**
	 * Create a RegexpMethodPointcutAdvisor for the given advice.
	 * <p>
	 *  为给定的建议创建一个RegexpMethodPointcutAdvisor
	 * 
	 * 
	 * @param pattern the pattern to use
	 * @param advice the advice to use
	 */
	public RegexpMethodPointcutAdvisor(String pattern, Advice advice) {
		setPattern(pattern);
		setAdvice(advice);
	}

	/**
	 * Create a RegexpMethodPointcutAdvisor for the given advice.
	 * <p>
	 *  为给定的建议创建一个RegexpMethodPointcutAdvisor
	 * 
	 * 
	 * @param patterns the patterns to use
	 * @param advice the advice to use
	 */
	public RegexpMethodPointcutAdvisor(String[] patterns, Advice advice) {
		setPatterns(patterns);
		setAdvice(advice);
	}


	/**
	 * Set the regular expression defining methods to match.
	 * <p>Use either this method or {@link #setPatterns}, not both.
	 * <p>
	 *  设置正则表达式定义方法以匹配<p>使用此方法或{@link #setPatterns},而不是两者
	 * 
	 * 
	 * @see #setPatterns
	 */
	public void setPattern(String pattern) {
		setPatterns(pattern);
	}

	/**
	 * Set the regular expressions defining methods to match.
	 * To be passed through to the pointcut implementation.
	 * <p>Matching will be the union of all these; if any of the
	 * patterns matches, the pointcut matches.
	 * <p>
	 * 设置正则表达式定义要匹配的方法要传递给切入点实现<p>匹配将是所有这些的联合;如果任何模式匹配,则切入点匹配
	 * 
	 * 
	 * @see AbstractRegexpMethodPointcut#setPatterns
	 */
	public void setPatterns(String... patterns) {
		this.patterns = patterns;
	}


	/**
	 * Initialize the singleton Pointcut held within this Advisor.
	 * <p>
	 *  初始化该顾问中持有的单身人士Pointcut
	 * 
	 */
	@Override
	public Pointcut getPointcut() {
		synchronized (this.pointcutMonitor) {
			if (this.pointcut == null) {
				this.pointcut = createPointcut();
				this.pointcut.setPatterns(this.patterns);
			}
			return pointcut;
		}
	}

	/**
	 * Create the actual pointcut: By default, a {@link JdkRegexpMethodPointcut}
	 * will be used.
	 * <p>
	 *  创建实际切入点：默认情况下,将使用{@link JdkRegexpMethodPointcut}
	 * 
	 * 
	 * @return the Pointcut instance (never {@code null})
	 */
	protected AbstractRegexpMethodPointcut createPointcut() {
		return new JdkRegexpMethodPointcut();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": advice [" + getAdvice() +
				"], pointcut patterns " + ObjectUtils.nullSafeToString(this.patterns);
	}


	/**
	 * Empty class used for a serializable monitor object.
	 * <p>
	 *  用于可序列化的监视器对象的空类
	 */
	private static class SerializableMonitor implements Serializable {
	}

}
