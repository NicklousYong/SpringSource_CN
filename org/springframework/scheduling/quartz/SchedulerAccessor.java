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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.ListenerManager;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.xml.XMLSchedulingDataProcessor;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Common base class for accessing a Quartz Scheduler, i.e. for registering jobs,
 * triggers and listeners on a {@link org.quartz.Scheduler} instance.
 *
 * <p>For concrete usage, check out the {@link SchedulerFactoryBean} and
 * {@link SchedulerAccessorBean} classes.
 *
 * <p>Compatible with Quartz 2.1.4 and higher, as of Spring 4.1.
 *
 * <p>
 *  用于访问Quartz Scheduler的通用基类,即在{@link orgquartzScheduler}实例上注册作业,触发器和侦听器
 * 
 * <p>有关具体用法,请查看{@link SchedulerFactoryBean}和{@link SchedulerAccessorBean}类
 * 
 *  <p>与Spring 41兼容,与Quartz 214及更高版本兼容
 * 
 * 
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 2.5.6
 */
public abstract class SchedulerAccessor implements ResourceLoaderAware {

	protected final Log logger = LogFactory.getLog(getClass());

	private boolean overwriteExistingJobs = false;

	private String[] jobSchedulingDataLocations;

	private List<JobDetail> jobDetails;

	private Map<String, Calendar> calendars;

	private List<Trigger> triggers;

	private SchedulerListener[] schedulerListeners;

	private JobListener[] globalJobListeners;

	private TriggerListener[] globalTriggerListeners;

	private PlatformTransactionManager transactionManager;

	protected ResourceLoader resourceLoader;


	/**
	 * Set whether any jobs defined on this SchedulerFactoryBean should overwrite
	 * existing job definitions. Default is "false", to not overwrite already
	 * registered jobs that have been read in from a persistent job store.
	 * <p>
	 *  设置此SchedulerFactoryBean上定义的任何作业是否应覆盖现有作业定义Default为"false",以不覆盖从持久作业存储中读取的已注册作业
	 * 
	 */
	public void setOverwriteExistingJobs(boolean overwriteExistingJobs) {
		this.overwriteExistingJobs = overwriteExistingJobs;
	}

	/**
	 * Set the location of a Quartz job definition XML file that follows the
	 * "job_scheduling_data_1_5" XSD or better. Can be specified to automatically
	 * register jobs that are defined in such a file, possibly in addition
	 * to jobs defined directly on this SchedulerFactoryBean.
	 * <p>
	 *  设置"job_scheduling_data_1_5"XSD或更好的Quartz作业定义XML文件的位置可以指定为自动注册在此类文件中定义的作业,可能除了直接在此SchedulerFactoryBea
	 * n上定义的作业之外。
	 * 
	 * 
	 * @see org.quartz.xml.XMLSchedulingDataProcessor
	 */
	public void setJobSchedulingDataLocation(String jobSchedulingDataLocation) {
		this.jobSchedulingDataLocations = new String[] {jobSchedulingDataLocation};
	}

	/**
	 * Set the locations of Quartz job definition XML files that follow the
	 * "job_scheduling_data_1_5" XSD or better. Can be specified to automatically
	 * register jobs that are defined in such files, possibly in addition
	 * to jobs defined directly on this SchedulerFactoryBean.
	 * <p>
	 * 设置遵循"job_scheduling_data_1_5"XSD或更高版本的Quartz作业定义XML文件的位置可以指定为自动注册在这些文件中定义的作业,可能除了直接在此SchedulerFactory
	 * Bean上定义的作业之外。
	 * 
	 * 
	 * @see org.quartz.xml.XMLSchedulingDataProcessor
	 */
	public void setJobSchedulingDataLocations(String... jobSchedulingDataLocations) {
		this.jobSchedulingDataLocations = jobSchedulingDataLocations;
	}

	/**
	 * Register a list of JobDetail objects with the Scheduler that
	 * this FactoryBean creates, to be referenced by Triggers.
	 * <p>This is not necessary when a Trigger determines the JobDetail
	 * itself: In this case, the JobDetail will be implicitly registered
	 * in combination with the Trigger.
	 * <p>
	 *  注册JobDetail对象的列表,该FactoryBean创建的调度程序将被触发器引用<p>当触发器确定JobDetail本身时,这不是必需的：在这种情况下,JobDetail将被隐式地与触发器一起注
	 * 册。
	 * 
	 * 
	 * @see #setTriggers
	 * @see org.quartz.JobDetail
	 */
	public void setJobDetails(JobDetail... jobDetails) {
		// Use modifiable ArrayList here, to allow for further adding of
		// JobDetail objects during autodetection of JobDetail-aware Triggers.
		this.jobDetails = new ArrayList<JobDetail>(Arrays.asList(jobDetails));
	}

