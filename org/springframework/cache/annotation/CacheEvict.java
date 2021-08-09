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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation indicating that a method (or all methods on a class) triggers a
 * {@link org.springframework.cache.Cache#evict(Object) cache evict} operation.
 *
 * <p>This annotation may be used as a <em>meta-annotation</em> to create custom
 * <em>composed annotations</em> with attribute overrides.
 *
 * <p>
 *  注释表明方法(或类上的所有方法)触发{@link orgspringframeworkcacheCache#evict(Object)cache evict}操作
 * 
 * <p>此注释可以用作元标注</em>,以创建具有属性覆盖的自定义<组合注释</em>
 * 
 * 
 * @author Costin Leau
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @since 3.1
 * @see CacheConfig
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {

	/**
	 * Alias for {@link #cacheNames}.
	 * <p>
	 *  {@link #cacheNames}的别名
	 * 
	 */
	@AliasFor("cacheNames")
	String[] value() default {};

	/**
	 * Names of the caches to use for the cache eviction operation.
	 * <p>Names may be used to determine the target cache (or caches), matching
	 * the qualifier value or bean name of a specific bean definition.
	 * <p>
	 *  用于缓存驱逐操作的缓存的名称<p>可以使用名称来确定目标缓存(或高速缓存),匹配特定bean定义的限定符值或bean名称
	 * 
	 * 
	 * @since 4.2
	 * @see #value
	 * @see CacheConfig#cacheNames
	 */
	@AliasFor("value")
	String[] cacheNames() default {};

	/**
	 * Spring Expression Language (SpEL) expression for computing the key dynamically.
	 * <p>Default is {@code ""}, meaning all method parameters are considered as a key,
	 * unless a custom {@link #keyGenerator} has been set.
	 * <p>The SpEL expression evaluates against a dedicated context that provides the
	 * following meta-data:
	 * <ul>
	 * <li>{@code #result} for a reference to the result of the method invocation, which
	 * can only be used if {@link #beforeInvocation()} is {@code false}. For supported
	 * wrappers such as {@code Optional}, {@code #result} refers to the actual object,
	 * not the wrapper</li>
	 * <li>{@code #root.method}, {@code #root.target}, and {@code #root.caches} for
	 * references to the {@link java.lang.reflect.Method method}, target object, and
	 * affected cache(s) respectively.</li>
	 * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
	 * ({@code #root.targetClass}) are also available.
	 * <li>Method arguments can be accessed by index. For instance the second argument
	 * can be accessed via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
	 * can also be accessed by name if that information is available.</li>
	 * </ul>
	 * <p>
	 *  用于计算密钥的Spring表达式语言(Spel)表达式动态<p>默认值为{@code""},这意味着除非定义了{@link #keyGenerator},否则所有方法参数都被视为密钥。
	 *  Spel表达式针对提供以下元数据的专用上下文进行求值：。
	 * <ul>
	 * {@ code #result}用于引用方法调用的结果,只有在{@link #beforeInvocation()}为{@code false}时才可以使用。
	 * 对于支持的包装器,如{@code可选} {@code #result}是指{@code #rootarget}和{@code #rootcaches}的实际对象,而不是{@ code #rootmethod}
	 * ,{@code #rootarget}和{@code #rootcaches}链接javalangreflectMethod方法},目标对象和受影响的缓存分别</li> <li>方法名称({@code #rootmethodName}
	 * )和目标类({@code #roottargetClass})的快捷方式也可用< li>方法参数可以通过索引访问例如第二个参数可以通过{@code #rootargs [1]}访问,{@code#p1}
	 * 或{@code#a1}参数也可以通过名称访问信息可用</li>。
	 * {@ code #result}用于引用方法调用的结果,只有在{@link #beforeInvocation()}为{@code false}时才可以使用。
	 * </ul>
	 */
	String key() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.interceptor.KeyGenerator}
	 * to use.
	 * <p>Mutually exclusive with the {@link #key} attribute.
	 * <p>
	 * 使用<p>与{@link #key}属性相互排斥的自定义{@link orgspringframeworkcacheinterceptorKeyGenerator}的bean名称
	 * 
	 * 
	 * @see CacheConfig#keyGenerator
	 */
	String keyGenerator() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.CacheManager} to use to
	 * create a default {@link org.springframework.cache.interceptor.CacheResolver} if none
	 * is set already.
	 * <p>Mutually exclusive with the {@link #cacheResolver} attribute.
	 * <p>
	 *  用于创建默认{@link orgspringframeworkcacheinterceptorCacheResolver}的自定义{@link orgspringframeworkcacheCacheManager}
	 * 的bean名称(如果没有设置)<p>与{@link #cacheResolver}属性相互排斥。
	 * 
	 * 
	 * @see org.springframework.cache.interceptor.SimpleCacheResolver
	 * @see CacheConfig#cacheManager
	 */
	String cacheManager() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.interceptor.CacheResolver}
	 * to use.
	 * <p>
	 *  要使用的自定义{@link orgspringframeworkcacheinterceptorCacheResolver}的bean名称
	 * 
	 * 
	 * @see CacheConfig#cacheResolver
	 */
	String cacheResolver() default "";

	/**
	 * Spring Expression Language (SpEL) expression used for making the cache
	 * eviction operation conditional.
	 * <p>Default is {@code ""}, meaning the cache eviction is always performed.
	 * <p>The SpEL expression evaluates against a dedicated context that provides the
	 * following meta-data:
	 * <ul>
	 * <li>{@code #root.method}, {@code #root.target}, and {@code #root.caches} for
	 * references to the {@link java.lang.reflect.Method method}, target object, and
	 * affected cache(s) respectively.</li>
	 * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
	 * ({@code #root.targetClass}) are also available.
	 * <li>Method arguments can be accessed by index. For instance the second argument
	 * can be accessed via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
	 * can also be accessed by name if that information is available.</li>
	 * </ul>
	 * <p>
	 *  用于使缓存驱逐操作的Spring表达式语言(Spel)表达式条件<p>默认值为{@code""},意味着始终执行缓存驱逐<p> Spel表达式针对提供以下元数据的专用上下文进行求值-数据：
	 * <ul>
	 * 对于{@link javalangreflectMethod method},目标对象和受影响的缓存的引用,<li> {@ code #rootmethod},{@code #roottarget}和{@code #rootarget}
	 *  li>方法名称的快捷方式({@code #rootmethodName})和目标类({@code #roottargetClass})也可用<li>方法参数可以通过索引访问例如第二个参数可以通过{@代码#rootargs [1]}
	 * ,{@code#p1}或{@code#a1}如果信息可用,也可以通过名称访问参数</li>。
	 * </ul>
	 */
	String condition() default "";

	/**
	 * Whether all the entries inside the cache(s) are removed.
	 * <p>By default, only the value under the associated key is removed.
	 * <p>Note that setting this parameter to {@code true} and specifying a
	 * {@link #key} is not allowed.
	 * <p>
	 *  是否删除缓存中的所有条目<p>默认情况下,只删除相关键下的值<p>请注意,将此参数设置为{@code true}并指定{@link #key}不允许
	 * 
	 */
	boolean allEntries() default false;

	/**
	 * Whether the eviction should occur before the method is invoked.
	 * <p>Setting this attribute to {@code true}, causes the eviction to
	 * occur irrespective of the method outcome (i.e., whether it threw an
	 * exception or not).
	 * <p>Defaults to {@code false}, meaning that the cache eviction operation
	 * will occur <em>after</em> the advised method is invoked successfully (i.e.,
	 * only if the invocation did not throw an exception).
	 * <p>
	 * 是否在调用方法之前应该发生驱逐<p>将此属性设置为{@code true},导致无论方法结果如何(即是否抛出异常)都会导致逐出。
	 * <p>默认为{ @code false},这意味着缓存驱逐操作将在</em>之后发生,建议的方法被成功调用(即只有调用没有引发异常)。
	 */
	boolean beforeInvocation() default false;

}
