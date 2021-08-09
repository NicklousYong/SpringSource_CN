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

package org.springframework.beans.factory.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

/**
 * {@link FactoryBean} that evaluates a property path on a given target object.
 *
 * <p>The target object can be specified directly or via a bean name.
 *
 * <p>Usage examples:
 *
 * <pre class="code">&lt;!-- target bean to be referenced by name --&gt;
 * &lt;bean id="tb" class="org.springframework.beans.TestBean" singleton="false"&gt;
 *   &lt;property name="age" value="10"/&gt;
 *   &lt;property name="spouse"&gt;
 *     &lt;bean class="org.springframework.beans.TestBean"&gt;
 *       &lt;property name="age" value="11"/&gt;
 *     &lt;/bean&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 *
 * &lt;!-- will result in 12, which is the value of property 'age' of the inner bean --&gt;
 * &lt;bean id="propertyPath1" class="org.springframework.beans.factory.config.PropertyPathFactoryBean"&gt;
 *   &lt;property name="targetObject"&gt;
 *     &lt;bean class="org.springframework.beans.TestBean"&gt;
 *       &lt;property name="age" value="12"/&gt;
 *     &lt;/bean&gt;
 *   &lt;/property&gt;
 *   &lt;property name="propertyPath" value="age"/&gt;
 * &lt;/bean&gt;
 *
 * &lt;!-- will result in 11, which is the value of property 'spouse.age' of bean 'tb' --&gt;
 * &lt;bean id="propertyPath2" class="org.springframework.beans.factory.config.PropertyPathFactoryBean"&gt;
 *   &lt;property name="targetBeanName" value="tb"/&gt;
 *   &lt;property name="propertyPath" value="spouse.age"/&gt;
 * &lt;/bean&gt;
 *
 * &lt;!-- will result in 10, which is the value of property 'age' of bean 'tb' --&gt;
 * &lt;bean id="tb.age" class="org.springframework.beans.factory.config.PropertyPathFactoryBean"/&gt;</pre>
 *
 * <p>If you are using Spring 2.0 and XML Schema support in your configuration file(s),
 * you can also use the following style of configuration for property path access.
 * (See also the appendix entitled 'XML Schema-based configuration' in the Spring
 * reference manual for more examples.)
 *
 * <pre class="code"> &lt;!-- will result in 10, which is the value of property 'age' of bean 'tb' --&gt;
 * &lt;util:property-path id="name" path="testBean.age"/&gt;</pre>
 *
 * Thanks to Matthias Ernst for the suggestion and initial prototype!
 *
 * <p>
 *  {@link FactoryBean},它评估给定目标对象的属性路径
 * 
 *  <p>可以直接指定目标对象,也可以通过bean名称指定目标对象
 * 
 *  <p>使用示例：
 * 
 * <pre class ="code">&lt;！ - 要由名称引用的目标bean  - &gt; &lt; bean id ="tb"class ="orgspringframeworkbeansTes
 * tBean"singleton ="false"&gt; &lt; property name ="age"value ="10"/&gt; &lt; property name ="spouse"&g
 * t; &lt; bean class ="orgspringframeworkbeansTestBean"&gt; &lt; property name ="age"value ="11"/&gt; &
 * LT; /豆腐&GT; &LT; /性&gt; &LT; /豆腐&GT;。
 * 
 *  &lt;！ - 将导致12,这是内部bean的属性'age'的值 - &gt; &lt; bean id ="propertyPath1"class ="orgspringframeworkbeans
 * factoryconfigPropertyPathFactoryBean"&gt; &lt; property name ="targetObject"&gt; &lt; bean class ="or
 * gspringframeworkbeansTestBean"&gt; &lt; property name ="age"value ="12"/&gt; &LT; /豆腐&GT; &LT; /性&gt;
 *  &lt; property name ="propertyPath"value ="age"/&gt; &LT; /豆腐&GT;。
 * 
 * &lt;！ - 将导致11,这是bean'tb'的属性'spouseage'的值 - > &lt; bean id ="propertyPath2"class ="orgspringframeworkb
 * eansfactoryconfigPropertyPathFactoryBean"&gt; &lt; property name ="targetBeanName"value ="tb"/&gt; &l
 * t; property name ="propertyPath"value ="spouseage"/&gt; &LT; /豆腐&GT;。
 * 
 *  &lt;！ - 将导致10,这是bean'tb'的属性'age'的值 - &gt; &lt; bean id ="tbage"class ="orgspringframeworkbeansfactor
 * yconfigPropertyPathFactoryBean"/&gt; </pre>。
 * 
 *  <p>如果您在配置文件中使用Spring 20和XML Schema支持,还可以使用以下样式进行属性路径访问(另请参见Spring中的"基于XML Schema的配置"附录)参考手册更多的例子)
 * 
 * <pre class ="code">&lt;！ - 将导致10,这是bean'tb'的属性'age'的值 - > &lt; util：property-path id ="name"path ="te
 * 
 * @author Juergen Hoeller
 * @since 1.1.2
 * @see #setTargetObject
 * @see #setTargetBeanName
 * @see #setPropertyPath
 */
public class PropertyPathFactoryBean implements FactoryBean<Object>, BeanNameAware, BeanFactoryAware {

	private static final Log logger = LogFactory.getLog(PropertyPathFactoryBean.class);

	private BeanWrapper targetBeanWrapper;

	private String targetBeanName;

	private String propertyPath;

	private Class<?> resultType;

	private String beanName;

	private BeanFactory beanFactory;


