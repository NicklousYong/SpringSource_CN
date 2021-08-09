/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.scheduling.concurrent;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import javax.naming.NamingException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiTemplate;

/**
 * JNDI-based variant of {@link ConcurrentTaskScheduler}, performing a default lookup for
 * JSR-236's "java:comp/DefaultManagedScheduledExecutorService" in a Java EE 7 environment.
 *
 * <p>Note: This class is not strictly JSR-236 based; it can work with any regular
 * {@link java.util.concurrent.ScheduledExecutorService} that can be found in JNDI.
 * The actual adapting to {@link javax.enterprise.concurrent.ManagedScheduledExecutorService}
 * happens in the base class {@link ConcurrentTaskScheduler} itself.
 *
 * <p>
 *  基于JNDI的变体{@link ConcurrentTaskScheduler},在Java EE 7环境中执行JSR-236的"java：comp / DefaultManagedScheduled
 * ExecutorService"的默认查找。
 * 
 * 注意：这个类不是严格的基于JSR-236的;它可以与JNDI中可以找到的任何常规{@link javautilconcurrentScheduledExecutorService}一起使用。
 * 实际适应{@link javaxenterpriseconcurrentManagedScheduledExecutorService}发生在基类{@link ConcurrentTaskScheduler}
 * 本身中。
 * 注意：这个类不是严格的基于JSR-236的;它可以与JNDI中可以找到的任何常规{@link javautilconcurrentScheduledExecutorService}一起使用。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.0
 */
public class DefaultManagedTaskScheduler extends ConcurrentTaskScheduler implements InitializingBean {

	private JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();

	private String jndiName = "java:comp/DefaultManagedScheduledExecutorService";


	/**
	 * Set the JNDI template to use for JNDI lookups.
	 * <p>
	 *  将JNDI模板设置为用于JNDI查找
	 * 
	 * 
	 * @see org.springframework.jndi.JndiAccessor#setJndiTemplate
	 */
	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiLocator.setJndiTemplate(jndiTemplate);
	}

	/**
	 * Set the JNDI environment to use for JNDI lookups.
	 * <p>
	 *  将JNDI环境设置为用于JNDI查找
	 * 
	 * 
	 * @see org.springframework.jndi.JndiAccessor#setJndiEnvironment
	 */
	public void setJndiEnvironment(Properties jndiEnvironment) {
		this.jndiLocator.setJndiEnvironment(jndiEnvironment);
	}

	/**
	 * Set whether the lookup occurs in a J2EE container, i.e. if the prefix
	 * "java:comp/env/" needs to be added if the JNDI name doesn't already
	 * contain it. PersistenceAnnotationBeanPostProcessor's default is "true".
	 * <p>
	 *  设置是否在J2EE容器中进行查找,即如果JNDI名称不包含它,则需要添加前缀"java：comp / env /"PersistenceAnnotationBeanPostProcessor的默认值为
	 * "true"。
	 * 
	 * 
	 * @see org.springframework.jndi.JndiLocatorSupport#setResourceRef
	 */
	public void setResourceRef(boolean resourceRef) {
		this.jndiLocator.setResourceRef(resourceRef);
	}

	/**
	 * Specify a JNDI name of the {@link java.util.concurrent.Executor} to delegate to,
	 * replacing the default JNDI name "java:comp/DefaultManagedScheduledExecutorService".
	 * <p>This can either be a fully qualified JNDI name, or the JNDI name relative
	 * to the current environment naming context if "resourceRef" is set to "true".
	 * <p>
	 * 指定要委托的{@link javautilconcurrentExecutor}的JNDI名称,替换默认的JNDI名称"java：comp / DefaultManagedScheduledExecut
	 * orService"<p>这可以是完全限定的JNDI名称,也可以是相对于当前环境命名的JNDI名称如果"resourceRef"设置为"true"。
	 * 
	 * @see #setConcurrentExecutor
	 * @see #setResourceRef
	 */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	@Override
	public void afterPropertiesSet() throws NamingException {
		if (this.jndiName != null) {
			ScheduledExecutorService executor = this.jndiLocator.lookup(this.jndiName, ScheduledExecutorService.class);
			setConcurrentExecutor(executor);
			setScheduledExecutor(executor);
		}
	}

}
