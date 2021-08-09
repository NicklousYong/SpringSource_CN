/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.scheduling.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * Spring bean-style class for accessing a Quartz Scheduler, i.e. for registering jobs,
 * triggers and listeners on a given {@link org.quartz.Scheduler} instance.
 *
 * <p>Compatible with Quartz 2.1.4 and higher, as of Spring 4.1.
 *
 * <p>
 *  Spring bean类用于访问Quartz Scheduler,即在给定的{@link orgquartzScheduler}实例上注册作业,触发器和侦听器
 * 
 *  <p>与Spring 41兼容,与Quartz 214及更高版本兼容
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5.6
 * @see #setScheduler
 * @see #setSchedulerName
 */
public class SchedulerAccessorBean extends SchedulerAccessor implements BeanFactoryAware, InitializingBean {

	private String schedulerName;

	private Scheduler scheduler;

	private BeanFactory beanFactory;


	/**
	 * Specify the Quartz {@link Scheduler} to operate on via its scheduler name in the Spring
	 * application context or also in the Quartz {@link org.quartz.impl.SchedulerRepository}.
	 * <p>Schedulers can be registered in the repository through custom bootstrapping,
	 * e.g. via the {@link org.quartz.impl.StdSchedulerFactory} or
	 * {@link org.quartz.impl.DirectSchedulerFactory} factory classes.
	 * However, in general, it's preferable to use Spring's {@link SchedulerFactoryBean}
	 * which includes the job/trigger/listener capabilities of this accessor as well.
	 * <p>If not specified, this accessor will try to retrieve a default {@link Scheduler}
	 * bean from the containing application context.
	 * <p>
	 * 指定Quartz {@link Scheduler}通过Spring应用程序上下文中的调度程序名称或Quartz {@link orgquartzimplSchedulerRepository}中的调度
	 * 程序名称进行操作<p>可以通过自定义引导(例如通过{@link orgquartzimplStdSchedulerFactory}或{@link orgquartzimplDirectSchedulerFactory}
	 * 工厂类但是,一般来说,最好使用Spring的{@link SchedulerFactoryBean},其中包括此访问器的作业/触发器/侦听器功能<p>如果未指定,该访问器将尝试从包含的应用程序上下文中检
	 * 索默认的{@link Scheduler} bean。
	 * 
	 */
	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	/**
	 * Specify the Quartz {@link Scheduler} instance to operate on.
	 * <p>If not specified, this accessor will try to retrieve a default {@link Scheduler}
	 * bean from the containing application context.
	 * <p>
	 * 指定Quartz {@link Scheduler}实例在<p>上操作如果未指定,此访问者将尝试从包含的应用程序上下文中检索默认的{@link Scheduler} bean
	 * 
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * Return the Quartz Scheduler instance that this accessor operates on.
	 * <p>
	 *  返回此访问器操作的Quartz Scheduler实例
	 */
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public void afterPropertiesSet() throws SchedulerException {
		if (this.scheduler == null) {
			this.scheduler = (this.schedulerName != null ? findScheduler(this.schedulerName) : findDefaultScheduler());
		}
		registerListeners();
		registerJobsAndTriggers();
	}

	protected Scheduler findScheduler(String schedulerName) throws SchedulerException {
		if (this.beanFactory instanceof ListableBeanFactory) {
			ListableBeanFactory lbf = (ListableBeanFactory) this.beanFactory;
			String[] beanNames = lbf.getBeanNamesForType(Scheduler.class);
			for (String beanName : beanNames) {
				Scheduler schedulerBean = (Scheduler) lbf.getBean(beanName);
				if (schedulerName.equals(schedulerBean.getSchedulerName())) {
					return schedulerBean;
				}
			}
		}
		Scheduler schedulerInRepo = SchedulerRepository.getInstance().lookup(schedulerName);
		if (schedulerInRepo == null) {
			throw new IllegalStateException("No Scheduler named '" + schedulerName + "' found");
		}
		return schedulerInRepo;
	}

	protected Scheduler findDefaultScheduler() {
		if (this.beanFactory != null) {
			return this.beanFactory.getBean(Scheduler.class);
		}
		else {
			throw new IllegalStateException(
					"No Scheduler specified, and cannot find a default Scheduler without a BeanFactory");
		}
	}

}
