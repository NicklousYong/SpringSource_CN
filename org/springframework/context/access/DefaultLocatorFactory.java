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

package org.springframework.context.access;

import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.access.BeanFactoryLocator;

/**
 * A factory class to get a default ContextSingletonBeanFactoryLocator instance.
 *
 * <p>
 *  一个工厂类来获取默认的ContextSingletonBeanFactoryLocator实例
 * 
 * 
 * @author Colin Sampaleanu
 * @see org.springframework.context.access.ContextSingletonBeanFactoryLocator
 */
public class DefaultLocatorFactory {

	/**
	 * Return an instance object implementing BeanFactoryLocator. This will normally
	 * be a singleton instance of the specific ContextSingletonBeanFactoryLocator class,
	 * using the default resource selector.
	 * <p>
	 * 返回一个实现BeanFactoryLocator的实例对象这通常是一个特定的ContextSingletonBeanFactoryLocator类的单例实例,使用默认的资源选择器
	 * 
	 */
	public static BeanFactoryLocator getInstance() throws FatalBeanException {
		return ContextSingletonBeanFactoryLocator.getInstance();
	}

	/**
	 * Return an instance object implementing BeanFactoryLocator. This will normally
	 * be a singleton instance of the specific ContextSingletonBeanFactoryLocator class,
	 * using the specified resource selector.
	 * <p>
	 *  返回一个实现BeanFactoryLocator的实例对象这通常是特定ContextSingletonBeanFactoryLocator类的单例实例,使用指定的资源选择器
	 * 
	 * @param selector a selector variable which provides a hint to the factory as to
	 * which instance to return.
	 */
	public static BeanFactoryLocator getInstance(String selector) throws FatalBeanException {
		return ContextSingletonBeanFactoryLocator.getInstance(selector);
	}
}
