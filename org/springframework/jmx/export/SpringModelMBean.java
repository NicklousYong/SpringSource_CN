/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.jmx.export;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.RequiredModelMBean;

/**
 * Extension of the {@link RequiredModelMBean} class that ensures the
 * {@link Thread#getContextClassLoader() thread context ClassLoader} is switched
 * for the managed resource's {@link ClassLoader} before any invocations occur.
 *
 * <p>
 *  在发生任何调用之前,扩展{@link RequiredModelMBean}类,确保{@link Thread#getContextClassLoader()线程上下文ClassLoader}被切换到
 * 被管理资源的{@link ClassLoader}。
 * 
 * 
 * @author Rob Harrop
 * @since 2.0
 * @see RequiredModelMBean
 */
public class SpringModelMBean extends RequiredModelMBean {

	/**
	 * Stores the {@link ClassLoader} to use for invocations. Defaults
	 * to the current thread {@link ClassLoader}.
	 * <p>
	 * 存储用于调用的{@link ClassLoader}默认为当前线程{@link ClassLoader}
	 * 
	 */
	private ClassLoader managedResourceClassLoader = Thread.currentThread().getContextClassLoader();


	/**
	 * Construct a new SpringModelMBean instance with an empty {@link ModelMBeanInfo}.
	 * <p>
	 *  使用空的{@link ModelMBeanInfo}构造一个新的SpringModelMBean实例
	 * 
	 * 
	 * @see javax.management.modelmbean.RequiredModelMBean#RequiredModelMBean()
	 */
	public SpringModelMBean() throws MBeanException, RuntimeOperationsException {
		super();
	}

	/**
	 * Construct a new SpringModelMBean instance with the given {@link ModelMBeanInfo}.
	 * <p>
	 *  使用给定的{@link ModelMBeanInfo}构造一个新的SpringModelMBean实例
	 * 
	 * 
	 * @see javax.management.modelmbean.RequiredModelMBean#RequiredModelMBean(ModelMBeanInfo)
	 */
	public SpringModelMBean(ModelMBeanInfo mbi) throws MBeanException, RuntimeOperationsException {
		super(mbi);
	}


	/**
	 * Sets managed resource to expose and stores its {@link ClassLoader}.
	 * <p>
	 *  设置托管资源以公开和存储其{@link ClassLoader}
	 * 
	 */
	@Override
	public void setManagedResource(Object managedResource, String managedResourceType)
			throws MBeanException, InstanceNotFoundException, InvalidTargetObjectTypeException {

		this.managedResourceClassLoader = managedResource.getClass().getClassLoader();
		super.setManagedResource(managedResource, managedResourceType);
	}


	/**
	 * Switches the {@link Thread#getContextClassLoader() context ClassLoader} for the
	 * managed resources {@link ClassLoader} before allowing the invocation to occur.
	 * <p>
	 *  在允许调用发生之前,切换管理资源{@link ClassLoader}的{@link Thread#getContextClassLoader()上下文ClassLoader}
	 * 
	 * 
	 * @see javax.management.modelmbean.ModelMBean#invoke
	 */
	@Override
	public Object invoke(String opName, Object[] opArgs, String[] sig)
			throws MBeanException, ReflectionException {

		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
			return super.invoke(opName, opArgs, sig);
		}
		finally {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
		}
	}

	/**
	 * Switches the {@link Thread#getContextClassLoader() context ClassLoader} for the
	 * managed resources {@link ClassLoader} before allowing the invocation to occur.
	 * <p>
	 *  在允许调用发生之前,切换管理资源{@link ClassLoader}的{@link Thread#getContextClassLoader()上下文ClassLoader}
	 * 
	 * 
	 * @see javax.management.modelmbean.ModelMBean#getAttribute
	 */
	@Override
	public Object getAttribute(String attrName)
			throws AttributeNotFoundException, MBeanException, ReflectionException {

		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
			return super.getAttribute(attrName);
		}
		finally {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
		}
	}

	/**
	 * Switches the {@link Thread#getContextClassLoader() context ClassLoader} for the
	 * managed resources {@link ClassLoader} before allowing the invocation to occur.
	 * <p>
	 * 在允许调用发生之前,切换管理资源{@link ClassLoader}的{@link Thread#getContextClassLoader()上下文ClassLoader}
	 * 
	 * 
	 * @see javax.management.modelmbean.ModelMBean#getAttributes
	 */
	@Override
	public AttributeList getAttributes(String[] attrNames) {
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
			return super.getAttributes(attrNames);
		}
		finally {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
		}
	}

	/**
	 * Switches the {@link Thread#getContextClassLoader() context ClassLoader} for the
	 * managed resources {@link ClassLoader} before allowing the invocation to occur.
	 * <p>
	 *  在允许调用发生之前,切换管理资源{@link ClassLoader}的{@link Thread#getContextClassLoader()上下文ClassLoader}
	 * 
	 * 
	 * @see javax.management.modelmbean.ModelMBean#setAttribute
	 */
	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {

		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
			super.setAttribute(attribute);
		}
		finally {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
		}
	}

	/**
	 * Switches the {@link Thread#getContextClassLoader() context ClassLoader} for the
	 * managed resources {@link ClassLoader} before allowing the invocation to occur.
	 * <p>
	 *  在允许调用发生之前,切换管理资源{@link ClassLoader}的{@link Thread#getContextClassLoader()上下文ClassLoader}
	 * 
	 * @see javax.management.modelmbean.ModelMBean#setAttributes
	 */
	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.managedResourceClassLoader);
			return super.setAttributes(attributes);
		}
		finally {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
		}
	}

}
