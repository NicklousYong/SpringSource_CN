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

package org.springframework.ejb.access;

import javax.naming.NamingException;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ClassUtils;

/**
 * Convenient {@link FactoryBean} for local Stateless Session Bean (SLSB) proxies.
 * Designed for EJB 2.x, but works for EJB 3 Session Beans as well.
 *
 * <p>See {@link org.springframework.jndi.JndiObjectLocator} for info on
 * how to specify the JNDI location of the target EJB.
 *
 * <p>If you want control over interceptor chaining, use an AOP ProxyFactoryBean
 * with LocalSlsbInvokerInterceptor rather than rely on this class.
 *
 * <p>In a bean container, this class is normally best used as a singleton. However,
 * if that bean container pre-instantiates singletons (as do the XML ApplicationContext
 * variants) you may have a problem if the bean container is loaded before the EJB
 * container loads the target EJB. That is because by default the JNDI lookup will be
 * performed in the init method of this class and cached, but the EJB will not have been
 * bound at the target location yet. The best solution is to set the "lookupHomeOnStartup"
 * property to "false", in which case the home will be fetched on first access to the EJB.
 * (This flag is only true by default for backwards compatibility reasons).
 *
 * <p>
 *  方便的{@link FactoryBean}用于本地无状态会话Bean(SLSB)代理设计用于EJB 2x,但适用于EJB 3会话Bean
 * 
 * 有关如何指定目标EJB的JNDI位置的信息,请参阅{@link orgspringframeworkjndiJndiObjectLocator}
 * 
 *  <p>如果要控制拦截器链接,请使用AOP ProxyFactoryBean与LocalSlsbInvokerInterceptor而不是依赖此类
 * 
 * 在一个bean容器中,这个类通常最好用作单例,但是如果这个bean容器预实例化单例(和XML ApplicationContext变体一样),如果bean容器在EJB容器之前被加载,你可能会遇到一个问题
 * 加载目标EJB这是因为默认情况下,将在此类的init方法中执行JNDI查找并进行缓存,但EJB不会被绑定到目标位置。
 * 
 * @author Rod Johnson
 * @author Colin Sampaleanu
 * @since 09.05.2003
 * @see AbstractSlsbInvokerInterceptor#setLookupHomeOnStartup
 * @see AbstractSlsbInvokerInterceptor#setCacheHome
 */
public class LocalStatelessSessionProxyFactoryBean extends LocalSlsbInvokerInterceptor
		implements FactoryBean<Object>, BeanClassLoaderAware {

	/** The business interface of the EJB we're proxying */
	private Class<?> businessInterface;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	/** EJBLocalObject */
	private Object proxy;


	/**
	 * Set the business interface of the EJB we're proxying.
	 * This will normally be a super-interface of the EJB local component interface.
	 * Using a business methods interface is a best practice when implementing EJBs.
	 * <p>
	 * 最佳解决方案是将"lookupHomeOnStartup"属性设置为"false",在这种情况下,首次访问EJB时将会获取该对象(默认情况下,此标志仅为true,因为向后兼容性原因)。
	 * 
	 * 
	 * @param businessInterface set the business interface of the EJB
	 */
	public void setBusinessInterface(Class<?> businessInterface) {
		this.businessInterface = businessInterface;
	}

	/**
	 * Return the business interface of the EJB we're proxying.
	 * <p>
	 * 设置我们代理的EJB的业务接口这通常是EJB本地组件接口的超级接口使用业务方法接口是实现EJB时的最佳实践
	 * 
	 */
	public Class<?> getBusinessInterface() {
		return this.businessInterface;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	public void afterPropertiesSet() throws NamingException {
		super.afterPropertiesSet();
		if (this.businessInterface == null) {
			throw new IllegalArgumentException("businessInterface is required");
		}
		this.proxy = new ProxyFactory(this.businessInterface, this).getProxy(this.beanClassLoader);
	}


	@Override
	public Object getObject() {
		return this.proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return this.businessInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
