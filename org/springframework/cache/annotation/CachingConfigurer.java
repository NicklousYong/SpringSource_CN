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

package org.springframework.cache.annotation;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;

/**
 * Interface to be implemented by @{@link org.springframework.context.annotation.Configuration
 * Configuration} classes annotated with @{@link EnableCaching} that wish or need to
 * specify explicitly how caches are resolved and how keys are generated for annotation-driven
 * cache management. Consider extending {@link CachingConfigurerSupport}, which provides a
 * stub implementation of all interface methods.
 *
 * <p>See @{@link EnableCaching} for general examples and context; see
 * {@link #cacheManager()}, {@link #cacheResolver()} and {@link #keyGenerator()}
 * for detailed instructions.
 *
 * <p>
 * 通过@ {@ link orgspringframeworkcontextannotationConfiguration Configuration}实现的接口,使用@ {@ link EnableCaching}
 * 注释,希望或需要明确指定缓存如何解析以及如何为注释驱动的缓存管理生成密钥考虑扩展{@link CachingConfigurerSupport} ,它提供了所有接口方法的存根实现。
 * 
 *  <p>请参阅@ {@ link EnableCaching}获取一般示例和上下文;有关详细说明,请参阅{@link #cacheManager()},{@link #cacheResolver()}和
 * {@link #keyGenerator()}。
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @see EnableCaching
 * @see CachingConfigurerSupport
 */
public interface CachingConfigurer {

	/**
	 * Return the cache manager bean to use for annotation-driven cache
	 * management. A default {@link CacheResolver} will be initialized
	 * behind the scenes with this cache manager. For more fine-grained
	 * management of the cache resolution, consider setting the
	 * {@link CacheResolver} directly.
	 * <p>Implementations must explicitly declare
	 * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
	 * <pre class="code">
	 * &#064;Configuration
	 * &#064;EnableCaching
	 * public class AppConfig extends CachingConfigurerSupport {
	 *     &#064;Bean // important!
	 *     &#064;Override
	 *     public CacheManager cacheManager() {
	 *         // configure and return CacheManager instance
	 *     }
	 *     // ...
	 * }
	 * </pre>
	 * See @{@link EnableCaching} for more complete examples.
	 * <p>
	 * 返回缓存管理器bean以用于注释驱动的缓存管理默认{@link CacheResolver}将使用此缓存管理器在幕后进行初始化为了更精细地管理缓存解析,请考虑直接设置{@link CacheResolver}
	 *  <p>实现必须明确声明{@link orgspringframeworkcontextannotationBean @Bean},例如。
	 * <pre class="code">
	 *  @Configuration @EnableCaching public class AppConfig extends CachingConfigurerSupport {@Bean // important！ @Override public CacheManager cacheManager(){//配置并返回CacheManager实例}
	 *  //}。
	 * </pre>
	 *  有关更完整的示例,请参阅@ {@ link EnableCaching}
	 * 
	 */
	CacheManager cacheManager();

	/**
	 * Return the {@link CacheResolver} bean to use to resolve regular caches for
	 * annotation-driven cache management. This is an alternative and more powerful
	 * option of specifying the {@link CacheManager} to use.
	 * <p>If both a {@link #cacheManager()} and {@link #cacheResolver()} are set, the
	 * cache manager is ignored.
	 * <p>Implementations must explicitly declare
	 * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
	 * <pre class="code">
	 * &#064;Configuration
	 * &#064;EnableCaching
	 * public class AppConfig extends CachingConfigurerSupport {
	 *     &#064;Bean // important!
	 *     &#064;Override
	 *     public CacheResolver cacheResolver() {
	 *         // configure and return CacheResolver instance
	 *     }
	 *     // ...
	 * }
	 * </pre>
	 * See {@link EnableCaching} for more complete examples.
	 * <p>
	 * 返回{@link CacheResolver} bean用于解析用于注释驱动的缓存管理的常规缓存这是一个替代的,更强大的选项,指定{@link CacheManager}使用<p>如果{@link #cacheManager( )}
	 * 和{@link #cacheResolver()},忽略缓存管理器<p>实现必须明确声明{@link orgspringframeworkcontextannotationBean @Bean},例如。
	 * <pre class="code">
	 *  @Configuration @EnableCaching public class AppConfig extends CachingConfigurerSupport {@Bean // important！ @Override public CacheResolver cacheResolver(){//配置并返回CacheResolver实例}
	 *  //}。
	 * </pre>
	 *  有关更完整的示例,请参阅{@link EnableCaching}
	 * 
	 */
	CacheResolver cacheResolver();

	/**
	 * Return the key generator bean to use for annotation-driven cache management.
	 * Implementations must explicitly declare
	 * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
	 * <pre class="code">
	 * &#064;Configuration
	 * &#064;EnableCaching
	 * public class AppConfig extends CachingConfigurerSupport {
	 *     &#064;Bean // important!
	 *     &#064;Override
	 *     public KeyGenerator keyGenerator() {
	 *         // configure and return KeyGenerator instance
	 *     }
	 *     // ...
	 * }
	 * </pre>
	 * See @{@link EnableCaching} for more complete examples.
	 * <p>
	 * 返回用于注释驱动的缓存管理的密钥生成器bean实现必须明确声明{@link orgspringframeworkcontextannotationBean @Bean},例如
	 * <pre class="code">
	 *  @Configuration @EnableCaching public class AppConfig extends CachingConfigurerSupport {@Bean // important！ @Override public KeyGenerator keyGenerator(){//配置并返回KeyGenerator实例}
	 *  //}。
	 * </pre>
	 *  有关更完整的示例,请参阅@ {@ link EnableCaching}
	 * 
	 */
	KeyGenerator keyGenerator();

	/**
	 * Return the {@link CacheErrorHandler} to use to handle cache-related errors.
	 * <p>By default,{@link org.springframework.cache.interceptor.SimpleCacheErrorHandler}
	 * is used and simply throws the exception back at the client.
	 * <p>Implementations must explicitly declare
	 * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
	 * <pre class="code">
	 * &#064;Configuration
	 * &#064;EnableCaching
	 * public class AppConfig extends CachingConfigurerSupport {
	 *     &#064;Bean // important!
	 *     &#064;Override
	 *     public CacheErrorHandler errorHandler() {
	 *         // configure and return CacheErrorHandler instance
	 *     }
	 *     // ...
	 * }
	 * </pre>
	 * See @{@link EnableCaching} for more complete examples.
	 * <p>
	 *  返回{@link CacheErrorHandler}以用于处理与缓存相关的错误<p>默认情况下,使用{@ link orgspringframeworkcacheinterceptorSimpleCacheErrorHandler}
	 * ,并将其简单地抛出客户端<p>实现必须显式声明{@link orgspringframeworkcontextannotationBean @Bean },例如。
	 * <pre class="code">
	 * @Configuration @EnableCaching public class AppConfig extends CachingConfigurerSupport {@Bean // important！ @Override public CacheErrorHandler errorHandler(){//配置并返回CacheErrorHandler实例}
	 */
	CacheErrorHandler errorHandler();

}
