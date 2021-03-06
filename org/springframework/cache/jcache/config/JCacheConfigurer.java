/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.cache.jcache.config;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheResolver;

/**
 * Extension of {@link CachingConfigurer} for the JSR-107 implementation.
 *
 * <p>To be implemented by classes annotated with
 * {@link org.springframework.cache.annotation.EnableCaching} that wish
 * or need to specify explicitly how exception caches are resolved for
 * annotation-driven cache management. Consider extending {@link JCacheConfigurerSupport},
 * which provides a stub implementation of all interface methods.
 *
 * <p>See {@link org.springframework.cache.annotation.EnableCaching} for
 * general examples and context; see {@link #exceptionCacheResolver()} for
 * detailed instructions.
 *
 * <p>
 *  扩展JSR-107实现的{@link CachingConfigurer}
 * 
 * <p>要通过使用{@link orgspringframeworkcacheannotationEnableCaching}注释的类来实现,它希望或需要明确指定如何为注释驱动的缓存管理解决异常缓存。
 * 考虑扩展{@link JCacheConfigurerSupport},它提供了所有接口方法的存根实现。
 * 
 *  <p>有关一般示例和上下文,请参阅{@link orgspringframeworkcacheannotationEnableCaching};有关详细说明,请参阅{@link #exceptionCacheResolver()}
 * 。
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 * @see CachingConfigurer
 * @see JCacheConfigurerSupport
 * @see org.springframework.cache.annotation.EnableCaching
 */
public interface JCacheConfigurer extends CachingConfigurer {

	/**
	 * Return the {@link CacheResolver} bean to use to resolve exception caches for
	 * annotation-driven cache management. Implementations must explicitly declare
	 * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
	 * <pre class="code">
	 * &#064;Configuration
	 * &#064;EnableCaching
	 * public class AppConfig extends JCacheConfigurerSupport {
	 *     &#064;Bean // important!
	 *     &#064;Override
	 *     public CacheResolver exceptionCacheResolver() {
	 *         // configure and return CacheResolver instance
	 *     }
	 *     // ...
	 * }
	 * </pre>
	 * See {@link org.springframework.cache.annotation.EnableCaching} for more complete examples.
	 * <p>
	 *  返回{@link CacheResolver} bean以用于解析用于注释驱动的缓存管理的异常缓存实现必须显式声明{@link orgspringframeworkcontextannotationBean @Bean}
	 * ,例如。
	 * <pre class="code">
	 * @Configuration @EnableCaching public class AppConfig扩展JCacheConfigurerSupport {@Bean // important！ @Override public CacheResolver exceptionCacheResolver(){//配置并返回CacheResolver实例}
	 */
	CacheResolver exceptionCacheResolver();

}
