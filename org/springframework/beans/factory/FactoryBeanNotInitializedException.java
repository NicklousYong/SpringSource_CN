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

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

/**
 * Exception to be thrown from a FactoryBean's {@code getObject()} method
 * if the bean is not fully initialized yet, for example because it is involved
 * in a circular reference.
 *
 * <p>Note: A circular reference with a FactoryBean cannot be solved by eagerly
 * caching singleton instances like with normal beans. The reason is that
 * <i>every</i> FactoryBean needs to be fully initialized before it can
 * return the created bean, while only <i>specific</i> normal beans need
 * to be initialized - that is, if a collaborating bean actually invokes
 * them on initialization instead of just storing the reference.
 *
 * <p>
 *  如果bean尚未完全初始化,则从FactoryBean的{@code getObject()}方法抛出异常,例如因为它涉及循环引用
 * 
 * 注意：使用FactoryBean的循环引用不能通过像普通bean一样高速缓存单例实例来解决,原因是每次</i> FactoryBean需要被完全初始化才能返回创建的bean,而只有<i>具体的</i>普
 * 通bean需要初始化 - 也就是说,如果协作bean在初始化时实际调用它们,而不是仅仅存储引用。
 * 
 * 
 * @author Juergen Hoeller
 * @since 30.10.2003
 * @see FactoryBean#getObject()
 */
@SuppressWarnings("serial")
public class FactoryBeanNotInitializedException extends FatalBeanException {

	/**
	 * Create a new FactoryBeanNotInitializedException with the default message.
	 * <p>
	 *  使用默认消息创建一个新的FactoryBeanNotInitializedException
	 * 
	 */
	public FactoryBeanNotInitializedException() {
		super("FactoryBean is not fully initialized yet");
	}

	/**
	 * Create a new FactoryBeanNotInitializedException with the given message.
	 * <p>
	 *  使用给定的消息创建一个新的FactoryBeanNotInitializedException
	 * 
	 * @param msg the detail message
	 */
	public FactoryBeanNotInitializedException(String msg) {
		super(msg);
	}

}
