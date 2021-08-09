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

package org.springframework.aop.framework;

/**
 * Delegate interface for a configured AOP proxy, allowing for the creation
 * of actual proxy objects.
 *
 * <p>Out-of-the-box implementations are available for JDK dynamic proxies
 * and for CGLIB proxies, as applied by {@link DefaultAopProxyFactory}.
 *
 * <p>
 *  为配置的AOP代理委托接口,允许创建实际的代理对象
 * 
 * <p>开箱即用的实现可用于JDK动态代理和CGLIB代理,由{@link DefaultAopProxyFactory}应用
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DefaultAopProxyFactory
 */
public interface AopProxy {

	/**
	 * Create a new proxy object.
	 * <p>Uses the AopProxy's default class loader (if necessary for proxy creation):
	 * usually, the thread context class loader.
	 * <p>
	 *  创建一个新的代理对象<p>使用AopProxy的默认类加载器(如果需要代理创建)：通常,线程上下文类加载器
	 * 
	 * 
	 * @return the new proxy object (never {@code null})
	 * @see Thread#getContextClassLoader()
	 */
	Object getProxy();

	/**
	 * Create a new proxy object.
	 * <p>Uses the given class loader (if necessary for proxy creation).
	 * {@code null} will simply be passed down and thus lead to the low-level
	 * proxy facility's default, which is usually different from the default chosen
	 * by the AopProxy implementation's {@link #getProxy()} method.
	 * <p>
	 *  创建一个新的代理对象<p>使用给定的类加载器(如果需要代理创建){@code null}将被简单地传递下来,从而导致低级代理设备的默认值,这通常不同于默认选择通过AopProxy实现的{@link #getProxy()}
	 * 方法。
	 * 
	 * @param classLoader the class loader to create the proxy with
	 * (or {@code null} for the low-level proxy facility's default)
	 * @return the new proxy object (never {@code null})
	 */
	Object getProxy(ClassLoader classLoader);

}
