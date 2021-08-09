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

package org.springframework.scheduling.annotation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * Bean post-processor that registers methods annotated with @{@link Scheduled}
 * to be invoked by a {@link org.springframework.scheduling.TaskScheduler} according
 * to the "fixedRate", "fixedDelay", or "cron" expression provided via the annotation.
 *
 * <p>This post-processor is automatically registered by Spring's
 * {@code <task:annotation-driven>} XML element, and also by the
 * {@link EnableScheduling @EnableScheduling} annotation.
 *
 * <p>Autodetects any {@link SchedulingConfigurer} instances in the container,
 * allowing for customization of the scheduler to be used or for fine-grained
 * control over task registration (e.g. registration of {@link Trigger} tasks.
 * See the @{@link EnableScheduling} javadocs for complete usage details.
 *
 * <p>
 * Bean后处理器注册由@ {@ link Scheduled}注释的方法,由{@link orgspringframeworkschedulingTaskScheduler}根据通过注释提供的"fixe
 * dRate","fixedDelay"或"cron"表达式调用。
 * 
 *  <p>这个后处理器是由Spring的{@code <task：annotation-driven>} XML元素自动注册的,还有{@link EnableScheduling @EnableScheduling}
 * 注释。
 * 
 *  自动检测容器中的任何{@link SchedulingConfigurer}实例,允许自定义要使用的调度程序或对任务注册进行细粒度控制(例如注册{@link Trigger}任务)请参阅@ {@ link EnableScheduling }
 *  javadocs以获取完整的使用细节。
 * 
 * 
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Elizabeth Chatman
 * @since 3.0
 * @see Scheduled
 * @see EnableScheduling
 * @see SchedulingConfigurer
 * @see org.springframework.scheduling.TaskScheduler
 * @see org.springframework.scheduling.config.ScheduledTaskRegistrar
 * @see AsyncAnnotationBeanPostProcessor
 */
public class ScheduledAnnotationBeanPostProcessor implements DestructionAwareBeanPostProcessor,
		Ordered, EmbeddedValueResolverAware, BeanFactoryAware, ApplicationContextAware,
		SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

	/**
	 * The default name of the {@link TaskScheduler} bean to pick up: "taskScheduler".
	 * <p>Note that the initial lookup happens by type; this is just the fallback
	 * in case of multiple scheduler beans found in the context.
	 * <p>
	 * 要取出的{@link TaskScheduler} bean的默认名称为"taskScheduler"<p>请注意,初始查找按类型发生;这只是在上下文中找到多个调度程序bean的情况下的后备
	 * 
	 * 
	 * @since 4.2
	 */
	public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = "taskScheduler";


	protected final Log logger = LogFactory.getLog(getClass());

	private Object scheduler;

	private StringValueResolver embeddedValueResolver;

	private BeanFactory beanFactory;

	private ApplicationContext applicationContext;

	private final ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();

	private final Set<Class<?>> nonAnnotatedClasses =
			Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));

	private final Map<Object, Set<ScheduledTask>> scheduledTasks =
			new IdentityHashMap<Object, Set<ScheduledTask>>(16);


	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}

	/**
	 * Set the {@link org.springframework.scheduling.TaskScheduler} that will invoke
	 * the scheduled methods, or a {@link java.util.concurrent.ScheduledExecutorService}
	 * to be wrapped as a TaskScheduler.
	 * <p>If not specified, default scheduler resolution will apply: searching for a
	 * unique {@link TaskScheduler} bean in the context, or for a {@link TaskScheduler}
	 * bean named "taskScheduler" otherwise; the same lookup will also be performed for
	 * a {@link ScheduledExecutorService} bean. If neither of the two is resolvable,
	 * a local single-threaded default scheduler will be created within the registrar.
	 * <p>
	 * 设置将调用预定方法的{@link orgspringframeworkschedulingTaskScheduler},或者将{@link javautilconcurrentScheduledExecutorService}
	 * 包装为TaskScheduler <p>如果未指定,将应用默认调度程序分辨率：搜索唯一的{@link TaskScheduler} bean或者对于名为"taskScheduler"的{@link TaskScheduler}
	 *  bean;对于{@link ScheduledExecutorService} bean也将执行相同的查找。
	 * 如果两者都不可解析,则会在注册器中创建本地单线程默认调度程序。
	 * 
	 * 
	 * @see #DEFAULT_TASK_SCHEDULER_BEAN_NAME
	 */
	public void setScheduler(Object scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.embeddedValueResolver = resolver;
	}

	/**
	 * Making a {@link BeanFactory} available is optional; if not set,
	 * {@link SchedulingConfigurer} beans won't get autodetected and
	 * a {@link #setScheduler scheduler} has to be explicitly configured.
	 * <p>
	 *  制作{@link BeanFactory}是可选的;如果未设置,{@link SchedulingConfigurer} bean将不会自动检测,并且必须显式配置{@link #setScheduler调度程序}
	 * 。
	 * 
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Setting an {@link ApplicationContext} is optional: If set, registered
	 * tasks will be activated in the {@link ContextRefreshedEvent} phase;
	 * if not set, it will happen at {@link #afterSingletonsInstantiated} time.
	 * <p>
	 * 设置{@link ApplicationContext}是可选的：如果设置,注册的任务将在{@link ContextRefreshedEvent}阶段激活;如果没有设置,它将发生在{@link #afterSingletonsInstantiated}
	 * 时间。
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		if (this.beanFactory == null) {
			this.beanFactory = applicationContext;
		}
	}


	@Override
	public void afterSingletonsInstantiated() {
		if (this.applicationContext == null) {
			// Not running in an ApplicationContext -> register tasks early...
			finishRegistration();
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext() == this.applicationContext) {
			// Running in an ApplicationContext -> register tasks this late...
			// giving other ContextRefreshedEvent listeners a chance to perform
			// their work at the same time (e.g. Spring Batch's job registration).
			finishRegistration();
		}
	}

	private void finishRegistration() {
		if (this.scheduler != null) {
			this.registrar.setScheduler(this.scheduler);
		}

		if (this.beanFactory instanceof ListableBeanFactory) {
			Map<String, SchedulingConfigurer> configurers =
					((ListableBeanFactory) this.beanFactory).getBeansOfType(SchedulingConfigurer.class);
			for (SchedulingConfigurer configurer : configurers.values()) {
				configurer.configureTasks(this.registrar);
			}
		}

		if (this.registrar.hasTasks() && this.registrar.getScheduler() == null) {
			Assert.state(this.beanFactory != null, "BeanFactory must be set to find scheduler by type");
			try {
				// Search for TaskScheduler bean...
				this.registrar.setTaskScheduler(this.beanFactory.getBean(TaskScheduler.class));
			}
			catch (NoUniqueBeanDefinitionException ex) {
				try {
					this.registrar.setTaskScheduler(
							this.beanFactory.getBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, TaskScheduler.class));
				}
				catch (NoSuchBeanDefinitionException ex2) {
					if (logger.isInfoEnabled()) {
						logger.info("More than one TaskScheduler bean exists within the context, and " +
								"none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' " +
								"(possibly as an alias); or implement the SchedulingConfigurer interface and call " +
								"ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " +
								ex.getBeanNamesFound());
					}
				}
			}
			catch (NoSuchBeanDefinitionException ex) {
				logger.debug("Could not find default TaskScheduler bean", ex);
				// Search for ScheduledExecutorService bean next...
				try {
					this.registrar.setScheduler(this.beanFactory.getBean(ScheduledExecutorService.class));
				}
				catch (NoUniqueBeanDefinitionException ex2) {
					try {
						this.registrar.setScheduler(
								this.beanFactory.getBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, ScheduledExecutorService.class));
					}
					catch (NoSuchBeanDefinitionException ex3) {
						if (logger.isInfoEnabled()) {
							logger.info("More than one ScheduledExecutorService bean exists within the context, and " +
									"none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' " +
									"(possibly as an alias); or implement the SchedulingConfigurer interface and call " +
									"ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " +
									ex2.getBeanNamesFound());
						}
					}
				}
				catch (NoSuchBeanDefinitionException ex2) {
					logger.debug("Could not find default ScheduledExecutorService bean", ex2);
					// Giving up -> falling back to default scheduler within the registrar...
					logger.info("No TaskScheduler/ScheduledExecutorService bean found for scheduled processing");
				}
			}
		}

		this.registrar.afterPropertiesSet();
	}


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, String beanName) {
		Class<?> targetClass = AopUtils.getTargetClass(bean);
		if (!this.nonAnnotatedClasses.contains(targetClass)) {
			Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
					new MethodIntrospector.MetadataLookup<Set<Scheduled>>() {
						@Override
						public Set<Scheduled> inspect(Method method) {
							Set<Scheduled> scheduledMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(
									method, Scheduled.class, Schedules.class);
							return (!scheduledMethods.isEmpty() ? scheduledMethods : null);
						}
					});
			if (annotatedMethods.isEmpty()) {
				this.nonAnnotatedClasses.add(targetClass);
				if (logger.isTraceEnabled()) {
					logger.trace("No @Scheduled annotations found on bean class: " + bean.getClass());
				}
			}
			else {
				// Non-empty set of methods
				for (Map.Entry<Method, Set<Scheduled>> entry : annotatedMethods.entrySet()) {
					Method method = entry.getKey();
					for (Scheduled scheduled : entry.getValue()) {
						processScheduled(scheduled, method, bean);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName +
							"': " + annotatedMethods);
				}
			}
		}
		return bean;
	}

	protected void processScheduled(Scheduled scheduled, Method method, Object bean) {
		try {
			Assert.isTrue(method.getParameterTypes().length == 0,
					"Only no-arg methods may be annotated with @Scheduled");

			Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
			Runnable runnable = new ScheduledMethodRunnable(bean, invocableMethod);
			boolean processedSchedule = false;
			String errorMessage =
					"Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";

			Set<ScheduledTask> tasks = new LinkedHashSet<ScheduledTask>(4);

			// Determine initial delay
			long initialDelay = scheduled.initialDelay();
			String initialDelayString = scheduled.initialDelayString();
			if (StringUtils.hasText(initialDelayString)) {
				Assert.isTrue(initialDelay < 0, "Specify 'initialDelay' or 'initialDelayString', not both");
				if (this.embeddedValueResolver != null) {
					initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
				}
				try {
					initialDelay = Long.parseLong(initialDelayString);
				}
				catch (NumberFormatException ex) {
					throw new IllegalArgumentException(
							"Invalid initialDelayString value \"" + initialDelayString + "\" - cannot parse into integer");
				}
			}

			// Check cron expression
			String cron = scheduled.cron();
			if (StringUtils.hasText(cron)) {
				Assert.isTrue(initialDelay == -1, "'initialDelay' not supported for cron triggers");
				processedSchedule = true;
				String zone = scheduled.zone();
				if (this.embeddedValueResolver != null) {
					cron = this.embeddedValueResolver.resolveStringValue(cron);
					zone = this.embeddedValueResolver.resolveStringValue(zone);
				}
				TimeZone timeZone;
				if (StringUtils.hasText(zone)) {
					timeZone = StringUtils.parseTimeZoneString(zone);
				}
				else {
					timeZone = TimeZone.getDefault();
				}
				tasks.add(this.registrar.scheduleCronTask(new CronTask(runnable, new CronTrigger(cron, timeZone))));
			}

			// At this point we don't need to differentiate between initial delay set or not anymore
			if (initialDelay < 0) {
				initialDelay = 0;
			}

			// Check fixed delay
			long fixedDelay = scheduled.fixedDelay();
			if (fixedDelay >= 0) {
				Assert.isTrue(!processedSchedule, errorMessage);
				processedSchedule = true;
				tasks.add(this.registrar.scheduleFixedDelayTask(new IntervalTask(runnable, fixedDelay, initialDelay)));
			}
			String fixedDelayString = scheduled.fixedDelayString();
			if (StringUtils.hasText(fixedDelayString)) {
				Assert.isTrue(!processedSchedule, errorMessage);
				processedSchedule = true;
				if (this.embeddedValueResolver != null) {
					fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
				}
				try {
					fixedDelay = Long.parseLong(fixedDelayString);
				}
				catch (NumberFormatException ex) {
					throw new IllegalArgumentException(
							"Invalid fixedDelayString value \"" + fixedDelayString + "\" - cannot parse into integer");
				}
				tasks.add(this.registrar.scheduleFixedDelayTask(new IntervalTask(runnable, fixedDelay, initialDelay)));
			}

			// Check fixed rate
			long fixedRate = scheduled.fixedRate();
			if (fixedRate >= 0) {
				Assert.isTrue(!processedSchedule, errorMessage);
				processedSchedule = true;
				tasks.add(this.registrar.scheduleFixedRateTask(new IntervalTask(runnable, fixedRate, initialDelay)));
			}
			String fixedRateString = scheduled.fixedRateString();
			if (StringUtils.hasText(fixedRateString)) {
				Assert.isTrue(!processedSchedule, errorMessage);
				processedSchedule = true;
				if (this.embeddedValueResolver != null) {
					fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
				}
				try {
					fixedRate = Long.parseLong(fixedRateString);
				}
				catch (NumberFormatException ex) {
					throw new IllegalArgumentException(
							"Invalid fixedRateString value \"" + fixedRateString + "\" - cannot parse into integer");
				}
				tasks.add(this.registrar.scheduleFixedRateTask(new IntervalTask(runnable, fixedRate, initialDelay)));
			}

			// Check whether we had any attribute set
			Assert.isTrue(processedSchedule, errorMessage);

			// Finally register the scheduled tasks
			synchronized (this.scheduledTasks) {
				Set<ScheduledTask> registeredTasks = this.scheduledTasks.get(bean);
				if (registeredTasks == null) {
					registeredTasks = new LinkedHashSet<ScheduledTask>(4);
					this.scheduledTasks.put(bean, registeredTasks);
				}
				registeredTasks.addAll(tasks);
			}
		}
		catch (IllegalArgumentException ex) {
			throw new IllegalStateException(
					"Encountered invalid @Scheduled method '" + method.getName() + "': " + ex.getMessage());
		}
	}


	@Override
	public void postProcessBeforeDestruction(Object bean, String beanName) {
		Set<ScheduledTask> tasks;
		synchronized (this.scheduledTasks) {
			tasks = this.scheduledTasks.remove(bean);
		}
		if (tasks != null) {
			for (ScheduledTask task : tasks) {
				task.cancel();
			}
		}
	}

	@Override
	public boolean requiresDestruction(Object bean) {
		synchronized (this.scheduledTasks) {
			return this.scheduledTasks.containsKey(bean);
		}
	}

	@Override
	public void destroy() {
		synchronized (this.scheduledTasks) {
			Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
			for (Set<ScheduledTask> tasks : allTasks) {
				for (ScheduledTask task : tasks) {
					task.cancel();
				}
			}
			this.scheduledTasks.clear();
		}
		this.registrar.destroy();
	}

}
