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

package org.springframework.validation;

import java.io.Serializable;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;

/**
 * Default implementation of the {@link Errors} and {@link BindingResult}
 * interfaces, for the registration and evaluation of binding errors on
 * JavaBean objects.
 *
 * <p>Performs standard JavaBean property access, also supporting nested
 * properties. Normally, application code will work with the
 * {@code Errors} interface or the {@code BindingResult} interface.
 * A {@link DataBinder} returns its {@code BindingResult} via
 * {@link DataBinder#getBindingResult()}.
 *
 * <p>
 *  {@link错误}和{@link BindingResult}接口的默认实现,用于注册和评估JavaBean对象上的绑定错误
 * 
 * <p>执行标准的JavaBean属性访问,也支持嵌套属性通常,应用程序代码将与{@code错误}界面或{@code BindingResult}接口一起使用{@link DataBinder}通过{ @link DataBinder#getBindingResult()}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0
 * @see DataBinder#getBindingResult()
 * @see DataBinder#initBeanPropertyAccess()
 * @see DirectFieldBindingResult
 */
@SuppressWarnings("serial")
public class BeanPropertyBindingResult extends AbstractPropertyBindingResult implements Serializable {

	private final Object target;

	private final boolean autoGrowNestedPaths;

	private final int autoGrowCollectionLimit;

	private transient BeanWrapper beanWrapper;


	/**
	 * Creates a new instance of the {@link BeanPropertyBindingResult} class.
	 * <p>
	 *  创建{@link BeanPropertyBindingResult}类的新实例
	 * 
	 * 
	 * @param target the target bean to bind onto
	 * @param objectName the name of the target object
	 */
	public BeanPropertyBindingResult(Object target, String objectName) {
		this(target, objectName, true, Integer.MAX_VALUE);
	}

	/**
	 * Creates a new instance of the {@link BeanPropertyBindingResult} class.
	 * <p>
	 *  创建{@link BeanPropertyBindingResult}类的新实例
	 * 
	 * 
	 * @param target the target bean to bind onto
	 * @param objectName the name of the target object
	 * @param autoGrowNestedPaths whether to "auto-grow" a nested path that contains a null value
	 * @param autoGrowCollectionLimit the limit for array and collection auto-growing
	 */
	public BeanPropertyBindingResult(Object target, String objectName, boolean autoGrowNestedPaths, int autoGrowCollectionLimit) {
		super(objectName);
		this.target = target;
		this.autoGrowNestedPaths = autoGrowNestedPaths;
		this.autoGrowCollectionLimit = autoGrowCollectionLimit;
	}


	@Override
	public final Object getTarget() {
		return this.target;
	}

	/**
	 * Returns the {@link BeanWrapper} that this instance uses.
	 * Creates a new one if none existed before.
	 * <p>
	 *  返回此实例使用的{@link BeanWrapper}如果以前没有,则创建一个新的
	 * 
	 * 
	 * @see #createBeanWrapper()
	 */
	@Override
	public final ConfigurablePropertyAccessor getPropertyAccessor() {
		if (this.beanWrapper == null) {
			this.beanWrapper = createBeanWrapper();
			this.beanWrapper.setExtractOldValueForEditor(true);
			this.beanWrapper.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
			this.beanWrapper.setAutoGrowCollectionLimit(this.autoGrowCollectionLimit);
		}
		return this.beanWrapper;
	}

	/**
	 * Create a new {@link BeanWrapper} for the underlying target object.
	 * <p>
	 *  为基础目标对象创建一个新的{@link BeanWrapper}
	 * 
	 * @see #getTarget()
	 */
	protected BeanWrapper createBeanWrapper() {
		Assert.state(this.target != null, "Cannot access properties on null bean instance '" + getObjectName() + "'!");
		return PropertyAccessorFactory.forBeanPropertyAccess(this.target);
	}

}
