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

/**
 * Abstract base class for classes that access a remote service.
 * Provides a "serviceInterface" bean property.
 *
 * <p>Note that the service interface being used will show some signs of
 * remotability, like the granularity of method calls that it offers.
 * Furthermore, it has to have serializable arguments etc.
 *
 * <p>Accessors are supposed to throw Spring's generic
 * {@link org.springframework.remoting.RemoteAccessException} in case
 * of remote invocation failure, provided that the service interface
 * does not declare {@code java.rmi.RemoteException}.
 *
 * <p>
 *  访问远程服务的类的抽象基类提供"serviceInterface"bean属性
 * 
 * <p>请注意,正在使用的服务界面将显示一些远程的迹象,如其提供的方法调用的粒度。此外,它必须具有可序列化的参数等
 * 
 *  <p>访问者应该抛出Spring的泛型{@link orgspringframeworkremotingRemoteAccessException},以防远程调用失败,前提是服务接口不声明{@code javarmiRemoteException}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see org.springframework.remoting.RemoteAccessException
 * @see java.rmi.RemoteException
 */
public abstract class RemoteAccessor extends RemotingSupport {

	private Class<?> serviceInterface;


	/**
	 * Set the interface of the service to access.
	 * The interface must be suitable for the particular service and remoting strategy.
	 * <p>Typically required to be able to create a suitable service proxy,
	 * but can also be optional if the lookup returns a typed proxy.
	 * <p>
	 */
	public void setServiceInterface(Class<?> serviceInterface) {
		if (serviceInterface != null && !serviceInterface.isInterface()) {
			throw new IllegalArgumentException("'serviceInterface' must be an interface");
		}
		this.serviceInterface = serviceInterface;
	}

	/**
	 * Return the interface of the service to access.
	 * <p>
	 *  将服务的接口设置为访问该接口必须适合于特定的服务和远程处理策略<p>通常需要能够创建合适的服务代理,但如果查找返回类型代理,也可以是可选的
	 * 
	 */
	public Class<?> getServiceInterface() {
		return this.serviceInterface;
	}

}
