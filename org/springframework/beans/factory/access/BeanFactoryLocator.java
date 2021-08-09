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

package org.springframework.beans.factory.access;

import org.springframework.beans.BeansException;

/**
 * Defines a contract for the lookup, use, and release of a
 * {@link org.springframework.beans.factory.BeanFactory},
 * or a {@code BeanFactory} subclass such as an
 * {@link org.springframework.context.ApplicationContext}.
 *
 * <p>Where this interface is implemented as a singleton class such as
 * {@link SingletonBeanFactoryLocator}, the Spring team <strong>strongly</strong>
 * suggests that it be used sparingly and with caution. By far the vast majority
 * of the code inside an application is best written in a Dependency Injection
 * style, where that code is served out of a
 * {@code BeanFactory}/{@code ApplicationContext} container, and has
 * its own dependencies supplied by the container when it is created. However,
 * even such a singleton implementation sometimes has its use in the small glue
 * layers of code that is sometimes needed to tie other code together. For
 * example, third party code may try to construct new objects directly, without
 * the ability to force it to get these objects out of a {@code BeanFactory}.
 * If the object constructed by the third party code is just a small stub or
 * proxy, which then uses an implementation of this class to get a
 * {@code BeanFactory} from which it gets the real object, to which it
 * delegates, then proper Dependency Injection has been achieved.
 *
 * <p>As another example, in a complex J2EE app with multiple layers, with each
 * layer having its own {@code ApplicationContext} definition (in a
 * hierarchy), a class like {@code SingletonBeanFactoryLocator} may be used
 * to demand load these contexts.
 *
 * <p>
 *  定义用于查找,使用和发布{@link orgspringframeworkbeansfactoryBeanFactory}或{@code BeanFactory}子类的合同,例如{@link orgspringframeworkcontextApplicationContext}
 * 。
 * 
 * <p>如果这个接口被实现为一个单例类,例如{@link SingletonBeanFactoryLocator},Spring团队<strong>强烈地</strong>表明它被谨慎使用,并且谨慎使用到
 * 目前为止绝大多数的代码应用程序最好以依赖注入样式编写,其中代码由{@code BeanFactory} / {@ code ApplicationContext}容器提供,并且在创建容器时具有其自己的依
 * 赖关系。
 * 但是,即使是这样的单例实现有时用于代码的小胶层,有时需要将其他代码结合在一起例如,第三方代码可能会尝试直接构造新对象,而无需强制它们将这些对象从{@代码BeanFactory}如果由第三方代码构造的对象
 * 只是一个小存根或代理,然后使用该类的实现来获取一个{@code BeanFactory},从该对象获取其授权的真实对象,然后适当的依赖注入已经实现。
 * 
 * @author Colin Sampaleanu
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.context.access.DefaultLocatorFactory
 * @see org.springframework.context.ApplicationContext
 */
public interface BeanFactoryLocator {

	/**
	 * Use the {@link org.springframework.beans.factory.BeanFactory} (or derived
	 * interface such as {@link org.springframework.context.ApplicationContext})
	 * specified by the {@code factoryKey} parameter.
	 * <p>The definition is possibly loaded/created as needed.
	 * <p>
	 * 
	 * 另一个例子是,在具有多个层的复杂J2EE应用程序中,每个层都有自己的{@code ApplicationContext}定义(在层次结构中),像{@code SingletonBeanFactoryLocator}
	 * 这样的类可以用来加载这些上下文。
	 * 
	 * 
	 * @param factoryKey a resource name specifying which {@code BeanFactory} the
	 * {@code BeanFactoryLocator} must return for usage. The actual meaning of the
	 * resource name is specific to the implementation of {@code BeanFactoryLocator}.
	 * @return the {@code BeanFactory} instance, wrapped as a {@link BeanFactoryReference} object
	 * @throws BeansException if there is an error loading or accessing the {@code BeanFactory}
	 */
	BeanFactoryReference useBeanFactory(String factoryKey) throws BeansException;

}
