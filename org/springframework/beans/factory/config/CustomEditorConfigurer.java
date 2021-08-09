/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

import java.beans.PropertyEditor;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

/**
 * {@link BeanFactoryPostProcessor} implementation that allows for convenient
 * registration of custom {@link PropertyEditor property editors}.
 *
 * <p>In case you want to register {@link PropertyEditor} instances,
 * the recommended usage as of Spring 2.0 is to use custom
 * {@link PropertyEditorRegistrar} implementations that in turn register any
 * desired editor instances on a given
 * {@link org.springframework.beans.PropertyEditorRegistry registry}. Each
 * PropertyEditorRegistrar can register any number of custom editors.
 *
 * <pre class="code">
 * &lt;bean id="customEditorConfigurer" class="org.springframework.beans.factory.config.CustomEditorConfigurer"&gt;
 *   &lt;property name="propertyEditorRegistrars"&gt;
 *     &lt;list&gt;
 *       &lt;bean class="mypackage.MyCustomDateEditorRegistrar"/&gt;
 *       &lt;bean class="mypackage.MyObjectEditorRegistrar"/&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * <p>
 * It's perfectly fine to register {@link PropertyEditor} <em>classes</em> via
 * the {@code customEditors} property. Spring will create fresh instances of
 * them for each editing attempt then:
 *
 * <pre class="code">
 * &lt;bean id="customEditorConfigurer" class="org.springframework.beans.factory.config.CustomEditorConfigurer"&gt;
 *   &lt;property name="customEditors"&gt;
 *     &lt;map&gt;
 *       &lt;entry key="java.util.Date" value="mypackage.MyCustomDateEditor"/&gt;
 *       &lt;entry key="mypackage.MyObject" value="mypackage.MyObjectEditor"/&gt;
 *     &lt;/map&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * <p>
 * Note, that you shouldn't register {@link PropertyEditor} bean instances via
 * the {@code customEditors} property as {@link PropertyEditor}s are stateful
 * and the instances will then have to be synchronized for every editing
 * attempt. In case you need control over the instantiation process of
 * {@link PropertyEditor}s, use a {@link PropertyEditorRegistrar} to register
 * them.
 *
 * <p>
 * Also supports "java.lang.String[]"-style array class names and primitive
 * class names (e.g. "boolean"). Delegates to {@link ClassUtils} for actual
 * class name resolution.
 *
 * <p><b>NOTE:</b> Custom property editors registered with this configurer do
 * <i>not</i> apply to data binding. Custom editors for data binding need to
 * be registered on the {@link org.springframework.validation.DataBinder}:
 * Use a common base class or delegate to common PropertyEditorRegistrar
 * implementations to reuse editor registration there.
 *
 * <p>
 *  {@link BeanFactoryPostProcessor}实现,允许方便地注册自定义{@link PropertyEditor属性编辑器}
 * 
 * <p>如果您想注册{@link PropertyEditor}实例,建议使用Spring 20的使用方法是使用自定义{@link PropertyEditorRegistrar}实现,然后在给定的{@link orgspringframeworkbeansPropertyEditorRegistry注册表}
 * 中注册任何所需的编辑器实例每个PropertyEditorRegistrar可以注册任意数量的自定义编辑器。
 * 
 * <pre class="code">
 *  &lt; bean id ="customEditorConfigurer"class ="orgspringframeworkbeansfactoryconfigCustomEditorConfig
 * urer"&gt; &lt; property name ="propertyEditorRegistrars"&gt; &LT;列表&gt; &lt; bean class ="mypackageMy
 * CustomDateEditorRegistrar"/&gt; &lt; bean class ="mypackageMyObjectEditorRegistrar"/&gt; &LT; /列表&gt;
 *  &LT; /性&gt; &LT; /豆腐&GT;。
 * </pre>
 * 
 * <p>
 * 通过{@code customEditors}属性注册{@link PropertyEditor} <em>类是非常好的,Spring会为每次编辑尝试创建新的实例,然后：
 * 
 * <pre class="code">
 *  &lt; bean id ="customEditorConfigurer"class ="orgspringframeworkbeansfactoryconfigCustomEditorConfig
 * urer"&gt; &lt; property name ="customEditors"&gt; &LT;地图&GT; &lt; entry key ="javautilDate"value ="my
 * packageMyCustomDateEditor"/&gt; &lt; entry key ="mypackageMyObject"value ="mypackageMyObjectEditor"/&
 * gt; &LT; /地图&GT; &LT; /性&gt; &LT; /豆腐&GT;。
 * 
 * @author Juergen Hoeller
 * @since 27.02.2004
 * @see java.beans.PropertyEditor
 * @see org.springframework.beans.PropertyEditorRegistrar
 * @see ConfigurableBeanFactory#addPropertyEditorRegistrar
 * @see ConfigurableBeanFactory#registerCustomEditor
 * @see org.springframework.validation.DataBinder#registerCustomEditor
 */
