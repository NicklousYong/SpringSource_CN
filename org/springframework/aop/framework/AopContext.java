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

import org.springframework.core.NamedThreadLocal;

/**
 * Class containing static methods used to obtain information about the current AOP invocation.
 *
 * <p>The {@code currentProxy()} method is usable if the AOP framework is configured to
 * expose the current proxy (not the default). It returns the AOP proxy in use. Target objects
 * or advice can use this to make advised calls, in the same way as {@code getEJBObject()}
 * can be used in EJBs. They can also use it to find advice configuration.
 *
 * <p>Spring's AOP framework does not expose proxies by default, as there is a performance cost
 * in doing so.
 *
 * <p>The functionality in this class might be used by a target object that needed access
 * to resources on the invocation. However, this approach should not be used when there is
 * a reasonable alternative, as it makes application code dependent on usage under AOP and
 * the Spring AOP framework in particular.
 *
 * <p>
 *  包含静态方法的类用于获取有关当前AOP调用的信息
 * 
 * <p>如果将AOP框架配置为公开当前代理(而不是默认值),则{@code currentProxy()}方法可用。
 * 它返回使用中的AOP代理Target对象或建议可以使用此方式进行建议调用与{@code getEJBObject()}相同的方式可以在EJB中使用它们也可以使用它来查找建议配置。
 * 
 *  默认情况下,Spring的AOP框架不会公开代理,因为这样做会导致性能成本上升
 * 
 *  <p>此类中的功能可能需要访问调用资源的目标对象使用。但是,当有合理的替代方法时,不应使用此方法,因为它使应用程序代码取决于AOP和Spring AOP框架特别
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 */
public abstract class AopContext {

	/**
	 * ThreadLocal holder for AOP proxy associated with this thread.
	 * Will contain {@code null} unless the "exposeProxy" property on
	 * the controlling proxy configuration has been set to "true".
	 * <p>
	 * 
	 * @see ProxyConfig#setExposeProxy
	 */
	private static final ThreadLocal<Object> currentProxy = new NamedThreadLocal<Object>("Current AOP proxy");


	/**
	 * Try to return the current AOP proxy. This method is usable only if the
	 * calling method has been invoked via AOP, and the AOP framework has been set
	 * to expose proxies. Otherwise, this method will throw an IllegalStateException.
	 * <p>
	 * 与此线程关联的AOP代理的ThreadLocal持有者将包含{@code null},除非控件代理配置上的"exposeProxy"属性设置为"true"
	 * 
	 * 
	 * @return Object the current AOP proxy (never returns {@code null})
	 * @throws IllegalStateException if the proxy cannot be found, because the
	 * method was invoked outside an AOP invocation context, or because the
	 * AOP framework has not been configured to expose the proxy
	 */
	public static Object currentProxy() throws IllegalStateException {
		Object proxy = currentProxy.get();
		if (proxy == null) {
			throw new IllegalStateException(
					"Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available.");
		}
		return proxy;
	}

	/**
	 * Make the given proxy available via the {@code currentProxy()} method.
	 * <p>Note that the caller should be careful to keep the old value as appropriate.
	 * <p>
	 *  尝试返回当前的AOP代理此方法仅在通过AOP调用调用方法时可用,并且AOP框架已设置为公开代理否则,此方法将抛出IllegalStateException
	 * 
	 * 
	 * @param proxy the proxy to expose (or {@code null} to reset it)
	 * @return the old proxy, which may be {@code null} if none was bound
	 * @see #currentProxy()
	 */
	static Object setCurrentProxy(Object proxy) {
		Object old = currentProxy.get();
		if (proxy != null) {
			currentProxy.set(proxy);
		}
		else {
			currentProxy.remove();
		}
		return old;
	}

}