	/**
	 * Register a list of Quartz Calendar objects with the Scheduler
	 * that this FactoryBean creates, to be referenced by Triggers.
	 * <p>
	 *  使用FactoryBean创建的Scheduler注册Quartz Calendar对象列表,由Triggers引用
	 * 
	 * 
	 * @param calendars Map with calendar names as keys as Calendar
	 * objects as values
	 * @see org.quartz.Calendar
	 */
	public void setCalendars(Map<String, Calendar> calendars) {
		this.calendars = calendars;
	}

	/**
	 * Register a list of Trigger objects with the Scheduler that
	 * this FactoryBean creates.
	 * <p>If the Trigger determines the corresponding JobDetail itself,
	 * the job will be automatically registered with the Scheduler.
	 * Else, the respective JobDetail needs to be registered via the
	 * "jobDetails" property of this FactoryBean.
	 * <p>
	 * 使用此FactoryBean创建的调度程序注册Trigger对象的列表<p>如果触发器确定相应的JobDetail本身,则该作业将自动注册到Scheduler Else,相应的JobDetail需要通过
	 * "jobDetails"属性注册这个FactoryBean。
	 * 
	 * 
	 * @see #setJobDetails
	 * @see org.quartz.JobDetail
	 */
	public void setTriggers(Trigger... triggers) {
		this.triggers = Arrays.asList(triggers);
	}

	/**
	 * Specify Quartz SchedulerListeners to be registered with the Scheduler.
	 * <p>
	 *  指定Quartz SchedulerListeners注册到Scheduler
	 * 
	 */
	public void setSchedulerListeners(SchedulerListener... schedulerListeners) {
		this.schedulerListeners = schedulerListeners;
	}

	/**
	 * Specify global Quartz JobListeners to be registered with the Scheduler.
	 * Such JobListeners will apply to all Jobs in the Scheduler.
	 * <p>
	 *  指定要在Scheduler中注册的全局Quartz JobListeners这样的JobListeners将应用于Scheduler中的所有作业
	 * 
	 */
	public void setGlobalJobListeners(JobListener... globalJobListeners) {
		this.globalJobListeners = globalJobListeners;
	}

	/**
	 * Specify global Quartz TriggerListeners to be registered with the Scheduler.
	 * Such TriggerListeners will apply to all Triggers in the Scheduler.
	 * <p>
	 *  指定要在Scheduler中注册的全局Quartz TriggerListeners这样的TriggerListeners将应用于Scheduler中的所有触发器
	 * 
	 */
	public void setGlobalTriggerListeners(TriggerListener... globalTriggerListeners) {
		this.globalTriggerListeners = globalTriggerListeners;
	}

