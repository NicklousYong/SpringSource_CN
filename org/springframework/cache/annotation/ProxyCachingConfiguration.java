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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * {@code @Configuration} class that registers the Spring infrastructure beans necessary
 * to enable proxy-based annotation-driven cache management.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @see EnableCaching
 * @see CachingConfigurationSelector
 */
@Configuration
public class ProxyCachingConfiguration extends AbstractCachingConfiguration {

	@Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor() {
		BeanFactoryCacheOperationSourceAdvisor advisor =
				new BeanFactoryCacheOperationSourceAdvisor();
		advisor.setCacheOperationSource(cacheOperationSource());
		advisor.setAdvice(cacheInterceptor());
		advisor.setOrder(this.enableCaching.<Integer>getNumber("order"));
		return advisor;
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public CacheOperationSource cacheOperationSource() {
		return new AnnotationCacheOperationSource();
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public CacheInterceptor cacheInterceptor() {
		CacheInterceptor interceptor = new CacheInterceptor();
		interceptor.setCacheOperationSources(cacheOperationSource());
		if (this.cacheResolver != null) {
			interceptor.setCacheResolver(this.cacheResolver);
		}
		else if (this.cacheManager != null) {
			interceptor.setCacheManager(this.cacheManager);
		}
		if (this.keyGenerator != null) {
			interceptor.setKeyGenerator(this.keyGenerator);
		}
		if (this.errorHandler != null) {
			interceptor.setErrorHandler(this.errorHandler);
		}
		return interceptor;
	}

}
