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

package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.util.StringUtils;

/**
 * Subclass of {@code AbstractReflectiveMBeanInfoAssembler} that allows
 * to specify method names to be exposed as MBean operations and attributes.
 * JavaBean getters and setters will automatically be exposed as JMX attributes.
 *
 * <p>You can supply an array of method names via the {@code managedMethods}
 * property. If you have multiple beans and you wish each bean to use a different
 * set of method names, then you can map bean keys (that is the name used to pass
 * the bean to the {@code MBeanExporter}) to a list of method names using the
 * {@code methodMappings} property.
 *
 * <p>If you specify values for both {@code methodMappings} and
 * {@code managedMethods}, Spring will attempt to find method names in the
 * mappings first. If no method names for the bean are found, it will use the
 * method names defined by {@code managedMethods}.
 *
 * <p>
 *  允许指定要显示为MBean操作和属性的方法名称的{@code AbstractReflectiveMBeanInfoAssembler}子类JavaBean getter和setter将自动显示为JM
 * X属性。
 * 
 * 您可以通过{@code managedMethods}属性提供方法名称数组如果您有多个bean,并希望每个bean使用不同的方法名称集,那么您可以映射bean密钥(即所使用的名称将bean传递给{@code MBeanExporter}
 * )到使用{@code methodMappings}属性的方法名称列表。
 * 
 *  <p>如果您为{@code methodMappings}和{@code managedMethods}指定了值,Spring将尝试首先在映射中查找方法名称如果没有找到bean的方法名称,它将使用由{@code managedMethods}
 * 。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2
 * @see #setManagedMethods
 * @see #setMethodMappings
 * @see InterfaceBasedMBeanInfoAssembler
 * @see SimpleReflectiveMBeanInfoAssembler
 * @see MethodExclusionMBeanInfoAssembler
 * @see org.springframework.jmx.export.MBeanExporter
 */
public class MethodNameBasedMBeanInfoAssembler extends AbstractConfigurableMBeanInfoAssembler {

	/**
	 * Stores the set of method names to use for creating the management interface.
	 * <p>
	 *  存储用于创建管理界面的一组方法名称
	 * 
	 */
	private Set<String> managedMethods;

	/**
	 * Stores the mappings of bean keys to an array of method names.
	 * <p>
	 *  将bean键的映射存储到方法名称数组中
	 * 
	 */
	private Map<String, Set<String>> methodMappings;


	/**
	 * Set the array of method names to use for creating the management info.
	 * These method names will be used for a bean if no entry corresponding to
	 * that bean is found in the {@code methodMappings} property.
	 * <p>
	 * 设置用于创建管理信息的方法名称数组如果在{@code methodMappings}属性中找不到与该bean相对应的条目,则将使用这些方法名称
	 * 
	 * 
	 * @param methodNames an array of method names indicating the methods to use
	 * @see #setMethodMappings
	 */
	public void setManagedMethods(String... methodNames) {
		this.managedMethods = new HashSet<String>(Arrays.asList(methodNames));
	}

	/**
	 * Set the mappings of bean keys to a comma-separated list of method names.
	 * The property key should match the bean key and the property value should match
	 * the list of method names. When searching for method names for a bean, Spring
	 * will check these mappings first.
	 * <p>
	 *  将bean键的映射设置为逗号分隔的方法名称列表属性键应该匹配bean键,属性值应与方法名称列表匹配当搜索bean的方法名称时,Spring将首先检查这些映射
	 * 
	 * @param mappings the mappins of bean keys to method names
	 */
	public void setMethodMappings(Properties mappings) {
		this.methodMappings = new HashMap<String, Set<String>>();
		for (Enumeration<?> en = mappings.keys(); en.hasMoreElements();) {
			String beanKey = (String) en.nextElement();
			String[] methodNames = StringUtils.commaDelimitedListToStringArray(mappings.getProperty(beanKey));
			this.methodMappings.put(beanKey, new HashSet<String>(Arrays.asList(methodNames)));
		}
	}


	@Override
	protected boolean includeReadAttribute(Method method, String beanKey) {
		return isMatch(method, beanKey);
	}

	@Override
	protected boolean includeWriteAttribute(Method method, String beanKey) {
		return isMatch(method, beanKey);
	}

	@Override
	protected boolean includeOperation(Method method, String beanKey) {
		return isMatch(method, beanKey);
	}

	protected boolean isMatch(Method method, String beanKey) {
		if (this.methodMappings != null) {
			Set<String> methodNames = this.methodMappings.get(beanKey);
			if (methodNames != null) {
				return methodNames.contains(method.getName());
			}
		}
		return (this.managedMethods != null && this.managedMethods.contains(method.getName()));
	}

}
