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

package org.springframework.aop.aspectj.annotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.reflect.PerClauseKind;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJProxyUtils;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.ProxyCreatorSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * AspectJ-based proxy factory, allowing for programmatic building
 * of proxies which include AspectJ aspects (code style as well
 * Java 5 annotation style).
 *
 * <p>
 *  基于AspectJ的代理工厂,允许编程构建代理,其中包括AspectJ方面(代码风格以及Java 5注释样式)
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @since 2.0
 * @see #addAspect(Object)
 * @see #addAspect(Class)
 * @see #getProxy()
 * @see #getProxy(ClassLoader)
 * @see org.springframework.aop.framework.ProxyFactory
 */
@SuppressWarnings("serial")
public class AspectJProxyFactory extends ProxyCreatorSupport {

	/** Cache for singleton aspect instances */
	private static final Map<Class<?>, Object> aspectCache = new HashMap<Class<?>, Object>();

	private final AspectJAdvisorFactory aspectFactory = new ReflectiveAspectJAdvisorFactory();


	/**
	 * Create a new AspectJProxyFactory.
	 * <p>
	 *  创建一个新的AspectJProxyFactory
	 * 
	 */
	public AspectJProxyFactory() {
	}

	/**
	 * Create a new AspectJProxyFactory.
	 * <p>Will proxy all interfaces that the given target implements.
	 * <p>
	 * 创建一个新的AspectJProxyFactory <p>将代理给定目标实现的所有接口
	 * 
	 * 
	 * @param target the target object to be proxied
	 */
	public AspectJProxyFactory(Object target) {
		Assert.notNull(target, "Target object must not be null");
		setInterfaces(ClassUtils.getAllInterfaces(target));
		setTarget(target);
	}

	/**
	 * Create a new {@code AspectJProxyFactory}.
	 * No target, only interfaces. Must add interceptors.
	 * <p>
	 *  创建一个新的{@code AspectJProxyFactory}没有目标,只有接口必须添加拦截器
	 * 
	 */
	public AspectJProxyFactory(Class<?>... interfaces) {
		setInterfaces(interfaces);
	}


	/**
	 * Add the supplied aspect instance to the chain. The type of the aspect instance
	 * supplied must be a singleton aspect. True singleton lifecycle is not honoured when
	 * using this method - the caller is responsible for managing the lifecycle of any
	 * aspects added in this way.
	 * <p>
	 *  将提供的aspect实例添加到链中提供的aspect实例的类型必须是单例方面使用此方法时,True singleton生命周期不会被执行 - 调用者负责管理以这种方式添加的任何方面的生命周期
	 * 
	 * 
	 * @param aspectInstance the AspectJ aspect instance
	 */
	public void addAspect(Object aspectInstance) {
		Class<?> aspectClass = aspectInstance.getClass();
		String aspectName = aspectClass.getName();
		AspectMetadata am = createAspectMetadata(aspectClass, aspectName);
		if (am.getAjType().getPerClause().getKind() != PerClauseKind.SINGLETON) {
			throw new IllegalArgumentException(
					"Aspect class [" + aspectClass.getName() + "] does not define a singleton aspect");
		}
		addAdvisorsFromAspectInstanceFactory(
				new SingletonMetadataAwareAspectInstanceFactory(aspectInstance, aspectName));
	}

	/**
	 * Add an aspect of the supplied type to the end of the advice chain.
	 * <p>
	 *  将提供的类型的一个方面添加到建议链的末尾
	 * 
	 * 
	 * @param aspectClass the AspectJ aspect class
	 */
	public void addAspect(Class<?> aspectClass) {
		String aspectName = aspectClass.getName();
		AspectMetadata am = createAspectMetadata(aspectClass, aspectName);
		MetadataAwareAspectInstanceFactory instanceFactory = createAspectInstanceFactory(am, aspectClass, aspectName);
		addAdvisorsFromAspectInstanceFactory(instanceFactory);
	}


	/**
	 * Add all {@link Advisor Advisors} from the supplied {@link MetadataAwareAspectInstanceFactory}
	 * to the current chain. Exposes any special purpose {@link Advisor Advisors} if needed.
	 * <p>
	 *  将所有{@link Advisor顾问}从所提供的{@link MetadataAwareAspectInstanceFactory}添加到当前链中(如果需要的话)展示任何特殊目的{@link Advisor顾问}
	 * 。
	 * 
	 * 
	 * @see AspectJProxyUtils#makeAdvisorChainAspectJCapableIfNecessary(List)
	 */
	private void addAdvisorsFromAspectInstanceFactory(MetadataAwareAspectInstanceFactory instanceFactory) {
		List<Advisor> advisors = this.aspectFactory.getAdvisors(instanceFactory);
		advisors = AopUtils.findAdvisorsThatCanApply(advisors, getTargetClass());
		AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(advisors);
		AnnotationAwareOrderComparator.sort(advisors);
		addAdvisors(advisors);
	}

