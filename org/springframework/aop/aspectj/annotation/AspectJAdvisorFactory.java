/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.AopConfigException;

/**
 * Interface for factories that can create Spring AOP Advisors from classes
 * annotated with AspectJ annotation syntax.
 *
 * <p>
 *  可以通过使用AspectJ注释语法注释的类创建Spring AOP Advisors的工厂接口
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjTypeSystem
 */
public interface AspectJAdvisorFactory {

	/**
	 * Determine whether or not the given class is an aspect, as reported
	 * by AspectJ's {@link org.aspectj.lang.reflect.AjTypeSystem}.
	 * <p>Will simply return {@code false} if the supposed aspect is
	 * invalid (such as an extension of a concrete aspect class).
	 * Will return true for some aspects that Spring AOP cannot process,
	 * such as those with unsupported instantiation models.
	 * Use the {@link #validate} method to handle these cases if necessary.
	 * <p>
	 * 确定给定的类是否是AspectJ的{@link orgaspectjlangreflectAjTypeSystem}报告的一个方面。
	 * 如果假定的方面无效(例如具体方面类的扩展),将简单地返回{@code false}将返回对于Spring AOP无法处理的某些方面(如具有不支持的实例化模型的那些方面),请使用{@link #validate}
	 * 方法来处理这些情况(如有必要)。
	 * 确定给定的类是否是AspectJ的{@link orgaspectjlangreflectAjTypeSystem}报告的一个方面。
	 * 
	 * 
	 * @param clazz the supposed annotation-style AspectJ class
	 * @return whether or not this class is recognized by AspectJ as an aspect class
	 */
	boolean isAspect(Class<?> clazz);

	/**
	 * Is the given class a valid AspectJ aspect class?
	 * <p>
	 *  给定的类是一个有效的AspectJ方面类吗?
	 * 
	 * 
	 * @param aspectClass the supposed AspectJ annotation-style class to validate
	 * @throws AopConfigException if the class is an invalid aspect
	 * (which can never be legal)
	 * @throws NotAnAtAspectException if the class is not an aspect at all
	 * (which may or may not be legal, depending on the context)
	 */
	void validate(Class<?> aspectClass) throws AopConfigException;

	/**
	 * Build Spring AOP Advisors for all annotated At-AspectJ methods
	 * on the specified aspect instance.
	 * <p>
	 *  在指定的Aspect实例上为所有注释的At-AspectJ方法构建Spring AOP Advisors
	 * 
	 * 
	 * @param aspectInstanceFactory the aspect instance factory
	 * (not the aspect instance itself in order to avoid eager instantiation)
	 * @return a list of advisors for this class
	 */
	List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory);

	/**
	 * Build a Spring AOP Advisor for the given AspectJ advice method.
	 * <p>
	 *  为给定的AspectJ建议方法构建一个Spring AOP顾问
	 * 
	 * 
	 * @param candidateAdviceMethod the candidate advice method
	 * @param aspectInstanceFactory the aspect instance factory
	 * @param declarationOrder the declaration order within the aspect
	 * @param aspectName the name of the aspect
	 * @return {@code null} if the method is not an AspectJ advice method
	 * or if it is a pointcut that will be used by other advice but will not
	 * create a Spring advice in its own right
	 */
	Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
			int declarationOrder, String aspectName);

	/**
	 * Build a Spring AOP Advice for the given AspectJ advice method.
	 * <p>
	 *  为给定的AspectJ建议方法构建一个Spring AOP建议
	 * 
	 * @param candidateAdviceMethod the candidate advice method
	 * @param expressionPointcut the AspectJ expression pointcut
	 * @param aspectInstanceFactory the aspect instance factory
	 * @param declarationOrder the declaration order within the aspect
	 * @param aspectName the name of the aspect
	 * @return {@code null} if the method is not an AspectJ advice method
	 * or if it is a pointcut that will be used by other advice but will not
	 * create a Spring advice in its own right
	 * @see org.springframework.aop.aspectj.AspectJAroundAdvice
	 * @see org.springframework.aop.aspectj.AspectJMethodBeforeAdvice
	 * @see org.springframework.aop.aspectj.AspectJAfterAdvice
	 * @see org.springframework.aop.aspectj.AspectJAfterReturningAdvice
	 * @see org.springframework.aop.aspectj.AspectJAfterThrowingAdvice
	 */
	Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
			MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName);

}
