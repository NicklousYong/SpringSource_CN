/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Convenience methods operating on bean factories, in particular
 * on the {@link ListableBeanFactory} interface.
 *
 * <p>Returns bean counts, bean names or bean instances,
 * taking into account the nesting hierarchy of a bean factory
 * (which the methods defined on the ListableBeanFactory interface don't,
 * in contrast to the methods defined on the BeanFactory interface).
 *
 * <p>
 *  在bean工厂上运行的方便方法,特别是在{@link ListableBeanFactory}界面上
 * 
 * 考虑到bean工厂的嵌套层次结构(与ListableBeanFactory接口中定义的方法相比,BeanFactory接口上定义的方法不同),返回bean计数,​​bean名称或bean实例。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 04.07.2003
 */
public abstract class BeanFactoryUtils {

	/**
	 * Separator for generated bean names. If a class name or parent name is not
	 * unique, "#1", "#2" etc will be appended, until the name becomes unique.
	 * <p>
	 *  生成的bean名称的分隔符如果类名或父名称不唯一,则将附加"#1","#2"等,直到名称变为唯一
	 * 
	 */
	public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";


	/**
	 * Return whether the given name is a factory dereference
	 * (beginning with the factory dereference prefix).
	 * <p>
	 *  返回给定的名称是否是工厂取消引用(从出厂设置引用前缀开头)
	 * 
	 * 
	 * @param name the name of the bean
	 * @return whether the given name is a factory dereference
	 * @see BeanFactory#FACTORY_BEAN_PREFIX
	 */
	public static boolean isFactoryDereference(String name) {
		return (name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
	}

	/**
	 * Return the actual bean name, stripping out the factory dereference
	 * prefix (if any, also stripping repeated factory prefixes if found).
	 * <p>
	 *  返回实际的bean名称,剥离出厂前的引用前缀(如果有的话,也可以在重复的工厂前缀中找到)
	 * 
	 * 
	 * @param name the name of the bean
	 * @return the transformed name
	 * @see BeanFactory#FACTORY_BEAN_PREFIX
	 */
	public static String transformedBeanName(String name) {
		Assert.notNull(name, "'name' must not be null");
		String beanName = name;
		while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
			beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
		}
		return beanName;
	}

	/**
	 * Return whether the given name is a bean name which has been generated
	 * by the default naming strategy (containing a "#..." part).
	 * <p>
	 *  返回给定的名称是否是由默认命名策略生成的bean名称(包含"#"部分)
	 * 
	 * 
	 * @param name the name of the bean
	 * @return whether the given name is a generated bean name
	 * @see #GENERATED_BEAN_NAME_SEPARATOR
	 * @see org.springframework.beans.factory.support.BeanDefinitionReaderUtils#generateBeanName
	 * @see org.springframework.beans.factory.support.DefaultBeanNameGenerator
	 */
	public static boolean isGeneratedBeanName(String name) {
		return (name != null && name.contains(GENERATED_BEAN_NAME_SEPARATOR));
	}

	/**
	 * Extract the "raw" bean name from the given (potentially generated) bean name,
	 * excluding any "#..." suffixes which might have been added for uniqueness.
	 * <p>
	 * 从给定(可能生成的)bean名称中提取"原始"bean名称,不包括可能已添加为唯一性的任何"#"后缀
	 * 
	 * 
	 * @param name the potentially generated bean name
	 * @return the raw bean name
	 * @see #GENERATED_BEAN_NAME_SEPARATOR
	 */
	public static String originalBeanName(String name) {
		Assert.notNull(name, "'name' must not be null");
		int separatorIndex = name.indexOf(GENERATED_BEAN_NAME_SEPARATOR);
		return (separatorIndex != -1 ? name.substring(0, separatorIndex) : name);
	}


