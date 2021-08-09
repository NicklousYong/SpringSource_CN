/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

import org.quartz.SchedulerContext;
import org.quartz.spi.TriggerFiredBundle;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * Subclass of {@link AdaptableJobFactory} that also supports Spring-style
 * dependency injection on bean properties. This is essentially the direct
 * equivalent of Spring's {@link QuartzJobBean} in the shape of a Quartz
 * {@link org.quartz.spi.JobFactory}.
 *
 * <p>Applies scheduler context, job data map and trigger data map entries
 * as bean property values. If no matching bean property is found, the entry
 * is by default simply ignored. This is analogous to QuartzJobBean's behavior.
 *
 * <p>Compatible with Quartz 2.1.4 and higher, as of Spring 4.1.
 *
 * <p>
 * 还支持对bean属性进行Spring样式的依赖注入的{@link AdaptableJobFactory}子类本质上与Spring的{@link QuartzJobBean}的形式直接相当,它的形状就是
 * Quartz {@link orgquartzspiJobFactory}。
 * 
 *  <p>将调度程序上下文,作业数据映射和触发器数据映射条目应用为bean属性值如果没有找到匹配的bean属性,则默认情况下该条目将被忽略这类似于QuartzJobBean的行为
 * 
 *  <p>与Spring 41兼容,与Quartz 214及更高版本兼容
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see SchedulerFactoryBean#setJobFactory
 * @see QuartzJobBean
 */
public class SpringBeanJobFactory extends AdaptableJobFactory implements SchedulerContextAware {

	private String[] ignoredUnknownProperties;

	private SchedulerContext schedulerContext;


	/**
	 * Specify the unknown properties (not found in the bean) that should be ignored.
	 * <p>Default is {@code null}, indicating that all unknown properties
	 * should be ignored. Specify an empty array to throw an exception in case
	 * of any unknown properties, or a list of property names that should be
	 * ignored if there is no corresponding property found on the particular
	 * job class (all other unknown properties will still trigger an exception).
	 * <p>
	 * 指定应该忽略的未知属性(未在bean中找到)<p>默认值为{@code null},表示应忽略所有未知属性指定一个空数组,以便在任何未知属性的情况下抛出异常,或如果在特定作业类上没有找到相应的属性,则应
	 * 忽略的属性名称列表(所有其他未知属性仍将触发异常)。
	 * 
	 */
	public void setIgnoredUnknownProperties(String... ignoredUnknownProperties) {
		this.ignoredUnknownProperties = ignoredUnknownProperties;
	}

	@Override
	public void setSchedulerContext(SchedulerContext schedulerContext) {
		this.schedulerContext = schedulerContext;
	}


	/**
	 * Create the job instance, populating it with property values taken
	 * from the scheduler context, job data map and trigger data map.
	 * <p>
	 *  创建作业实例,使用从调度程序上下文,作业数据映射和触发器数据映射获取的属性值进行填充
	 * 
	 */
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		Object job = super.createJobInstance(bundle);
		if (isEligibleForPropertyPopulation(job)) {
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
			MutablePropertyValues pvs = new MutablePropertyValues();
			if (this.schedulerContext != null) {
				pvs.addPropertyValues(this.schedulerContext);
			}
			pvs.addPropertyValues(bundle.getJobDetail().getJobDataMap());
			pvs.addPropertyValues(bundle.getTrigger().getJobDataMap());
			if (this.ignoredUnknownProperties != null) {
				for (String propName : this.ignoredUnknownProperties) {
					if (pvs.contains(propName) && !bw.isWritableProperty(propName)) {
						pvs.removePropertyValue(propName);
					}
				}
				bw.setPropertyValues(pvs);
			}
			else {
				bw.setPropertyValues(pvs, true);
			}
		}
		return job;
	}

	/**
	 * Return whether the given job object is eligible for having
	 * its bean properties populated.
	 * <p>The default implementation ignores {@link QuartzJobBean} instances,
	 * which will inject bean properties themselves.
	 * <p>
	 *  返回给定的作业对象是否有资格使其bean属性填充<p>默认实现忽略{@link QuartzJobBean}实例,它将注入bean属性本身
	 * 
	 * @param jobObject the job object to introspect
	 * @see QuartzJobBean
	 */
	protected boolean isEligibleForPropertyPopulation(Object jobObject) {
		return (!(jobObject instanceof QuartzJobBean));
	}

}
