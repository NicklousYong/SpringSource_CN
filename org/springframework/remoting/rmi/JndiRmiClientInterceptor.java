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
import java.rmi.RemoteException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.SystemException;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.util.ReflectionUtils;

/**
 * {@link org.aopalliance.intercept.MethodInterceptor} for accessing RMI services from JNDI.
 * Typically used for RMI-IIOP (CORBA), but can also be used for EJB home objects
 * (for example, a Stateful Session Bean home). In contrast to a plain JNDI lookup,
 * this accessor also performs narrowing through PortableRemoteObject.
 *
 * <p>With conventional RMI services, this invoker is typically used with the RMI
 * service interface. Alternatively, this invoker can also proxy a remote RMI service
 * with a matching non-RMI business interface, i.e. an interface that mirrors the RMI
 * service methods but does not declare RemoteExceptions. In the latter case,
 * RemoteExceptions thrown by the RMI stub will automatically get converted to
 * Spring's unchecked RemoteAccessException.
 *
 * <p>The JNDI environment can be specified as "jndiEnvironment" property,
 * or be configured in a {@code jndi.properties} file or as system properties.
 * For example:
 *
 * <pre class="code">&lt;property name="jndiEnvironment"&gt;
 * 	 &lt;props>
 *		 &lt;prop key="java.naming.factory.initial"&gt;com.sun.jndi.cosnaming.CNCtxFactory&lt;/prop&gt;
 *		 &lt;prop key="java.naming.provider.url"&gt;iiop://localhost:1050&lt;/prop&gt;
 *	 &lt;/props&gt;
 * &lt;/property&gt;</pre>
 *
 * <p>
 * 用于从JNDI访问RMI服务的{@link orgaopallianceinterceptMethodInterceptor}通常用于RMI-IIOP(CORBA),但也可用于EJB主对象(例如,有状态
 * 会话Bean home)与普通JNDI查找相反,此访问器也通过PortableRemoteObject执行缩小。
 * 
 *  对于常规RMI服务,此调用者通常与RMI服务接口一起使用。
 * 或者,此调用程序还可以使用匹配的非RMI业务界面(即镜像RMI服务方法但不包含RMI服务方法的接口)代理远程RMI服务声明RemoteExceptions在后一种情况下,RMI存根抛出的RemoteEx
 * ceptions将自动转换为Spring未检查的RemoteAccessException。
 *  对于常规RMI服务,此调用者通常与RMI服务接口一起使用。
 * 
 * <p> JNDI环境可以指定为"jndiEnvironment"属性,或者在{@code jndiproperties}文件或系统属性中进行配置例如：
 * 
 *  <pre class ="code">&lt; property name ="jndiEnvironment"&gt;
 * &lt;props>
 *  &lt; prop key ="javanamingfactoryinitial"&gt; comsunjndicosnamingCNCtxFactory&lt; / prop&gt; &lt; pr
 * op key ="javanamingproviderurl"&gt; iiop：// localhost：1050&lt; / prop&gt; &LT; /道具&GT; &LT; /性&gt; </PRE>
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setJndiTemplate
 * @see #setJndiEnvironment
 * @see #setJndiName
 * @see JndiRmiServiceExporter
 * @see JndiRmiProxyFactoryBean
 * @see org.springframework.remoting.RemoteAccessException
 * @see java.rmi.RemoteException
 * @see java.rmi.Remote
 * @see javax.rmi.PortableRemoteObject#narrow
 */
public class JndiRmiClientInterceptor extends JndiObjectLocator implements MethodInterceptor, InitializingBean {

	private Class<?> serviceInterface;

	private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();

	private boolean lookupStubOnStartup = true;

	private boolean cacheStub = true;

	private boolean refreshStubOnConnectFailure = false;

	private boolean exposeAccessContext = false;

	private Object cachedStub;

	private final Object stubMonitor = new Object();


	/**
	 * Set the interface of the service to access.
	 * The interface must be suitable for the particular service and remoting tool.
	 * <p>Typically required to be able to create a suitable service proxy,
	 * but can also be optional if the lookup returns a typed stub.
	 * <p>
	 *  将服务的接口设置为访问该接口必须适合于特定的服务和远程处理工具<p>通常需要能够创建合适的服务代理,但如果查找返回一个类型的存根,也可以是可选的
	 * 
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
	 *  返回服务界面进行访问
	 * 
	 */
	public Class<?> getServiceInterface() {
		return this.serviceInterface;
	}

