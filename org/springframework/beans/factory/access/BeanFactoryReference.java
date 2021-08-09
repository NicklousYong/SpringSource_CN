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

package org.springframework.beans.factory.access;

import org.springframework.beans.factory.BeanFactory;

/**
 * Used to track a reference to a {@link BeanFactory} obtained through
 * a {@link BeanFactoryLocator}.
 *
 * <p>It is safe to call {@link #release()} multiple times, but
 * {@link #getFactory()} must not be called after calling release.
 *
 * <p>
 *  用于跟踪通过{@link BeanFactoryLocator}获取的{@link BeanFactory}的引用
 * 
 *  <p>多次调用{@link #release()}是安全的,但是在调用release之后不能调用{@link #getFactory()}
 * 
 * 
 * @author Colin Sampaleanu
 * @see BeanFactoryLocator
 * @see org.springframework.context.access.ContextBeanFactoryReference
 */
public interface BeanFactoryReference {

	/**
	 * Return the {@link BeanFactory} instance held by this reference.
	 * <p>
	 * 返回此引用所持有的{@link BeanFactory}实例
	 * 
	 * 
	 * @throws IllegalStateException if invoked after {@code release()} has been called
	 */
	BeanFactory getFactory();

	/**
	 * Indicate that the {@link BeanFactory} instance referred to by this object is not
	 * needed any longer by the client code which obtained the {@link BeanFactoryReference}.
	 * <p>Depending on the actual implementation of {@link BeanFactoryLocator}, and
	 * the actual type of {@code BeanFactory}, this may possibly not actually
	 * do anything; alternately in the case of a 'closeable' {@code BeanFactory}
	 * or derived class (such as {@link org.springframework.context.ApplicationContext})
	 * may 'close' it, or may 'close' it once no more references remain.
	 * <p>In an EJB usage scenario this would normally be called from
	 * {@code ejbRemove()} and {@code ejbPassivate()}.
	 * <p>This is safe to call multiple times.
	 * <p>
	 *  表明此对象引用的{@link BeanFactory}实例不再需要获得{@link BeanFactoryReference} <p>的客户端代码,具体取决于{@link BeanFactoryLocator}
	 * 的实际实现以及实际类型的{@code BeanFactory},这可能实际上不会做任何事情;在"可关闭"{@code BeanFactory}或派生类(例如{@link orgspringframeworkcontextApplicationContext}
	 * )的情况下,可以"关闭"它,或者在不再有引用的情况下可能会"关闭"。
	 * 
	 * @see BeanFactoryLocator
	 * @see org.springframework.context.access.ContextBeanFactoryReference
	 * @see org.springframework.context.ConfigurableApplicationContext#close()
	 */
	void release();

}
