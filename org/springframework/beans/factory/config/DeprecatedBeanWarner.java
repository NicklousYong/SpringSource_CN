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

package org.springframework.beans.factory.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Bean factory post processor that logs a warning for {@link Deprecated @Deprecated} beans.
 *
 * <p>
 *  Bean工厂后处理器,为{@link弃用@Deprecated} bean记录一个警告
 * 
 * 
 * @author Arjen Poutsma
 * @since 3.0.3
 */
public class DeprecatedBeanWarner implements BeanFactoryPostProcessor {

	/**
	 * Logger available to subclasses.
	 * <p>
	 *  记录器可用于子类
	 * 
	 */
	protected transient Log logger = LogFactory.getLog(getClass());

	/**
	 * Set the name of the logger to use.
	 * The name will be passed to the underlying logger implementation through Commons Logging,
	 * getting interpreted as log category according to the logger's configuration.
	 * <p>This can be specified to not log into the category of this warner class but rather
	 * into a specific named category.
	 * <p>
	 * 设置要使用的记录器的名称该名称将通过Commons Logging传递到底层日志记录器实现,根据记录器的配置将其解释为日志类别<p>可以将其指定为不登录到此warner类的类别,但而是进入特定的命名类别
	 * 。
	 * 
	 * 
	 * @see org.apache.commons.logging.LogFactory#getLog(String)
	 * @see org.apache.log4j.Logger#getLogger(String)
	 * @see java.util.logging.Logger#getLogger(String)
	 */
	public void setLoggerName(String loggerName) {
		this.logger = LogFactory.getLog(loggerName);
	}


	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (isLogEnabled()) {
			String[] beanNames = beanFactory.getBeanDefinitionNames();
			for (String beanName : beanNames) {
				String nameToLookup = beanName;
				if (beanFactory.isFactoryBean(beanName)) {
					nameToLookup = BeanFactory.FACTORY_BEAN_PREFIX + beanName;
				}
				Class<?> beanType = ClassUtils.getUserClass(beanFactory.getType(nameToLookup));
				if (beanType != null && beanType.isAnnotationPresent(Deprecated.class)) {
					BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
					logDeprecatedBean(beanName, beanType, beanDefinition);
				}
			}
		}
	}

	/**
	 * Logs a warning for a bean annotated with {@link Deprecated @Deprecated}.
	 * <p>
	 *  为使用{@link Deprecated @Deprecated}注释的bean记录一个警告
	 * 
	 * 
	 * @param beanName the name of the deprecated bean
	 * @param beanType the user-specified type of the deprecated bean
	 * @param beanDefinition the definition of the deprecated bean
	 */
	protected void logDeprecatedBean(String beanName, Class<?> beanType, BeanDefinition beanDefinition) {
		StringBuilder builder = new StringBuilder();
		builder.append(beanType);
		builder.append(" ['");
		builder.append(beanName);
		builder.append('\'');
		String resourceDescription = beanDefinition.getResourceDescription();
		if (StringUtils.hasLength(resourceDescription)) {
			builder.append(" in ");
			builder.append(resourceDescription);
		}
		builder.append("] has been deprecated");
		writeToLog(builder.toString());
	}

	/**
	 * Actually write to the underlying log.
	 * <p>The default implementations logs the message at "warn" level.
	 * <p>
	 *  实际写入基础日志<p>默认实现将消息记录在"warn"级别
	 * 
	 * 
	 * @param message the message to write
	 */
	protected void writeToLog(String message) {
		logger.warn(message);
	}

	/**
	 * Determine whether the {@link #logger} field is enabled.
	 * <p>Default is {@code true} when the "warn" level is enabled.
	 * Subclasses can override this to change the level under which logging occurs.
	 * <p>
	 *  确定是否启用{@link #logger}字段<p>当启用"警告"级别时,默认值为{@code true}子类可以覆盖此值以更改记录发生时的级别
	 */
	protected boolean isLogEnabled() {
		return logger.isWarnEnabled();
	}

}
