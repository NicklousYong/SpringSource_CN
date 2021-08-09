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

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.core.SpringProperties;

/**
 * {@link JndiLocatorSupport} subclass with public lookup methods,
 * for convenient use as a delegate.
 *
 * <p>
 *  {@link JndiLocatorSupport}子类具有公共查找方法,方便用作委托
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0.1
 */
public class JndiLocatorDelegate extends JndiLocatorSupport {

	/**
	 * System property that instructs Spring to ignore a default JNDI environment, i.e.
	 * to always return {@code false} from {@link #isDefaultJndiEnvironmentAvailable()}.
	 * <p>The default is "false", allowing for regular default JNDI access e.g. in
	 * {@link JndiPropertySource}. Switching this flag to {@code true} is an optimization
	 * for scenarios where nothing is ever to be found for such JNDI fallback searches
	 * to begin with, avoiding the repeated JNDI lookup overhead.
	 * <p>Note that this flag just affects JNDI fallback searches, not explicitly configured
	 * JNDI lookups such as for a {@code DataSource} or some other environment resource.
	 * The flag literally just affects code which attempts JNDI searches based on the
	 * {@code JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable()} check: in particular,
	 * {@code StandardServletEnvironment} and {@code StandardPortletEnvironment}.
	 * <p>
	 * 系统属性指示Spring忽略默认的JNDI环境,即始终从{@link #isDefaultJndiEnvironmentAvailable())返回{@code false} <p>默认值为"false
	 * ",允许常规的默认JNDI访问,例如{@链接JndiPropertySource}将此标志切换为{@code true}是针对这样的JNDI后备搜索始终没有找到的情况的优化,避免重复的JNDI查找开销<p>
	 * 请注意,此标志仅影响JNDI后备搜索,未明确配置的JNDI查找,例如对于{@code DataSource}或某些其他环境资源。
	 * 该标志字面上仅影响根据{@code JndiLocatorDelegate尝试JNDI搜索的代码isDefaultJndiEnvironmentAvailable()}检查：特别是{@code StandardServletEnvironment}
	 * 和{@code StandardPortletEnvironment}。
	 * 
	 * 
	 * @since 4.3
	 * @see #isDefaultJndiEnvironmentAvailable()
	 * @see JndiPropertySource
	 */
	public static final String IGNORE_JNDI_PROPERTY_NAME = "spring.jndi.ignore";


	private static final boolean shouldIgnoreDefaultJndiEnvironment =
			SpringProperties.getFlag(IGNORE_JNDI_PROPERTY_NAME);


	@Override
	public Object lookup(String jndiName) throws NamingException {
		return super.lookup(jndiName);
	}

	@Override
	public <T> T lookup(String jndiName, Class<T> requiredType) throws NamingException {
		return super.lookup(jndiName, requiredType);
	}


	/**
	 * Configure a {@code JndiLocatorDelegate} with its "resourceRef" property set to
	 * {@code true}, meaning that all names will be prefixed with "java:comp/env/".
	 * <p>
	 * 
	 * @see #setResourceRef
	 */
	public static JndiLocatorDelegate createDefaultResourceRefLocator() {
		JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
		jndiLocator.setResourceRef(true);
		return jndiLocator;
	}

	/**
	 * Check whether a default JNDI environment, as in a J2EE environment,
	 * is available on this JVM.
	 * <p>
	 * 配置{@code JndiLocatorDelegate},其"resourceRef"属性设置为{@code true},这意味着所有名称都将以"java：comp / env /"为前缀
	 * 
	 * 
	 * @return {@code true} if a default InitialContext can be used,
	 * {@code false} if not
	 */
	public static boolean isDefaultJndiEnvironmentAvailable() {
		if (shouldIgnoreDefaultJndiEnvironment) {
			return false;
		}
		try {
			new InitialContext().getEnvironment();
			return true;
		}
		catch (Throwable ex) {
			return false;
		}
	}

}
