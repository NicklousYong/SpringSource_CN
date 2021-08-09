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

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

/**
 * Context information for use by {@link Condition}s.
 *
 * <p>
 *  {@link条件}使用的上下文信息
 * 
 * 
 * @author Phillip Webb
 * @since 4.0
 */
public interface ConditionContext {

	/**
	 * Return the {@link BeanDefinitionRegistry} that will hold the bean definition
	 * should the condition match or {@code null} if the registry is not available.
	 * <p>
	 *  如果注册表不可用,则返回{@link BeanDefinitionRegistry},如果该条件匹配,则将保留该bean定义,或{@code null}
	 * 
	 * 
	 * @return the registry or {@code null}
	 */
	BeanDefinitionRegistry getRegistry();

	/**
	 * Return the {@link ConfigurableListableBeanFactory} that will hold the bean
	 * definition should the condition match or {@code null} if the bean factory
	 * is not available.
	 * <p>
	 * 返回{@link ConfigurableListableBeanFactory},如果bean工厂不可用,则该条件匹配时将保存bean定义,或{@code null}
	 * 
	 * 
	 * @return the bean factory or {@code null}
	 */
	ConfigurableListableBeanFactory getBeanFactory();

	/**
	 * Return the {@link Environment} for which the current application is running
	 * or {@code null} if no environment is available.
	 * <p>
	 *  如果没有环境可用,返回当前应用程序正在运行的{@link环境}或{@code null}
	 * 
	 * 
	 * @return the environment or {@code null}
	 */
	Environment getEnvironment();

	/**
	 * Return the {@link ResourceLoader} currently being used or {@code null}
	 * if the resource loader cannot be obtained.
	 * <p>
	 *  如果无法获取资源加载器,则返回当前正在使用的{@link ResourceLoader}或{@code null}
	 * 
	 * 
	 * @return a resource loader or {@code null}
	 */
	ResourceLoader getResourceLoader();

	/**
	 * Return the {@link ClassLoader} that should be used to load additional
	 * classes or {@code null} if the default classloader should be used.
	 * <p>
	 *  如果使用默认的类加载器,则返回应用于加载其他类的{@link ClassLoader}或{@code null}
	 * 
	 * @return the class loader or {@code null}
	 */
	ClassLoader getClassLoader();

}
