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

package org.springframework.aop;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Extension of the AOP Alliance {@link org.aopalliance.intercept.MethodInvocation}
 * interface, allowing access to the proxy that the method invocation was made through.
 *
 * <p>Useful to be able to substitute return values with the proxy,
 * if necessary, for example if the invocation target returned itself.
 *
 * <p>
 *  扩展AOP联盟{@link orgaopallianceinterceptMethodInvocation}接口,允许访问方法调用的代理,通过
 * 
 * <p>如果需要,可以使用代理替换返回值,例如,如果调用目标返回自身
 * 
 * 
 * @author Juergen Hoeller
 * @author Adrian Colyer
 * @since 1.1.3
 * @see org.springframework.aop.framework.ReflectiveMethodInvocation
 * @see org.springframework.aop.support.DelegatingIntroductionInterceptor
 */
public interface ProxyMethodInvocation extends MethodInvocation {

	/**
	 * Return the proxy that this method invocation was made through.
	 * <p>
	 *  返回此方法调用的代理
	 * 
	 * 
	 * @return the original proxy object
	 */
	Object getProxy();

	/**
	 * Create a clone of this object. If cloning is done before {@code proceed()}
	 * is invoked on this object, {@code proceed()} can be invoked once per clone
	 * to invoke the joinpoint (and the rest of the advice chain) more than once.
	 * <p>
	 *  创建此对象的克隆如果在此对象上调用{@code proceed()}之前完成克隆,则每个克隆可以调用{@code proceed()}一次以调用连接点(以及其他建议链)不止一次
	 * 
	 * 
	 * @return an invocable clone of this invocation.
	 * {@code proceed()} can be called once per clone.
	 */
	MethodInvocation invocableClone();

	/**
	 * Create a clone of this object. If cloning is done before {@code proceed()}
	 * is invoked on this object, {@code proceed()} can be invoked once per clone
	 * to invoke the joinpoint (and the rest of the advice chain) more than once.
	 * <p>
	 *  创建此对象的克隆如果在此对象上调用{@code proceed()}之前完成克隆,则每个克隆可以调用{@code proceed()}一次以调用连接点(以及其他建议链)不止一次
	 * 
	 * 
	 * @param arguments the arguments that the cloned invocation is supposed to use,
	 * overriding the original arguments
	 * @return an invocable clone of this invocation.
	 * {@code proceed()} can be called once per clone.
	 */
	MethodInvocation invocableClone(Object... arguments);

	/**
	 * Set the arguments to be used on subsequent invocations in the any advice
	 * in this chain.
	 * <p>
	 *  在此链中的任何建议中设置要用于后续调用的参数
	 * 
	 * 
	 * @param arguments the argument array
	 */
	void setArguments(Object... arguments);

	/**
	 * Add the specified user attribute with the given value to this invocation.
	 * <p>Such attributes are not used within the AOP framework itself. They are
	 * just kept as part of the invocation object, for use in special interceptors.
	 * <p>
	 * 将具有给定值的指定用户属性添加到此调用<p>此类属性不在AOP框架本身内使用它们仅作为调用对象的一部分使用,用于特殊拦截器
	 * 
	 * 
	 * @param key the name of the attribute
	 * @param value the value of the attribute, or {@code null} to reset it
	 */
	void setUserAttribute(String key, Object value);

	/**
	 * Return the value of the specified user attribute.
	 * <p>
	 *  返回指定用户属性的值
	 * 
	 * @param key the name of the attribute
	 * @return the value of the attribute, or {@code null} if not set
	 * @see #setUserAttribute
	 */
	Object getUserAttribute(String key);

}
