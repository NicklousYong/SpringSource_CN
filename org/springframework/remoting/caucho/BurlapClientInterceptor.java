/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.remoting.caucho;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.ConnectException;
import java.net.MalformedURLException;

import com.caucho.burlap.client.BurlapProxyFactory;
import com.caucho.burlap.client.BurlapRuntimeException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;
import org.springframework.util.Assert;

/**
 * {@link org.aopalliance.intercept.MethodInterceptor} for accessing a Burlap service.
 * Supports authentication via username and password.
 * The service URL must be an HTTP URL exposing a Burlap service.
 *
 * <p>Burlap is a slim, XML-based RPC protocol.
 * For information on Burlap, see the
 * <a href="http://www.caucho.com/burlap">Burlap website</a>
 *
 * <p>Note: There is no requirement for services accessed with this proxy factory
 * to have been exported using Spring's {@link BurlapServiceExporter}, as there is
 * no special handling involved. As a consequence, you can also access services that
 * have been exported using Caucho's {@link com.caucho.burlap.server.BurlapServlet}.
 *
 * <p>
 *  用于访问Burlap服务的{@link orgaopallianceinterceptMethodInterceptor}支持通过用户名和密码进行身份验证服务URL必须是一个暴露了一个Burlap服务
 * 的HTTP URL。
 * 
 * <p> Burlap是一个纤薄的基于XML的RPC协议有关Burlap的信息,请参阅<a href=\"http://wwwcauchocom/burlap\"> Burlap网站</a>
 * 
 *  注意：没有要求使用此代理工厂访问的服务已使用Spring的{@link BurlapServiceExporter}导出,因为没有特殊的处理方法,因此您还可以访问使用Caucho的导出的服务{@link comcauchoburlapserverBurlapServlet}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 29.09.2003
 * @see #setServiceInterface
 * @see #setServiceUrl
 * @see #setUsername
 * @see #setPassword
 * @see BurlapServiceExporter
 * @see BurlapProxyFactoryBean
 * @see com.caucho.burlap.client.BurlapProxyFactory
 * @see com.caucho.burlap.server.BurlapServlet
 * @deprecated as of Spring 4.0, since Burlap hasn't evolved in years
 * and is effectively retired (in contrast to its sibling Hessian)
 */
@Deprecated
public class BurlapClientInterceptor extends UrlBasedRemoteAccessor implements MethodInterceptor {

	private BurlapProxyFactory proxyFactory = new BurlapProxyFactory();

	private Object burlapProxy;


	/**
	 * Set the BurlapProxyFactory instance to use.
	 * If not specified, a default BurlapProxyFactory will be created.
	 * <p>Allows to use an externally configured factory instance,
	 * in particular a custom BurlapProxyFactory subclass.
	 * <p>
	 *  将BurlapProxyFactory实例设置为使用如果未指定,将创建默认的BurlapProxyFactory <p>允许使用外部配置的工厂实例,特别是自定义BurlapProxyFactory子类
	 * 。
	 * 
	 */
	public void setProxyFactory(BurlapProxyFactory proxyFactory) {
		this.proxyFactory = (proxyFactory != null ? proxyFactory : new BurlapProxyFactory());
	}

	/**
	 * Set the username that this factory should use to access the remote service.
	 * Default is none.
	 * <p>The username will be sent by Burlap via HTTP Basic Authentication.
	 * <p>
	 * 设置此工厂用于访问远程服务的用户名Default is none <p>用户名将由Burlap通过HTTP基本认证发送
	 * 
	 * 
	 * @see com.caucho.burlap.client.BurlapProxyFactory#setUser
	 */
	public void setUsername(String username) {
		this.proxyFactory.setUser(username);
	}

