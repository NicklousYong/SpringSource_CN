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

package org.springframework.aop.framework;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;

/**
 * Convenient superclass for {@link FactoryBean} types that produce singleton-scoped
 * proxy objects.
 *
 * <p>Manages pre- and post-interceptors (references, rather than
 * interceptor names, as in {@link ProxyFactoryBean}) and provides
 * consistent interface management.
 *
 * <p>
 *  产生单例范围代理对象的{@link FactoryBean}类型的便捷超类
 * 
 * <p>管理拦截前和拦截器(引用而不是拦截器名称,如{@link ProxyFactoryBean}中),并提供一致的界面管理
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class AbstractSingletonProxyFactoryBean extends ProxyConfig
		implements FactoryBean<Object>, BeanClassLoaderAware, InitializingBean {

	private Object target;

	private Class<?>[] proxyInterfaces;

	private Object[] preInterceptors;

	private Object[] postInterceptors;

	/** Default is global AdvisorAdapterRegistry */
	private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();

	private transient ClassLoader proxyClassLoader;

	private Object proxy;


	/**
	 * Set the target object, that is, the bean to be wrapped with a transactional proxy.
	 * <p>The target may be any object, in which case a SingletonTargetSource will
	 * be created. If it is a TargetSource, no wrapper TargetSource is created:
	 * This enables the use of a pooling or prototype TargetSource etc.
	 * <p>
	 *  设置目标对象,即要使用事务代理程序包装的bean <p>目标可以是任何对象,在这种情况下,将创建一个SingletonTargetSource如果是TargetSource,则不创建包装器Target
	 * Source：这将启用使用池或原型的TargetSource等。
	 * 
	 * 
	 * @see org.springframework.aop.TargetSource
	 * @see org.springframework.aop.target.SingletonTargetSource
	 * @see org.springframework.aop.target.LazyInitTargetSource
	 * @see org.springframework.aop.target.PrototypeTargetSource
	 * @see org.springframework.aop.target.CommonsPool2TargetSource
	 */
	public void setTarget(Object target) {
		this.target = target;
	}

	/**
	 * Specify the set of interfaces being proxied.
	 * <p>If not specified (the default), the AOP infrastructure works
	 * out which interfaces need proxying by analyzing the target,
	 * proxying all the interfaces that the target object implements.
	 * <p>
	 *  指定被代理的接口集<p>如果未指定(默认),则AOP基础架构通过分析目标来实现需要代理的接口,代理目标对象实现的所有接口
	 * 
	 */
	public void setProxyInterfaces(Class<?>[] proxyInterfaces) {
		this.proxyInterfaces = proxyInterfaces;
	}

	/**
	 * Set additional interceptors (or advisors) to be applied before the
	 * implicit transaction interceptor, e.g. a PerformanceMonitorInterceptor.
	 * <p>You may specify any AOP Alliance MethodInterceptors or other
	 * Spring AOP Advices, as well as Spring AOP Advisors.
	 * <p>
	 * 在隐式事务拦截器之前设置要应用的附加拦截器(或指导者),例如PerformanceMonitorInterceptor <p>您可以指定任何AOP Alliance MethodInterceptors
	 * 或其他Spring AOP建议,以及Spring AOP Advisors。
	 * 
	 * 
	 * @see org.springframework.aop.interceptor.PerformanceMonitorInterceptor
	 */
	public void setPreInterceptors(Object[] preInterceptors) {
		this.preInterceptors = preInterceptors;
	}

	/**
	 * Set additional interceptors (or advisors) to be applied after the
	 * implicit transaction interceptor.
	 * <p>You may specify any AOP Alliance MethodInterceptors or other
	 * Spring AOP Advices, as well as Spring AOP Advisors.
	 * <p>
	 *  在隐式事务拦截器之后设置要应用的其他拦截器(或者指导者)<p>您可以指定任何AOP Alliance MethodInterceptors或其他Spring AOP建议,以及Spring AOP Ad
	 * visors。
	 * 
	 */
	public void setPostInterceptors(Object[] postInterceptors) {
		this.postInterceptors = postInterceptors;
	}

	/**
	 * Specify the AdvisorAdapterRegistry to use.
	 * Default is the global AdvisorAdapterRegistry.
	 * <p>
	 *  指定AdvisorAdapterRegistry使用Default是全局AdvisorAdapterRegistry
	 * 
	 * 
	 * @see org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry
	 */
	public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
		this.advisorAdapterRegistry = advisorAdapterRegistry;
	}

	/**
	 * Set the ClassLoader to generate the proxy class in.
	 * <p>Default is the bean ClassLoader, i.e. the ClassLoader used by the
	 * containing BeanFactory for loading all bean classes. This can be
	 * overridden here for specific proxies.
	 * <p>
	 *  设置ClassLoader在<p>中生成代理类默认是Bean ClassLoader,即包含BeanFactory用于加载所有bean类的ClassLoader可以在这里替代特定的代理
	 * 
	 */
	public void setProxyClassLoader(ClassLoader classLoader) {
		this.proxyClassLoader = classLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		if (this.proxyClassLoader == null) {
			this.proxyClassLoader = classLoader;
		}
	}


	@Override
	public void afterPropertiesSet() {
		if (this.target == null) {
			throw new IllegalArgumentException("Property 'target' is required");
		}
		if (this.target instanceof String) {
			throw new IllegalArgumentException("'target' needs to be a bean reference, not a bean name as value");
		}
		if (this.proxyClassLoader == null) {
			this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
		}

		ProxyFactory proxyFactory = new ProxyFactory();

		if (this.preInterceptors != null) {
			for (Object interceptor : this.preInterceptors) {
				proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
			}
		}

		// Add the main interceptor (typically an Advisor).
		proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(createMainInterceptor()));

		if (this.postInterceptors != null) {
			for (Object interceptor : this.postInterceptors) {
				proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
			}
		}

		proxyFactory.copyFrom(this);

		TargetSource targetSource = createTargetSource(this.target);
		proxyFactory.setTargetSource(targetSource);

		if (this.proxyInterfaces != null) {
			proxyFactory.setInterfaces(this.proxyInterfaces);
		}
		else if (!isProxyTargetClass()) {
			// Rely on AOP infrastructure to tell us what interfaces to proxy.
			proxyFactory.setInterfaces(
					ClassUtils.getAllInterfacesForClass(targetSource.getTargetClass(), this.proxyClassLoader));
		}

		postProcessProxyFactory(proxyFactory);

		this.proxy = proxyFactory.getProxy(this.proxyClassLoader);
	}

	/**
	 * Determine a TargetSource for the given target (or TargetSource).
	 * <p>
	 * 确定给定目标(或TargetSource)的TargetSource
	 * 
	 * 
	 * @param target target. If this is an implementation of TargetSource it is
	 * used as our TargetSource; otherwise it is wrapped in a SingletonTargetSource.
	 * @return a TargetSource for this object
	 */
	protected TargetSource createTargetSource(Object target) {
		if (target instanceof TargetSource) {
			return (TargetSource) target;
		}
		else {
			return new SingletonTargetSource(target);
		}
	}

	/**
	 * A hook for subclasses to post-process the {@link ProxyFactory}
	 * before creating the proxy instance with it.
	 * <p>
	 *  在创建代理实例之前,子类的钩子用于后处理{@link ProxyFactory}
	 * 
	 * 
	 * @param proxyFactory the AOP ProxyFactory about to be used
	 * @since 4.2
	 */
	protected void postProcessProxyFactory(ProxyFactory proxyFactory) {
	}


	@Override
	public Object getObject() {
		if (this.proxy == null) {
			throw new FactoryBeanNotInitializedException();
		}
		return this.proxy;
	}

	@Override
	public Class<?> getObjectType() {
		if (this.proxy != null) {
			return this.proxy.getClass();
		}
		if (this.proxyInterfaces != null && this.proxyInterfaces.length == 1) {
			return this.proxyInterfaces[0];
		}
		if (this.target instanceof TargetSource) {
			return ((TargetSource) this.target).getTargetClass();
		}
		if (this.target != null) {
			return this.target.getClass();
		}
		return null;
	}

	@Override
	public final boolean isSingleton() {
		return true;
	}


	/**
	 * Create the "main" interceptor for this proxy factory bean.
	 * Typically an Advisor, but can also be any type of Advice.
	 * <p>Pre-interceptors will be applied before, post-interceptors
	 * will be applied after this interceptor.
	 * <p>
	 *  为此代理工厂bean创建"主要"拦截器通常是一个顾问,但也可以是任何类型的建议,前拦截器将被应用于之前,拦截器将在此拦截器之后应用
	 */
	protected abstract Object createMainInterceptor();

}
