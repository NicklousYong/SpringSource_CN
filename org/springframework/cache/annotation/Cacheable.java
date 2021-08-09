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
import java.util.concurrent.Callable;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation indicating that the result of invoking a method (or all methods
 * in a class) can be cached.
 *
 * <p>Each time an advised method is invoked, caching behavior will be applied,
 * checking whether the method has been already invoked for the given arguments.
 * A sensible default simply uses the method parameters to compute the key, but
 * a SpEL expression can be provided via the {@link #key} attribute, or a custom
 * {@link org.springframework.cache.interceptor.KeyGenerator} implementation can
 * replace the default one (see {@link #keyGenerator}).
 *
 * <p>If no value is found in the cache for the computed key, the target method
 * will be invoked and the returned value stored in the associated cache. Note
 * that Java8's {@code Optional} return types are automatically handled and its
 * content is stored in the cache if present.
 *
 * <p>This annotation may be used as a <em>meta-annotation</em> to create custom
 * <em>composed annotations</em> with attribute overrides.
 *
 * <p>
 *  指示可以缓存调用方法(或类中的所有方法)的结果的注释
 * 
 * <p>每次调用一个建议的方法时,将应用缓存行为,检查该方法是否已经为给定的参数调用。
 * 一个合理的默认值只是使用方法参数来计算密钥,但可以通过{@link #key}属性或自定义{@link orgspringframeworkcacheinterceptorKeyGenerator}实现
 * 可以替换默认值(请参阅{@link #keyGenerator})。
 * <p>每次调用一个建议的方法时,将应用缓存行为,检查该方法是否已经为给定的参数调用。
 * 
 *  <p>如果在计算密钥的缓存中没有找到值,则将调用目标方法,并将返回的值存储在关联的缓存中。请注意,Java8的{@code可选}返回类型将自动处理,其内容存储在缓存如果存在
 * 
 * <p>此注释可以用作元标注</em>,以创建具有属性覆盖的自定义<组合注释</em>
 * 
 * 
 * @author Costin Leau
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @since 3.1
 * @see CacheConfig
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable {

	/**
	 * Alias for {@link #cacheNames}.
	 * <p>
	 *  {@link #cacheNames}的别名
	 * 
	 */
	@AliasFor("cacheNames")
	String[] value() default {};

	/**
	 * Names of the caches in which method invocation results are stored.
	 * <p>Names may be used to determine the target cache (or caches), matching
	 * the qualifier value or bean name of a specific bean definition.
	 * <p>
	 *  存储方法调用结果的缓存的名称<p>可以使用名称来确定目标缓存(或高速缓存),匹配特定bean定义的限定符值或bean名称
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
	 * unless a custom {@link #keyGenerator} has been configured.
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
	 *  用于计算密钥的Spring表达式语言(Spel)表达式动态<p>默认值为{@code""},表示所有方法参数都被视为关键字,除非已经配置了自定义的{@link #keyGenerator} <p> S
	 * pel表达式针对提供以下元数据的专用上下文进行求值：。
	 * <ul>
	 * 对于{@link javalangreflectMethod method},目标对象和受影响的缓存的引用,<li> {@ code #rootmethod},{@code #roottarget}和{@code #rootarget}
	 *  li>方法名称的快捷方式({@code #rootmethodName})和目标类({@code #roottargetClass})也可用<li>方法参数可以通过索引访问例如第二个参数可以通过{@代码#rootargs [1]}
	 * ,{@code#p1}或{@code#a1}如果信息可用,也可以通过名称访问参数</li>。
	 * </ul>
	 */
	String key() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.interceptor.KeyGenerator}
	 * to use.
	 * <p>Mutually exclusive with the {@link #key} attribute.
	 * <p>
	 *  使用<p>与{@link #key}属性相互排斥的自定义{@link orgspringframeworkcacheinterceptorKeyGenerator}的bean名称
	 * 
	 * 
	 * @see CacheConfig#keyGenerator
	 */
	String keyGenerator() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.CacheManager} to use to
	 * create a default {@link org.springframework.cache.interceptor.CacheResolver} if none
	 * is set already.
	 * <p>Mutually exclusive with the {@link #cacheResolver}  attribute.
	 * <p>
	 * 用于创建默认{@link orgspringframeworkcacheinterceptorCacheResolver}的自定义{@link orgspringframeworkcacheCacheManager}
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
	 * Spring Expression Language (SpEL) expression used for making the method
	 * caching conditional.
	 * <p>Default is {@code ""}, meaning the method result is always cached.
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
	 *  用于使方法缓存的Spring表达式语言(Spel)表达式条件<p>默认值为{@code""},意味着方法结果总是缓存<p> SpEL表达式针对专用上下文进行求值,数据：
	 * <ul>
	 * 对于{@link javalangreflectMethod method},目标对象和受影响的缓存的引用,<li> {@ code #rootmethod},{@code #roottarget}和{@code #rootarget}
	 *  li>方法名称的快捷方式({@code #rootmethodName})和目标类({@code #roottargetClass})也可用<li>方法参数可以通过索引访问例如第二个参数可以通过{@代码#rootargs [1]}
	 * ,{@code#p1}或{@code#a1}如果信息可用,也可以通过名称访问参数</li>。
	 * </ul>
	 */
	String condition() default "";

	/**
	 * Spring Expression Language (SpEL) expression used to veto method caching.
	 * <p>Unlike {@link #condition}, this expression is evaluated after the method
	 * has been called and can therefore refer to the {@code result}.
	 * <p>Default is {@code ""}, meaning that caching is never vetoed.
	 * <p>The SpEL expression evaluates against a dedicated context that provides the
	 * following meta-data:
	 * <ul>
	 * <li>{@code #result} for a reference to the result of the method invocation. For
	 * supported wrappers such as {@code Optional}, {@code #result} refers to the actual
	 * object, not the wrapper</li>
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
	 * 用于否决方法缓存的Spring表达式语言(Spel)表达式与{@link #condition}不同,此方法在调用该方法后进行评估,因此可以参考{@code result} <p>默认值为{ @code""}
	 * ,意味着缓存从不被否决<p> Spel表达式针对提供以下元数据的专用上下文进行求值：。
	 * <ul>
	 * 对于引用方法调用结果的<li> {@ code #result}对于{@code可选}支持的包装器,{@code #result}是指实际对象,而不是包装器</li> <对于{@link javalangreflectMethod method},目标对象和受影响的缓存的引用,分别使用li>
	 *  {@ code #rootmethod},{@code #roottarget}和{@code #rootcaches} </li> <li >方法名称的快捷方式({@code #rootmethodName}
	 * )和目标类({@code #roottargetClass})也可用<li>方法参数可以通过索引访问例如第二个参数可以通过{@code #rootargs [1]},{@code#p1}或{@code#a1}
	 * 如果信息可用,也可以通过名称访问参数</li>。
	 * </ul>
	 * 
	 * @since 3.2
	 */
	String unless() default "";

	/**
	 * Synchronize the invocation of the underlying method if several threads are
	 * attempting to load a value for the same key. The synchronization leads to
	 * a couple of limitations:
	 * <ol>
	 * <li>{@link #unless()} is not supported</li>
	 * <li>Only one cache may be specified</li>
	 * <li>No other cache-related operation can be combined</li>
	 * </ol>
	 * This is effectively a hint and the actual cache provider that you are
	 * using may not support it in a synchronized fashion. Check your provider
	 * documentation for more details on the actual semantics.
	 * <p>
	 * 
	 * @since 4.3
	 * @see org.springframework.cache.Cache#get(Object, Callable)
	 */
	boolean sync() default false;

}
