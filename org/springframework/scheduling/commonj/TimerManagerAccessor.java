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

package org.springframework.scheduling.commonj;

import javax.naming.NamingException;

import commonj.timers.TimerManager;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.jndi.JndiLocatorSupport;

/**
 * Base class for classes that are accessing a CommonJ {@link commonj.timers.TimerManager}
 * Defines common configuration settings and common lifecycle handling.
 *
 * <p>
 *  访问CommonJ的类的基类{@link commonjtimersTimerManager}定义常见的配置设置和常用的生命周期处理
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see commonj.timers.TimerManager
 */
public abstract class TimerManagerAccessor extends JndiLocatorSupport
		implements InitializingBean, DisposableBean, Lifecycle {

	private TimerManager timerManager;

	private String timerManagerName;

	private boolean shared = false;


	/**
	 * Specify the CommonJ TimerManager to delegate to.
	 * <p>Note that the given TimerManager's lifecycle will be managed
	 * by this FactoryBean.
	 * <p>Alternatively (and typically), you can specify the JNDI name
	 * of the target TimerManager.
	 * <p>
	 * 指定CommonJ TimerManager委托给<p>请注意,给定的TimerManager的生命周期将由此FactoryBean <p>替代(通常)进行管理,您可以指定目标TimerManager的
	 * JNDI名称。
	 * 
	 * 
	 * @see #setTimerManagerName
	 */
	public void setTimerManager(TimerManager timerManager) {
		this.timerManager = timerManager;
	}

	/**
	 * Set the JNDI name of the CommonJ TimerManager.
	 * <p>This can either be a fully qualified JNDI name, or the JNDI name relative
	 * to the current environment naming context if "resourceRef" is set to "true".
	 * <p>
	 *  设置CommonJ TimerManager的JNDI名称<p>如果"resourceRef"设置为"true",则可以将其设置为完全限定的JNDI名称或相对于当前环境命名上下文的JNDI名称
	 * 
	 * 
	 * @see #setTimerManager
	 * @see #setResourceRef
	 */
	public void setTimerManagerName(String timerManagerName) {
		this.timerManagerName = timerManagerName;
	}

	/**
	 * Specify whether the TimerManager obtained by this FactoryBean
	 * is a shared instance ("true") or an independent instance ("false").
	 * The lifecycle of the former is supposed to be managed by the application
	 * server, while the lifecycle of the latter is up to the application.
	 * <p>Default is "false", i.e. managing an independent TimerManager instance.
	 * This is what the CommonJ specification suggests that application servers
	 * are supposed to offer via JNDI lookups, typically declared as a
	 * {@code resource-ref} of type {@code commonj.timers.TimerManager}
	 * in {@code web.xml}, with {@code res-sharing-scope} set to 'Unshareable'.
	 * <p>Switch this flag to "true" if you are obtaining a shared TimerManager,
	 * typically through specifying the JNDI location of a TimerManager that
	 * has been explicitly declared as 'Shareable'. Note that WebLogic's
	 * cluster-aware Job Scheduler is a shared TimerManager too.
	 * <p>The sole difference between this FactoryBean being in shared or
	 * non-shared mode is that it will only attempt to suspend / resume / stop
	 * the underlying TimerManager in case of an independent (non-shared) instance.
	 * This only affects the {@link org.springframework.context.Lifecycle} support
	 * as well as application context shutdown.
	 * <p>
	 * 指定此FactoryBean获取的TimerManager是否为共享实例("true")或独立实例("false")。
	 * 前者的生命周期应由应用程序服务器管理,而后者的生命周期由应用程序<p>默认为"false",即管理独立的TimerManager实例这是CommonJ规范建议应用程序服务器应通过JNDI查找提供的,通常
	 * 声明为{@code resource-ref}类型为{@代码commonjtimersTimerManager}在{@code webxml}中,{@code res-sharing-scope}设置为
	 * "Unshareable"如果您正在获取共享的TimerManager,则将此标志切换为"true",通常通过指定已显式声明为"可共享"的TimerManager的JNDI位置。
	 * 指定此FactoryBean获取的TimerManager是否为共享实例("true")或独立实例("false")。
	 * 请注意,WebLogic的群集感知作业计划程序是共享的TimerManager < p>这个FactoryBean处于共享或非共享模式的唯一区别是,只有在独立(非共享)实例的情况下,它才会尝试挂起/恢复
	 * /停止底层TimerManager。
	 * 指定此FactoryBean获取的TimerManager是否为共享实例("true")或独立实例("false")。
	 * 这仅影响{@link orgspringframeworkcontextLifecycle }支持以及应用程序上下文关闭。
	 * 
	 * 
	 * @see #stop()
	 * @see #start()
	 * @see #destroy()
	 * @see commonj.timers.TimerManager
	 */
	public void setShared(boolean shared) {
		this.shared = shared;
	}


	@Override
	public void afterPropertiesSet() throws NamingException {
		if (this.timerManager == null) {
			if (this.timerManagerName == null) {
				throw new IllegalArgumentException("Either 'timerManager' or 'timerManagerName' must be specified");
			}
			this.timerManager = lookup(this.timerManagerName, TimerManager.class);
		}
	}

	protected final TimerManager getTimerManager() {
		return this.timerManager;
	}


	//---------------------------------------------------------------------
	// Implementation of Lifecycle interface
	//---------------------------------------------------------------------

	/**
	 * Resumes the underlying TimerManager (if not shared).
	 * <p>
	 * 恢复底层TimerManager(如果不共享)
	 * 
	 * 
	 * @see commonj.timers.TimerManager#resume()
	 */
	@Override
	public void start() {
		if (!this.shared) {
			this.timerManager.resume();
		}
	}

	/**
	 * Suspends the underlying TimerManager (if not shared).
	 * <p>
	 *  暂停底层TimerManager(如果不共享)
	 * 
	 * 
	 * @see commonj.timers.TimerManager#suspend()
	 */
	@Override
	public void stop() {
		if (!this.shared) {
			this.timerManager.suspend();
		}
	}

	/**
	 * Considers the underlying TimerManager as running if it is
	 * neither suspending nor stopping.
	 * <p>
	 *  如果它不是暂停还是停止,请将基础TimerManager视为运行
	 * 
	 * 
	 * @see commonj.timers.TimerManager#isSuspending()
	 * @see commonj.timers.TimerManager#isStopping()
	 */
	@Override
	public boolean isRunning() {
		return (!this.timerManager.isSuspending() && !this.timerManager.isStopping());
	}


	//---------------------------------------------------------------------
	// Implementation of DisposableBean interface
	//---------------------------------------------------------------------

	/**
	 * Stops the underlying TimerManager (if not shared).
	 * <p>
	 *  停止底层TimerManager(如果不共享)
	 * 
	 * @see commonj.timers.TimerManager#stop()
	 */
	@Override
	public void destroy() {
		// Stop the entire TimerManager, if necessary.
		if (!this.shared) {
			// May return early, but at least we already cancelled all known Timers.
			this.timerManager.stop();
		}
	}

}
