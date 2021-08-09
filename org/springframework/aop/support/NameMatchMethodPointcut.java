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
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;

/**
 * Pointcut bean for simple method name matches, as alternative to regexp patterns.
 * Does not handle overloaded methods: all methods *with a given name will be eligible.
 *
 * <p>
 *  Pointcut bean用于简单的方法名称匹配,作为regexp模式的替代方法不处理重载方法：具有给定名称的所有方法*将符合资格
 * 
 * 
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Rob Harrop
 * @since 11.02.2004
 * @see #isMatch
 */
@SuppressWarnings("serial")
public class NameMatchMethodPointcut extends StaticMethodMatcherPointcut implements Serializable {

	private List<String> mappedNames = new LinkedList<String>();


	/**
	 * Convenience method when we have only a single method name to match.
	 * Use either this method or {@code setMappedNames}, not both.
	 * <p>
	 * 当我们只有一个方法名称匹配时,方便方法使用这种方法或{@code setMappedNames},而不是两者
	 * 
	 * 
	 * @see #setMappedNames
	 */
	public void setMappedName(String mappedName) {
		setMappedNames(mappedName);
	}

	/**
	 * Set the method names defining methods to match.
	 * Matching will be the union of all these; if any match,
	 * the pointcut matches.
	 * <p>
	 *  设置方法名称定义方法匹配匹配将是所有这些的联合;如果有任何匹配,则切入点匹配
	 * 
	 */
	public void setMappedNames(String... mappedNames) {
		this.mappedNames = new LinkedList<String>();
		if (mappedNames != null) {
			this.mappedNames.addAll(Arrays.asList(mappedNames));
		}
	}

	/**
	 * Add another eligible method name, in addition to those already named.
	 * Like the set methods, this method is for use when configuring proxies,
	 * before a proxy is used.
	 * <p><b>NB:</b> This method does not work after the proxy is in
	 * use, as advice chains will be cached.
	 * <p>
	 *  添加另一个符合条件的方法名称,除了已经命名的类似于set方法之外,在使用代理之前,此方法用于配置代理<p> <b>注意：</b>此方法在代理正在使用中,因为建议链将被缓存
	 * 
	 * 
	 * @param name name of the additional method that will match
	 * @return this pointcut to allow for multiple additions in one line
	 */
	public NameMatchMethodPointcut addMethodName(String name) {
		this.mappedNames.add(name);
		return this;
	}


	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		for (String mappedName : this.mappedNames) {
			if (mappedName.equals(method.getName()) || isMatch(method.getName(), mappedName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return if the given method name matches the mapped name.
	 * <p>The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches,
	 * as well as direct equality. Can be overridden in subclasses.
	 * <p>
	 *  如果给定的方法名称与映射名匹配,则返回<p>默认实现检查"xxx *","* xxx"和"* xxx *"匹配以及直接相等可以在子类中覆盖
	 * 
	 * @param methodName the method name of the class
	 * @param mappedName the name in the descriptor
	 * @return if the names match
	 * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
	 */
	protected boolean isMatch(String methodName, String mappedName) {
		return PatternMatchUtils.simpleMatch(mappedName, methodName);
	}


	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof NameMatchMethodPointcut &&
				ObjectUtils.nullSafeEquals(this.mappedNames, ((NameMatchMethodPointcut) other).mappedNames)));
	}

	@Override
	public int hashCode() {
		return (this.mappedNames != null ? this.mappedNames.hashCode() : 0);
	}

}
