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

package org.springframework.core;

import java.lang.reflect.Method;

import org.springframework.util.ObjectUtils;

/**
 * A common key class for a method against a specific target class,
 * including {@link #toString()} representation and {@link Comparable}
 * support (as suggested for custom {@code HashMap} keys as of Java 8).
 *
 * <p>
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.3
 */
public final class MethodClassKey implements Comparable<MethodClassKey> {

	private final Method method;

	private final Class<?> targetClass;


	/**
	 * Create a key object for the given method and target class.
	 * <p>
	 *  针对特定目标类的方法的常见关键类,包括{@link #toString()}表示和{@link Comparable}支持(对于Java 8中的自定义{@code HashMap}键的建议)
	 * 
	 * 
	 * @param method the method to wrap (must not be {@code null})
	 * @param targetClass the target class that the method will be invoked
	 * on (may be {@code null} if identical to the declaring class)
	 */
	public MethodClassKey(Method method, Class<?> targetClass) {
		this.method = method;
		this.targetClass = targetClass;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MethodClassKey)) {
			return false;
		}
		MethodClassKey otherKey = (MethodClassKey) other;
		return (this.method.equals(otherKey.method) &&
				ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass));
	}

	@Override
	public int hashCode() {
		return this.method.hashCode() + (this.targetClass != null ? this.targetClass.hashCode() * 29 : 0);
	}

	@Override
	public String toString() {
		return this.method + (this.targetClass != null ? " on " + this.targetClass : "");
	}

	@Override
	public int compareTo(MethodClassKey other) {
		int result = this.method.getName().compareTo(other.method.getName());
		if (result == 0) {
			result = this.method.toString().compareTo(other.method.toString());
			if (result == 0 && this.targetClass != null) {
				result = this.targetClass.getName().compareTo(other.targetClass.getName());
			}
		}
		return result;
	}

}
