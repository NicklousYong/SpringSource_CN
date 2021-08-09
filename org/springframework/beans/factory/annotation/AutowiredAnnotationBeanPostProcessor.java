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

package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that autowires annotated fields, setter methods and arbitrary config methods.
 * Such members to be injected are detected through a Java 5 annotation: by default,
 * Spring's {@link Autowired @Autowired} and {@link Value @Value} annotations.
 *
 * <p>Also supports JSR-330's {@link javax.inject.Inject @Inject} annotation,
 * if available, as a direct alternative to Spring's own {@code @Autowired}.
 *
 * <p>Only one constructor (at max) of any given bean class may carry this
 * annotation with the 'required' parameter set to {@code true},
 * indicating <i>the</i> constructor to autowire when used as a Spring bean.
 * If multiple <i>non-required</i> constructors carry the annotation, they
 * will be considered as candidates for autowiring. The constructor with
 * the greatest number of dependencies that can be satisfied by matching
 * beans in the Spring container will be chosen. If none of the candidates
 * can be satisfied, then a default constructor (if present) will be used.
 * An annotated constructor does not have to be public.
 *
 * <p>Fields are injected right after construction of a bean, before any
 * config methods are invoked. Such a config field does not have to be public.
 *
 * <p>Config methods may have an arbitrary name and any number of arguments; each of
 * those arguments will be autowired with a matching bean in the Spring container.
 * Bean property setter methods are effectively just a special case of such a
 * general config method. Config methods do not have to be public.
 *
 * <p>Note: A default AutowiredAnnotationBeanPostProcessor will be registered
 * by the "context:annotation-config" and "context:component-scan" XML tags.
 * Remove or turn off the default annotation configuration there if you intend
 * to specify a custom AutowiredAnnotationBeanPostProcessor bean definition.
 * <p><b>NOTE:</b> Annotation injection will be performed <i>before</i> XML injection;
 * thus the latter configuration will override the former for properties wired through
 * both approaches.
 *
 * <p>In addition to regular injection points as discussed above, this post-processor
 * also handles Spring's {@link Lookup @Lookup} annotation which identifies lookup
 * methods to be replaced by the container at runtime. This is essentially a type-safe
 * version of {@code getBean(Class, args)} and {@code getBean(String, args)},
 * See {@link Lookup @Lookup's javadoc} for details.
 *
 * <p>
 * 自动打印注释字段,setter方法和任意配置方法的{@link orgspringframeworkbeansfactoryconfigBeanPostProcessor}实现通过Java 5注释检测要
 * 注入的成员：默认情况下,Spring的{@link Autowired @Autowired}和{@link Value @Value}注释。
 * 
 *  <p>还支持JSR-330的{@link javaxinjectInject @Inject}注释(如果可用),作为Spring自己的{@code @Autowired}的直接替代方法
 * 
 * <p>任何给定bean类的只有一个构造函数(at max)可能会携带这个注释,其中"必需"参数设置为{@code true},表示<i> </i>构造函数在用作弹簧时自动连接bean如果多个<i>不需要
 * 的</i>构造函数携带注释,它们将被视为自动连线的候选项将选择具有最大数目的依赖关系的构造函数,可以通过匹配Spring容器中的bean来满足它们。
 * 如果没有的候选人可以满足,那么默认构造函数(如果存在)将被使用注释的构造函数不必是公共的。
 * 
 *  在构造bean之后,在调用任何配置方法之前,字段将被注入。这样的配置字段不必是公开的
 * 
 * 配置方法可能具有任意名称和任意数量的参数;这些参数中的每个参数都将在Spring容器中与一个匹配的bean自动连接Bean属性setter方法实际上只是一个这样的一般配置方法的特殊情况Config方法不
 * 必被公开。
 * 
 *  注意：默认的AutowiredAnnotationBeanPostProcessor将被"context：annotation-config"和"context：component-scan"XML标
 * 记注册。
 * 如果您打算指定自定义的AutowiredAnnotationBeanPostProcessor bean定义,删除或关闭默认的注释配置,注意：</b>注入注解将在</i> XML注入之前执行。
 * 因此后一种配置将覆盖前者通过两种方法连接的属性。
 * 
 * 除了如上所述的常规注入点之外,这个后处理器还处理Spring的{@link Lookup @Lookup}注释,它标识在运行时被容器替换的查找方法这本质上是一个类型安全的{ @code getBean(Class,args)}
 * 和{@code getBean(String,args)},有关详细信息,请参阅{@link Lookup @ Lookup的javadoc}。
 * 
 * 
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Stephane Nicoll
 * @since 2.5
 * @see #setAutowiredAnnotationType
 * @see Autowired
 * @see Value
 */
public class AutowiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {

	protected final Log logger = LogFactory.getLog(getClass());

	private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
			new LinkedHashSet<Class<? extends Annotation>>();

	private String requiredParameterName = "required";

	private boolean requiredParameterValue = true;

	private int order = Ordered.LOWEST_PRECEDENCE - 2;

	private ConfigurableListableBeanFactory beanFactory;

	private final Set<String> lookupMethodsChecked =
			Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(256));

	private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache =
			new ConcurrentHashMap<Class<?>, Constructor<?>[]>(256);

	private final Map<String, InjectionMetadata> injectionMetadataCache =
			new ConcurrentHashMap<String, InjectionMetadata>(256);


	/**
	 * Create a new AutowiredAnnotationBeanPostProcessor
	 * for Spring's standard {@link Autowired} annotation.
	 * <p>Also supports JSR-330's {@link javax.inject.Inject} annotation, if available.
	 * <p>
	 *  为Spring的标准{@link Autowired}注释创建一个新的AutowiredAnnotationBeanPostProcessor <p>还支持JSR-330的{@link javaxinjectInject}
	 * 注释(如果可用)。
	 * 
	 */
	@SuppressWarnings("unchecked")
	public AutowiredAnnotationBeanPostProcessor() {
		this.autowiredAnnotationTypes.add(Autowired.class);
		this.autowiredAnnotationTypes.add(Value.class);
		try {
			this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
			logger.info("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}


	/**
	 * Set the 'autowired' annotation type, to be used on constructors, fields,
	 * setter methods and arbitrary config methods.
	 * <p>The default autowired annotation type is the Spring-provided
	 * {@link Autowired} annotation, as well as {@link Value}.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a member is
	 * supposed to be autowired.
	 * <p>
	 * 设置"autowired"注释类型,用于构造函数,字段,setter方法和任意配置方法<p>默认的自动连线注释类型是Spring提供的{@link Autowired}注释以及{@link Value}
	 *  <p>此setter属性存在,以便开发人员可以提供自己的(非Spring特定的)注释类型,以指示一个成员应该是自动连线。
	 * 
	 */
	public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
		Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
		this.autowiredAnnotationTypes.clear();
		this.autowiredAnnotationTypes.add(autowiredAnnotationType);
	}

	/**
	 * Set the 'autowired' annotation types, to be used on constructors, fields,
	 * setter methods and arbitrary config methods.
	 * <p>The default autowired annotation type is the Spring-provided
	 * {@link Autowired} annotation, as well as {@link Value}.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation types to indicate that a member is
	 * supposed to be autowired.
	 * <p>
	 * 设置"autowired"注释类型,用于构造函数,字段,setter方法和任意配置方法<p>默认的自动连线注释类型是Spring提供的{@link Autowired}注释以及{@link Value}
	 *  <p>此setter属性存在,以便开发人员可以提供自己的(非Spring特定的)注释类型,以指示成员应该是自动连线。
	 * 
	 */
	public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
		Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
		this.autowiredAnnotationTypes.clear();
		this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
	}

	/**
	 * Set the name of a parameter of the annotation that specifies
	 * whether it is required.
	 * <p>
	 *  设置指定是否需要的注释的参数的名称
	 * 
	 * 
	 * @see #setRequiredParameterValue(boolean)
	 */
	public void setRequiredParameterName(String requiredParameterName) {
		this.requiredParameterName = requiredParameterName;
	}

	/**
	 * Set the boolean value that marks a dependency as required
	 * <p>For example if using 'required=true' (the default),
	 * this value should be {@code true}; but if using
	 * 'optional=false', this value should be {@code false}.
	 * <p>
	 *  设置根据需要标记依赖关系的布尔值<p>例如,如果使用'required = true'(默认值),则该值应为{@code true};但是如果使用'optional = false',则该值应为{@code false}
	 * 。
	 * 
	 * 
	 * @see #setRequiredParameterName(String)
	 */
	public void setRequiredParameterValue(boolean requiredParameterValue) {
		this.requiredParameterValue = requiredParameterValue;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
			throw new IllegalArgumentException(
					"AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
		}
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}


	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		if (beanType != null) {
			InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
			metadata.checkConfigMembers(beanDefinition);
		}
	}

	@Override
	public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName) throws BeansException {
		if (!this.lookupMethodsChecked.contains(beanName)) {
			ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					Lookup lookup = method.getAnnotation(Lookup.class);
					if (lookup != null) {
						LookupOverride override = new LookupOverride(method, lookup.value());
						try {
							RootBeanDefinition mbd = (RootBeanDefinition) beanFactory.getMergedBeanDefinition(beanName);
							mbd.getMethodOverrides().addOverride(override);
						}
						catch (NoSuchBeanDefinitionException ex) {
							throw new BeanCreationException(beanName,
									"Cannot apply @Lookup to beans without corresponding bean definition");
						}
					}
				}
			});
			this.lookupMethodsChecked.add(beanName);
		}

		// Quick check on the concurrent map first, with minimal locking.
		Constructor<?>[] candidateConstructors = this.candidateConstructorsCache.get(beanClass);
		if (candidateConstructors == null) {
			synchronized (this.candidateConstructorsCache) {
				candidateConstructors = this.candidateConstructorsCache.get(beanClass);
				if (candidateConstructors == null) {
					Constructor<?>[] rawCandidates = beanClass.getDeclaredConstructors();
					List<Constructor<?>> candidates = new ArrayList<Constructor<?>>(rawCandidates.length);
					Constructor<?> requiredConstructor = null;
					Constructor<?> defaultConstructor = null;
					for (Constructor<?> candidate : rawCandidates) {
						AnnotationAttributes ann = findAutowiredAnnotation(candidate);
						if (ann == null) {
							Class<?> userClass = ClassUtils.getUserClass(beanClass);
							if (userClass != beanClass) {
								try {
									Constructor<?> superCtor =
											userClass.getDeclaredConstructor(candidate.getParameterTypes());
									ann = findAutowiredAnnotation(superCtor);
								}
								catch (NoSuchMethodException ex) {
									// Simply proceed, no equivalent superclass constructor found...
								}
							}
						}
						if (ann != null) {
							if (requiredConstructor != null) {
								throw new BeanCreationException(beanName,
										"Invalid autowire-marked constructor: " + candidate +
										". Found constructor with 'required' Autowired annotation already: " +
										requiredConstructor);
							}
							if (candidate.getParameterTypes().length == 0) {
								throw new IllegalStateException(
										"Autowired annotation requires at least one argument: " + candidate);
							}
							boolean required = determineRequiredStatus(ann);
							if (required) {
								if (!candidates.isEmpty()) {
									throw new BeanCreationException(beanName,
											"Invalid autowire-marked constructors: " + candidates +
											". Found constructor with 'required' Autowired annotation: " +
											candidate);
								}
								requiredConstructor = candidate;
							}
							candidates.add(candidate);
						}
						else if (candidate.getParameterTypes().length == 0) {
							defaultConstructor = candidate;
						}
					}
					if (!candidates.isEmpty()) {
						// Add default constructor to list of optional constructors, as fallback.
						if (requiredConstructor == null) {
							if (defaultConstructor != null) {
								candidates.add(defaultConstructor);
							}
							else if (candidates.size() == 1 && logger.isWarnEnabled()) {
								logger.warn("Inconsistent constructor declaration on bean with name '" + beanName +
										"': single autowire-marked constructor flagged as optional - this constructor " +
										"is effectively required since there is no default constructor to fall back to: " +
										candidates.get(0));
							}
						}
						candidateConstructors = candidates.toArray(new Constructor<?>[candidates.size()]);
					}
					else if (rawCandidates.length == 1 && rawCandidates[0].getParameterTypes().length > 0) {
						candidateConstructors = new Constructor<?>[] {rawCandidates[0]};
					}
					else {
						candidateConstructors = new Constructor<?>[0];
					}
					this.candidateConstructorsCache.put(beanClass, candidateConstructors);
				}
			}
		}
		return (candidateConstructors.length > 0 ? candidateConstructors : null);
	}

	@Override
	public PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

		InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
		try {
			metadata.inject(bean, beanName, pvs);
		}
		catch (BeanCreationException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
		}
		return pvs;
	}

	/**
	 * 'Native' processing method for direct calls with an arbitrary target instance,
	 * resolving all of its fields and methods which are annotated with {@code @Autowired}.
	 * <p>
	 * 用于直接调用任意目标实例的'Native'处理方法,解析所有的字段和方法,这些方法用{@code @Autowired}注释
	 * 
	 * 
	 * @param bean the target instance to process
	 * @throws BeansException if autowiring failed
	 */
	public void processInjection(Object bean) throws BeansException {
		Class<?> clazz = bean.getClass();
		InjectionMetadata metadata = findAutowiringMetadata(clazz.getName(), clazz, null);
		try {
			metadata.inject(bean, null, null);
		}
		catch (BeanCreationException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", ex);
		}
	}


	private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
		// Fall back to class name as cache key, for backwards compatibility with custom callers.
		String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
		// Quick check on the concurrent map first, with minimal locking.
		InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
		if (InjectionMetadata.needsRefresh(metadata, clazz)) {
			synchronized (this.injectionMetadataCache) {
				metadata = this.injectionMetadataCache.get(cacheKey);
				if (InjectionMetadata.needsRefresh(metadata, clazz)) {
					if (metadata != null) {
						metadata.clear(pvs);
					}
					try {
						metadata = buildAutowiringMetadata(clazz);
						this.injectionMetadataCache.put(cacheKey, metadata);
					}
					catch (NoClassDefFoundError err) {
						throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
								"] for autowiring metadata: could not find class that it depends on", err);
					}
				}
			}
		}
		return metadata;
	}

	private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
		LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
		Class<?> targetClass = clazz;

		do {
			final LinkedList<InjectionMetadata.InjectedElement> currElements =
					new LinkedList<InjectionMetadata.InjectedElement>();

			ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					AnnotationAttributes ann = findAutowiredAnnotation(field);
					if (ann != null) {
						if (Modifier.isStatic(field.getModifiers())) {
							if (logger.isWarnEnabled()) {
								logger.warn("Autowired annotation is not supported on static fields: " + field);
							}
							return;
						}
						boolean required = determineRequiredStatus(ann);
						currElements.add(new AutowiredFieldElement(field, required));
					}
				}
			});

			ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
					if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
						return;
					}
					AnnotationAttributes ann = findAutowiredAnnotation(bridgedMethod);
					if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
						if (Modifier.isStatic(method.getModifiers())) {
							if (logger.isWarnEnabled()) {
								logger.warn("Autowired annotation is not supported on static methods: " + method);
							}
							return;
						}
						if (method.getParameterTypes().length == 0) {
							if (logger.isWarnEnabled()) {
								logger.warn("Autowired annotation should be used on methods with parameters: " + method);
							}
						}
						boolean required = determineRequiredStatus(ann);
						PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
						currElements.add(new AutowiredMethodElement(method, required, pd));
					}
				}
			});

			elements.addAll(0, currElements);
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);

		return new InjectionMetadata(clazz, elements);
	}

	private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
		if (ao.getAnnotations().length > 0) {
			for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
				AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, type);
				if (attributes != null) {
					return attributes;
				}
			}
		}
		return null;
	}

	/**
	 * Determine if the annotated field or method requires its dependency.
	 * <p>A 'required' dependency means that autowiring should fail when no beans
	 * are found. Otherwise, the autowiring process will simply bypass the field
	 * or method when no beans are found.
	 * <p>
	 *  确定注释的字段或方法是否需要其依赖性<p>"必需"依赖性意味着当没有找到bean时,自动连接应该失败否则,当没有找到bean时,自动连接过程将简单地绕过字段或方法
	 * 
	 * 
	 * @param ann the Autowired annotation
	 * @return whether the annotation indicates that a dependency is required
	 */
	protected boolean determineRequiredStatus(AnnotationAttributes ann) {
		return (!ann.containsKey(this.requiredParameterName) ||
				this.requiredParameterValue == ann.getBoolean(this.requiredParameterName));
	}

	/**
	 * Obtain all beans of the given type as autowire candidates.
	 * <p>
	 *  获取给定类型的所有bean作为自动连线候选
	 * 
	 * 
	 * @param type the type of the bean
	 * @return the target beans, or an empty Collection if no bean of this type is found
	 * @throws BeansException if bean retrieval failed
	 */
	protected <T> Map<String, T> findAutowireCandidates(Class<T> type) throws BeansException {
		if (this.beanFactory == null) {
			throw new IllegalStateException("No BeanFactory configured - " +
					"override the getBeanOfType method or specify the 'beanFactory' property");
		}
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(this.beanFactory, type);
	}

	/**
	 * Register the specified bean as dependent on the autowired beans.
	 * <p>
	 *  将指定的bean注册为依赖于自动连线的bean
	 * 
	 */
	private void registerDependentBeans(String beanName, Set<String> autowiredBeanNames) {
		if (beanName != null) {
			for (String autowiredBeanName : autowiredBeanNames) {
				if (this.beanFactory.containsBean(autowiredBeanName)) {
					this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Autowiring by type from bean name '" + beanName +
							"' to bean named '" + autowiredBeanName + "'");
				}
			}
		}
	}

	/**
	 * Resolve the specified cached method argument or field value.
	 * <p>
	 *  解析指定的缓存方法参数或字段值
	 * 
	 */
	private Object resolvedCachedArgument(String beanName, Object cachedArgument) {
		if (cachedArgument instanceof DependencyDescriptor) {
			DependencyDescriptor descriptor = (DependencyDescriptor) cachedArgument;
			return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
		}
		else {
			return cachedArgument;
		}
	}


	/**
	 * Class representing injection information about an annotated field.
	 * <p>
	 *  表示关于注释字段的注入信息的类
	 * 
	 */
	private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

		private final boolean required;

		private volatile boolean cached = false;

		private volatile Object cachedFieldValue;

		public AutowiredFieldElement(Field field, boolean required) {
			super(field, null);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			Object value;
			if (this.cached) {
				value = resolvedCachedArgument(beanName, this.cachedFieldValue);
			}
			else {
				DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
				desc.setContainingClass(bean.getClass());
				Set<String> autowiredBeanNames = new LinkedHashSet<String>(1);
				TypeConverter typeConverter = beanFactory.getTypeConverter();
				try {
					value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
				}
				catch (BeansException ex) {
					throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
				}
				synchronized (this) {
					if (!this.cached) {
						if (value != null || this.required) {
							this.cachedFieldValue = desc;
							registerDependentBeans(beanName, autowiredBeanNames);
							if (autowiredBeanNames.size() == 1) {
								String autowiredBeanName = autowiredBeanNames.iterator().next();
								if (beanFactory.containsBean(autowiredBeanName)) {
									if (beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
										this.cachedFieldValue = new ShortcutDependencyDescriptor(
												desc, autowiredBeanName, field.getType());
									}
								}
							}
						}
						else {
							this.cachedFieldValue = null;
						}
						this.cached = true;
					}
				}
			}
			if (value != null) {
				ReflectionUtils.makeAccessible(field);
				field.set(bean, value);
			}
		}
	}


	/**
	 * Class representing injection information about an annotated method.
	 * <p>
	 *  表示关于注释方法的注入信息的类
	 * 
	 */
	private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

		private final boolean required;

		private volatile boolean cached = false;

		private volatile Object[] cachedMethodArguments;

		public AutowiredMethodElement(Method method, boolean required, PropertyDescriptor pd) {
			super(method, pd);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			if (checkPropertySkipping(pvs)) {
				return;
			}
			Method method = (Method) this.member;
			Object[] arguments;
			if (this.cached) {
				// Shortcut for avoiding synchronization...
				arguments = resolveCachedArguments(beanName);
			}
			else {
				Class<?>[] paramTypes = method.getParameterTypes();
				arguments = new Object[paramTypes.length];
				DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
				Set<String> autowiredBeanNames = new LinkedHashSet<String>(paramTypes.length);
				TypeConverter typeConverter = beanFactory.getTypeConverter();
				for (int i = 0; i < arguments.length; i++) {
					MethodParameter methodParam = new MethodParameter(method, i);
					DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
					currDesc.setContainingClass(bean.getClass());
					descriptors[i] = currDesc;
					try {
						Object arg = beanFactory.resolveDependency(currDesc, beanName, autowiredBeanNames, typeConverter);
						if (arg == null && !this.required) {
							arguments = null;
							break;
						}
						arguments[i] = arg;
					}
					catch (BeansException ex) {
						throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(methodParam), ex);
					}
				}
				synchronized (this) {
					if (!this.cached) {
						if (arguments != null) {
							this.cachedMethodArguments = new Object[paramTypes.length];
							for (int i = 0; i < arguments.length; i++) {
								this.cachedMethodArguments[i] = descriptors[i];
							}
							registerDependentBeans(beanName, autowiredBeanNames);
							if (autowiredBeanNames.size() == paramTypes.length) {
								Iterator<String> it = autowiredBeanNames.iterator();
								for (int i = 0; i < paramTypes.length; i++) {
									String autowiredBeanName = it.next();
									if (beanFactory.containsBean(autowiredBeanName)) {
										if (beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i])) {
											this.cachedMethodArguments[i] = new ShortcutDependencyDescriptor(
													descriptors[i], autowiredBeanName, paramTypes[i]);
										}
									}
								}
							}
						}
						else {
							this.cachedMethodArguments = null;
						}
						this.cached = true;
					}
				}
			}
			if (arguments != null) {
				try {
					ReflectionUtils.makeAccessible(method);
					method.invoke(bean, arguments);
				}
				catch (InvocationTargetException ex){
					throw ex.getTargetException();
				}
			}
		}

		private Object[] resolveCachedArguments(String beanName) {
			if (this.cachedMethodArguments == null) {
				return null;
			}
			Object[] arguments = new Object[this.cachedMethodArguments.length];
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = resolvedCachedArgument(beanName, this.cachedMethodArguments[i]);
			}
			return arguments;
		}
	}


	/**
	 * DependencyDescriptor variant with a pre-resolved target bean name.
	 * <p>
	 * 具有预解析目标bean名称的DependencyDescriptor变体
	 */
	@SuppressWarnings("serial")
	private static class ShortcutDependencyDescriptor extends DependencyDescriptor {

		private final String shortcutName;

		private final Class<?> requiredType;

		public ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcutName, Class<?> requiredType) {
			super(original);
			this.shortcutName = shortcutName;
			this.requiredType = requiredType;
		}

		@Override
		public Object resolveShortcut(BeanFactory beanFactory) {
			return resolveCandidate(this.shortcutName, this.requiredType, beanFactory);
		}
	}

}