	/**
	 * Specify a target object to apply the property path to.
	 * Alternatively, specify a target bean name.
	 * <p>
	 * stBeanage"/&gt; </pre>。
	 * 
	 *  感谢Matthias Ernst的建议和初始原型！
	 * 
	 * 
	 * @param targetObject a target object, for example a bean reference
	 * or an inner bean
	 * @see #setTargetBeanName
	 */
	public void setTargetObject(Object targetObject) {
		this.targetBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(targetObject);
	}

	/**
	 * Specify the name of a target bean to apply the property path to.
	 * Alternatively, specify a target object directly.
	 * <p>
	 *  指定一个目标对象来应用该属性路径或者,指定一个目标bean名称
	 * 
	 * 
	 * @param targetBeanName the bean name to be looked up in the
	 * containing bean factory (e.g. "testBean")
	 * @see #setTargetObject
	 */
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = StringUtils.trimAllWhitespace(targetBeanName);
	}

	/**
	 * Specify the property path to apply to the target.
	 * <p>
	 *  指定要将属性路径应用于目标bean的名称,或者直接指定目标对象
	 * 
	 * 
	 * @param propertyPath the property path, potentially nested
	 * (e.g. "age" or "spouse.age")
	 */
	public void setPropertyPath(String propertyPath) {
		this.propertyPath = StringUtils.trimAllWhitespace(propertyPath);
	}

	/**
	 * Specify the type of the result from evaluating the property path.
	 * <p>Note: This is not necessary for directly specified target objects
	 * or singleton target beans, where the type can be determined through
	 * introspection. Just specify this in case of a prototype target,
	 * provided that you need matching by type (for example, for autowiring).
	 * <p>
	 *  指定要应用于目标的属性路径
	 * 
	 * 
	 * @param resultType the result type, for example "java.lang.Integer"
	 */
	public void setResultType(Class<?> resultType) {
		this.resultType = resultType;
	}

	/**
	 * The bean name of this PropertyPathFactoryBean will be interpreted
	 * as "beanName.property" pattern, if neither "targetObject" nor
	 * "targetBeanName" nor "propertyPath" have been specified.
	 * This allows for concise bean definitions with just an id/name.
	 * <p>
	 * 通过评估属性路径指定结果的类型<p>注意：对于直接指定的目标对象或单例目标bean而言,这不是必需的,其中可以通过内省来确定类型。
	 * 只要在原型目标的情况下指定,只要您需要按类型匹配(例如,用于自动装配)。
	 * 
	 */
	@Override
	public void setBeanName(String beanName) {
		this.beanName = StringUtils.trimAllWhitespace(BeanFactoryUtils.originalBeanName(beanName));
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;

		if (this.targetBeanWrapper != null && this.targetBeanName != null) {
			throw new IllegalArgumentException("Specify either 'targetObject' or 'targetBeanName', not both");
		}

		if (this.targetBeanWrapper == null && this.targetBeanName == null) {
			if (this.propertyPath != null) {
				throw new IllegalArgumentException(
						"Specify 'targetObject' or 'targetBeanName' in combination with 'propertyPath'");
			}

			// No other properties specified: check bean name.
			int dotIndex = this.beanName.indexOf('.');
			if (dotIndex == -1) {
				throw new IllegalArgumentException(
						"Neither 'targetObject' nor 'targetBeanName' specified, and PropertyPathFactoryBean " +
						"bean name '" + this.beanName + "' does not follow 'beanName.property' syntax");
			}
			this.targetBeanName = this.beanName.substring(0, dotIndex);
			this.propertyPath = this.beanName.substring(dotIndex + 1);
		}

		else if (this.propertyPath == null) {
			// either targetObject or targetBeanName specified
			throw new IllegalArgumentException("'propertyPath' is required");
		}

		if (this.targetBeanWrapper == null && this.beanFactory.isSingleton(this.targetBeanName)) {
			// Eagerly fetch singleton target bean, and determine result type.
			Object bean = this.beanFactory.getBean(this.targetBeanName);
			this.targetBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
			this.resultType = this.targetBeanWrapper.getPropertyType(this.propertyPath);
		}
	}


	@Override
	public Object getObject() throws BeansException {
		BeanWrapper target = this.targetBeanWrapper;
		if (target != null) {
			if (logger.isWarnEnabled() && this.targetBeanName != null &&
					this.beanFactory instanceof ConfigurableBeanFactory &&
					((ConfigurableBeanFactory) this.beanFactory).isCurrentlyInCreation(this.targetBeanName)) {
				logger.warn("Target bean '" + this.targetBeanName + "' is still in creation due to a circular " +
						"reference - obtained value for property '" + this.propertyPath + "' may be outdated!");
			}
		}
		else {
			// Fetch prototype target bean...
			Object bean = this.beanFactory.getBean(this.targetBeanName);
			target = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		}
		return target.getPropertyValue(this.propertyPath);
	}

	@Override
	public Class<?> getObjectType() {
		return this.resultType;
	}

	/**
	 * While this FactoryBean will often be used for singleton targets,
	 * the invoked getters for the property path might return a new object
	 * for each call, so we have to assume that we're not returning the
	 * same object for each {@link #getObject()} call.
	 * <p>
	 *  如果没有指定"targetObject"或"targetBeanName"和"propertyPath",则此PropertyPathFactoryBean的bean名称将被解释为"beanNamep
	 * roperty"模式。
	 * 这允许使用简单的bean定义,只需要一个id / name。
	 * 
	 */
	@Override
	public boolean isSingleton() {
		return false;
	}

}
