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

package org.springframework.cache.jcache.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * Advisor driven by a {@link JCacheOperationSource}, used to include a
 * cache advice bean for methods that are cacheable.
 *
 * <p>
 *  由{@link JCacheOperationSource}驱动的顾问,用于为可缓存的方法包含缓存建议bean
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
@SuppressWarnings("serial")
public class BeanFactoryJCacheOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

	private JCacheOperationSource cacheOperationSource;

	private final JCacheOperationSourcePointcut pointcut = new JCacheOperationSourcePointcut() {
		@Override
		protected JCacheOperationSource getCacheOperationSource() {
			return cacheOperationSource;
		}
	};

	/**
	 * Set the cache operation attribute source which is used to find cache
	 * attributes. This should usually be identical to the source reference
	 * set on the cache interceptor itself.
	 * <p>
	 * 设置用于查找缓存属性的缓存操作属性源这通常与缓存拦截器本身上的源引用集相同
	 * 
	 */
	public void setCacheOperationSource(JCacheOperationSource cacheOperationSource) {
		this.cacheOperationSource = cacheOperationSource;
	}

	/**
	 * Set the {@link org.springframework.aop.ClassFilter} to use for this pointcut.
	 * Default is {@link org.springframework.aop.ClassFilter#TRUE}.
	 * <p>
	 *  设置{@link orgspringframeworkaopClassFilter}用于此切入点默认为{@link orgspringframeworkaopClassFilter#TRUE}
	 */
	public void setClassFilter(ClassFilter classFilter) {
		this.pointcut.setClassFilter(classFilter);
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

}
