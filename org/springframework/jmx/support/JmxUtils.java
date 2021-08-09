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

package org.springframework.jmx.support;

import java.beans.PropertyDescriptor;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import javax.management.DynamicMBean;
import javax.management.JMX;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Collection of generic utility methods to support Spring JMX.
 * Includes a convenient method to locate an MBeanServer.
 *
 * <p>
 *  用于支持Spring JMX的通用实用程序方法的集合包括一个方便的方法来定位MBeanServer
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see #locateMBeanServer
 */
public abstract class JmxUtils {

	/**
	 * The key used when extending an existing {@link ObjectName} with the
	 * identity hash code of its corresponding managed resource.
	 * <p>
	 * 使用其对应的托管资源的身份哈希代码扩展现有的{@link ObjectName}时使用的密钥
	 * 
	 */
	public static final String IDENTITY_OBJECT_NAME_KEY = "identity";

	/**
	 * Suffix used to identify an MBean interface.
	 * <p>
	 *  用于标识MBean接口的后缀
	 * 
	 */
	private static final String MBEAN_SUFFIX = "MBean";


	private static final Log logger = LogFactory.getLog(JmxUtils.class);


	/**
	 * Attempt to find a locally running {@code MBeanServer}. Fails if no
	 * {@code MBeanServer} can be found. Logs a warning if more than one
	 * {@code MBeanServer} found, returning the first one from the list.
	 * <p>
	 *  尝试找到本地运行的{@code MBeanServer}如果找不到{@code MBeanServer},则失败如果找到多个{@code MBeanServer},则记录一个警告,从列表中返回第一个。
	 * 
	 * 
	 * @return the {@code MBeanServer} if found
	 * @throws org.springframework.jmx.MBeanServerNotFoundException
	 * if no {@code MBeanServer} could be found
	 * @see javax.management.MBeanServerFactory#findMBeanServer
	 */
	public static MBeanServer locateMBeanServer() throws MBeanServerNotFoundException {
		return locateMBeanServer(null);
	}

	/**
	 * Attempt to find a locally running {@code MBeanServer}. Fails if no
	 * {@code MBeanServer} can be found. Logs a warning if more than one
	 * {@code MBeanServer} found, returning the first one from the list.
	 * <p>
	 *  尝试找到本地运行的{@code MBeanServer}如果找不到{@code MBeanServer},则失败如果找到多个{@code MBeanServer},则记录一个警告,从列表中返回第一个。
	 * 
	 * 
	 * @param agentId the agent identifier of the MBeanServer to retrieve.
	 * If this parameter is {@code null}, all registered MBeanServers are considered.
	 * If the empty String is given, the platform MBeanServer will be returned.
	 * @return the {@code MBeanServer} if found
	 * @throws org.springframework.jmx.MBeanServerNotFoundException
	 * if no {@code MBeanServer} could be found
	 * @see javax.management.MBeanServerFactory#findMBeanServer(String)
	 */
	public static MBeanServer locateMBeanServer(String agentId) throws MBeanServerNotFoundException {
		MBeanServer server = null;

		// null means any registered server, but "" specifically means the platform server
		if (!"".equals(agentId)) {
			List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(agentId);
			if (servers != null && servers.size() > 0) {
				// Check to see if an MBeanServer is registered.
				if (servers.size() > 1 && logger.isWarnEnabled()) {
					logger.warn("Found more than one MBeanServer instance" +
							(agentId != null ? " with agent id [" + agentId + "]" : "") +
							". Returning first from list.");
				}
				server = servers.get(0);
			}
		}

		if (server == null && !StringUtils.hasLength(agentId)) {
			// Attempt to load the PlatformMBeanServer.
			try {
				server = ManagementFactory.getPlatformMBeanServer();
			}
			catch (SecurityException ex) {
				throw new MBeanServerNotFoundException("No specific MBeanServer found, " +
						"and not allowed to obtain the Java platform MBeanServer", ex);
			}
		}

		if (server == null) {
			throw new MBeanServerNotFoundException(
					"Unable to locate an MBeanServer instance" +
					(agentId != null ? " with agent id [" + agentId + "]" : ""));
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Found MBeanServer: " + server);
		}
		return server;
	}

