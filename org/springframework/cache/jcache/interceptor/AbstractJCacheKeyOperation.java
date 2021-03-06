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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;

import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;

/**
 * A base {@link JCacheOperation} that operates with a key.
 *
 * <p>
 *  使用键操作的基础{@link JCacheOperation}
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
abstract class AbstractJCacheKeyOperation<A extends Annotation> extends AbstractJCacheOperation<A> {

	private final KeyGenerator keyGenerator;

	private final List<CacheParameterDetail> keyParameterDetails;


	/**
	 * Create a new instance.
	 * <p>
	 *  创建一个新的实例
	 * 
	 * 
	 * @param methodDetails the {@link CacheMethodDetails} related to the cached method
	 * @param cacheResolver the cache resolver to resolve regular caches
	 * @param keyGenerator the key generator to compute cache keys
	 */
	protected AbstractJCacheKeyOperation(CacheMethodDetails<A> methodDetails,
			CacheResolver cacheResolver, KeyGenerator keyGenerator) {

		super(methodDetails, cacheResolver);
		this.keyGenerator = keyGenerator;
		this.keyParameterDetails = initializeKeyParameterDetails(this.allParameterDetails);
	}


	/**
	 * Return the {@link KeyGenerator} to use to compute cache keys.
	 * <p>
	 *  返回{@link KeyGenerator}用于计算缓存键
	 * 
	 */
	public KeyGenerator getKeyGenerator() {
		return this.keyGenerator;
	}

	/**
	 * Return the {@link CacheInvocationParameter} for the parameters that are to be
	 * used to compute the key.
	 * <p>Per the spec, if some method parameters are annotated with
	 * {@link javax.cache.annotation.CacheKey}, only those parameters should be part
	 * of the key. If none are annotated, all parameters except the parameter annotated
	 * with {@link javax.cache.annotation.CacheValue} should be part of the key.
	 * <p>The method arguments must match the signature of the related method invocation
	 * <p>
	 * 如果某些方法参数用{@link javaxcacheannotationCacheKey}进行注释,则返回用于计算密钥<p>的参数的{@link CacheInvocationParameter},只有
	 * 这些参数应该是密钥的一部分如果没有被注释,除了使用{@link javaxcacheannotationCacheValue}注释的参数之外的所有参数都应该是键的一部分<p>方法参数必须与相关方法调用的
	 * 
	 * @param values the parameters value for a particular invocation
	 * @return the {@link CacheInvocationParameter} instances for the parameters to be
	 * used to compute the key
	 */
	public CacheInvocationParameter[] getKeyParameters(Object... values) {
		List<CacheInvocationParameter> result = new ArrayList<CacheInvocationParameter>();
		for (CacheParameterDetail keyParameterDetail : this.keyParameterDetails) {
			int parameterPosition = keyParameterDetail.getParameterPosition();
			if (parameterPosition >= values.length) {
				throw new IllegalStateException("Values mismatch, key parameter at position "
						+ parameterPosition + " cannot be matched against " + values.length + " value(s)");
			}
			result.add(keyParameterDetail.toCacheInvocationParameter(values[parameterPosition]));
		}
		return result.toArray(new CacheInvocationParameter[result.size()]);
	}


	private static List<CacheParameterDetail> initializeKeyParameterDetails(List<CacheParameterDetail> allParameters) {
		List<CacheParameterDetail> all = new ArrayList<CacheParameterDetail>();
		List<CacheParameterDetail> annotated = new ArrayList<CacheParameterDetail>();
		for (CacheParameterDetail allParameter : allParameters) {
			if (!allParameter.isValue()) {
				all.add(allParameter);
			}
			if (allParameter.isKey()) {
				annotated.add(allParameter);
			}
		}
		return (annotated.isEmpty() ? all : annotated);
	}

}
