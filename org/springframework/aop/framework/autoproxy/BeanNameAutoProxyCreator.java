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

package org.springframework.aop.framework.autoproxy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

/**
 * Auto proxy creator that identifies beans to proxy via a list of names.
 * Checks for direct, "xxx*", and "*xxx" matches.
 *
 * <p>For configuration details, see the javadoc of the parent class
 * AbstractAutoProxyCreator. Typically, you will specify a list of
 * interceptor names to apply to all identified beans, via the
 * "interceptorNames" property.
 *
 * <p>
 *  自动代理创建者,通过名称列表识别要进行代理的bean检查直接的"xxx *"和"* xxx"匹配
 * 
 * <p>有关配置详细信息,请参阅父类AbstractAutoProxyCreator的javadoc通常,将通过"interceptorNames"属性指定要应用于所有标识的bean的拦截器名称列表
 * 
 * 
 * @author Juergen Hoeller
 * @since 10.10.2003
 * @see #setBeanNames
 * @see #isMatch
 * @see #setInterceptorNames
 * @see AbstractAutoProxyCreator
 */
@SuppressWarnings("serial")
public class BeanNameAutoProxyCreator extends AbstractAutoProxyCreator {

	private List<String> beanNames;


	/**
	 * Set the names of the beans that should automatically get wrapped with proxies.
	 * A name can specify a prefix to match by ending with "*", e.g. "myBean,tx*"
	 * will match the bean named "myBean" and all beans whose name start with "tx".
	 * <p><b>NOTE:</b> In case of a FactoryBean, only the objects created by the
	 * FactoryBean will get proxied. This default behavior applies as of Spring 2.0.
	 * If you intend to proxy a FactoryBean instance itself (a rare use case, but
	 * Spring 1.2's default behavior), specify the bean name of the FactoryBean
	 * including the factory-bean prefix "&": e.g. "&myFactoryBean".
	 * <p>
	 * 设置应该使用代理自动包装的bean的名称名称可以指定要以"*"结尾匹配的前缀,例如"myBean,tx *"将匹配名为"myBean"的bean和所有名称从"tx"<p> <b>注意：</b>如果是Fa
	 * ctoryBean,只有FactoryBean创建的对象才能被代理。
	 * 默认行为适用于Spring 20如果您打算代理一个FactoryBean实例本身罕见的用例,但Spring 12的默认行为),指定FactoryBean的bean名称,包括factory-bean前缀"
	 * &"：eg"&myFactoryBean"。
	 * 
	 * 
	 * @see org.springframework.beans.factory.FactoryBean
	 * @see org.springframework.beans.factory.BeanFactory#FACTORY_BEAN_PREFIX
	 */
	public void setBeanNames(String... beanNames) {
		Assert.notEmpty(beanNames, "'beanNames' must not be empty");
		this.beanNames = new ArrayList<String>(beanNames.length);
		for (String mappedName : beanNames) {
			this.beanNames.add(StringUtils.trimWhitespace(mappedName));
		}
	}


	/**
	 * Identify as bean to proxy if the bean name is in the configured list of names.
	 * <p>
	 *  如果bean名称在配置的名称列表中,则将其标识为代理的bean
	 * 
	 */
	@Override
	protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {
		if (this.beanNames != null) {
			for (String mappedName : this.beanNames) {
				if (FactoryBean.class.isAssignableFrom(beanClass)) {
					if (!mappedName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
						continue;
					}
					mappedName = mappedName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
				}
				if (isMatch(beanName, mappedName)) {
					return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
				}
				BeanFactory beanFactory = getBeanFactory();
				if (beanFactory != null) {
					String[] aliases = beanFactory.getAliases(beanName);
					for (String alias : aliases) {
						if (isMatch(alias, mappedName)) {
							return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
						}
					}
				}
			}
		}
		return DO_NOT_PROXY;
	}

	/**
	 * Return if the given bean name matches the mapped name.
	 * <p>The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches,
	 * as well as direct equality. Can be overridden in subclasses.
	 * <p>
	 * 如果给定的bean名称与映射名匹配,则返回<p>默认实现检查"xxx *","* xxx"和"* xxx *"匹配以及直接相等可以在子类中覆盖
	 * 
	 * @param beanName the bean name to check
	 * @param mappedName the name in the configured list of names
	 * @return if the names match
	 * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
	 */
	protected boolean isMatch(String beanName, String mappedName) {
		return PatternMatchUtils.simpleMatch(mappedName, beanName);
	}

}
