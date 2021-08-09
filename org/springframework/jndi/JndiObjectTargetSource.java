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

package org.springframework.jndi;

import javax.naming.NamingException;

import org.springframework.aop.TargetSource;

/**
 * AOP {@link org.springframework.aop.TargetSource} that provides
 * configurable JNDI lookups for {@code getTarget()} calls.
 *
 * <p>Can be used as alternative to {@link JndiObjectFactoryBean}, to allow for
 * relocating a JNDI object lazily or for each operation (see "lookupOnStartup"
 * and "cache" properties). This is particularly useful during development, as it
 * allows for hot restarting of the JNDI server (for example, a remote JMS server).
 *
 * <p>Example:
 *
 * <pre class="code">
 * &lt;bean id="queueConnectionFactoryTarget" class="org.springframework.jndi.JndiObjectTargetSource"&gt;
 *   &lt;property name="jndiName" value="JmsQueueConnectionFactory"/&gt;
 *   &lt;property name="lookupOnStartup" value="false"/&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="queueConnectionFactory" class="org.springframework.aop.framework.ProxyFactoryBean"&gt;
 *   &lt;property name="proxyInterfaces" value="javax.jms.QueueConnectionFactory"/&gt;
 *   &lt;property name="targetSource" ref="queueConnectionFactoryTarget"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * A {@code createQueueConnection} call on the "queueConnectionFactory" proxy will
 * cause a lazy JNDI lookup for "JmsQueueConnectionFactory" and a subsequent delegating
 * call to the retrieved QueueConnectionFactory's {@code createQueueConnection}.
 *
 * <p><b>Alternatively, use a {@link JndiObjectFactoryBean} with a "proxyInterface".</b>
 * "lookupOnStartup" and "cache" can then be specified on the JndiObjectFactoryBean,
 * creating a JndiObjectTargetSource underneath (instead of defining separate
 * ProxyFactoryBean and JndiObjectTargetSource beans).
 *
 * <p>
 *  AOP {@link orgspringframeworkaopTargetSource},为{@code getTarget()}调用提供可配置的JNDI查找
 * 
 * <p>可以作为{@link JndiObjectFactoryBean}的替代方法,允许懒惰或每个操作重定位一个JNDI对象(参见"lookupOnStartup"和"缓存"属性)这在开发过程中特别有用
 * ,因为它允许热重新启动JNDI服务器(例如,远程JMS服务器)。
 * 
 *  <P>实施例：
 * 
 * <pre class="code">
 *  &lt; bean id ="queueConnectionFactoryTarget"class ="orgspringframeworkjndiJndiObjectTargetSource"&gt
 * ; &lt; property name ="jndiName"value ="JmsQueueConnectionFactory"/&gt; &lt; property name ="lookupOn
 * Startup"value ="false"/&gt; &LT; /豆腐&GT;。
 * 
 * &lt; bean id ="queueConnectionFactory"class ="orgspringframeworkaopframeworkProxyFactoryBean"&gt; &lt
 * ; property name ="proxyInterfaces"value ="javaxjmsQueueConnectionFactory"/&gt; &lt; property name ="t
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setLookupOnStartup
 * @see #setCache
 * @see org.springframework.aop.framework.ProxyFactoryBean#setTargetSource
 * @see JndiObjectFactoryBean#setProxyInterface
 */
public class JndiObjectTargetSource extends JndiObjectLocator implements TargetSource {

	private boolean lookupOnStartup = true;

	private boolean cache = true;

	private Object cachedObject;

	private Class<?> targetClass;


	/**
	 * Set whether to look up the JNDI object on startup. Default is "true".
	 * <p>Can be turned off to allow for late availability of the JNDI object.
	 * In this case, the JNDI object will be fetched on first access.
	 * <p>
	 * argetSource"ref ="queueConnectionFactoryTarget"/&gt; &LT; /豆腐&GT; </PRE>。
	 * 
	 *  "queueConnectionFactory"代理上的{@code createQueueConnection}调用将导致对"JmsQueueConnectionFactory"的延迟JNDI查找,
	 * 并对后续的委托调用QueueConnectionFactory的{@code createQueueConnection}。
	 * 
	 * <p> <b>或者,可以在JndiObjectFactoryBean上指定使用"proxyInterface"</b>"lookupOnStartup"和"cache"的{@link JndiObjectFactoryBean}
	 * ,在其下创建一个JndiObjectTargetSource(而不是定义单独的ProxyFactoryBean和JndiObjectTargetSource bean)。
	 * 
	 * @see #setCache
	 */
	public void setLookupOnStartup(boolean lookupOnStartup) {
		this.lookupOnStartup = lookupOnStartup;
	}

	/**
	 * Set whether to cache the JNDI object once it has been located.
	 * Default is "true".
	 * <p>Can be turned off to allow for hot redeployment of JNDI objects.
	 * In this case, the JNDI object will be fetched for each invocation.
	 * <p>
	 * 
	 * 
	 * @see #setLookupOnStartup
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	@Override
	public void afterPropertiesSet() throws NamingException {
		super.afterPropertiesSet();
		if (this.lookupOnStartup) {
			Object object = lookup();
			if (this.cache) {
				this.cachedObject = object;
			}
			else {
				this.targetClass = object.getClass();
			}
		}
	}


	@Override
	public Class<?> getTargetClass() {
		if (this.cachedObject != null) {
			return this.cachedObject.getClass();
		}
		else if (this.targetClass != null) {
			return this.targetClass;
		}
		else {
			return getExpectedType();
		}
	}

	@Override
	public boolean isStatic() {
		return (this.cachedObject != null);
	}

	@Override
	public Object getTarget() {
		try {
			if (this.lookupOnStartup || !this.cache) {
				return (this.cachedObject != null ? this.cachedObject : lookup());
			}
			else {
				synchronized (this) {
					if (this.cachedObject == null) {
						this.cachedObject = lookup();
					}
					return this.cachedObject;
				}
			}
		}
		catch (NamingException ex) {
			throw new JndiLookupFailureException("JndiObjectTargetSource failed to obtain new target object", ex);
		}
	}

	@Override
	public void releaseTarget(Object target) {
	}

}
