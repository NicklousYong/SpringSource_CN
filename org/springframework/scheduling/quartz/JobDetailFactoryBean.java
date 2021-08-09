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

import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A Spring {@link FactoryBean} for creating a Quartz {@link org.quartz.JobDetail}
 * instance, supporting bean-style usage for JobDetail configuration.
 *
 * <p>{@code JobDetail(Impl)} itself is already a JavaBean but lacks
 * sensible defaults. This class uses the Spring bean name as job name,
 * and the Quartz default group ("DEFAULT") as job group if not specified.
 *
 * <p>
 *  用于创建Quartz {@link orgquartzJobDetail}实例的Spring {@link FactoryBean},支持JobDetail配置的bean样式使用
 * 
 * <p> {@ Code JobDetail(Impl)}本身已经是一个JavaBean,但缺乏明智的默认值此类使用Spring bean名称作为作业名称,Quartz默认组("DEFAULT")作为作业
 * 组(如果未指定)。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.1
 * @see #setName
 * @see #setGroup
 * @see org.springframework.beans.factory.BeanNameAware
 * @see org.quartz.Scheduler#DEFAULT_GROUP
 */
public class JobDetailFactoryBean
		implements FactoryBean<JobDetail>, BeanNameAware, ApplicationContextAware, InitializingBean {

	private String name;

	private String group;

	private Class<?> jobClass;

	private JobDataMap jobDataMap = new JobDataMap();

	private boolean durability = false;

	private boolean requestsRecovery = false;

	private String description;

	private String beanName;

	private ApplicationContext applicationContext;

	private String applicationContextJobDataKey;

	private JobDetail jobDetail;


	/**
	 * Specify the job's name.
	 * <p>
	 *  指定作业的名称
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Specify the job's group.
	 * <p>
	 *  指定作业的组
	 * 
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Specify the job's implementation class.
	 * <p>
	 *  指定作业的实现类
	 * 
	 */
	public void setJobClass(Class<?> jobClass) {
		this.jobClass = jobClass;
	}

	/**
	 * Set the job's JobDataMap.
	 * <p>
	 *  设置作业的JobDataMap
	 * 
	 * 
	 * @see #setJobDataAsMap
	 */
	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	/**
	 * Return the job's JobDataMap.
	 * <p>
	 *  返回作业的JobDataMap
	 * 
	 */
	public JobDataMap getJobDataMap() {
		return this.jobDataMap;
	}

	/**
	 * Register objects in the JobDataMap via a given Map.
	 * <p>These objects will be available to this Job only,
	 * in contrast to objects in the SchedulerContext.
	 * <p>Note: When using persistent Jobs whose JobDetail will be kept in the
	 * database, do not put Spring-managed beans or an ApplicationContext
	 * reference into the JobDataMap but rather into the SchedulerContext.
	 * <p>
	 *  通过给定的Map注册JobDataMap中的对象<p>这些对象仅适用于此作业,与SchedulerContext <p>中的对象相反。
	 * 注意：在使用JobDetail保留在数据库中的持久性作业时,请勿将Spring管理的Bean或ApplicationContext引用到JobDataMap中,而是进入SchedulerContext。
	 *  通过给定的Map注册JobDataMap中的对象<p>这些对象仅适用于此作业,与SchedulerContext <p>中的对象相反。
	 * 
	 * 
	 * @param jobDataAsMap Map with String keys and any objects as values
	 * (for example Spring-managed beans)
	 * @see org.springframework.scheduling.quartz.SchedulerFactoryBean#setSchedulerContextAsMap
	 */
	public void setJobDataAsMap(Map<String, ?> jobDataAsMap) {
		getJobDataMap().putAll(jobDataAsMap);
	}

	/**
	 * Specify the job's durability, i.e. whether it should remain stored
	 * in the job store even if no triggers point to it anymore.
	 * <p>
	 * 指定作业的持久性,即是否应该保留在作业存储中,即使没有任何触发器指向它
	 * 
	 */
	public void setDurability(boolean durability) {
		this.durability = durability;
	}

	/**
	 * Set the recovery flag for this job, i.e. whether or not the job should
	 * get re-executed if a 'recovery' or 'fail-over' situation is encountered.
	 * <p>
	 *  设置此作业的恢复标志,即如果遇到"恢复"或"故障切换"情况,该作业是否应重新执行
	 * 
	 */
	public void setRequestsRecovery(boolean requestsRecovery) {
		this.requestsRecovery = requestsRecovery;
	}

	/**
	 * Set a textual description for this job.
	 * <p>
	 *  设置此作业的文本描述
	 * 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Set the key of an ApplicationContext reference to expose in the JobDataMap,
	 * for example "applicationContext". Default is none.
	 * Only applicable when running in a Spring ApplicationContext.
	 * <p>In case of a QuartzJobBean, the reference will be applied to the Job
	 * instance as bean property. An "applicationContext" attribute will correspond
	 * to a "setApplicationContext" method in that scenario.
	 * <p>Note that BeanFactory callback interfaces like ApplicationContextAware
	 * are not automatically applied to Quartz Job instances, because Quartz
	 * itself is responsible for the lifecycle of its Jobs.
	 * <p><b>Note: When using persistent job stores where JobDetail contents will
	 * be kept in the database, do not put an ApplicationContext reference into
	 * the JobDataMap but rather into the SchedulerContext.</b>
	 * <p>
	 * 将ApplicationContext引用的密钥设置为在JobDataMap中公开,例如"applicationContext"默认值为none仅在运行Spring ApplicationContext
	 * 时适用<p>如果是QuartzJobBean,引用将作为bean属性应用于Job实例"applicationContext"属性将对应于该场景中的"setApplicationContext"方法<p>
	 * 请注意,BeanFactory回调接口(如ApplicationContextAware)不会自动应用于Quartz Job实例,因为Quartz本身负责其作业的生命周期<p> < b>注意：在使用Jo
	 * 
	 * @see org.springframework.scheduling.quartz.SchedulerFactoryBean#setApplicationContextSchedulerContextKey
	 * @see org.springframework.context.ApplicationContext
	 */
	public void setApplicationContextJobDataKey(String applicationContextJobDataKey) {
		this.applicationContextJobDataKey = applicationContextJobDataKey;
	}


	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() {
		if (this.name == null) {
			this.name = this.beanName;
		}
		if (this.group == null) {
			this.group = Scheduler.DEFAULT_GROUP;
		}
		if (this.applicationContextJobDataKey != null) {
			if (this.applicationContext == null) {
				throw new IllegalStateException(
					"JobDetailBean needs to be set up in an ApplicationContext " +
					"to be able to handle an 'applicationContextJobDataKey'");
			}
			getJobDataMap().put(this.applicationContextJobDataKey, this.applicationContext);
		}

		JobDetailImpl jdi = new JobDetailImpl();
		jdi.setName(this.name);
		jdi.setGroup(this.group);
		jdi.setJobClass((Class) this.jobClass);
		jdi.setJobDataMap(this.jobDataMap);
		jdi.setDurability(this.durability);
		jdi.setRequestsRecovery(this.requestsRecovery);
		jdi.setDescription(this.description);
		this.jobDetail = jdi;
	}


	@Override
	public JobDetail getObject() {
		return this.jobDetail;
	}

	@Override
	public Class<?> getObjectType() {
		return JobDetail.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
