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

package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Base class for concrete, full-fledged {@link BeanDefinition} classes,
 * factoring out common properties of {@link GenericBeanDefinition},
 * {@link RootBeanDefinition}, and {@link ChildBeanDefinition}.
 *
 * <p>The autowire constants match the ones defined in the
 * {@link org.springframework.beans.factory.config.AutowireCapableBeanFactory}
 * interface.
 *
 * <p>
 *  基类具有完整的{@link BeanDefinition}类,分为{@link GenericBeanDefinition},{@link RootBeanDefinition}和{@link ChildBeanDefinition}
 * 的常见属性。
 * 
 * <p> autowire常量与{@link orgspringframeworkbeansfactoryconfigAutowireCapableBeanFactory}界面中定义的常量相匹配
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Mark Fisher
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 */
@SuppressWarnings("serial")
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor
		implements BeanDefinition, Cloneable {

	/**
	 * Constant for the default scope name: {@code ""}, equivalent to singleton
	 * status unless overridden from a parent bean definition (if applicable).
	 * <p>
	 *  默认范围名称的常量：{@code""},相当于单例状态,除非从父bean定义(如果适用)覆盖
	 * 
	 */
	public static final String SCOPE_DEFAULT = "";

	/**
	 * Constant that indicates no autowiring at all.
	 * <p>
	 *  常数,表示没有自动连线
	 * 
	 * 
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;

	/**
	 * Constant that indicates autowiring bean properties by name.
	 * <p>
	 *  常数表示按名称自动连线bean属性
	 * 
	 * 
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	/**
	 * Constant that indicates autowiring bean properties by type.
	 * <p>
	 *  指示类型自动连接bean属性的常数
	 * 
	 * 
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

	/**
	 * Constant that indicates autowiring a constructor.
	 * <p>
	 *  表示自动装配构造函数的常数
	 * 
	 * 
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

	/**
	 * Constant that indicates determining an appropriate autowire strategy
	 * through introspection of the bean class.
	 * <p>
	 *  常数,表示通过内省bean类确定适当的自动线路策略
	 * 
	 * 
	 * @see #setAutowireMode
	 * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
	 * use annotation-based autowiring for clearer demarcation of autowiring needs.
	 */
	@Deprecated
	public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;

	/**
	 * Constant that indicates no dependency check at all.
	 * <p>
	 *  表示根本没有依赖关系的常量
	 * 
	 * 
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_NONE = 0;

	/**
	 * Constant that indicates dependency checking for object references.
	 * <p>
	 *  指示对象引用的依赖性检查的常量
	 * 
	 * 
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_OBJECTS = 1;

	/**
	 * Constant that indicates dependency checking for "simple" properties.
	 * <p>
	 * 指示"简单"属性的依赖性检查的常量
	 * 
	 * 
	 * @see #setDependencyCheck
	 * @see org.springframework.beans.BeanUtils#isSimpleProperty
	 */
	public static final int DEPENDENCY_CHECK_SIMPLE = 2;

	/**
	 * Constant that indicates dependency checking for all properties
	 * (object references as well as "simple" properties).
	 * <p>
	 *  指示所有属性(对象引用以及"简单"属性)的依赖关系检查的常量
	 * 
	 * 
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_ALL = 3;

	/**
	 * Constant that indicates the container should attempt to infer the
	 * {@link #setDestroyMethodName destroy method name} for a bean as opposed to
	 * explicit specification of a method name. The value {@value} is specifically
	 * designed to include characters otherwise illegal in a method name, ensuring
	 * no possibility of collisions with legitimately named methods having the same
	 * name.
	 * <p>Currently, the method names detected during destroy method inference
	 * are "close" and "shutdown", if present on the specific bean class.
	 * <p>
	 *  指示容器的常量应尝试推断一个bean的{@link #setDestroyMethodName destroy method name},而不是显式指定方法名称{@value}值专门用于在方法名称中包
	 * 含非法的字符,确保没有与具有相同名称的合法命名方法冲突的可能性。
	 * 目前,在destroy方法推断中检测到的方法名称是"close"和"shutdown",如果存在于特定的bean类上。
	 * 
	 */
	public static final String INFER_METHOD = "(inferred)";


	private volatile Object beanClass;

	private String scope = SCOPE_DEFAULT;

	private boolean abstractFlag = false;

	private boolean lazyInit = false;

	private int autowireMode = AUTOWIRE_NO;

	private int dependencyCheck = DEPENDENCY_CHECK_NONE;

	private String[] dependsOn;

	private boolean autowireCandidate = true;

	private boolean primary = false;

	private final Map<String, AutowireCandidateQualifier> qualifiers =
			new LinkedHashMap<String, AutowireCandidateQualifier>(0);

	private boolean nonPublicAccessAllowed = true;

	private boolean lenientConstructorResolution = true;

	private ConstructorArgumentValues constructorArgumentValues;

	private MutablePropertyValues propertyValues;

	private MethodOverrides methodOverrides = new MethodOverrides();

	private String factoryBeanName;

	private String factoryMethodName;

	private String initMethodName;

	private String destroyMethodName;

	private boolean enforceInitMethod = true;

	private boolean enforceDestroyMethod = true;

	private boolean synthetic = false;

	private int role = BeanDefinition.ROLE_APPLICATION;

	private String description;

	private Resource resource;


	/**
	 * Create a new AbstractBeanDefinition with default settings.
	 * <p>
	 *  使用默认设置创建一个新的AbstractBeanDefinition
	 * 
	 */
	protected AbstractBeanDefinition() {
		this(null, null);
	}

	/**
	 * Create a new AbstractBeanDefinition with the given
	 * constructor argument values and property values.
	 * <p>
	 * 使用给定的构造函数参数值和属性值创建一个新的AbstractBeanDefinition
	 * 
	 */
	protected AbstractBeanDefinition(ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		setConstructorArgumentValues(cargs);
		setPropertyValues(pvs);
	}

	/**
	 * Create a new AbstractBeanDefinition as a deep copy of the given
	 * bean definition.
	 * <p>
	 *  创建一个新的AbstractBeanDefinition作为给定bean定义的深层副本
	 * 
	 * 
	 * @param original the original bean definition to copy from
	 */
	protected AbstractBeanDefinition(BeanDefinition original) {
		setParentName(original.getParentName());
		setBeanClassName(original.getBeanClassName());
		setFactoryBeanName(original.getFactoryBeanName());
		setFactoryMethodName(original.getFactoryMethodName());
		setScope(original.getScope());
		setAbstract(original.isAbstract());
		setLazyInit(original.isLazyInit());
		setRole(original.getRole());
		setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
		setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
		setSource(original.getSource());
		copyAttributesFrom(original);

		if (original instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition originalAbd = (AbstractBeanDefinition) original;
			if (originalAbd.hasBeanClass()) {
				setBeanClass(originalAbd.getBeanClass());
			}
			setAutowireMode(originalAbd.getAutowireMode());
			setDependencyCheck(originalAbd.getDependencyCheck());
			setDependsOn(originalAbd.getDependsOn());
			setAutowireCandidate(originalAbd.isAutowireCandidate());
			copyQualifiersFrom(originalAbd);
			setPrimary(originalAbd.isPrimary());
			setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
			setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
			setInitMethodName(originalAbd.getInitMethodName());
			setEnforceInitMethod(originalAbd.isEnforceInitMethod());
			setDestroyMethodName(originalAbd.getDestroyMethodName());
			setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
			setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
			setSynthetic(originalAbd.isSynthetic());
			setResource(originalAbd.getResource());
		}
		else {
			setResourceDescription(original.getResourceDescription());
		}
	}


	/**
	 * Override settings in this bean definition (presumably a copied parent
	 * from a parent-child inheritance relationship) from the given bean
	 * definition (presumably the child).
	 * <ul>
	 * <li>Will override beanClass if specified in the given bean definition.
	 * <li>Will always take {@code abstract}, {@code scope},
	 * {@code lazyInit}, {@code autowireMode}, {@code dependencyCheck},
	 * and {@code dependsOn} from the given bean definition.
	 * <li>Will add {@code constructorArgumentValues}, {@code propertyValues},
	 * {@code methodOverrides} from the given bean definition to existing ones.
	 * <li>Will override {@code factoryBeanName}, {@code factoryMethodName},
	 * {@code initMethodName}, and {@code destroyMethodName} if specified
	 * in the given bean definition.
	 * </ul>
	 * <p>
	 *  从给定的bean定义(大概是孩子)覆盖此bean定义(可能是父 - 子继承关系中的复制父项)中的设置
	 * <ul>
	 * <li>如果在给定的bean定义中指定,将覆盖beanClass <li>将始终采用{@code abstract},{@code scope},{@code lazyInit},{@code autowireMode}
	 * ,{@code dependencyCheck}和{@code dependsOn}从给定的bean定义<li>将添加{@code constructorArgumentValues},{@code propertyValues}
	 * ,{@code methodOverrides}从给定的bean定义到现有的<li>将覆盖{@code factoryBeanName} ,{@code factoryMethodName},{@code initMethodName}
	 * 和{@code destroyMethodName}如果在给定的bean定义中指定。
	 * </ul>
	 */
	public void overrideFrom(BeanDefinition other) {
		if (StringUtils.hasLength(other.getBeanClassName())) {
			setBeanClassName(other.getBeanClassName());
		}
		if (StringUtils.hasLength(other.getFactoryBeanName())) {
			setFactoryBeanName(other.getFactoryBeanName());
		}
		if (StringUtils.hasLength(other.getFactoryMethodName())) {
			setFactoryMethodName(other.getFactoryMethodName());
		}
		if (StringUtils.hasLength(other.getScope())) {
			setScope(other.getScope());
		}
		setAbstract(other.isAbstract());
		setLazyInit(other.isLazyInit());
		setRole(other.getRole());
		getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
		getPropertyValues().addPropertyValues(other.getPropertyValues());
		setSource(other.getSource());
		copyAttributesFrom(other);

		if (other instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition otherAbd = (AbstractBeanDefinition) other;
			if (otherAbd.hasBeanClass()) {
				setBeanClass(otherAbd.getBeanClass());
			}
			setAutowireCandidate(otherAbd.isAutowireCandidate());
			setAutowireMode(otherAbd.getAutowireMode());
			copyQualifiersFrom(otherAbd);
			setPrimary(otherAbd.isPrimary());
			setDependencyCheck(otherAbd.getDependencyCheck());
			setDependsOn(otherAbd.getDependsOn());
			setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
			setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
			if (StringUtils.hasLength(otherAbd.getInitMethodName())) {
				setInitMethodName(otherAbd.getInitMethodName());
				setEnforceInitMethod(otherAbd.isEnforceInitMethod());
			}
			if (otherAbd.getDestroyMethodName() != null) {
				setDestroyMethodName(otherAbd.getDestroyMethodName());
				setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
			}
			getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
			setSynthetic(otherAbd.isSynthetic());
			setResource(otherAbd.getResource());
		}
		else {
			setResourceDescription(other.getResourceDescription());
		}
	}

	/**
	 * Apply the provided default values to this bean.
	 * <p>
	 *  将提供的默认值应用于此bean
	 * 
	 * 
	 * @param defaults the defaults to apply
	 */
	public void applyDefaults(BeanDefinitionDefaults defaults) {
		setLazyInit(defaults.isLazyInit());
		setAutowireMode(defaults.getAutowireMode());
		setDependencyCheck(defaults.getDependencyCheck());
		setInitMethodName(defaults.getInitMethodName());
		setEnforceInitMethod(false);
		setDestroyMethodName(defaults.getDestroyMethodName());
		setEnforceDestroyMethod(false);
	}


	/**
	 * Return whether this definition specifies a bean class.
	 * <p>
	 *  返回此定义是否指定了一个bean类
	 * 
	 */
	public boolean hasBeanClass() {
		return (this.beanClass instanceof Class);
	}

	/**
	 * Specify the class for this bean.
	 * <p>
	 *  指定此bean的类
	 * 
	 */
	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Return the class of the wrapped bean, if already resolved.
	 * <p>
	 *  如果已经解决了返回包装bean的类
	 * 
	 * 
	 * @return the bean class, or {@code null} if none defined
	 * @throws IllegalStateException if the bean definition does not define a bean class,
	 * or a specified bean class name has not been resolved into an actual Class
	 */
	public Class<?> getBeanClass() throws IllegalStateException {
		Object beanClassObject = this.beanClass;
		if (beanClassObject == null) {
			throw new IllegalStateException("No bean class specified on bean definition");
		}
		if (!(beanClassObject instanceof Class)) {
			throw new IllegalStateException(
					"Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
		}
		return (Class<?>) beanClassObject;
	}

	@Override
	public void setBeanClassName(String beanClassName) {
		this.beanClass = beanClassName;
	}

	@Override
	public String getBeanClassName() {
		Object beanClassObject = this.beanClass;
		if (beanClassObject instanceof Class) {
			return ((Class<?>) beanClassObject).getName();
		}
		else {
			return (String) beanClassObject;
		}
	}

	/**
	 * Determine the class of the wrapped bean, resolving it from a
	 * specified class name if necessary. Will also reload a specified
	 * Class from its name when called with the bean class already resolved.
	 * <p>
	 * 确定包装的bean的类,如果需要,从指定的类名解析它。在使用已经解析的bean类调用时,也会从其名称重新加载指定的类
	 * 
	 * 
	 * @param classLoader the ClassLoader to use for resolving a (potential) class name
	 * @return the resolved bean class
	 * @throws ClassNotFoundException if the class name could be resolved
	 */
	public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
		String className = getBeanClassName();
		if (className == null) {
			return null;
		}
		Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
		this.beanClass = resolvedClass;
		return resolvedClass;
	}


	/**
	 * Set the name of the target scope for the bean.
	 * <p>The default is singleton status, although this is only applied once
	 * a bean definition becomes active in the containing factory. A bean
	 * definition may eventually inherit its scope from a parent bean definition.
	 * For this reason, the default scope name is an empty string (i.e., {@code ""}),
	 * with singleton status being assumed until a resolved scope is set.
	 * <p>
	 *  设置bean的目标范围的名称<p>默认值为单例状态,尽管只有在bean定义在包含工厂中变为活动状态时才应用此bean定义可能最终从父bean定义继承其作用域因此,默认范围名称是一个空字符串(即{@code""}
	 * ),假定单身状态直到解析的范围被设置。
	 * 
	 * 
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	@Override
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Return the name of the target scope for the bean.
	 * <p>
	 *  返回bean的目标作用域的名称
	 * 
	 */
	@Override
	public String getScope() {
		return this.scope;
	}

	/**
	 * Return whether this a <b>Singleton</b>, with a single shared instance
	 * returned from all calls.
	 * <p>
	 *  返回这是一个<b> Singleton </b>,从所有调用返回单个共享实例
	 * 
	 * 
	 * @see #SCOPE_SINGLETON
	 */
	@Override
	public boolean isSingleton() {
		return SCOPE_SINGLETON.equals(scope) || SCOPE_DEFAULT.equals(scope);
	}

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance
	 * returned for each call.
	 * <p>
	 * 返回这是否为原型</b>,每个调用返回一个独立的实例
	 * 
	 * 
	 * @see #SCOPE_PROTOTYPE
	 */
	@Override
	public boolean isPrototype() {
		return SCOPE_PROTOTYPE.equals(scope);
	}

	/**
	 * Set if this bean is "abstract", i.e. not meant to be instantiated itself but
	 * rather just serving as parent for concrete child bean definitions.
	 * <p>Default is "false". Specify true to tell the bean factory to not try to
	 * instantiate that particular bean in any case.
	 * <p>
	 *  设置这个bean是否是"抽象的",即不是要自己实例化,而只是作为具体子bean定义的父级使用<p>默认值为"false"指定true以告知bean工厂不尝试实例化该特定bean任何状况之下
	 * 
	 */
	public void setAbstract(boolean abstractFlag) {
		this.abstractFlag = abstractFlag;
	}

	/**
	 * Return whether this bean is "abstract", i.e. not meant to be instantiated
	 * itself but rather just serving as parent for concrete child bean definitions.
	 * <p>
	 *  返回这个bean是否是"抽象的",即不是要自己实例化,而只是作为具体子bean定义的父级
	 * 
	 */
	@Override
	public boolean isAbstract() {
		return this.abstractFlag;
	}

	/**
	 * Set whether this bean should be lazily initialized.
	 * <p>If {@code false}, the bean will get instantiated on startup by bean
	 * factories that perform eager initialization of singletons.
	 * <p>
	 *  设置这个bean是否应该被延迟初始化<p>如果{@code false},bean将在启动时由Bean工厂实例化,这些bean工厂执行急速初始化单例
	 * 
	 */
	@Override
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	/**
	 * Return whether this bean should be lazily initialized, i.e. not
	 * eagerly instantiated on startup. Only applicable to a singleton bean.
	 * <p>
	 * 返回这个bean是否应该被懒惰地初始化,即在启动时不是急切的实例化只适用于单例bean
	 * 
	 */
	@Override
	public boolean isLazyInit() {
		return this.lazyInit;
	}


	/**
	 * Set the autowire mode. This determines whether any automagical detection
	 * and setting of bean references will happen. Default is AUTOWIRE_NO,
	 * which means there's no autowire.
	 * <p>
	 *  设置autowire模式这决定了bean引用的任何自动检测和设置是否发生默认为AUTOWIRE_NO,这意味着没有自动连线
	 * 
	 * 
	 * @param autowireMode the autowire mode to set.
	 * Must be one of the constants defined in this class.
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 */
	public void setAutowireMode(int autowireMode) {
		this.autowireMode = autowireMode;
	}

	/**
	 * Return the autowire mode as specified in the bean definition.
	 * <p>
	 *  返回在bean定义中指定的autowire模式
	 * 
	 */
	public int getAutowireMode() {
		return this.autowireMode;
	}

	/**
	 * Return the resolved autowire code,
	 * (resolving AUTOWIRE_AUTODETECT to AUTOWIRE_CONSTRUCTOR or AUTOWIRE_BY_TYPE).
	 * <p>
	 *  返回已解决的自动线代码(将AUTOWIRE_AUTODETECT解析为AUTOWIRE_CONSTRUCTOR或AUTOWIRE_BY_TYPE)
	 * 
	 * 
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_BY_TYPE
	 */
	public int getResolvedAutowireMode() {
		if (this.autowireMode == AUTOWIRE_AUTODETECT) {
			// Work out whether to apply setter autowiring or constructor autowiring.
			// If it has a no-arg constructor it's deemed to be setter autowiring,
			// otherwise we'll try constructor autowiring.
			Constructor<?>[] constructors = getBeanClass().getConstructors();
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					return AUTOWIRE_BY_TYPE;
				}
			}
			return AUTOWIRE_CONSTRUCTOR;
		}
		else {
			return this.autowireMode;
		}
	}

	/**
	 * Set the dependency check code.
	 * <p>
	 *  设置依赖关系检查码
	 * 
	 * 
	 * @param dependencyCheck the code to set.
	 * Must be one of the four constants defined in this class.
	 * @see #DEPENDENCY_CHECK_NONE
	 * @see #DEPENDENCY_CHECK_OBJECTS
	 * @see #DEPENDENCY_CHECK_SIMPLE
	 * @see #DEPENDENCY_CHECK_ALL
	 */
	public void setDependencyCheck(int dependencyCheck) {
		this.dependencyCheck = dependencyCheck;
	}

	/**
	 * Return the dependency check code.
	 * <p>
	 *  返回相关性检查代码
	 * 
	 */
	public int getDependencyCheck() {
		return this.dependencyCheck;
	}

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * The bean factory will guarantee that these beans get initialized first.
	 * <p>Note that dependencies are normally expressed through bean properties or
	 * constructor arguments. This property should just be necessary for other kinds
	 * of dependencies like statics (*ugh*) or database preparation on startup.
	 * <p>
	 * 设置bean依赖于初始化的bean的名称bean工厂将保证这些bean首先被初始化<p>请注意,依赖关系通常通过bean属性或构造函数来表示该属性应该是其他类型的依赖关系所必需的像静态(* ugh *)
	 * 或启动时的数据库准备。
	 * 
	 */
	@Override
	public void setDependsOn(String... dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * Return the bean names that this bean depends on.
	 * <p>
	 *  返回该bean依赖的bean名称
	 * 
	 */
	@Override
	public String[] getDependsOn() {
		return this.dependsOn;
	}

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * <p>
	 *  设置这个bean是否是自动连线到其他bean的候选项
	 * 
	 */
	@Override
	public void setAutowireCandidate(boolean autowireCandidate) {
		this.autowireCandidate = autowireCandidate;
	}

	/**
	 * Return whether this bean is a candidate for getting autowired into some other bean.
	 * <p>
	 *  返回这个bean是否是自动连线到其他bean的候选项
	 * 
	 */
	@Override
	public boolean isAutowireCandidate() {
		return this.autowireCandidate;
	}

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * <p>
	 *  设置这个bean是否是主要的自动线路候选者如果这个值对于多个匹配候选者中的一个bean是真实的,则它将用作连接器
	 * 
	 */
	@Override
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	/**
	 * Return whether this bean is a primary autowire candidate.
	 * If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * <p>
	 * 返回这个bean是否是主要的自动线路候选者如果这个值对于多个匹配候选者中的一个bean是真实的,它将用作一个连接器
	 * 
	 */
	@Override
	public boolean isPrimary() {
		return this.primary;
	}

	/**
	 * Register a qualifier to be used for autowire candidate resolution,
	 * keyed by the qualifier's type name.
	 * <p>
	 *  注册一个限定符,用于自动线路候选解决,由限定词的类型名称键入
	 * 
	 * 
	 * @see AutowireCandidateQualifier#getTypeName()
	 */
	public void addQualifier(AutowireCandidateQualifier qualifier) {
		this.qualifiers.put(qualifier.getTypeName(), qualifier);
	}

	/**
	 * Return whether this bean has the specified qualifier.
	 * <p>
	 *  返回此bean是否具有指定的限定符
	 * 
	 */
	public boolean hasQualifier(String typeName) {
		return this.qualifiers.keySet().contains(typeName);
	}

	/**
	 * Return the qualifier mapped to the provided type name.
	 * <p>
	 *  将限定符返回给提供的类型名称
	 * 
	 */
	public AutowireCandidateQualifier getQualifier(String typeName) {
		return this.qualifiers.get(typeName);
	}

	/**
	 * Return all registered qualifiers.
	 * <p>
	 *  返回所有注册的限定词
	 * 
	 * 
	 * @return the Set of {@link AutowireCandidateQualifier} objects.
	 */
	public Set<AutowireCandidateQualifier> getQualifiers() {
		return new LinkedHashSet<AutowireCandidateQualifier>(this.qualifiers.values());
	}

	/**
	 * Copy the qualifiers from the supplied AbstractBeanDefinition to this bean definition.
	 * <p>
	 *  将提供的AbstractBeanDefinition中的限定符复制到此bean定义
	 * 
	 * 
	 * @param source the AbstractBeanDefinition to copy from
	 */
	public void copyQualifiersFrom(AbstractBeanDefinition source) {
		Assert.notNull(source, "Source must not be null");
		this.qualifiers.putAll(source.qualifiers);
	}


	/**
	 * Specify whether to allow access to non-public constructors and methods,
	 * for the case of externalized metadata pointing to those. The default is
	 * {@code true}; switch this to {@code false} for public access only.
	 * <p>This applies to constructor resolution, factory method resolution,
	 * and also init/destroy methods. Bean property accessors have to be public
	 * in any case and are not affected by this setting.
	 * <p>Note that annotation-driven configuration will still access non-public
	 * members as far as they have been annotated. This setting applies to
	 * externalized metadata in this bean definition only.
	 * <p>
	 * 指定是否允许访问非公共构造函数和方法,对于外部化元数据指向的那些,默认为{@code true};将其切换到{@code false}仅供公开访问<p>这适用于构造函数解析,工厂方法解析以及init /
	 *  destroy方法Bean属性访问器在任何情况下都必须公开,并且不受此设置的影响<p >请注意,注释驱动的配置仍将访问非公开成员,只要它们已被注释。
	 * 此设置仅适用于此bean定义中的外部化元数据。
	 * 
	 */
	public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
		this.nonPublicAccessAllowed = nonPublicAccessAllowed;
	}

	/**
	 * Return whether to allow access to non-public constructors and methods.
	 * <p>
	 *  返回是否允许访问非公共构造函数和方法
	 * 
	 */
	public boolean isNonPublicAccessAllowed() {
		return this.nonPublicAccessAllowed;
	}

	/**
	 * Specify whether to resolve constructors in lenient mode ({@code true},
	 * which is the default) or to switch to strict resolution (throwing an exception
	 * in case of ambiguous constructors that all match when converting the arguments,
	 * whereas lenient mode would use the one with the 'closest' type matches).
	 * <p>
	 * 指定是否解析宽松模式下的构造函数({@code true},这是默认值)或切换到严格解析(如果在转换参数时都匹配的模糊构造函数抛出异常,而宽大模式将使用一个与"最接近"类型匹配)
	 * 
	 */
	public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
		this.lenientConstructorResolution = lenientConstructorResolution;
	}

	/**
	 * Return whether to resolve constructors in lenient mode or in strict mode.
	 * <p>
	 *  返回是否以宽松模式或严格模式解析构造函数
	 * 
	 */
	public boolean isLenientConstructorResolution() {
		return this.lenientConstructorResolution;
	}

	/**
	 * Specify constructor argument values for this bean.
	 * <p>
	 *  指定此bean的构造函数参数值
	 * 
	 */
	public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
		this.constructorArgumentValues =
				(constructorArgumentValues != null ? constructorArgumentValues : new ConstructorArgumentValues());
	}

	/**
	 * Return constructor argument values for this bean (never {@code null}).
	 * <p>
	 *  返回此bean的构造函数参数值(从不{@code null})
	 * 
	 */
	@Override
	public ConstructorArgumentValues getConstructorArgumentValues() {
		return this.constructorArgumentValues;
	}

	/**
	 * Return if there are constructor argument values defined for this bean.
	 * <p>
	 *  如果为此bean定义了构造函数参数值,则返回
	 * 
	 */
	public boolean hasConstructorArgumentValues() {
		return !this.constructorArgumentValues.isEmpty();
	}

	/**
	 * Specify property values for this bean, if any.
	 * <p>
	 *  指定此bean的属性值(如果有)
	 * 
	 */
	public void setPropertyValues(MutablePropertyValues propertyValues) {
		this.propertyValues = (propertyValues != null ? propertyValues : new MutablePropertyValues());
	}

	/**
	 * Return property values for this bean (never {@code null}).
	 * <p>
	 *  返回此bean的属性值(从不{@code null})
	 * 
	 */
	@Override
	public MutablePropertyValues getPropertyValues() {
		return this.propertyValues;
	}

	/**
	 * Specify method overrides for the bean, if any.
	 * <p>
	 *  指定bean的方法覆盖(如果有)
	 * 
	 */
	public void setMethodOverrides(MethodOverrides methodOverrides) {
		this.methodOverrides = (methodOverrides != null ? methodOverrides : new MethodOverrides());
	}

	/**
	 * Return information about methods to be overridden by the IoC
	 * container. This will be empty if there are no method overrides.
	 * Never returns {@code null}.
	 * <p>
	 * 返回有关要由IoC容器覆盖的方法的信息如果没有方法覆盖,则将为空否决返回{@code null}
	 * 
	 */
	public MethodOverrides getMethodOverrides() {
		return this.methodOverrides;
	}


	@Override
	public void setFactoryBeanName(String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}

	@Override
	public String getFactoryBeanName() {
		return this.factoryBeanName;
	}

	@Override
	public void setFactoryMethodName(String factoryMethodName) {
		this.factoryMethodName = factoryMethodName;
	}

	@Override
	public String getFactoryMethodName() {
		return this.factoryMethodName;
	}

	/**
	 * Set the name of the initializer method. The default is {@code null}
	 * in which case there is no initializer method.
	 * <p>
	 *  设置初始化器方法的名称默认值为{@code null},在这种情况下,没有初始化方法
	 * 
	 */
	public void setInitMethodName(String initMethodName) {
		this.initMethodName = initMethodName;
	}

	/**
	 * Return the name of the initializer method.
	 * <p>
	 *  返回初始化器方法的名称
	 * 
	 */
	public String getInitMethodName() {
		return this.initMethodName;
	}

	/**
	 * Specify whether or not the configured init method is the default.
	 * Default value is {@code false}.
	 * <p>
	 *  指定配置的init方法是否为默认值Default值为{@code false}
	 * 
	 * 
	 * @see #setInitMethodName
	 */
	public void setEnforceInitMethod(boolean enforceInitMethod) {
		this.enforceInitMethod = enforceInitMethod;
	}

	/**
	 * Indicate whether the configured init method is the default.
	 * <p>
	 *  指示配置的init方法是否为默认值
	 * 
	 * 
	 * @see #getInitMethodName()
	 */
	public boolean isEnforceInitMethod() {
		return this.enforceInitMethod;
	}

	/**
	 * Set the name of the destroy method. The default is {@code null}
	 * in which case there is no destroy method.
	 * <p>
	 *  设置destroy方法的名称默认是{@code null},在这种情况下没有破坏方法
	 * 
	 */
	public void setDestroyMethodName(String destroyMethodName) {
		this.destroyMethodName = destroyMethodName;
	}

	/**
	 * Return the name of the destroy method.
	 * <p>
	 *  返回destroy方法的名称
	 * 
	 */
	public String getDestroyMethodName() {
		return this.destroyMethodName;
	}

	/**
	 * Specify whether or not the configured destroy method is the default.
	 * Default value is {@code false}.
	 * <p>
	 *  指定配置的destroy方法是否为默认值Default值为{@code false}
	 * 
	 * 
	 * @see #setDestroyMethodName
	 */
	public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
		this.enforceDestroyMethod = enforceDestroyMethod;
	}

	/**
	 * Indicate whether the configured destroy method is the default.
	 * <p>
	 *  指示配置的destroy方法是否为默认值
	 * 
	 * 
	 * @see #getDestroyMethodName
	 */
	public boolean isEnforceDestroyMethod() {
		return this.enforceDestroyMethod;
	}


	/**
	 * Set whether this bean definition is 'synthetic', that is, not defined
	 * by the application itself (for example, an infrastructure bean such
	 * as a helper for auto-proxying, created through {@code <aop:config>}).
	 * <p>
	 * 设置此bean定义是否为"合成",即不由应用程序本身定义(例如,通过{@code <aop：config>}创建的基础架构bean(例如自动代理的帮助器)
	 * 
	 */
	public void setSynthetic(boolean synthetic) {
		this.synthetic = synthetic;
	}

	/**
	 * Return whether this bean definition is 'synthetic', that is,
	 * not defined by the application itself.
	 * <p>
	 *  返回此bean定义是否为"合成",即不由应用程序本身定义
	 * 
	 */
	public boolean isSynthetic() {
		return this.synthetic;
	}

	/**
	 * Set the role hint for this {@code BeanDefinition}.
	 * <p>
	 *  设置{@code BeanDefinition}的角色提示
	 * 
	 */
	public void setRole(int role) {
		this.role = role;
	}

	/**
	 * Return the role hint for this {@code BeanDefinition}.
	 * <p>
	 *  返回此{@code BeanDefinition}的角色提示
	 * 
	 */
	@Override
	public int getRole() {
		return this.role;
	}


	/**
	 * Set a human-readable description of this bean definition.
	 * <p>
	 *  设置这个bean定义的可读描述
	 * 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set the resource that this bean definition came from
	 * (for the purpose of showing context in case of errors).
	 * <p>
	 *  设置此bean定义来源的资源(为了在发生错误时显示上下文的目的)
	 * 
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Return the resource that this bean definition came from.
	 * <p>
	 *  返回此bean定义来源的资源
	 * 
	 */
	public Resource getResource() {
		return this.resource;
	}

	/**
	 * Set a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 * <p>
	 *  设置此bean定义来源的资源的描述(出于错误的情况下显示上下文的目的)
	 * 
	 */
	public void setResourceDescription(String resourceDescription) {
		this.resource = new DescriptiveResource(resourceDescription);
	}

	@Override
	public String getResourceDescription() {
		return (this.resource != null ? this.resource.getDescription() : null);
	}

	/**
	 * Set the originating (e.g. decorated) BeanDefinition, if any.
	 * <p>
	 * 设置始发(如装饰)BeanDefinition,如果有的话
	 * 
	 */
	public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
		this.resource = new BeanDefinitionResource(originatingBd);
	}

	@Override
	public BeanDefinition getOriginatingBeanDefinition() {
		return (this.resource instanceof BeanDefinitionResource ?
				((BeanDefinitionResource) this.resource).getBeanDefinition() : null);
	}

	/**
	 * Validate this bean definition.
	 * <p>
	 *  验证此bean定义
	 * 
	 * 
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	public void validate() throws BeanDefinitionValidationException {
		if (!getMethodOverrides().isEmpty() && getFactoryMethodName() != null) {
			throw new BeanDefinitionValidationException(
					"Cannot combine static factory method with method overrides: " +
					"the static factory method must create the instance");
		}

		if (hasBeanClass()) {
			prepareMethodOverrides();
		}
	}

	/**
	 * Validate and prepare the method overrides defined for this bean.
	 * Checks for existence of a method with the specified name.
	 * <p>
	 *  验证并准备为此bean定义的方法覆盖检查是否存在具有指定名称的方法
	 * 
	 * 
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	public void prepareMethodOverrides() throws BeanDefinitionValidationException {
		// Check that lookup methods exists.
		MethodOverrides methodOverrides = getMethodOverrides();
		if (!methodOverrides.isEmpty()) {
			Set<MethodOverride> overrides = methodOverrides.getOverrides();
			synchronized (overrides) {
				for (MethodOverride mo : overrides) {
					prepareMethodOverride(mo);
				}
			}
		}
	}

	/**
	 * Validate and prepare the given method override.
	 * Checks for existence of a method with the specified name,
	 * marking it as not overloaded if none found.
	 * <p>
	 *  验证并准备给定的方法覆盖检查存在具有指定名称的方法,如果没有找到,将其标记为不重载
	 * 
	 * 
	 * @param mo the MethodOverride object to validate
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
		int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
		if (count == 0) {
			throw new BeanDefinitionValidationException(
					"Invalid method override: no method with name '" + mo.getMethodName() +
					"' on class [" + getBeanClassName() + "]");
		}
		else if (count == 1) {
			// Mark override as not overloaded, to avoid the overhead of arg type checking.
			mo.setOverloaded(false);
		}
	}


	/**
	 * Public declaration of Object's {@code clone()} method.
	 * Delegates to {@link #cloneBeanDefinition()}.
	 * <p>
	 *  Object的{@code clone()}方法的公共声明委托给{@link #cloneBeanDefinition()}
	 * 
	 * 
	 * @see Object#clone()
	 */
	@Override
	public Object clone() {
		return cloneBeanDefinition();
	}

	/**
	 * Clone this bean definition.
	 * To be implemented by concrete subclasses.
	 * <p>
	 *  克隆此bean定义要由具体的子类实现
	 * 
	 * @return the cloned bean definition object
	 */
	public abstract AbstractBeanDefinition cloneBeanDefinition();


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractBeanDefinition)) {
			return false;
		}

		AbstractBeanDefinition that = (AbstractBeanDefinition) other;

		if (!ObjectUtils.nullSafeEquals(getBeanClassName(), that.getBeanClassName())) return false;
		if (!ObjectUtils.nullSafeEquals(this.scope, that.scope)) return false;
		if (this.abstractFlag != that.abstractFlag) return false;
		if (this.lazyInit != that.lazyInit) return false;

		if (this.autowireMode != that.autowireMode) return false;
		if (this.dependencyCheck != that.dependencyCheck) return false;
		if (!Arrays.equals(this.dependsOn, that.dependsOn)) return false;
		if (this.autowireCandidate != that.autowireCandidate) return false;
		if (!ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers)) return false;
		if (this.primary != that.primary) return false;

		if (this.nonPublicAccessAllowed != that.nonPublicAccessAllowed) return false;
		if (this.lenientConstructorResolution != that.lenientConstructorResolution) return false;
		if (!ObjectUtils.nullSafeEquals(this.constructorArgumentValues, that.constructorArgumentValues)) return false;
		if (!ObjectUtils.nullSafeEquals(this.propertyValues, that.propertyValues)) return false;
		if (!ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides)) return false;

		if (!ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName)) return false;
		if (!ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName)) return false;
		if (!ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName)) return false;
		if (this.enforceInitMethod != that.enforceInitMethod) return false;
		if (!ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName)) return false;
		if (this.enforceDestroyMethod != that.enforceDestroyMethod) return false;

		if (this.synthetic != that.synthetic) return false;
		if (this.role != that.role) return false;

		return super.equals(other);
	}

	@Override
	public int hashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(getBeanClassName());
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
		hashCode = 29 * hashCode + super.hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("class [");
		sb.append(getBeanClassName()).append("]");
		sb.append("; scope=").append(this.scope);
		sb.append("; abstract=").append(this.abstractFlag);
		sb.append("; lazyInit=").append(this.lazyInit);
		sb.append("; autowireMode=").append(this.autowireMode);
		sb.append("; dependencyCheck=").append(this.dependencyCheck);
		sb.append("; autowireCandidate=").append(this.autowireCandidate);
		sb.append("; primary=").append(this.primary);
		sb.append("; factoryBeanName=").append(this.factoryBeanName);
		sb.append("; factoryMethodName=").append(this.factoryMethodName);
		sb.append("; initMethodName=").append(this.initMethodName);
		sb.append("; destroyMethodName=").append(this.destroyMethodName);
		if (this.resource != null) {
			sb.append("; defined in ").append(this.resource.getDescription());
		}
		return sb.toString();
	}

}
