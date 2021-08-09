/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2007 the original author or authors.
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

import java.lang.reflect.InvocationTargetException;

/**
 * Abstract base class for remote service exporters that are based
 * on deserialization of {@link RemoteInvocation} objects.
 *
 * <p>Provides a "remoteInvocationExecutor" property, with a
 * {@link DefaultRemoteInvocationExecutor} as default strategy.
 *
 * <p>
 *  基于{@link RemoteInvocation}对象的反序列化的远程服务出口商的抽象基类
 * 
 * <p>提供"remoteInvocationExecutor"属性,使用{@link DefaultRemoteInvocationExecutor}作为默认策略
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see RemoteInvocationExecutor
 * @see DefaultRemoteInvocationExecutor
 */
public abstract class RemoteInvocationBasedExporter extends RemoteExporter {

	private RemoteInvocationExecutor remoteInvocationExecutor = new DefaultRemoteInvocationExecutor();


	/**
	 * Set the RemoteInvocationExecutor to use for this exporter.
	 * Default is a DefaultRemoteInvocationExecutor.
	 * <p>A custom invocation executor can extract further context information
	 * from the invocation, for example user credentials.
	 * <p>
	 *  设置RemoteInvocationExecutor以用于此导出器默认值为DefaultRemoteInvocationExecutor <p>自定义调用执行程序可以从调用中提取进一步的上下文信息,例
	 * 如用户凭据。
	 * 
	 */
	public void setRemoteInvocationExecutor(RemoteInvocationExecutor remoteInvocationExecutor) {
		this.remoteInvocationExecutor = remoteInvocationExecutor;
	}

	/**
	 * Return the RemoteInvocationExecutor used by this exporter.
	 * <p>
	 *  返回此导出器使用的RemoteInvocationExecutor
	 * 
	 */
	public RemoteInvocationExecutor getRemoteInvocationExecutor() {
		return this.remoteInvocationExecutor;
	}


	/**
	 * Apply the given remote invocation to the given target object.
	 * The default implementation delegates to the RemoteInvocationExecutor.
	 * <p>Can be overridden in subclasses for custom invocation behavior,
	 * possibly for applying additional invocation parameters from a
	 * custom RemoteInvocation subclass. Note that it is preferable to use
	 * a custom RemoteInvocationExecutor which is a reusable strategy.
	 * <p>
	 * 将给定的远程调用应用于给定的目标对象默认实现委托给RemoteInvocationExecutor <p>可以在子类中覆盖自定义调用行为,可能用于从自定义RemoteInvocation子类应用其他调用
	 * 参数请注意,最好使用自定义RemoteInvocationExecutor是一种可重用的策略。
	 * 
	 * 
	 * @param invocation the remote invocation
	 * @param targetObject the target object to apply the invocation to
	 * @return the invocation result
	 * @throws NoSuchMethodException if the method name could not be resolved
	 * @throws IllegalAccessException if the method could not be accessed
	 * @throws InvocationTargetException if the method invocation resulted in an exception
	 * @see RemoteInvocationExecutor#invoke
	 */
	protected Object invoke(RemoteInvocation invocation, Object targetObject)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		if (logger.isTraceEnabled()) {
			logger.trace("Executing " + invocation);
		}
		try {
			return getRemoteInvocationExecutor().invoke(invocation, targetObject);
		}
		catch (NoSuchMethodException ex) {
			if (logger.isDebugEnabled()) {
				logger.warn("Could not find target method for " + invocation, ex);
			}
			throw ex;
		}
		catch (IllegalAccessException ex) {
			if (logger.isDebugEnabled()) {
				logger.warn("Could not access target method for " + invocation, ex);
			}
			throw ex;
		}
		catch (InvocationTargetException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Target method failed for " + invocation, ex.getTargetException());
			}
			throw ex;
		}
	}

	/**
	 * Apply the given remote invocation to the given target object, wrapping
	 * the invocation result in a serializable RemoteInvocationResult object.
	 * The default implementation creates a plain RemoteInvocationResult.
	 * <p>Can be overridden in subclasses for custom invocation behavior,
	 * for example to return additional context information. Note that this
	 * is not covered by the RemoteInvocationExecutor strategy!
	 * <p>
	 *  将给定的远程调用应用于给定的目标对象,将调用结果包装在可序列化的RemoteInvocationResult对象中默认实现创建一个简单的RemoteInvocationResult <p>可以在子类中
	 * 覆盖自定义调用行为,例如返回其他上下文信息请注意,不在RemoteInvocationExecutor策略范围内！。
	 * 
	 * @param invocation the remote invocation
	 * @param targetObject the target object to apply the invocation to
	 * @return the invocation result
	 * @see #invoke
	 */
	protected RemoteInvocationResult invokeAndCreateResult(RemoteInvocation invocation, Object targetObject) {
		try {
			Object value = invoke(invocation, targetObject);
			return new RemoteInvocationResult(value);
		}
		catch (Throwable ex) {
			return new RemoteInvocationResult(ex);
		}
	}

}
