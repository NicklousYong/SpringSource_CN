/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.jndi;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Convenient superclass for JNDI accessors, providing "jndiTemplate"
 * and "jndiEnvironment" bean properties.
 *
 * <p>
 *  为JNDI访问者提供方便的超类,提供"jndiTemplate"和"jndiEnvironment"bean属性
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setJndiTemplate
 * @see #setJndiEnvironment
 */
public class JndiAccessor {

	/**
	 * Logger, available to subclasses.
	 * <p>
	 *  记录器,可用于子类
	 * 
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	private JndiTemplate jndiTemplate = new JndiTemplate();


	/**
	 * Set the JNDI template to use for JNDI lookups.
	 * <p>You can also specify JNDI environment settings via "jndiEnvironment".
	 * <p>
	 * 设置用于JNDI查找的JNDI模板<p>您还可以通过"jndiEnvironment"指定JNDI环境设置
	 * 
	 * 
	 * @see #setJndiEnvironment
	 */
	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiTemplate = (jndiTemplate != null ? jndiTemplate : new JndiTemplate());
	}

	/**
	 * Return the JNDI template to use for JNDI lookups.
	 * <p>
	 *  返回JNDI模板以用于JNDI查找
	 * 
	 */
	public JndiTemplate getJndiTemplate() {
		return this.jndiTemplate;
	}

	/**
	 * Set the JNDI environment to use for JNDI lookups.
	 * <p>Creates a JndiTemplate with the given environment settings.
	 * <p>
	 *  将JNDI环境设置为用于JNDI查找<p>使用给定的环境设置创建JndiTemplate
	 * 
	 * 
	 * @see #setJndiTemplate
	 */
	public void setJndiEnvironment(Properties jndiEnvironment) {
		this.jndiTemplate = new JndiTemplate(jndiEnvironment);
	}

	/**
	 * Return the JNDI environment to use for JNDI lookups.
	 * <p>
	 *  返回JNDI环境以用于JNDI查找
	 */
	public Properties getJndiEnvironment() {
		return this.jndiTemplate.getEnvironment();
	}

}