	/**
	 * Set the transaction manager to be used for registering jobs and triggers
	 * that are defined by this SchedulerFactoryBean. Default is none; setting
	 * this only makes sense when specifying a DataSource for the Scheduler.
	 * <p>
	 * 将事务管理器设置为用于注册由此SchedulerFactoryBean定义的作业和触发器Default为none;在为调度程序指定DataSource时,设置此选项才有意义
	 * 
	 */
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}


	/**
	 * Register jobs and triggers (within a transaction, if possible).
	 * <p>
	 *  注册作业和触发器(如果可能的话)
	 * 
	 */
	protected void registerJobsAndTriggers() throws SchedulerException {
		TransactionStatus transactionStatus = null;
		if (this.transactionManager != null) {
			transactionStatus = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		}

		try {
			if (this.jobSchedulingDataLocations != null) {
				ClassLoadHelper clh = new ResourceLoaderClassLoadHelper(this.resourceLoader);
				clh.initialize();
				XMLSchedulingDataProcessor dataProcessor = new XMLSchedulingDataProcessor(clh);
				for (String location : this.jobSchedulingDataLocations) {
					dataProcessor.processFileAndScheduleJobs(location, getScheduler());
				}
			}

			// Register JobDetails.
			if (this.jobDetails != null) {
				for (JobDetail jobDetail : this.jobDetails) {
					addJobToScheduler(jobDetail);
				}
			}
			else {
				// Create empty list for easier checks when registering triggers.
				this.jobDetails = new LinkedList<JobDetail>();
			}

			// Register Calendars.
			if (this.calendars != null) {
				for (String calendarName : this.calendars.keySet()) {
					Calendar calendar = this.calendars.get(calendarName);
					getScheduler().addCalendar(calendarName, calendar, true, true);
				}
			}

			// Register Triggers.
			if (this.triggers != null) {
				for (Trigger trigger : this.triggers) {
					addTriggerToScheduler(trigger);
				}
			}
		}

		catch (Throwable ex) {
			if (transactionStatus != null) {
				try {
					this.transactionManager.rollback(transactionStatus);
				}
				catch (TransactionException tex) {
					logger.error("Job registration exception overridden by rollback exception", ex);
					throw tex;
				}
			}
			if (ex instanceof SchedulerException) {
				throw (SchedulerException) ex;
			}
			if (ex instanceof Exception) {
				throw new SchedulerException("Registration of jobs and triggers failed: " + ex.getMessage(), ex);
			}
			throw new SchedulerException("Registration of jobs and triggers failed: " + ex.getMessage());
		}

		if (transactionStatus != null) {
			this.transactionManager.commit(transactionStatus);
		}
	}

	/**
	 * Add the given job to the Scheduler, if it doesn't already exist.
	 * Overwrites the job in any case if "overwriteExistingJobs" is set.
	 * <p>
	 *  将给定作业添加到调度程序(如果尚不存在)如果设置了"overwriteExistingJobs",则覆盖作业
	 * 
	 * 
	 * @param jobDetail the job to add
	 * @return {@code true} if the job was actually added,
	 * {@code false} if it already existed before
	 * @see #setOverwriteExistingJobs
	 */
	private boolean addJobToScheduler(JobDetail jobDetail) throws SchedulerException {
		if (this.overwriteExistingJobs || getScheduler().getJobDetail(jobDetail.getKey()) == null) {
			getScheduler().addJob(jobDetail, true);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Add the given trigger to the Scheduler, if it doesn't already exist.
	 * Overwrites the trigger in any case if "overwriteExistingJobs" is set.
	 * <p>
	 *  将给定的触发器添加到调度程序(如果尚不存在)如果设置了"overwriteExistingJobs",则覆盖触发器
	 * 
	 * 
	 * @param trigger the trigger to add
	 * @return {@code true} if the trigger was actually added,
	 * {@code false} if it already existed before
	 * @see #setOverwriteExistingJobs
	 */
	private boolean addTriggerToScheduler(Trigger trigger) throws SchedulerException {
		boolean triggerExists = (getScheduler().getTrigger(trigger.getKey()) != null);
		if (triggerExists && !this.overwriteExistingJobs) {
			return false;
		}

		// Check if the Trigger is aware of an associated JobDetail.
		JobDetail jobDetail = (JobDetail) trigger.getJobDataMap().remove("jobDetail");
		if (triggerExists) {
			if (jobDetail != null && !this.jobDetails.contains(jobDetail) && addJobToScheduler(jobDetail)) {
				this.jobDetails.add(jobDetail);
			}
			getScheduler().rescheduleJob(trigger.getKey(), trigger);
		}
		else {
			try {
				if (jobDetail != null && !this.jobDetails.contains(jobDetail) &&
						(this.overwriteExistingJobs || getScheduler().getJobDetail(jobDetail.getKey()) == null)) {
					getScheduler().scheduleJob(jobDetail, trigger);
					this.jobDetails.add(jobDetail);
				}
				else {
					getScheduler().scheduleJob(trigger);
				}
			}
			catch (ObjectAlreadyExistsException ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Unexpectedly found existing trigger, assumably due to cluster race condition: " +
							ex.getMessage() + " - can safely be ignored");
				}
				if (this.overwriteExistingJobs) {
					getScheduler().rescheduleJob(trigger.getKey(), trigger);
				}
			}
		}
		return true;
	}

	/**
	 * Register all specified listeners with the Scheduler.
	 * <p>
	 *  使用Scheduler注册所有指定的侦听器
	 * 
	 */
	protected void registerListeners() throws SchedulerException {
		ListenerManager listenerManager = getScheduler().getListenerManager();
		if (this.schedulerListeners != null) {
			for (SchedulerListener listener : this.schedulerListeners) {
				listenerManager.addSchedulerListener(listener);
			}
		}
		if (this.globalJobListeners != null) {
			for (JobListener listener : this.globalJobListeners) {
				listenerManager.addJobListener(listener);
			}
		}
		if (this.globalTriggerListeners != null) {
			for (TriggerListener listener : this.globalTriggerListeners) {
				listenerManager.addTriggerListener(listener);
			}
		}
	}


	/**
	 * Template method that determines the Scheduler to operate on.
	 * To be implemented by subclasses.
	 * <p>
	 *  确定调度程序操作的模板方法由子类实现
	 */
	protected abstract Scheduler getScheduler();

}
