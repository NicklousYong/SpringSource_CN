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

package org.springframework.remoting.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.util.ClassUtils;

/**
 * Encapsulates a remote invocation, providing core method invocation properties
 * in a serializable fashion. Used for RMI and HTTP-based serialization invokers.
 *
 * <p>This is an SPI class, typically not used directly by applications.
 * Can be subclassed for additional invocation parameters.
 *
 * <p>Both {@link RemoteInvocation} and {@link RemoteInvocationResult} are designed
 * for use with standard Java serialization as well as JavaBean-style serialization.
 *
 * <p>
 *  封装远程调用,以可序列化的方式提供核心方法调用属性用于基于RMI和基于HTTP的序列化调用器
 * 
 * <p>这是一个SPI类,通常不被应用程序直接使用可以为其他调用参数进行子类化
 * 
 *  <p> {@link RemoteInvocation}和{@link RemoteInvocationResult}均设计用于标准Java序列化以及JavaBean风格的序列化
 * 
 * 
 * @author Juergen Hoeller
 * @since 25.02.2004
 * @see RemoteInvocationResult
 * @see RemoteInvocationFactory
 * @see RemoteInvocationExecutor
 * @see org.springframework.remoting.rmi.RmiProxyFactoryBean
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 * @see org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 */
public class RemoteInvocation implements Serializable {

	/** use serialVersionUID from Spring 1.1 for interoperability */
	private static final long serialVersionUID = 6876024250231820554L;


	private String methodName;

	private Class<?>[] parameterTypes;

	private Object[] arguments;

	private Map<String, Serializable> attributes;


	/**
	 * Create a new RemoteInvocation for the given AOP method invocation.
	 * <p>
	 *  为给定的AOP方法调用创建一个新的RemoteInvocation
	 * 
	 * 
	 * @param methodInvocation the AOP invocation to convert
	 */
	public RemoteInvocation(MethodInvocation methodInvocation) {
		this.methodName = methodInvocation.getMethod().getName();
		this.parameterTypes = methodInvocation.getMethod().getParameterTypes();
		this.arguments = methodInvocation.getArguments();
	}

	/**
	 * Create a new RemoteInvocation for the given parameters.
	 * <p>
	 *  为给定的参数创建一个新的RemoteInvocation
	 * 
	 * 
	 * @param methodName the name of the method to invoke
	 * @param parameterTypes the parameter types of the method
	 * @param arguments the arguments for the invocation
	 */
	public RemoteInvocation(String methodName, Class<?>[] parameterTypes, Object[] arguments) {
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.arguments = arguments;
	}

	/**
	 * Create a new RemoteInvocation for JavaBean-style deserialization
	 * (e.g. with Jackson).
	 * <p>
	 *  为JavaBean样式反序列化创建一个新的RemoteInvocation(例如与Jackson)
	 * 
	 */
	public RemoteInvocation() {
	}


	/**
	 * Set the name of the target method.
	 * <p>This setter is intended for JavaBean-style deserialization.
	 * <p>
	 *  设置目标方法的名称<p>此setter用于JavaBean样式的反序列化
	 * 
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * Return the name of the target method.
	 * <p>
	 *  返回目标方法的名称
	 * 
	 */
	public String getMethodName() {
		return this.methodName;
	}

	/**
	 * Set the parameter types of the target method.
	 * <p>This setter is intended for JavaBean-style deserialization.
	 * <p>
	 *  设置目标方法的参数类型<p>此setter用于JavaBean样式反序列化
	 * 
	 */
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	/**
	 * Return the parameter types of the target method.
	 * <p>
	 *  返回目标方法的参数类型
	 * 
	 */
	public Class<?>[] getParameterTypes() {
		return this.parameterTypes;
	}

