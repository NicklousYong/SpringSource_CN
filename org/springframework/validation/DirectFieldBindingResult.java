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

package org.springframework.validation;

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;

/**
 * Special implementation of the Errors and BindingResult interfaces,
 * supporting registration and evaluation of binding errors on value objects.
 * Performs direct field access instead of going through JavaBean getters.
 *
 * <p>Since Spring 4.1 this implementation is able to traverse nested fields.
 *
 * <p>
 *  错误和BindingResult接口的特殊实现,支持对值对象的绑定错误的注册和评估执行直接的现场访问,而不是通过JavaBean getter
 * 
 * <p>自Spring 41以来,这个实现能够遍历嵌套字段
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see DataBinder#getBindingResult()
 * @see DataBinder#initDirectFieldAccess()
 * @see BeanPropertyBindingResult
 */
@SuppressWarnings("serial")
public class DirectFieldBindingResult extends AbstractPropertyBindingResult {

	private final Object target;

	private final boolean autoGrowNestedPaths;

	private transient ConfigurablePropertyAccessor directFieldAccessor;


	/**
	 * Create a new DirectFieldBindingResult instance.
	 * <p>
	 *  创建一个新的DirectFieldBindingResult实例
	 * 
	 * 
	 * @param target the target object to bind onto
	 * @param objectName the name of the target object
	 */
	public DirectFieldBindingResult(Object target, String objectName) {
		this(target, objectName, true);
	}

	/**
	 * Create a new DirectFieldBindingResult instance.
	 * <p>
	 *  创建一个新的DirectFieldBindingResult实例
	 * 
	 * 
	 * @param target the target object to bind onto
	 * @param objectName the name of the target object
	 * @param autoGrowNestedPaths whether to "auto-grow" a nested path that contains a null value
	 */
	public DirectFieldBindingResult(Object target, String objectName, boolean autoGrowNestedPaths) {
		super(objectName);
		this.target = target;
		this.autoGrowNestedPaths = autoGrowNestedPaths;
	}


	@Override
	public final Object getTarget() {
		return this.target;
	}

	/**
	 * Returns the DirectFieldAccessor that this instance uses.
	 * Creates a new one if none existed before.
	 * <p>
	 *  返回此实例使用的DirectFieldAccessor创建一个新的(如果以前没有)
	 * 
	 * 
	 * @see #createDirectFieldAccessor()
	 */
	@Override
	public final ConfigurablePropertyAccessor getPropertyAccessor() {
		if (this.directFieldAccessor == null) {
			this.directFieldAccessor = createDirectFieldAccessor();
			this.directFieldAccessor.setExtractOldValueForEditor(true);
			this.directFieldAccessor.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
		}
		return this.directFieldAccessor;
	}

	/**
	 * Create a new DirectFieldAccessor for the underlying target object.
	 * <p>
	 *  为底层目标对象创建一个新的DirectFieldAccessor
	 * 
	 * @see #getTarget()
	 */
	protected ConfigurablePropertyAccessor createDirectFieldAccessor() {
		Assert.state(this.target != null, "Cannot access fields on null target instance '" + getObjectName() + "'!");
		return PropertyAccessorFactory.forDirectFieldAccess(this.target);
	}

}
