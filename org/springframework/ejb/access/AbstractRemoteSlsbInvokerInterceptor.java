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

package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;

/**
 * Base class for interceptors proxying remote Stateless Session Beans.
 * Designed for EJB 2.x, but works for EJB 3 Session Beans as well.
 *
 * <p>Such an interceptor must be the last interceptor in the advice chain.
 * In this case, there is no target object.
 *
 * <p>
 *  拦截器的基类代理远程无状态会话Bean专为EJB 2x设计,但适用于EJB 3会话Bean
 * 
 * <p>这样的拦截器必须是建议链中的最后一个拦截器在这种情况下,没有目标对象
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class AbstractRemoteSlsbInvokerInterceptor extends AbstractSlsbInvokerInterceptor {

	private Class<?> homeInterface;

	private boolean refreshHomeOnConnectFailure = false;

	private volatile boolean homeAsComponent = false;



	/**
	 * Set a home interface that this invoker will narrow to before performing
	 * the parameterless SLSB {@code create()} call that returns the actual
	 * SLSB proxy.
	 * <p>Default is none, which will work on all J2EE servers that are not based
	 * on CORBA. A plain {@code javax.ejb.EJBHome} interface is known to be
	 * sufficient to make a WebSphere 5.0 Remote SLSB work. On other servers,
	 * the specific home interface for the target SLSB might be necessary.
	 * <p>
	 *  在执行返回实际SLSB代理的无参数SLSB {@code create()}调用之前,设置此调用者将缩小到的归属接口。
	 * 缺省值为none,这将适用于不基于CORBA A的所有J2EE服务器已知纯粹的{@code javaxejbEJBHome}接口足以使WebSphere 50 Remote SLSB工作在其他服务器上,
	 * 目标SLSB的特定home接口可能是必需的。
	 *  在执行返回实际SLSB代理的无参数SLSB {@code create()}调用之前,设置此调用者将缩小到的归属接口。
	 * 
	 */
	public void setHomeInterface(Class<?> homeInterface) {
		if (homeInterface != null && !homeInterface.isInterface()) {
			throw new IllegalArgumentException(
					"Home interface class [" + homeInterface.getClass() + "] is not an interface");
		}
		this.homeInterface = homeInterface;
	}

	/**
	 * Set whether to refresh the EJB home on connect failure.
	 * Default is "false".
	 * <p>Can be turned on to allow for hot restart of the EJB server.
	 * If a cached EJB home throws an RMI exception that indicates a
	 * remote connect failure, a fresh home will be fetched and the
	 * invocation will be retried.
	 * <p>
	 * 设置是否在连接失败时刷新EJB home默认为"false"可以打开以允许EJB服务器的热重启如果缓存的EJB主机引发指示远程连接失败的RMI异常,那么一个新的家将被抓取并且将重试该调用
	 * 
	 * 
	 * @see java.rmi.ConnectException
	 * @see java.rmi.ConnectIOException
	 * @see java.rmi.NoSuchObjectException
	 */
	public void setRefreshHomeOnConnectFailure(boolean refreshHomeOnConnectFailure) {
		this.refreshHomeOnConnectFailure = refreshHomeOnConnectFailure;
	}

	@Override
	protected boolean isHomeRefreshable() {
		return this.refreshHomeOnConnectFailure;
	}


	/**
	 * This overridden lookup implementation performs a narrow operation
	 * after the JNDI lookup, provided that a home interface is specified.
	 * <p>
	 *  如果指定了一个home接口,这个被覆盖的查找实现在JNDI查找之后执行一个窄操作
	 * 
	 * 
	 * @see #setHomeInterface
	 * @see javax.rmi.PortableRemoteObject#narrow
	 */
	@Override
	protected Object lookup() throws NamingException {
		Object homeObject = super.lookup();
		if (this.homeInterface != null) {
			try {
				homeObject = PortableRemoteObject.narrow(homeObject, this.homeInterface);
			}
			catch (ClassCastException ex) {
				throw new RemoteLookupFailureException(
						"Could not narrow EJB home stub to home interface [" + this.homeInterface.getName() + "]", ex);
			}
		}
		return homeObject;
	}

	/**
	 * Check for EJB3-style home object that serves as EJB component directly.
	 * <p>
	 *  检查用作EJB组件的EJB3样式的home对象
	 * 
	 */
	@Override
	protected Method getCreateMethod(Object home) throws EjbAccessException {
		if (this.homeAsComponent) {
			return null;
		}
		if (!(home instanceof EJBHome)) {
			// An EJB3 Session Bean...
			this.homeAsComponent = true;
			return null;
		}
		return super.getCreateMethod(home);
	}


	/**
	 * Fetches an EJB home object and delegates to {@code doInvoke}.
	 * <p>If configured to refresh on connect failure, it will call
	 * {@link #refreshAndRetry} on corresponding RMI exceptions.
	 * <p>
	 *  获取EJB主对象并委托{@code doInvoke} <p>如果配置为在连接失败时进行刷新,则会在相应的RMI异常中调用{@link #refreshAndRetry}
	 * 
	 * 
	 * @see #getHome
	 * @see #doInvoke
	 * @see #refreshAndRetry
	 */
	@Override
	public Object invokeInContext(MethodInvocation invocation) throws Throwable {
		try {
			return doInvoke(invocation);
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
	}

	/**
	 * Determine whether the given RMI exception indicates a connect failure.
	 * <p>The default implementation delegates to RmiClientInterceptorUtils.
	 * <p>
	 * 确定给定的RMI异常是否指示连接失败<p>默认实现委托给RmiClientInterceptorUtils
	 * 
	 * 
	 * @param ex the RMI exception to check
	 * @return whether the exception should be treated as connect failure
	 * @see org.springframework.remoting.rmi.RmiClientInterceptorUtils#isConnectFailure
	 */
	protected boolean isConnectFailure(RemoteException ex) {
		return RmiClientInterceptorUtils.isConnectFailure(ex);
	}

	private Object handleRemoteConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
		if (this.refreshHomeOnConnectFailure) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not connect to remote EJB [" + getJndiName() + "] - retrying", ex);
			}
			else if (logger.isWarnEnabled()) {
				logger.warn("Could not connect to remote EJB [" + getJndiName() + "] - retrying");
			}
			return refreshAndRetry(invocation);
		}
		else {
			throw ex;
		}
	}

	/**
	 * Refresh the EJB home object and retry the given invocation.
	 * Called by invoke on connect failure.
	 * <p>
	 *  刷新EJB主对象并重试给定的调用通过调用在连接失败时调用
	 * 
	 * 
	 * @param invocation the AOP method invocation
	 * @return the invocation result, if any
	 * @throws Throwable in case of invocation failure
	 * @see #invoke
	 */
	protected Object refreshAndRetry(MethodInvocation invocation) throws Throwable {
		try {
			refreshHome();
		}
		catch (NamingException ex) {
			throw new RemoteLookupFailureException("Failed to locate remote EJB [" + getJndiName() + "]", ex);
		}
		return doInvoke(invocation);
	}


	/**
	 * Perform the given invocation on the current EJB home.
	 * Template method to be implemented by subclasses.
	 * <p>
	 *  对当前EJB主页模板方法执行给定的调用以由子类实现
	 * 
	 * 
	 * @param invocation the AOP method invocation
	 * @return the invocation result, if any
	 * @throws Throwable in case of invocation failure
	 * @see #getHome
	 * @see #newSessionBeanInstance
	 */
	protected abstract Object doInvoke(MethodInvocation invocation) throws Throwable;


	/**
	 * Return a new instance of the stateless session bean.
	 * To be invoked by concrete remote SLSB invoker subclasses.
	 * <p>Can be overridden to change the algorithm.
	 * <p>
	 *  返回无状态会话bean的新实例由具体的远程SLSB调用器子类调用<p>可以覆盖以更改算法
	 * 
	 * 
	 * @throws NamingException if thrown by JNDI
	 * @throws InvocationTargetException if thrown by the create method
	 * @see #create
	 */
	protected Object newSessionBeanInstance() throws NamingException, InvocationTargetException {
		if (logger.isDebugEnabled()) {
			logger.debug("Trying to create reference to remote EJB");
		}
		Object ejbInstance = create();
		if (logger.isDebugEnabled()) {
			logger.debug("Obtained reference to remote EJB: " + ejbInstance);
		}
		return ejbInstance;
	}

	/**
	 * Remove the given EJB instance.
	 * To be invoked by concrete remote SLSB invoker subclasses.
	 * <p>
	 *  删除给定的EJB实例由具体的远程SLSB调用器子类调用
	 * 
	 * @param ejb the EJB instance to remove
	 * @see javax.ejb.EJBObject#remove
	 */
	protected void removeSessionBeanInstance(EJBObject ejb) {
		if (ejb != null && !this.homeAsComponent) {
			try {
				ejb.remove();
			}
			catch (Throwable ex) {
				logger.warn("Could not invoke 'remove' on remote EJB proxy", ex);
			}
		}
	}

}
