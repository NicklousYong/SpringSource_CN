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

import com.caucho.hessian.HessianException;
import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.SerializerFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;
import org.springframework.util.Assert;

/**
 * {@link org.aopalliance.intercept.MethodInterceptor} for accessing a Hessian service.
 * Supports authentication via username and password.
 * The service URL must be an HTTP URL exposing a Hessian service.
 *
 * <p>Hessian is a slim, binary RPC protocol.
 * For information on Hessian, see the
 * <a href="http://www.caucho.com/hessian">Hessian website</a>
 * <b>Note: As of Spring 4.0, this client requires Hessian 4.0 or above.</b>
 *
 * <p>Note: There is no requirement for services accessed with this proxy factory
 * to have been exported using Spring's {@link HessianServiceExporter}, as there is
 * no special handling involved. As a consequence, you can also access services that
 * have been exported using Caucho's {@link com.caucho.hessian.server.HessianServlet}.
 *
 * <p>
 *  用于访问Hessian服务的{@link orgaopallianceinterceptMethodInterceptor}支持通过用户名和密码进行身份验证服务URL必须是暴露Hessian服务的HT
 * TP URL。
 * 
 * <p> Hessian是一个超薄的二进制RPC协议有关Hessian的信息,请参阅<a href=\"http://wwwcauchocom/hessian\"> Hessian网站</a> <b>注意
 * ：截至春季40,此客户端要求Hessian 40以上</b>。
 * 
 *  注意：使用此代理工厂访问的服务不需要使用Spring的{@link HessianServiceExporter}导出,因为没有特殊的处理方式,因此您还可以访问使用Caucho的导出的服务{@link comcauchohessianserverHessianServlet}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 29.09.2003
 * @see #setServiceInterface
 * @see #setServiceUrl
 * @see #setUsername
 * @see #setPassword
 * @see HessianServiceExporter
 * @see HessianProxyFactoryBean
 * @see com.caucho.hessian.client.HessianProxyFactory
 * @see com.caucho.hessian.server.HessianServlet
 */
public class HessianClientInterceptor extends UrlBasedRemoteAccessor implements MethodInterceptor {

	private HessianProxyFactory proxyFactory = new HessianProxyFactory();

	private Object hessianProxy;


	/**
	 * Set the HessianProxyFactory instance to use.
	 * If not specified, a default HessianProxyFactory will be created.
	 * <p>Allows to use an externally configured factory instance,
	 * in particular a custom HessianProxyFactory subclass.
	 * <p>
	 *  将HessianProxyFactory实例设置为使用如果未指定,将创建默认的HessianProxyFactory <p>允许使用外部配置的工厂实例,特别是自定义HessianProxyFactor
	 * y子类。
	 * 
	 */
	public void setProxyFactory(HessianProxyFactory proxyFactory) {
		this.proxyFactory = (proxyFactory != null ? proxyFactory : new HessianProxyFactory());
	}