	/**
	 * Convert an array of {@code MBeanParameterInfo} into an array of
	 * {@code Class} instances corresponding to the parameters.
	 * <p>
	 *  将{@code MBeanParameterInfo}的数组转换为与参数对应的{@code Class}实例数组
	 * 
	 * 
	 * @param paramInfo the JMX parameter info
	 * @return the parameter types as classes
	 * @throws ClassNotFoundException if a parameter type could not be resolved
	 */
	public static Class<?>[] parameterInfoToTypes(MBeanParameterInfo[] paramInfo) throws ClassNotFoundException {
		return parameterInfoToTypes(paramInfo, ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Convert an array of {@code MBeanParameterInfo} into an array of
	 * {@code Class} instances corresponding to the parameters.
	 * <p>
	 * 将{@code MBeanParameterInfo}的数组转换为与参数对应的{@code Class}实例数组
	 * 
	 * 
	 * @param paramInfo the JMX parameter info
	 * @param classLoader the ClassLoader to use for loading parameter types
	 * @return the parameter types as classes
	 * @throws ClassNotFoundException if a parameter type could not be resolved
	 */
	public static Class<?>[] parameterInfoToTypes(MBeanParameterInfo[] paramInfo, ClassLoader classLoader)
			throws ClassNotFoundException {

		Class<?>[] types = null;
		if (paramInfo != null && paramInfo.length > 0) {
			types = new Class<?>[paramInfo.length];
			for (int x = 0; x < paramInfo.length; x++) {
				types[x] = ClassUtils.forName(paramInfo[x].getType(), classLoader);
			}
		}
		return types;
	}

	/**
	 * Create a {@code String[]} representing the argument signature of a
	 * method. Each element in the array is the fully qualified class name
	 * of the corresponding argument in the methods signature.
	 * <p>
	 *  创建一个表示方法的参数签名的{@code String []}数组中的每个元素都是方法签名中相应参数的完全限定类名
	 * 
	 * 
	 * @param method the method to build an argument signature for
	 * @return the signature as array of argument types
	 */
	public static String[] getMethodSignature(Method method) {
		Class<?>[] types = method.getParameterTypes();
		String[] signature = new String[types.length];
		for (int x = 0; x < types.length; x++) {
			signature[x] = types[x].getName();
		}
		return signature;
	}

	/**
	 * Return the JMX attribute name to use for the given JavaBeans property.
	 * <p>When using strict casing, a JavaBean property with a getter method
	 * such as {@code getFoo()} translates to an attribute called
	 * {@code Foo}. With strict casing disabled, {@code getFoo()}
	 * would translate to just {@code foo}.
	 * <p>
	 *  返回要用于给定JavaBeans属性的JMX属性名称<p>使用strict套件时,具有getter方法(如{@code getFoo()}的JavaBean属性将转换为称为{@code Foo}的属性
	 * ,{@code getFoo()}将转换为{@code foo}。
	 * 
	 * 
	 * @param property the JavaBeans property descriptor
	 * @param useStrictCasing whether to use strict casing
	 * @return the JMX attribute name to use
	 */
	public static String getAttributeName(PropertyDescriptor property, boolean useStrictCasing) {
		if (useStrictCasing) {
			return StringUtils.capitalize(property.getName());
		}
		else {
			return property.getName();
		}
	}

	/**
	 * Append an additional key/value pair to an existing {@link ObjectName} with the key being
	 * the static value {@code identity} and the value being the identity hash code of the
	 * managed resource being exposed on the supplied {@link ObjectName}. This can be used to
	 * provide a unique {@link ObjectName} for each distinct instance of a particular bean or
	 * class. Useful when generating {@link ObjectName ObjectNames} at runtime for a set of
	 * managed resources based on the template value supplied by a
	 * {@link org.springframework.jmx.export.naming.ObjectNamingStrategy}.
	 * <p>
	 * 在现有的{@link ObjectName}中添加一个附加的键/值对,其中键为静态值{@code identity},值为被提供的{@link ObjectName}上显示的托管资源的标识哈希代码。
	 * 可以用于为特定bean或类的每个不同实例提供唯一的{@link ObjectName}在基于{@link提供的模板值的情况下在运行时生成{@link ObjectName ObjectNames}时可用
	 * 的一组受管资源orgspringframeworkjmxexportnamingObjectNamingStrategy}。
	 * 在现有的{@link ObjectName}中添加一个附加的键/值对,其中键为静态值{@code identity},值为被提供的{@link ObjectName}上显示的托管资源的标识哈希代码。
	 * 
	 * 
	 * @param objectName the original JMX ObjectName
	 * @param managedResource the MBean instance
	 * @return an ObjectName with the MBean identity added
	 * @throws MalformedObjectNameException in case of an invalid object name specification
	 * @see org.springframework.util.ObjectUtils#getIdentityHexString(Object)
	 */
	public static ObjectName appendIdentityToObjectName(ObjectName objectName, Object managedResource)
			throws MalformedObjectNameException {

		Hashtable<String, String> keyProperties = objectName.getKeyPropertyList();
		keyProperties.put(IDENTITY_OBJECT_NAME_KEY, ObjectUtils.getIdentityHexString(managedResource));
		return ObjectNameManager.getInstance(objectName.getDomain(), keyProperties);
	}

	/**
	 * Return the class or interface to expose for the given bean.
	 * This is the class that will be searched for attributes and operations
	 * (for example, checked for annotations).
	 * <p>This implementation returns the superclass for a CGLIB proxy and
	 * the class of the given bean else (for a JDK proxy or a plain bean class).
	 * <p>
	 * 返回为给定的bean公开的类或接口这是将搜索属性和操作的类(例如,检查注释)<p>此实现返回CGLIB代理的超类和给定bean的类else(对于JDK代理或纯bean类)
	 * 
	 * 
	 * @param managedBean the bean instance (might be an AOP proxy)
	 * @return the bean class to expose
	 * @see org.springframework.util.ClassUtils#getUserClass(Object)
	 */
	public static Class<?> getClassToExpose(Object managedBean) {
		return ClassUtils.getUserClass(managedBean);
	}

	/**
	 * Return the class or interface to expose for the given bean class.
	 * This is the class that will be searched for attributes and operations
	 * (for example, checked for annotations).
	 * <p>This implementation returns the superclass for a CGLIB proxy and
	 * the class of the given bean else (for a JDK proxy or a plain bean class).
	 * <p>
	 *  返回为给定的bean类公开的类或接口这是将搜索属性和操作的类(例如,检查注释)<p>此实现返回CGLIB代理的超类,给定的类bean else(对于JDK代理或纯bean类)
	 * 
	 * 
	 * @param clazz the bean class (might be an AOP proxy class)
	 * @return the bean class to expose
	 * @see org.springframework.util.ClassUtils#getUserClass(Class)
	 */
	public static Class<?> getClassToExpose(Class<?> clazz) {
		return ClassUtils.getUserClass(clazz);
	}

	/**
	 * Determine whether the given bean class qualifies as an MBean as-is.
	 * <p>This implementation checks for {@link javax.management.DynamicMBean}
	 * classes as well as classes with corresponding "*MBean" interface
	 * (Standard MBeans) or corresponding "*MXBean" interface (Java 6 MXBeans).
	 * <p>
	 * 确定给定的b​​ean类是否符合MBean的要求。
	 * 此实现检查{@link javaxmanagementDynamicMBean}类以及具有相应"* MBean"接口(标准MBean)或相应"* MXBean"接口(Java)的类6个MXBeans)。
	 * 确定给定的b​​ean类是否符合MBean的要求。
	 * 
	 * 
	 * @param clazz the bean class to analyze
	 * @return whether the class qualifies as an MBean
	 * @see org.springframework.jmx.export.MBeanExporter#isMBean(Class)
	 */
	public static boolean isMBean(Class<?> clazz) {
		return (clazz != null &&
				(DynamicMBean.class.isAssignableFrom(clazz) ||
						(getMBeanInterface(clazz) != null || getMXBeanInterface(clazz) != null)));
	}

	/**
	 * Return the Standard MBean interface for the given class, if any
	 * (that is, an interface whose name matches the class name of the
	 * given class but with suffix "MBean").
	 * <p>
	 *  返回给定类的标准MBean接口(如果有的话),即名称与给定类的类名相匹配但带有后缀"MBean"的接口)
	 * 
	 * 
	 * @param clazz the class to check
	 * @return the Standard MBean interface for the given class
	 */
	public static Class<?> getMBeanInterface(Class<?> clazz) {
		if (clazz == null || clazz.getSuperclass() == null) {
			return null;
		}
		String mbeanInterfaceName = clazz.getName() + MBEAN_SUFFIX;
		Class<?>[] implementedInterfaces = clazz.getInterfaces();
		for (Class<?> iface : implementedInterfaces) {
			if (iface.getName().equals(mbeanInterfaceName)) {
				return iface;
			}
		}
		return getMBeanInterface(clazz.getSuperclass());
	}

	/**
	 * Return the Java 6 MXBean interface exists for the given class, if any
	 * (that is, an interface whose name ends with "MXBean" and/or
	 * carries an appropriate MXBean annotation).
	 * <p>
	 *  返回给定类的Java 6 MXBean接口(如果有的话)(即名称以"MXBean"结尾的接口和/或携带适当的MXBean注释)
	 * 
	 * 
	 * @param clazz the class to check
	 * @return whether there is an MXBean interface for the given class
	 */
	public static Class<?> getMXBeanInterface(Class<?> clazz) {
		if (clazz == null || clazz.getSuperclass() == null) {
			return null;
		}
		Class<?>[] implementedInterfaces = clazz.getInterfaces();
		for (Class<?> iface : implementedInterfaces) {
			if (JMX.isMXBeanInterface(iface)) {
				return iface;
			}
		}
		return getMXBeanInterface(clazz.getSuperclass());
	}

	/**
	 * Check whether MXBean support is available, i.e. whether we're running
	 * on Java 6 or above.
	 * <p>
	 *  检查MXBean支持是否可用,即是否在Java 6或更高版本上运行
	 * 
	 * @return {@code true} if available; {@code false} otherwise
	 * @deprecated as of Spring 4.0, since Java 6 is required anyway now
	 */
	@Deprecated
	public static boolean isMXBeanSupportAvailable() {
		return true;
	}

}
