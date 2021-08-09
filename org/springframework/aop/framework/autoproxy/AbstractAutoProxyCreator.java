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

package org.springframework.aop.framework.autoproxy;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyProcessorSupport;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.util.StringUtils;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that wraps each eligible bean with an AOP proxy, delegating to specified interceptors
 * before invoking the bean itself.
 *
 * <p>This class distinguishes between "common" interceptors: shared for all proxies it
 * creates, and "specific" interceptors: unique per bean instance. There need not
 * be any common interceptors. If there are, they are set using the interceptorNames
 * property. As with ProxyFactoryBean, interceptors names in the current factory
 * are used rather than bean references to allow correct handling of prototype
 * advisors and interceptors: for example, to support stateful mixins.
 * Any advice type is supported for "interceptorNames" entries.
 *
 * <p>Such auto-proxying is particularly useful if there's a large number of beans that
 * need to be wrapped with similar proxies, i.e. delegating to the same interceptors.
 * Instead of x repetitive proxy definitions for x target beans, you can register
 * one single such post processor with the bean factory to achieve the same effect.
 *
 * <p>Subclasses can apply any strategy to decide if a bean is to be proxied,
 * e.g. by type, by name, by definition details, etc. They can also return
 * additional interceptors that should just be applied to the specific bean
 * instance. The default concrete implementation is BeanNameAutoProxyCreator,
 * identifying the beans to be proxied via a list of bean names.
 *
 * <p>Any number of {@link TargetSourceCreator} implementations can be used to create
 * a custom target source - for example, to pool prototype objects. Auto-proxying will
 * occur even if there is no advice, as long as a TargetSourceCreator specifies a custom
 * {@link org.springframework.aop.TargetSource}. If there are no TargetSourceCreators set,
 * or if none matches, a {@link org.springframework.aop.target.SingletonTargetSource}
 * will be used by default to wrap the target bean instance.
 *
 * <p>
 *  {@link orgspringframeworkbeansfactoryconfigBeanPostProcessor}实现,它将每个符合条件的bean与AOP代理进行包装,在调用bean本身之前委
 * 托给指定的拦截器。
 * 
 * <p>此类区分"共同"拦截器：为其创建的所有代理共享,"特定"拦截器：每个bean实例唯一每个bean实例不需要任何常见的拦截器如果有的话,它们使用interceptorNames属性设置As Prox
 * yFactoryBean ,使用当前工厂中的拦截器名称而不是bean引用来允许正确处理原型顾问和拦截器：例如,支持状态混合任何"interceptorNames"条目都支持任何建议类型。
 * 
 * <p>如果有大量的bean需要用类似的代理程序包装,即委托给同一个拦截器,这样的自动代理特别有用。而不是x对象bean的x重复的代理定义,您可以注册一个这样的帖子处理器与豆厂达成同样的效果
 * 
 *  <p>子类可以应用任何策略来决定是否要代理bean,例如通过类型,按名称,定义细节等等。
 * 他们还可以返回应该应用于特定bean实例的其他拦截器默认的具体实现是BeanNameAutoProxyCreator,通过bean名称列表来标识要代理的bean。
 * 
 * <p>可以使用任何数量的{@link TargetSourceCreator}实现来创建自定义目标源 - 例如,对原型对象进行汇总自动代理将会发生,即使没有任何建议,只要TargetSourceCrea
 * tor指定自定义{ @link orgspringframeworkaopTargetSource}如果没有设置TargetSourceCreators,或者没有匹配,默认情况下将使用{@link orgspringframeworkaoptargetSingletonTargetSource}
 * 来包装目标bean实例。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Rob Harrop
 * @since 13.10.2003
 * @see #setInterceptorNames
 * @see #getAdvicesAndAdvisorsForBean
 * @see BeanNameAutoProxyCreator
 * @see DefaultAdvisorAutoProxyCreator
 */
