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
 * Base interface holding AOP <b>advice</b> (action to take at a joinpoint)
 * and a filter determining the applicability of the advice (such as
 * a pointcut). <i>This interface is not for use by Spring users, but to
 * allow for commonality in support for different types of advice.</i>
 *
 * <p>Spring AOP is based around <b>around advice</b> delivered via method
 * <b>interception</b>, compliant with the AOP Alliance interception API.
 * The Advisor interface allows support for different types of advice,
 * such as <b>before</b> and <b>after</b> advice, which need not be
 * implemented using interception.
 *
 * <p>
 * 基本接口保持AOP <b>建议</b>(在连接点执行的操作)和确定建议的适用性的过滤器(如切入点)<i>此接口不适用于Spring用户,但是允许共同点支持不同类型的建议</i>
 * 
 *  <p> Spring AOP基于通过方法<b>拦截</b>提供的<b>围绕</b>,符合AOP联盟拦截API Advisor界面允许支持不同类型的建议,例如< b> before </b>和<b> a
 * fter </b> advice,这不需要使用拦截实现。
 * 
 * 
 * @author Rod Johnson
 */
public interface Advisor {

	/**
	 * Return the advice part of this aspect. An advice may be an
	 * interceptor, a before advice, a throws advice, etc.
	 * <p>
	 *  返回此方面的建议部分建议可能是拦截器,先前的建议,抛出建议等
	 * 
	 * 
	 * @return the advice that should apply if the pointcut matches
	 * @see org.aopalliance.intercept.MethodInterceptor
	 * @see BeforeAdvice
	 * @see ThrowsAdvice
	 * @see AfterReturningAdvice
	 */
	Advice getAdvice();

	/**
	 * Return whether this advice is associated with a particular instance
	 * (for example, creating a mixin) or shared with all instances of
	 * the advised class obtained from the same Spring bean factory.
	 * <p><b>Note that this method is not currently used by the framework.</b>
	 * Typical Advisor implementations always return {@code true}.
	 * Use singleton/prototype bean definitions or appropriate programmatic
	 * proxy creation to ensure that Advisors have the correct lifecycle model.
	 * <p>
	 * 返回此建议是否与特定实例相关联(例如,创建一个mixin)或与从同一个Spring bean工厂获得的建议类的所有实例共享<p> <b>请注意,此方法目前不被框架</b>典型的Advisor实现总是返回
	 * {@code true}使用单例/原型bean定义或适当的编程代理创建,以确保Advisors具有正确的生命周期模型。
	 * 
	 * @return whether this advice is associated with a particular target instance
	 */
	boolean isPerInstance();

}
