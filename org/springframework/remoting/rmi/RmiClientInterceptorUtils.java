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

package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.StubNotFoundException;
import java.rmi.UnknownHostException;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.NO_RESPONSE;
import org.omg.CORBA.SystemException;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.util.ReflectionUtils;

/**
 * Factored-out methods for performing invocations within an RMI client.
 * Can handle both RMI and non-RMI service interfaces working on an RMI stub.
 *
 * <p>Note: This is an SPI class, not intended to be used by applications.
 *
 * <p>
 *  用于在RMI客户端中执行调用的有缺点的方法可以处理在RMI存根上工作的RMI和非RMI服务接口
 * 
 *  <p>注意：这是一个SPI类,不适用于应用程序
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 */
public abstract class RmiClientInterceptorUtils {

	private static final Log logger = LogFactory.getLog(RmiClientInterceptorUtils.class);


	/**
	 * Perform a raw method invocation on the given RMI stub,
	 * letting reflection exceptions through as-is.
	 * <p>
	 * 在给定的RMI存根上执行原始方法调用,通过原样反映异常
	 * 
	 * 
	 * @param invocation the AOP MethodInvocation
	 * @param stub the RMI stub
	 * @return the invocation result, if any
	 * @throws InvocationTargetException if thrown by reflection
	 */
	public static Object invokeRemoteMethod(MethodInvocation invocation, Object stub)
			throws InvocationTargetException {

		Method method = invocation.getMethod();
		try {
			if (method.getDeclaringClass().isInstance(stub)) {
				// directly implemented
				return method.invoke(stub, invocation.getArguments());
			}
			else {
				// not directly implemented
				Method stubMethod = stub.getClass().getMethod(method.getName(), method.getParameterTypes());
				return stubMethod.invoke(stub, invocation.getArguments());
			}
		}
		catch (InvocationTargetException ex) {
			throw ex;
		}
		catch (NoSuchMethodException ex) {
			throw new RemoteProxyFailureException("No matching RMI stub method found for: " + method, ex);
		}
		catch (Throwable ex) {
			throw new RemoteProxyFailureException("Invocation of RMI stub method failed: " + method, ex);
		}
	}

	/**
	 * Wrap the given arbitrary exception that happened during remote access
	 * in either a RemoteException or a Spring RemoteAccessException (if the
	 * method signature does not support RemoteException).
	 * <p>Only call this for remote access exceptions, not for exceptions
	 * thrown by the target service itself!
	 * <p>
	 *  将RemoteException或Spring RemoteAccessException中的远程访问中发生的给定的任意异常(如果方法签名不支持RemoteException)包含在外。
	 * <p>仅将该参数称为远程访问异常,而不是由目标服务本身抛出的异常！。
	 * 
	 * 
	 * @param method the invoked method
	 * @param ex the exception that happened, to be used as cause for the
	 * RemoteAccessException or RemoteException
	 * @param message the message for the RemoteAccessException respectively
	 * RemoteException
	 * @return the exception to be thrown to the caller
	 */
	public static Exception convertRmiAccessException(Method method, Throwable ex, String message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message, ex);
		}
		if (ReflectionUtils.declaresException(method, RemoteException.class)) {
			return new RemoteException(message, ex);
		}
		else {
			return new RemoteAccessException(message, ex);
		}
	}

	/**
	 * Convert the given RemoteException that happened during remote access
	 * to Spring's RemoteAccessException if the method signature does not
	 * support RemoteException. Else, return the original RemoteException.
	 * <p>
	 *  转换远程访问Spring RemoteAccessException期间发生的给定RemoteException,如果方法签名不支持RemoteException Else,返回原始的RemoteEx
	 * ception。
	 * 
	 * 
	 * @param method the invoked method
	 * @param ex the RemoteException that happened
	 * @param serviceName the name of the service (for debugging purposes)
	 * @return the exception to be thrown to the caller
	 */
	public static Exception convertRmiAccessException(Method method, RemoteException ex, String serviceName) {
		return convertRmiAccessException(method, ex, isConnectFailure(ex), serviceName);
	}

	/**
	 * Convert the given RemoteException that happened during remote access
	 * to Spring's RemoteAccessException if the method signature does not
	 * support RemoteException. Else, return the original RemoteException.
	 * <p>
	 * 转换远程访问Spring RemoteAccessException期间发生的给定RemoteException,如果方法签名不支持RemoteException Else,返回原始的RemoteExc
	 * eption。
	 * 
	 * 
	 * @param method the invoked method
	 * @param ex the RemoteException that happened
	 * @param isConnectFailure whether the given exception should be considered
	 * a connect failure
	 * @param serviceName the name of the service (for debugging purposes)
	 * @return the exception to be thrown to the caller
	 */
	public static Exception convertRmiAccessException(
			Method method, RemoteException ex, boolean isConnectFailure, String serviceName) {

		if (logger.isDebugEnabled()) {
			logger.debug("Remote service [" + serviceName + "] threw exception", ex);
		}
		if (ReflectionUtils.declaresException(method, ex.getClass())) {
			return ex;
		}
		else {
			if (isConnectFailure) {
				return new RemoteConnectFailureException("Could not connect to remote service [" + serviceName + "]", ex);
			}
			else {
				return new RemoteAccessException("Could not access remote service [" + serviceName + "]", ex);
			}
		}
	}

	/**
	 * Determine whether the given RMI exception indicates a connect failure.
	 * <p>Treats RMI's ConnectException, ConnectIOException, UnknownHostException,
	 * NoSuchObjectException and StubNotFoundException as connect failure.
	 * <p>
	 *  确定给定的RMI异常是否指示连接失败<p>将RMI的ConnectException,ConnectIOException,UnknownHostException,NoSuchObjectExcep
	 * tion和StubNotFoundException视为连接失败。
	 * 
	 * 
	 * @param ex the RMI exception to check
	 * @return whether the exception should be treated as connect failure
	 * @see java.rmi.ConnectException
	 * @see java.rmi.ConnectIOException
	 * @see java.rmi.UnknownHostException
	 * @see java.rmi.NoSuchObjectException
	 * @see java.rmi.StubNotFoundException
	 */
	public static boolean isConnectFailure(RemoteException ex) {
		return (ex instanceof ConnectException || ex instanceof ConnectIOException ||
				ex instanceof UnknownHostException || ex instanceof NoSuchObjectException ||
				ex instanceof StubNotFoundException || ex.getCause() instanceof SocketException ||
				isCorbaConnectFailure(ex.getCause()));
	}

	/**
	 * Check whether the given RMI exception root cause indicates a CORBA
	 * connection failure.
	 * <p>This is relevant on the IBM JVM, in particular for WebSphere EJB clients.
	 * <p>See the
	 * <a href="http://www.redbooks.ibm.com/Redbooks.nsf/RedbookAbstracts/tips0243.html">IBM website</code>
	 * for details.
	 * <p>
	 *  检查给定的RMI异常根本原因是否指示CORBA连接失败<p>这与IBM JVM有关,特别是对于WebSphere EJB客户端<p>请参阅<a href ="http：// wwwredbooksibmcom / Redbooksnsf / RedbookAbstracts / tips0243html">
	 *  IBM网站</code>了解详情。
	 * 
	 * @param ex the RMI exception to check
	 */
	private static boolean isCorbaConnectFailure(Throwable ex) {
		return ((ex instanceof COMM_FAILURE || ex instanceof NO_RESPONSE) &&
				((SystemException) ex).completed == CompletionStatus.COMPLETED_NO);
	}

}
