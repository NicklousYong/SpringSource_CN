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

package org.springframework.aop;

import org.aopalliance.aop.Advice;

/**
 * Subinterface of AOP Alliance Advice that allows additional interfaces
 * to be implemented by an Advice, and available via a proxy using that
 * interceptor. This is a fundamental AOP concept called <b>introduction</b>.
 *
 * <p>Introductions are often <b>mixins</b>, enabling the building of composite
 * objects that can achieve many of the goals of multiple inheritance in Java.
 *
 * <p>Compared to {qlink IntroductionInfo}, this interface allows an advice to
 * implement a range of interfaces that is not necessarily known in advance.
 * Thus an {@link IntroductionAdvisor} can be used to specify which interfaces
 * will be exposed in an advised object.
 *
 * <p>
 *  AOP联盟咨询的子界面,允许通过建议实现其他接口,并通过使用该拦截器的代理可用。这是一个基本的AOP概念,称为<b>简介</b>
 * 
 * <p>介绍通常是<b> mixins </b>,使得能够构建可以实现Java中多重继承的许多目标的复合对象
 * 
 *  <p>与{qlink IntroductionInfo}相比,该接口允许建议实现一系列不一定提前知道的接口。
 * 
 * @author Rod Johnson
 * @since 1.1.1
 * @see IntroductionInfo
 * @see IntroductionAdvisor
 */
public interface DynamicIntroductionAdvice extends Advice {

	/**
	 * Does this introduction advice implement the given interface?
	 * <p>
	 * 因此,可以使用{@link IntroductionAdvisor}指定哪些接口将在建议对象中公开。
	 * 
	 * 
	 * @param intf the interface to check
	 * @return whether the advice implements the specified interface
	 */
	boolean implementsInterface(Class<?> intf);

}
