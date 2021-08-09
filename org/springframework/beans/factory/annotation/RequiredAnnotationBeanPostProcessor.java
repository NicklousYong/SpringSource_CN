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

package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Conventions;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that enforces required JavaBean properties to have been configured.
 * Required bean properties are detected through a Java 5 annotation:
 * by default, Spring's {@link Required} annotation.
 *
 * <p>The motivation for the existence of this BeanPostProcessor is to allow
 * developers to annotate the setter properties of their own classes with an
 * arbitrary JDK 1.5 annotation to indicate that the container must check
 * for the configuration of a dependency injected value. This neatly pushes
 * responsibility for such checking onto the container (where it arguably belongs),
 * and obviates the need (<b>in part</b>) for a developer to code a method that
 * simply checks that all required properties have actually been set.
 *
 * <p>Please note that an 'init' method may still need to implemented (and may
 * still be desirable), because all that this class does is enforce that a
 * 'required' property has actually been configured with a value. It does
 * <b>not</b> check anything else... In particular, it does not check that a
 * configured value is not {@code null}.
 *
 * <p>Note: A default RequiredAnnotationBeanPostProcessor will be registered
 * by the "context:annotation-config" and "context:component-scan" XML tags.
 * Remove or turn off the default annotation configuration there if you intend
 * to specify a custom RequiredAnnotationBeanPostProcessor bean definition.
 *
 * <p>
 * {@link orgspringframeworkbeansfactoryconfigBeanPostProcessor}实现强制所需的JavaBean属性进行配置通过Java 5注释检测需要的bean
 * 属性：默认情况下,Spring的{@link必需}注释。
 * 
 *  这个BeanPostProcessor存在的动机是允许开发人员使用任意的JDK 15注释来注释自己的类的setter属性,以表明容器必须检查依赖注入值的配置。
 * 这样检查容器(可以说是属于它),并且消除了开发人员编写一个简单地检查所有必需属性实际设置的方法的需要(<b>部分</b>)。
 * 
 * 请注意,"init"方法可能仍然需要实现(并且可能仍然是可取的),因为这个类所做的一切都是强制一个"必需"属性实际上已经配置了一个值它是<b>不检查任何其他特别的,它不检查配置的值不是{@code null}
 * 。
 * 
 *  注意：默认的RequiredAnnotationBeanPostProcessor将被"context：annotation-config"和"context：component-scan"XML标记
 * 注册。
 * 如果您打算指定一个自定义的RequiredAnnotationBeanPostProcessor bean定义,删除或关闭默认注释配置。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see #setRequiredAnnotationType
 * @see Required
 */
public class RequiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {

	/**
	 * Bean definition attribute that may indicate whether a given bean is supposed
	 * to be skipped when performing this post-processor's required property check.
	 * <p>
	 *  Bean定义属性可以指示在执行后处理器所需的属性检查时是否应跳过给定的bean
	 * 
	 * 
	 * @see #shouldSkip
	 */
	public static final String SKIP_REQUIRED_CHECK_ATTRIBUTE =
			Conventions.getQualifiedAttributeName(RequiredAnnotationBeanPostProcessor.class, "skipRequiredCheck");


	private Class<? extends Annotation> requiredAnnotationType = Required.class;

	private int order = Ordered.LOWEST_PRECEDENCE - 1;

	private ConfigurableListableBeanFactory beanFactory;