	/**
	 * Create an {@link AspectMetadata} instance for the supplied aspect type.
	 * <p>
	 *  为提供的方面类型创建{@link AspectMetadata}实例
	 * 
	 */
	private AspectMetadata createAspectMetadata(Class<?> aspectClass, String aspectName) {
		AspectMetadata am = new AspectMetadata(aspectClass, aspectName);
		if (!am.getAjType().isAspect()) {
			throw new IllegalArgumentException("Class [" + aspectClass.getName() + "] is not a valid aspect type");
		}
		return am;
	}

	/**
	 * Create a {@link MetadataAwareAspectInstanceFactory} for the supplied aspect type. If the aspect type
	 * has no per clause, then a {@link SingletonMetadataAwareAspectInstanceFactory} is returned, otherwise
	 * a {@link PrototypeAspectInstanceFactory} is returned.
	 * <p>
	 * 为所提供的方面类型创建{@link MetadataAwareAspectInstanceFactory}如果aspect类型没有per子句,则返回{@link SingletonMetadataAwareAspectInstanceFactory}
	 * ,否则返回{@link PrototypeAspectInstanceFactory}。
	 * 
	 */
	private MetadataAwareAspectInstanceFactory createAspectInstanceFactory(
			AspectMetadata am, Class<?> aspectClass, String aspectName) {

		MetadataAwareAspectInstanceFactory instanceFactory = null;
		if (am.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
			// Create a shared aspect instance.
			Object instance = getSingletonAspectInstance(aspectClass);
			instanceFactory = new SingletonMetadataAwareAspectInstanceFactory(instance, aspectName);
		}
		else {
			// Create a factory for independent aspect instances.
			instanceFactory = new SimpleMetadataAwareAspectInstanceFactory(aspectClass, aspectName);
		}
		return instanceFactory;
	}

	/**
	 * Get the singleton aspect instance for the supplied aspect type. An instance
	 * is created if one cannot be found in the instance cache.
	 * <p>
	 *  获取提供的方面类型的单例方面实例如果在实例缓存中找不到实例,则会创建一个实例
	 * 
	 */
	private Object getSingletonAspectInstance(Class<?> aspectClass) {
		synchronized (aspectCache) {
			Object instance = aspectCache.get(aspectClass);
			if (instance != null) {
				return instance;
			}
			try {
				instance = aspectClass.newInstance();
				aspectCache.put(aspectClass, instance);
				return instance;
			}
			catch (InstantiationException ex) {
				throw new AopConfigException("Unable to instantiate aspect class [" + aspectClass.getName() + "]", ex);
			}
			catch (IllegalAccessException ex) {
				throw new AopConfigException("Cannot access aspect class [" + aspectClass.getName() + "]", ex);
			}
		}
	}


	/**
	 * Create a new proxy according to the settings in this factory.
	 * <p>Can be called repeatedly. Effect will vary if we've added
	 * or removed interfaces. Can add and remove interceptors.
	 * <p>Uses a default class loader: Usually, the thread context class loader
	 * (if necessary for proxy creation).
	 * <p>
	 *  根据这个工厂的设置创建一个新的代理<p>可以重复调用如果添加或删除接口,效果会有所不同可以添加和删除拦截器<p>使用默认的类加载器：通常,线程上下文类加载器(如果需要代理创建)
	 * 
	 * 
	 * @return the new proxy
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProxy() {
		return (T) createAopProxy().getProxy();
	}

	/**
	 * Create a new proxy according to the settings in this factory.
	 * <p>Can be called repeatedly. Effect will vary if we've added
	 * or removed interfaces. Can add and remove interceptors.
	 * <p>Uses the given class loader (if necessary for proxy creation).
	 * <p>
	 * 根据这个工厂的设置创建一个新的代理<p>可以反复调用如果添加或删除接口,效果会有所不同可以添加和删除拦截器<p>使用给定的类加载器(如果需要代理创建)
	 * 
	 * @param classLoader the class loader to create the proxy with
	 * @return the new proxy
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProxy(ClassLoader classLoader) {
		return (T) createAopProxy().getProxy(classLoader);
	}

}