	/**
	 * Set the RemoteInvocationFactory to use for this accessor.
	 * Default is a {@link DefaultRemoteInvocationFactory}.
	 * <p>A custom invocation factory can add further context information
	 * to the invocation, for example user credentials.
	 * <p>
	 * 将RemoteInvocationFactory设置为用于此访问者默认为{@link DefaultRemoteInvocationFactory} <p>自定义调用工厂可以向调用添加其他上下文信息,例
	 * 如用户凭据。
	 * 
	 */
	public void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
		this.remoteInvocationFactory = remoteInvocationFactory;
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
	 * Set whether to look up the RMI stub on startup. Default is "true".
	 * <p>Can be turned off to allow for late start of the RMI server.
	 * In this case, the RMI stub will be fetched on first access.
	 * <p>
	 *  设置是否在启动时查找RMI存根默认值为"true"<p>可以关闭以允许晚期启动RMI服务器在这种情况下,RMI存根将在第一次访问时获取
	 * 
	 * 
	 * @see #setCacheStub
	 */
	public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
		this.lookupStubOnStartup = lookupStubOnStartup;
	}

	/**
	 * Set whether to cache the RMI stub once it has been located.
	 * Default is "true".
	 * <p>Can be turned off to allow for hot restart of the RMI server.
	 * In this case, the RMI stub will be fetched for each invocation.
	 * <p>
	 *  设置是否在定位后缓存RMI存根默认为"true"<p>可以关闭以允许RMI服务器的热重启在这种情况下,将为每个调用获取RMI存根
	 * 
	 * 
	 * @see #setLookupStubOnStartup
	 */
	public void setCacheStub(boolean cacheStub) {
		this.cacheStub = cacheStub;
	}

	/**
	 * Set whether to refresh the RMI stub on connect failure.
	 * Default is "false".
	 * <p>Can be turned on to allow for hot restart of the RMI server.
	 * If a cached RMI stub throws an RMI exception that indicates a
	 * remote connect failure, a fresh proxy will be fetched and the
	 * invocation will be retried.
	 * <p>
	 * 设置是否在连接故障时刷新RMI存根默认为"false"可以打开以允许RMI服务器的热重启如果缓存的RMI存根引发指示远程连接失败的RMI异常,则新的代理将被抓取并且将重试该调用
	 * 
	 * 
	 * @see java.rmi.ConnectException
	 * @see java.rmi.ConnectIOException
	 * @see java.rmi.NoSuchObjectException
	 */
	public void setRefreshStubOnConnectFailure(boolean refreshStubOnConnectFailure) {
		this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
	}

	/**
	 * Set whether to expose the JNDI environment context for all access to the target
	 * RMI stub, i.e. for all method invocations on the exposed object reference.
	 * <p>Default is "false", i.e. to only expose the JNDI context for object lookup.
	 * Switch this flag to "true" in order to expose the JNDI environment (including
	 * the authorization context) for each RMI invocation, as needed by WebLogic
	 * for RMI stubs with authorization requirements.
	 * <p>
	 *  设置是否暴露JNDI环境上下文,以便对目标RMI存根进行所有访问,即针对暴露的对象引用上的所有方法调用。
	 * <p>默认值为"false",即仅公开对象查找的JNDI上下文将此标志切换到为了为每个RMI调用公开JNDI环境(包括授权上下文),根据WebLogic for RMI存根的需求,具有授权要求"true
	 * "。
	 *  设置是否暴露JNDI环境上下文,以便对目标RMI存根进行所有访问,即针对暴露的对象引用上的所有方法调用。
	 * 
	 */
	public void setExposeAccessContext(boolean exposeAccessContext) {
		this.exposeAccessContext = exposeAccessContext;
	}


	@Override
	public void afterPropertiesSet() throws NamingException {
		super.afterPropertiesSet();
		prepare();
	}

	/**
	 * Fetches the RMI stub on startup, if necessary.
	 * <p>
	 *  如果需要,在启动时获取RMI存根
	 * 
	 * 
	 * @throws RemoteLookupFailureException if RMI stub creation failed
	 * @see #setLookupStubOnStartup
	 * @see #lookupStub
	 */
	public void prepare() throws RemoteLookupFailureException {
		// Cache RMI stub on initialization?
		if (this.lookupStubOnStartup) {
			Object remoteObj = lookupStub();
			if (logger.isDebugEnabled()) {
				if (remoteObj instanceof RmiInvocationHandler) {
					logger.debug("JNDI RMI object [" + getJndiName() + "] is an RMI invoker");
				}
				else if (getServiceInterface() != null) {
					boolean isImpl = getServiceInterface().isInstance(remoteObj);
					logger.debug("Using service interface [" + getServiceInterface().getName() +
							"] for JNDI RMI object [" + getJndiName() + "] - " +
							(!isImpl ? "not " : "") + "directly implemented");
				}
			}
			if (this.cacheStub) {
				this.cachedStub = remoteObj;
			}
		}
	}

	/**
	 * Create the RMI stub, typically by looking it up.
	 * <p>Called on interceptor initialization if "cacheStub" is "true";
	 * else called for each invocation by {@link #getStub()}.
	 * <p>The default implementation retrieves the service from the
	 * JNDI environment. This can be overridden in subclasses.
	 * <p>
	 * 创建RMI存根,通常通过查找它<p>如果"cacheStub"为"true",则调用拦截器初始化;另外通过{@link #getStub()}调用每个调用。
	 * <p>默认实现从JNDI环境检索服务这可以在子类中被覆盖。
	 * 
	 * 
	 * @return the RMI stub to store in this interceptor
	 * @throws RemoteLookupFailureException if RMI stub creation failed
	 * @see #setCacheStub
	 * @see #lookup
	 */
	protected Object lookupStub() throws RemoteLookupFailureException {
		try {
			Object stub = lookup();
			if (getServiceInterface() != null && !(stub instanceof RmiInvocationHandler)) {
				try {
					stub = PortableRemoteObject.narrow(stub, getServiceInterface());
				}
				catch (ClassCastException ex) {
					throw new RemoteLookupFailureException(
							"Could not narrow RMI stub to service interface [" + getServiceInterface().getName() + "]", ex);
				}
			}
			return stub;
		}
		catch (NamingException ex) {
			throw new RemoteLookupFailureException("JNDI lookup for RMI service [" + getJndiName() + "] failed", ex);
		}
	}

	/**
	 * Return the RMI stub to use. Called for each invocation.
	 * <p>The default implementation returns the stub created on initialization,
	 * if any. Else, it invokes {@link #lookupStub} to get a new stub for
	 * each invocation. This can be overridden in subclasses, for example in
	 * order to cache a stub for a given amount of time before recreating it,
	 * or to test the stub whether it is still alive.
	 * <p>
	 *  返回RMI存根以使用每次调用调用<p>默认实现返回在初始化时创建的存根(如果有),它会调用{@link #lookupStub}以获取每个调用的新存根这可以在子类中被覆盖,例如,为了在重新创建存根之前
	 * 将存根缓存一段给定的时间,或者测试存根是否仍然存在。
	 * 
	 * 
	 * @return the RMI stub to use for an invocation
	 * @throws NamingException if stub creation failed
	 * @throws RemoteLookupFailureException if RMI stub creation failed
	 */
	protected Object getStub() throws NamingException, RemoteLookupFailureException {
		if (!this.cacheStub || (this.lookupStubOnStartup && !this.refreshStubOnConnectFailure)) {
			return (this.cachedStub != null ? this.cachedStub : lookupStub());
		}
		else {
			synchronized (this.stubMonitor) {
				if (this.cachedStub == null) {
					this.cachedStub = lookupStub();
				}
				return this.cachedStub;
			}
		}
	}


	/**
	 * Fetches an RMI stub and delegates to {@link #doInvoke}.
	 * If configured to refresh on connect failure, it will call
	 * {@link #refreshAndRetry} on corresponding RMI exceptions.
	 * <p>
	 * 获取RMI存根并委托{@link #doInvoke}如果配置为在连接失败时进行刷新,则会在相应的RMI异常中调用{@link #refreshAndRetry}
	 * 
	 * 
	 * @see #getStub
	 * @see #doInvoke
	 * @see #refreshAndRetry
	 * @see java.rmi.ConnectException
	 * @see java.rmi.ConnectIOException
	 * @see java.rmi.NoSuchObjectException
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object stub;
		try {
			stub = getStub();
		}
		catch (NamingException ex) {
			throw new RemoteLookupFailureException("JNDI lookup for RMI service [" + getJndiName() + "] failed", ex);
		}

		Context ctx = (this.exposeAccessContext ? getJndiTemplate().getContext() : null);
		try {
			return doInvoke(invocation, stub);
		}
		catch (RemoteConnectFailureException ex) {
			return handleRemoteConnectFailure(invocation, ex);
		}
		catch (RemoteException ex) {
			if (isConnectFailure(ex)) {
				return handleRemoteConnectFailure(invocation, ex);
			}
			else {
				throw ex;
			}
		}
		catch (SystemException ex) {
			if (isConnectFailure(ex)) {
				return handleRemoteConnectFailure(invocation, ex);
			}
			else {
				throw ex;
			}
		}
		finally {
			getJndiTemplate().releaseContext(ctx);
		}
	}

	/**
	 * Determine whether the given RMI exception indicates a connect failure.
	 * <p>The default implementation delegates to
	 * {@link RmiClientInterceptorUtils#isConnectFailure}.
	 * <p>
	 *  确定给定的RMI异常是否指示连接失败<p>默认实现委托给{@link RmiClientInterceptorUtils#isConnectFailure}
	 * 
	 * 
	 * @param ex the RMI exception to check
	 * @return whether the exception should be treated as connect failure
	 */
	protected boolean isConnectFailure(RemoteException ex) {
		return RmiClientInterceptorUtils.isConnectFailure(ex);
	}

	/**
	 * Determine whether the given CORBA exception indicates a connect failure.
	 * <p>The default implementation checks for CORBA's
	 * {@link org.omg.CORBA.OBJECT_NOT_EXIST} exception.
	 * <p>
	 *  确定给定的CORBA异常是否指示连接失败<p>默认实现检查CORBA的{@link orgomgCORBAOBJECT_NOT_EXIST}异常
	 * 
	 * 
	 * @param ex the RMI exception to check
	 * @return whether the exception should be treated as connect failure
	 */
	protected boolean isConnectFailure(SystemException ex) {
		return (ex instanceof OBJECT_NOT_EXIST);
	}

	/**
	 * Refresh the stub and retry the remote invocation if necessary.
	 * <p>If not configured to refresh on connect failure, this method
	 * simply rethrows the original exception.
	 * <p>
	 *  刷新存根并重试远程调用(如有必要)<p>如果没有配置为在连接失败时进行刷新,则此方法只是重新抛出原始异常
	 * 
	 * 
	 * @param invocation the invocation that failed
	 * @param ex the exception raised on remote invocation
	 * @return the result value of the new invocation, if succeeded
	 * @throws Throwable an exception raised by the new invocation, if failed too.
	 */
	private Object handleRemoteConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
		if (this.refreshStubOnConnectFailure) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not connect to RMI service [" + getJndiName() + "] - retrying", ex);
			}
			else if (logger.isWarnEnabled()) {
				logger.warn("Could not connect to RMI service [" + getJndiName() + "] - retrying");
			}
			return refreshAndRetry(invocation);
		}
		else {
			throw ex;
		}
	}

	/**
	 * Refresh the RMI stub and retry the given invocation.
	 * Called by invoke on connect failure.
	 * <p>
	 *  刷新RMI存根并重试给定的调用在调用连接失败时调用
	 * 
	 * 
	 * @param invocation the AOP method invocation
	 * @return the invocation result, if any
	 * @throws Throwable in case of invocation failure
	 * @see #invoke
	 */
	protected Object refreshAndRetry(MethodInvocation invocation) throws Throwable {
		Object freshStub;
		synchronized (this.stubMonitor) {
			this.cachedStub = null;
			freshStub = lookupStub();
			if (this.cacheStub) {
				this.cachedStub = freshStub;
			}
		}
		return doInvoke(invocation, freshStub);
	}


	/**
	 * Perform the given invocation on the given RMI stub.
	 * <p>
	 * 在给定的RMI存根上执行给定的调用
	 * 
	 * 
	 * @param invocation the AOP method invocation
	 * @param stub the RMI stub to invoke
	 * @return the invocation result, if any
	 * @throws Throwable in case of invocation failure
	 */
	protected Object doInvoke(MethodInvocation invocation, Object stub) throws Throwable {
		if (stub instanceof RmiInvocationHandler) {
			// RMI invoker
			try {
				return doInvoke(invocation, (RmiInvocationHandler) stub);
			}
			catch (RemoteException ex) {
				throw convertRmiAccessException(ex, invocation.getMethod());
			}
			catch (SystemException ex) {
				throw convertCorbaAccessException(ex, invocation.getMethod());
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
			catch (Throwable ex) {
				throw new RemoteInvocationFailureException("Invocation of method [" + invocation.getMethod() +
						"] failed in RMI service [" + getJndiName() + "]", ex);
			}
		}
		else {
			// traditional RMI stub
			try {
				return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, stub);
			}
			catch (InvocationTargetException ex) {
				Throwable targetEx = ex.getTargetException();
				if (targetEx instanceof RemoteException) {
					throw convertRmiAccessException((RemoteException) targetEx, invocation.getMethod());
				}
				else if (targetEx instanceof SystemException) {
					throw convertCorbaAccessException((SystemException) targetEx, invocation.getMethod());
				}
				else {
					throw targetEx;
				}
			}
		}
	}

	/**
	 * Apply the given AOP method invocation to the given {@link RmiInvocationHandler}.
	 * <p>The default implementation delegates to {@link #createRemoteInvocation}.
	 * <p>
	 *  将给定的AOP方法调用应用于给定的{@link RmiInvocationHandler} <p>默认实现委托给{@link #createRemoteInvocation}
	 * 
	 * 
	 * @param methodInvocation the current AOP method invocation
	 * @param invocationHandler the RmiInvocationHandler to apply the invocation to
	 * @return the invocation result
	 * @throws RemoteException in case of communication errors
	 * @throws NoSuchMethodException if the method name could not be resolved
	 * @throws IllegalAccessException if the method could not be accessed
	 * @throws InvocationTargetException if the method invocation resulted in an exception
	 * @see org.springframework.remoting.support.RemoteInvocation
	 */
	protected Object doInvoke(MethodInvocation methodInvocation, RmiInvocationHandler invocationHandler)
			throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
			return "RMI invoker proxy for service URL [" + getJndiName() + "]";
		}

		return invocationHandler.invoke(createRemoteInvocation(methodInvocation));
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
	 *  为给定的AOP方法调用创建一个新的RemoteInvocation对象<p>默认实现委托给已配置的{@link #setRemoteInvocationFactory RemoteInvocationFactory}
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
	 * Convert the given RMI RemoteException that happened during remote access
	 * to Spring's RemoteAccessException if the method signature does not declare
	 * RemoteException. Else, return the original RemoteException.
	 * <p>
	 * 转换在远程访问Spring的RemoteAccessException期间发生的给定的RMI RemoteException,如果方法签名没有声明RemoteException Else,返回原始的Re
	 * moteException。
	 * 
	 * 
	 * @param method the invoked method
	 * @param ex the RemoteException that happened
	 * @return the exception to be thrown to the caller
	 */
	private Exception convertRmiAccessException(RemoteException ex, Method method) {
		return RmiClientInterceptorUtils.convertRmiAccessException(method, ex, isConnectFailure(ex), getJndiName());
	}

	/**
	 * Convert the given CORBA SystemException that happened during remote access
	 * to Spring's RemoteAccessException if the method signature does not declare
	 * RemoteException. Else, return the SystemException wrapped in a RemoteException.
	 * <p>
	 *  转换在远程访问Spring的RemoteAccessException期间发生的给定的CORBA SystemException,如果方法签名没有声明RemoteException Else,则返回包
	 * 含在RemoteException中的SystemException。
	 * 
	 * @param method the invoked method
	 * @param ex the RemoteException that happened
	 * @return the exception to be thrown to the caller
	 */
	private Exception convertCorbaAccessException(SystemException ex, Method method) {
		if (ReflectionUtils.declaresException(method, RemoteException.class)) {
			// A traditional RMI service: wrap CORBA exceptions in standard RemoteExceptions.
			return new RemoteException("Failed to access CORBA service [" + getJndiName() + "]", ex);
		}
		else {
			if (isConnectFailure(ex)) {
				return new RemoteConnectFailureException("Could not connect to CORBA service [" + getJndiName() + "]", ex);
			}
			else {
				return new RemoteAccessException("Could not access CORBA service [" + getJndiName() + "]", ex);
			}
		}
	}

}
