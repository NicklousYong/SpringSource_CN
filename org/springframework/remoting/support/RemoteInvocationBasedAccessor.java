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

package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Abstract base class for remote service accessors that are based
 * on serialization of {@link RemoteInvocation} objects.
 *
 * Provides a "remoteInvocationFactory" property, with a
 * {@link DefaultRemoteInvocationFactory} as default strategy.
 *
 * <p>
 *  基于{@link RemoteInvocation}对象的序列化的远程服务访问器的抽象基类
 * 
 * 提供"remoteInvocationFactory"属性,使用{@link DefaultRemoteInvocationFactory}作为默认策略
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setRemoteInvocationFactory
 * @see RemoteInvocation
 * @see RemoteInvocationFactory
 * @see DefaultRemoteInvocationFactory
 */
public abstract class RemoteInvocationBasedAccessor extends UrlBasedRemoteAccessor {

	private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();


	/**
	 * Set the RemoteInvocationFactory to use for this accessor.
	 * Default is a {@link DefaultRemoteInvocationFactory}.
	 * <p>A custom invocation factory can add further context information
	 * to the invocation, for example user credentials.
	 * <p>
	 *  将RemoteInvocationFactory设置为用于此访问者默认为{@link DefaultRemoteInvocationFactory} <p>自定义调用工厂可以向调用添加其他上下文信息,
	 * 例如用户凭据。
	 * 
	 */
	public void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
		this.remoteInvocationFactory =
				(remoteInvocationFactory != null ? remoteInvocationFactory : new DefaultRemoteInvocationFactory());
	}

	/**
	 * Return the RemoteInvocationFactory used by this accessor.
	 * <p>
	 *  返回此访问器使用的RemoteInvocationFactory
	 * 
	 */
	public RemoteInvocationFactory getRemoteInvocationFactory() {
		return this.remoteInvocationFactory;
	}

	/**
	 * Create a new RemoteInvocation object for the given AOP method invocation.
	 * <p>The default implementation delegates to the configured
	 * {@link #setRemoteInvocationFactory RemoteInvocationFactory}.
	 * This can be overridden in subclasses in order to provide custom RemoteInvocation
	 * subclasses, containing additional invocation parameters (e.g. user credentials).
	 * <p>Note that it is preferable to build a custom RemoteInvocationFactory
	 * as a reusable strategy, instead of overriding this method.
	 * <p>
	 * 为给定的AOP方法调用创建一个新的RemoteInvocation对象<p>默认实现委托给已配置的{@link #setRemoteInvocationFactory RemoteInvocationFactory}
	 * 这可以在子类中覆盖,以便提供自定义RemoteInvocation子类,其中包含其他调用参数(例如用户凭据)<p>请注意,最好将自定义RemoteInvocationFactory构建为可重用的策略,而
	 * 不是覆盖此方法。
	 * 
	 * 
	 * @param methodInvocation the current AOP method invocation
	 * @return the RemoteInvocation object
	 * @see RemoteInvocationFactory#createRemoteInvocation
	 */
	protected RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
		return getRemoteInvocationFactory().createRemoteInvocation(methodInvocation);
	}

	/**
	 * Recreate the invocation result contained in the given RemoteInvocationResult object.
	 * <p>The default implementation calls the default {@code recreate()} method.
	 * This can be overridden in subclass to provide custom recreation, potentially
	 * processing the returned result object.
	 * <p>
	 * 
	 * @param result the RemoteInvocationResult to recreate
	 * @return a return value if the invocation result is a successful return
	 * @throws Throwable if the invocation result is an exception
	 * @see RemoteInvocationResult#recreate()
	 */
	protected Object recreateRemoteInvocationResult(RemoteInvocationResult result) throws Throwable {
		return result.recreate();
	}

}
