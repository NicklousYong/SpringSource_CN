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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.Advisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Base class for {@link BeanPostProcessor} implementations that apply a
 * Spring AOP {@link Advisor} to specific beans.
 *
 * <p>
 *  适用于特定bean的Spring AOP {@link Advisor}的{@link BeanPostProcessor}实现的基类
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.2
 */
@SuppressWarnings("serial")
public abstract class AbstractAdvisingBeanPostProcessor extends ProxyProcessorSupport implements BeanPostProcessor {

	protected Advisor advisor;

	protected boolean beforeExistingAdvisors = false;

	private final Map<Class<?>, Boolean> eligibleBeans = new ConcurrentHashMap<Class<?>, Boolean>(256);


	/**
	 * Set whether this post-processor's advisor is supposed to apply before
	 * existing advisors when encountering a pre-advised object.
	 * <p>Default is "false", applying the advisor after existing advisors, i.e.
	 * as close as possible to the target method. Switch this to "true" in order
	 * for this post-processor's advisor to wrap existing advisors as well.
	 * <p>Note: Check the concrete post-processor's javadoc whether it possibly
	 * changes this flag by default, depending on the nature of its advisor.
	 * <p>
	 * 设置这个后处理器的顾问是否应该在遇到预先建议的对象之前应用于现有顾问之前?默认是"false",在现有顾问之后应用顾问,即尽可能靠近目标方法切换到"为了让这个后处理器的顾问也可以包装现有的顾问,请注意：
	 * 检查具体的后处理器的javadoc是否可以默认地更改此标志,具体取决于其顾问的性质。
	 * 
	 */
	public void setBeforeExistingAdvisors(boolean beforeExistingAdvisors) {
		this.beforeExistingAdvisors = beforeExistingAdvisors;
	}


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (bean instanceof AopInfrastructureBean) {
			// Ignore AOP infrastructure such as scoped proxies.
			return bean;
		}

		if (bean instanceof Advised) {
			Advised advised = (Advised) bean;
			if (!advised.isFrozen() && isEligible(AopUtils.getTargetClass(bean))) {
				// Add our local Advisor to the existing proxy's Advisor chain...
				if (this.beforeExistingAdvisors) {
					advised.addAdvisor(0, this.advisor);
				}
				else {
					advised.addAdvisor(this.advisor);
				}
				return bean;
			}
		}

		if (isEligible(bean, beanName)) {
			ProxyFactory proxyFactory = prepareProxyFactory(bean, beanName);
			if (!proxyFactory.isProxyTargetClass()) {
				evaluateProxyInterfaces(bean.getClass(), proxyFactory);
			}
			proxyFactory.addAdvisor(this.advisor);
			customizeProxyFactory(proxyFactory);
			return proxyFactory.getProxy(getProxyClassLoader());
		}

		// No async proxy needed.
		return bean;
	}

	/**
	 * Check whether the given bean is eligible for advising with this
	 * post-processor's {@link Advisor}.
	 * <p>Delegates to {@link #isEligible(Class)} for target class checking.
	 * Can be overridden e.g. to specifically exclude certain beans by name.
	 * <p>Note: Only called for regular bean instances but not for existing
	 * proxy instances which implement {@link Advised} and allow for adding
	 * the local {@link Advisor} to the existing proxy's {@link Advisor} chain.
	 * For the latter, {@link #isEligible(Class)} is being called directly,
	 * with the actual target class behind the existing proxy (as determined
	 * by {@link AopUtils#getTargetClass(Object)}).
	 * <p>
	 * 检查给定的bean是否有资格与此后处理器的{@link Advisor} <p>代表{@link #isEligible(Class)}的代理人进行目标类检查建议。
	 * 可以覆盖,例如,以名称<p >注意：仅调用常规bean实例,但不调用实现{@link Advised}的现有代理实例,并允许将本地{@link Advisor}添加到现有代理的{@link Advisor}
	 * 链中。
	 * 检查给定的bean是否有资格与此后处理器的{@link Advisor} <p>代表{@link #isEligible(Class)}的代理人进行目标类检查建议。
	 * 对于后者,{@link #isEligible(Class)}被直接调用,实际目标类位于现有代理之后(由{@link AopUtils#getTargetClass(Object)}确定)。
	 * 
	 * 
	 * @param bean the bean instance
	 * @param beanName the name of the bean
	 * @see #isEligible(Class)
	 */
	protected boolean isEligible(Object bean, String beanName) {
		return isEligible(bean.getClass());
	}

	/**
	 * Check whether the given class is eligible for advising with this
	 * post-processor's {@link Advisor}.
	 * <p>Implements caching of {@code canApply} results per bean target class.
	 * <p>
	 * 检查给定的类是否有资格与此后处理器的{@link顾问}进行建议<p>实现每个bean目标类的{@code canApply}结果的缓存
	 * 
	 * 
	 * @param targetClass the class to check against
	 * @see AopUtils#canApply(Advisor, Class)
	 */
	protected boolean isEligible(Class<?> targetClass) {
		Boolean eligible = this.eligibleBeans.get(targetClass);
		if (eligible != null) {
			return eligible;
		}
		eligible = AopUtils.canApply(this.advisor, targetClass);
		this.eligibleBeans.put(targetClass, eligible);
		return eligible;
	}

	/**
	 * Prepare a {@link ProxyFactory} for the given bean.
	 * <p>Subclasses may customize the handling of the target instance and in
	 * particular the exposure of the target class. The default introspection
	 * of interfaces for non-target-class proxies and the configured advisor
	 * will be applied afterwards; {@link #customizeProxyFactory} allows for
	 * late customizations of those parts right before proxy creation.
	 * <p>
	 *  为给定的bean准备{@link ProxyFactory} <p>子类可以自定义目标实例的处理,特别是目标类的暴露。
	 * 非目标级代理和配置的顾问的接口的默认内省将是后来申请{@link #customizeProxyFactory}允许在代理创建之前对这些部分进行后期定制。
	 * 
	 * 
	 * @param bean the bean instance to create a proxy for
	 * @param beanName the corresponding bean name
	 * @return the ProxyFactory, initialized with this processor's
	 * {@link ProxyConfig} settings and the specified bean
	 * @since 4.2.3
	 * @see #customizeProxyFactory
	 */
	protected ProxyFactory prepareProxyFactory(Object bean, String beanName) {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.copyFrom(this);
		proxyFactory.setTarget(bean);
		return proxyFactory;
	}

	/**
	 * Subclasses may choose to implement this: for example,
	 * to change the interfaces exposed.
	 * <p>The default implementation is empty.
	 * <p>
	 * 
	 * @param proxyFactory ProxyFactory that is already configured with
	 * target, advisor and interfaces and will be used to create the proxy
	 * immediately after this method returns
	 * @since 4.2.3
	 * @see #prepareProxyFactory
	 */
	protected void customizeProxyFactory(ProxyFactory proxyFactory) {
	}

}