public class CustomEditorConfigurer implements BeanFactoryPostProcessor, Ordered {

	protected final Log logger = LogFactory.getLog(getClass());

	private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered

	private PropertyEditorRegistrar[] propertyEditorRegistrars;

	private Map<Class<?>, Class<? extends PropertyEditor>> customEditors;


	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * Specify the {@link PropertyEditorRegistrar PropertyEditorRegistrars}
	 * to apply to beans defined within the current application context.
	 * <p>This allows for sharing {@code PropertyEditorRegistrars} with
	 * {@link org.springframework.validation.DataBinder DataBinders}, etc.
	 * Furthermore, it avoids the need for synchronization on custom editors:
	 * A {@code PropertyEditorRegistrar} will always create fresh editor
	 * instances for each bean creation attempt.
	 * <p>
	 * </pre>
	 * 
	 * <p>
	 * 请注意,您不应通过{@code customEditors}属性注册{@link PropertyEditor} bean实例,因为{@link PropertyEditor}是有状态的,然后必须对每个
	 * 编辑尝试进行同步实例如果需要控制在{@link PropertyEditor}的实例化过程中,使用{@link PropertyEditorRegistrar}注册它们。
	 * 
	 * <p>
	 *  还支持"javalangString []" - 样式数组类名和原始类名(例如"boolean")代表{@link ClassUtils}实现类名解析
	 * 
	 * <p> <b>注意：</b>在此配置程序中注册的自定义属性编辑器<i>不适用于数据绑定用于数据绑定的自定义编辑者需要在{@link orgspringframeworkvalidationDataBinder}
	 * 
	 * @see ConfigurableListableBeanFactory#addPropertyEditorRegistrar
	 */
	public void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
		this.propertyEditorRegistrars = propertyEditorRegistrars;
	}

	/**
	 * Specify the custom editors to register via a {@link Map}, using the
	 * class name of the required type as the key and the class name of the
	 * associated {@link PropertyEditor} as value.
	 * <p>
	 * 上注册：使用一个常见的基类或委托通用的PropertyEditorRegistrar实现来重用编辑器注册。
	 * 
	 * 
	 * @see ConfigurableListableBeanFactory#registerCustomEditor
	 */
	public void setCustomEditors(Map<Class<?>, Class<? extends PropertyEditor>> customEditors) {
		this.customEditors = customEditors;
	}


	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (this.propertyEditorRegistrars != null) {
			for (PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
				beanFactory.addPropertyEditorRegistrar(propertyEditorRegistrar);
			}
		}
		if (this.customEditors != null) {
			for (Map.Entry<Class<?>, Class<? extends PropertyEditor>> entry : this.customEditors.entrySet()) {
				Class<?> requiredType = entry.getKey();
				Class<? extends PropertyEditor> propertyEditorClass = entry.getValue();
				beanFactory.registerCustomEditor(requiredType, propertyEditorClass);
			}
		}
	}

}