	/**
	 * Set the arguments for the target method call.
	 * <p>This setter is intended for JavaBean-style deserialization.
	 * <p>
	 * 设置目标方法调用的参数<p>此setter适用于JavaBean样式的反序列化
	 * 
	 */
	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	/**
	 * Return the arguments for the target method call.
	 * <p>
	 *  返回目标方法调用的参数
	 * 
	 */
	public Object[] getArguments() {
		return this.arguments;
	}


	/**
	 * Add an additional invocation attribute. Useful to add additional
	 * invocation context without having to subclass RemoteInvocation.
	 * <p>Attribute keys have to be unique, and no overriding of existing
	 * attributes is allowed.
	 * <p>The implementation avoids to unnecessarily create the attributes
	 * Map, to minimize serialization size.
	 * <p>
	 *  添加额外的调用属性有助于添加额外的调用上下文而不需要子类RemoteInvocation <p>属性键必须是唯一的,并且不允许覆盖现有属性<p>实现避免不必要地创建属性Map,以最小化序列化尺寸
	 * 
	 * 
	 * @param key the attribute key
	 * @param value the attribute value
	 * @throws IllegalStateException if the key is already bound
	 */
	public void addAttribute(String key, Serializable value) throws IllegalStateException {
		if (this.attributes == null) {
			this.attributes = new HashMap<String, Serializable>();
		}
		if (this.attributes.containsKey(key)) {
			throw new IllegalStateException("There is already an attribute with key '" + key + "' bound");
		}
		this.attributes.put(key, value);
	}

	/**
	 * Retrieve the attribute for the given key, if any.
	 * <p>The implementation avoids to unnecessarily create the attributes
	 * Map, to minimize serialization size.
	 * <p>
	 *  检索给定键的属性(如果有的话)<p>实现避免不必要地创建属性Map,以最小化序列化大小
	 * 
	 * 
	 * @param key the attribute key
	 * @return the attribute value, or {@code null} if not defined
	 */
	public Serializable getAttribute(String key) {
		if (this.attributes == null) {
			return null;
		}
		return this.attributes.get(key);
	}

	/**
	 * Set the attributes Map. Only here for special purposes:
	 * Preferably, use {@link #addAttribute} and {@link #getAttribute}.
	 * <p>
	 *  将属性Map设置为特殊目的：最好使用{@link #addAttribute}和{@link #getAttribute}
	 * 
	 * 
	 * @param attributes the attributes Map
	 * @see #addAttribute
	 * @see #getAttribute
	 */
	public void setAttributes(Map<String, Serializable> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Return the attributes Map. Mainly here for debugging purposes:
	 * Preferably, use {@link #addAttribute} and {@link #getAttribute}.
	 * <p>
	 * 返回属性Map主要在这里进行调试：最好使用{@link #addAttribute}和{@link #getAttribute}
	 * 
	 * 
	 * @return the attributes Map, or {@code null} if none created
	 * @see #addAttribute
	 * @see #getAttribute
	 */
	public Map<String, Serializable> getAttributes() {
		return this.attributes;
	}


	/**
	 * Perform this invocation on the given target object.
	 * Typically called when a RemoteInvocation is received on the server.
	 * <p>
	 *  对给定的目标对象执行此调用通常在服务器上收到RemoteInvocation时调用
	 * 
	 * @param targetObject the target object to apply the invocation to
	 * @return the invocation result
	 * @throws NoSuchMethodException if the method name could not be resolved
	 * @throws IllegalAccessException if the method could not be accessed
	 * @throws InvocationTargetException if the method invocation resulted in an exception
	 * @see java.lang.reflect.Method#invoke
	 */
	public Object invoke(Object targetObject)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		Method method = targetObject.getClass().getMethod(this.methodName, this.parameterTypes);
		return method.invoke(targetObject, this.arguments);
	}


	@Override
	public String toString() {
		return "RemoteInvocation: method name '" + this.methodName + "'; parameter types " +
				ClassUtils.classNamesToString(this.parameterTypes);
	}

}