	/**
	 * Count all beans in any hierarchy in which this factory participates.
	 * Includes counts of ancestor bean factories.
	 * <p>Beans that are "overridden" (specified in a descendant factory
	 * with the same name) are only counted once.
	 * <p>
	 *  计算此工厂参与的任何层次结构中的所有bean包括祖先bean工厂的数量<p>"覆盖"(在具有相同名称的后代工厂中指定)的豆只计数一次
	 * 
	 * 
	 * @param lbf the bean factory
	 * @return count of beans including those defined in ancestor factories
	 */
	public static int countBeansIncludingAncestors(ListableBeanFactory lbf) {
		return beanNamesIncludingAncestors(lbf).length;
	}

	/**
	 * Return all bean names in the factory, including ancestor factories.
	 * <p>
	 *  返回工厂中的所有bean名称,包括祖先工厂
	 * 
	 * 
	 * @param lbf the bean factory
	 * @return the array of matching bean names, or an empty array if none
	 * @see #beanNamesForTypeIncludingAncestors
	 */
	public static String[] beanNamesIncludingAncestors(ListableBeanFactory lbf) {
		return beanNamesForTypeIncludingAncestors(lbf, Object.class);
	}

	/**
	 * Get all bean names for the given type, including those defined in ancestor
	 * factories. Will return unique names in case of overridden bean definitions.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>This version of {@code beanNamesForTypeIncludingAncestors} automatically
	 * includes prototypes and FactoryBeans.
	 * <p>
	 * 获取给定类型的所有bean名称,包括在祖先工厂中定义的所有bean名称将在覆盖的bean定义时返回唯一的名称<p>考虑由FactoryBeans创建的对象,这意味着FactoryBeans将被初始化如果
	 * FactoryBean创建的对象不不匹配,原始FactoryBean本身将匹配类型<p>此版本的{@code beanNamesForTypeIncludingAncestors}自动包含原型和Fact
	 * oryBeans。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param type the type that beans must match (as a {@code ResolvableType})
	 * @return the array of matching bean names, or an empty array if none
	 * @since 4.2
	 */
	public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, ResolvableType type) {
		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		String[] result = lbf.getBeanNamesForType(type);
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				String[] parentResult = beanNamesForTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type);
				List<String> resultList = new ArrayList<String>();
				resultList.addAll(Arrays.asList(result));
				for (String beanName : parentResult) {
					if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
						resultList.add(beanName);
					}
				}
				result = StringUtils.toStringArray(resultList);
			}
		}
		return result;
	}

	/**
	 * Get all bean names for the given type, including those defined in ancestor
	 * factories. Will return unique names in case of overridden bean definitions.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>This version of {@code beanNamesForTypeIncludingAncestors} automatically
	 * includes prototypes and FactoryBeans.
	 * <p>
	 * 获取给定类型的所有bean名称,包括在祖先工厂中定义的所有bean名称将在覆盖的bean定义时返回唯一的名称<p>考虑由FactoryBeans创建的对象,这意味着FactoryBeans将被初始化如果
	 * FactoryBean创建的对象不不匹配,原始FactoryBean本身将匹配类型<p>此版本的{@code beanNamesForTypeIncludingAncestors}自动包含原型和Fact
	 * oryBeans。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param type the type that beans must match (as a {@code Class})
	 * @return the array of matching bean names, or an empty array if none
	 */
	public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type) {
		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		String[] result = lbf.getBeanNamesForType(type);
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				String[] parentResult = beanNamesForTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type);
				List<String> resultList = new ArrayList<String>();
				resultList.addAll(Arrays.asList(result));
				for (String beanName : parentResult) {
					if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
						resultList.add(beanName);
					}
				}
				result = StringUtils.toStringArray(resultList);
			}
		}
		return result;
	}

	/**
	 * Get all bean names for the given type, including those defined in ancestor
	 * factories. Will return unique names in case of overridden bean definitions.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit"
	 * flag is set, which means that FactoryBeans will get initialized. If the
	 * object created by the FactoryBean doesn't match, the raw FactoryBean itself
	 * will be matched against the type. If "allowEagerInit" is not set,
	 * only raw FactoryBeans will be checked (which doesn't require initialization
	 * of each FactoryBean).
	 * <p>
	 * 获取给定类型的所有bean名称,包括在祖先工厂中定义的所有bean名称将在覆盖的bean定义时返回唯一的名称<p>如果设置了"allowEagerInit"标志,则会考虑FactoryBeans创建的对
	 * 象,这意味着FactoryBeans将被初始化如果FactoryBean创建的对象不匹配,则原始FactoryBean本身将与类型匹配。
	 * 如果未设置"allowEagerInit",则只会检查原始FactoryBeans(不需要初始化每个FactoryBean)。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @param type the type that beans must match
	 * @return the array of matching bean names, or an empty array if none
	 */
	public static String[] beanNamesForTypeIncludingAncestors(
			ListableBeanFactory lbf, Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				String[] parentResult = beanNamesForTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
				List<String> resultList = new ArrayList<String>();
				resultList.addAll(Arrays.asList(result));
				for (String beanName : parentResult) {
					if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
						resultList.add(beanName);
					}
				}
				result = StringUtils.toStringArray(resultList);
			}
		}
		return result;
	}

	/**
	 * Return all beans of the given type or subtypes, also picking up beans defined in
	 * ancestor bean factories if the current bean factory is a HierarchicalBeanFactory.
	 * The returned Map will only contain beans of this type.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p><b>Note: Beans of the same name will take precedence at the 'lowest' factory level,
	 * i.e. such beans will be returned from the lowest factory that they are being found in,
	 * hiding corresponding beans in ancestor factories.</b> This feature allows for
	 * 'replacing' beans by explicitly choosing the same bean name in a child factory;
	 * the bean in the ancestor factory won't be visible then, not even for by-type lookups.
	 * <p>
	 * 返回给定类型或子类型的所有bean,如果当前bean工厂是HierarchicalBeanFactory,那么也可以在祖先bean工厂中定义bean。
	 * 返回的Map将只包含这种类型的bean <p>请考虑由FactoryBeans创建的对象,这意味着FactoryBeans将被初始化如果FactoryBean创建的对象不匹配,则原始FactoryBea
	 * n本身将匹配类型<p> <b>注意：同名的Bean将优先于"最低"工厂级别,即这些豆类将从被发现的最低工厂返回,在祖先的工厂中隐藏相应的豆类</b>此功能允许通过在子工厂中显式选择相同的bean名称"替
	 * 换"bean;祖先工厂中的bean将不可见,甚至不是用于逐个查找。
	 * 返回给定类型或子类型的所有bean,如果当前bean工厂是HierarchicalBeanFactory,那么也可以在祖先bean工厂中定义bean。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @return the Map of matching bean instances, or an empty Map if none
	 * @throws BeansException if a bean could not be created
	 */
	public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type)
			throws BeansException {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> result = new LinkedHashMap<String, T>(4);
		result.putAll(lbf.getBeansOfType(type));
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				Map<String, T> parentResult = beansOfTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type);
				for (Map.Entry<String, T> entry : parentResult.entrySet()) {
					String beanName = entry.getKey();
					if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
						result.put(beanName, entry.getValue());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Return all beans of the given type or subtypes, also picking up beans defined in
	 * ancestor bean factories if the current bean factory is a HierarchicalBeanFactory.
	 * The returned Map will only contain beans of this type.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p><b>Note: Beans of the same name will take precedence at the 'lowest' factory level,
	 * i.e. such beans will be returned from the lowest factory that they are being found in,
	 * hiding corresponding beans in ancestor factories.</b> This feature allows for
	 * 'replacing' beans by explicitly choosing the same bean name in a child factory;
	 * the bean in the ancestor factory won't be visible then, not even for by-type lookups.
	 * <p>
	 * 返回给定类型或子类型的所有bean,如果当前的bean工厂是HierarchicalBeanFactory,那么也可以拾取在祖代bean工厂中定义的bean。
	 * 返回的Map将只包含这种类型的bean <p>如果使用"allowEagerInit",则考虑FactoryBeans创建的对象"flag被设置,这意味着FactoryBeans将被初始化如果Facto
	 * ryBean创建的对象不匹配,则原始FactoryBean本身将与类型匹配。
	 * 返回给定类型或子类型的所有bean,如果当前的bean工厂是HierarchicalBeanFactory,那么也可以拾取在祖代bean工厂中定义的bean。
	 * 如果未设置"allowEagerInit",则只会检查原始FactoryBeans(其中不需要初始化每个FactoryBean)<p> <b>注意：同名的豆将优先于"最低"工厂级,即这样的bean将从被
	 * 发现的最低工厂返回,在祖先工厂中隐藏相应的bean </b>此功能允许通过在子工厂中显式选择相同的bean名称来"替换"bean;祖先工厂中的bean将不可见,甚至不是用于逐个查找。
	 * 返回给定类型或子类型的所有bean,如果当前的bean工厂是HierarchicalBeanFactory,那么也可以拾取在祖代bean工厂中定义的bean。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the Map of matching bean instances, or an empty Map if none
	 * @throws BeansException if a bean could not be created
	 */
	public static <T> Map<String, T> beansOfTypeIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> result = new LinkedHashMap<String, T>(4);
		result.putAll(lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit));
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				Map<String, T> parentResult = beansOfTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
				for (Map.Entry<String, T> entry : parentResult.entrySet()) {
					String beanName = entry.getKey();
					if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
						result.put(beanName, entry.getValue());
					}
				}
			}
		}
		return result;
	}


	/**
	 * Return a single bean of the given type or subtypes, also picking up beans
	 * defined in ancestor bean factories if the current bean factory is a
	 * HierarchicalBeanFactory. Useful convenience method when we expect a
	 * single bean and don't care about the bean name.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>This version of {@code beanOfTypeIncludingAncestors} automatically includes
	 * prototypes and FactoryBeans.
	 * <p><b>Note: Beans of the same name will take precedence at the 'lowest' factory level,
	 * i.e. such beans will be returned from the lowest factory that they are being found in,
	 * hiding corresponding beans in ancestor factories.</b> This feature allows for
	 * 'replacing' beans by explicitly choosing the same bean name in a child factory;
	 * the bean in the ancestor factory won't be visible then, not even for by-type lookups.
	 * <p>
	 * 返回给定类型或子类型的单个bean,如果当前bean工厂是HierarchicalBeanFactory,那么在祖先bean工厂中定义的bean也会被拾取。
	 * 当我们期望单个bean并且不关心bean名称时,有用的方便方法<p>考虑由FactoryBeans创建的对象,这意味着FactoryBeans将被初始化如果FactoryBean创建的对象不匹配,则原始
	 * FactoryBean本身将与类型<p>匹配。
	 * 返回给定类型或子类型的单个bean,如果当前bean工厂是HierarchicalBeanFactory,那么在祖先bean工厂中定义的bean也会被拾取。
	 * 此版本的{@code beanOfTypeIncludingAncestors}自动包含原型和FactoryBeans <p> <b>注意：相同名称的豆将优先于"最低"工厂级,即这样的bean将从被发现
	 * 的最低工厂返回,在祖先工厂中隐藏相应的bean </b>此功能允许通过在子工厂中显式选择相同的bean名称来"替换"bean;祖先工厂中的bean将不可见,甚至不是用于逐个查找。
	 * 返回给定类型或子类型的单个bean,如果当前bean工厂是HierarchicalBeanFactory,那么在祖先bean工厂中定义的bean也会被拾取。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @return the matching bean instance
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 */
	public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type)
			throws BeansException {

		Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type);
		return uniqueBean(type, beansOfType);
	}

	/**
	 * Return a single bean of the given type or subtypes, also picking up beans
	 * defined in ancestor bean factories if the current bean factory is a
	 * HierarchicalBeanFactory. Useful convenience method when we expect a
	 * single bean and don't care about the bean name.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p><b>Note: Beans of the same name will take precedence at the 'lowest' factory level,
	 * i.e. such beans will be returned from the lowest factory that they are being found in,
	 * hiding corresponding beans in ancestor factories.</b> This feature allows for
	 * 'replacing' beans by explicitly choosing the same bean name in a child factory;
	 * the bean in the ancestor factory won't be visible then, not even for by-type lookups.
	 * <p>
	 * 返回给定类型或子类型的单个bean,如果当前bean工厂是HierarchicalBeanFactory,那么在祖先bean工厂中定义的bean也会被拾取。
	 * 当我们期望单个bean并且不关心bean名称时,有用的方便方法<p>如果设置了"allowEagerInit"标志,则可以考虑FactoryBeans创建的对象,这意味着FactoryBeans将被初始
	 * 化如果FactoryBean创建的对象不匹配,则原始FactoryBean本身将匹配类型如果"allowEagerInit"未设置将只检查原始的FactoryBean(不需要初始化每个FactoryBe
	 * an)<p> <b>注意：同名的Bean将优先于"最低"工厂级,即这样的bean将从被发现的最低工厂返回,在祖先工厂中隐藏相应的bean </b>此功能允许通过在子工厂中显式选择相同的bean名称来"替
	 * 换"bean;祖先工厂中的bean将不可见,甚至不是用于逐个查找。
	 * 返回给定类型或子类型的单个bean,如果当前bean工厂是HierarchicalBeanFactory,那么在祖先bean工厂中定义的bean也会被拾取。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the matching bean instance
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 */
	public static <T> T beanOfTypeIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type, includeNonSingletons, allowEagerInit);
		return uniqueBean(type, beansOfType);
	}

	/**
	 * Return a single bean of the given type or subtypes, not looking in ancestor
	 * factories. Useful convenience method when we expect a single bean and
	 * don't care about the bean name.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>This version of {@code beanOfType} automatically includes
	 * prototypes and FactoryBeans.
	 * <p>
	 * 返回给定类型或子类型的单个bean,而不是在祖先工厂中查找当我们期望单个bean并且不关心bean名称时有用的便利方法<p>考虑由FactoryBeans创建的对象,这意味着FactoryBeans将获
	 * 得初始化如果FactoryBean创建的对象不匹配,则原始FactoryBean本身将与类型<p>匹配。
	 * 此版本的{@code beanOfType}自动包含原型和FactoryBean。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @return the matching bean instance
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 */
	public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type) throws BeansException {
		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> beansOfType = lbf.getBeansOfType(type);
		return uniqueBean(type, beansOfType);
	}

	/**
	 * Return a single bean of the given type or subtypes, not looking in ancestor
	 * factories. Useful convenience method when we expect a single bean and
	 * don't care about the bean name.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit"
	 * flag is set, which means that FactoryBeans will get initialized. If the
	 * object created by the FactoryBean doesn't match, the raw FactoryBean itself
	 * will be matched against the type. If "allowEagerInit" is not set,
	 * only raw FactoryBeans will be checked (which doesn't require initialization
	 * of each FactoryBean).
	 * <p>
	 * 返回给定类型或子类型的单个bean,而不是在祖先工厂中查找当我们期望单个bean并且不关心bean名称时有用的便利方法<p>如果"allowEagerInit"标志为"allowEagerInit"标志
	 * ,则考虑由FactoryBeans创建的对象set,这意味着FactoryBeans将被初始化如果FactoryBean创建的对象不匹配,则原始FactoryBean本身将与类型匹配。
	 * 如果未设置"allowEagerInit",则只会检查原始FactoryBeans(不会检查需要初始化每个FactoryBean)。
	 * 
	 * 
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the matching bean instance
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 */
	public static <T> T beanOfType(
			ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
		return uniqueBean(type, beansOfType);
	}

	/**
	 * Extract a unique bean for the given type from the given Map of matching beans.
	 * <p>
	 * 
	 * @param type type of bean to match
	 * @param matchingBeans all matching beans found
	 * @return the unique bean instance
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 */
	private static <T> T uniqueBean(Class<T> type, Map<String, T> matchingBeans) {
		int nrFound = matchingBeans.size();
		if (nrFound == 1) {
			return matchingBeans.values().iterator().next();
		}
		else if (nrFound > 1) {
			throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
		}
		else {
			throw new NoSuchBeanDefinitionException(type);
		}
	}

}
