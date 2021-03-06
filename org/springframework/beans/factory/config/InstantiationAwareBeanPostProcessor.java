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

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;

/**
 * Subinterface of {@link BeanPostProcessor} that adds a before-instantiation callback,
 * and a callback after instantiation but before explicit properties are set or
 * autowiring occurs.
 *
 * <p>Typically used to suppress default instantiation for specific target beans,
 * for example to create proxies with special TargetSources (pooling targets,
 * lazily initializing targets, etc), or to implement additional injection strategies
 * such as field injection.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework. It is recommended to implement the plain
 * {@link BeanPostProcessor} interface as far as possible, or to derive from
 * {@link InstantiationAwareBeanPostProcessorAdapter} in order to be shielded
 * from extensions to this interface.
 *
 * <p>
 *  {@link BeanPostProcessor}的子界面,添加了实例前回调,并且在实例化之后但在显式属性设置或自动连线发生之前的回调
 * 
 * 通常用于抑制特定目标bean的默认实例化,例如创建具有特殊TargetSource(池化目标,延迟初始化目标等)的代理,或者实现其他注入策略,如现场注入
 * 
 *  <p> <b>注意：</b>此接口是一个专用接口,主要用于框架内部使用建议尽可能实现简单的{@link BeanPostProcessor}接口,或从{ @link InstantiationAwareBeanPostProcessorAdapter}
 * ,以便屏蔽该接口的扩展。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 1.2
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor <i>before the target bean gets instantiated</i>.
	 * The returned bean object may be a proxy to use instead of the target bean,
	 * effectively suppressing default instantiation of the target bean.
	 * <p>If a non-null object is returned by this method, the bean creation process
	 * will be short-circuited. The only further processing applied is the
	 * {@link #postProcessAfterInitialization} callback from the configured
	 * {@link BeanPostProcessor BeanPostProcessors}.
	 * <p>This callback will only be applied to bean definitions with a bean class.
	 * In particular, it will not be applied to beans with a "factory-method".
	 * <p>Post-processors may implement the extended
	 * {@link SmartInstantiationAwareBeanPostProcessor} interface in order
	 * to predict the type of the bean object that they are going to return here.
	 * <p>
	 * 在目标bean被实例化之前应用此BeanPostProcessor <i>返回的bean对象可以是代替目标bean的代理,有效地抑制目标bean的默认实例化<p>如果非空对象是通过这种方法返回,bean
	 * 创建过程将被短路。
	 * 应用的唯一进一步处理是从配置的{@link BeanPostProcessor BeanPostProcessors}的{@link #postProcessAfterInitialization}回调
	 * <p>这个回调只会应用于bean定义,一个bean类特别地,它不会应用于具有"factory-method"的bean<p>后处理器可以实现扩展的{@link SmartInstantiationAwareBeanPostProcessor}
	 * 接口,以便预测他们将要返回的bean对象的类型。
	 * 
	 * 
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return the bean object to expose instead of a default instance of the target bean,
	 * or {@code null} to proceed with default instantiation
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#hasBeanClass
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getFactoryMethodName
	 */
	Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

	/**
	 * Perform operations after the bean has been instantiated, via a constructor or factory method,
	 * but before Spring property population (from explicit properties or autowiring) occurs.
	 * <p>This is the ideal callback for performing field injection on the given bean instance.
	 * See Spring's own {@link org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor}
	 * for a typical example.
	 * <p>
	 * 在bean实例化之后,通过构造函数或工厂方法执行操作,但是Spring属性(从显式属性或自动布线)出现之前,这是在给定的bean实例上执行字段注入的理想回调参见Spring的自己的{ @link orgspringframeworkbeansfactoryannotationAutowiredAnnotationBeanPostProcessor}
	 * 为典型的例子。
	 * 
	 * 
	 * @param bean the bean instance created, with properties not having been set yet
	 * @param beanName the name of the bean
	 * @return {@code true} if properties should be set on the bean; {@code false}
	 * if property population should be skipped. Normal implementations should return {@code true}.
	 * Returning {@code false} will also prevent any subsequent InstantiationAwareBeanPostProcessor
	 * instances being invoked on this bean instance.
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;

	/**
	 * Post-process the given property values before the factory applies them
	 * to the given bean. Allows for checking whether all dependencies have been
	 * satisfied, for example based on a "Required" annotation on bean property setters.
	 * <p>Also allows for replacing the property values to apply, typically through
	 * creating a new MutablePropertyValues instance based on the original PropertyValues,
	 * adding or removing specific values.
	 * <p>
	 * 在工厂将它们应用到给定的bean之前,处理给定的属性值允许检查是否满足所有依赖关系,例如基于bean属性setters上的"必需"注释<p>还允许将属性值替换为通常通过基于原始PropertyValue
	 * s创建一个新的MutablePropertyValues实例,添加或删除特定值。
	 * 
	 * @param pvs the property values that the factory is about to apply (never {@code null})
	 * @param pds the relevant property descriptors for the target bean (with ignored
	 * dependency types - which the factory handles specifically - already filtered out)
	 * @param bean the bean instance created, but whose properties have not yet been set
	 * @param beanName the name of the bean
	 * @return the actual property values to apply to the given bean
	 * (can be the passed-in PropertyValues instance), or {@code null}
	 * to skip property population
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.MutablePropertyValues
	 */
	PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException;

}
