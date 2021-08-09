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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;

/**
 * Descriptor for a specific dependency that is about to be injected.
 * Wraps a constructor parameter, a method parameter or a field,
 * allowing unified access to their metadata.
 *
 * <p>
 *  要注入的特定依赖项的描述符包装构造函数参数,方法参数或字段,允许统一访问其元数据
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 */
@SuppressWarnings("serial")
public class DependencyDescriptor extends InjectionPoint implements Serializable {

	private final Class<?> declaringClass;

	private String methodName;

	private Class<?>[] parameterTypes;

	private int parameterIndex;

	private String fieldName;

	private final boolean required;

	private final boolean eager;

	private int nestingLevel = 1;

	private Class<?> containingClass;


	/**
	 * Create a new descriptor for a method or constructor parameter.
	 * Considers the dependency as 'eager'.
	 * <p>
	 * 为方法或构造函数参数创建一个新的描述符将依赖关系视为"渴望"
	 * 
	 * 
	 * @param methodParameter the MethodParameter to wrap
	 * @param required whether the dependency is required
	 */
	public DependencyDescriptor(MethodParameter methodParameter, boolean required) {
		this(methodParameter, required, true);
	}

	/**
	 * Create a new descriptor for a method or constructor parameter.
	 * <p>
	 *  为方法或构造函数参数创建一个新的描述符
	 * 
	 * 
	 * @param methodParameter the MethodParameter to wrap
	 * @param required whether the dependency is required
	 * @param eager whether this dependency is 'eager' in the sense of
	 * eagerly resolving potential target beans for type matching
	 */
	public DependencyDescriptor(MethodParameter methodParameter, boolean required, boolean eager) {
		super(methodParameter);
		this.declaringClass = methodParameter.getDeclaringClass();
		if (this.methodParameter.getMethod() != null) {
			this.methodName = methodParameter.getMethod().getName();
			this.parameterTypes = methodParameter.getMethod().getParameterTypes();
		}
		else {
			this.parameterTypes = methodParameter.getConstructor().getParameterTypes();
		}
		this.parameterIndex = methodParameter.getParameterIndex();
		this.containingClass = methodParameter.getContainingClass();
		this.required = required;
		this.eager = eager;
	}

	/**
	 * Create a new descriptor for a field.
	 * Considers the dependency as 'eager'.
	 * <p>
	 *  为字段创建一个新的描述符将依赖关系视为"渴望"
	 * 
	 * 
	 * @param field the field to wrap
	 * @param required whether the dependency is required
	 */
	public DependencyDescriptor(Field field, boolean required) {
		this(field, required, true);
	}

	/**
	 * Create a new descriptor for a field.
	 * <p>
	 *  为一个字段创建一个新的描述符
	 * 
	 * 
	 * @param field the field to wrap
	 * @param required whether the dependency is required
	 * @param eager whether this dependency is 'eager' in the sense of
	 * eagerly resolving potential target beans for type matching
	 */
	public DependencyDescriptor(Field field, boolean required, boolean eager) {
		super(field);
		this.declaringClass = field.getDeclaringClass();
		this.fieldName = field.getName();
		this.required = required;
		this.eager = eager;
	}

	/**
	 * Copy constructor.
	 * <p>
	 *  复制构造函数
	 * 
	 * 
	 * @param original the original descriptor to create a copy from
	 */
	public DependencyDescriptor(DependencyDescriptor original) {
		super(original);
		this.declaringClass = original.declaringClass;
		this.methodName = original.methodName;
		this.parameterTypes = original.parameterTypes;
		this.parameterIndex = original.parameterIndex;
		this.fieldName = original.fieldName;
		this.containingClass = original.containingClass;
		this.required = original.required;
		this.eager = original.eager;
		this.nestingLevel = original.nestingLevel;
	}


	/**
	 * Return whether this dependency is required.
	 * <p>
	 *  返回是否需要依赖
	 * 
	 */
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * Return whether this dependency is 'eager' in the sense of
	 * eagerly resolving potential target beans for type matching.
	 * <p>
	 *  返回这种依赖关系是否在渴望解决类型匹配的潜在目标bean的意义上是"渴望的"
	 * 
	 */
	public boolean isEager() {
		return this.eager;
	}

	/**
	 * Resolve the specified not-unique scenario: by default,
	 * throwing a {@link NoUniqueBeanDefinitionException}.
	 * <p>Subclasses may override this to select one of the instances or
	 * to opt out with no result at all through returning {@code null}.
	 * <p>
	 *  解决指定的不唯一的方案：默认情况下,抛出{@link NoUniqueBeanDefinitionException} <p>子类可以覆盖此选项以选择其中一个实例,或者通过返回{@code null}
	 * 选择退出而不使用任何结果。
	 * 
	 * 
	 * @param type the requested bean type
	 * @param matchingBeans a map of bean names and corresponding bean
	 * instances which have been pre-selected for the given type
	 * (qualifiers etc already applied)
	 * @return a bean instance to proceed with, or {@code null} for none
	 * @throws BeansException in case of the not-unique scenario being fatal
	 * @since 4.3
	 */
	public Object resolveNotUnique(Class<?> type, Map<String, Object> matchingBeans) throws BeansException {
		throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
	}

