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

package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;

import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;

/**
 * Convenient superclass for RMI-based remote exporters. Provides a facility
 * to automatically wrap a given plain Java service object with an
 * RmiInvocationWrapper, exposing the {@link RmiInvocationHandler} remote interface.
 *
 * <p>Using the RMI invoker mechanism, RMI communication operates at the {@link RmiInvocationHandler}
 * level, sharing a common invoker stub for any number of services. Service interfaces are <i>not</i>
 * required to extend {@code java.rmi.Remote} or declare {@code java.rmi.RemoteException}
 * on all service methods. However, in and out parameters still have to be serializable.
 *
 * <p>
 *  基于RMI的远程出口商的方便的超类提供了使用RmiInvocationWrapper自动包装给定的普通Java服务对象的功能,展现了{@link RmiInvocationHandler}远程接口
 * 
 * <p>使用RMI调用机制,RMI通信在{@link RmiInvocationHandler}级别运行,为任何数量的服务共享一个普通的调用者存根服务接口</i>不需要扩展{@code javarmiRemote}
 * 或在所有服务方法上声明{@code javarmiRemoteException}但是,输入和输出参数仍然必须是可序列化的。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.5
 * @see RmiServiceExporter
 * @see JndiRmiServiceExporter
 */
public abstract class RmiBasedExporter extends RemoteInvocationBasedExporter {

	/**
	 * Determine the object to export: either the service object itself
	 * or a RmiInvocationWrapper in case of a non-RMI service object.
	 * <p>
	 *  确定要导出的对象：在非RMI服务对象的情况下,服务对象本身或RmiInvocationWrapper
	 * 
	 * 
	 * @return the RMI object to export
	 * @see #setService
	 * @see #setServiceInterface
	 */
	protected Remote getObjectToExport() {
		// determine remote object
		if (getService() instanceof Remote &&
				(getServiceInterface() == null || Remote.class.isAssignableFrom(getServiceInterface()))) {
			// conventional RMI service
			return (Remote) getService();
		}
		else {
			// RMI invoker
			if (logger.isDebugEnabled()) {
				logger.debug("RMI service [" + getService() + "] is an RMI invoker");
			}
			return new RmiInvocationWrapper(getProxyForService(), this);
		}
	}

	/**
	 * Redefined here to be visible to RmiInvocationWrapper.
	 * Simply delegates to the corresponding superclass method.
	 * <p>
	 *  在这里重新定义为RmiInvocationWrapper可见只是委托给相应的超类方法
	 */
	@Override
	protected Object invoke(RemoteInvocation invocation, Object targetObject)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		return super.invoke(invocation, targetObject);
	}

}
