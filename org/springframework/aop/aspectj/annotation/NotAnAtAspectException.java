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

package org.springframework.aop.aspectj.annotation;

import org.springframework.aop.framework.AopConfigException;

/**
 * Extension of AopConfigException thrown when trying to perform
 * an advisor generation operation on a class that is not an
 * AspectJ annotation-style aspect.
 *
 * <p>
 *  尝试在不是AspectJ注释式方面的类上执行顾问程序生成操作时抛出的AopConfigException扩展
 * 
 * 
 * @author Rod Johnson
 * @since 2.0
 */
@SuppressWarnings("serial")
public class NotAnAtAspectException extends AopConfigException {

	private Class<?> nonAspectClass;


	/**
	 * Create a new NotAnAtAspectException for the given class.
	 * <p>
	 *  为给定的类创建一个新的NotAnAtAspectException
	 * 
	 * 
	 * @param nonAspectClass the offending class
	 */
	public NotAnAtAspectException(Class<?> nonAspectClass) {
		super(nonAspectClass.getName() + " is not an @AspectJ aspect");
		this.nonAspectClass = nonAspectClass;
	}

	/**
	 * Returns the offending class.
	 * <p>
	 * 返回违规类
	 */
	public Class<?> getNonAspectClass() {
		return this.nonAspectClass;
	}

}
