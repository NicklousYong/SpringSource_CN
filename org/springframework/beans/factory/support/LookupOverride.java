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

package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.util.ObjectUtils;

/**
 * Represents an override of a method that looks up an object in the same IoC context.
 *
 * <p>Methods eligible for lookup override must not have arguments.
 *
 * <p>
 *  表示在同一IoC上下文中查找对象的方法的覆盖
 * 
 *  <p>符合查找覆盖率的方法不能有参数
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 */
public class LookupOverride extends MethodOverride {

	private final String beanName;

	private Method method;


	/**
	 * Construct a new LookupOverride.
	 * <p>
	 *  构造一个新的LookupOverride
	 * 
	 * 
	 * @param methodName the name of the method to override
	 * @param beanName the name of the bean in the current {@code BeanFactory}
	 * that the overridden method should return (may be {@code null})
	 */
	public LookupOverride(String methodName, String beanName) {
		super(methodName);
		this.beanName = beanName;
	}

	/**
	 * Construct a new LookupOverride.
	 * <p>
	 *  构造一个新的LookupOverride
	 * 
	 * 
	 * @param method the method to override
	 * @param beanName the name of the bean in the current {@code BeanFactory}
	 * that the overridden method should return (may be {@code null})
	 */
	public LookupOverride(Method method, String beanName) {
		super(method.getName());
		this.method = method;
		this.beanName = beanName;
	}


	/**
	 * Return the name of the bean that should be returned by this method.
	 * <p>
	 * 返回此方法应返回的bean的名称
	 * 
	 */
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Match the specified method by {@link Method} reference or method name.
	 * <p>For backwards compatibility reasons, in a scenario with overloaded
	 * non-abstract methods of the given name, only the no-arg variant of a
	 * method will be turned into a container-driven lookup method.
	 * <p>In case of a provided {@link Method}, only straight matches will
	 * be considered, usually demarcated by the {@code @Lookup} annotation.
	 * <p>
	 *  通过{@link方法}引用或方法名称匹配指定的方法<p>为了向后兼容性原因,在具有给定名称的重载非抽象方法的场景中,只有方法的无参数变体将变为容器驱动的查找方法<p>如果提供{@link方法},则只会
	 * 考虑直线匹配,通常由{@code @Lookup}注释划分。
	 */
	@Override
	public boolean matches(Method method) {
		if (this.method != null) {
			return method.equals(this.method);
		}
		else {
			return (method.getName().equals(getMethodName()) && (!isOverloaded() ||
					Modifier.isAbstract(method.getModifiers()) || method.getParameterTypes().length == 0));
		}
	}


	@Override
	public boolean equals(Object other) {
		if (!(other instanceof LookupOverride) || !super.equals(other)) {
			return false;
		}
		LookupOverride that = (LookupOverride) other;
		return (ObjectUtils.nullSafeEquals(this.method, that.method) &&
				ObjectUtils.nullSafeEquals(this.beanName, that.beanName));
	}

	@Override
	public int hashCode() {
		return (29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.beanName));
	}

	@Override
	public String toString() {
		return "LookupOverride for method '" + getMethodName() + "'";
	}

}
