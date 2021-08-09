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

import org.aopalliance.aop.Advice;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;

/**
 * Convenient class for name-match method pointcuts that hold an Advice,
 * making them an Advisor.
 *
 * <p>
 *  名称匹配方法切入点的方便类,持有建议,成为顾问
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see NameMatchMethodPointcut
 */
@SuppressWarnings("serial")
public class NameMatchMethodPointcutAdvisor extends AbstractGenericPointcutAdvisor {

	private final NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();


	public NameMatchMethodPointcutAdvisor() {
	}

	public NameMatchMethodPointcutAdvisor(Advice advice) {
		setAdvice(advice);
	}


	/**
	 * Set the {@link ClassFilter} to use for this pointcut.
	 * Default is {@link ClassFilter#TRUE}.
	 * <p>
	 *  设置{@link ClassFilter}用于此切入点默认为{@link ClassFilter#TRUE}
	 * 
	 * 
	 * @see NameMatchMethodPointcut#setClassFilter
	 */
	public void setClassFilter(ClassFilter classFilter) {
		this.pointcut.setClassFilter(classFilter);
	}

	/**
	 * Convenience method when we have only a single method name to match.
	 * Use either this method or {@code setMappedNames}, not both.
	 * <p>
	 * 当我们只有一个方法名称匹配时,方便方法使用这种方法或{@code setMappedNames},而不是两者
	 * 
	 * 
	 * @see #setMappedNames
	 * @see NameMatchMethodPointcut#setMappedName
	 */
	public void setMappedName(String mappedName) {
		this.pointcut.setMappedName(mappedName);
	}

	/**
	 * Set the method names defining methods to match.
	 * Matching will be the union of all these; if any match,
	 * the pointcut matches.
	 * <p>
	 *  设置方法名称定义方法匹配匹配将是所有这些的联合;如果有任何匹配,则切入点匹配
	 * 
	 * 
	 * @see NameMatchMethodPointcut#setMappedNames
	 */
	public void setMappedNames(String... mappedNames) {
		this.pointcut.setMappedNames(mappedNames);
	}

	/**
	 * Add another eligible method name, in addition to those already named.
	 * Like the set methods, this method is for use when configuring proxies,
	 * before a proxy is used.
	 * <p>
	 *  添加另一个符合条件的方法名称,除了已经命名的类似于set方法之外,在使用代理之前,此方法用于配置代理时
	 * 
	 * @param name name of the additional method that will match
	 * @return this pointcut to allow for multiple additions in one line
	 * @see NameMatchMethodPointcut#addMethodName
	 */
	public NameMatchMethodPointcut addMethodName(String name) {
		return this.pointcut.addMethodName(name);
	}


	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

}
