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

package org.springframework.beans.factory;

/**
 * Callback interface triggered at the end of the singleton pre-instantiation phase
 * during {@link BeanFactory} bootstrap. This interface can be implemented by
 * singleton beans in order to perform some initialization after the regular
 * singleton instantiation algorithm, avoiding side effects with accidental early
 * initialization (e.g. from {@link ListableBeanFactory#getBeansOfType} calls).
 * In that sense, it is an alternative to {@link InitializingBean} which gets
 * triggered right at the end of a bean's local construction phase.
 *
 * <p>This callback variant is somewhat similar to
 * {@link org.springframework.context.event.ContextRefreshedEvent} but doesn't
 * require an implementation of {@link org.springframework.context.ApplicationListener},
 * with no need to filter context references across a context hierarchy etc.
 * It also implies a more minimal dependency on just the {@code beans} package
 * and is being honored by standalone {@link ListableBeanFactory} implementations,
 * not just in an {@link org.springframework.context.ApplicationContext} environment.
 *
 * <p><b>NOTE:</b> If you intend to start/manage asynchronous tasks, preferably
 * implement {@link org.springframework.context.Lifecycle} instead which offers
 * a richer model for runtime management and allows for phased startup/shutdown.
 *
 * <p>
 * 在{@link BeanFactory}引导过程中单例预实例化阶段结束时,回调接口触发此接口可以由单例bean实现,以便在常规单例实例化算法之后执行一些初始化,避免意外早期初始化的副作用(例如来自{@link ListableBeanFactory#getBeansOfType}
 * 调用)在这个意义上,它是{@link InitializingBean}的替代,它在bean的本地构建阶段结束时被触发。
 * 
 * <p>这个回调变体有点类似于{@link orgspringframeworkcontexteventContextRefreshedEvent},但不需要实现{@link orgspringframeworkcontextApplicationListener}
 * ,而不需要跨上下文层次结构等过滤上下文引用。
 * 这也意味着对{@code beans}包,并且被独立的{@link ListableBeanFactory}实现所尊重,而不仅仅是在{@link orgspringframeworkcontextApplicationContext}
 * 环境中。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
 */
public interface SmartInitializingSingleton {

	/**
	 * Invoked right at the end of the singleton pre-instantiation phase,
	 * with a guarantee that all regular singleton beans have been created
	 * already. {@link ListableBeanFactory#getBeansOfType} calls within
	 * this method won't trigger accidental side effects during bootstrap.
	 * <p><b>NOTE:</b> This callback won't be triggered for singleton beans
	 * lazily initialized on demand after {@link BeanFactory} bootstrap,
	 * and not for any other bean scope either. Carefully use it for beans
	 * with the intended bootstrap semantics only.
	 * <p>
	 *  <p> <b>注意：</b>如果您打算启动/管理异步任务,最好实现{@link orgspringframeworkcontextLifecycle},为运行时管理提供了更丰富的模型,并允许分阶段启动
	 * /关闭。
	 * 
	 */
	void afterSingletonsInstantiated();

}
