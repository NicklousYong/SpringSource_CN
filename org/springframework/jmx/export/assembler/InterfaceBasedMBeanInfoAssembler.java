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

package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Subclass of {@code AbstractReflectiveMBeanInfoAssembler} that allows for
 * the management interface of a bean to be defined using arbitrary interfaces.
 * Any methods or properties that are defined in those interfaces are exposed
 * as MBean operations and attributes.
 *
 * <p>By default, this class votes on the inclusion of each operation or attribute
 * based on the interfaces implemented by the bean class. However, you can supply an
 * array of interfaces via the {@code managedInterfaces} property that will be
 * used instead. If you have multiple beans and you wish each bean to use a different
 * set of interfaces, then you can map bean keys (that is the name used to pass the
 * bean to the {@code MBeanExporter}) to a list of interface names using the
 * {@code interfaceMappings} property.
 *
 * <p>If you specify values for both {@code interfaceMappings} and
 * {@code managedInterfaces}, Spring will attempt to find interfaces in the
 * mappings first. If no interfaces for the bean are found, it will use the
 * interfaces defined by {@code managedInterfaces}.
 *
 * <p>
 * 允许使用任意接口定义bean的管理界面的{@code AbstractReflectiveMBeanInfoAssembler}的子类在这些接口中定义的任何方法或属性都将显示为MBean操作和属性
 * 
 *  <p>默认情况下,该类基于bean类实现的接口对每个操作或属性的包含进行投票。
 * 但是,您可以通过{@code managedInterfaces}属性提供一组接口,如果您有多个bean,并且您希望每个bean使用不同的接口集,然后可以将bean键(即用于将bean传递给{@code MBeanExporter}
 * 的名称)映射到使用{@代码interfaceMappings}属性。
 *  <p>默认情况下,该类基于bean类实现的接口对每个操作或属性的包含进行投票。
 * 
 * <p>如果您为{@code interfaceMappings}和{@code managedInterfaces}指定了值,Spring将尝试首先在映射中查找接口。
 * 如果没有找到接口,它将使用{@code managedInterfaces又}。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see #setManagedInterfaces
 * @see #setInterfaceMappings
 * @see MethodNameBasedMBeanInfoAssembler
 * @see SimpleReflectiveMBeanInfoAssembler
 * @see org.springframework.jmx.export.MBeanExporter
 */
