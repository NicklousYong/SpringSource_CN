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

package org.springframework.aop.framework.autoproxy;

import org.springframework.beans.factory.BeanNameAware;

/**
 * BeanPostProcessor implementation that creates AOP proxies based on all candidate
 * Advisors in the current BeanFactory. This class is completely generic; it contains
 * no special code to handle any particular aspects, such as pooling aspects.
 *
 * <p>It's possible to filter out advisors - for example, to use multiple post processors
 * of this type in the same factory - by setting the {@code usePrefix} property
 * to true, in which case only advisors beginning with the DefaultAdvisorAutoProxyCreator's
 * bean name followed by a dot (like "aapc.") will be used. This default prefix can be
 * changed from the bean name by setting the {@code advisorBeanNamePrefix} property.
 * The separator (.) will also be used in this case.
 *
 * <p>
 * BeanPostProcessor实现,基于当前BeanFactory中的所有候选顾问创建AOP代理此类是完全通用的;它不包含处理任何特定方面的特殊代码,如池化方面
 * 
 *  <p>可以过滤掉顾问 - 例如,在同一工厂使用这种类型的多个后处理器 - 通过将{@code usePrefix}属性设置为true,在这种情况下,只有以DefaultAdvisorAutoProxy
 * Creator的bean名称开头的顾问才跟随将使用一个点(如"aapc")这个默认前缀可以通过设置{@code advisorBeanNamePrefix}属性从bean名称更改。
 * 在这种情况下也使用separator()。
 * 
 * 
 * @author Rod Johnson
 * @author Rob Harrop
 */
@SuppressWarnings("serial")
public class DefaultAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator implements BeanNameAware {

	/** Separator between prefix and remainder of bean name */
	public final static String SEPARATOR = ".";


	private boolean usePrefix;

	private String advisorBeanNamePrefix;


	/**
	 * Set whether to exclude advisors with a certain prefix
	 * in the bean name.
	 * <p>
	 *  设置是否在bean名称中排除具有某个前缀的顾问
	 * 
	 */
	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}

	/**
	 * Return whether to exclude advisors with a certain prefix
	 * in the bean name.
	 * <p>
	 * 返回是否在bean名称中排除具有某个前缀的顾问
	 * 
	 */
	public boolean isUsePrefix() {
		return this.usePrefix;
	}

	/**
	 * Set the prefix for bean names that will cause them to be included for
	 * auto-proxying by this object. This prefix should be set to avoid circular
	 * references. Default value is the bean name of this object + a dot.
	 * <p>
	 *  设置bean名称的前缀,使其被包含在该对象的自动代理中前缀应该设置为避免循环引用默认值是该对象的bean名称+一个点
	 * 
	 * 
	 * @param advisorBeanNamePrefix the exclusion prefix
	 */
	public void setAdvisorBeanNamePrefix(String advisorBeanNamePrefix) {
		this.advisorBeanNamePrefix = advisorBeanNamePrefix;
	}

	/**
	 * Return the prefix for bean names that will cause them to be included
	 * for auto-proxying by this object.
	 * <p>
	 *  返回bean名称的前缀,这些名称将由此对象自动代理引起
	 * 
	 */
	public String getAdvisorBeanNamePrefix() {
		return this.advisorBeanNamePrefix;
	}

	@Override
	public void setBeanName(String name) {
		// If no infrastructure bean name prefix has been set, override it.
		if (this.advisorBeanNamePrefix == null) {
			this.advisorBeanNamePrefix = name + SEPARATOR;
		}
	}


	/**
	 * Consider Advisor beans with the specified prefix as eligible, if activated.
	 * <p>
	 *  考虑具有指定前缀的Advisor bean(如果已启用)
	 * 
	 * @see #setUsePrefix
	 * @see #setAdvisorBeanNamePrefix
	 */
	@Override
	protected boolean isEligibleAdvisorBean(String beanName) {
		return (!isUsePrefix() || beanName.startsWith(getAdvisorBeanNamePrefix()));
	}

}
