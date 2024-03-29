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

package org.springframework.validation.beanvalidation;

import java.lang.annotation.Annotation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

/**
 * A convenient {@link BeanPostProcessor} implementation that delegates to a
 * JSR-303 provider for performing method-level validation on annotated methods.
 *
 * <p>Applicable methods have JSR-303 constraint annotations on their parameters
 * and/or on their return value (in the latter case specified at the method level,
 * typically as inline annotation), e.g.:
 *
 * <pre class="code">
 * public @NotNull Object myValidMethod(@NotNull String arg1, @Max(10) int arg2)
 * </pre>
 *
 * <p>Target classes with such annotated methods need to be annotated with Spring's
 * {@link Validated} annotation at the type level, for their methods to be searched for
 * inline constraint annotations. Validation groups can be specified through {@code @Validated}
 * as well. By default, JSR-303 will validate against its default group only.
 *
 * <p>As of Spring 4.0, this functionality requires either a Bean Validation 1.1 provider
 * (such as Hibernate Validator 5.x) or the Bean Validation 1.0 API with Hibernate Validator
 * 4.3. The actual provider will be autodetected and automatically adapted.
 *
 * <p>
 *  一个方便的{@link BeanPostProcessor}实现,委托给JSR-303提供程序,用于对注释方法执行方法级验证
 * 
 * <p>适用的方法对其参数和/或其返回值具有JSR-303约束注释(在后一种情况下,在方法级别指定,通常为内联注释),例如：
 * 
 * <pre class="code">
 *  public @NotNull Object myValidMethod(@NotNull String arg1,@Max(10)int arg2)
 * </pre>
 * 
 *  <p>使用这种注释方法的目标类需要使用Spring的{@link Validated}注释在类型级别进行注释,方法是搜索内联约束注释。
 * 可以通过{@code @Validated}指定验证组默认情况下,JSR-303将仅对其默认组进行验证。
 * 
 * <p>从Spring 40开始,此功能需要一个Bean验证11提供程序(如Hibernate Validator 5x)或具有Hibernate Validator的Bean验证10 API 43实际的
 * 提供程序将被自动检测并自动调整。
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.1
 * @see MethodValidationInterceptor
 * @see org.hibernate.validator.method.MethodValidator
 */
@SuppressWarnings("serial")
public class MethodValidationPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor
		implements InitializingBean {

	private Class<? extends Annotation> validatedAnnotationType = Validated.class;

	private Validator validator;


	/**
	 * Set the 'validated' annotation type.
	 * The default validated annotation type is the {@link Validated} annotation.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a class is supposed
	 * to be validated in the sense of applying method validation.
	 * <p>
	 * 
	 * @param validatedAnnotationType the desired annotation type
	 */
	public void setValidatedAnnotationType(Class<? extends Annotation> validatedAnnotationType) {
		Assert.notNull(validatedAnnotationType, "'validatedAnnotationType' must not be null");
		this.validatedAnnotationType = validatedAnnotationType;
	}

	/**
	 * Set the JSR-303 Validator to delegate to for validating methods.
	 * <p>Default is the default ValidatorFactory's default Validator.
	 * <p>
	 *  设置"已验证"注释类型默认的已验证注释类型是{@link Validated}注释<p>此setter属性存在,以便开发人员可以提供自己的(非Spring特定的)注释类型,以指示类是应该的在应用方法验
	 * 证的意义上被验证。
	 * 
	 */
	public void setValidator(Validator validator) {
		if (validator instanceof LocalValidatorFactoryBean) {
			this.validator = ((LocalValidatorFactoryBean) validator).getValidator();
		}
		else {
			this.validator = validator;
		}
	}

	/**
	 * Set the JSR-303 ValidatorFactory to delegate to for validating methods,
	 * using its default Validator.
	 * <p>Default is the default ValidatorFactory's default Validator.
	 * <p>
	 *  将JSR-303验证器设置为委托以进行验证方法<p>默认值为默认ValidatorFactory的默认验证器
	 * 
	 * 
	 * @see javax.validation.ValidatorFactory#getValidator()
	 */
	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.validator = validatorFactory.getValidator();
	}


	@Override
	public void afterPropertiesSet() {
		Pointcut pointcut = new AnnotationMatchingPointcut(this.validatedAnnotationType, true);
		this.advisor = new DefaultPointcutAdvisor(pointcut, createMethodValidationAdvice(this.validator));
	}

	/**
	 * Create AOP advice for method validation purposes, to be applied
	 * with a pointcut for the specified 'validated' annotation.
	 * <p>
	 * 将JSR-303 ValidatorFactory设置为委托以验证方法,使用其默认验证器<p>默认值为默认ValidatorFactory的默认验证器
	 * 
	 * 
	 * @param validator the JSR-303 Validator to delegate to
	 * @return the interceptor to use (typically, but not necessarily,
	 * a {@link MethodValidationInterceptor} or subclass thereof)
	 * @since 4.2
	 */
	protected Advice createMethodValidationAdvice(Validator validator) {
		return (validator != null ? new MethodValidationInterceptor(validator) : new MethodValidationInterceptor());
	}

}
