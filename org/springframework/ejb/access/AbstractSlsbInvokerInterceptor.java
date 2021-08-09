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
import javax.naming.Context;
import javax.naming.NamingException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.jndi.JndiObjectLocator;

/**
 * Base class for AOP interceptors invoking local or remote Stateless Session Beans.
 * Designed for EJB 2.x, but works for EJB 3 Session Beans as well.
 *
 * <p>Such an interceptor must be the last interceptor in the advice chain.
 * In this case, there is no direct target object: The call is handled in a
 * special way, getting executed on an EJB instance retrieved via an EJB home.
 *
 * <p>
 *  调用本地或远程无状态会话Bean的AOP拦截器的基类专为EJB 2x而设计,但适用于EJB 3会话Bean
 * 
 * <p>这样一个拦截器必须是建立链中的最后一个拦截器在这种情况下,没有直接的目标对象：调用以特殊的方式处理,在通过EJB主机检索的EJB实例上执行
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class AbstractSlsbInvokerInterceptor extends JndiObjectLocator
		implements MethodInterceptor {

	private boolean lookupHomeOnStartup = true;

	private boolean cacheHome = true;

	private boolean exposeAccessContext = false;

	/**
	 * The EJB's home object, potentially cached.
	 * The type must be Object as it could be either EJBHome or EJBLocalHome.
	 * <p>
	 *  EJB的home对象,可能缓存该类型必须是Object,因为它可以是EJBHome或EJBLocalHome
	 * 
	 */
	private Object cachedHome;

	/**
	 * The no-arg create() method required on EJB homes, potentially cached.
	 * <p>
	 *  EJB家庭需要的no-arg create()方法,可能被缓存
	 * 
	 */
	private Method createMethod;

	private final Object homeMonitor = new Object();


	/**
	 * Set whether to look up the EJB home object on startup.
	 * Default is "true".
	 * <p>Can be turned off to allow for late start of the EJB server.
	 * In this case, the EJB home object will be fetched on first access.
	 * <p>
	 *  设置是否在启动时查找EJB主对象默认为"true"<p>可以关闭以允许EJB服务器的后期启动在这种情况下,将首先访问EJB home对象
	 * 
	 * 
	 * @see #setCacheHome
	 */
	public void setLookupHomeOnStartup(boolean lookupHomeOnStartup) {
		this.lookupHomeOnStartup = lookupHomeOnStartup;
	}

	/**
	 * Set whether to cache the EJB home object once it has been located.
	 * Default is "true".
	 * <p>Can be turned off to allow for hot restart of the EJB server.
	 * In this case, the EJB home object will be fetched for each invocation.
	 * <p>
	 * 设置是否在找到EJB主目标后缓存默认值为"true"<p>可以关闭以允许EJB服务器的热重启在这种情况下,将为每个调用获取EJB home对象
	 * 
	 * 
	 * @see #setLookupHomeOnStartup
	 */
	public void setCacheHome(boolean cacheHome) {
		this.cacheHome = cacheHome;
	}

	/**
	 * Set whether to expose the JNDI environment context for all access to the target
	 * EJB, i.e. for all method invocations on the exposed object reference.
	 * <p>Default is "false", i.e. to only expose the JNDI context for object lookup.
	 * Switch this flag to "true" in order to expose the JNDI environment (including
	 * the authorization context) for each EJB invocation, as needed by WebLogic
	 * for EJBs with authorization requirements.
	 * <p>
	 *  设置是否暴露JNDI环境上下文,以便对目标EJB进行所有访问,即针对所暴露的对象引用的所有方法调用。
	 * <p>默认值为"false",即仅暴露对象查找的JNDI上下文将此标志切换到"为了公开每个EJB调用的JNDI环境(包括授权上下文),根据具有授权要求的WebLogic for EJB的需要。
	 * 
	 */
	public void setExposeAccessContext(boolean exposeAccessContext) {
		this.exposeAccessContext = exposeAccessContext;
	}


	/**
	 * Fetches EJB home on startup, if necessary.
	 * <p>
	 *  如果需要,在启动时将EJB提取回家
	 * 
	 * 
	 * @see #setLookupHomeOnStartup
	 * @see #refreshHome
	 */
	@Override
	public void afterPropertiesSet() throws NamingException {
		super.afterPropertiesSet();
		if (this.lookupHomeOnStartup) {
			// look up EJB home and create method
			refreshHome();
		}
	}

	/**
	 * Refresh the cached home object, if applicable.
	 * Also caches the create method on the home object.
	 * <p>
	 *  刷新缓存的home对象(如果适用)还可以在home对象上缓存create方法
	 * 
	 * 
	 * @throws NamingException if thrown by the JNDI lookup
	 * @see #lookup
	 * @see #getCreateMethod
	 */
	protected void refreshHome() throws NamingException {
		synchronized (this.homeMonitor) {
			Object home = lookup();
			if (this.cacheHome) {
				this.cachedHome = home;
				this.createMethod = getCreateMethod(home);
			}
		}
	}

	/**
	 * Determine the create method of the given EJB home object.
	 * <p>
	 * 确定给定的EJB home对象的create方法
	 * 
	 * 
	 * @param home the EJB home object
	 * @return the create method
	 * @throws EjbAccessException if the method couldn't be retrieved
	 */
	protected Method getCreateMethod(Object home) throws EjbAccessException {
		try {
			// Cache the EJB create() method that must be declared on the home interface.
			return home.getClass().getMethod("create", (Class[]) null);
		}
		catch (NoSuchMethodException ex) {
			throw new EjbAccessException("EJB home [" + home + "] has no no-arg create() method");
		}
	}

	/**
	 * Return the EJB home object to use. Called for each invocation.
	 * <p>Default implementation returns the home created on initialization,
	 * if any; else, it invokes lookup to get a new proxy for each invocation.
	 * <p>Can be overridden in subclasses, for example to cache a home object
	 * for a given amount of time before recreating it, or to test the home
	 * object whether it is still alive.
	 * <p>
	 *  返回EJB主对象使用每次调用调用<p>默认实现返回初始化时创建的主页(如果有);否则,它调用查找以获取每个调用的新代理<p>可以在子类中覆盖,例如,在重新创建之前,将home对象缓存一段给定的时间,或
	 * 者测试home对象是否仍然存在。
	 * 
	 * 
	 * @return the EJB home object to use for an invocation
	 * @throws NamingException if proxy creation failed
	 * @see #lookup
	 * @see #getCreateMethod
	 */
	protected Object getHome() throws NamingException {
		if (!this.cacheHome || (this.lookupHomeOnStartup && !isHomeRefreshable())) {
			return (this.cachedHome != null ? this.cachedHome : lookup());
		}
		else {
			synchronized (this.homeMonitor) {
				if (this.cachedHome == null) {
					this.cachedHome = lookup();
					this.createMethod = getCreateMethod(this.cachedHome);
				}
				return this.cachedHome;
			}
		}
	}

	/**
	 * Return whether the cached EJB home object is potentially
	 * subject to on-demand refreshing. Default is "false".
	 * <p>
	 *  返回缓存的EJB home对象是否可能受到按需刷新默认为"false"
	 * 
	 */
	protected boolean isHomeRefreshable() {
		return false;
	}


	/**
	 * Prepares the thread context if necessar, and delegates to
	 * {@link #invokeInContext}.
	 * <p>
	 *  如果需要,准备线程上下文,并委托给{@link #invokeInContext}
	 * 
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Context ctx = (this.exposeAccessContext ? getJndiTemplate().getContext() : null);
		try {
			return invokeInContext(invocation);
		}
		finally {
			getJndiTemplate().releaseContext(ctx);
		}
	}

	/**
	 * Perform the given invocation on the current EJB home,
	 * within the thread context being prepared accordingly.
	 * Template method to be implemented by subclasses.
	 * <p>
	 *  在当前EJB主机上执行给定的调用,在正在准备的线程上下文中由子类实现的Template方法
	 * 
	 * 
	 * @param invocation the AOP method invocation
	 * @return the invocation result, if any
	 * @throws Throwable in case of invocation failure
	 */
	protected abstract Object invokeInContext(MethodInvocation invocation) throws Throwable;


	/**
	 * Invokes the {@code create()} method on the cached EJB home object.
	 * <p>
	 * 在缓存的EJB主对象上调用{@code create()}方法
	 * 
	 * @return a new EJBObject or EJBLocalObject
	 * @throws NamingException if thrown by JNDI
	 * @throws InvocationTargetException if thrown by the create method
	 */
	protected Object create() throws NamingException, InvocationTargetException {
		try {
			Object home = getHome();
			Method createMethodToUse = this.createMethod;
			if (createMethodToUse == null) {
				createMethodToUse = getCreateMethod(home);
			}
			if (createMethodToUse == null) {
				return home;
			}
			// Invoke create() method on EJB home object.
			return createMethodToUse.invoke(home, (Object[]) null);
		}
		catch (IllegalAccessException ex) {
			throw new EjbAccessException("Could not access EJB home create() method", ex);
		}
	}

}
