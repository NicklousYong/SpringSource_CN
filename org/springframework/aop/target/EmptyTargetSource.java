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

package org.springframework.aop.target;

import java.io.Serializable;

import org.springframework.aop.TargetSource;
import org.springframework.util.ObjectUtils;

/**
 * Canonical {@code TargetSource} when there is no target
 * (or just the target class known), and behavior is supplied
 * by interfaces and advisors only.
 *
 * <p>
 *  当没有目标(或只是目标类已知)时,规范的{@code TargetSource},行为仅由接口和顾问提供
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class EmptyTargetSource implements TargetSource, Serializable {

	/** use serialVersionUID from Spring 1.2 for interoperability */
	private static final long serialVersionUID = 3680494563553489691L;


	//---------------------------------------------------------------------
	// Static factory methods
	//---------------------------------------------------------------------

	/**
	 * The canonical (Singleton) instance of this {@link EmptyTargetSource}.
	 * <p>
	 *  这个{@link EmptyTargetSource}的规范(Singleton)实例
	 * 
	 */
	public static final EmptyTargetSource INSTANCE = new EmptyTargetSource(null, true);


	/**
	 * Return an EmptyTargetSource for the given target Class.
	 * <p>
	 * 返回给定目标类的EmptyTargetSource
	 * 
	 * 
	 * @param targetClass the target Class (may be {@code null})
	 * @see #getTargetClass()
	 */
	public static EmptyTargetSource forClass(Class<?> targetClass) {
		return forClass(targetClass, true);
	}

	/**
	 * Return an EmptyTargetSource for the given target Class.
	 * <p>
	 *  返回给定目标类的EmptyTargetSource
	 * 
	 * 
	 * @param targetClass the target Class (may be {@code null})
	 * @param isStatic whether the TargetSource should be marked as static
	 * @see #getTargetClass()
	 */
	public static EmptyTargetSource forClass(Class<?> targetClass, boolean isStatic) {
		return (targetClass == null && isStatic ? INSTANCE : new EmptyTargetSource(targetClass, isStatic));
	}


	//---------------------------------------------------------------------
	// Instance implementation
	//---------------------------------------------------------------------

	private final Class<?> targetClass;

	private final boolean isStatic;


	/**
	 * Create a new instance of the {@link EmptyTargetSource} class.
	 * <p>This constructor is {@code private} to enforce the
	 * Singleton pattern / factory method pattern.
	 * <p>
	 *  创建{@link EmptyTargetSource}类的新实例<p>这个构造函数是{@code private}来执行Singleton模式/工厂方法模式
	 * 
	 * 
	 * @param targetClass the target class to expose (may be {@code null})
	 * @param isStatic whether the TargetSource is marked as static
	 */
	private EmptyTargetSource(Class<?> targetClass, boolean isStatic) {
		this.targetClass = targetClass;
		this.isStatic = isStatic;
	}

	/**
	 * Always returns the specified target Class, or {@code null} if none.
	 * <p>
	 *  始终返回指定的目标类,否则返回{@code null}
	 * 
	 */
	@Override
	public Class<?> getTargetClass() {
		return this.targetClass;
	}

	/**
	 * Always returns {@code true}.
	 * <p>
	 *  始终返回{@code true}
	 * 
	 */
	@Override
	public boolean isStatic() {
		return this.isStatic;
	}

	/**
	 * Always returns {@code null}.
	 * <p>
	 *  始终返回{@code null}
	 * 
	 */
	@Override
	public Object getTarget() {
		return null;
	}

	/**
	 * Nothing to release.
	 * <p>
	 *  没什么可以释放
	 * 
	 */
	@Override
	public void releaseTarget(Object target) {
	}


	/**
	 * Returns the canonical instance on deserialization in case
	 * of no target class, thus protecting the Singleton pattern.
	 * <p>
	 *  在没有目标类的情况下返回反序列化的规范实例,从而保护Singleton模式
	 */
	private Object readResolve() {
		return (this.targetClass == null && this.isStatic ? INSTANCE : this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EmptyTargetSource)) {
			return false;
		}
		EmptyTargetSource otherTs = (EmptyTargetSource) other;
		return (ObjectUtils.nullSafeEquals(this.targetClass, otherTs.targetClass) && this.isStatic == otherTs.isStatic);
	}

	@Override
	public int hashCode() {
		return EmptyTargetSource.class.hashCode() * 13 + ObjectUtils.nullSafeHashCode(this.targetClass);
	}

	@Override
	public String toString() {
		return "EmptyTargetSource: " +
				(this.targetClass != null ? "target class [" + this.targetClass.getName() + "]" : "no target class") +
				", " + (this.isStatic ? "static" : "dynamic");
	}

}
