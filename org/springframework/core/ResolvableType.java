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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.springframework.core.SerializableTypeWrapper.FieldTypeProvider;
import org.springframework.core.SerializableTypeWrapper.MethodParameterTypeProvider;
import org.springframework.core.SerializableTypeWrapper.TypeProvider;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Encapsulates a Java {@link java.lang.reflect.Type}, providing access to
 * {@link #getSuperType() supertypes}, {@link #getInterfaces() interfaces}, and
 * {@link #getGeneric(int...) generic parameters} along with the ability to ultimately
 * {@link #resolve() resolve} to a {@link java.lang.Class}.
 *
 * <p>{@code ResolvableTypes} may be obtained from {@link #forField(Field) fields},
 * {@link #forMethodParameter(Method, int) method parameters},
 * {@link #forMethodReturnType(Method) method returns} or
 * {@link #forClass(Class) classes}. Most methods on this class will themselves return
 * {@link ResolvableType}s, allowing easy navigation. For example:
 * <pre class="code">
 * private HashMap&lt;Integer, List&lt;String&gt;&gt; myMap;
 *
 * public void example() {
 *     ResolvableType t = ResolvableType.forField(getClass().getDeclaredField("myMap"));
 *     t.getSuperType(); // AbstractMap&lt;Integer, List&lt;String&gt;&gt;
 *     t.asMap(); // Map&lt;Integer, List&lt;String&gt;&gt;
 *     t.getGeneric(0).resolve(); // Integer
 *     t.getGeneric(1).resolve(); // List
 *     t.getGeneric(1); // List&lt;String&gt;
 *     t.resolveGeneric(1, 0); // String
 * }
 * </pre>
 *
 * <p>
 * 封装Java {@link javalangreflectType},提供对{@link #getSuperType()supertypes},{@link #getInterfaces()接口}和{@link #getGeneric(int)通用参数}
 * 的访问以及最终的能力{@link #resolve()resolve}到{@link javalangClass}。
 * 
 *  可以从{@link #forField(Field)fields},{@link #forMethodParameter(Method,int)方法参数} {@link #forMethodReturnType(Method))返回的{@link ResolvableTypes}
 * 方法返回}或{ @link #forClass(Class)classes}这个类上的大多数方法都将返回{@link ResolvableType},允许轻松导航例如：。
 * <pre class="code">
 *  private HashMap&lt; Integer,List&String&gt;&gt; MYMAP;
 * 
 * public void example(){ResolvableType t = ResolvableTypeforField(getClass()getDeclaredField("myMap")); tgetSuperType(); // AbstractMap&lt; Integer,List&lt; String&gt;&gt; tasMap(); // Map&lt; Integer,List&lt; String&gt;&gt; tgetGeneric(0)解析(); //整数tgetGeneric(1)resolve(); // List tgetGeneric(1); // List&lt; String&gt; tresolveGeneric(1,0); // String}
 * 。
 * </pre>
 * 
 * 
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 4.0
 * @see #forField(Field)
 * @see #forMethodParameter(Method, int)
 * @see #forMethodReturnType(Method)
 * @see #forConstructorParameter(Constructor, int)
 * @see #forClass(Class)
 * @see #forType(Type)
 * @see #forInstance(Object)
 * @see ResolvableTypeProvider
 */
@SuppressWarnings("serial")
public class ResolvableType implements Serializable {

	/**
	 * {@code ResolvableType} returned when no value is available. {@code NONE} is used
	 * in preference to {@code null} so that multiple method calls can be safely chained.
	 * <p>
	 *  没有值可用时返回{@code ResolvableType} {@code NONE}优先使用{@code null},以便多个方法调用可以安全地链接
	 * 
	 */
	public static final ResolvableType NONE = new ResolvableType(null, null, null, 0);

	private static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];

	private static final ConcurrentReferenceHashMap<ResolvableType, ResolvableType> cache =
			new ConcurrentReferenceHashMap<ResolvableType, ResolvableType>(256);


	/**
	 * The underlying Java type being managed (only ever {@code null} for {@link #NONE}).
	 * <p>
	 *  正在管理的底层Java类型({@link #NONE}仅限{@code null})
	 * 
	 */
	private final Type type;

	/**
	 * Optional provider for the type.
	 * <p>
	 *  该类型的可选提供程序
	 * 
	 */
	private final TypeProvider typeProvider;

	/**
	 * The {@code VariableResolver} to use or {@code null} if no resolver is available.
	 * <p>
	 *  如果没有解析器可用,则使用{@code VariableResolver}或{@code null}
	 * 
	 */
	private final VariableResolver variableResolver;

	/**
	 * The component type for an array or {@code null} if the type should be deduced.
	 * <p>
	 * 数组的组件类型或{@code null},如果该类型应该被推导出来
	 * 
	 */
	private final ResolvableType componentType;

	/**
	 * Copy of the resolved value.
	 * <p>
	 *  复制已解析的值
	 * 
	 */
	private final Class<?> resolved;

	private final Integer hash;

	private ResolvableType superType;

	private ResolvableType[] interfaces;

	private ResolvableType[] generics;


	/**
	 * Private constructor used to create a new {@link ResolvableType} for cache key purposes,
	 * with no upfront resolution.
	 * <p>
	 *  私有构造函数用于为缓存关键目的创建一个新的{@link ResolvableType},没有预先分辨率
	 * 
	 */
	private ResolvableType(Type type, TypeProvider typeProvider, VariableResolver variableResolver) {
		this.type = type;
		this.typeProvider = typeProvider;
		this.variableResolver = variableResolver;
		this.componentType = null;
		this.resolved = null;
		this.hash = calculateHashCode();
	}

	/**
	 * Private constructor used to create a new {@link ResolvableType} for cache value purposes,
	 * with upfront resolution and a pre-calculated hash.
	 * <p>
	 *  用于为缓存值目的创建一个新的{@link ResolvableType}的私有构造函数,具有前置分辨率和预先计算的散列
	 * 
	 * 
	 * @since 4.2
	 */
	private ResolvableType(Type type, TypeProvider typeProvider, VariableResolver variableResolver, Integer hash) {
		this.type = type;
		this.typeProvider = typeProvider;
		this.variableResolver = variableResolver;
		this.componentType = null;
		this.resolved = resolveClass();
		this.hash = hash;
	}

	/**
	 * Private constructor used to create a new {@link ResolvableType} for uncached purposes,
	 * with upfront resolution but lazily calculated hash.
	 * <p>
	 *  私有构造函数用于为非缓存目的创建一个新的{@link ResolvableType},具有前期分辨率但是延迟计算的散列
	 * 
	 */
	private ResolvableType(
			Type type, TypeProvider typeProvider, VariableResolver variableResolver, ResolvableType componentType) {

		this.type = type;
		this.typeProvider = typeProvider;
		this.variableResolver = variableResolver;
		this.componentType = componentType;
		this.resolved = resolveClass();
		this.hash = null;
	}

	/**
	 * Private constructor used to create a new {@link ResolvableType} on a {@link Class} basis.
	 * Avoids all {@code instanceof} checks in order to create a straight {@link Class} wrapper.
	 * <p>
	 *  用于在{@link Class}基础上创建新的{@link ResolvableType}的私有构造函数避免所有{@code instanceof}检查,以创建一个直线{@link Class}包装器
	 * 。
	 * 
	 * 
	 * @since 4.2
	 */
	private ResolvableType(Class<?> sourceClass) {
		this.resolved = (sourceClass != null ? sourceClass : Object.class);
		this.type = this.resolved;
		this.typeProvider = null;
		this.variableResolver = null;
		this.componentType = null;
		this.hash = null;
	}


	/**
	 * Return the underling Java {@link Type} being managed. With the exception of
	 * the {@link #NONE} constant, this method will never return {@code null}.
	 * <p>
	 * 返回正在管理的底层Java {@link Type}除{@link #NONE}常量外,此方法将永远不会返回{@code null}
	 * 
	 */
	public Type getType() {
		return SerializableTypeWrapper.unwrap(this.type);
	}

	/**
	 * Return the underlying Java {@link Class} being managed, if available;
	 * otherwise {@code null}.
	 * <p>
	 *  返回正在管理的底层Java {@link Class}(如果可用)否则{@code null}
	 * 
	 */
	public Class<?> getRawClass() {
		if (this.type == this.resolved) {
			return this.resolved;
		}
		Type rawType = this.type;
		if (rawType instanceof ParameterizedType) {
			rawType = ((ParameterizedType) rawType).getRawType();
		}
		return (rawType instanceof Class ? (Class<?>) rawType : null);
	}

	/**
	 * Return the underlying source of the resolvable type. Will return a {@link Field},
	 * {@link MethodParameter} or {@link Type} depending on how the {@link ResolvableType}
	 * was constructed. With the exception of the {@link #NONE} constant, this method will
	 * never return {@code null}. This method is primarily to provide access to additional
	 * type information or meta-data that alternative JVM languages may provide.
	 * <p>
	 *  返回可解析类型的基础源将根据{@link ResolvableType}的构造方式返回{@link Field},{@link MethodParameter}或{@link Type}除了{@link #NONE}
	 * 常量,此方法将永远不会返回{@code null}此方法主要用于访问替代JVM语言可能提供的其他类型信息或元数据。
	 * 
	 */
	public Object getSource() {
		Object source = (this.typeProvider != null ? this.typeProvider.getSource() : null);
		return (source != null ? source : this.type);
	}

	/**
	 * Determine whether the given object is an instance of this {@code ResolvableType}.
	 * <p>
	 *  确定给定对象是否为此{@code ResolvableType}的实例
	 * 
	 * 
	 * @param obj the object to check
	 * @since 4.2
	 * @see #isAssignableFrom(Class)
	 */
	public boolean isInstance(Object obj) {
		return (obj != null && isAssignableFrom(obj.getClass()));
	}

	/**
	 * Determine whether this {@code ResolvableType} is assignable from the
	 * specified other type.
	 * <p>
	 * 确定此{@code ResolvableType}是否可以从指定的其他类型分配
	 * 
	 * 
	 * @param other the type to be checked against (as a {@code Class})
	 * @since 4.2
	 * @see #isAssignableFrom(ResolvableType)
	 */
	public boolean isAssignableFrom(Class<?> other) {
		return isAssignableFrom(forClass(other), null);
	}

	/**
	 * Determine whether this {@code ResolvableType} is assignable from the
	 * specified other type.
	 * <p>Attempts to follow the same rules as the Java compiler, considering
	 * whether both the {@link #resolve() resolved} {@code Class} is
	 * {@link Class#isAssignableFrom(Class) assignable from} the given type
	 * as well as whether all {@link #getGenerics() generics} are assignable.
	 * <p>
	 *  确定此{@code ResolvableType}是否可以从指定的其他类型分配<p>尝试遵循与Java编译器相同的规则,考虑{@link #resolve()已解决} {@code Class}是否为
	 * {@链接类#isAssignableFrom(Class)可以从}给定的类型以及是否所有{@link #getGenerics()泛型}可分配。
	 * 
	 * 
	 * @param other the type to be checked against (as a {@code ResolvableType})
	 * @return {@code true} if the specified other type can be assigned to this
	 * {@code ResolvableType}; {@code false} otherwise
	 */
	public boolean isAssignableFrom(ResolvableType other) {
		return isAssignableFrom(other, null);
	}

	private boolean isAssignableFrom(ResolvableType other, Map<Type, Type> matchedBefore) {
		Assert.notNull(other, "ResolvableType must not be null");

		// If we cannot resolve types, we are not assignable
		if (this == NONE || other == NONE) {
			return false;
		}

		// Deal with array by delegating to the component type
		if (isArray()) {
			return (other.isArray() && getComponentType().isAssignableFrom(other.getComponentType()));
		}

		if (matchedBefore != null && matchedBefore.get(this.type) == other.type) {
			return true;
		}

		// Deal with wildcard bounds
		WildcardBounds ourBounds = WildcardBounds.get(this);
		WildcardBounds typeBounds = WildcardBounds.get(other);

		// In the form X is assignable to <? extends Number>
		if (typeBounds != null) {
			return (ourBounds != null && ourBounds.isSameKind(typeBounds) &&
					ourBounds.isAssignableFrom(typeBounds.getBounds()));
		}

		// In the form <? extends Number> is assignable to X...
		if (ourBounds != null) {
			return ourBounds.isAssignableFrom(other);
		}

		// Main assignability check about to follow
		boolean exactMatch = (matchedBefore != null);  // We're checking nested generic variables now...
		boolean checkGenerics = true;
		Class<?> ourResolved = null;
		if (this.type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) this.type;
			// Try default variable resolution
			if (this.variableResolver != null) {
				ResolvableType resolved = this.variableResolver.resolveVariable(variable);
				if (resolved != null) {
					ourResolved = resolved.resolve();
				}
			}
			if (ourResolved == null) {
				// Try variable resolution against target type
				if (other.variableResolver != null) {
					ResolvableType resolved = other.variableResolver.resolveVariable(variable);
					if (resolved != null) {
						ourResolved = resolved.resolve();
						checkGenerics = false;
					}
				}
			}
			if (ourResolved == null) {
				// Unresolved type variable, potentially nested -> never insist on exact match
				exactMatch = false;
			}
		}
		if (ourResolved == null) {
			ourResolved = resolve(Object.class);
		}
		Class<?> otherResolved = other.resolve(Object.class);

		// We need an exact type match for generics
		// List<CharSequence> is not assignable from List<String>
		if (exactMatch ? !ourResolved.equals(otherResolved) : !ClassUtils.isAssignable(ourResolved, otherResolved)) {
			return false;
		}

		if (checkGenerics) {
			// Recursively check each generic
			ResolvableType[] ourGenerics = getGenerics();
			ResolvableType[] typeGenerics = other.as(ourResolved).getGenerics();
			if (ourGenerics.length != typeGenerics.length) {
				return false;
			}
			if (matchedBefore == null) {
				matchedBefore = new IdentityHashMap<Type, Type>(1);
			}
			matchedBefore.put(this.type, other.type);
			for (int i = 0; i < ourGenerics.length; i++) {
				if (!ourGenerics[i].isAssignableFrom(typeGenerics[i], matchedBefore)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Return {@code true} if this type resolves to a Class that represents an array.
	 * <p>
	 *  如果此类型解析为表示数组的类,则返回{@code true}
	 * 
	 * 
	 * @see #getComponentType()
	 */
	public boolean isArray() {
		if (this == NONE) {
			return false;
		}
		return (((this.type instanceof Class && ((Class<?>) this.type).isArray())) ||
				this.type instanceof GenericArrayType || resolveType().isArray());
	}

	/**
	 * Return the ResolvableType representing the component type of the array or
	 * {@link #NONE} if this type does not represent an array.
	 * <p>
	 *  如果此类型不表示数组,则返回表示数组的组件类型的ResolvableType或{@link #NONE}
	 * 
	 * 
	 * @see #isArray()
	 */
	public ResolvableType getComponentType() {
		if (this == NONE) {
			return NONE;
		}
		if (this.componentType != null) {
			return this.componentType;
		}
		if (this.type instanceof Class) {
			Class<?> componentType = ((Class<?>) this.type).getComponentType();
			return forType(componentType, this.variableResolver);
		}
		if (this.type instanceof GenericArrayType) {
			return forType(((GenericArrayType) this.type).getGenericComponentType(), this.variableResolver);
		}
		return resolveType().getComponentType();
	}

	/**
	 * Convenience method to return this type as a resolvable {@link Collection} type.
	 * Returns {@link #NONE} if this type does not implement or extend
	 * {@link Collection}.
	 * <p>
	 * 将此类型返回为可解析的{@link Collection}类型的便捷方法如果此类型未实现或扩展,则返回{@link #NONE} {@link Collection}
	 * 
	 * 
	 * @see #as(Class)
	 * @see #asMap()
	 */
	public ResolvableType asCollection() {
		return as(Collection.class);
	}

	/**
	 * Convenience method to return this type as a resolvable {@link Map} type.
	 * Returns {@link #NONE} if this type does not implement or extend
	 * {@link Map}.
	 * <p>
	 *  将此类型返回为可解析的{@link Map}类型的便捷方法如果此类型未实现或扩展,则返回{@link #NONE} {@link Map}
	 * 
	 * 
	 * @see #as(Class)
	 * @see #asCollection()
	 */
	public ResolvableType asMap() {
		return as(Map.class);
	}

	/**
	 * Return this type as a {@link ResolvableType} of the specified class. Searches
	 * {@link #getSuperType() supertype} and {@link #getInterfaces() interface}
	 * hierarchies to find a match, returning {@link #NONE} if this type does not
	 * implement or extend the specified class.
	 * <p>
	 *  将此类型返回为指定类的{@link ResolvableType}搜索{@link #getSuperType()supertype}和{@link #getInterfaces()interface}
	 * 层次结构以查找匹配,如果此类型返回{@link #NONE}不实现或扩展指定的类。
	 * 
	 * 
	 * @param type the required class type
	 * @return a {@link ResolvableType} representing this object as the specified
	 * type, or {@link #NONE} if not resolvable as that type
	 * @see #asCollection()
	 * @see #asMap()
	 * @see #getSuperType()
	 * @see #getInterfaces()
	 */
	public ResolvableType as(Class<?> type) {
		if (this == NONE) {
			return NONE;
		}
		if (ObjectUtils.nullSafeEquals(resolve(), type)) {
			return this;
		}
		for (ResolvableType interfaceType : getInterfaces()) {
			ResolvableType interfaceAsType = interfaceType.as(type);
			if (interfaceAsType != NONE) {
				return interfaceAsType;
			}
		}
		return getSuperType().as(type);
	}

	/**
	 * Return a {@link ResolvableType} representing the direct supertype of this type.
	 * If no supertype is available this method returns {@link #NONE}.
	 * <p>
	 *  返回表示此类型的直接超类型的{@link ResolvableType}如果没有超类型可用,此方法返回{@link #NONE}
	 * 
	 * 
	 * @see #getInterfaces()
	 */
	public ResolvableType getSuperType() {
		Class<?> resolved = resolve();
		if (resolved == null || resolved.getGenericSuperclass() == null) {
			return NONE;
		}
		if (this.superType == null) {
			this.superType = forType(SerializableTypeWrapper.forGenericSuperclass(resolved), asVariableResolver());
		}
		return this.superType;
	}

	/**
	 * Return a {@link ResolvableType} array representing the direct interfaces
	 * implemented by this type. If this type does not implement any interfaces an
	 * empty array is returned.
	 * <p>
	 * 返回一个表示由此类型实现的直接接口的{@link ResolvableType}数组如果此类型不实现任何接口,则返回一个空数组
	 * 
	 * 
	 * @see #getSuperType()
	 */
	public ResolvableType[] getInterfaces() {
		Class<?> resolved = resolve();
		if (resolved == null || ObjectUtils.isEmpty(resolved.getGenericInterfaces())) {
			return EMPTY_TYPES_ARRAY;
		}
		if (this.interfaces == null) {
			this.interfaces = forTypes(SerializableTypeWrapper.forGenericInterfaces(resolved), asVariableResolver());
		}
		return this.interfaces;
	}

	/**
	 * Return {@code true} if this type contains generic parameters.
	 * <p>
	 *  如果此类型包含通用参数,则返回{@code true}
	 * 
	 * 
	 * @see #getGeneric(int...)
	 * @see #getGenerics()
	 */
	public boolean hasGenerics() {
		return (getGenerics().length > 0);
	}

	/**
	 * Return {@code true} if this type contains unresolvable generics only,
	 * that is, no substitute for any of its declared type variables.
	 * <p>
	 *  如果此类型仅包含不可解析的泛型,则返回{@code true},也就是说,不能替换其声明的任何类型变量
	 * 
	 */
	boolean isEntirelyUnresolvable() {
		if (this == NONE) {
			return false;
		}
		ResolvableType[] generics = getGenerics();
		for (ResolvableType generic : generics) {
			if (!generic.isUnresolvableTypeVariable() && !generic.isWildcardWithoutBounds()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine whether the underlying type has any unresolvable generics:
	 * either through an unresolvable type variable on the type itself
	 * or through implementing a generic interface in a raw fashion,
	 * i.e. without substituting that interface's type variables.
	 * The result will be {@code true} only in those two scenarios.
	 * <p>
	 *  确定底层类型是否具有任何不可解析的泛型：通过类型本身上的一个不可解析的类型变量,或者通过以原始方式实现一个通用接口,即不使用该接口的类型变量,结果将仅在{@code true}中两种情况
	 * 
	 */
	public boolean hasUnresolvableGenerics() {
		if (this == NONE) {
			return false;
		}
		ResolvableType[] generics = getGenerics();
		for (ResolvableType generic : generics) {
			if (generic.isUnresolvableTypeVariable() || generic.isWildcardWithoutBounds()) {
				return true;
			}
		}
		Class<?> resolved = resolve();
		if (resolved != null) {
			for (Type genericInterface : resolved.getGenericInterfaces()) {
				if (genericInterface instanceof Class) {
					if (forClass((Class<?>) genericInterface).hasGenerics()) {
						return true;
					}
				}
			}
			return getSuperType().hasUnresolvableGenerics();
		}
		return false;
	}

	/**
	 * Determine whether the underlying type is a type variable that
	 * cannot be resolved through the associated variable resolver.
	 * <p>
	 * 确定底层类型是否是无法通过关联的变量解析器解析的类型变量
	 * 
	 */
	private boolean isUnresolvableTypeVariable() {
		if (this.type instanceof TypeVariable) {
			if (this.variableResolver == null) {
				return true;
			}
			TypeVariable<?> variable = (TypeVariable<?>) this.type;
			ResolvableType resolved = this.variableResolver.resolveVariable(variable);
			if (resolved == null || resolved.isUnresolvableTypeVariable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine whether the underlying type represents a wildcard
	 * without specific bounds (i.e., equal to {@code ? extends Object}).
	 * <p>
	 *  确定底层类型是否表示没有特定边界的通配符(即等于{@code?extends Object})
	 * 
	 */
	private boolean isWildcardWithoutBounds() {
		if (this.type instanceof WildcardType) {
			WildcardType wt = (WildcardType) this.type;
			if (wt.getLowerBounds().length == 0) {
				Type[] upperBounds = wt.getUpperBounds();
				if (upperBounds.length == 0 || (upperBounds.length == 1 && Object.class == upperBounds[0])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return a {@link ResolvableType} for the specified nesting level. See
	 * {@link #getNested(int, Map)} for details.
	 * <p>
	 *  为指定的嵌套级别返回{@link ResolvableType}有关详细信息,请参阅{@link #getNested(int,Map)}
	 * 
	 * 
	 * @param nestingLevel the nesting level
	 * @return the {@link ResolvableType} type, or {@code #NONE}
	 */
	public ResolvableType getNested(int nestingLevel) {
		return getNested(nestingLevel, null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified nesting level. The nesting level
	 * refers to the specific generic parameter that should be returned. A nesting level
	 * of 1 indicates this type; 2 indicates the first nested generic; 3 the second; and so
	 * on. For example, given {@code List<Set<Integer>>} level 1 refers to the
	 * {@code List}, level 2 the {@code Set}, and level 3 the {@code Integer}.
	 * <p>The {@code typeIndexesPerLevel} map can be used to reference a specific generic
	 * for the given level. For example, an index of 0 would refer to a {@code Map} key;
	 * whereas, 1 would refer to the value. If the map does not contain a value for a
	 * specific level the last generic will be used (e.g. a {@code Map} value).
	 * <p>Nesting levels may also apply to array types; for example given
	 * {@code String[]}, a nesting level of 2 refers to {@code String}.
	 * <p>If a type does not {@link #hasGenerics() contain} generics the
	 * {@link #getSuperType() supertype} hierarchy will be considered.
	 * <p>
	 * 返回指定嵌套级别的{@link ResolvableType}嵌套级别是指应返回的特定通用参数嵌套级别1表示此类型; 2表示第一个嵌套泛型; 3第二;例如,给定{@code List <Set <Integer >>}
	 *  1级指的是{@code列表},第2级为{@code集},级别3为{@code整数} <p> {@code typeIndexesPerLevel}映射可用于引用给定级别的特定通用。
	 * 例如,索引为0将引用{@code Map}键;而1将指代值如果地图不包含特定级别的值,则将使用最后一个泛型(例如{@code Map}值)嵌套级别也可能适用于阵列类型;例如给定{@code String []}
	 * ,嵌套级别2指代{@code String} <p>如果类型不{@link #hasGenerics()包含}泛型{@link #getSuperType()超类型}层次结构将被考虑。
	 * 
	 * 
	 * @param nestingLevel the required nesting level, indexed from 1 for the current
	 * type, 2 for the first nested generic, 3 for the second and so on
	 * @param typeIndexesPerLevel a map containing the generic index for a given nesting
	 * level (may be {@code null})
	 * @return a {@link ResolvableType} for the nested level or {@link #NONE}
	 */
	public ResolvableType getNested(int nestingLevel, Map<Integer, Integer> typeIndexesPerLevel) {
		ResolvableType result = this;
		for (int i = 2; i <= nestingLevel; i++) {
			if (result.isArray()) {
				result = result.getComponentType();
			}
			else {
				// Handle derived types
				while (result != ResolvableType.NONE && !result.hasGenerics()) {
					result = result.getSuperType();
				}
				Integer index = (typeIndexesPerLevel != null ? typeIndexesPerLevel.get(i) : null);
				index = (index == null ? result.getGenerics().length - 1 : index);
				result = result.getGeneric(index);
			}
		}
		return result;
	}

	/**
	 * Return a {@link ResolvableType} representing the generic parameter for the given
	 * indexes. Indexes are zero based; for example given the type
	 * {@code Map<Integer, List<String>>}, {@code getGeneric(0)} will access the
	 * {@code Integer}. Nested generics can be accessed by specifying multiple indexes;
	 * for example {@code getGeneric(1, 0)} will access the {@code String} from the nested
	 * {@code List}. For convenience, if no indexes are specified the first generic is
	 * returned.
	 * <p>If no generic is available at the specified indexes {@link #NONE} is returned.
	 * <p>
	 * 返回表示给定索引的通用参数的{@link ResolvableType}索引为零;例如给定类型{@code Map <Integer,List <String >>},{@code getGeneric(0)}
	 * 将访问{@code Integer}可以通过指定多个索引来访问嵌套泛型;例如{@code getGeneric(1,0)}将从嵌套的{@code List}访问{@code String}为方便起见,如
	 * 果没有指定索引,则返回第一个泛型<p>如果没有可用的通用将返回指定的索引{@link #NONE}。
	 * 
	 * 
	 * @param indexes the indexes that refer to the generic parameter (may be omitted to
	 * return the first generic)
	 * @return a {@link ResolvableType} for the specified generic or {@link #NONE}
	 * @see #hasGenerics()
	 * @see #getGenerics()
	 * @see #resolveGeneric(int...)
	 * @see #resolveGenerics()
	 */
	public ResolvableType getGeneric(int... indexes) {
		try {
			if (indexes == null || indexes.length == 0) {
				return getGenerics()[0];
			}
			ResolvableType generic = this;
			for (int index : indexes) {
				generic = generic.getGenerics()[index];
			}
			return generic;
		}
		catch (IndexOutOfBoundsException ex) {
			return NONE;
		}
	}

	/**
	 * Return an array of {@link ResolvableType}s representing the generic parameters of
	 * this type. If no generics are available an empty array is returned. If you need to
	 * access a specific generic consider using the {@link #getGeneric(int...)} method as
	 * it allows access to nested generics and protects against
	 * {@code IndexOutOfBoundsExceptions}.
	 * <p>
	 * 返回一个表示此类型的通用参数的{@link ResolvableType}数组如果没有泛型可用,则返回一个空数组如果您需要访问特定的通用方法,请考虑使用{@link #getGeneric(int)}方
	 * 法允许访问嵌套泛型,并防止{@code IndexOutOfBoundsExceptions}。
	 * 
	 * 
	 * @return an array of {@link ResolvableType}s representing the generic parameters
	 * (never {@code null})
	 * @see #hasGenerics()
	 * @see #getGeneric(int...)
	 * @see #resolveGeneric(int...)
	 * @see #resolveGenerics()
	 */
	public ResolvableType[] getGenerics() {
		if (this == NONE) {
			return EMPTY_TYPES_ARRAY;
		}
		if (this.generics == null) {
			if (this.type instanceof Class) {
				Class<?> typeClass = (Class<?>) this.type;
				this.generics = forTypes(SerializableTypeWrapper.forTypeParameters(typeClass), this.variableResolver);
			}
			else if (this.type instanceof ParameterizedType) {
				Type[] actualTypeArguments = ((ParameterizedType) this.type).getActualTypeArguments();
				ResolvableType[] generics = new ResolvableType[actualTypeArguments.length];
				for (int i = 0; i < actualTypeArguments.length; i++) {
					generics[i] = forType(actualTypeArguments[i], this.variableResolver);
				}
				this.generics = generics;
			}
			else {
				this.generics = resolveType().getGenerics();
			}
		}
		return this.generics;
	}

	/**
	 * Convenience method that will {@link #getGenerics() get} and
	 * {@link #resolve() resolve} generic parameters.
	 * <p>
	 *  {@link #getGenerics()get}和{@link #resolve()解析}通用参数的便利方法
	 * 
	 * 
	 * @return an array of resolved generic parameters (the resulting array
	 * will never be {@code null}, but it may contain {@code null} elements})
	 * @see #getGenerics()
	 * @see #resolve()
	 */
	public Class<?>[] resolveGenerics() {
		return resolveGenerics(null);
	}

	/**
	 * Convenience method that will {@link #getGenerics() get} and {@link #resolve()
	 * resolve} generic parameters, using the specified {@code fallback} if any type
	 * cannot be resolved.
	 * <p>
	 *  方便的方法将{@link #getGenerics()get}和{@link #resolve()解析}通用参数,使用指定的{@code fallback}如果任何类型无法解析
	 * 
	 * 
	 * @param fallback the fallback class to use if resolution fails (may be {@code null})
	 * @return an array of resolved generic parameters (the resulting array will never be
	 * {@code null}, but it may contain {@code null} elements})
	 * @see #getGenerics()
	 * @see #resolve()
	 */
	public Class<?>[] resolveGenerics(Class<?> fallback) {
		ResolvableType[] generics = getGenerics();
		Class<?>[] resolvedGenerics = new Class<?>[generics.length];
		for (int i = 0; i < generics.length; i++) {
			resolvedGenerics[i] = generics[i].resolve(fallback);
		}
		return resolvedGenerics;
	}

	/**
	 * Convenience method that will {@link #getGeneric(int...) get} and
	 * {@link #resolve() resolve} a specific generic parameters.
	 * <p>
	 *  {@link #getGeneric(int)get}和{@link #resolve()解析}一个特定的通用参数的便利方法
	 * 
	 * 
	 * @param indexes the indexes that refer to the generic parameter
	 * (may be omitted to return the first generic)
	 * @return a resolved {@link Class} or {@code null}
	 * @see #getGeneric(int...)
	 * @see #resolve()
	 */
	public Class<?> resolveGeneric(int... indexes) {
		return getGeneric(indexes).resolve();
	}

	/**
	 * Resolve this type to a {@link java.lang.Class}, returning {@code null}
	 * if the type cannot be resolved. This method will consider bounds of
	 * {@link TypeVariable}s and {@link WildcardType}s if direct resolution fails;
	 * however, bounds of {@code Object.class} will be ignored.
	 * <p>
	 * 将此类型解析为{@link javalangClass},如果类型无法解析,则返回{@code null}如果直接解析失败,则此方法将考虑{@link TypeVariable}和{@link WildcardType}
	 * 的边界;然而,{@code Objectclass}的界限将被忽略。
	 * 
	 * 
	 * @return the resolved {@link Class}, or {@code null} if not resolvable
	 * @see #resolve(Class)
	 * @see #resolveGeneric(int...)
	 * @see #resolveGenerics()
	 */
	public Class<?> resolve() {
		return resolve(null);
	}

	/**
	 * Resolve this type to a {@link java.lang.Class}, returning the specified
	 * {@code fallback} if the type cannot be resolved. This method will consider bounds
	 * of {@link TypeVariable}s and {@link WildcardType}s if direct resolution fails;
	 * however, bounds of {@code Object.class} will be ignored.
	 * <p>
	 *  将此类型解析为{@link javalangClass},如果类型无法解析,返回指定的{@code fallback}如果直接解析失败,则此方法将考虑{@link TypeVariable}和{@link WildcardType}
	 * 的边界;然而,{@code Objectclass}的界限将被忽略。
	 * 
	 * 
	 * @param fallback the fallback class to use if resolution fails (may be {@code null})
	 * @return the resolved {@link Class} or the {@code fallback}
	 * @see #resolve()
	 * @see #resolveGeneric(int...)
	 * @see #resolveGenerics()
	 */
	public Class<?> resolve(Class<?> fallback) {
		return (this.resolved != null ? this.resolved : fallback);
	}

	private Class<?> resolveClass() {
		if (this.type instanceof Class || this.type == null) {
			return (Class<?>) this.type;
		}
		if (this.type instanceof GenericArrayType) {
			Class<?> resolvedComponent = getComponentType().resolve();
			return (resolvedComponent != null ? Array.newInstance(resolvedComponent, 0).getClass() : null);
		}
		return resolveType().resolve();
	}

	/**
	 * Resolve this type by a single level, returning the resolved value or {@link #NONE}.
	 * <p>Note: The returned {@link ResolvableType} should only be used as an intermediary
	 * as it cannot be serialized.
	 * <p>
	 *  解析此类型,返回解析值或{@link #NONE} <p>注意：返回的{@link ResolvableType}只能用作中介,因为它不能被序列化
	 * 
	 */
	ResolvableType resolveType() {
		if (this.type instanceof ParameterizedType) {
			return forType(((ParameterizedType) this.type).getRawType(), this.variableResolver);
		}
		if (this.type instanceof WildcardType) {
			Type resolved = resolveBounds(((WildcardType) this.type).getUpperBounds());
			if (resolved == null) {
				resolved = resolveBounds(((WildcardType) this.type).getLowerBounds());
			}
			return forType(resolved, this.variableResolver);
		}
		if (this.type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) this.type;
			// Try default variable resolution
			if (this.variableResolver != null) {
				ResolvableType resolved = this.variableResolver.resolveVariable(variable);
				if (resolved != null) {
					return resolved;
				}
			}
			// Fallback to bounds
			return forType(resolveBounds(variable.getBounds()), this.variableResolver);
		}
		return NONE;
	}

	private Type resolveBounds(Type[] bounds) {
		if (ObjectUtils.isEmpty(bounds) || Object.class == bounds[0]) {
			return null;
		}
		return bounds[0];
	}

	private ResolvableType resolveVariable(TypeVariable<?> variable) {
		if (this.type instanceof TypeVariable) {
			return resolveType().resolveVariable(variable);
		}
		if (this.type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) this.type;
			TypeVariable<?>[] variables = resolve().getTypeParameters();
			for (int i = 0; i < variables.length; i++) {
				if (ObjectUtils.nullSafeEquals(variables[i].getName(), variable.getName())) {
					Type actualType = parameterizedType.getActualTypeArguments()[i];
					return forType(actualType, this.variableResolver);
				}
			}
			if (parameterizedType.getOwnerType() != null) {
				return forType(parameterizedType.getOwnerType(), this.variableResolver).resolveVariable(variable);
			}
		}
		if (this.variableResolver != null) {
			return this.variableResolver.resolveVariable(variable);
		}
		return null;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ResolvableType)) {
			return false;
		}

		ResolvableType otherType = (ResolvableType) other;
		if (!ObjectUtils.nullSafeEquals(this.type, otherType.type)) {
			return false;
		}
		if (this.typeProvider != otherType.typeProvider &&
				(this.typeProvider == null || otherType.typeProvider == null ||
				!ObjectUtils.nullSafeEquals(this.typeProvider.getSource(), otherType.typeProvider.getSource()))) {
			return false;
		}
		if (this.variableResolver != otherType.variableResolver &&
				(this.variableResolver == null || otherType.variableResolver == null ||
				!ObjectUtils.nullSafeEquals(this.variableResolver.getSource(), otherType.variableResolver.getSource()))) {
			return false;
		}
		if (!ObjectUtils.nullSafeEquals(this.componentType, otherType.componentType)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return (this.hash != null ? this.hash : calculateHashCode());
	}

	private int calculateHashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(this.type);
		if (this.typeProvider != null) {
			hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.typeProvider.getSource());
		}
		if (this.variableResolver != null) {
			hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.variableResolver.getSource());
		}
		if (this.componentType != null) {
			hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.componentType);
		}
		return hashCode;
	}

	/**
	 * Adapts this {@link ResolvableType} to a {@link VariableResolver}.
	 * <p>
	 * 将{@link ResolvableType}适用于{@link VariableResolver}
	 * 
	 */
	VariableResolver asVariableResolver() {
		if (this == NONE) {
			return null;
		}
		return new DefaultVariableResolver();
	}

	/**
	 * Custom serialization support for {@link #NONE}.
	 * <p>
	 *  {@link #NONE}的自定义序列化支持
	 * 
	 */
	private Object readResolve() {
		return (this.type == null ? NONE : this);
	}

	/**
	 * Return a String representation of this type in its fully resolved form
	 * (including any generic parameters).
	 * <p>
	 *  以完全解析的形式(包括任何通用参数)返回此类型的字符串表示形式
	 * 
	 */
	@Override
	public String toString() {
		if (isArray()) {
			return getComponentType() + "[]";
		}
		if (this.resolved == null) {
			return "?";
		}
		if (this.type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) this.type;
			if (this.variableResolver == null || this.variableResolver.resolveVariable(variable) == null) {
				// Don't bother with variable boundaries for toString()...
				// Can cause infinite recursions in case of self-references
				return "?";
			}
		}
		StringBuilder result = new StringBuilder(this.resolved.getName());
		if (hasGenerics()) {
			result.append('<');
			result.append(StringUtils.arrayToDelimitedString(getGenerics(), ", "));
			result.append('>');
		}
		return result.toString();
	}


	// Factory methods

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class},
	 * using the full generic type information for assignability checks.
	 * For example: {@code ResolvableType.forClass(MyArrayList.class)}.
	 * <p>
	 *  为指定的{@link类}返回{@link ResolvableType},使用完整的通用类型信息进行可分配性检查例如：{@code ResolvableTypeforClass(MyArrayListclass)}
	 * 。
	 * 
	 * 
	 * @param sourceClass the source class ({@code null} is semantically
	 * equivalent to {@code Object.class} for typical use cases here}
	 * @return a {@link ResolvableType} for the specified class
	 * @see #forClass(Class, Class)
	 * @see #forClassWithGenerics(Class, Class...)
	 */
	public static ResolvableType forClass(Class<?> sourceClass) {
		return new ResolvableType(sourceClass);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class}, doing
	 * assignability checks against the raw class only (analogous to
	 * {@link Class#isAssignableFrom}, which this serves as a wrapper for.
	 * For example: {@code ResolvableType.forRawClass(List.class)}.
	 * <p>
	 *  对于指定的{@link类}返回一个{@link ResolvableType},只对原始类进行可分配性检查(类似于{@link Class#isAssignableFrom},它用作包装器)例如：{@code ResolvableTypeforRawClass Listclass)}
	 * 。
	 * 
	 * 
	 * @param sourceClass the source class ({@code null} is semantically
	 * equivalent to {@code Object.class} for typical use cases here}
	 * @return a {@link ResolvableType} for the specified class
	 * @since 4.2
	 * @see #forClass(Class)
	 * @see #getRawClass()
	 */
	public static ResolvableType forRawClass(Class<?> sourceClass) {
		return new ResolvableType(sourceClass) {
			@Override
			public boolean isAssignableFrom(Class<?> other) {
				return ClassUtils.isAssignable(getRawClass(), other);
			}
			@Override
			public boolean isAssignableFrom(ResolvableType other) {
				Class<?> otherClass = other.getRawClass();
				return (otherClass != null && ClassUtils.isAssignable(getRawClass(), otherClass));
			}
		};
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class}
	 * with a given implementation.
	 * For example: {@code ResolvableType.forClass(List.class, MyArrayList.class)}.
	 * <p>
	 * 为给定的实现返回指定的{@link类}的{@link ResolvableType}例如：{@code ResolvableTypeforClass(Listclass,MyArrayListclass)}
	 * 。
	 * 
	 * 
	 * @param sourceClass the source class (must not be {@code null}
	 * @param implementationClass the implementation class
	 * @return a {@link ResolvableType} for the specified class backed by the given
	 * implementation class
	 * @see #forClass(Class)
	 * @see #forClassWithGenerics(Class, Class...)
	 */
	public static ResolvableType forClass(Class<?> sourceClass, Class<?> implementationClass) {
		Assert.notNull(sourceClass, "Source class must not be null");
		ResolvableType asType = forType(implementationClass).as(sourceClass);
		return (asType == NONE ? forType(sourceClass) : asType);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class} with pre-declared generics.
	 * <p>
	 *  使用预先声明的泛型返回指定的{@link Class}的{@link ResolvableType}
	 * 
	 * 
	 * @param sourceClass the source class
	 * @param generics the generics of the class
	 * @return a {@link ResolvableType} for the specific class and generics
	 * @see #forClassWithGenerics(Class, ResolvableType...)
	 */
	public static ResolvableType forClassWithGenerics(Class<?> sourceClass, Class<?>... generics) {
		Assert.notNull(sourceClass, "Source class must not be null");
		Assert.notNull(generics, "Generics must not be null");
		ResolvableType[] resolvableGenerics = new ResolvableType[generics.length];
		for (int i = 0; i < generics.length; i++) {
			resolvableGenerics[i] = forClass(generics[i]);
		}
		return forClassWithGenerics(sourceClass, resolvableGenerics);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class} with pre-declared generics.
	 * <p>
	 *  使用预先声明的泛型返回指定的{@link Class}的{@link ResolvableType}
	 * 
	 * 
	 * @param sourceClass the source class
	 * @param generics the generics of the class
	 * @return a {@link ResolvableType} for the specific class and generics
	 * @see #forClassWithGenerics(Class, Class...)
	 */
	public static ResolvableType forClassWithGenerics(Class<?> sourceClass, ResolvableType... generics) {
		Assert.notNull(sourceClass, "Source class must not be null");
		Assert.notNull(generics, "Generics must not be null");
		TypeVariable<?>[] variables = sourceClass.getTypeParameters();
		Assert.isTrue(variables.length == generics.length, "Mismatched number of generics specified");

		Type[] arguments = new Type[generics.length];
		for (int i = 0; i < generics.length; i++) {
			ResolvableType generic = generics[i];
			Type argument = (generic != null ? generic.getType() : null);
			arguments[i] = (argument != null ? argument : variables[i]);
		}

		ParameterizedType syntheticType = new SyntheticParameterizedType(sourceClass, arguments);
		return forType(syntheticType, new TypeVariablesVariableResolver(variables, generics));
	}

	/**
	 * Return a {@link ResolvableType} for the specified instance. The instance does not
	 * convey generic information but if it implements {@link ResolvableTypeProvider} a
	 * more precise {@link ResolvableType} can be used than the simple one based on
	 * the {@link #forClass(Class) Class instance}.
	 * <p>
	 *  为指定的实例返回{@link ResolvableType}该实例不传达通用信息,但如果实现{@link ResolvableTypeProvider},则可以使用比{@link #forClass更简单的{@link ResolvableType}
	 *  (Class)类实例}。
	 * 
	 * 
	 * @param instance the instance
	 * @return a {@link ResolvableType} for the specified instance
	 * @since 4.2
	 * @see ResolvableTypeProvider
	 */
	public static ResolvableType forInstance(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		if (instance instanceof ResolvableTypeProvider) {
			ResolvableType type = ((ResolvableTypeProvider) instance).getResolvableType();
			if (type != null) {
				return type;
			}
		}
		return ResolvableType.forClass(instance.getClass());
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field}.
	 * <p>
	 *  为指定的{@link字段}返回{@link ResolvableType}
	 * 
	 * 
	 * @param field the source field
	 * @return a {@link ResolvableType} for the specified field
	 * @see #forField(Field, Class)
	 */
	public static ResolvableType forField(Field field) {
		Assert.notNull(field, "Field must not be null");
		return forType(null, new FieldTypeProvider(field), null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field} with a given
	 * implementation.
	 * <p>Use this variant when the class that declares the field includes generic
	 * parameter variables that are satisfied by the implementation class.
	 * <p>
	 * 使用给定的实现为指定的{@link Field}返回一个{@link ResolvableType} <p>当声明该字段的类包含实现类满足的通用参数变量时,使用此变体
	 * 
	 * 
	 * @param field the source field
	 * @param implementationClass the implementation class
	 * @return a {@link ResolvableType} for the specified field
	 * @see #forField(Field)
	 */
	public static ResolvableType forField(Field field, Class<?> implementationClass) {
		Assert.notNull(field, "Field must not be null");
		ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
		return forType(null, new FieldTypeProvider(field), owner.asVariableResolver());
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field} with a given
	 * implementation.
	 * <p>Use this variant when the class that declares the field includes generic
	 * parameter variables that are satisfied by the implementation type.
	 * <p>
	 *  使用给定的实现返回指定的{@link Field}的{@link ResolvableType} <p>当声明该字段的类包含实现类型满足的通用参数变量时,使用此变体
	 * 
	 * 
	 * @param field the source field
	 * @param implementationType the implementation type
	 * @return a {@link ResolvableType} for the specified field
	 * @see #forField(Field)
	 */
	public static ResolvableType forField(Field field, ResolvableType implementationType) {
		Assert.notNull(field, "Field must not be null");
		implementationType = (implementationType == null ? NONE : implementationType);
		ResolvableType owner = implementationType.as(field.getDeclaringClass());
		return forType(null, new FieldTypeProvider(field), owner.asVariableResolver());
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field} with the
	 * given nesting level.
	 * <p>
	 *  使用给定的嵌套级别为指定的{@link字段}返回{@link ResolvableType}
	 * 
	 * 
	 * @param field the source field
	 * @param nestingLevel the nesting level (1 for the outer level; 2 for a nested
	 * generic type; etc)
	 * @see #forField(Field)
	 */
	public static ResolvableType forField(Field field, int nestingLevel) {
		Assert.notNull(field, "Field must not be null");
		return forType(null, new FieldTypeProvider(field), null).getNested(nestingLevel);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field} with a given
	 * implementation and the given nesting level.
	 * <p>Use this variant when the class that declares the field includes generic
	 * parameter variables that are satisfied by the implementation class.
	 * <p>
	 * 使用给定的实现和给定的嵌套级别为指定的{@link Field}返回{@link ResolvableType} <p>当声明该字段的类包含实现类满足的一般参数变量时,使用此变体
	 * 
	 * 
	 * @param field the source field
	 * @param nestingLevel the nesting level (1 for the outer level; 2 for a nested
	 * generic type; etc)
	 * @param implementationClass the implementation class
	 * @return a {@link ResolvableType} for the specified field
	 * @see #forField(Field)
	 */
	public static ResolvableType forField(Field field, int nestingLevel, Class<?> implementationClass) {
		Assert.notNull(field, "Field must not be null");
		ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
		return forType(null, new FieldTypeProvider(field), owner.asVariableResolver()).getNested(nestingLevel);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Constructor} parameter.
	 * <p>
	 *  为指定的{@link构造函数}参数返回{@link ResolvableType}
	 * 
	 * 
	 * @param constructor the source constructor (must not be {@code null})
	 * @param parameterIndex the parameter index
	 * @return a {@link ResolvableType} for the specified constructor parameter
	 * @see #forConstructorParameter(Constructor, int, Class)
	 */
	public static ResolvableType forConstructorParameter(Constructor<?> constructor, int parameterIndex) {
		Assert.notNull(constructor, "Constructor must not be null");
		return forMethodParameter(new MethodParameter(constructor, parameterIndex));
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Constructor} parameter
	 * with a given implementation. Use this variant when the class that declares the
	 * constructor includes generic parameter variables that are satisfied by the
	 * implementation class.
	 * <p>
	 *  为给定的实现返回指定的{@link构造函数}参数的{@link ResolvableType}当声明构造函数的类包含实现类满足的通用参数变量时,使用此变体
	 * 
	 * 
	 * @param constructor the source constructor (must not be {@code null})
	 * @param parameterIndex the parameter index
	 * @param implementationClass the implementation class
	 * @return a {@link ResolvableType} for the specified constructor parameter
	 * @see #forConstructorParameter(Constructor, int)
	 */
	public static ResolvableType forConstructorParameter(Constructor<?> constructor, int parameterIndex,
			Class<?> implementationClass) {

		Assert.notNull(constructor, "Constructor must not be null");
		MethodParameter methodParameter = new MethodParameter(constructor, parameterIndex);
		methodParameter.setContainingClass(implementationClass);
		return forMethodParameter(methodParameter);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Method} return type.
	 * <p>
	 *  为指定的{@link Method}返回类型返回{@link ResolvableType}
	 * 
	 * 
	 * @param method the source for the method return type
	 * @return a {@link ResolvableType} for the specified method return
	 * @see #forMethodReturnType(Method, Class)
	 */
	public static ResolvableType forMethodReturnType(Method method) {
		Assert.notNull(method, "Method must not be null");
		return forMethodParameter(new MethodParameter(method, -1));
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Method} return type.
	 * Use this variant when the class that declares the method includes generic
	 * parameter variables that are satisfied by the implementation class.
	 * <p>
	 * 为指定的{@link Method}返回类型返回{@link ResolvableType}当声明方法的类包含实现类满足的通用参数变量时,使用此变体
	 * 
	 * 
	 * @param method the source for the method return type
	 * @param implementationClass the implementation class
	 * @return a {@link ResolvableType} for the specified method return
	 * @see #forMethodReturnType(Method)
	 */
	public static ResolvableType forMethodReturnType(Method method, Class<?> implementationClass) {
		Assert.notNull(method, "Method must not be null");
		MethodParameter methodParameter = new MethodParameter(method, -1);
		methodParameter.setContainingClass(implementationClass);
		return forMethodParameter(methodParameter);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Method} parameter.
	 * <p>
	 *  为指定的{@link Method}参数返回{@link ResolvableType}
	 * 
	 * 
	 * @param method the source method (must not be {@code null})
	 * @param parameterIndex the parameter index
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(Method, int, Class)
	 * @see #forMethodParameter(MethodParameter)
	 */
	public static ResolvableType forMethodParameter(Method method, int parameterIndex) {
		Assert.notNull(method, "Method must not be null");
		return forMethodParameter(new MethodParameter(method, parameterIndex));
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Method} parameter with a
	 * given implementation. Use this variant when the class that declares the method
	 * includes generic parameter variables that are satisfied by the implementation class.
	 * <p>
	 *  使用给定的实现为指定的{@link Method}参数返回{@link ResolvableType}当声明方法的类包含实现类满足的通用参数变量时,使用此变体
	 * 
	 * 
	 * @param method the source method (must not be {@code null})
	 * @param parameterIndex the parameter index
	 * @param implementationClass the implementation class
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(Method, int, Class)
	 * @see #forMethodParameter(MethodParameter)
	 */
	public static ResolvableType forMethodParameter(Method method, int parameterIndex, Class<?> implementationClass) {
		Assert.notNull(method, "Method must not be null");
		MethodParameter methodParameter = new MethodParameter(method, parameterIndex);
		methodParameter.setContainingClass(implementationClass);
		return forMethodParameter(methodParameter);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link MethodParameter}.
	 * <p>
	 *  为指定的{@link MethodParameter}返回{@link ResolvableType}
	 * 
	 * 
	 * @param methodParameter the source method parameter (must not be {@code null})
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(Method, int)
	 */
	public static ResolvableType forMethodParameter(MethodParameter methodParameter) {
		return forMethodParameter(methodParameter, (Type) null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link MethodParameter} with a
	 * given implementation type. Use this variant when the class that declares the method
	 * includes generic parameter variables that are satisfied by the implementation type.
	 * <p>
	 * 使用给定的实现类型为指定的{@link MethodParameter}返回{@link ResolvableType}当声明方法的类包含实现类型满足的通用参数变量时,使用此变体
	 * 
	 * 
	 * @param methodParameter the source method parameter (must not be {@code null})
	 * @param implementationType the implementation type
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(MethodParameter)
	 */
	public static ResolvableType forMethodParameter(MethodParameter methodParameter, ResolvableType implementationType) {
		Assert.notNull(methodParameter, "MethodParameter must not be null");
		implementationType = (implementationType != null ? implementationType :
				forType(methodParameter.getContainingClass()));
		ResolvableType owner = implementationType.as(methodParameter.getDeclaringClass());
		return forType(null, new MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).
				getNested(methodParameter.getNestingLevel(), methodParameter.typeIndexesPerLevel);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link MethodParameter},
	 * overriding the target type to resolve with a specific given type.
	 * <p>
	 *  为指定的{@link MethodParameter}返回{@link ResolvableType},覆盖目标类型以使用特定的给定类型进行解析
	 * 
	 * 
	 * @param methodParameter the source method parameter (must not be {@code null})
	 * @param targetType the type to resolve (a part of the method parameter's type)
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(Method, int)
	 */
	public static ResolvableType forMethodParameter(MethodParameter methodParameter, Type targetType) {
		Assert.notNull(methodParameter, "MethodParameter must not be null");
		ResolvableType owner = forType(methodParameter.getContainingClass()).as(methodParameter.getDeclaringClass());
		return forType(targetType, new MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).
				getNested(methodParameter.getNestingLevel(), methodParameter.typeIndexesPerLevel);
	}

	/**
	 * Resolve the top-level parameter type of the given {@code MethodParameter}.
	 * <p>
	 *  解决给定的{@code MethodParameter}的顶级参数类型
	 * 
	 * 
	 * @param methodParameter the method parameter to resolve
	 * @since 4.1.9
	 * @see MethodParameter#setParameterType
	 */
	static void resolveMethodParameter(MethodParameter methodParameter) {
		Assert.notNull(methodParameter, "MethodParameter must not be null");
		ResolvableType owner = forType(methodParameter.getContainingClass()).as(methodParameter.getDeclaringClass());
		methodParameter.setParameterType(
				forType(null, new MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).resolve());
	}

	/**
	 * Return a {@link ResolvableType} as a array of the specified {@code componentType}.
	 * <p>
	 *  将{@link ResolvableType}返回为指定的{@code componentType}的数组,
	 * 
	 * 
	 * @param componentType the component type
	 * @return a {@link ResolvableType} as an array of the specified component type
	 */
	public static ResolvableType forArrayComponent(ResolvableType componentType) {
		Assert.notNull(componentType, "Component type must not be null");
		Class<?> arrayClass = Array.newInstance(componentType.resolve(), 0).getClass();
		return new ResolvableType(arrayClass, null, null, componentType);
	}

	private static ResolvableType[] forTypes(Type[] types, VariableResolver owner) {
		ResolvableType[] result = new ResolvableType[types.length];
		for (int i = 0; i < types.length; i++) {
			result[i] = forType(types[i], owner);
		}
		return result;
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Type}.
	 * Note: The resulting {@link ResolvableType} may not be {@link Serializable}.
	 * <p>
	 *  为指定的{@link类型}返回{@link ResolvableType}注意：生成的{@link ResolvableType}可能不是{@link Serializable}
	 * 
	 * 
	 * @param type the source type or {@code null}
	 * @return a {@link ResolvableType} for the specified {@link Type}
	 * @see #forType(Type, ResolvableType)
	 */
	public static ResolvableType forType(Type type) {
		return forType(type, null, null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Type} backed by the given
	 * owner type. Note: The resulting {@link ResolvableType} may not be {@link Serializable}.
	 * <p>
	 * 返回由给定所有者类型支持的指定的{@link类型}的{@link ResolvableType}注意：生成的{@link ResolvableType}可能不是{@link可序列化}
	 * 
	 * 
	 * @param type the source type or {@code null}
	 * @param owner the owner type used to resolve variables
	 * @return a {@link ResolvableType} for the specified {@link Type} and owner
	 * @see #forType(Type)
	 */
	public static ResolvableType forType(Type type, ResolvableType owner) {
		VariableResolver variableResolver = null;
		if (owner != null) {
			variableResolver = owner.asVariableResolver();
		}
		return forType(type, variableResolver);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Type} backed by a given
	 * {@link VariableResolver}.
	 * <p>
	 *  为给定的{@link VariableResolver}支持的指定的{@link Type}返回{@link ResolvableType}
	 * 
	 * 
	 * @param type the source type or {@code null}
	 * @param variableResolver the variable resolver or {@code null}
	 * @return a {@link ResolvableType} for the specified {@link Type} and {@link VariableResolver}
	 */
	static ResolvableType forType(Type type, VariableResolver variableResolver) {
		return forType(type, null, variableResolver);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Type} backed by a given
	 * {@link VariableResolver}.
	 * <p>
	 *  为给定的{@link VariableResolver}支持的指定的{@link Type}返回{@link ResolvableType}
	 * 
	 * 
	 * @param type the source type or {@code null}
	 * @param typeProvider the type provider or {@code null}
	 * @param variableResolver the variable resolver or {@code null}
	 * @return a {@link ResolvableType} for the specified {@link Type} and {@link VariableResolver}
	 */
	static ResolvableType forType(Type type, TypeProvider typeProvider, VariableResolver variableResolver) {
		if (type == null && typeProvider != null) {
			type = SerializableTypeWrapper.forTypeProvider(typeProvider);
		}
		if (type == null) {
			return NONE;
		}

		// For simple Class references, build the wrapper right away -
		// no expensive resolution necessary, so not worth caching...
		if (type instanceof Class) {
			return new ResolvableType(type, typeProvider, variableResolver, (ResolvableType) null);
		}

		// Purge empty entries on access since we don't have a clean-up thread or the like.
		cache.purgeUnreferencedEntries();

		// Check the cache - we may have a ResolvableType which has been resolved before...
		ResolvableType key = new ResolvableType(type, typeProvider, variableResolver);
		ResolvableType resolvableType = cache.get(key);
		if (resolvableType == null) {
			resolvableType = new ResolvableType(type, typeProvider, variableResolver, key.hash);
			cache.put(resolvableType, resolvableType);
		}
		return resolvableType;
	}

	/**
	 * Clear the internal {@code ResolvableType} cache.
	 * <p>
	 *  清除内部{@code ResolvableType}缓存
	 * 
	 * 
	 * @since 4.2
	 */
	public static void clearCache() {
		cache.clear();
	}


	/**
	 * Strategy interface used to resolve {@link TypeVariable}s.
	 * <p>
	 *  用于解决{@link TypeVariable}的策略界面
	 * 
	 */
	interface VariableResolver extends Serializable {

		/**
		 * Return the source of the resolver (used for hashCode and equals).
		 * <p>
		 *  返回解析器的源(用于hashCode和equals)
		 * 
		 */
		Object getSource();

		/**
		 * Resolve the specified variable.
		 * <p>
		 *  解决指定的变量
		 * 
		 * 
		 * @param variable the variable to resolve
		 * @return the resolved variable, or {@code null} if not found
		 */
		ResolvableType resolveVariable(TypeVariable<?> variable);
	}


	@SuppressWarnings("serial")
	private class DefaultVariableResolver implements VariableResolver {

		@Override
		public ResolvableType resolveVariable(TypeVariable<?> variable) {
			return ResolvableType.this.resolveVariable(variable);
		}

		@Override
		public Object getSource() {
			return ResolvableType.this;
		}
	}


	@SuppressWarnings("serial")
	private static class TypeVariablesVariableResolver implements VariableResolver {

		private final TypeVariable<?>[] variables;

		private final ResolvableType[] generics;

		public TypeVariablesVariableResolver(TypeVariable<?>[] variables, ResolvableType[] generics) {
			this.variables = variables;
			this.generics = generics;
		}

		@Override
		public ResolvableType resolveVariable(TypeVariable<?> variable) {
			for (int i = 0; i < this.variables.length; i++) {
				if (SerializableTypeWrapper.unwrap(this.variables[i]).equals(
						SerializableTypeWrapper.unwrap(variable))) {
					return this.generics[i];
				}
			}
			return null;
		}

		@Override
		public Object getSource() {
			return this.generics;
		}
	}


	private static final class SyntheticParameterizedType implements ParameterizedType, Serializable {

		private final Type rawType;

		private final Type[] typeArguments;

		public SyntheticParameterizedType(Type rawType, Type[] typeArguments) {
			this.rawType = rawType;
			this.typeArguments = typeArguments;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}

		@Override
		public Type getRawType() {
			return this.rawType;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return this.typeArguments;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof ParameterizedType)) {
				return false;
			}
			ParameterizedType otherType = (ParameterizedType) other;
			return (otherType.getOwnerType() == null && this.rawType.equals(otherType.getRawType()) &&
					Arrays.equals(this.typeArguments, otherType.getActualTypeArguments()));
		}

		@Override
		public int hashCode() {
			return (this.rawType.hashCode() * 31 + Arrays.hashCode(this.typeArguments));
		}
	}


	/**
	 * Internal helper to handle bounds from {@link WildcardType}s.
	 * <p>
	 *  内部帮手处理来自{@link WildcardType}的界限
	 * 
	 */
	private static class WildcardBounds {

		private final Kind kind;

		private final ResolvableType[] bounds;

		/**
		 * Internal constructor to create a new {@link WildcardBounds} instance.
		 * <p>
		 *  内部构造函数创建一个新的{@link WildcardBounds}实例
		 * 
		 * 
		 * @param kind the kind of bounds
		 * @param bounds the bounds
		 * @see #get(ResolvableType)
		 */
		public WildcardBounds(Kind kind, ResolvableType[] bounds) {
			this.kind = kind;
			this.bounds = bounds;
		}

		/**
		 * Return {@code true} if this bounds is the same kind as the specified bounds.
		 * <p>
		 * 如果此边界与指定边界相同,则返回{@code true}
		 * 
		 */
		public boolean isSameKind(WildcardBounds bounds) {
			return this.kind == bounds.kind;
		}

		/**
		 * Return {@code true} if this bounds is assignable to all the specified types.
		 * <p>
		 *  如果此边界可分配给所有指定的类型,则返回{@code true}
		 * 
		 * 
		 * @param types the types to test against
		 * @return {@code true} if this bounds is assignable to all types
		 */
		public boolean isAssignableFrom(ResolvableType... types) {
			for (ResolvableType bound : this.bounds) {
				for (ResolvableType type : types) {
					if (!isAssignable(bound, type)) {
						return false;
					}
				}
			}
			return true;
		}

		private boolean isAssignable(ResolvableType source, ResolvableType from) {
			return (this.kind == Kind.UPPER ? source.isAssignableFrom(from) : from.isAssignableFrom(source));
		}

		/**
		 * Return the underlying bounds.
		 * <p>
		 *  返回底层边界
		 * 
		 */
		public ResolvableType[] getBounds() {
			return this.bounds;
		}

		/**
		 * Get a {@link WildcardBounds} instance for the specified type, returning
		 * {@code null} if the specified type cannot be resolved to a {@link WildcardType}.
		 * <p>
		 *  获取指定类型的{@link WildcardBounds}实例,如果指定的类型无法解析为{@link WildcardType},则返回{@code null}
		 * 
		 * 
		 * @param type the source type
		 * @return a {@link WildcardBounds} instance or {@code null}
		 */
		public static WildcardBounds get(ResolvableType type) {
			ResolvableType resolveToWildcard = type;
			while (!(resolveToWildcard.getType() instanceof WildcardType)) {
				if (resolveToWildcard == NONE) {
					return null;
				}
				resolveToWildcard = resolveToWildcard.resolveType();
			}
			WildcardType wildcardType = (WildcardType) resolveToWildcard.type;
			Kind boundsType = (wildcardType.getLowerBounds().length > 0 ? Kind.LOWER : Kind.UPPER);
			Type[] bounds = boundsType == Kind.UPPER ? wildcardType.getUpperBounds() : wildcardType.getLowerBounds();
			ResolvableType[] resolvableBounds = new ResolvableType[bounds.length];
			for (int i = 0; i < bounds.length; i++) {
				resolvableBounds[i] = ResolvableType.forType(bounds[i], type.variableResolver);
			}
			return new WildcardBounds(boundsType, resolvableBounds);
		}

		/**
		 * The various kinds of bounds.
		 * <p>
		 *  各种界限
		 */
		enum Kind {UPPER, LOWER}
	}

}
