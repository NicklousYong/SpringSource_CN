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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

/**
 * JSR-303 {@link ConstraintValidatorFactory} implementation that delegates to a
 * Spring BeanFactory for creating autowired {@link ConstraintValidator} instances.
 *
 * <p>Note that this class is meant for programmatic use, not for declarative use
 * in a standard {@code validation.xml} file. Consider
 * {@link org.springframework.web.bind.support.SpringWebConstraintValidatorFactory}
 * for declarative use in a web application, e.g. with JAX-RS or JAX-WS.
 *
 * <p>
 *  JSR-303 {@link ConstraintValidatorFactory}实现,委托给Spring BeanFactory创建自动连线{@link ConstraintValidator}实
 * 例。
 * 
 * <p>请注意,此类用于编程使用,不适用于标准的{@code validationxml}文件中的声明式使用。
 * 考虑使用{@link orgspringframeworkwebbindsupportSpringWebConstraintValidatorFactory}在Web应用程序中进行声明性使用,例如使用J
 * AX-RS或JAX-WS。
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory#createBean(Class)
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory {

	private final AutowireCapableBeanFactory beanFactory;


	/**
	 * Create a new SpringConstraintValidatorFactory for the given BeanFactory.
	 * <p>
	 * <p>请注意,此类用于编程使用,不适用于标准的{@code validationxml}文件中的声明式使用。
	 * 
	 * 
	 * @param beanFactory the target BeanFactory
	 */
	public SpringConstraintValidatorFactory(AutowireCapableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}


	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		return this.beanFactory.createBean(key);
	}

	// Bean Validation 1.1 releaseInstance method
	public void releaseInstance(ConstraintValidator<?, ?> instance) {
		this.beanFactory.destroyBean(instance);
	}

}
