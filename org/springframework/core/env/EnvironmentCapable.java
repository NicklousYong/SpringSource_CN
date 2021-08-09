/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.core.env;

/**
 * Interface indicating a component that contains and exposes an {@link Environment} reference.
 *
 * <p>All Spring application contexts are EnvironmentCapable, and the interface is used primarily
 * for performing {@code instanceof} checks in framework methods that accept BeanFactory
 * instances that may or may not actually be ApplicationContext instances in order to interact
 * with the environment if indeed it is available.
 *
 * <p>As mentioned, {@link org.springframework.context.ApplicationContext ApplicationContext}
 * extends EnvironmentCapable, and thus exposes a {@link #getEnvironment()} method; however,
 * {@link org.springframework.context.ConfigurableApplicationContext ConfigurableApplicationContext}
 * redefines {@link org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * getEnvironment()} and narrows the signature to return a {@link ConfigurableEnvironment}.
 * The effect is that an Environment object is 'read-only' until it is being accessed from
 * a ConfigurableApplicationContext, at which point it too may be configured.
 *
 * <p>
 *  指示包含并公开{@link Environment}引用的组件的接口
 * 
 * 所有Spring应用程序上下文都是EnvironmentCapable,并且该接口主要用于在框架方法中执行{@code instanceof}检查,框架方法接受BeanFactory实例,实例可能实际上
 * 也可能不是ApplicationContext实例,以便与环境进行交互是可用的。
 * 
 * 如上所述,{@link orgspringframeworkcontextApplicationContext ApplicationContext}扩展了EnvironmentCapable,从而暴露
 * 了一个{@link #getEnvironment()}方法;然而,{@link orgspringframeworkcontextConfigurableApplicationContext ConfigurableApplicationContext}
 * 
 * @author Chris Beams
 * @since 3.1
 * @see Environment
 * @see ConfigurableEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment()
 */
public interface EnvironmentCapable {

	/**
	 * Return the {@link Environment} associated with this component.
	 * <p>
	 * 重新定义{@link orgspringframeworkcontextConfigurableApplicationContext#getEnvironment getEnvironment()}并缩
	 * 小签名以返回{@link ConfigurableEnvironment}效果是环境对象是"只读",直到被访问为止一个ConfigurableApplicationContext,此时也可以配置它。
	 * 
	 */
	Environment getEnvironment();

}
