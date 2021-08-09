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

package org.springframework.jndi;

import javax.naming.NamingException;

import org.springframework.core.env.PropertySource;

/**
 * {@link PropertySource} implementation that reads properties from an underlying Spring
 * {@link JndiLocatorDelegate}.
 *
 * <p>By default, the underlying {@code JndiLocatorDelegate} will be configured with its
 * {@link JndiLocatorDelegate#setResourceRef(boolean) "resourceRef"} property set to
 * {@code true}, meaning that names looked up will automatically be prefixed with
 * "java:comp/env/" in alignment with published
 * <a href="http://download.oracle.com/javase/jndi/tutorial/beyond/misc/policy.html">JNDI
 * naming conventions</a>. To override this setting or to change the prefix, manually
 * configure a {@code JndiLocatorDelegate} and provide it to one of the constructors here
 * that accepts it. The same applies when providing custom JNDI properties. These should
 * be specified using {@link JndiLocatorDelegate#setJndiEnvironment(java.util.Properties)}
 * prior to construction of the {@code JndiPropertySource}.
 *
 * <p>Note that {@link org.springframework.web.context.support.StandardServletEnvironment
 * StandardServletEnvironment} includes a {@code JndiPropertySource} by default, and any
 * customization of the underlying {@link JndiLocatorDelegate} may be performed within an
 * {@link org.springframework.context.ApplicationContextInitializer
 * ApplicationContextInitializer} or {@link org.springframework.web.WebApplicationInitializer
 * WebApplicationInitializer}.
 *
 * <p>
 *  {@link PropertySource}实现从底层的Spring {@link JndiLocatorDelegate}读取属性
 * 
 * <p>默认情况下,底层的{@code JndiLocatorDelegate}将被配置为{@link JndiLocatorDelegate#setResourceRef(boolean)"resourceRef"}
 * 属性设置为{@code true},这意味着查找的名称将自动被前缀"java：comp / env /"与已发布的<a href=\"http://downloadoraclecom/javase/jndi/tutorial/beyond/misc/policyhtml\">
 *  JNDI命名约定</a>保持一致要覆盖此设置或更改前缀,手动配置一个{@code JndiLocatorDelegate},并将其提供给其中一个接受它的构造函数。
 * 同样适用于提供自定义JNDI属性。
 * 这些应在构造之前使用{@link JndiLocatorDelegate#setJndiEnvironment(javautilProperties)}指定{@code JndiPropertySource}
 * 。
 * 同样适用于提供自定义JNDI属性。
 * 
 * 请注意,默认情况下,{@link orgspringframeworkwebcontextsupportStandardServletEnvironment StandardServletEnvironment}
 * 包含{@code JndiPropertySource},并且可以在{@link orgspringframeworkcontextApplicationContextInitializer ApplicationContextInitializer}
 * 或{@link orgspringframeworkwebWebApplicationInitializer WebApplicationInitializer}中执行底层{@link JndiLocatorDelegate}
 * 的任何定制,。
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see JndiLocatorDelegate
 * @see org.springframework.context.ApplicationContextInitializer
 * @see org.springframework.web.WebApplicationInitializer
 * @see org.springframework.web.context.support.StandardServletEnvironment
 */
public class JndiPropertySource extends PropertySource<JndiLocatorDelegate> {

	/**
	 * Create a new {@code JndiPropertySource} with the given name
	 * and a {@link JndiLocatorDelegate} configured to prefix any names with
	 * "java:comp/env/".
	 * <p>
	 * 
	 */
	public JndiPropertySource(String name) {
		this(name, JndiLocatorDelegate.createDefaultResourceRefLocator());
	}

	/**
	 * Create a new {@code JndiPropertySource} with the given name and the given
	 * {@code JndiLocatorDelegate}.
	 * <p>
	 *  使用给定的名称创建一个新的{@code JndiPropertySource},并配置一个{@link JndiLocatorDelegate},将任何名称与"java：comp / env /"进行
	 * 匹配。
	 * 
	 */
	public JndiPropertySource(String name, JndiLocatorDelegate jndiLocator) {
		super(name, jndiLocator);
	}


	/**
	 * This implementation looks up and returns the value associated with the given
	 * name from the underlying {@link JndiLocatorDelegate}. If a {@link NamingException}
	 * is thrown during the call to {@link JndiLocatorDelegate#lookup(String)}, returns
	 * {@code null} and issues a DEBUG-level log statement with the exception message.
	 * <p>
	 *  使用给定的名称和给定的{@code JndiLocatorDelegate}创建一个新的{@code JndiPropertySource}
	 * 
	 */
	@Override
	public Object getProperty(String name) {
		if (getSource().isResourceRef() && name.indexOf(':') != -1) {
			// We're in resource-ref (prefixing with "java:comp/env") mode. Let's not bother
			// with property names with a colon it since they're probably just containing a
			// default value clause, very unlikely to match including the colon part even in
			// a textual property source, and effectively never meant to match that way in
			// JNDI where a colon indicates a separator between JNDI scheme and actual name.
			return null;
		}

		try {
			Object value = this.source.lookup(name);
			if (logger.isDebugEnabled()) {
				logger.debug("JNDI lookup for name [" + name + "] returned: [" + value + "]");
			}
			return value;
		}
		catch (NamingException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("JNDI lookup for name [" + name + "] threw NamingException " +
						"with message: " + ex.getMessage() + ". Returning null.");
			}
			return null;
		}
	}

}
