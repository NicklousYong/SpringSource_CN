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

package org.springframework.beans.factory.config;

import java.lang.reflect.Field;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * {@link FactoryBean} which retrieves a static or non-static field value.
 *
 * <p>Typically used for retrieving public static final constants. Usage example:
 *
 * <pre class="code">// standard definition for exposing a static field, specifying the "staticField" property
 * &lt;bean id="myField" class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean"&gt;
 *   &lt;property name="staticField" value="java.sql.Connection.TRANSACTION_SERIALIZABLE"/&gt;
 * &lt;/bean&gt;
 *
 * // convenience version that specifies a static field pattern as bean name
 * &lt;bean id="java.sql.Connection.TRANSACTION_SERIALIZABLE"
 *       class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean"/&gt;</pre>
 * </pre>
 *
 * <p>If you are using Spring 2.0, you can also use the following style of configuration for
 * public static fields.
 *
 * <pre class="code">&lt;util:constant static-field="java.sql.Connection.TRANSACTION_SERIALIZABLE"/&gt;</pre>
 *
 * <p>
 *  {@link FactoryBean},它检索静态或非静态字段值
 * 
 *  <p>通常用于检索公共静态最终常量用法示例：
 * 
 * <pre class ="code"> //用于公开静态字段的标准定义,指定"staticField"属性&lt; bean id ="myField"class ="orgspringframewor
 * kbeansfactoryconfigFieldRetrievingFactoryBean"&gt; &lt; property name ="staticField"value ="javasqlCo
 * nnectionTRANSACTION_SERIALIZABLE"/&gt; &LT; /豆腐&GT;。
 * 
 *  //方便版本,指定一个静态字段模式作为bean名称&lt; bean id ="javasqlConnectionTRANSACTION_SERIALIZABLE"class ="orgspringf
 * rameworkbeansfactoryconfigFieldRetrievingFactoryBean"/&gt; </pre>。
 * </pre>
 * 
 *  <p>如果您使用的是Spring 20,还可以使用以下风格的公共静态字段
 * 
 *  <pre class ="code">&lt; util：constant static-field ="javasqlConnectionTRANSACTION_SERIALIZABLE"/&gt;
 *  </pre>。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setStaticField
 */
public class FieldRetrievingFactoryBean
		implements FactoryBean<Object>, BeanNameAware, BeanClassLoaderAware, InitializingBean {

	private Class<?> targetClass;

	private Object targetObject;

	private String targetField;

	private String staticField;

	private String beanName;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	// the field we will retrieve
	private Field fieldObject;


	/**
	 * Set the target class on which the field is defined.
	 * Only necessary when the target field is static; else,
	 * a target object needs to be specified anyway.
	 * <p>
	 * 设置定义字段的目标类只有当目标字段为静态时才需要;否则,需要指定目标对象
	 * 
	 * 
	 * @see #setTargetObject
	 * @see #setTargetField
	 */
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	/**
	 * Return the target class on which the field is defined.
	 * <p>
	 *  返回定义该字段的目标类
	 * 
	 */
	public Class<?> getTargetClass() {
		return targetClass;
	}

	/**
	 * Set the target object on which the field is defined.
	 * Only necessary when the target field is not static;
	 * else, a target class is sufficient.
	 * <p>
	 *  设置定义字段的目标对象只有当目标字段不是静态时才需要;否则,目标类就足够了
	 * 
	 * 
	 * @see #setTargetClass
	 * @see #setTargetField
	 */
	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
	}

	/**
	 * Return the target object on which the field is defined.
	 * <p>
	 *  返回定义字段的目标对象
	 * 
	 */
	public Object getTargetObject() {
		return this.targetObject;
	}

	/**
	 * Set the name of the field to be retrieved.
	 * Refers to either a static field or a non-static field,
	 * depending on a target object being set.
	 * <p>
	 *  设置要检索的字段的名称指静态字段或非静态字段,具体取决于正在设置的目标对象
	 * 
	 * 
	 * @see #setTargetClass
	 * @see #setTargetObject
	 */
	public void setTargetField(String targetField) {
		this.targetField = StringUtils.trimAllWhitespace(targetField);
	}

	/**
	 * Return the name of the field to be retrieved.
	 * <p>
	 *  返回要检索的字段的名称
	 * 
	 */
	public String getTargetField() {
		return this.targetField;
	}

	/**
	 * Set a fully qualified static field name to retrieve,
	 * e.g. "example.MyExampleClass.MY_EXAMPLE_FIELD".
	 * Convenient alternative to specifying targetClass and targetField.
	 * <p>
	 *  设置一个完全限定的静态字段名称来检索,例如"exampleMyExampleClassMY_EXAMPLE_FIELD"方便的替代方法来指定targetClass和targetField
	 * 
	 * 
	 * @see #setTargetClass
	 * @see #setTargetField
	 */
	public void setStaticField(String staticField) {
		this.staticField = StringUtils.trimAllWhitespace(staticField);
	}

	/**
	 * The bean name of this FieldRetrievingFactoryBean will be interpreted
	 * as "staticField" pattern, if neither "targetClass" nor "targetObject"
	 * nor "targetField" have been specified.
	 * This allows for concise bean definitions with just an id/name.
	 * <p>
	 * FieldRetrievingFactoryBean的bean名称将被解释为"staticField"模式,如果既没有指定"targetClass"也没有"targetObject"和"targetFi
	 * eld"。
	 */
	@Override
	public void setBeanName(String beanName) {
		this.beanName = StringUtils.trimAllWhitespace(BeanFactoryUtils.originalBeanName(beanName));
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}


	@Override
	public void afterPropertiesSet() throws ClassNotFoundException, NoSuchFieldException {
		if (this.targetClass != null && this.targetObject != null) {
			throw new IllegalArgumentException("Specify either targetClass or targetObject, not both");
		}

		if (this.targetClass == null && this.targetObject == null) {
			if (this.targetField != null) {
				throw new IllegalArgumentException(
						"Specify targetClass or targetObject in combination with targetField");
			}

			// If no other property specified, consider bean name as static field expression.
			if (this.staticField == null) {
				this.staticField = this.beanName;
			}

			// Try to parse static field into class and field.
			int lastDotIndex = this.staticField.lastIndexOf('.');
			if (lastDotIndex == -1 || lastDotIndex == this.staticField.length()) {
				throw new IllegalArgumentException(
						"staticField must be a fully qualified class plus static field name: " +
						"e.g. 'example.MyExampleClass.MY_EXAMPLE_FIELD'");
			}
			String className = this.staticField.substring(0, lastDotIndex);
			String fieldName = this.staticField.substring(lastDotIndex + 1);
			this.targetClass = ClassUtils.forName(className, this.beanClassLoader);
			this.targetField = fieldName;
		}

		else if (this.targetField == null) {
			// Either targetClass or targetObject specified.
			throw new IllegalArgumentException("targetField is required");
		}

		// Try to get the exact method first.
		Class<?> targetClass = (this.targetObject != null) ? this.targetObject.getClass() : this.targetClass;
		this.fieldObject = targetClass.getField(this.targetField);
	}


	@Override
	public Object getObject() throws IllegalAccessException {
		if (this.fieldObject == null) {
			throw new FactoryBeanNotInitializedException();
		}
		ReflectionUtils.makeAccessible(this.fieldObject);
		if (this.targetObject != null) {
			// instance field
			return this.fieldObject.get(this.targetObject);
		}
		else {
			// class field
			return this.fieldObject.get(null);
		}
	}

	@Override
	public Class<?> getObjectType() {
		return (this.fieldObject != null ? this.fieldObject.getType() : null);
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
