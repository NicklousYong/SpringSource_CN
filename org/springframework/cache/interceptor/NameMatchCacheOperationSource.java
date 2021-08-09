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

package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;

/**
 * Simple {@link CacheOperationSource} implementation that allows attributes to be matched
 * by registered name.
 *
 * <p>
 *  简单的{@link CacheOperationSource}实现,允许属性与注册名称匹配
 * 
 * 
 * @author Costin Leau
 * @since 3.1
 */
@SuppressWarnings("serial")
public class NameMatchCacheOperationSource implements CacheOperationSource, Serializable {

	/**
	 * Logger available to subclasses.
	 * <p>Static for optimal serialization.
	 * <p>
	 *  Logger可用于子类<p>静态以实现最佳序列化
	 * 
	 */
	protected static final Log logger = LogFactory.getLog(NameMatchCacheOperationSource.class);


	/** Keys are method names; values are TransactionAttributes */
	private Map<String, Collection<CacheOperation>> nameMap = new LinkedHashMap<String, Collection<CacheOperation>>();


	/**
	 * Set a name/attribute map, consisting of method names
	 * (e.g. "myMethod") and CacheOperation instances
	 * (or Strings to be converted to CacheOperation instances).
	 * <p>
	 * 设置名称/属性映射,由方法名称(例如"myMethod")和CacheOperation实例(或要转换为CacheOperation实例的字符串)组成
	 * 
	 * 
	 * @see CacheOperation
	 */
	public void setNameMap(Map<String, Collection<CacheOperation>> nameMap) {
		for (Map.Entry<String, Collection<CacheOperation>> entry : nameMap.entrySet()) {
			addCacheMethod(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Add an attribute for a cacheable method.
	 * <p>Method names can be exact matches, or of the pattern "xxx*",
	 * "*xxx" or "*xxx*" for matching multiple methods.
	 * <p>
	 *  添加可缓存方法的属性<p>方法名称可以是精确匹配,或者匹配多个方法的模式"xxx *","* xxx"或"* xxx *"
	 * 
	 * 
	 * @param methodName the name of the method
	 * @param ops operation associated with the method
	 */
	public void addCacheMethod(String methodName, Collection<CacheOperation> ops) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding method [" + methodName + "] with cache operations [" + ops + "]");
		}
		this.nameMap.put(methodName, ops);
	}

	@Override
	public Collection<CacheOperation> getCacheOperations(Method method, Class<?> targetClass) {
		// look for direct name match
		String methodName = method.getName();
		Collection<CacheOperation> ops = this.nameMap.get(methodName);

		if (ops == null) {
			// Look for most specific name match.
			String bestNameMatch = null;
			for (String mappedName : this.nameMap.keySet()) {
				if (isMatch(methodName, mappedName)
						&& (bestNameMatch == null || bestNameMatch.length() <= mappedName.length())) {
					ops = this.nameMap.get(mappedName);
					bestNameMatch = mappedName;
				}
			}
		}

		return ops;
	}

	/**
	 * Return if the given method name matches the mapped name.
	 * <p>The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches,
	 * as well as direct equality. Can be overridden in subclasses.
	 * <p>
	 *  如果给定的方法名称与映射名匹配,则返回<p>默认实现检查"xxx *","* xxx"和"* xxx *"匹配以及直接相等可以在子类中覆盖
	 * 
	 * @param methodName the method name of the class
	 * @param mappedName the name in the descriptor
	 * @return if the names match
	 * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
	 */
	protected boolean isMatch(String methodName, String mappedName) {
		return PatternMatchUtils.simpleMatch(mappedName, methodName);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof NameMatchCacheOperationSource)) {
			return false;
		}
		NameMatchCacheOperationSource otherTas = (NameMatchCacheOperationSource) other;
		return ObjectUtils.nullSafeEquals(this.nameMap, otherTas.nameMap);
	}

	@Override
	public int hashCode() {
		return NameMatchCacheOperationSource.class.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + this.nameMap;
	}
}
