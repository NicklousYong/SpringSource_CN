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

package org.springframework.remoting.rmi;

import javax.naming.NamingException;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ClassUtils;

/**
 * {@link FactoryBean} for RMI proxies from JNDI.
 *
 * <p>Typically used for RMI-IIOP (CORBA), but can also be used for EJB home objects
 * (for example, a Stateful Session Bean home). In contrast to a plain JNDI lookup,
 * this accessor also performs narrowing through {@link javax.rmi.PortableRemoteObject}.
 *
 * <p>With conventional RMI services, this invoker is typically used with the RMI
 * service interface. Alternatively, this invoker can also proxy a remote RMI service
 * with a matching non-RMI business interface, i.e. an interface that mirrors the RMI
 * service methods but does not declare RemoteExceptions. In the latter case,
 * RemoteExceptions thrown by the RMI stub will automatically get converted to
 * Spring's unchecked RemoteAccessException.
 *
 * <p>The JNDI environment can be specified as "jndiEnvironment" property,
 * or be configured in a {@code jndi.properties} file or as system properties.
 * For example:
 *
 * <pre class="code">&lt;property name="jndiEnvironment"&gt;
 * 	 &lt;props>
 *		 &lt;prop key="java.naming.factory.initial"&gt;com.sun.jndi.cosnaming.CNCtxFactory&lt;/prop&gt;
 *		 &lt;prop key="java.naming.provider.url"&gt;iiop://localhost:1050&lt;/prop&gt;
 *	 &lt;/props&gt;
 * &lt;/property&gt;</pre>
 *
 * <p>
 *  来自JNDI的RMI代理的{@link FactoryBean}
 * 
 * 通常用于RMI-IIOP(CORBA),但也可用于EJB主对象(例如,有状态会话Bean home)与纯JNDI查找相反,此访问器也通过{@link javaxrmiPortableRemoteObject }
 * 。
 * 
 *  对于常规RMI服务,此调用者通常与RMI服务接口一起使用。
 * 或者,此调用程序还可以使用匹配的非RMI业务界面(即镜像RMI服务方法但不包含RMI服务方法的接口)代理远程RMI服务声明RemoteExceptions在后一种情况下,RMI存根抛出的RemoteEx
 * ceptions将自动转换为Spring未检查的RemoteAccessException。
 *  对于常规RMI服务,此调用者通常与RMI服务接口一起使用。
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setServiceInterface
 * @see #setJndiName
 * @see #setJndiTemplate
 * @see #setJndiEnvironment
 * @see #setJndiName
 * @see JndiRmiServiceExporter
 * @see org.springframework.remoting.RemoteAccessException
 * @see java.rmi.RemoteException
 * @see java.rmi.Remote
 * @see javax.rmi.PortableRemoteObject#narrow
 */
public class JndiRmiProxyFactoryBean extends JndiRmiClientInterceptor
		implements FactoryBean<Object>, BeanClassLoaderAware {

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private Object serviceProxy;


	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	public void afterPropertiesSet() throws NamingException {
		super.afterPropertiesSet();
		if (getServiceInterface() == null) {
			throw new IllegalArgumentException("Property 'serviceInterface' is required");
		}
		this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(this.beanClassLoader);
	}


	@Override
	public Object getObject() {
		return this.serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
