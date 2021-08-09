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

package org.springframework.core;

/**
 * Interface to be implemented by objects that can return information about
 * the current call stack. Useful in AOP (as in AspectJ cflow concept)
 * but not AOP-specific.
 *
 * <p>
 *  要由可返回有关当前调用堆栈的信息的对象实现的接口在AOP中有用(在AspectJ cflow概念中),但不适用于AOP
 * 
 * 
 * @author Rod Johnson
 * @since 02.02.2004
 */
public interface ControlFlow {

	/**
	 * Detect whether we're under the given class,
	 * according to the current stack trace.
	 * <p>
	 * 根据当前的堆栈跟踪,检测是否属于给定的类
	 * 
	 * 
	 * @param clazz the clazz to look for
	 */
	boolean under(Class<?> clazz);

	/**
	 * Detect whether we're under the given class and method,
	 * according to the current stack trace.
	 * <p>
	 *  根据当前的堆栈跟踪,检测是否符合给定的类和方法
	 * 
	 * 
	 * @param clazz the clazz to look for
	 * @param methodName the name of the method to look for
	 */
	boolean under(Class<?> clazz, String methodName);

	/**
	 * Detect whether the current stack trace contains the given token.
	 * <p>
	 *  检测当前堆栈跟踪是否包含给定的令牌
	 * 
	 * @param token the token to look for
	 */
	boolean underToken(String token);

}