public class InterfaceBasedMBeanInfoAssembler extends AbstractConfigurableMBeanInfoAssembler
		implements BeanClassLoaderAware, InitializingBean {

	/**
	 * Stores the array of interfaces to use for creating the management interface.
	 * <p>
	 *  存储用于创建管理界面的接口数组
	 * 
	 */
	private Class<?>[] managedInterfaces;

	/**
	 * Stores the mappings of bean keys to an array of {@code Class}es.
	 * <p>
	 *  将bean键的映射存储到{@code Class} es的数组中
	 * 
	 */
	private Properties interfaceMappings;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	/**
	 * Stores the mappings of bean keys to an array of {@code Class}es.
	 * <p>
	 *  将bean键的映射存储到{@code Class} es的数组中
	 * 
	 */
	private Map<String, Class<?>[]> resolvedInterfaceMappings;


	/**
	 * Set the array of interfaces to use for creating the management info.
	 * These interfaces will be used for a bean if no entry corresponding to
	 * that bean is found in the {@code interfaceMappings} property.
	 * <p>
	 *  设置用于创建管理信息的接口数组如果在{@code interfaceMappings}属性中找不到与该bean相对应的条目,则这些接口将用于一个bean
	 * 
	 * 
	 * @param managedInterfaces an array of classes indicating the interfaces to use.
	 * Each entry <strong>MUST</strong> be an interface.
	 * @see #setInterfaceMappings
	 */
	public void setManagedInterfaces(Class<?>[] managedInterfaces) {
		if (managedInterfaces != null) {
			for (Class<?> ifc : managedInterfaces) {
				if (!ifc.isInterface()) {
					throw new IllegalArgumentException(
							"Management interface [" + ifc.getName() + "] is not an interface");
				}
			}
		}
		this.managedInterfaces = managedInterfaces;
	}

	/**
	 * Set the mappings of bean keys to a comma-separated list of interface names.
	 * <p>The property key should match the bean key and the property value should match
	 * the list of interface names. When searching for interfaces for a bean, Spring
	 * will check these mappings first.
	 * <p>
	 * 将bean键的映射设置为逗号分隔的接口名称列表<p>属性键应与bean键匹配,属性值应与接口名称列表匹配当搜索bean的接口时,Spring将检查这些映射第一
	 * 
	 * 
	 * @param mappings the mappins of bean keys to interface names
	 */
	public void setInterfaceMappings(Properties mappings) {
		this.interfaceMappings = mappings;
	}

	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}


	@Override
	public void afterPropertiesSet() {
		if (this.interfaceMappings != null) {
			this.resolvedInterfaceMappings = resolveInterfaceMappings(this.interfaceMappings);
		}
	}

	/**
	 * Resolve the given interface mappings, turning class names into Class objects.
	 * <p>
	 *  解析给定的接口映射,将类名转换为Class对象
	 * 
	 * 
	 * @param mappings the specified interface mappings
	 * @return the resolved interface mappings (with Class objects as values)
	 */
	private Map<String, Class<?>[]> resolveInterfaceMappings(Properties mappings) {
		Map<String, Class<?>[]> resolvedMappings = new HashMap<String, Class<?>[]>(mappings.size());
		for (Enumeration<?> en = mappings.propertyNames(); en.hasMoreElements();) {
			String beanKey = (String) en.nextElement();
			String[] classNames = StringUtils.commaDelimitedListToStringArray(mappings.getProperty(beanKey));
			Class<?>[] classes = resolveClassNames(classNames, beanKey);
			resolvedMappings.put(beanKey, classes);
		}
		return resolvedMappings;
	}

	/**
	 * Resolve the given class names into Class objects.
	 * <p>
	 *  将给定的类名解析为Class对象
	 * 
	 * 
	 * @param classNames the class names to resolve
	 * @param beanKey the bean key that the class names are associated with
	 * @return the resolved Class
	 */
	private Class<?>[] resolveClassNames(String[] classNames, String beanKey) {
		Class<?>[] classes = new Class<?>[classNames.length];
		for (int x = 0; x < classes.length; x++) {
			Class<?> cls = ClassUtils.resolveClassName(classNames[x].trim(), this.beanClassLoader);
			if (!cls.isInterface()) {
				throw new IllegalArgumentException(
						"Class [" + classNames[x] + "] mapped to bean key [" + beanKey + "] is no interface");
			}
			classes[x] = cls;
		}
		return classes;
	}


	/**
	 * Check to see if the {@code Method} is declared in
	 * one of the configured interfaces and that it is public.
	 * <p>
	 *  检查{@code方法}是否在其中一个已配置的接口中声明并且是公共的
	 * 
	 * 
	 * @param method the accessor {@code Method}.
	 * @param beanKey the key associated with the MBean in the
	 * {@code beans} {@code Map}.
	 * @return {@code true} if the {@code Method} is declared in one of the
	 * configured interfaces, otherwise {@code false}.
	 */
	@Override
	protected boolean includeReadAttribute(Method method, String beanKey) {
		return isPublicInInterface(method, beanKey);
	}

	/**
	 * Check to see if the {@code Method} is declared in
	 * one of the configured interfaces and that it is public.
	 * <p>
	 *  检查{@code方法}是否在其中一个已配置的接口中声明并且是公共的
	 * 
	 * 
	 * @param method the mutator {@code Method}.
	 * @param beanKey the key associated with the MBean in the
	 * {@code beans} {@code Map}.
	 * @return {@code true} if the {@code Method} is declared in one of the
	 * configured interfaces, otherwise {@code false}.
	 */
	@Override
	protected boolean includeWriteAttribute(Method method, String beanKey) {
		return isPublicInInterface(method, beanKey);
	}

	/**
	 * Check to see if the {@code Method} is declared in
	 * one of the configured interfaces and that it is public.
	 * <p>
	 *  检查{@code方法}是否在其中一个已配置的接口中声明并且是公共的
	 * 
	 * 
	 * @param method the operation {@code Method}.
	 * @param beanKey the key associated with the MBean in the
	 * {@code beans} {@code Map}.
	 * @return {@code true} if the {@code Method} is declared in one of the
	 * configured interfaces, otherwise {@code false}.
	 */
	@Override
	protected boolean includeOperation(Method method, String beanKey) {
		return isPublicInInterface(method, beanKey);
	}

	/**
	 * Check to see if the {@code Method} is both public and declared in
	 * one of the configured interfaces.
	 * <p>
	 * 检查{@code方法}是否已公开,并在其中一个已配置的接口中声明
	 * 
	 * 
	 * @param method the {@code Method} to check.
	 * @param beanKey the key associated with the MBean in the beans map
	 * @return {@code true} if the {@code Method} is declared in one of the
	 * configured interfaces and is public, otherwise {@code false}.
	 */
	private boolean isPublicInInterface(Method method, String beanKey) {
		return ((method.getModifiers() & Modifier.PUBLIC) > 0) && isDeclaredInInterface(method, beanKey);
	}

	/**
	 * Checks to see if the given method is declared in a managed
	 * interface for the given bean.
	 * <p>
	 *  检查给定方法是否在给定bean的受管接口中声明
	 */
	private boolean isDeclaredInInterface(Method method, String beanKey) {
		Class<?>[] ifaces = null;

		if (this.resolvedInterfaceMappings != null) {
			ifaces = this.resolvedInterfaceMappings.get(beanKey);
		}

		if (ifaces == null) {
			ifaces = this.managedInterfaces;
			if (ifaces == null) {
				ifaces = ClassUtils.getAllInterfacesForClass(method.getDeclaringClass());
			}
		}

		if (ifaces != null) {
			for (Class<?> ifc : ifaces) {
				for (Method ifcMethod : ifc.getMethods()) {
					if (ifcMethod.getName().equals(method.getName()) &&
							Arrays.equals(ifcMethod.getParameterTypes(), method.getParameterTypes())) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
