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
 * {@code AbstractReflectiveMBeanInfoAssembler} subclass that allows
 * method names to be explicitly excluded as MBean operations and attributes.
 *
 * <p>Any method not explicitly excluded from the management interface will be exposed to
 * JMX. JavaBean getters and setters will automatically be exposed as JMX attributes.
 *
 * <p>You can supply an array of method names via the {@code ignoredMethods}
 * property. If you have multiple beans and you wish each bean to use a different
 * set of method names, then you can map bean keys (that is the name used to pass
 * the bean to the {@code MBeanExporter}) to a list of method names using the
 * {@code ignoredMethodMappings} property.
 *
 * <p>If you specify values for both {@code ignoredMethodMappings} and
 * {@code ignoredMethods}, Spring will attempt to find method names in the
 * mappings first. If no method names for the bean are found, it will use the
 * method names defined by {@code ignoredMethods}.
 *
 * <p>
 *  {@code AbstractReflectiveMBeanInfoAssembler}子类,允许将方法名称明确排除为MBean操作和属性
 * 
 * <p>任何未明确排除在管理界面中的方法都将暴露给JMX JavaBean getter和setter将自动显示为JMX属性
 * 
 *  您可以通过{@code ignoredMethods}属性提供方法名称数组如果您有多个bean,并希望每个bean使用不同的方法名称集合,那么可以映射bean密钥(即所使用的名称将bean传递给{@code MBeanExporter}
 * )到使用{@code ignoredMethodMappings}属性的方法名称列表。
 * 
 *  <p>如果您为{@code ignoredMethodMappings}和{@code ignoredMethods}指定了值,Spring将尝试首先在映射中查找方法名称如果没有找到bean的方法名称
 * ,它将使用由{@code ignoredMethods}。
 * 
 * 
 * @author Rob Harrop
 * @author Seth Ladd
 * @since 1.2.5
 * @see #setIgnoredMethods
 * @see #setIgnoredMethodMappings
 * @see InterfaceBasedMBeanInfoAssembler
 * @see SimpleReflectiveMBeanInfoAssembler
 * @see MethodNameBasedMBeanInfoAssembler
 * @see org.springframework.jmx.export.MBeanExporter
 */
public class MethodExclusionMBeanInfoAssembler extends AbstractConfigurableMBeanInfoAssembler {

	private Set<String> ignoredMethods;

	private Map<String, Set<String>> ignoredMethodMappings;


	/**
	 * Set the array of method names to be <b>ignored</b> when creating the management info.
	 * <p>These method names will be used for a bean if no entry corresponding to
	 * that bean is found in the {@code ignoredMethodsMappings} property.
	 * <p>
	 * 
	 * @see #setIgnoredMethodMappings(java.util.Properties)
	 */
	public void setIgnoredMethods(String... ignoredMethodNames) {
		this.ignoredMethods = new HashSet<String>(Arrays.asList(ignoredMethodNames));
	}

	/**
	 * Set the mappings of bean keys to a comma-separated list of method names.
	 * <p>These method names are <b>ignored</b> when creating the management interface.
	 * <p>The property key must match the bean key and the property value must match
	 * the list of method names. When searching for method names to ignore for a bean,
	 * Spring will check these mappings first.
	 * <p>
	 * 在创建管理信息时将方法名称的数组设置为<b>忽略</b> <p>如果在{@code ignoredMethodsMappings}属性中找不到与该bean相对应的条目,则这些方法名称将用于一个bean。
	 * 
	 */
	public void setIgnoredMethodMappings(Properties mappings) {
		this.ignoredMethodMappings = new HashMap<String, Set<String>>();
		for (Enumeration<?> en = mappings.keys(); en.hasMoreElements();) {
			String beanKey = (String) en.nextElement();
			String[] methodNames = StringUtils.commaDelimitedListToStringArray(mappings.getProperty(beanKey));
			this.ignoredMethodMappings.put(beanKey, new HashSet<String>(Arrays.asList(methodNames)));
		}
	}


	@Override
	protected boolean includeReadAttribute(Method method, String beanKey) {
		return isNotIgnored(method, beanKey);
	}

	@Override
	protected boolean includeWriteAttribute(Method method, String beanKey) {
		return isNotIgnored(method, beanKey);
	}

	@Override
	protected boolean includeOperation(Method method, String beanKey) {
		return isNotIgnored(method, beanKey);
	}

	/**
	 * Determine whether the given method is supposed to be included,
	 * that is, not configured as to be ignored.
	 * <p>
	 *  将bean键的映射设置为逗号分隔的方法名称列表<p>创建管理界面时,这些方法名称被忽略</b> <p>属性键必须与bean键和属性值相匹配必须匹配方法名称列表当搜索方法名称以忽略一个bean时,Spr
	 * ing将首先检查这些映射。
	 * 
	 * 
	 * @param method the operation method
	 * @param beanKey the key associated with the MBean in the beans map
	 * of the {@code MBeanExporter}
	 */
	protected boolean isNotIgnored(Method method, String beanKey) {
		if (this.ignoredMethodMappings != null) {
			Set<String> methodNames = this.ignoredMethodMappings.get(beanKey);
			if (methodNames != null) {
				return !methodNames.contains(method.getName());
			}
		}
		if (this.ignoredMethods != null) {
			return !this.ignoredMethods.contains(method.getName());
		}
		return true;
	}

}
