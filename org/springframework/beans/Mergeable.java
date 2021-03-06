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

package org.springframework.beans;

/**
 * Interface representing an object whose value set can be merged with
 * that of a parent object.
 *
 * <p>
 *  表示其值集合可以与父对象的集合的对象的接口
 * 
 * 
 * @author Rob Harrop
 * @since 2.0
 * @see org.springframework.beans.factory.support.ManagedSet
 * @see org.springframework.beans.factory.support.ManagedList
 * @see org.springframework.beans.factory.support.ManagedMap
 * @see org.springframework.beans.factory.support.ManagedProperties
 */
public interface Mergeable {

	/**
	 * Is merging enabled for this particular instance?
	 * <p>
	 *  是否为此特定实例启用合并?
	 * 
	 */
	boolean isMergeEnabled();

	/**
	 * Merge the current value set with that of the supplied object.
	 * <p>The supplied object is considered the parent, and values in
	 * the callee's value set must override those of the supplied object.
	 * <p>
	 * 将当前值与所提供的对象的值合并<p>提供的对象被认为是父对象,被调用者的值集中的值必须覆盖提供的对象的值
	 * 
	 * @param parent the object to merge with
	 * @return the result of the merge operation
	 * @throws IllegalArgumentException if the supplied parent is {@code null}
	 * @exception IllegalStateException if merging is not enabled for this instance
	 * (i.e. {@code mergeEnabled} equals {@code false}).
	 */
	Object merge(Object parent);

}
