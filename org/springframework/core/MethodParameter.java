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

package org.springframework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Helper class that encapsulates the specification of a method parameter, i.e. a {@link Method}
 * or {@link Constructor} plus a parameter index and a nested type index for a declared generic
 * type. Useful as a specification object to pass along.
 *
 * <p>As of 4.2, there is a {@link org.springframework.core.annotation.SynthesizingMethodParameter}
 * subclass available which synthesizes annotations with attribute aliases. That subclass is used
 * for web and message endpoint processing, in particular.
 *
 * <p>
 * 封装方法参数规范的助手类,即{@link Method}或{@link Constructor}加上声明的通用类型的参数索引和嵌套类型索引作为传递的规范对象有用
 * 
 *  <p>截至42,有一个{@link orgspringframeworkcoreannotationSynthesizingMethodParameter}可用的子类,它与属性别名合成注释。
 * 该子类用于Web和消息端点处理,特别是。
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Andy Clement
 * @author Sam Brannen
 * @since 2.0
 * @see GenericCollectionTypeResolver
 * @see org.springframework.core.annotation.SynthesizingMethodParameter
 */
public class MethodParameter {

	private static final Class<?> javaUtilOptionalClass;

	static {
		Class<?> clazz;
		try {
			clazz = ClassUtils.forName("java.util.Optional", MethodParameter.class.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			// Java 8 not available - Optional references simply not supported then.
			clazz = null;
		}
		javaUtilOptionalClass = clazz;
	}


	private final Method method;

	private final Constructor<?> constructor;

	private final int parameterIndex;

	private int nestingLevel = 1;

	/** Map from Integer level to Integer type index */
	Map<Integer, Integer> typeIndexesPerLevel;

	private volatile Class<?> containingClass;

	private volatile Class<?> parameterType;

	private volatile Type genericParameterType;

	private volatile Annotation[] parameterAnnotations;

	private volatile ParameterNameDiscoverer parameterNameDiscoverer;

	private volatile String parameterName;

	private volatile MethodParameter nestedMethodParameter;


	/**
	 * Create a new {@code MethodParameter} for the given method, with nesting level 1.
	 * <p>
	 *  为给定的方法创建一个新的{@code MethodParameter},嵌套级别为1
	 * 
	 * 
	 * @param method the Method to specify a parameter for
	 * @param parameterIndex the index of the parameter: -1 for the method
	 * return type; 0 for the first method parameter; 1 for the second method
	 * parameter, etc.
	 */
	public MethodParameter(Method method, int parameterIndex) {
		this(method, parameterIndex, 1);
	}

	/**
	 * Create a new {@code MethodParameter} for the given method.
	 * <p>
	 *  为给定的方法创建一个新的{@code MethodParameter}
	 * 
	 * 
	 * @param method the Method to specify a parameter for
	 * @param parameterIndex the index of the parameter: -1 for the method
	 * return type; 0 for the first method parameter; 1 for the second method
	 * parameter, etc.
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 */
	public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
		Assert.notNull(method, "Method must not be null");
		this.method = method;
		this.parameterIndex = parameterIndex;
		this.nestingLevel = nestingLevel;
		this.constructor = null;
	}

	/**
	 * Create a new MethodParameter for the given constructor, with nesting level 1.
	 * <p>
	 *  为给定的构造函数创建一个新的MethodParameter,嵌套级别为1
	 * 
	 * 
	 * @param constructor the Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 */
	public MethodParameter(Constructor<?> constructor, int parameterIndex) {
		this(constructor, parameterIndex, 1);
	}

