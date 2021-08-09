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
 * An extension of the {@link Lifecycle} interface for those objects that require to
 * be started upon ApplicationContext refresh and/or shutdown in a particular order.
 * The {@link #isAutoStartup()} return value indicates whether this object should
 * be started at the time of a context refresh. The callback-accepting
 * {@link #stop(Runnable)} method is useful for objects that have an asynchronous
 * shutdown process. Any implementation of this interface <i>must</i> invoke the
 * callback's run() method upon shutdown completion to avoid unnecessary delays
 * in the overall ApplicationContext shutdown.
 *
 * <p>This interface extends {@link Phased}, and the {@link #getPhase()} method's
 * return value indicates the phase within which this Lifecycle component should
 * be started and stopped. The startup process begins with the <i>lowest</i>
 * phase value and ends with the <i>highest</i> phase value (Integer.MIN_VALUE
 * is the lowest possible, and Integer.MAX_VALUE is the highest possible). The
 * shutdown process will apply the reverse order. Any components with the
 * same value will be arbitrarily ordered within the same phase.
 *
 * <p>Example: if component B depends on component A having already started, then
 * component A should have a lower phase value than component B. During the
 * shutdown process, component B would be stopped before component A.
 *
 * <p>Any explicit "depends-on" relationship will take precedence over
 * the phase order such that the dependent bean always starts after its
 * dependency and always stops before its dependency.
 *
 * <p>Any Lifecycle components within the context that do not also implement
 * SmartLifecycle will be treated as if they have a phase value of 0. That
 * way a SmartLifecycle implementation may start before those Lifecycle
 * components if it has a negative phase value, or it may start after
 * those components if it has a positive phase value.
 *
 * <p>Note that, due to the auto-startup support in SmartLifecycle,
 * a SmartLifecycle bean instance will get initialized on startup of the
 * application context in any case. As a consequence, the bean definition
 * lazy-init flag has very limited actual effect on SmartLifecycle beans.
 *
 * <p>
 * 对于那些需要在ApplicationContext刷新和/或以特定顺序关闭时启动的对象的{@link Lifecycle}接口的扩展{@link #isAutoStartup()}返回值指示是否该对象应
 * 该在当时启动的上下文刷新回调接受{@link #stop(Runnable)}方法对于具有异步关闭进程的对象很有用此接口的任何实现<i>必须在关闭后调用回调的run()方法完成以避免在整个Applica
 * tionContext关闭时出现不必要的延迟。
 * 
 * <p>此接口扩展了{@link Phased},{@link #getPhase()}方法的返回值表示此Lifecycle组件应启动和停止的阶段启动过程以<i>最低< i>相位值,并以最高相位值结束(I
 * ntegerMIN_VALUE为最低可能,IntegerMAX_VALUE为最高)关闭过程将以相反的顺序应用具有相同值的任何组件将被任意排序在同一个阶段。
 * 
 *  <p>示例：如果组件B依赖于已经启动的组件A,则组件A应具有低于组件B的相位值。在关闭过程中,组件B将在组件A之前停止
 * 
 * <p>任何明确的"依赖"关系将优先于相位顺序,使得依赖bean始终在其依赖之后启动,并在其依赖之前始终停止
 * 
 *  <p>上下文中还没有实现SmartLifecycle的任何生命周期组件将被视为具有相位值为0的方式。
 * SmartLifecycle实现可以在这些Lifecycle组件之前启动,如果它具有负相位值,或者可能开始在这些组件之后,如果它具有正相位值。
 * 
 * @author Mark Fisher
 * @since 3.0
 * @see LifecycleProcessor
 * @see ConfigurableApplicationContext
 */
public interface SmartLifecycle extends Lifecycle, Phased {

	/**
	 * Returns {@code true} if this {@code Lifecycle} component should get
	 * started automatically by the container at the time that the containing
	 * {@link ApplicationContext} gets refreshed.
	 * <p>A value of {@code false} indicates that the component is intended to
	 * be started through an explicit {@link #start()} call instead, analogous
	 * to a plain {@link Lifecycle} implementation.
	 * <p>
	 * 
	 *  请注意,由于SmartLifecycle中的自动启动支持,SmartLifecycle bean实例将在应用程序上下文的启动时得到初始化。
	 * 因此,bean定义lazy-init标志对SmartLifecycle的实际效果非常有限豆。
	 * 
	 * 
	 * @see #start()
	 * @see #getPhase()
	 * @see LifecycleProcessor#onRefresh()
	 * @see ConfigurableApplicationContext#refresh()
	 */
	boolean isAutoStartup();

	/**
	 * Indicates that a Lifecycle component must stop if it is currently running.
	 * <p>The provided callback is used by the {@link LifecycleProcessor} to support
	 * an ordered, and potentially concurrent, shutdown of all components having a
	 * common shutdown order value. The callback <b>must</b> be executed after
	 * the {@code SmartLifecycle} component does indeed stop.
	 * <p>The {@link LifecycleProcessor} will call <i>only</i> this variant of the
	 * {@code stop} method; i.e. {@link Lifecycle#stop()} will not be called for
	 * {@code SmartLifecycle} implementations unless explicitly delegated to within
	 * the implementation of this method.
	 * <p>
	 * 如果{@code Lifecycle}组件在包含{@link ApplicationContext}被刷新时由容器自动启动,返回{@code true} <p> {@code false}的值表示组件是
	 * 打算通过明确的{@link #start()}调用开始,类似于一个简单的{@link生命周期}实现。
	 * 
	 * 
	 * @see #stop()
	 * @see #getPhase()
	 */
	void stop(Runnable callback);

}
