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

package org.springframework.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * Enables Spring's annotation-driven cache management capability, similar to the
 * support found in Spring's {@code <cache:*>} XML namespace. To be used together
 * with @{@link org.springframework.context.annotation.Configuration Configuration}
 * classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableCaching
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public MyService myService() {
 *         // configure and return a class having &#064;Cacheable methods
 *         return new MyService();
 *     }
 *
 *     &#064;Bean
 *     public CacheManager cacheManager() {
 *         // configure and return an implementation of Spring's CacheManager SPI
 *         SimpleCacheManager cacheManager = new SimpleCacheManager();
 *         cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("default")));
 *         return cacheManager;
 *     }
 * }</pre>
 *
 * <p>For reference, the example above can be compared to the following Spring XML
 * configuration:
 *
 * <pre class="code">
 * {@code
 * <beans>
 *
 *     <cache:annotation-driven/>
 *
 *     <bean id="myService" class="com.foo.MyService"/>
 *
 *     <bean id="cacheManager" class="org.springframework.cache.support.SimpleCacheManager">
 *         <property name="caches">
 *             <set>
 *                 <bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean">
 *                     <property name="name" value="default"/>
 *                 </bean>
 *             </set>
 *         </property>
 *     </bean>
 *
 * </beans>
 * }</pre>
 *
 * In both of the scenarios above, {@code @EnableCaching} and {@code
 * <cache:annotation-driven/>} are responsible for registering the necessary Spring
 * components that power annotation-driven cache management, such as the
 * {@link org.springframework.cache.interceptor.CacheInterceptor CacheInterceptor} and the
 * proxy- or AspectJ-based advice that weaves the interceptor into the call stack when
 * {@link org.springframework.cache.annotation.Cacheable @Cacheable} methods are invoked.
 *
 * <p>If the JSR-107 API and Spring's JCache implementation are present, the necessary
 * components to manage standard cache annotations are also registered. This creates the
 * proxy- or AspectJ-based advice that weaves the interceptor into the call stack when
 * methods annotated with {@code CacheResult}, {@code CachePut}, {@code CacheRemove} or
 * {@code CacheRemoveAll} are invoked.
 *
 * <p><strong>A bean of type {@link org.springframework.cache.CacheManager CacheManager}
 * must be registered</strong>, as there is no reasonable default that the framework can
 * use as a convention. And whereas the {@code <cache:annotation-driven>} element assumes
 * a bean <em>named</em> "cacheManager", {@code @EnableCaching} searches for a cache
 * manager bean <em>by type</em>. Therefore, naming of the cache manager bean method is
 * not significant.
 *
 * <p>For those that wish to establish a more direct relationship between
 * {@code @EnableCaching} and the exact cache manager bean to be used,
 * the {@link CachingConfigurer} callback interface may be implemented.
 * Notice the {@code @Override}-annotated methods below:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableCaching
 * public class AppConfig extends CachingConfigurerSupport {
 *
 *     &#064;Bean
 *     public MyService myService() {
 *         // configure and return a class having &#064;Cacheable methods
 *         return new MyService();
 *     }
 *
 *     &#064;Bean
 *     &#064;Override
 *     public CacheManager cacheManager() {
 *         // configure and return an implementation of Spring's CacheManager SPI
 *         SimpleCacheManager cacheManager = new SimpleCacheManager();
 *         cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("default")));
 *         return cacheManager;
 *     }
 *
 *     &#064;Bean
 *     &#064;Override
 *     public KeyGenerator keyGenerator() {
 *         // configure and return an implementation of Spring's KeyGenerator SPI
 *         return new MyKeyGenerator();
 *     }
 * }</pre>
 *
 * This approach may be desirable simply because it is more explicit, or it may be
 * necessary in order to distinguish between two {@code CacheManager} beans present in the
 * same container.
 *
 * <p>Notice also the {@code keyGenerator} method in the example above. This allows for
 * customizing the strategy for cache key generation, per Spring's {@link
 * org.springframework.cache.interceptor.KeyGenerator KeyGenerator} SPI. Normally,
 * {@code @EnableCaching} will configure Spring's
 * {@link org.springframework.cache.interceptor.SimpleKeyGenerator SimpleKeyGenerator}
 * for this purpose, but when implementing {@code CachingConfigurer}, a key generator
 * must be provided explicitly. Return {@code null} or {@code new SimpleKeyGenerator()}
 * from this method if no customization is necessary.
 *
 * <p>{@link CachingConfigurer} offers additional customization options: it is recommended
 * to extend from {@link org.springframework.cache.annotation.CachingConfigurerSupport
 * CachingConfigurerSupport} that provides a default implementation for all methods which
 * can be useful if you do not need to customize everything. See {@link CachingConfigurer}
 * Javadoc for further details.
 *
 * <p>The {@link #mode()} attribute controls how advice is applied; if the mode is
 * {@link AdviceMode#PROXY} (the default), then the other attributes such as
 * {@link #proxyTargetClass()} control the behavior of the proxying.
 *
 * <p>If the {@linkplain #mode} is set to {@link AdviceMode#ASPECTJ}, then the
 * {@link #proxyTargetClass()} attribute is obsolete. Note also that in this case the
 * {@code spring-aspects} module JAR must be present on the classpath.
 *
 * <p>
 * 启用S​​pring的注释驱动的缓存管理功能,类似于Spring的{@code <cache：*>} XML命名空间中的支持要与@ {@ link orgspringframeworkcontextannotationConfiguration Configuration}
 * 类一起使用,如下所示：。
 * 
 * <pre class="code">
 *  @Configuration @EnableCaching public class AppConfig {
 * 
 *  @Bean public MyService myService(){//配置并返回一个具有@Cacheable方法的类返回新的MyService(); }
 * 
 *  @Bean public CacheManager cacheManager(){//配置并返回Spring的CacheManager的实现SPI SimpleCacheManager cacheManager = new SimpleCacheManager(); cacheManagersetCaches(ArraysasList(new ConcurrentMapCache("default")));返回cacheManager; }
 * } </pre>。
 * 
 * <p>为了参考,可以将以上示例与以下Spring XML配置进行比较：
 * 
 * <pre class="code">
 *  {@码
 * <beans>
 * 
 * <cache:annotation-driven/>
 * 
 * <bean id="myService" class="com.foo.MyService"/>
 * 
 * <bean id="cacheManager" class="org.springframework.cache.support.SimpleCacheManager">
 * <property name="caches">
 * <set>
 * <bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean">
 * <property name="name" value="default"/>
 * </bean>
 * </set>
 * </property>
 * </bean>
 * 
 * </beans>
 *  } </PRE>
 * 
 *  在上述两种情况下,{@code @EnableCaching}和{@code <cache：annotation-driven />}负责注册必要的Spring组件,用于注释驱动的缓存管理,例如{@link orgspringframeworkcacheinterceptorCacheInterceptor CacheInterceptor }
 * 和基于代理或AspectJ的建议,当{@link orgspringframeworkcacheannotationCacheable @Cacheable}方法被调用时,将拦截器编入调用堆栈。
 * 
 * <p>如果存在JSR-107 API和Spring的JCache实现,则还会注册管理标准缓存注释的必要组件。
 * 这样创建了基于代理或基于AspectJ的建议,将拦截器编入调用堆栈中,当使用{ @code CacheResult},{@code CachePut},{@code CacheRemove}或{@code CacheRemoveAll}
 * 被调用。
 * <p>如果存在JSR-107 API和Spring的JCache实现,则还会注册管理标准缓存注释的必要组件。
 * 
 * <p> <strong>类型为{@link orgspringframeworkcacheCacheManager CacheManager}的bean必须注册</strong>,因为没有合理的默认框架
 * 可以用作约定,而{@code <cache：annotation-driven >}元素假定一个名为</em>"cacheManager"的bean,{@code @EnableCaching}通过类型
 * </em>搜索缓存管理器bean <em>因此,缓存管理器bean方法的命名不是重大。
 * 
 * @author Chris Beams
 * @since 3.1
 * @see CachingConfigurer
 * @see CachingConfigurationSelector
 * @see ProxyCachingConfiguration
 * @see org.springframework.cache.aspectj.AspectJCachingConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CachingConfigurationSelector.class)
public @interface EnableCaching {

	/**
	 * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
	 * to standard Java interface-based proxies. The default is {@code false}. <strong>
	 * Applicable only if {@link #mode()} is set to {@link AdviceMode#PROXY}</strong>.
	 * <p>Note that setting this attribute to {@code true} will affect <em>all</em>
	 * Spring-managed beans requiring proxying, not just those marked with {@code @Cacheable}.
	 * For example, other beans marked with Spring's {@code @Transactional} annotation will
	 * be upgraded to subclass proxying at the same time. This approach has no negative
	 * impact in practice unless one is explicitly expecting one type of proxy vs another,
	 * e.g. in tests.
	 * <p>
	 * 
	 *  <p>对于那些希望在{@code @EnableCaching}和要使用的精确缓存管理器bean之间建立更直接的关系的人,可以实现{@link CachingConfigurer}回调接口注意{@code @Override}
	 *   - 注释方法如下：。
	 * 
	 * <pre class="code">
	 * @Configuration @EnableCaching public class AppConfig extends CachingConfigurerSupport {
	 * 
	 *  @Bean public MyService myService(){//配置并返回一个具有@Cacheable方法的类返回新的MyService(); }
	 * 
	 *  @Bean @Override public CacheManager cacheManager(){//配置并返回Spring的CacheManager的实现SPI SimpleCacheManager cacheManager = new SimpleCacheManager(); cacheManagersetCaches(ArraysasList(new ConcurrentMapCache("default")));返回cacheManager; }
	 * 。
	 * 
	 *  @Bean @Override public KeyGenerator keyGenerator(){//配置并返回Spring的KeyGenerator SPI的实现返回新的MyKeyGenerator(); }
	 * } </pre>。
	 * 
	 * 这种方法可能只是因为它更加明确,或者为了区分同一容器中存在的两个{@code CacheManager} bean可能是必要的
	 * 
	 *  <p>请注意上述示例中的{@code keyGenerator}方法允许根据Spring的{@link orgspringframeworkcacheinterceptorKeyGenerator KeyGenerator}
	 *  SPI定制缓存密钥生成策略通常,{@code @EnableCaching}将配置Spring的{@link为了这个目的,orgspringframeworkcacheinterceptorSimpleKeyGenerator SimpleKeyGenerator}
	 * ,但是当实现{@code CachingConfigurer}时,如果不需要定制,必须明确地从这个方法返回一个密钥生成器返回{@code null}或{@code new SimpleKeyGenerator()}
	 * 。
	 * 
	 */
	boolean proxyTargetClass() default false;

	/**
	 * Indicate how caching advice should be applied. The default is
	 * {@link AdviceMode#PROXY}.
	 * <p>
	 * <p> {@ link CachingConfigurer}提供了额外的自定义选项：建议从{@link orgspringframeworkcacheannotationCachingConfigurerSupport CachingConfigurerSupport}
	 * 中扩展,为所有方法提供默认实现,如果您不需要自定义所有方法,可以使用该功能。
	 * 参见{@link CachingConfigurer Javadoc进一步细节。
	 * 
	 *  <p> {@link #mode()}属性控制如何应用建议;如果模式是{@link AdviceMode#PROXY}(默认),则其他属性(如{@link #proxyTargetClass()})控
	 * 制代理的行为。
	 * 
	 * <p>如果{@linkplain #mode}设置为{@link AdviceMode#ASPECTJ},则{@link #proxyTargetClass()}属性已过时请注意,在这种情况下,{@code spring-aspects}
	 * 模块JAR必须存在于类路径上。
	 * 
	 * 
	 * @see AdviceMode
	 */
	AdviceMode mode() default AdviceMode.PROXY;

	/**
	 * Indicate the ordering of the execution of the caching advisor
	 * when multiple advices are applied at a specific joinpoint.
	 * The default is {@link Ordered#LOWEST_PRECEDENCE}.
	 * <p>
	 * 指示是否创建基于子类的代理(CGLIB),而不是基于标准的基于Java接口的代理。
	 * 默认值为{@code false} <strong>仅当{@link #mode()}设置为{@link请注意,将此属性设置为{@code true}将影响<em>所有</em>需要代理的Spring管
	 * 理的bean,而不仅仅是那些标有{@code @Cacheable}例如,标记有Spring的{@code @Transactional}注释的其他bean将同时升级到子类代理。
	 * 指示是否创建基于子类的代理(CGLIB),而不是基于标准的基于Java接口的代理。这种方法在实践中没有负面影响,除非有人明确地期望一种类型的代理与另一种类型,例如在测试中。
	 * 
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

}
