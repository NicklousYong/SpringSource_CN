/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2010-2012 the original author or authors.
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

package org.springframework.cache.interceptor;

import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * Proxy factory bean for simplified declarative caching handling.
 * This is a convenient alternative to a standard AOP
 * {@link org.springframework.aop.framework.ProxyFactoryBean}
 * with a separate {@link CacheInterceptor} definition.
 *
 * <p>This class is designed to facilitate declarative cache demarcation: namely, wrapping
 * a singleton target object with a caching proxy, proxying all the interfaces that the
 * target implements. Exists primarily for third-party framework integration.
 * <strong>Users should favor the {@code cache:} XML namespace
 * {@link org.springframework.cache.annotation.Cacheable @Cacheable} annotation.</strong>
 * See the <a href="http://bit.ly/p9rIvx">declarative annotation-based caching</a> section
 * of the Spring reference documentation for more information.
 *
 * <p>
 * 用于简化声明缓存处理的代理工厂bean这是一个方便的替代方法,它是标准AOP {@link orgspringframeworkaopframeworkProxyFactoryBean}的替代方法,它具
 * 有单独的{@link CacheInterceptor}定义。
 * 
 *  <p>此类旨在促进声明式缓存分界：即,使用缓存代理来包装单例目标对象,代理目标实现的所有接口存在主要用于第三方框架集成<strong>用户应该赞成{@代码缓存：} XML命名空间{@link orgspringframeworkcacheannotationCacheable @Cacheable}
 * 注释</strong>请参阅Spring参考文档中的<a href=\"http://bitly/p9rIvx\">基于声明性注释的缓存</a>部分更多信息。
 * 
 * 
 * @author Costin Leau
 * @since 3.1
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see CacheInterceptor
 */
@SuppressWarnings("serial")
public class CacheProxyFactoryBean extends AbstractSingletonProxyFactoryBean {

	private final CacheInterceptor cachingInterceptor = new CacheInterceptor();

	private Pointcut pointcut;


	/**
	 * Set a pointcut, i.e a bean that can cause conditional invocation
	 * of the CacheInterceptor depending on method and attributes passed.
	 * Note: Additional interceptors are always invoked.
	 * <p>
	 * 
	 * @see #setPreInterceptors
	 * @see #setPostInterceptors
	 */
	public void setPointcut(Pointcut pointcut) {
		this.pointcut = pointcut;
	}

	@Override
	protected Object createMainInterceptor() {
		this.cachingInterceptor.afterPropertiesSet();
		if (this.pointcut == null) {
			// Rely on default pointcut.
			throw new UnsupportedOperationException();
		}
		return new DefaultPointcutAdvisor(this.pointcut, this.cachingInterceptor);
	}

	/**
	 * Set the sources used to find cache operations.
	 * <p>
	 * 设置一个切入点,即根据传递的方法和属性可以导致对CacheInterceptor进行条件调用的bean注意：总是调用其他拦截器
	 * 
	 */
	public void setCacheOperationSources(CacheOperationSource... cacheOperationSources) {
		this.cachingInterceptor.setCacheOperationSources(cacheOperationSources);
	}

}
