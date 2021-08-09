/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

package org.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;

/**
 * This interface represents a generic runtime joinpoint (in the AOP
 * terminology).
 *
 * <p>A runtime joinpoint is an <i>event</i> that occurs on a static
 * joinpoint (i.e. a location in a the program). For instance, an
 * invocation is the runtime joinpoint on a method (static joinpoint).
 * The static part of a given joinpoint can be generically retrieved
 * using the {@link #getStaticPart()} method.
 *
 * <p>In the context of an interception framework, a runtime joinpoint
 * is then the reification of an access to an accessible object (a
 * method, a constructor, a field), i.e. the static part of the
 * joinpoint. It is passed to the interceptors that are installed on
 * the static joinpoint.
 *
 * <p>
 *  此接口表示通用运行时连接点(在AOP术语中)
 * 
 * <p>运行时连接点是发生在静态连接点(即程序中的位置)的<i>事件</i>例如,调用是方法(静态连接点)上的运行时连接点静态部分可以使用{@link #getStaticPart()}方法一般地检索给定
 * 的连接点。
 * 
 *  在拦截框架的上下文中,运行时连接点是对可访问对象(方法,构造函数,字段)的访问的定义,即连接点的静态部分它被传递给拦截器安装在静态连接点上
 * 
 * 
 * @author Rod Johnson
 * @see Interceptor
 */
public interface Joinpoint {

	/**
	 * Proceed to the next interceptor in the chain.
	 * <p>The implementation and the semantics of this method depends
	 * on the actual joinpoint type (see the children interfaces).
	 * <p>
	 *  继续到链中的下一个拦截器<p>此方法的实现和语义取决于实际的连接点类型(请参阅children接口)
	 * 
	 * 
	 * @return see the children interfaces' proceed definition
	 * @throws Throwable if the joinpoint throws an exception
	 */
	Object proceed() throws Throwable;

	/**
	 * Return the object that holds the current joinpoint's static part.
	 * <p>For instance, the target object for an invocation.
	 * <p>
	 * 返回保存当前连接点静态部分<p>的对象,例如调用的目标对象
	 * 
	 * 
	 * @return the object (can be null if the accessible object is static)
	 */
	Object getThis();

	/**
	 * Return the static part of this joinpoint.
	 * <p>The static part is an accessible object on which a chain of
	 * interceptors are installed.
	 * <p>
	 *  返回此连接点的静态部分<p>静态部分是一个可访问对象,在其上安装了一系列拦截器
	 */
	AccessibleObject getStaticPart();

}