	/**
	 * Specify the Hessian SerializerFactory to use.
	 * <p>This will typically be passed in as an inner bean definition
	 * of type {@code com.caucho.hessian.io.SerializerFactory},
	 * with custom bean property values applied.
	 * <p>
	 * 指定Hessian SerializerFactory以使用<p>这将通常作为类型为{@code comcauchohessianioSerializerFactory}的内部bean定义传递,应用自定
	 * 义bean属性值。
	 * 
	 */
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		this.proxyFactory.setSerializerFactory(serializerFactory);
	}

	/**
	 * Set whether to send the Java collection type for each serialized
	 * collection. Default is "true".
	 * <p>
	 *  设置是否为每个序列化集合发送Java集合类型默认为"true"
	 * 
	 */
	public void setSendCollectionType(boolean sendCollectionType) {
		this.proxyFactory.getSerializerFactory().setSendCollectionType(sendCollectionType);
	}

	/**
	 * Set whether to allow non-serializable types as Hessian arguments
	 * and return values. Default is "true".
	 * <p>
	 *  设置是否允许非可序列化类型为Hessian参数和返回值默认为"true"
	 * 
	 */
	public void setAllowNonSerializable(boolean allowNonSerializable) {
		this.proxyFactory.getSerializerFactory().setAllowNonSerializable(allowNonSerializable);
	}

	/**
	 * Set whether overloaded methods should be enabled for remote invocations.
	 * Default is "false".
	 * <p>
	 *  设置是否为远程调用启用重载方法默认值为"false"
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setOverloadEnabled
	 */
	public void setOverloadEnabled(boolean overloadEnabled) {
		this.proxyFactory.setOverloadEnabled(overloadEnabled);
	}

	/**
	 * Set the username that this factory should use to access the remote service.
	 * Default is none.
	 * <p>The username will be sent by Hessian via HTTP Basic Authentication.
	 * <p>
	 *  设置此工厂用于访问远程服务的用户名Default is none <p>用户名将由Hessian通过HTTP基本认证发送
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setUser
	 */
	public void setUsername(String username) {
		this.proxyFactory.setUser(username);
	}

	/**
	 * Set the password that this factory should use to access the remote service.
	 * Default is none.
	 * <p>The password will be sent by Hessian via HTTP Basic Authentication.
	 * <p>
	 * 设置此工厂用于访问远程服务的密码默认值为无<p>密码将由Hessian通过HTTP基本认证发送
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setPassword
	 */
	public void setPassword(String password) {
		this.proxyFactory.setPassword(password);
	}

	/**
	 * Set whether Hessian's debug mode should be enabled.
	 * Default is "false".
	 * <p>
	 *  设置是否启用Hessian的调试模式默认为"false"
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setDebug
	 */
	public void setDebug(boolean debug) {
		this.proxyFactory.setDebug(debug);
	}

	/**
	 * Set whether to use a chunked post for sending a Hessian request.
	 * <p>
	 *  设置是否使用分块的帖子发送Hessian请求
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setChunkedPost
	 */
	public void setChunkedPost(boolean chunkedPost) {
		this.proxyFactory.setChunkedPost(chunkedPost);
	}

	/**
	 * Specify a custom HessianConnectionFactory to use for the Hessian client.
	 * <p>
	 *  指定用于Hessian客户端的自定义HessianConnectionFactory
	 * 
	 */
	public void setConnectionFactory(HessianConnectionFactory connectionFactory) {
		this.proxyFactory.setConnectionFactory(connectionFactory);
	}

	/**
	 * Set the socket connect timeout to use for the Hessian client.
	 * <p>
	 *  设置套接字连接超时用于Hessian客户端
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setConnectTimeout
	 */
	public void setConnectTimeout(long timeout) {
		this.proxyFactory.setConnectTimeout(timeout);
	}

	/**
	 * Set the timeout to use when waiting for a reply from the Hessian service.
	 * <p>
	 *  设置等待Hessian服务的回复时使用的超时时间
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setReadTimeout
	 */
	public void setReadTimeout(long timeout) {
		this.proxyFactory.setReadTimeout(timeout);
	}

	/**
	 * Set whether version 2 of the Hessian protocol should be used for
	 * parsing requests and replies. Default is "false".
	 * <p>
	 *  设置Hessian协议的版本2是否应用于解析请求和回复默认值为"false"
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setHessian2Request
	 */
	public void setHessian2(boolean hessian2) {
		this.proxyFactory.setHessian2Request(hessian2);
		this.proxyFactory.setHessian2Reply(hessian2);
	}

	/**
	 * Set whether version 2 of the Hessian protocol should be used for
	 * parsing requests. Default is "false".
	 * <p>
	 *  设置Hessian协议的版本2是否应该用于解析请求默认为"false"
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setHessian2Request
	 */
	public void setHessian2Request(boolean hessian2) {
		this.proxyFactory.setHessian2Request(hessian2);
	}

	/**
	 * Set whether version 2 of the Hessian protocol should be used for
	 * parsing replies. Default is "false".
	 * <p>
	 * 设置Hessian协议的版本2是否应该用于解析回复默认为"false"
	 * 
	 * 
	 * @see com.caucho.hessian.client.HessianProxyFactory#setHessian2Reply
	 */
	public void setHessian2Reply(boolean hessian2) {
		this.proxyFactory.setHessian2Reply(hessian2);
	}


	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		prepare();
	}

	/**
	 * Initialize the Hessian proxy for this interceptor.
	 * <p>
	 *  初始化此拦截器的Hessian代理
	 * 
	 * 
	 * @throws RemoteLookupFailureException if the service URL is invalid
	 */
	public void prepare() throws RemoteLookupFailureException {
		try {
			this.hessianProxy = createHessianProxy(this.proxyFactory);
		}
		catch (MalformedURLException ex) {
			throw new RemoteLookupFailureException("Service URL [" + getServiceUrl() + "] is invalid", ex);
		}
	}

	/**
	 * Create the Hessian proxy that is wrapped by this interceptor.
	 * <p>
	 *  创建由此拦截器包装的Hessian代理
	 * 
	 * 
	 * @param proxyFactory the proxy factory to use
	 * @return the Hessian proxy
	 * @throws MalformedURLException if thrown by the proxy factory
	 * @see com.caucho.hessian.client.HessianProxyFactory#create
	 */
	protected Object createHessianProxy(HessianProxyFactory proxyFactory) throws MalformedURLException {
		Assert.notNull(getServiceInterface(), "'serviceInterface' is required");
		return proxyFactory.create(getServiceInterface(), getServiceUrl(), getBeanClassLoader());
	}


	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (this.hessianProxy == null) {
			throw new IllegalStateException("HessianClientInterceptor is not properly initialized - " +
					"invoke 'prepare' before attempting any operations");
		}

		ClassLoader originalClassLoader = overrideThreadContextClassLoader();
		try {
			return invocation.getMethod().invoke(this.hessianProxy, invocation.getArguments());
		}
		catch (InvocationTargetException ex) {
			Throwable targetEx = ex.getTargetException();
			// Hessian 4.0 check: another layer of InvocationTargetException.
			if (targetEx instanceof InvocationTargetException) {
				targetEx = ((InvocationTargetException) targetEx).getTargetException();
			}
			if (targetEx instanceof HessianConnectionException) {
				throw convertHessianAccessException(targetEx);
			}
			else if (targetEx instanceof HessianException || targetEx instanceof HessianRuntimeException) {
				Throwable cause = targetEx.getCause();
				throw convertHessianAccessException(cause != null ? cause : targetEx);
			}
			else if (targetEx instanceof UndeclaredThrowableException) {
				UndeclaredThrowableException utex = (UndeclaredThrowableException) targetEx;
				throw convertHessianAccessException(utex.getUndeclaredThrowable());
			}
			else {
				throw targetEx;
			}
		}
		catch (Throwable ex) {
			throw new RemoteProxyFailureException(
					"Failed to invoke Hessian proxy for remote service [" + getServiceUrl() + "]", ex);
		}
		finally {
			resetThreadContextClassLoader(originalClassLoader);
		}
	}

	/**
	 * Convert the given Hessian access exception to an appropriate
	 * Spring RemoteAccessException.
	 * <p>
	 *  将给定的Hessian访问异常转换为适当的Spring RemoteAccessException
	 * 
	 * @param ex the exception to convert
	 * @return the RemoteAccessException to throw
	 */
	protected RemoteAccessException convertHessianAccessException(Throwable ex) {
		if (ex instanceof HessianConnectionException || ex instanceof ConnectException) {
			return new RemoteConnectFailureException(
					"Cannot connect to Hessian remote service at [" + getServiceUrl() + "]", ex);
		}
		else {
			return new RemoteAccessException(
				"Cannot access Hessian remote service at [" + getServiceUrl() + "]", ex);
		}
	}

}