@SuppressWarnings("serial")
public abstract class AbstractAutoProxyCreator extends ProxyProcessorSupport
		implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware {

	/**
	 * Convenience constant for subclasses: Return value for "do not proxy".
	 * <p>
	 *  子类的便利常数："不要代理"的返回值
	 * 
	 * 
	 * @see #getAdvicesAndAdvisorsForBean
	 */
	protected static final Object[] DO_NOT_PROXY = null;

	/**
	 * Convenience constant for subclasses: Return value for
	 * "proxy without additional interceptors, just the common ones".
	 * <p>
	 *  子类的便利常数："代理没有附加拦截器的代码",返回值为常用值
	 * 
	 * 
	 * @see #getAdvicesAndAdvisorsForBean
	 */
	protected static final Object[] PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS = new Object[0];


	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** Default is global AdvisorAdapterRegistry */
	private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();

	/**
	 * Indicates whether or not the proxy should be frozen. Overridden from super
	 * to prevent the configuration from becoming frozen too early.
	 * <p>
	 * 指示代理是否应该被冻结从超级版覆盖,以防止配置过早冻结
	 * 
	 */
	private boolean freezeProxy = false;

	/** Default is no common interceptors */
	private String[] interceptorNames = new String[0];

	private boolean applyCommonInterceptorsFirst = true;

	private TargetSourceCreator[] customTargetSourceCreators;

	private BeanFactory beanFactory;

	private final Set<String> targetSourcedBeans =
			Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));

	private final Set<Object> earlyProxyReferences =
			Collections.newSetFromMap(new ConcurrentHashMap<Object, Boolean>(16));

	private final Map<Object, Class<?>> proxyTypes = new ConcurrentHashMap<Object, Class<?>>(16);

	private final Map<Object, Boolean> advisedBeans = new ConcurrentHashMap<Object, Boolean>(256);


	/**
	 * Set whether or not the proxy should be frozen, preventing advice
	 * from being added to it once it is created.
	 * <p>Overridden from the super class to prevent the proxy configuration
	 * from being frozen before the proxy is created.
	 * <p>
	 *  设置代理是否应该被冻结,阻止在创建代理之后添加它的建议<p>从超级类覆盖以防止代理配置在创建代理之前被冻结
	 * 
	 */
	@Override
	public void setFrozen(boolean frozen) {
		this.freezeProxy = frozen;
	}

	@Override
	public boolean isFrozen() {
		return this.freezeProxy;
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
	 * Set custom TargetSourceCreators to be applied in this order.
	 * If the list is empty, or they all return null, a SingletonTargetSource
	 * will be created for each bean.
	 * <p>Note that TargetSourceCreators will kick in even for target beans
	 * where no advices or advisors have been found. If a TargetSourceCreator
	 * returns a TargetSource for a specific bean, that bean will be proxied
	 * in any case.
	 * <p>TargetSourceCreators can only be invoked if this post processor is used
	 * in a BeanFactory, and its BeanFactoryAware callback is used.
	 * <p>
	 * 设置定制的TargetSourceCreators以此顺序应用如果列表为空,或者都返回null,则将为每个bean创建一个SingletonTargetSource <p>请注意,TargetSourc
	 * eCreators甚至会在没有找到任何建议或顾问的目标bean中启动如果一个TargetSourceCreator为特定的bean返回一个TargetSource,则该bean在任何情况下都将被代理<p>
	 * 如果在BeanFactory中使用此后处理器,则只能调用TargetSourceCreators,并且使用其BeanFactoryAware回调。
	 * 
	 * 
	 * @param targetSourceCreators list of TargetSourceCreator.
	 * Ordering is significant: The TargetSource returned from the first matching
	 * TargetSourceCreator (that is, the first that returns non-null) will be used.
	 */
	public void setCustomTargetSourceCreators(TargetSourceCreator... targetSourceCreators) {
		this.customTargetSourceCreators = targetSourceCreators;
	}

	/**
	 * Set the common interceptors. These must be bean names in the current factory.
	 * They can be of any advice or advisor type Spring supports.
	 * <p>If this property isn't set, there will be zero common interceptors.
	 * This is perfectly valid, if "specific" interceptors such as matching
	 * Advisors are all we want.
	 * <p>
	 * 设置常见的拦截器这些必须是当前工厂中的bean名称他们可以是任何建议或顾问类型Spring支持<p>如果此属性未设置,将有零个常见拦截器这是完全有效的,如果"具体"拦截器如匹配顾问都是我们想要的
	 * 
	 */
	public void setInterceptorNames(String... interceptorNames) {
		this.interceptorNames = interceptorNames;
	}

	/**
	 * Set whether the common interceptors should be applied before bean-specific ones.
	 * Default is "true"; else, bean-specific interceptors will get applied first.
	 * <p>
	 *  设置在bean特定的默认值为"true"之前是否应用公共拦截器?否则,bean特定的拦截器将首先被应用
	 * 
	 */
	public void setApplyCommonInterceptorsFirst(boolean applyCommonInterceptorsFirst) {
		this.applyCommonInterceptorsFirst = applyCommonInterceptorsFirst;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Return the owning BeanFactory.
	 * May be {@code null}, as this object doesn't need to belong to a bean factory.
	 * <p>
	 *  返回拥有的BeanFactory可能是{@code null},因为这个对象不需要属于一个bean工厂
	 * 
	 */
	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}


	@Override
	public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
		if (this.proxyTypes.isEmpty()) {
			return null;
		}
		Object cacheKey = getCacheKey(beanClass, beanName);
		return this.proxyTypes.get(cacheKey);
	}

	@Override
	public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	@Override
	public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
		Object cacheKey = getCacheKey(bean.getClass(), beanName);
		if (!this.earlyProxyReferences.contains(cacheKey)) {
			this.earlyProxyReferences.add(cacheKey);
		}
		return wrapIfNecessary(bean, beanName, cacheKey);
	}

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		Object cacheKey = getCacheKey(beanClass, beanName);

		if (beanName == null || !this.targetSourcedBeans.contains(beanName)) {
			if (this.advisedBeans.containsKey(cacheKey)) {
				return null;
			}
			if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
				this.advisedBeans.put(cacheKey, Boolean.FALSE);
				return null;
			}
		}

		// Create proxy here if we have a custom TargetSource.
		// Suppresses unnecessary default instantiation of the target bean:
		// The TargetSource will handle target instances in a custom fashion.
		if (beanName != null) {
			TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
			if (targetSource != null) {
				this.targetSourcedBeans.add(beanName);
				Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
				Object proxy = createProxy(beanClass, beanName, specificInterceptors, targetSource);
				this.proxyTypes.put(cacheKey, proxy.getClass());
				return proxy;
			}
		}

		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) {
		return true;
	}

	@Override
	public PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {

		return pvs;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	/**
	 * Create a proxy with the configured interceptors if the bean is
	 * identified as one to proxy by the subclass.
	 * <p>
	 *  如果bean被标识为一个代理子类,则使用配置的拦截器创建一个代理
	 * 
	 * 
	 * @see #getAdvicesAndAdvisorsForBean
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean != null) {
			Object cacheKey = getCacheKey(bean.getClass(), beanName);
			if (!this.earlyProxyReferences.contains(cacheKey)) {
				return wrapIfNecessary(bean, beanName, cacheKey);
			}
		}
		return bean;
	}


	/**
	 * Build a cache key for the given bean class and bean name.
	 * <p>Note: As of 4.2.3, this implementation does not return a concatenated
	 * class/name String anymore but rather the most efficient cache key possible:
	 * a plain bean name, prepended with {@link BeanFactory#FACTORY_BEAN_PREFIX}
	 * in case of a {@code FactoryBean}; or if no bean name specified, then the
	 * given bean {@code Class} as-is.
	 * <p>
	 * 为给定的bean类和bean名称构建缓存键<p>注意：从423开始,此实现不再返回连接的类/名称String,而是可以使用最有效的缓存键：简单的bean名称,前面加上{ @link BeanFactory#FACTORY_BEAN_PREFIX}
	 * 在{@code FactoryBean}的情况下;或者如果没有指定bean名称,那么给定的bean {@code Class}就是这样的。
	 * 
	 * 
	 * @param beanClass the bean class
	 * @param beanName the bean name
	 * @return the cache key for the given class and name
	 */
	protected Object getCacheKey(Class<?> beanClass, String beanName) {
		if (StringUtils.hasLength(beanName)) {
			return (FactoryBean.class.isAssignableFrom(beanClass) ?
					BeanFactory.FACTORY_BEAN_PREFIX + beanName : beanName);
		}
		else {
			return beanClass;
		}
	}

	/**
	 * Wrap the given bean if necessary, i.e. if it is eligible for being proxied.
	 * <p>
	 *  如果有必要,包装给定的bean,即如果它有资格被代理
	 * 
	 * 
	 * @param bean the raw bean instance
	 * @param beanName the name of the bean
	 * @param cacheKey the cache key for metadata access
	 * @return a proxy wrapping the bean, or the raw bean instance as-is
	 */
	protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
		if (beanName != null && this.targetSourcedBeans.contains(beanName)) {
			return bean;
		}
		if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
			return bean;
		}
		if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
			this.advisedBeans.put(cacheKey, Boolean.FALSE);
			return bean;
		}

		// Create proxy if we have advice.
		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
		if (specificInterceptors != DO_NOT_PROXY) {
			this.advisedBeans.put(cacheKey, Boolean.TRUE);
			Object proxy = createProxy(
					bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
			this.proxyTypes.put(cacheKey, proxy.getClass());
			return proxy;
		}

		this.advisedBeans.put(cacheKey, Boolean.FALSE);
		return bean;
	}

	/**
	 * Return whether the given bean class represents an infrastructure class
	 * that should never be proxied.
	 * <p>The default implementation considers Advices, Advisors and
	 * AopInfrastructureBeans as infrastructure classes.
	 * <p>
	 *  返回给定的bean类是否代表不应该被代理的基础设施类。默认实现将Advice,Advisors和AopInfrastructureBeans作为基础架构类
	 * 
	 * 
	 * @param beanClass the class of the bean
	 * @return whether the bean represents an infrastructure class
	 * @see org.aopalliance.aop.Advice
	 * @see org.springframework.aop.Advisor
	 * @see org.springframework.aop.framework.AopInfrastructureBean
	 * @see #shouldSkip
	 */
	protected boolean isInfrastructureClass(Class<?> beanClass) {
		boolean retVal = Advice.class.isAssignableFrom(beanClass) ||
				Pointcut.class.isAssignableFrom(beanClass) ||
				Advisor.class.isAssignableFrom(beanClass) ||
				AopInfrastructureBean.class.isAssignableFrom(beanClass);
		if (retVal && logger.isTraceEnabled()) {
			logger.trace("Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
		}
		return retVal;
	}

	/**
	 * Subclasses should override this method to return {@code true} if the
	 * given bean should not be considered for auto-proxying by this post-processor.
	 * <p>Sometimes we need to be able to avoid this happening if it will lead to
	 * a circular reference. This implementation returns {@code false}.
	 * <p>
	 * 如果给定的bean不应被考虑用于这个后处理器的自动代理,子类应该覆盖此方法返回{@code true} <p>有时候,如果导致循环引用,我们需要避免这种情况发生此实现返回{@code false}
	 * 
	 * 
	 * @param beanClass the class of the bean
	 * @param beanName the name of the bean
	 * @return whether to skip the given bean
	 */
	protected boolean shouldSkip(Class<?> beanClass, String beanName) {
		return false;
	}

	/**
	 * Create a target source for bean instances. Uses any TargetSourceCreators if set.
	 * Returns {@code null} if no custom TargetSource should be used.
	 * <p>This implementation uses the "customTargetSourceCreators" property.
	 * Subclasses can override this method to use a different mechanism.
	 * <p>
	 *  创建bean实例的目标源如果设置,则使用任何TargetSourceCreators如果不使用自定义的TargetSource,则返回{@code null} <p>此实现使用"customTarge
	 * tSourceCreators"属性子类可以覆盖此方法以使用其他机制。
	 * 
	 * 
	 * @param beanClass the class of the bean to create a TargetSource for
	 * @param beanName the name of the bean
	 * @return a TargetSource for this bean
	 * @see #setCustomTargetSourceCreators
	 */
	protected TargetSource getCustomTargetSource(Class<?> beanClass, String beanName) {
		// We can't create fancy target sources for directly registered singletons.
		if (this.customTargetSourceCreators != null &&
				this.beanFactory != null && this.beanFactory.containsBean(beanName)) {
			for (TargetSourceCreator tsc : this.customTargetSourceCreators) {
				TargetSource ts = tsc.getTargetSource(beanClass, beanName);
				if (ts != null) {
					// Found a matching TargetSource.
					if (logger.isDebugEnabled()) {
						logger.debug("TargetSourceCreator [" + tsc +
								" found custom TargetSource for bean with name '" + beanName + "'");
					}
					return ts;
				}
			}
		}

		// No custom TargetSource found.
		return null;
	}

	/**
	 * Create an AOP proxy for the given bean.
	 * <p>
	 *  为给定的bean创建AOP代理
	 * 
	 * 
	 * @param beanClass the class of the bean
	 * @param beanName the name of the bean
	 * @param specificInterceptors the set of interceptors that is
	 * specific to this bean (may be empty, but not null)
	 * @param targetSource the TargetSource for the proxy,
	 * already pre-configured to access the bean
	 * @return the AOP proxy for the bean
	 * @see #buildAdvisors
	 */
	protected Object createProxy(
			Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {

		if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
			AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory) this.beanFactory, beanName, beanClass);
		}

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.copyFrom(this);

		if (!proxyFactory.isProxyTargetClass()) {
			if (shouldProxyTargetClass(beanClass, beanName)) {
				proxyFactory.setProxyTargetClass(true);
			}
			else {
				evaluateProxyInterfaces(beanClass, proxyFactory);
			}
		}

		Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
		for (Advisor advisor : advisors) {
			proxyFactory.addAdvisor(advisor);
		}

		proxyFactory.setTargetSource(targetSource);
		customizeProxyFactory(proxyFactory);

		proxyFactory.setFrozen(this.freezeProxy);
		if (advisorsPreFiltered()) {
			proxyFactory.setPreFiltered(true);
		}

		return proxyFactory.getProxy(getProxyClassLoader());
	}

	/**
	 * Determine whether the given bean should be proxied with its target class rather than its interfaces.
	 * <p>Checks the {@link AutoProxyUtils#PRESERVE_TARGET_CLASS_ATTRIBUTE "preserveTargetClass" attribute}
	 * of the corresponding bean definition.
	 * <p>
	 * 确定给定的b​​ean是否应该与其目标类代替,而不是其接口<p>检查相应bean定义的{@link AutoProxyUtils#PRESERVE_TARGET_CLASS_ATTRIBUTE"preserveTargetClass"attribute"。
	 * 
	 * 
	 * @param beanClass the class of the bean
	 * @param beanName the name of the bean
	 * @return whether the given bean should be proxied with its target class
	 * @see AutoProxyUtils#shouldProxyTargetClass
	 */
	protected boolean shouldProxyTargetClass(Class<?> beanClass, String beanName) {
		return (this.beanFactory instanceof ConfigurableListableBeanFactory &&
				AutoProxyUtils.shouldProxyTargetClass((ConfigurableListableBeanFactory) this.beanFactory, beanName));
	}

	/**
	 * Return whether the Advisors returned by the subclass are pre-filtered
	 * to match the bean's target class already, allowing the ClassFilter check
	 * to be skipped when building advisors chains for AOP invocations.
	 * <p>Default is {@code false}. Subclasses may override this if they
	 * will always return pre-filtered Advisors.
	 * <p>
	 *  返回子类返回的顾问是否已经过滤,以匹配bean的目标类,允许在为AOP调用构建顾问链时跳过ClassFilter检查<p>默认值为{@code false}如果子类可以覆盖将始终返回预先过滤的顾问
	 * 
	 * 
	 * @return whether the Advisors are pre-filtered
	 * @see #getAdvicesAndAdvisorsForBean
	 * @see org.springframework.aop.framework.Advised#setPreFiltered
	 */
	protected boolean advisorsPreFiltered() {
		return false;
	}

	/**
	 * Determine the advisors for the given bean, including the specific interceptors
	 * as well as the common interceptor, all adapted to the Advisor interface.
	 * <p>
	 *  确定给定bean的顾问,包括特定拦截器以及通用拦截器,所有拦截器都适用于Advisor界面
	 * 
	 * 
	 * @param beanName the name of the bean
	 * @param specificInterceptors the set of interceptors that is
	 * specific to this bean (may be empty, but not null)
	 * @return the list of Advisors for the given bean
	 */
	protected Advisor[] buildAdvisors(String beanName, Object[] specificInterceptors) {
		// Handle prototypes correctly...
		Advisor[] commonInterceptors = resolveInterceptorNames();

		List<Object> allInterceptors = new ArrayList<Object>();
		if (specificInterceptors != null) {
			allInterceptors.addAll(Arrays.asList(specificInterceptors));
			if (commonInterceptors.length > 0) {
				if (this.applyCommonInterceptorsFirst) {
					allInterceptors.addAll(0, Arrays.asList(commonInterceptors));
				}
				else {
					allInterceptors.addAll(Arrays.asList(commonInterceptors));
				}
			}
		}
		if (logger.isDebugEnabled()) {
			int nrOfCommonInterceptors = commonInterceptors.length;
			int nrOfSpecificInterceptors = (specificInterceptors != null ? specificInterceptors.length : 0);
			logger.debug("Creating implicit proxy for bean '" + beanName + "' with " + nrOfCommonInterceptors +
					" common interceptors and " + nrOfSpecificInterceptors + " specific interceptors");
		}

		Advisor[] advisors = new Advisor[allInterceptors.size()];
		for (int i = 0; i < allInterceptors.size(); i++) {
			advisors[i] = this.advisorAdapterRegistry.wrap(allInterceptors.get(i));
		}
		return advisors;
	}

	/**
	 * Resolves the specified interceptor names to Advisor objects.
	 * <p>
	 *  将指定的拦截器名称解析为Advisor对象
	 * 
	 * 
	 * @see #setInterceptorNames
	 */
	private Advisor[] resolveInterceptorNames() {
		ConfigurableBeanFactory cbf = (this.beanFactory instanceof ConfigurableBeanFactory ?
				(ConfigurableBeanFactory) this.beanFactory : null);
		List<Advisor> advisors = new ArrayList<Advisor>();
		for (String beanName : this.interceptorNames) {
			if (cbf == null || !cbf.isCurrentlyInCreation(beanName)) {
				Object next = this.beanFactory.getBean(beanName);
				advisors.add(this.advisorAdapterRegistry.wrap(next));
			}
		}
		return advisors.toArray(new Advisor[advisors.size()]);
	}

	/**
	 * Subclasses may choose to implement this: for example,
	 * to change the interfaces exposed.
	 * <p>The default implementation is empty.
	 * <p>
	 * 子类可以选择实现：例如,更改暴露的接口<p>默认实现为空
	 * 
	 * 
	 * @param proxyFactory ProxyFactory that is already configured with
	 * TargetSource and interfaces and will be used to create the proxy
	 * immediately after this method returns
	 */
	protected void customizeProxyFactory(ProxyFactory proxyFactory) {
	}


	/**
	 * Return whether the given bean is to be proxied, what additional
	 * advices (e.g. AOP Alliance interceptors) and advisors to apply.
	 * <p>
	 *  返回给定的bean是否被代理,还有什么额外的建议(例如AOP联盟拦截器)和顾问来应用
	 * 
	 * @param beanClass the class of the bean to advise
	 * @param beanName the name of the bean
	 * @param customTargetSource the TargetSource returned by the
	 * {@link #getCustomTargetSource} method: may be ignored.
	 * Will be {@code null} if no custom target source is in use.
	 * @return an array of additional interceptors for the particular bean;
	 * or an empty array if no additional interceptors but just the common ones;
	 * or {@code null} if no proxy at all, not even with the common interceptors.
	 * See constants DO_NOT_PROXY and PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS.
	 * @throws BeansException in case of errors
	 * @see #DO_NOT_PROXY
	 * @see #PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS
	 */
	protected abstract Object[] getAdvicesAndAdvisorsForBean(
			Class<?> beanClass, String beanName, TargetSource customTargetSource) throws BeansException;

}
