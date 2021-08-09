/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.context;

/**
 * A common interface defining methods for start/stop lifecycle control.
 * The typical use case for this is to control asynchronous processing.
 * <b>NOTE: This interface does not imply specific auto-startup semantics.
 * Consider implementing {@link SmartLifecycle} for that purpose.</b>
 *
 * <p>Can be implemented by both components (typically a Spring bean defined in a
 * Spring context) and containers  (typically a Spring {@link ApplicationContext}
 * itself). Containers will propagate start/stop signals to all components that
 * apply within each container, e.g. for a stop/restart scenario at runtime.
 *
 * <p>Can be used for direct invocations or for management operations via JMX.
 * In the latter case, the {@link org.springframework.jmx.export.MBeanExporter}
 * will typically be defined with an
 * {@link org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler},
 * restricting the visibility of activity-controlled components to the Lifecycle
 * interface.
 *
 * <p>Note that the Lifecycle interface is only supported on <b>top-level singleton
 * beans</b>. On any other component, the Lifecycle interface will remain undetected
 * and hence ignored. Also, note that the extended {@link SmartLifecycle} interface
 * provides integration with the application context's startup and shutdown phases.
 *
 * <p>
 * 用于启动/停止生命周期控制的通用接口定义用于启动/停止生命周期控制的方法典型的用例是控制异步处理<b>注意：此接口并不意味着特定的自动启动语义考虑为此实现{@link SmartLifecycle} </b >
 * 。
 * 
 *  <p>可以由两个组件(通常在Spring上下文中定义的Spring Bean)和容器(通常是Spring {@link ApplicationContext}本身)实现)容器将将启动/停止信号传播到在
 * 每个容器中应用的所有组件,例如在运行时停止/重新启动场景。
 * 
 * <p>可以用于直接调用或通过JMX进行管理操作在后一种情况下,{@link orgspringframeworkjmxexportMBeanExporter}通常将使用{@link orgspringframeworkjmxexportassemblerInterfaceBasedMBeanInfoAssembler}
 * 定义,将活动控制组件的可见性限制为Lifecycle界面。
 * 
 *  <p>请注意,仅在<b>顶级单例Bean上支持Lifecycle界面</b>在任何其他组件上,Lifecycle界面将保持不被检测,因此被忽略另外,请注意,扩展的{@link SmartLifecycle}
 * 界面提供与应用程序上下文的启动和关闭阶段的集成。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see SmartLifecycle
 * @see ConfigurableApplicationContext
 * @see org.springframework.jms.listener.AbstractMessageListenerContainer
 * @see org.springframework.scheduling.quartz.SchedulerFactoryBean
 */
public interface Lifecycle {

	/**
	 * Start this component.
	 * <p>Should not throw an exception if the component is already running.
	 * <p>In the case of a container, this will propagate the start signal to all
	 * components that apply.
	 * <p>
	 * 
	 * @see SmartLifecycle#isAutoStartup()
	 */
	void start();

	/**
	 * Stop this component, typically in a synchronous fashion, such that the component is
	 * fully stopped upon return of this method. Consider implementing {@link SmartLifecycle}
	 * and its {@code stop(Runnable)} variant when asynchronous stop behavior is necessary.
	 * <p>Note that this stop notification is not guaranteed to come before destruction: On
	 * regular shutdown, {@code Lifecycle} beans will first receive a stop notification before
	 * the general destruction callbacks are being propagated; however, on hot refresh during a
	 * context's lifetime or on aborted refresh attempts, only destroy methods will be called.
	 * <p>Should not throw an exception if the component isn't started yet.
	 * <p>In the case of a container, this will propagate the stop signal to all components
	 * that apply.
	 * <p>
	 * 启动此组件<p>如果组件已经运行,则不应抛出异常<p>在容器的情况下,这将会将启动信号传播到所有应用的组件
	 * 
	 * 
	 * @see SmartLifecycle#stop(Runnable)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	void stop();

	/**
	 * Check whether this component is currently running.
	 * <p>In the case of a container, this will return {@code true} only if <i>all</i>
	 * components that apply are currently running.
	 * <p>
	 * 通常以同步方式停止此组件,以使组件在返回此方法后完全停止在需要异步停止行为时,请考虑实现{@link SmartLifecycle}及其{@code stop(Runnable)}变体<p>注意这个停止
	 * 通知不能保证在销毁之前发生：在常规关机时,{@code Lifecycle} bean将在广泛的销毁回调传播之前首先收到一个停止通知;然而,在上下文生命周期的热刷新或者中止刷新尝试时,只有破坏方法将被调
	 * 用<p>如果组件未启动,则不应抛出异常<p>在容器的情况下,这将传播对所有应用的组件的停止信号。
	 * 
	 * 
	 * @return whether the component is currently running
	 */
	boolean isRunning();

}
