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

package org.springframework.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @CacheConfig} provides a mechanism for sharing common cache-related
 * settings at the class level.
 *
 * <p>When this annotation is present on a given class, it provides a set
 * of default settings for any cache operation defined in that class.
 *
 * <p>
 *  {@code @CacheConfig}提供了一种在类级别共享缓存相关设置的机制
 * 
 * <p>当给定类中存在此注释时,它将为该类中定义的任何高速缓存操作提供一组默认设置
 * 
 * 
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @since 4.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheConfig {

	/**
	 * Names of the default caches to consider for caching operations defined
	 * in the annotated class.
	 * <p>If none is set at the operation level, these are used instead of the default.
	 * <p>May be used to determine the target cache (or caches), matching the
	 * qualifier value or the bean names of a specific bean definition.
	 * <p>
	 *  在注释类中定义的缓存操作的默认缓存的名称<p>如果在操作级别没有设置这些缓存,则使用这些缓存的名称,而不是默认的<p>可用于确定目标缓存(或高速缓存)匹配限定符值或特定bean定义的bean名称
	 * 
	 */
	String[] cacheNames() default {};

	/**
	 * The bean name of the default {@link org.springframework.cache.interceptor.KeyGenerator} to
	 * use for the class.
	 * <p>If none is set at the operation level, this one is used instead of the default.
	 * <p>The key generator is mutually exclusive with the use of a custom key. When such key is
	 * defined for the operation, the value of this key generator is ignored.
	 * <p>
	 * 用于类<p>的默认{@link orgspringframeworkcacheinterceptorKeyGenerator}的bean名称如果在操作级别没有设置,则使用该名称,而不是默认的<p>。
	 * 密钥生成器与使用自定义键当为操作定义此类键时,此键生成器的值将被忽略。
	 * 
	 */
	String keyGenerator() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.CacheManager} to use to
	 * create a default {@link org.springframework.cache.interceptor.CacheResolver} if none
	 * is set already.
	 * <p>If no resolver and no cache manager are set at the operation level, and no cache
	 * resolver is set via {@link #cacheResolver}, this one is used instead of the default.
	 * <p>
	 *  如果没有设置,则用于创建默认{@link orgspringframeworkcacheinterceptorCacheResolver}的自定义{@link orgspringframeworkcacheCacheManager}
	 * 的bean名称<p>如果在操作级别没有设置解析器,没有缓存管理器,并且没有设置缓存解析器通过{@link #cacheResolver},使用这一个而不是默认值。
	 * 
	 * 
	 * @see org.springframework.cache.interceptor.SimpleCacheResolver
	 */
	String cacheManager() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.interceptor.CacheResolver} to use.
	 * <p>If no resolver and no cache manager are set at the operation level, this one is used
	 * instead of the default.
	 * <p>
	 * 使用<p>的自定义{@link orgspringframeworkcacheinterceptorCacheResolver}的bean名称如果在操作级别没有设置解析器和无缓存管理器,则使用这个名称而
	 * 不是默认值。
	 */
	String cacheResolver() default "";

}
