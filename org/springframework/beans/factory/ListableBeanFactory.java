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

package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;

/**
 * Extension of the {@link BeanFactory} interface to be implemented by bean factories
 * that can enumerate all their bean instances, rather than attempting bean lookup
 * by name one by one as requested by clients. BeanFactory implementations that
 * preload all their bean definitions (such as XML-based factories) may implement
 * this interface.
 *
 * <p>If this is a {@link HierarchicalBeanFactory}, the return values will <i>not</i>
 * take any BeanFactory hierarchy into account, but will relate only to the beans
 * defined in the current factory. Use the {@link BeanFactoryUtils} helper class
 * to consider beans in ancestor factories too.
 *
 * <p>The methods in this interface will just respect bean definitions of this factory.
 * They will ignore any singleton beans that have been registered by other means like
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}'s
 * {@code registerSingleton} method, with the exception of
 * {@code getBeanNamesOfType} and {@code getBeansOfType} which will check
 * such manually registered singletons too. Of course, BeanFactory's {@code getBean}
 * does allow transparent access to such special beans as well. However, in typical
 * scenarios, all beans will be defined by external bean definitions anyway, so most
 * applications don't need to worry about this differentiation.
 *
 * <p><b>NOTE:</b> With the exception of {@code getBeanDefinitionCount}
 * and {@code containsBeanDefinition}, the methods in this interface
 * are not designed for frequent invocation. Implementations may be slow.
 *
 * <p>
 * 扩展{@link BeanFactory}接口,由bean工厂实现,可以枚举所有的bean实例,而不是按照客户端的BeanFactory实现按照名称一个一个地尝试bean查找它们预先加载其所有bean定
 * 义(如XML-基于工厂)可以实现这个接口。
 * 
 *  <p>如果这是一个{@link HierarchicalBeanFactory},则返回值将<i>不会考虑任何BeanFactory层次结构,但将仅与当前工厂中定义的bean相关联使用{@link BeanFactoryUtils }
 * 助手类也考虑在祖先工厂的bean。
 * 
 * <p>这个接口中的方法将仅仅遵循这个工厂的bean定义他们将忽略通过{@link orgspringframeworkbeansfactoryconfigConfigurableBeanFactory}
 * 的{@code registerSingleton}方法注册的任何单例bean,除{@代码getBeanNamesOfType}和{@code getBeansOfType},这将检查这样的手动注册单例
 * 当然,BeanFactory的{@code getBean}也允许透明访问这些特殊的bean。
 * 然而,在典型的情况下,所有的bean都将由外部bean定义无论如何,定义,所以大多数应用程序不需要担心这种差异。
 * 
 * <p> <b>注意：</b>除{@code getBeanDefinitionCount}和{@code containsBeanDefinition}之外,此接口中的方法不是为频繁调用而设计的。
 * 实现可能很慢。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * Check if this bean factory contains a bean definition with the given name.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * <p>
	 *  检查此bean工厂是否包含具有给定名称的bean定义<p>不考虑此工厂可能参与的任何层次结构,并忽略通过其他方式而不是bean定义注册的任何单例bean
	 * 
	 * 
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a bean definition with the given name
	 * @see #containsBean
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * Return the number of beans defined in the factory.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * <p>
	 *  返回工厂定义的bean数量<p>不考虑此工厂可能参与的任何层次结构,并忽略通过其他方式注册的任何单例bean,而不是bean定义
	 * 
	 * 
	 * @return the number of beans defined in the factory
	 */
	int getBeanDefinitionCount();

	/**
	 * Return the names of all beans defined in this factory.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * <p>
	 * 返回此工厂中定义的所有bean的名称<p>不考虑此工厂可能参与的任何层次结构,并忽略通过其他方式注册的任何单例bean,而不是bean定义
	 * 
	 * 
	 * @return the names of all beans defined in this factory,
	 * or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * <p>
	 * 返回与给定类型(包括子类)匹配的bean的名称,从FactoryBeans <p> <b>的情况下,从bean定义或{@code getObjectType}的值判断注意：此方法仅仅介绍顶级bean < / b>
	 * 它不</i>检查可能匹配指定类型的嵌套bean <p>考虑由FactoryBeans创建的对象,这意味着FactoryBeans将被初始化如果FactoryBean创建的对象不是" t匹配,原始Fact
	 * oryBean本身将匹配类型<p>不考虑此工厂可能参与使用BeanFactoryUtils'{@code beanNamesForTypeIncludingAncestors}中的任何层次结构,以在祖先
	 * 工厂中包含bean<p>注意：<i>不是</i>忽略已经通过其他方式注册的单例bean而不是bean定义<p>此版本的{@code getBeanNamesForType}匹配各种bean,无论是单身,
	 * 原型,或FactoryBeans在大多数实现中,结果将与{@code getBeanNamesForType(type,true,true))中的结果相同} <p>此方法返回的Bean名称应始终按照定义
	 * 的顺序返回bean名称<我在后端配置中尽可能的。
	 * 
	 * 
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @since 4.2
	 * @see #isTypeMatch(String, ResolvableType)
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType)
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * <p>
	 * 返回与给定类型(包括子类)匹配的bean的名称,从FactoryBeans <p> <b>的情况下,从bean定义或{@code getObjectType}的值判断注意：此方法仅仅介绍顶级bean < / b>
	 * 它不</i>检查可能匹配指定类型的嵌套bean <p>考虑由FactoryBeans创建的对象,这意味着FactoryBeans将被初始化如果FactoryBean创建的对象不是" t匹配,原始Fact
	 * oryBean本身将匹配类型<p>不考虑此工厂可能参与使用BeanFactoryUtils'{@code beanNamesForTypeIncludingAncestors}中的任何层次结构,以在祖先
	 * 工厂中包含bean<p>注意：<i>不是</i>忽略已经通过其他方式注册的单例bean而不是bean定义<p>此版本的{@code getBeanNamesForType}匹配各种bean,无论是单身,
	 * 原型,或FactoryBeans在大多数实现中,结果将与{@code getBeanNamesForType(type,true,true))中的结果相同} <p>此方法返回的Bean名称应始终按照定义
	 * 的顺序返回bean名称<我在后端配置中尽可能的。
	 * 
	 * 
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	String[] getBeanNamesForType(Class<?> type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * <p>
	 * 返回与给定类型(包括子类)匹配的bean的名称,从FactoryBeans <p> <b>的情况下,从bean定义或{@code getObjectType}的值判断注意：此方法仅仅介绍顶级bean < / b>
	 * 它不</i>检查可能匹配指定类型的嵌套bean <p>如果设置了"allowEagerInit"标志,则会考虑FactoryBeans创建的对象,这意味着FactoryBeans将被初始化If Fact
	 * oryBean创建的对象不匹配,原始FactoryBean本身将匹配类型如果未设置"allowEagerInit",则只会检查原始FactoryBeans(不需要初始化每个FactoryBean)<p>
	 * 是否不考虑这个工厂可能参与的任何层次结构使用BeanFactoryUtils的{@code beanNamesForTypeIncludingAncestors}将bean包含在祖先工厂中也是<p>注意
	 * ：<i>不</i>忽略通过其他方式而不是bean定义注册的单例bean <p>此命令返回的Bean名称方法应该始终以后端配置中定义</i>的顺序返回bean名称<i>。
	 * 
	 * 
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of getBeansOfType matches all kinds of beans, be it
	 * singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeansOfType(type, true, true)}.
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * <p>
	 * 返回与给定对象类型(包括子类)匹配的bean实例,从FactoryBeans <p> <b>的任何一个bean定义或{@code getObjectType}的值判断注意：此方法仅仅介绍顶级bean </b>
	 * 它不</i>检查可能与指定类型匹配的嵌套bean <p>考虑由FactoryBeans创建的对象,这意味着FactoryBeans将被初始化如果FactoryBean创建的对象不不匹配,原始Factor
	 * yBean本身将匹配类型<p>不考虑任何层次结构,此工厂可能参与使用BeanFactoryUtils'{@code beansOfTypeIncludingAncestors}在祖先工厂中包括bean<p>
	 * 注意：<i>不</i>忽略已经通过其他方式注册的单例bean而不是bean定义<p>此版本的getBeansOfType匹配所有类型的bean,无论是单例,原型还是FactoryBean大多数实现结果将
	 * 与{@code getBeansOfType(type,true,true))的结果相同} <p>此方法返回的Map应始终按照定义的顺序返回bean名称和对应的bean实例<i>我在后端配置中尽可能的。
	 * 
	 * 
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 1.1.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	<T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * <p>
	 * 返回与给定对象类型(包括子类)匹配的bean实例,从FactoryBeans <p> <b>的任何一个bean定义或{@code getObjectType}的值判断注意：此方法仅仅介绍顶级bean </b>
	 * 它不</i>检查可能与指定类型匹配的嵌套bean <p>如果设置了"allowEagerInit"标志,则会考虑FactoryBeans创建的对象,这意味着FactoryBeans将被初始化如果Fact
	 * oryBean创建的对象不匹配,则原始FactoryBean本身将匹配类型如果未设置"allowEagerInit",则只会检查原始FactoryBean(不需要初始化每个FactoryBean)<p>
	 * 不考虑这个工厂可能参与的任何层次结构使用BeanFactoryUtils'{@code beansOfTypeIncludingAncestors}在祖先工厂中包含bean <p>注意：<i>不</i>
	 * 忽略通过其他方式而不是bean定义注册的单例bean <p>由此返回的Map方法应始终以后端配置中定义</i>的顺序返回bean名称和相应的bean实例<i>,尽可能。
	 * 
	 * 
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	<T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	/**
	 * Find all names of beans whose {@code Class} has the supplied {@link Annotation}
	 * type, without creating any bean instances yet.
	 * <p>
	 * 找到{@code Class}具有提供的{@link Annotation}类型的所有bean的名称,而不创建任何bean实例
	 * 
	 * 
	 * @param annotationType the type of annotation to look for
	 * @return the names of all matching beans
	 * @since 4.0
	 */
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	/**
	 * Find all beans whose {@code Class} has the supplied {@link Annotation} type,
	 * returning a Map of bean names with corresponding bean instances.
	 * <p>
	 *  找到所有的{@code Class}的bean都有提供的{@link Annotation}类型的bean,返回一个bean对应的bean实例的映射
	 * 
	 * 
	 * @param annotationType the type of annotation to look for
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 3.0
	 */
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * Find an {@link Annotation} of {@code annotationType} on the specified
	 * bean, traversing its interfaces and super classes if no annotation can be
	 * found on the given class itself.
	 * <p>
	 *  在指定的bean上查找{@code annotationType}的{@link注释},遍历其接口和超类,如果在给定的类本身没有找到注释
	 * 
	 * @param beanName the name of the bean to look for annotations on
	 * @param annotationType the annotation class to look for
	 * @return the annotation of the given type if found, or {@code null}
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 3.0
	 */
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

}
