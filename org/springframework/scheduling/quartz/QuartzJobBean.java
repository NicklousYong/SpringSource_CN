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

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * Simple implementation of the Quartz Job interface, applying the
 * passed-in JobDataMap and also the SchedulerContext as bean property
 * values. This is appropriate because a new Job instance will be created
 * for each execution. JobDataMap entries will override SchedulerContext
 * entries with the same keys.
 *
 * <p>For example, let's assume that the JobDataMap contains a key
 * "myParam" with value "5": The Job implementation can then expose
 * a bean property "myParam" of type int to receive such a value,
 * i.e. a method "setMyParam(int)". This will also work for complex
 * types like business objects etc.
 *
 * <p><b>Note that the preferred way to apply dependency injection
 * to Job instances is via a JobFactory:</b> that is, to specify
 * {@link SpringBeanJobFactory} as Quartz JobFactory (typically via
 * {@link SchedulerFactoryBean#setJobFactory} SchedulerFactoryBean's "jobFactory" property}).
 * This allows to implement dependency-injected Quartz Jobs without
 * a dependency on Spring base classes.
 *
 * <p>
 * 简单实现Quartz Job界面,将传入的JobDataMap以及SchedulerContext应用为bean属性值这是适当的,因为将为每个执行创建一个新的Job实例JobDataMap条目将覆盖具有
 * 相同键的SchedulerContext条目。
 * 
 *  例如,假设JobDataMap包含一个值为"5"的键"myParam"：然后,Job实现可以暴露类型为int的bean属性"myParam",以接收这样的值,即一种方法"setMyParam int)
 * "这也适用于诸如业务对象等复杂类型。
 * 
 * 请注意,对Job实例应用依赖注入的首选方法是通过JobFactory：</b>来指定{@link SpringBeanJobFactory}为Quartz JobFactory(通常通过{@link SchedulerFactoryBean#setJobFactory}
 *  SchedulerFactoryBean的"jobFactory"属性})这允许实现依赖注入的Quartz作业,而不依赖于Spring基类。
 * 
 * @author Juergen Hoeller
 * @since 18.02.2004
 * @see org.quartz.JobExecutionContext#getMergedJobDataMap()
 * @see org.quartz.Scheduler#getContext()
 * @see SchedulerFactoryBean#setSchedulerContextAsMap
 * @see SpringBeanJobFactory
 * @see SchedulerFactoryBean#setJobFactory
 */
public abstract class QuartzJobBean implements Job {

	/**
	 * This implementation applies the passed-in job data map as bean property
	 * values, and delegates to {@code executeInternal} afterwards.
	 * <p>
	 * 
	 * 
	 * @see #executeInternal
	 */
	@Override
	public final void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValues(context.getScheduler().getContext());
			pvs.addPropertyValues(context.getMergedJobDataMap());
			bw.setPropertyValues(pvs, true);
		}
		catch (SchedulerException ex) {
			throw new JobExecutionException(ex);
		}
		executeInternal(context);
	}

	/**
	 * Execute the actual job. The job data map will already have been
	 * applied as bean property values by execute. The contract is
	 * exactly the same as for the standard Quartz execute method.
	 * <p>
	 *  此实现将传入的作业数据映射应用为bean属性值,然后委托给{@code executeInternal}
	 * 
	 * 
	 * @see #execute
	 */
	protected abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

}