	/**
	 * Cache for validated bean names, skipping re-validation for the same bean
	 * <p>
	 * 缓存用于验证的bean名称,跳过同一个bean的重新验证
	 * 
	 */
	private final Set<String> validatedBeanNames =
			Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(64));


	/**
	 * Set the 'required' annotation type, to be used on bean property
	 * setter methods.
	 * <p>The default required annotation type is the Spring-provided
	 * {@link Required} annotation.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a property value
	 * is required.
	 * <p>
	 *  设置"必需"注释类型,用于bean属性setter方法<p>默认的必需注释类型是Spring提供的{@link必需}注释<p>此setter属性存在,以便开发人员可以提供自己的非Spring特定)注释
	 * 类型,以指示属性值是必需的。
	 * 
	 */
	public void setRequiredAnnotationType(Class<? extends Annotation> requiredAnnotationType) {
		Assert.notNull(requiredAnnotationType, "'requiredAnnotationType' must not be null");
		this.requiredAnnotationType = requiredAnnotationType;
	}

	/**
	 * Return the 'required' annotation type.
	 * <p>
	 *  返回"必需"注释类型
	 * 
	 */
	protected Class<? extends Annotation> getRequiredAnnotationType() {
		return this.requiredAnnotationType;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
		}
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}


	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
	}

	@Override
	public PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {

		if (!this.validatedBeanNames.contains(beanName)) {
			if (!shouldSkip(this.beanFactory, beanName)) {
				List<String> invalidProperties = new ArrayList<String>();
				for (PropertyDescriptor pd : pds) {
					if (isRequiredProperty(pd) && !pvs.contains(pd.getName())) {
						invalidProperties.add(pd.getName());
					}
				}
				if (!invalidProperties.isEmpty()) {
					throw new BeanInitializationException(buildExceptionMessage(invalidProperties, beanName));
				}
			}
			this.validatedBeanNames.add(beanName);
		}
		return pvs;
	}

	/**
	 * Check whether the given bean definition is not subject to the annotation-based
	 * required property check as performed by this post-processor.
	 * <p>The default implementations check for the presence of the
	 * {@link #SKIP_REQUIRED_CHECK_ATTRIBUTE} attribute in the bean definition, if any.
	 * It also suggests skipping in case of a bean definition with a "factory-bean"
	 * reference set, assuming that instance-based factories pre-populate the bean.
	 * <p>
	 * 检查给定的bean定义是否不受这个后处理器所执行的基于注释的所需属性检查。
	 * 缺省实现检查bean定义中是否存在{@link #SKIP_REQUIRED_CHECK_ATTRIBUTE}属性(如果有)它还建议在具有"factory-bean"引用集的bean定义的情况下跳过,假
	 * 设基于实例的工厂预先填充bean。
	 * 检查给定的bean定义是否不受这个后处理器所执行的基于注释的所需属性检查。
	 * 
	 * 
	 * @param beanFactory the BeanFactory to check against
	 * @param beanName the name of the bean to check against
	 * @return {@code true} to skip the bean; {@code false} to process it
	 */
	protected boolean shouldSkip(ConfigurableListableBeanFactory beanFactory, String beanName) {
		if (beanFactory == null || !beanFactory.containsBeanDefinition(beanName)) {
			return false;
		}
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
		if (beanDefinition.getFactoryBeanName() != null) {
			return true;
		}
		Object value = beanDefinition.getAttribute(SKIP_REQUIRED_CHECK_ATTRIBUTE);
		return (value != null && (Boolean.TRUE.equals(value) || Boolean.valueOf(value.toString())));
	}

	/**
	 * Is the supplied property required to have a value (that is, to be dependency-injected)?
	 * <p>This implementation looks for the existence of a
	 * {@link #setRequiredAnnotationType "required" annotation}
	 * on the supplied {@link PropertyDescriptor property}.
	 * <p>
	 *  提供的属性是否需要具有值(即依赖注入)? <p>此实现在提供的{@link PropertyDescriptor属性}上查找存在{@link #setRequiredAnnotationType"必需"注释}
	 * 。
	 * 
	 * 
	 * @param propertyDescriptor the target PropertyDescriptor (never {@code null})
	 * @return {@code true} if the supplied property has been marked as being required;
	 * {@code false} if not, or if the supplied property does not have a setter method
	 */
	protected boolean isRequiredProperty(PropertyDescriptor propertyDescriptor) {
		Method setter = propertyDescriptor.getWriteMethod();
		return (setter != null && AnnotationUtils.getAnnotation(setter, getRequiredAnnotationType()) != null);
	}

	/**
	 * Build an exception message for the given list of invalid properties.
	 * <p>
	 * 
	 * @param invalidProperties the list of names of invalid properties
	 * @param beanName the name of the bean
	 * @return the exception message
	 */
	private String buildExceptionMessage(List<String> invalidProperties, String beanName) {
		int size = invalidProperties.size();
		StringBuilder sb = new StringBuilder();
		sb.append(size == 1 ? "Property" : "Properties");
		for (int i = 0; i < size; i++) {
			String propertyName = invalidProperties.get(i);
			if (i > 0) {
				if (i == (size - 1)) {
					sb.append(" and");
				}
				else {
					sb.append(",");
				}
			}
			sb.append(" '").append(propertyName).append("'");
		}
		sb.append(size == 1 ? " is" : " are");
		sb.append(" required for bean '").append(beanName).append("'");
		return sb.toString();
	}

}
