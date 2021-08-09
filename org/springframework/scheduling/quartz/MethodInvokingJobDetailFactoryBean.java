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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that exposes a
 * {@link org.quartz.JobDetail} object which delegates job execution to a
 * specified (static or non-static) method. Avoids the need for implementing
 * a one-line Quartz Job that just invokes an existing service method on a
 * Spring-managed target bean.
 *
 * <p>Inherits common configuration properties from the {@link MethodInvoker}
 * base class, such as {@link #setTargetObject "targetObject"} and
 * {@link #setTargetMethod "targetMethod"}, adding support for lookup of the target
 * bean by name through the {@link #setTargetBeanName "targetBeanName"} property
 * (as alternative to specifying a "targetObject" directly, allowing for
 * non-singleton target objects).
 *
 * <p>Supports both concurrently running jobs and non-currently running
 * jobs through the "concurrent" property. Jobs created by this
 * MethodInvokingJobDetailFactoryBean are by default volatile and durable
 * (according to Quartz terminology).
 *
 * <p><b>NOTE: JobDetails created via this FactoryBean are <i>not</i>
 * serializable and thus not suitable for persistent job stores.</b>
 * You need to implement your own Quartz Job as a thin wrapper for each case
 * where you want a persistent job to delegate to a specific service method.
 *
 * <p>Compatible with Quartz 2.1.4 and higher, as of Spring 4.1.
 *
 * <p>
 * 公开了一个{@link orgquartzJobDetail}对象,该对象将作业执行委托给指定的(静态或非静态)方法避免了实现单行Quartz Job的需要,该Quartz Job只需调用Spring上
 * 现有的服务方法管理目标bean。
 * 
 *  <p>继承{@link MethodInvoker}基类(例如{@link #setTargetObject"targetObject"}和{@link #setTargetMethod"targetMethod"}
 * )中的常用配置属性,添加了按名称查找目标bean的支持{@link #setTargetBeanName"targetBeanName"}属性(作为直接指定"targetObject"的替代方法,允许非
 * 单例对象对象)。
 * 
 * <p>通过"concurrent"属性同时运行作业和非当前运行的作业通过此MethodInvokingJobDetailFactoryBean创建的作业默认情况下是volatile和持久的(根据Quar
 * tz术语)。
 * 
 *  <p> <b>注意：通过此FactoryBean创建的JobDetails是<i>不</i>可序列化,因此不适合持久性作业存储</b>您需要为每种情况实现您自己的Quartz Job作为一个薄包装您希
 * 望持久性作业委托给特定的服务方法。
 * 
 *  <p>与Spring 41兼容,与Quartz 214及更高版本兼容
 * 
 * 
 * @author Juergen Hoeller
 * @author Alef Arendsen
 * @since 18.02.2004
 * @see #setTargetBeanName
 * @see #setTargetObject
 * @see #setTargetMethod
 * @see #setConcurrent
 */
public class MethodInvokingJobDetailFactoryBean extends ArgumentConvertingMethodInvoker
		implements FactoryBean<JobDetail>, BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, InitializingBean {

	private String name;

	private String group = Scheduler.DEFAULT_GROUP;

	private boolean concurrent = true;

	private String targetBeanName;

	private String beanName;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private BeanFactory beanFactory;

	private JobDetail jobDetail;


	/**
	 * Set the name of the job.
	 * <p>Default is the bean name of this FactoryBean.
	 * <p>
	 *  设置作业的名称<p>默认是此FactoryBean的bean名称
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the group of the job.
	 * <p>Default is the default group of the Scheduler.
	 * <p>
	 *  设置作业组<p>默认是调度程序的默认组
	 * 
	 * 
	 * @see org.quartz.Scheduler#DEFAULT_GROUP
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Specify whether or not multiple jobs should be run in a concurrent fashion.
	 * The behavior when one does not want concurrent jobs to be executed is
	 * realized through adding the {@code @PersistJobDataAfterExecution} and
	 * {@code @DisallowConcurrentExecution} markers.
	 * More information on stateful versus stateless jobs can be found
	 * <a href="http://www.quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/tutorial-lesson-03">here</a>.
	 * <p>The default setting is to run jobs concurrently.
	 * <p>
	 * 指定是否应该以并发方式运行多个作业通过添加{@code @PersistJobDataAfterExecution}和{@code @DisallowConcurrentExecution}标记可以实现
	 * 不需要并发作业的行为更多有关状态与状态的信息可以找到工作<a href=\"http://wwwquartz-schedulerorg/documentation/quartz-21x/tutorials/tutorial-lesson-03\">
	 * 此处</a> <p>默认设置是同时运行作业。
	 * 
	 */
	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	/**
	 * Set the name of the target bean in the Spring BeanFactory.
	 * <p>This is an alternative to specifying {@link #setTargetObject "targetObject"},
	 * allowing for non-singleton beans to be invoked. Note that specified
	 * "targetObject" and {@link #setTargetClass "targetClass"} values will
	 * override the corresponding effect of this "targetBeanName" setting
	 * (i.e. statically pre-define the bean type or even the bean object).
	 * <p>
	 * 在Spring BeanFactory中设置目标bean的名称<p>这是指定{@link #setTargetObject"targetObject"}的替代方法,允许调用非单例bean注意指定的"ta
	 * rgetObject"和{@link# setTargetClass"targetClass"}将覆盖此"targetBeanName"设置的相应效果(即静态预定义bean类型,甚至是bean对象)。
	 * 
	 */
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
		return ClassUtils.forName(className, this.beanClassLoader);
	}


	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {
		prepare();

		// Use specific name if given, else fall back to bean name.
		String name = (this.name != null ? this.name : this.beanName);

		// Consider the concurrent flag to choose between stateful and stateless job.
		Class<?> jobClass = (this.concurrent ? MethodInvokingJob.class : StatefulMethodInvokingJob.class);

		// Build JobDetail instance.
		JobDetailImpl jdi = new JobDetailImpl();
		jdi.setName(name);
		jdi.setGroup(this.group);
		jdi.setJobClass((Class) jobClass);
		jdi.setDurability(true);
		jdi.getJobDataMap().put("methodInvoker", this);
		this.jobDetail = jdi;

		postProcessJobDetail(this.jobDetail);
	}

	/**
	 * Callback for post-processing the JobDetail to be exposed by this FactoryBean.
	 * <p>The default implementation is empty. Can be overridden in subclasses.
	 * <p>
	 *  回调用于对JobDetail进行后处理以由该FactoryBean公开<p>默认实现为空可以在子类中覆盖
	 * 
	 * 
	 * @param jobDetail the JobDetail prepared by this FactoryBean
	 */
	protected void postProcessJobDetail(JobDetail jobDetail) {
	}


	/**
	 * Overridden to support the {@link #setTargetBeanName "targetBeanName"} feature.
	 * <p>
	 *  被覆盖以支持{@link #setTargetBeanName"targetBeanName"}功能
	 * 
	 */
	@Override
	public Class<?> getTargetClass() {
		Class<?> targetClass = super.getTargetClass();
		if (targetClass == null && this.targetBeanName != null) {
			Assert.state(this.beanFactory != null, "BeanFactory must be set when using 'targetBeanName'");
			targetClass = this.beanFactory.getType(this.targetBeanName);
		}
		return targetClass;
	}

	/**
	 * Overridden to support the {@link #setTargetBeanName "targetBeanName"} feature.
	 * <p>
	 *  被覆盖以支持{@link #setTargetBeanName"targetBeanName"}功能
	 * 
	 */
	@Override
	public Object getTargetObject() {
		Object targetObject = super.getTargetObject();
		if (targetObject == null && this.targetBeanName != null) {
			Assert.state(this.beanFactory != null, "BeanFactory must be set when using 'targetBeanName'");
			targetObject = this.beanFactory.getBean(this.targetBeanName);
		}
		return targetObject;
	}


	@Override
	public JobDetail getObject() {
		return this.jobDetail;
	}

	@Override
	public Class<? extends JobDetail> getObjectType() {
		return (this.jobDetail != null ? this.jobDetail.getClass() : JobDetail.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	/**
	 * Quartz Job implementation that invokes a specified method.
	 * Automatically applied by MethodInvokingJobDetailFactoryBean.
	 * <p>
	 * 调用一个指定方法的Quartz Job实现由MethodInvokingJobDetailFactoryBean自动应用
	 * 
	 */
	public static class MethodInvokingJob extends QuartzJobBean {

		protected static final Log logger = LogFactory.getLog(MethodInvokingJob.class);

		private MethodInvoker methodInvoker;

		/**
		 * Set the MethodInvoker to use.
		 * <p>
		 *  将MethodInvoker设置为使用
		 * 
		 */
		public void setMethodInvoker(MethodInvoker methodInvoker) {
			this.methodInvoker = methodInvoker;
		}

		/**
		 * Invoke the method via the MethodInvoker.
		 * <p>
		 *  通过MethodInvoker调用该方法
		 * 
		 */
		@Override
		protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
			try {
				context.setResult(this.methodInvoker.invoke());
			}
			catch (InvocationTargetException ex) {
				if (ex.getTargetException() instanceof JobExecutionException) {
					// -> JobExecutionException, to be logged at info level by Quartz
					throw (JobExecutionException) ex.getTargetException();
				}
				else {
					// -> "unhandled exception", to be logged at error level by Quartz
					throw new JobMethodInvocationFailedException(this.methodInvoker, ex.getTargetException());
				}
			}
			catch (Exception ex) {
				// -> "unhandled exception", to be logged at error level by Quartz
				throw new JobMethodInvocationFailedException(this.methodInvoker, ex);
			}
		}
	}


	/**
	 * Extension of the MethodInvokingJob, implementing the StatefulJob interface.
	 * Quartz checks whether or not jobs are stateful and if so,
	 * won't let jobs interfere with each other.
	 * <p>
	 *  MethodInvokingJob的扩展,实现StatefulJob接口Quartz检查作业是否有状态,如果是这样,不会让作业相互干扰
	 */
	@PersistJobDataAfterExecution
	@DisallowConcurrentExecution
	public static class StatefulMethodInvokingJob extends MethodInvokingJob {

		// No implementation, just an addition of the tag interface StatefulJob
		// in order to allow stateful method invoking jobs.
	}

}