	/**
	 * Resolve a shortcut for this dependency against the given factory, for example
	 * taking some pre-resolved information into account.
	 * <p>The resolution algorithm will first attempt to resolve a shortcut through this
	 * method before going into the regular type matching algorithm across all beans.
	 * Subclasses may override this method to improve resolution performance based on
	 * pre-cached information while still receiving {@link InjectionPoint} exposure etc.
	 * <p>
	 * 解决针对给定工厂的此依赖性的快捷方式,例如将一些预先解析的信息纳入考虑<p>分解算法将首先尝试通过此方法解析快捷方式,然后再进入所有bean之间的常规类型匹配算法子类可以覆盖此方法以提高基于预缓存信息的
	 * 分辨率性能,同时仍然收到{@link InjectionPoint}曝光等。
	 * 
	 * 
	 * @param beanFactory the associated factory
	 * @return the shortcut result if any, or {@code null} if none
	 * @throws BeansException if the shortcut could not be obtained
	 * @since 4.3.1
	 */
	public Object resolveShortcut(BeanFactory beanFactory) throws BeansException {
		return null;
	}

	/**
	 * Resolve the specified bean name, as a candidate result of the matching
	 * algorithm for this dependency, to a bean instance from the given factory.
	 * <p>The default implementation calls {@link BeanFactory#getBean(String)}.
	 * Subclasses may provide additional arguments or other customizations.
	 * <p>
	 *  将指定的bean名称作为该依赖关系的匹配算法的候选结果解析为给定工厂的bean实例。
	 * <p>默认实现调用{@link BeanFactory#getBean(String)}子类可以提供其他参数或其他定制。
	 * 
	 * 
	 * @param beanName the bean name, as a candidate result for this dependency
	 * @param requiredType the expected type of the bean (as an assertion)
	 * @param beanFactory the associated factory
	 * @return the bean instance (never {@code null})
	 * @throws BeansException if the bean could not be obtained
	 * @since 4.3.2
	 * @see BeanFactory#getBean(String)
	 */
	public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory)
			throws BeansException {

		return beanFactory.getBean(beanName, requiredType);
	}


	/**
	 * Increase this descriptor's nesting level.
	 * <p>
	 *  增加描述符的嵌套级别
	 * 
	 * 
	 * @see MethodParameter#increaseNestingLevel()
	 */
	public void increaseNestingLevel() {
		this.nestingLevel++;
		if (this.methodParameter != null) {
			this.methodParameter.increaseNestingLevel();
		}
	}

	/**
	 * Optionally set the concrete class that contains this dependency.
	 * This may differ from the class that declares the parameter/field in that
	 * it may be a subclass thereof, potentially substituting type variables.
	 * <p>
	 * 可选地设置包含此依赖关系的具体类这可能与声明参数/字段的类不同,因为它可能是其子类,可能替换类型变量
	 * 
	 * 
	 * @since 4.0
	 */
	public void setContainingClass(Class<?> containingClass) {
		this.containingClass = containingClass;
		if (this.methodParameter != null) {
			GenericTypeResolver.resolveParameterType(this.methodParameter, containingClass);
		}
	}

	/**
	 * Build a ResolvableType object for the wrapped parameter/field.
	 * <p>
	 *  为包装的参数/字段构建ResolvableType对象
	 * 
	 * 
	 * @since 4.0
	 */
	public ResolvableType getResolvableType() {
		return (this.field != null ? ResolvableType.forField(this.field, this.nestingLevel, this.containingClass) :
				ResolvableType.forMethodParameter(this.methodParameter));
	}

	/**
	 * Return whether a fallback match is allowed.
	 * <p>This is {@code false} by default but may be overridden to return {@code true} in order
	 * to suggest to a {@link org.springframework.beans.factory.support.AutowireCandidateResolver}
	 * that a fallback match is acceptable as well.
	 * <p>
	 *  返回是否允许回退匹配<p>默认情况下,这是{@code false},但可能会被覆盖以返回{@code true},以便建议{@link orgspringframeworkbeansfactorysupportAutowireCandidateResolver}
	 * 也可以接受回退匹配。
	 * 
	 * 
	 * @since 4.0
	 */
	public boolean fallbackMatchAllowed() {
		return false;
	}

	/**
	 * Return a variant of this descriptor that is intended for a fallback match.
	 * <p>
	 *  返回一个用于回退匹配的描述符变体
	 * 
	 * 
	 * @since 4.0
	 * @see #fallbackMatchAllowed()
	 */
	public DependencyDescriptor forFallbackMatch() {
		return new DependencyDescriptor(this) {
			@Override
			public boolean fallbackMatchAllowed() {
				return true;
			}
		};
	}

	/**
	 * Initialize parameter name discovery for the underlying method parameter, if any.
	 * <p>This method does not actually try to retrieve the parameter name at
	 * this point; it just allows discovery to happen when the application calls
	 * {@link #getDependencyName()} (if ever).
	 * <p>
	 * 初始化底层方法参数的参数名称发现(如果有)<p>此方法实际上并没有尝试在此时检索参数名称;当应用程序调用{​​@link #getDependencyName()}(如果有)时,它只允许发现发生
	 * 
	 */
	public void initParameterNameDiscovery(ParameterNameDiscoverer parameterNameDiscoverer) {
		if (this.methodParameter != null) {
			this.methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
		}
	}

	/**
	 * Determine the name of the wrapped parameter/field.
	 * <p>
	 *  确定包装参数/字段的名称
	 * 
	 * 
	 * @return the declared name (never {@code null})
	 */
	public String getDependencyName() {
		return (this.field != null ? this.field.getName() : this.methodParameter.getParameterName());
	}

	/**
	 * Determine the declared (non-generic) type of the wrapped parameter/field.
	 * <p>
	 *  确定包装参数/字段的声明(非泛型)类型
	 * 
	 * 
	 * @return the declared type (never {@code null})
	 */
	public Class<?> getDependencyType() {
		if (this.field != null) {
			if (this.nestingLevel > 1) {
				Type type = this.field.getGenericType();
				for (int i = 2; i <= this.nestingLevel; i++) {
					if (type instanceof ParameterizedType) {
						Type[] args = ((ParameterizedType) type).getActualTypeArguments();
						type = args[args.length - 1];
					}
					// TODO: Object.class if unresolvable
				}
				if (type instanceof Class) {
					return (Class<?>) type;
				}
				else if (type instanceof ParameterizedType) {
					Type arg = ((ParameterizedType) type).getRawType();
					if (arg instanceof Class) {
						return (Class<?>) arg;
					}
				}
				return Object.class;
			}
			else {
				return this.field.getType();
			}
		}
		else {
			return this.methodParameter.getNestedParameterType();
		}
	}

	/**
	 * Determine the generic element type of the wrapped Collection parameter/field, if any.
	 * <p>
	 *  确定包装的Collection参数/字段的通用元素类型(如果有)
	 * 
	 * 
	 * @return the generic type, or {@code null} if none
	 */
	public Class<?> getCollectionType() {
		return (this.field != null ?
				GenericCollectionTypeResolver.getCollectionFieldType(this.field, this.nestingLevel) :
				GenericCollectionTypeResolver.getCollectionParameterType(this.methodParameter));
	}

	/**
	 * Determine the generic key type of the wrapped Map parameter/field, if any.
	 * <p>
	 *  确定包装的Map参数/字段的通用键类型(如果有)
	 * 
	 * 
	 * @return the generic type, or {@code null} if none
	 */
	public Class<?> getMapKeyType() {
		return (this.field != null ?
				GenericCollectionTypeResolver.getMapKeyFieldType(this.field, this.nestingLevel) :
				GenericCollectionTypeResolver.getMapKeyParameterType(this.methodParameter));
	}

	/**
	 * Determine the generic value type of the wrapped Map parameter/field, if any.
	 * <p>
	 *  确定包装的Map参数/字段的通用值类型(如果有)
	 * 
	 * @return the generic type, or {@code null} if none
	 */
	public Class<?> getMapValueType() {
		return (this.field != null ?
				GenericCollectionTypeResolver.getMapValueFieldType(this.field, this.nestingLevel) :
				GenericCollectionTypeResolver.getMapValueParameterType(this.methodParameter));
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other)) {
			return false;
		}
		DependencyDescriptor otherDesc = (DependencyDescriptor) other;
		return (this.required == otherDesc.required && this.eager == otherDesc.eager &&
				this.nestingLevel == otherDesc.nestingLevel && this.containingClass == otherDesc.containingClass);
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization; just initialize state after deserialization.
		ois.defaultReadObject();

		// Restore reflective handles (which are unfortunately not serializable)
		try {
			if (this.fieldName != null) {
				this.field = this.declaringClass.getDeclaredField(this.fieldName);
			}
			else {
				if (this.methodName != null) {
					this.methodParameter = new MethodParameter(
							this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
				}
				else {
					this.methodParameter = new MethodParameter(
							this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
				}
				for (int i = 1; i < this.nestingLevel; i++) {
					this.methodParameter.increaseNestingLevel();
				}
			}
		}
		catch (Throwable ex) {
			throw new IllegalStateException("Could not find original class structure", ex);
		}
	}

}
