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

import org.springframework.beans.factory.Aware;
import org.springframework.core.io.ResourceLoader;

/**
 * Interface to be implemented by any object that wishes to be notified of
 * the <b>ResourceLoader</b> (typically the ApplicationContext) that it runs in.
 * This is an alternative to a full ApplicationContext dependency via the
 * ApplicationContextAware interface.
 *
 * <p>Note that Resource dependencies can also be exposed as bean properties
 * of type Resource, populated via Strings with automatic type conversion by
 * the bean factory. This removes the need for implementing any callback
 * interface just for the purpose of accessing a specific file resource.
 *
 * <p>You typically need a ResourceLoader when your application object has
 * to access a variety of file resources whose names are calculated. A good
 * strategy is to make the object use a DefaultResourceLoader but still
 * implement ResourceLoaderAware to allow for overriding when running in an
 * ApplicationContext. See ReloadableResourceBundleMessageSource for an example.
 *
 * <p>A passed-in ResourceLoader can also be checked for the
 * <b>ResourcePatternResolver</b> interface and cast accordingly, to be able
 * to resolve resource patterns into arrays of Resource objects. This will always
 * work when running in an ApplicationContext (the context interface extends
 * ResourcePatternResolver). Use a PathMatchingResourcePatternResolver as default.
 * See also the {@code ResourcePatternUtils.getResourcePatternResolver} method.
 *
 * <p>As alternative to a ResourcePatternResolver dependency, consider exposing
 * bean properties of type Resource array, populated via pattern Strings with
 * automatic type conversion by the bean factory.
 *
 * <p>
 * 要通过任何希望通知其运行的ResourceBoader </b>(通常为ApplicationContext)的对象来实现的接口,这是通过ApplicationContextAware接口完整的Appl
 * icationContext依赖项的替代方法。
 * 
 *  <p>请注意,资源依赖关系也可以作为资源类型的bean属性公开,通过使用bean工厂进行自动类型转换的字符串填充。这消除了为了访问特定文件资源而实现任何回调接口的需要
 * 
 * 当您的应用程序对象必须访问名称被计算的各种文件资源时,通常需要一个ResourceLoader。
 * 一个好的策略是使对象使用DefaultResourceLoader,但仍然实现ResourceLoaderAware,以便在ApplicationContext中运行时允许重写。
 * 请参阅ReloadableResourceBundleMessageSource举个例子。
 * 
 * <p>还可以检查一个传入的ResourceLoader以获取<b> ResourcePatternResolver </b>接口,并相应地进行转换,以便将资源模式解析为资源对象的数组。
 * 在ApplicationContext(上下文接口扩展ResourcePatternResolver)将PathMatchingResourcePatternResolver用作默认值另请参见{@code ResourcePatternUtilsgetResourcePatternResolver}
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 10.03.2004
 * @see ApplicationContextAware
 * @see org.springframework.beans.factory.InitializingBean
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.core.io.support.ResourcePatternUtils#getResourcePatternResolver
 * @see org.springframework.core.io.DefaultResourceLoader
 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
 * @see org.springframework.context.support.ReloadableResourceBundleMessageSource
 */
public interface ResourceLoaderAware extends Aware {

	/**
	 * Set the ResourceLoader that this object runs in.
	 * <p>This might be a ResourcePatternResolver, which can be checked
	 * through {@code instanceof ResourcePatternResolver}. See also the
	 * {@code ResourcePatternUtils.getResourcePatternResolver} method.
	 * <p>Invoked after population of normal bean properties but before an init callback
	 * like InitializingBean's {@code afterPropertiesSet} or a custom init-method.
	 * Invoked before ApplicationContextAware's {@code setApplicationContext}.
	 * <p>
	 * 方法。
	 * <p>还可以检查一个传入的ResourceLoader以获取<b> ResourcePatternResolver </b>接口,并相应地进行转换,以便将资源模式解析为资源对象的数组。
	 * 
	 *  <p>作为ResourcePatternResolver依赖关系的替代方法,请考虑通过bean工厂自动转换类型,通过模式字符串填充资源数组类型的bean属性
	 * 
	 * 
	 * @param resourceLoader ResourceLoader object to be used by this object
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see org.springframework.core.io.support.ResourcePatternUtils#getResourcePatternResolver
	 */
	void setResourceLoader(ResourceLoader resourceLoader);

}
