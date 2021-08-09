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

package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the {@link ApplicationContext} that it runs in.
 *
 * <p>Implementing this interface makes sense for example when an object
 * requires access to a set of collaborating beans. Note that configuration
 * via bean references is preferable to implementing this interface just
 * for bean lookup purposes.
 *
 * <p>This interface can also be implemented if an object needs access to file
 * resources, i.e. wants to call {@code getResource}, wants to publish
 * an application event, or requires access to the MessageSource. However,
 * it is preferable to implement the more specific {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} or {@link MessageSourceAware} interface
 * in such a specific scenario.
 *
 * <p>Note that file resource dependencies can also be exposed as bean properties
 * of type {@link org.springframework.core.io.Resource}, populated via Strings
 * with automatic type conversion by the bean factory. This removes the need
 * for implementing any callback interface just for the purpose of accessing
 * a specific file resource.
 *
 * <p>{@link org.springframework.context.support.ApplicationObjectSupport} is a
 * convenience base class for application objects, implementing this interface.
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link org.springframework.beans.factory.BeanFactory BeanFactory javadocs}.
 *
 * <p>
 *  要由任何希望被通知其运行的{@link ApplicationContext}的对象来实现的接口
 * 
 * <p>实现这个接口是有道理的,例如当对象需要访问一组协作bean时注意,通过bean引用的配置比实现此接口更适合用于bean查找
 * 
 *  <p>如果对象需要访问文件资源,即想要调用{@code getResource},想要发布应用程序事件,或者需要访问MessageSource,那么也可以实现此接口。
 * 但是,最好实现更多特定的{@link ResourceLoaderAware},{@link ApplicationEventPublisherAware}或{@link MessageSourceAware}
 * 界面。
 *  <p>如果对象需要访问文件资源,即想要调用{@code getResource},想要发布应用程序事件,或者需要访问MessageSource,那么也可以实现此接口。
 * 
 * <p>请注意,文件资源依赖关系也可以作为类型为{@link orgspringframeworkcoreioResource}的bean属性公开,由bean工厂自动类型转换通过字符串填充。
 * 这不需要实现任何回调接口,仅用于访问特定文件资源。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @see ResourceLoaderAware
 * @see ApplicationEventPublisherAware
 * @see MessageSourceAware
 * @see org.springframework.context.support.ApplicationObjectSupport
 * @see org.springframework.beans.factory.BeanFactoryAware
 */
public interface ApplicationContextAware extends Aware {

	/**
	 * Set the ApplicationContext that this object runs in.
	 * Normally this call will be used to initialize the object.
	 * <p>Invoked after population of normal bean properties but before an init callback such
	 * as {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet()}
	 * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
	 * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
	 * {@link MessageSourceAware}, if applicable.
	 * <p>
	 * 
	 *  <p> {@ link orgspringframeworkcontextsupportApplicationObjectSupport}是应用程序对象的便利基类,实现此接口
	 * 
	 *  <p>有关所有bean生命周期方法的列表,请参阅{@link orgspringframeworkbeansfactoryBeanFactory BeanFactory javadocs}
	 * 
	 * 
	 * @param applicationContext the ApplicationContext object to be used by this object
	 * @throws ApplicationContextException in case of context initialization errors
	 * @throws BeansException if thrown by application context methods
	 * @see org.springframework.beans.factory.BeanInitializationException
	 */
	void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}
