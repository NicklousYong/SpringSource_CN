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

import java.util.LinkedList;
import java.util.List;
import javax.naming.NamingException;

import commonj.timers.Timer;
import commonj.timers.TimerManager;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that retrieves a
 * CommonJ {@link commonj.timers.TimerManager} and exposes it for bean references.
 *
 * <p><b>This is the central convenience class for setting up a
 * CommonJ TimerManager in a Spring context.</b>
 *
 * <p>Allows for registration of ScheduledTimerListeners. This is the main
 * purpose of this class; the TimerManager itself could also be fetched
 * from JNDI via {@link org.springframework.jndi.JndiObjectFactoryBean}.
 * In scenarios that just require static registration of tasks at startup,
 * there is no need to access the TimerManager itself in application code.
 *
 * <p>Note that the TimerManager uses a TimerListener instance that is
 * shared between repeated executions, in contrast to Quartz which
 * instantiates a new Job for each execution.
 *
 * <p>
 *  {@link orgspringframeworkbeansfactoryFactoryBean},它检索一个CommonJ {@link commonjtimersTimerManager}并将其公
 * 开给bean引用。
 * 
 * <p> <b>这是在Spring上下文</b>中设置CommonJ TimerManager的中心便利类
 * 
 *  <p>允许注册ScheduledTimerListeners这是此类的主要目的; TimerManager本身也可以通过{@link orgspringframeworkjndiJndiObjectFactoryBean}
 * 从JNDI中获取。
 * 在启动时只需要静态注册任务的情况下,不需要在应用程序代码中访问TimerManager本身。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see ScheduledTimerListener
 * @see commonj.timers.TimerManager
 * @see commonj.timers.TimerListener
 */
public class TimerManagerFactoryBean extends TimerManagerAccessor
		implements FactoryBean<TimerManager>, InitializingBean, DisposableBean, Lifecycle {

	private ScheduledTimerListener[] scheduledTimerListeners;

	private final List<Timer> timers = new LinkedList<Timer>();


	/**
	 * Register a list of ScheduledTimerListener objects with the TimerManager
	 * that this FactoryBean creates. Depending on each ScheduledTimerListener's settings,
	 * it will be registered via one of TimerManager's schedule methods.
	 * <p>
	 *  <p>请注意,TimerManager使用在重复执行之间共享的TimerListener实例,与Quartz相反,Quartz会为每个执行实例化一个新的作业
	 * 
	 * 
	 * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, long)
	 * @see commonj.timers.TimerManager#schedule(commonj.timers.TimerListener, long, long)
	 * @see commonj.timers.TimerManager#scheduleAtFixedRate(commonj.timers.TimerListener, long, long)
	 */
	public void setScheduledTimerListeners(ScheduledTimerListener[] scheduledTimerListeners) {
		this.scheduledTimerListeners = scheduledTimerListeners;
	}


	//---------------------------------------------------------------------
	// Implementation of InitializingBean interface
	//---------------------------------------------------------------------

	@Override
	public void afterPropertiesSet() throws NamingException {
		super.afterPropertiesSet();
		if (this.scheduledTimerListeners != null) {
			TimerManager timerManager = getTimerManager();
			for (ScheduledTimerListener scheduledTask : this.scheduledTimerListeners) {
				Timer timer;
				if (scheduledTask.isOneTimeTask()) {
					timer = timerManager.schedule(scheduledTask.getTimerListener(), scheduledTask.getDelay());
				}
				else {
					if (scheduledTask.isFixedRate()) {
						timer = timerManager.scheduleAtFixedRate(
								scheduledTask.getTimerListener(), scheduledTask.getDelay(), scheduledTask.getPeriod());
					}
					else {
						timer = timerManager.schedule(
								scheduledTask.getTimerListener(), scheduledTask.getDelay(), scheduledTask.getPeriod());
					}
				}
				this.timers.add(timer);
			}
		}
	}


	//---------------------------------------------------------------------
	// Implementation of FactoryBean interface
	//---------------------------------------------------------------------

	@Override
	public TimerManager getObject() {
		return getTimerManager();
	}

	@Override
	public Class<? extends TimerManager> getObjectType() {
		TimerManager timerManager = getTimerManager();
		return (timerManager != null ? timerManager.getClass() : TimerManager.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	//---------------------------------------------------------------------
	// Implementation of DisposableBean interface
	//---------------------------------------------------------------------

	/**
	 * Cancels all statically registered Timers on shutdown,
	 * and stops the underlying TimerManager (if not shared).
	 * <p>
	 * 使用TimerManager注册ScheduledTimerListener对象的列表,这个FactoryBean创建根据每个ScheduledTimerListener的设置,它将通过TimerMan
	 * ager的调度方法之一注册。
	 * 
	 * 
	 * @see commonj.timers.Timer#cancel()
	 * @see commonj.timers.TimerManager#stop()
	 */
	@Override
	public void destroy() {
		// Cancel all registered timers.
		for (Timer timer : this.timers) {
			try {
				timer.cancel();
			}
			catch (Throwable ex) {
				logger.warn("Could not cancel CommonJ Timer", ex);
			}
		}
		this.timers.clear();

		// Stop the TimerManager itself.
		super.destroy();
	}

}
