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
import java.rmi.RemoteException;

import org.springframework.remoting.support.RemoteInvocation;

/**
 * Interface for RMI invocation handlers instances on the server,
 * wrapping exported services. A client uses a stub implementing
 * this interface to access such a service.
 *
 * <p>This is an SPI interface, not to be used directly by applications.
 *
 * <p>
 *  服务器上RMI调用处理程序实例的接口,包装导出的服务客户端使用实现此接口的存根访问这样的服务
 * 
 * <p>这是一个SPI接口,不被应用程序直接使用
 * 
 * 
 * @author Juergen Hoeller
 * @since 14.05.2003
 */
public interface RmiInvocationHandler extends Remote {

	/**
	 * Return the name of the target interface that this invoker operates on.
	 * <p>
	 *  返回此调用者操作的目标接口的名称
	 * 
	 * 
	 * @return the name of the target interface, or {@code null} if none
	 * @throws RemoteException in case of communication errors
	 * @see RmiServiceExporter#getServiceInterface()
	 */
	public String getTargetInterfaceName() throws RemoteException;

	/**
	 * Apply the given invocation to the target object.
	 * <p>Called by
	 * {@link RmiClientInterceptor#doInvoke(org.aopalliance.intercept.MethodInvocation, RmiInvocationHandler)}.
	 * <p>
	 *  将给定的调用应用于目标对象<p>由{@link RmiClientInterceptor#doInvoke(orgaopallianceinterceptMethodInvocation,RmiInvocationHandler)}
	 * 调用}。
	 * 
	 * @param invocation object that encapsulates invocation parameters
	 * @return the object returned from the invoked method, if any
	 * @throws RemoteException in case of communication errors
	 * @throws NoSuchMethodException if the method name could not be resolved
	 * @throws IllegalAccessException if the method could not be accessed
	 * @throws InvocationTargetException if the method invocation resulted in an exception
	 */
	public Object invoke(RemoteInvocation invocation)
			throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

}
