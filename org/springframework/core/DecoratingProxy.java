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

package org.springframework.core;

/**
 * Interface to be implemented by decorating proxies, in particular Spring AOP
 * proxies but potentially also custom proxies with decorator semantics.
 *
 * <p>Note that this interface should just be implemented if the decorated class
 * is not within the hierarchy of the proxy class to begin with. In particular,
 * a "target-class" proxy such as a Spring AOP CGLIB proxy should not implement
 * it since any lookup on the target class can simply be performed on the proxy
 * class there anyway.
 *
 * <p>Defined in the core module in order to allow
 * #{@link org.springframework.core.annotation.AnnotationAwareOrderComparator}
 * (and potential other candidates without spring-aop dependencies) to use it
 * for introspection purposes, in particular annotation lookups.
 *
 * <p>
 *  通过装饰代理来实现的界面,特别是Spring AOP代理,但也可能具有装饰语义的定制代理
 * 
 * 注意,如果装饰的类不在代理类的层次结构中开始,那么这个接口应该被实现。
 * 特别地,诸如Spring AOP CGLIB代理之类的"target-class"代理不应该实现它目标类的查找可以简单地在代理类上执行。
 * 
 *  <p>在核心模块中定义,以允许#{@ link orgspringframeworkcoreannotationAnnotationAwareOrderComparator}(以及没有spring-a
 * 
 * @author Juergen Hoeller
 * @since 4.3
 */
public interface DecoratingProxy {

	/**
	 * Return the (ultimate) decorated class behind this proxy.
	 * <p>In case of an AOP proxy, this will be the ultimate target class,
	 * not just the immediate target (in case of multiple nested proxies).
	 * <p>
	 * op依赖关系的潜在的其他候选者)将其用于内省目的,特别是注释查找。
	 * 
	 * 
	 * @return the decorated class (never {@code null})
	 */
	Class<?> getDecoratedClass();

}
