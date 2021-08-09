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

package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Set of method overrides, determining which, if any, methods on a
 * managed object the Spring IoC container will override at runtime.
 *
 * <p>The currently supported {@link MethodOverride} variants are
 * {@link LookupOverride} and {@link ReplaceOverride}.
 *
 * <p>
 *  一组方法覆盖,确定Spring IoC容器将在运行时覆盖受管对象上的方法(如果有的话)
 * 
 * <p>目前支持的{@link MethodOverride}变体是{@link LookupOverride}和{@link ReplaceOverride}
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 * @see MethodOverride
 */
public class MethodOverrides {

	private final Set<MethodOverride> overrides =
			Collections.synchronizedSet(new LinkedHashSet<MethodOverride>(0));

	private volatile boolean modified = false;


	/**
	 * Create new MethodOverrides.
	 * <p>
	 *  创建新的MethodOverrides
	 * 
	 */
	public MethodOverrides() {
	}

	/**
	 * Deep copy constructor.
	 * <p>
	 *  深层复制构造函数
	 * 
	 */
	public MethodOverrides(MethodOverrides other) {
		addOverrides(other);
	}


	/**
	 * Copy all given method overrides into this object.
	 * <p>
	 *  将所有给定的方法覆盖复制到此对象中
	 * 
	 */
	public void addOverrides(MethodOverrides other) {
		if (other != null) {
			this.modified = true;
			this.overrides.addAll(other.overrides);
		}
	}

	/**
	 * Add the given method override.
	 * <p>
	 *  添加给定的方法覆盖
	 * 
	 */
	public void addOverride(MethodOverride override) {
		this.modified = true;
		this.overrides.add(override);
	}

	/**
	 * Return all method overrides contained by this object.
	 * <p>
	 *  返回此对象包含的所有方法覆盖
	 * 
	 * 
	 * @return Set of MethodOverride objects
	 * @see MethodOverride
	 */
	public Set<MethodOverride> getOverrides() {
		this.modified = true;
		return this.overrides;
	}

	/**
	 * Return whether the set of method overrides is empty.
	 * <p>
	 *  返回方法覆盖的集合是否为空
	 * 
	 */
	public boolean isEmpty() {
		return (!this.modified || this.overrides.isEmpty());
	}

	/**
	 * Return the override for the given method, if any.
	 * <p>
	 *  返回给定方法的覆盖(如果有)
	 * 
	 * @param method method to check for overrides for
	 * @return the method override, or {@code null} if none
	 */
	public MethodOverride getOverride(Method method) {
		if (!this.modified) {
			return null;
		}
		synchronized (this.overrides) {
			MethodOverride match = null;
			for (MethodOverride candidate : this.overrides) {
				if (candidate.matches(method)) {
					match = candidate;
				}
			}
			return match;
		}
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MethodOverrides)) {
			return false;
		}
		MethodOverrides that = (MethodOverrides) other;
		return this.overrides.equals(that.overrides);

	}

	@Override
	public int hashCode() {
		return this.overrides.hashCode();
	}

}
