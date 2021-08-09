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

package org.springframework.context.weaving;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor}
 * implementation that passes the context's default {@link LoadTimeWeaver}
 * to beans that implement the {@link LoadTimeWeaverAware} interface.
 *
 * <p>{@link org.springframework.context.ApplicationContext Application contexts}
 * will automatically register this with their underlying {@link BeanFactory bean factory},
 * provided that a default {@code LoadTimeWeaver} is actually available.
 *
 * <p>Applications should not use this class directly.
 *
 * <p>
 *  {@link orgspringframeworkbeansfactoryconfigBeanPostProcessor}实现,将上下文的默认{@link LoadTimeWeaver}传递给实现{@link LoadTimeWeaverAware}
 * 接口的bean。
 * 
 * <p> {@ link orgspringframeworkcontextApplicationContext应用程序上下文}将自动将其注册到其底层{@link BeanFactory bean factory}
 * ,前提是默认{@code LoadTimeWeaver}实际可用。
 * 
 *  <p>应用程序不应该直接使用这个类
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see LoadTimeWeaverAware
 * @see org.springframework.context.ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME
 */
public class LoadTimeWeaverAwareProcessor implements BeanPostProcessor, BeanFactoryAware {

	private LoadTimeWeaver loadTimeWeaver;

	private BeanFactory beanFactory;


	/**
	 * Create a new {@code LoadTimeWeaverAwareProcessor} that will
	 * auto-retrieve the {@link LoadTimeWeaver} from the containing
	 * {@link BeanFactory}, expecting a bean named
	 * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
	 * <p>
	 *  创建一个新的{@code LoadTimeWeaverAwareProcessor},它将从包含的{@link BeanFactory}中自动检索{@link LoadTimeWeaver},期望一个
	 * 名为{@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME"的bean"loadTimeWeaver"}。
	 * 
	 */
	public LoadTimeWeaverAwareProcessor() {
	}

	/**
	 * Create a new {@code LoadTimeWeaverAwareProcessor} for the given
	 * {@link LoadTimeWeaver}.
	 * <p>If the given {@code loadTimeWeaver} is {@code null}, then a
	 * {@code LoadTimeWeaver} will be auto-retrieved from the containing
	 * {@link BeanFactory}, expecting a bean named
	 * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
	 * <p>
	 * 为给定的{@link LoadTimeWeaver}创建一个新的{@code LoadTimeWeaverAwareProcessor} <p>如果给定的{@code loadTimeWeaver}为{@code null}
	 * ,则{@code LoadTimeWeaver}将从包含的{ @link BeanFactory},期待一个名为{@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME"loadTimeWeaver"的名称}
	 * 。
	 * 
	 * 
	 * @param loadTimeWeaver the specific {@code LoadTimeWeaver} that is to be used
	 */
	public LoadTimeWeaverAwareProcessor(LoadTimeWeaver loadTimeWeaver) {
		this.loadTimeWeaver = loadTimeWeaver;
	}

	/**
	 * Create a new {@code LoadTimeWeaverAwareProcessor}.
	 * <p>The {@code LoadTimeWeaver} will be auto-retrieved from
	 * the given {@link BeanFactory}, expecting a bean named
	 * {@link ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME "loadTimeWeaver"}.
	 * <p>
	 * 
	 * @param beanFactory the BeanFactory to retrieve the LoadTimeWeaver from
	 */
	public LoadTimeWeaverAwareProcessor(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof LoadTimeWeaverAware) {
			LoadTimeWeaver ltw = this.loadTimeWeaver;
			if (ltw == null) {
				Assert.state(this.beanFactory != null,
						"BeanFactory required if no LoadTimeWeaver explicitly specified");
				ltw = this.beanFactory.getBean(
						ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME, LoadTimeWeaver.class);
			}
			((LoadTimeWeaverAware) bean).setLoadTimeWeaver(ltw);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String name) {
		return bean;
	}

}
