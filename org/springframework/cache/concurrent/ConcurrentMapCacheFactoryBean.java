/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.cache.concurrent;

import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * {@link FactoryBean} for easy configuration of a {@link ConcurrentMapCache}
 * when used within a Spring container. Can be configured through bean properties;
 * uses the assigned Spring bean name as the default cache name.
 *
 * <p>Useful for testing or simple caching scenarios, typically in combination
 * with {@link org.springframework.cache.support.SimpleCacheManager} or
 * dynamically through {@link ConcurrentMapCacheManager}.
 *
 * <p>
 *  {@link FactoryBean}在Spring容器中使用时可以轻松配置{@link ConcurrentMapCache}可以通过bean属性进行配置;使用分配的Spring bean名称作为默
 * 认缓存名称。
 * 
 * <p>通常与{@link orgspringframeworkcachesupportSimpleCacheManager}结合使用或通过{@link ConcurrentMapCacheManager}
 * 动态执行测试或简单缓存场景,。
 * 
 * 
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ConcurrentMapCacheFactoryBean
		implements FactoryBean<ConcurrentMapCache>, BeanNameAware, InitializingBean {

	private String name = "";

	private ConcurrentMap<Object, Object> store;

	private boolean allowNullValues = true;

	private ConcurrentMapCache cache;


	/**
	 * Specify the name of the cache.
	 * <p>Default is "" (empty String).
	 * <p>
	 *  指定缓存的名称<p>默认值为""(空字符串)
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Specify the ConcurrentMap to use as an internal store
	 * (possibly pre-populated).
	 * <p>Default is a standard {@link java.util.concurrent.ConcurrentHashMap}.
	 * <p>
	 *  指定ConcurrentMap用作内部存储(可能预先填充)<p>默认值为标准{@link javautilconcurrentConcurrentHashMap}
	 * 
	 */
	public void setStore(ConcurrentMap<Object, Object> store) {
		this.store = store;
	}

	/**
	 * Set whether to allow {@code null} values
	 * (adapting them to an internal null holder value).
	 * <p>Default is "true".
	 * <p>
	 *  设置是否允许{@code null}值(使其适应内部空值持有者值)<p>默认值为"true"
	 */
	public void setAllowNullValues(boolean allowNullValues) {
		this.allowNullValues = allowNullValues;
	}

	@Override
	public void setBeanName(String beanName) {
		if (!StringUtils.hasLength(this.name)) {
			setName(beanName);
		}
	}

	@Override
	public void afterPropertiesSet() {
		this.cache = (this.store != null ? new ConcurrentMapCache(this.name, this.store, this.allowNullValues) :
				new ConcurrentMapCache(this.name, this.allowNullValues));
	}


	@Override
	public ConcurrentMapCache getObject() {
		return this.cache;
	}

	@Override
	public Class<?> getObjectType() {
		return ConcurrentMapCache.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