	/**
	 * Set the password that this factory should use to access the remote service.
	 * Default is none.
	 * <p>The password will be sent by Burlap via HTTP Basic Authentication.
	 * <p>
	 *  设置此工厂用于访问远程服务的密码默认值为无<p>密码将由Burlap通过HTTP基本认证发送
	 * 
	 * 
	 * @see com.caucho.burlap.client.BurlapProxyFactory#setPassword
	 */
	public void setPassword(String password) {
		this.proxyFactory.setPassword(password);
	}

	/**
	 * Set whether overloaded methods should be enabled for remote invocations.
	 * Default is "false".
	 * <p>
	 *  设置是否为远程调用启用重载方法默认值为"false"
	 * 
	 * 
	 * @see com.caucho.burlap.client.BurlapProxyFactory#setOverloadEnabled
	 */
	public void setOverloadEnabled(boolean overloadEnabled) {
		this.proxyFactory.setOverloadEnabled(overloadEnabled);
	}


	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		prepare();
	}

	/**
	 * Initialize the Burlap proxy for this interceptor.
	 * <p>
	 *  初始化此拦截器的Burlap代理
	 * 
	 * 
	 * @throws RemoteLookupFailureException if the service URL is invalid
	 */
	public void prepare() throws RemoteLookupFailureException {
		try {
			this.burlapProxy = createBurlapProxy(this.proxyFactory);
		}
		catch (MalformedURLException ex) {
			throw new RemoteLookupFailureException("Service URL [" + getServiceUrl() + "] is invalid", ex);
		}
	}

	/**
	 * Create the Burlap proxy that is wrapped by this interceptor.
	 * <p>
	 *  创建由此拦截器包装的Burlap代理
	 * 
	 * 
	 * @param proxyFactory the proxy factory to use
	 * @return the Burlap proxy
	 * @throws MalformedURLException if thrown by the proxy factory
	 * @see com.caucho.burlap.client.BurlapProxyFactory#create
	 */
	protected Object createBurlapProxy(BurlapProxyFactory proxyFactory) throws MalformedURLException {
		Assert.notNull(getServiceInterface(), "Property 'serviceInterface' is required");
		return proxyFactory.create(getServiceInterface(), getServiceUrl());
	}


	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (this.burlapProxy == null) {
			throw new IllegalStateException("BurlapClientInterceptor is not properly initialized - " +
					"invoke 'prepare' before attempting any operations");
		}

		ClassLoader originalClassLoader = overrideThreadContextClassLoader();
		try {
			return invocation.getMethod().invoke(this.burlapProxy, invocation.getArguments());
		}
		catch (InvocationTargetException ex) {
			Throwable targetEx = ex.getTargetException();
			if (targetEx instanceof BurlapRuntimeException) {
				Throwable cause = targetEx.getCause();
				throw convertBurlapAccessException(cause != null ? cause : targetEx);
			}
			else if (targetEx instanceof UndeclaredThrowableException) {
				UndeclaredThrowableException utex = (UndeclaredThrowableException) targetEx;
				throw convertBurlapAccessException(utex.getUndeclaredThrowable());
			}
			else {
				throw targetEx;
			}
		}
		catch (Throwable ex) {
			throw new RemoteProxyFailureException(
					"Failed to invoke Burlap proxy for remote service [" + getServiceUrl() + "]", ex);
		}
		finally {
			resetThreadContextClassLoader(originalClassLoader);
		}
	}

	/**
	 * Convert the given Burlap access exception to an appropriate
	 * Spring RemoteAccessException.
	 * <p>
	 *  将给定的Burlap访问异常转换为适当的Spring RemoteAccessException
	 * 
	 * @param ex the exception to convert
	 * @return the RemoteAccessException to throw
	 */
	protected RemoteAccessException convertBurlapAccessException(Throwable ex) {
		if (ex instanceof ConnectException) {
			return new RemoteConnectFailureException(
					"Cannot connect to Burlap remote service at [" + getServiceUrl() + "]", ex);
		}
		else {
			return new RemoteAccessException(
				"Cannot access Burlap remote service at [" + getServiceUrl() + "]", ex);
		}
	}

}
