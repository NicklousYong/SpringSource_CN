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

package org.springframework.aop.framework;

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * Base class with common functionality for proxy processors, in particular
 * ClassLoader management and the {@link #evaluateProxyInterfaces} algorithm.
 *
 * <p>
 *  具有代理处理器通用功能的基类,特别是ClassLoader管理和{@link #evaluateProxyInterfaces}算法
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.1
 * @see AbstractAdvisingBeanPostProcessor
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator
 */
@SuppressWarnings("serial")
public class ProxyProcessorSupport extends ProxyConfig implements Ordered, BeanClassLoaderAware, AopInfrastructureBean {

	/**
	 * This should run after all other processors, so that it can just add
	 * an advisor to existing proxies rather than double-proxy.
	 * <p>
	 * 这应该在所有其他处理器之后运行,以便它可以为现有代理添加一个顾问,而不是双重代理
	 * 
	 */
	private int order = Ordered.LOWEST_PRECEDENCE;

	private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();

	private boolean classLoaderConfigured = false;


	/**
	 * Set the ordering which will apply to this class's implementation
	 * of Ordered, used when applying multiple processors.
	 * <p>Default value is {@code Integer.MAX_VALUE}, meaning that it's non-ordered.
	 * <p>
	 *  设置适用于此类的Order的排序,在应用多个处理器时使用"p"默认值为{@code IntegerMAX_VALUE},这意味着它是无序的
	 * 
	 * 
	 * @param order the ordering value
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * Set the ClassLoader to generate the proxy class in.
	 * <p>Default is the bean ClassLoader, i.e. the ClassLoader used by the containing
	 * {@link org.springframework.beans.factory.BeanFactory} for loading all bean classes.
	 * This can be overridden here for specific proxies.
	 * <p>
	 *  设置ClassLoader以在<p>中生成代理类。
	 * 默认是Bean ClassLoader,即包含{@link orgspringframeworkbeansfactoryBeanFactory}用于加载所有bean类的ClassLoader可以在这里替
	 * 代特定的代理。
	 *  设置ClassLoader以在<p>中生成代理类。
	 * 
	 */
	public void setProxyClassLoader(ClassLoader classLoader) {
		this.proxyClassLoader = classLoader;
		this.classLoaderConfigured = (classLoader != null);
	}

	/**
	 * Return the configured proxy ClassLoader for this processor.
	 * <p>
	 *  返回此处理器的配置代理ClassLoader
	 * 
	 */
	protected ClassLoader getProxyClassLoader() {
		return this.proxyClassLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		if (!this.classLoaderConfigured) {
			this.proxyClassLoader = classLoader;
		}
	}


	/**
	 * Check the interfaces on the given bean class and apply them to the {@link ProxyFactory},
	 * if appropriate.
	 * <p>Calls {@link #isConfigurationCallbackInterface} and {@link #isInternalLanguageInterface}
	 * to filter for reasonable proxy interfaces, falling back to a target-class proxy otherwise.
	 * <p>
	 * 检查给定bean类中的接口,并将其应用于{@link ProxyFactory}(如果适用)<p>调用{@link #isConfigurationCallbackInterface}和{@link #isInternalLanguageInterface}
	 * 过滤合理的代理接口,返回目标-class代理。
	 * 
	 * 
	 * @param beanClass the class of the bean
	 * @param proxyFactory the ProxyFactory for the bean
	 */
	protected void evaluateProxyInterfaces(Class<?> beanClass, ProxyFactory proxyFactory) {
		Class<?>[] targetInterfaces = ClassUtils.getAllInterfacesForClass(beanClass, getProxyClassLoader());
		boolean hasReasonableProxyInterface = false;
		for (Class<?> ifc : targetInterfaces) {
			if (!isConfigurationCallbackInterface(ifc) && !isInternalLanguageInterface(ifc) &&
					ifc.getMethods().length > 0) {
				hasReasonableProxyInterface = true;
				break;
			}
		}
		if (hasReasonableProxyInterface) {
			// Must allow for introductions; can't just set interfaces to the target's interfaces only.
			for (Class<?> ifc : targetInterfaces) {
				proxyFactory.addInterface(ifc);
			}
		}
		else {
			proxyFactory.setProxyTargetClass(true);
		}
	}

	/**
	 * Determine whether the given interface is just a container callback and
	 * therefore not to be considered as a reasonable proxy interface.
	 * <p>If no reasonable proxy interface is found for a given bean, it will get
	 * proxied with its full target class, assuming that as the user's intention.
	 * <p>
	 *  确定给定的接口是否只是一个容器回调,因此不被认为是合理的代理接口。如果没有为给定的bean找到合适的代理接口,那么它将被代理其完整的目标类,假设为用户的意图
	 * 
	 * 
	 * @param ifc the interface to check
	 * @return whether the given interface is just a container callback
	 */
	protected boolean isConfigurationCallbackInterface(Class<?> ifc) {
		return (InitializingBean.class == ifc || DisposableBean.class == ifc ||
				ObjectUtils.containsElement(ifc.getInterfaces(), Aware.class));
	}

	/**
	 * Determine whether the given interface is a well-known internal language interface
	 * and therefore not to be considered as a reasonable proxy interface.
	 * <p>If no reasonable proxy interface is found for a given bean, it will get
	 * proxied with its full target class, assuming that as the user's intention.
	 * <p>
	 * 确定给定的接口是否是一个众所周知的内部语言接口,因此不被认为是合理的代理接口。如果没有为给定的bean找到合适的代理接口,它将被代理其完整的目标类,假定作为用户的意图
	 * 
	 * @param ifc the interface to check
	 * @return whether the given interface is an internal language interface
	 */
	protected boolean isInternalLanguageInterface(Class<?> ifc) {
		return (ifc.getName().equals("groovy.lang.GroovyObject") ||
				ifc.getName().endsWith(".cglib.proxy.Factory"));
	}

}
