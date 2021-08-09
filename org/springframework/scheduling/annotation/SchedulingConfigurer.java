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

package org.springframework.scheduling.annotation;

import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Optional interface to be implemented by @{@link
 * org.springframework.context.annotation.Configuration Configuration} classes annotated
 * with @{@link EnableScheduling}. Typically used for setting a specific
 * {@link org.springframework.scheduling.TaskScheduler TaskScheduler} bean to be used when
 * executing scheduled tasks or for registering scheduled tasks in a <em>programmatic</em>
 * fashion as opposed to the <em>declarative</em> approach of using the @{@link Scheduled}
 * annotation. For example, this may be necessary when implementing {@link
 * org.springframework.scheduling.Trigger Trigger}-based tasks, which are not supported by
 * the {@code @Scheduled} annotation.
 *
 * <p>See @{@link EnableScheduling} for detailed usage examples.
 *
 * <p>
 * 通过@ {@ link EnableScheduling}注释的@ {@ link orgspringframeworkcontextannotationConfiguration Configuration}
 * 实现的可选接口通常用于设置一个特定的{@link orgspringframeworkschedulingTaskScheduler Task Bean),用于执行计划任务或将计划任务注册到< em> programmatic </em>时尚,而不是使用@ {@ link Scheduled}
 * 注释的<em>声明式</em>方法例如,在实现{@link orgspringframeworkschedulingTrigger Trigger}的任务时,这可能是必需的, {@code @Scheduled}
 * 注释不支持这些注释。
 * 
 *  <p>有关详细的使用示例,请参阅@ {@ link EnableScheduling}
 * 
 * @author Chris Beams
 * @since 3.1
 * @see EnableScheduling
 * @see ScheduledTaskRegistrar
 */
public interface SchedulingConfigurer {

	/**
	 * Callback allowing a {@link org.springframework.scheduling.TaskScheduler
	 * TaskScheduler} and specific {@link org.springframework.scheduling.config.Task Task}
	 * instances to be registered against the given the {@link ScheduledTaskRegistrar}
	 * <p>
	 * 
	 * 
	 * @param taskRegistrar the registrar to be configured.
	 */
	void configureTasks(ScheduledTaskRegistrar taskRegistrar);

}