	/**
	 * Create a new MethodParameter for the given constructor.
	 * <p>
	 *  为给定的构造函数创建一个新的MethodParameter
	 * 
	 * 
	 * @param constructor the Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 * @param nestingLevel the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List)
	 */
	public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
		Assert.notNull(constructor, "Constructor must not be null");
		this.constructor = constructor;
		this.parameterIndex = parameterIndex;
		this.nestingLevel = nestingLevel;
		this.method = null;
	}

	/**
	 * Copy constructor, resulting in an independent MethodParameter object
	 * based on the same metadata and cache state that the original object was in.
	 * <p>
	 * 复制构造函数,导致基于原始对象所在的元数据和缓存状态相同的独立MethodParameter对象
	 * 
	 * 
	 * @param original the original MethodParameter object to copy from
	 */
	public MethodParameter(MethodParameter original) {
		Assert.notNull(original, "Original must not be null");
		this.method = original.method;
		this.constructor = original.constructor;
		this.parameterIndex = original.parameterIndex;
		this.nestingLevel = original.nestingLevel;
		this.typeIndexesPerLevel = original.typeIndexesPerLevel;
		this.containingClass = original.containingClass;
		this.parameterType = original.parameterType;
		this.genericParameterType = original.genericParameterType;
		this.parameterAnnotations = original.parameterAnnotations;
		this.parameterNameDiscoverer = original.parameterNameDiscoverer;
		this.parameterName = original.parameterName;
	}


	/**
	 * Return the wrapped Method, if any.
	 * <p>Note: Either Method or Constructor is available.
	 * <p>
	 *  返回包装的方法,如果有的话<p>注意：方法或构造方法可用
	 * 
	 * 
	 * @return the Method, or {@code null} if none
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * Return the wrapped Constructor, if any.
	 * <p>Note: Either Method or Constructor is available.
	 * <p>
	 *  返回包装的构造方法(如果有的话)注意：方法或构造方法可用
	 * 
	 * 
	 * @return the Constructor, or {@code null} if none
	 */
	public Constructor<?> getConstructor() {
		return this.constructor;
	}

	/**
	 * Return the class that declares the underlying Method or Constructor.
	 * <p>
	 *  返回声明底层Method或Constructor的类
	 * 
	 */
	public Class<?> getDeclaringClass() {
		return getMember().getDeclaringClass();
	}

	/**
	 * Return the wrapped member.
	 * <p>
	 *  返回包装成员
	 * 
	 * 
	 * @return the Method or Constructor as Member
	 */
	public Member getMember() {
		// NOTE: no ternary expression to retain JDK <8 compatibility even when using
		// the JDK 8 compiler (potentially selecting java.lang.reflect.Executable
		// as common type, with that new base class not available on older JDKs)
		if (this.method != null) {
			return this.method;
		}
		else {
			return this.constructor;
		}
	}

	/**
	 * Return the wrapped annotated element.
	 * <p>Note: This method exposes the annotations declared on the method/constructor
	 * itself (i.e. at the method/constructor level, not at the parameter level).
	 * <p>
	 *  返回包装的注释元素<p>注意：此方法公开了方法/构造函数本身声明的注释(即在方法/构造函数级别,而不是参数级别)
	 * 
	 * 
	 * @return the Method or Constructor as AnnotatedElement
	 */
	public AnnotatedElement getAnnotatedElement() {
		// NOTE: no ternary expression to retain JDK <8 compatibility even when using
		// the JDK 8 compiler (potentially selecting java.lang.reflect.Executable
		// as common type, with that new base class not available on older JDKs)
		if (this.method != null) {
			return this.method;
		}
		else {
			return this.constructor;
		}
	}

	/**
	 * Return the index of the method/constructor parameter.
	 * <p>
	 *  返回method / constructor参数的索引
	 * 
	 * 
	 * @return the parameter index (-1 in case of the return type)
	 */
	public int getParameterIndex() {
		return this.parameterIndex;
	}

	/**
	 * Increase this parameter's nesting level.
	 * <p>
	 *  增加此参数的嵌套级别
	 * 
	 * 
	 * @see #getNestingLevel()
	 */
	public void increaseNestingLevel() {
		this.nestingLevel++;
	}

	/**
	 * Decrease this parameter's nesting level.
	 * <p>
	 *  减小此参数的嵌套级别
	 * 
	 * 
	 * @see #getNestingLevel()
	 */
	public void decreaseNestingLevel() {
		getTypeIndexesPerLevel().remove(this.nestingLevel);
		this.nestingLevel--;
	}

	/**
	 * Return the nesting level of the target type
	 * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
	 * nested List, whereas 2 would indicate the element of the nested List).
	 * <p>
	 * 返回目标类型的嵌套级别(通常为1;例如,在列表列表中,1表示嵌套列表,而2表示嵌套列表的元素)
	 * 
	 */
	public int getNestingLevel() {
		return this.nestingLevel;
	}

	/**
	 * Set the type index for the current nesting level.
	 * <p>
	 *  设置当前嵌套级别的类型索引
	 * 
	 * 
	 * @param typeIndex the corresponding type index
	 * (or {@code null} for the default type index)
	 * @see #getNestingLevel()
	 */
	public void setTypeIndexForCurrentLevel(int typeIndex) {
		getTypeIndexesPerLevel().put(this.nestingLevel, typeIndex);
	}

	/**
	 * Return the type index for the current nesting level.
	 * <p>
	 *  返回当前嵌套级别的类型索引
	 * 
	 * 
	 * @return the corresponding type index, or {@code null}
	 * if none specified (indicating the default type index)
	 * @see #getNestingLevel()
	 */
	public Integer getTypeIndexForCurrentLevel() {
		return getTypeIndexForLevel(this.nestingLevel);
	}

	/**
	 * Return the type index for the specified nesting level.
	 * <p>
	 *  返回指定嵌套级别的类型索引
	 * 
	 * 
	 * @param nestingLevel the nesting level to check
	 * @return the corresponding type index, or {@code null}
	 * if none specified (indicating the default type index)
	 */
	public Integer getTypeIndexForLevel(int nestingLevel) {
		return getTypeIndexesPerLevel().get(nestingLevel);
	}

	/**
	 * Obtain the (lazily constructed) type-indexes-per-level Map.
	 * <p>
	 *  获取(懒洋洋的)类型索引 - 每级地图
	 * 
	 */
	private Map<Integer, Integer> getTypeIndexesPerLevel() {
		if (this.typeIndexesPerLevel == null) {
			this.typeIndexesPerLevel = new HashMap<Integer, Integer>(4);
		}
		return this.typeIndexesPerLevel;
	}

	/**
	 * Return a variant of this {@code MethodParameter} which points to the
	 * same parameter but one nesting level deeper. This is effectively the
	 * same as {@link #increaseNestingLevel()}, just with an independent
	 * {@code MethodParameter} object (e.g. in case of the original being cached).
	 * <p>
	 *  返回这个{@code MethodParameter}的变体,它指向相同的参数,但是一个嵌套级别更深。
	 * 这与{@link #increaseNestingLevel()}有效地相同,只需使用独立的{@code MethodParameter}对象(例如,在例如的原始缓存)。
	 * 
	 * 
	 * @since 4.3
	 */
	public MethodParameter nested() {
		if (this.nestedMethodParameter != null) {
			return this.nestedMethodParameter;
		}
		MethodParameter nestedParam = clone();
		nestedParam.nestingLevel = this.nestingLevel + 1;
		this.nestedMethodParameter = nestedParam;
		return nestedParam;
	}

	/**
	 * Return whether this method parameter is declared as optional
	 * in the form of Java 8's {@link java.util.Optional}.
	 * <p>
	 *  返回此方法参数是否以Java 8的{@link javautilOptional}的形式声明为可选的
	 * 
	 * 
	 * @since 4.3
	 */
	public boolean isOptional() {
		return (getParameterType() == javaUtilOptionalClass);
	}

	/**
	 * Return a variant of this {@code MethodParameter} which points to
	 * the same parameter but one nesting level deeper in case of a
	 * {@link java.util.Optional} declaration.
	 * <p>
	 * 返回这个{@code MethodParameter}的变体,它指向相同的参数,但是在{@link javautilOptional}声明的情况下更深入一个嵌套级别
	 * 
	 * 
	 * @since 4.3
	 * @see #isOptional()
	 * @see #nested()
	 */
	public MethodParameter nestedIfOptional() {
		return (isOptional() ? nested() : this);
	}


	/**
	 * Set a containing class to resolve the parameter type against.
	 * <p>
	 *  设置一个包含类来解析参数类型
	 * 
	 */
	void setContainingClass(Class<?> containingClass) {
		this.containingClass = containingClass;
	}

	public Class<?> getContainingClass() {
		return (this.containingClass != null ? this.containingClass : getDeclaringClass());
	}

	/**
	 * Set a resolved (generic) parameter type.
	 * <p>
	 *  设置一个已解析(通用)参数类型
	 * 
	 */
	void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	/**
	 * Return the type of the method/constructor parameter.
	 * <p>
	 *  返回method / constructor参数的类型
	 * 
	 * 
	 * @return the parameter type (never {@code null})
	 */
	public Class<?> getParameterType() {
		if (this.parameterType == null) {
			if (this.parameterIndex < 0) {
				this.parameterType = (this.method != null ? this.method.getReturnType() : null);
			}
			else {
				this.parameterType = (this.method != null ?
					this.method.getParameterTypes()[this.parameterIndex] :
					this.constructor.getParameterTypes()[this.parameterIndex]);
			}
		}
		return this.parameterType;
	}

	/**
	 * Return the generic type of the method/constructor parameter.
	 * <p>
	 *  返回method / constructor参数的通用类型
	 * 
	 * 
	 * @return the parameter type (never {@code null})
	 * @since 3.0
	 */
	public Type getGenericParameterType() {
		if (this.genericParameterType == null) {
			if (this.parameterIndex < 0) {
				this.genericParameterType = (this.method != null ? this.method.getGenericReturnType() : null);
			}
			else {
				this.genericParameterType = (this.method != null ?
					this.method.getGenericParameterTypes()[this.parameterIndex] :
					this.constructor.getGenericParameterTypes()[this.parameterIndex]);
			}
		}
		return this.genericParameterType;
	}

	/**
	 * Return the nested type of the method/constructor parameter.
	 * <p>
	 *  返回方法/构造函数参数的嵌套类型
	 * 
	 * 
	 * @return the parameter type (never {@code null})
	 * @since 3.1
	 * @see #getNestingLevel()
	 */
	public Class<?> getNestedParameterType() {
		if (this.nestingLevel > 1) {
			Type type = getGenericParameterType();
			for (int i = 2; i <= this.nestingLevel; i++) {
				if (type instanceof ParameterizedType) {
					Type[] args = ((ParameterizedType) type).getActualTypeArguments();
					Integer index = getTypeIndexForLevel(i);
					type = args[index != null ? index : args.length - 1];
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
			return getParameterType();
		}
	}

	/**
	 * Return the nested generic type of the method/constructor parameter.
	 * <p>
	 *  返回嵌套的泛型类型的方法/ constructor参数
	 * 
	 * 
	 * @return the parameter type (never {@code null})
	 * @since 4.2
	 * @see #getNestingLevel()
	 */
	public Type getNestedGenericParameterType() {
		if (this.nestingLevel > 1) {
			Type type = getGenericParameterType();
			for (int i = 2; i <= this.nestingLevel; i++) {
				if (type instanceof ParameterizedType) {
					Type[] args = ((ParameterizedType) type).getActualTypeArguments();
					Integer index = getTypeIndexForLevel(i);
					type = args[index != null ? index : args.length - 1];
				}
			}
			return type;
		}
		else {
			return getGenericParameterType();
		}
	}

	/**
	 * Return the annotations associated with the target method/constructor itself.
	 * <p>
	 *  返回与目标方法/构造函数本身相关联的注释
	 * 
	 */
	public Annotation[] getMethodAnnotations() {
		return adaptAnnotationArray(getAnnotatedElement().getAnnotations());
	}

	/**
	 * Return the method/constructor annotation of the given type, if available.
	 * <p>
	 *  返回给定类型的方法/构造函数注释(如果可用)
	 * 
	 * 
	 * @param annotationType the annotation type to look for
	 * @return the annotation object, or {@code null} if not found
	 */
	public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
		return adaptAnnotation(getAnnotatedElement().getAnnotation(annotationType));
	}

	/**
	 * Return whether the method/constructor is annotated with the given type.
	 * <p>
	 *  返回方法/构造函数是否用给定类型注释
	 * 
	 * 
	 * @param annotationType the annotation type to look for
	 * @since 4.3
	 * @see #getMethodAnnotation(Class)
	 */
	public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
		return getAnnotatedElement().isAnnotationPresent(annotationType);
	}

	/**
	 * Return the annotations associated with the specific method/constructor parameter.
	 * <p>
	 * 返回与特定方法/构造函数参数相关联的注释
	 * 
	 */
	public Annotation[] getParameterAnnotations() {
		if (this.parameterAnnotations == null) {
			Annotation[][] annotationArray = (this.method != null ?
					this.method.getParameterAnnotations() : this.constructor.getParameterAnnotations());
			if (this.parameterIndex >= 0 && this.parameterIndex < annotationArray.length) {
				this.parameterAnnotations = adaptAnnotationArray(annotationArray[this.parameterIndex]);
			}
			else {
				this.parameterAnnotations = new Annotation[0];
			}
		}
		return this.parameterAnnotations;
	}

	/**
	 * Return {@code true} if the parameter has at least one annotation,
	 * {@code false} if it has none.
	 * <p>
	 *  如果参数至少有一个注释{@code false}(如果没有),则返回{@code true}
	 * 
	 * 
	 * @see #getParameterAnnotations()
	 */
	public boolean hasParameterAnnotations() {
		return (getParameterAnnotations().length != 0);
	}

	/**
	 * Return the parameter annotation of the given type, if available.
	 * <p>
	 *  返回给定类型的参数注释(如果可用)
	 * 
	 * 
	 * @param annotationType the annotation type to look for
	 * @return the annotation object, or {@code null} if not found
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getParameterAnnotation(Class<A> annotationType) {
		Annotation[] anns = getParameterAnnotations();
		for (Annotation ann : anns) {
			if (annotationType.isInstance(ann)) {
				return (A) ann;
			}
		}
		return null;
	}

	/**
	 * Return whether the parameter is declared with the given annotation type.
	 * <p>
	 *  返回参数是否以给定的注释类型声明
	 * 
	 * 
	 * @param annotationType the annotation type to look for
	 * @see #getParameterAnnotation(Class)
	 */
	public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
		return (getParameterAnnotation(annotationType) != null);
	}

	/**
	 * Initialize parameter name discovery for this method parameter.
	 * <p>This method does not actually try to retrieve the parameter name at
	 * this point; it just allows discovery to happen when the application calls
	 * {@link #getParameterName()} (if ever).
	 * <p>
	 *  初始化此方法参数的参数名称发现<p>此方法实际上并没有尝试在此时检索参数名称;当应用程序调用{​​@link #getParameterName()}(如果有)时,它只允许发现,
	 * 
	 */
	public void initParameterNameDiscovery(ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	/**
	 * Return the name of the method/constructor parameter.
	 * <p>
	 *  返回方法/ constructor参数的名称
	 * 
	 * 
	 * @return the parameter name (may be {@code null} if no
	 * parameter name metadata is contained in the class file or no
	 * {@link #initParameterNameDiscovery ParameterNameDiscoverer}
	 * has been set to begin with)
	 */
	public String getParameterName() {
		ParameterNameDiscoverer discoverer = this.parameterNameDiscoverer;
		if (discoverer != null) {
			String[] parameterNames = (this.method != null ?
					discoverer.getParameterNames(this.method) : discoverer.getParameterNames(this.constructor));
			if (parameterNames != null) {
				this.parameterName = parameterNames[this.parameterIndex];
			}
			this.parameterNameDiscoverer = null;
		}
		return this.parameterName;
	}


	/**
	 * A template method to post-process a given annotation instance before
	 * returning it to the caller.
	 * <p>The default implementation simply returns the given annotation as-is.
	 * <p>
	 *  在给定注释实例返回给调用者之前进行后处理的模板方法<p>默认实现只是按原样返回给定的注释
	 * 
	 * 
	 * @param annotation the annotation about to be returned
	 * @return the post-processed annotation (or simply the original one)
	 * @since 4.2
	 */
	protected <A extends Annotation> A adaptAnnotation(A annotation) {
		return annotation;
	}

	/**
	 * A template method to post-process a given annotation array before
	 * returning it to the caller.
	 * <p>The default implementation simply returns the given annotation array as-is.
	 * <p>
	 * 在给定注释数组返回给调用者之前进行后处理的模板方法<p>默认实现只是按原样返回给定的注释数组
	 * 
	 * 
	 * @param annotations the annotation array about to be returned
	 * @return the post-processed annotation array (or simply the original one)
	 * @since 4.2
	 */
	protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
		return annotations;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MethodParameter)) {
			return false;
		}
		MethodParameter otherParam = (MethodParameter) other;
		return (this.parameterIndex == otherParam.parameterIndex && getMember().equals(otherParam.getMember()));
	}

	@Override
	public int hashCode() {
		return (getMember().hashCode() * 31 + this.parameterIndex);
	}

	@Override
	public String toString() {
		return (this.method != null ? "method '" + this.method.getName() + "'" : "constructor") +
				" parameter " + this.parameterIndex;
	}

	@Override
	public MethodParameter clone() {
		return new MethodParameter(this);
	}


	/**
	 * Create a new MethodParameter for the given method or constructor.
	 * <p>This is a convenience constructor for scenarios where a
	 * Method or Constructor reference is treated in a generic fashion.
	 * <p>
	 *  为给定的方法或构造函数创建一个新的MethodParameter <p>这是一种方便的构造函数,用于以通用方式处理方法或构造函数引用的方案
	 * 
	 * @param methodOrConstructor the Method or Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 * @return the corresponding MethodParameter instance
	 */
	public static MethodParameter forMethodOrConstructor(Object methodOrConstructor, int parameterIndex) {
		if (methodOrConstructor instanceof Method) {
			return new MethodParameter((Method) methodOrConstructor, parameterIndex);
		}
		else if (methodOrConstructor instanceof Constructor) {
			return new MethodParameter((Constructor<?>) methodOrConstructor, parameterIndex);
		}
		else {
			throw new IllegalArgumentException(
					"Given object [" + methodOrConstructor + "] is neither a Method nor a Constructor");
		}
	}

}
