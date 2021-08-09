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

package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 * Helper class for resolving generic types against type variables.
 *
 * <p>Mainly intended for usage within the framework, resolving method
 * parameter types even when they are declared generically.
 *
 * <p>
 *  用于根据类型变量解析通用类型的Helper类
 * 
 *  <p>主要是为了在框架内使用,即使在一般声明时也解析方法参数类型
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 2.5.2
 * @see GenericCollectionTypeResolver
 */
public abstract class GenericTypeResolver {

	/** Cache from Class to TypeVariable Map */
	@SuppressWarnings("rawtypes")
	private static final Map<Class<?>, Map<TypeVariable, Type>> typeVariableCache =
			new ConcurrentReferenceHashMap<Class<?>, Map<TypeVariable, Type>>();


	/**
	 * Determine the target type for the given parameter specification.
	 * <p>
	 * 确定给定参数规范的目标类型
	 * 
	 * 
	 * @param methodParameter the method parameter specification
	 * @return the corresponding generic parameter type
	 * @deprecated as of Spring 4.0, use {@link MethodParameter#getGenericParameterType()}
	 */
	@Deprecated
	public static Type getTargetType(MethodParameter methodParameter) {
		Assert.notNull(methodParameter, "MethodParameter must not be null");
		return methodParameter.getGenericParameterType();
	}

	/**
	 * Determine the target type for the given generic parameter type.
	 * <p>
	 *  确定给定通用参数类型的目标类型
	 * 
	 * 
	 * @param methodParameter the method parameter specification
	 * @param implementationClass the class to resolve type variables against
	 * @return the corresponding generic parameter or return type
	 */
	public static Class<?> resolveParameterType(MethodParameter methodParameter, Class<?> implementationClass) {
		Assert.notNull(methodParameter, "MethodParameter must not be null");
		Assert.notNull(implementationClass, "Class must not be null");
		methodParameter.setContainingClass(implementationClass);
		ResolvableType.resolveMethodParameter(methodParameter);
		return methodParameter.getParameterType();
	}

	/**
	 * Determine the target type for the generic return type of the given method,
	 * where formal type variables are declared on the given class.
	 * <p>
	 *  确定给定方法的通用返回类型的目标类型,其中在给定类上声明了形式类型变量
	 * 
	 * 
	 * @param method the method to introspect
	 * @param clazz the class to resolve type variables against
	 * @return the corresponding generic parameter or return type
	 * @see #resolveReturnTypeForGenericMethod
	 */
	public static Class<?> resolveReturnType(Method method, Class<?> clazz) {
		Assert.notNull(method, "Method must not be null");
		Assert.notNull(clazz, "Class must not be null");
		return ResolvableType.forMethodReturnType(method, clazz).resolve(method.getReturnType());
	}

	/**
	 * Determine the target type for the generic return type of the given
	 * <em>generic method</em>, where formal type variables are declared on
	 * the given method itself.
	 * <p>For example, given a factory method with the following signature,
	 * if {@code resolveReturnTypeForGenericMethod()} is invoked with the reflected
	 * method for {@code creatProxy()} and an {@code Object[]} array containing
	 * {@code MyService.class}, {@code resolveReturnTypeForGenericMethod()} will
	 * infer that the target return type is {@code MyService}.
	 * <pre class="code">{@code public static <T> T createProxy(Class<T> clazz)}</pre>
	 * <h4>Possible Return Values</h4>
	 * <ul>
	 * <li>the target return type, if it can be inferred</li>
	 * <li>the {@linkplain Method#getReturnType() standard return type}, if
	 * the given {@code method} does not declare any {@linkplain
	 * Method#getTypeParameters() formal type variables}</li>
	 * <li>the {@linkplain Method#getReturnType() standard return type}, if the
	 * target return type cannot be inferred (e.g., due to type erasure)</li>
	 * <li>{@code null}, if the length of the given arguments array is shorter
	 * than the length of the {@linkplain
	 * Method#getGenericParameterTypes() formal argument list} for the given
	 * method</li>
	 * </ul>
	 * <p>
	 * 确定给定的通用方法</em>的通用返回类型的目标类型,其中在给定方法本身上声明形式类型变量<p>例如,给定具有以下签名的工厂方法,如果{ @code resolveReturnTypeForGenericMethod()}
	 * 使用{@code creatProxy()}的反射方法和包含{@code MyServiceclass} {@code resolveReturnTypeForGenericMethod()}的{@code Object [])数组调用,将推断目标返回类型是{@code MyService}
	 *  <pre class ="code"> {@ code public static <T> T createProxy(Class <T> clazz)} </pre> <h4>可能的返回值</h4>
	 * 。
	 * <ul>
	 * <li>目标返回类型,如果可以推断</li> <li> {@linkplain Method#getReturnType()标准返回类型},如果给定的{@code方法}不声明任何{@linkplain方法#getTypeParameters()形式类型变量}
	 *  </li> <li> {@linkplain方法#getReturnType()标准返回类型},如果目标返回类型无法推断(例如,由于类型擦除)</li>如果给定参数数组的长度小于给定方法的{@linkplain方法#getGenericParameterTypes()形式参数列表}
	 * 的长度,则为{@ code null} <li> {@ code null} </li>。
	 * </ul>
	 * 
	 * @param method the method to introspect, never {@code null}
	 * @param args the arguments that will be supplied to the method when it is
	 * invoked (never {@code null})
	 * @param classLoader the ClassLoader to resolve class names against, if necessary
	 * (may be {@code null})
	 * @return the resolved target return type, the standard return type, or {@code null}
	 * @since 3.2.5
	 * @see #resolveReturnType
	 */
	public static Class<?> resolveReturnTypeForGenericMethod(Method method, Object[] args, ClassLoader classLoader) {
		Assert.notNull(method, "Method must not be null");
		Assert.notNull(args, "Argument array must not be null");

		TypeVariable<Method>[] declaredTypeVariables = method.getTypeParameters();
		Type genericReturnType = method.getGenericReturnType();
		Type[] methodArgumentTypes = method.getGenericParameterTypes();

		// No declared type variables to inspect, so just return the standard return type.
		if (declaredTypeVariables.length == 0) {
			return method.getReturnType();
		}

		// The supplied argument list is too short for the method's signature, so
		// return null, since such a method invocation would fail.
		if (args.length < methodArgumentTypes.length) {
			return null;
		}

		// Ensure that the type variable (e.g., T) is declared directly on the method
		// itself (e.g., via <T>), not on the enclosing class or interface.
		boolean locallyDeclaredTypeVariableMatchesReturnType = false;
		for (TypeVariable<Method> currentTypeVariable : declaredTypeVariables) {
			if (currentTypeVariable.equals(genericReturnType)) {
				locallyDeclaredTypeVariableMatchesReturnType = true;
				break;
			}
		}

		if (locallyDeclaredTypeVariableMatchesReturnType) {
			for (int i = 0; i < methodArgumentTypes.length; i++) {
				Type currentMethodArgumentType = methodArgumentTypes[i];
				if (currentMethodArgumentType.equals(genericReturnType)) {
					return args[i].getClass();
				}
				if (currentMethodArgumentType instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) currentMethodArgumentType;
					Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
					for (Type typeArg : actualTypeArguments) {
						if (typeArg.equals(genericReturnType)) {
							Object arg = args[i];
							if (arg instanceof Class) {
								return (Class<?>) arg;
							}
							else if (arg instanceof String && classLoader != null) {
								try {
									return classLoader.loadClass((String) arg);
								}
								catch (ClassNotFoundException ex) {
									throw new IllegalStateException(
											"Could not resolve specific class name argument [" + arg + "]", ex);
								}
							}
							else {
								// Consider adding logic to determine the class of the typeArg, if possible.
								// For now, just fall back...
								return method.getReturnType();
							}
						}
					}
				}
			}
		}

		// Fall back...
		return method.getReturnType();
	}

	/**
	 * Resolve the single type argument of the given generic interface against the given
	 * target method which is assumed to return the given interface or an implementation
	 * of it.
	 * <p>
	 *  根据给定的目标方法解析给定通用接口的单一类型参数,该方法假定返回给定的接口或其实现
	 * 
	 * 
	 * @param method the target method to check the return type of
	 * @param genericIfc the generic interface or superclass to resolve the type argument from
	 * @return the resolved parameter type of the method return type, or {@code null}
	 * if not resolvable or if the single argument is of type {@link WildcardType}.
	 */
	public static Class<?> resolveReturnTypeArgument(Method method, Class<?> genericIfc) {
		Assert.notNull(method, "method must not be null");
		ResolvableType resolvableType = ResolvableType.forMethodReturnType(method).as(genericIfc);
		if (!resolvableType.hasGenerics() || resolvableType.getType() instanceof WildcardType) {
			return null;
		}
		return getSingleGeneric(resolvableType);
	}

	/**
	 * Resolve the single type argument of the given generic interface against
	 * the given target class which is assumed to implement the generic interface
	 * and possibly declare a concrete type for its type variable.
	 * <p>
	 * 根据给定的目标类来解决给定通用接口的单一类型参数,该目标类被假定为实现通用接口,并可能为其类型变量声明具体类型
	 * 
	 * 
	 * @param clazz the target class to check against
	 * @param genericIfc the generic interface or superclass to resolve the type argument from
	 * @return the resolved type of the argument, or {@code null} if not resolvable
	 */
	public static Class<?> resolveTypeArgument(Class<?> clazz, Class<?> genericIfc) {
		ResolvableType resolvableType = ResolvableType.forClass(clazz).as(genericIfc);
		if (!resolvableType.hasGenerics()) {
			return null;
		}
		return getSingleGeneric(resolvableType);
	}

	private static Class<?> getSingleGeneric(ResolvableType resolvableType) {
		if (resolvableType.getGenerics().length > 1) {
			throw new IllegalArgumentException("Expected 1 type argument on generic interface [" +
					resolvableType + "] but found " + resolvableType.getGenerics().length);
		}
		return resolvableType.getGeneric().resolve();
	}


	/**
	 * Resolve the type arguments of the given generic interface against the given
	 * target class which is assumed to implement the generic interface and possibly
	 * declare concrete types for its type variables.
	 * <p>
	 *  根据假定实现通用接口的给定目标类,解析给定通用接口的类型参数,并可能声明其类型变量的具体类型
	 * 
	 * 
	 * @param clazz the target class to check against
	 * @param genericIfc the generic interface or superclass to resolve the type argument from
	 * @return the resolved type of each argument, with the array size matching the
	 * number of actual type arguments, or {@code null} if not resolvable
	 */
	public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
		ResolvableType type = ResolvableType.forClass(clazz).as(genericIfc);
		if (!type.hasGenerics() || type.isEntirelyUnresolvable()) {
			return null;
		}
		return type.resolveGenerics(Object.class);
	}

	/**
	 * Resolve the specified generic type against the given TypeVariable map.
	 * <p>
	 *  根据给定的TypeVariable映射解析指定的泛型类型
	 * 
	 * 
	 * @param genericType the generic type to resolve
	 * @param map the TypeVariable Map to resolved against
	 * @return the type if it resolves to a Class, or {@code Object.class} otherwise
	 * @deprecated as of Spring 4.0 in favor of {@link ResolvableType}
	 */
	@Deprecated
	@SuppressWarnings("rawtypes")
	public static Class<?> resolveType(Type genericType, Map<TypeVariable, Type> map) {
		return ResolvableType.forType(genericType, new TypeVariableMapVariableResolver(map)).resolve(Object.class);
	}

	/**
	 * Build a mapping of {@link TypeVariable#getName TypeVariable names} to
	 * {@link Class concrete classes} for the specified {@link Class}. Searches
	 * all super types, enclosing types and interfaces.
	 * <p>
	 *  构建{@link TypeVariable#getName TypeVariable names}到{@link Class具体类}的映射,用于指定的{@link Class}搜索所有超类型,封闭类型
	 * 和接口。
	 * 
	 * @deprecated as of Spring 4.0 in favor of {@link ResolvableType}
	 */
	@Deprecated
	@SuppressWarnings("rawtypes")
	public static Map<TypeVariable, Type> getTypeVariableMap(Class<?> clazz) {
		Map<TypeVariable, Type> typeVariableMap = typeVariableCache.get(clazz);
		if (typeVariableMap == null) {
			typeVariableMap = new HashMap<TypeVariable, Type>();
			buildTypeVariableMap(ResolvableType.forClass(clazz), typeVariableMap);
			typeVariableCache.put(clazz, Collections.unmodifiableMap(typeVariableMap));
		}
		return typeVariableMap;
	}

	@SuppressWarnings("rawtypes")
	private static void buildTypeVariableMap(ResolvableType type, Map<TypeVariable, Type> typeVariableMap) {
		if (type != ResolvableType.NONE) {
			if (type.getType() instanceof ParameterizedType) {
				TypeVariable<?>[] variables = type.resolve().getTypeParameters();
				for (int i = 0; i < variables.length; i++) {
					ResolvableType generic = type.getGeneric(i);
					while (generic.getType() instanceof TypeVariable<?>) {
						generic = generic.resolveType();
					}
					if (generic != ResolvableType.NONE) {
						typeVariableMap.put(variables[i], generic.getType());
					}
				}
			}
			buildTypeVariableMap(type.getSuperType(), typeVariableMap);
			for (ResolvableType interfaceType : type.getInterfaces()) {
				buildTypeVariableMap(interfaceType, typeVariableMap);
			}
			if (type.resolve().isMemberClass()) {
				buildTypeVariableMap(ResolvableType.forClass(type.resolve().getEnclosingClass()), typeVariableMap);
			}
		}
	}


	@SuppressWarnings({"serial", "rawtypes"})
	private static class TypeVariableMapVariableResolver implements ResolvableType.VariableResolver {

		private final Map<TypeVariable, Type> typeVariableMap;

		public TypeVariableMapVariableResolver(Map<TypeVariable, Type> typeVariableMap) {
			this.typeVariableMap = typeVariableMap;
		}

		@Override
		public ResolvableType resolveVariable(TypeVariable<?> variable) {
			Type type = this.typeVariableMap.get(variable);
			return (type != null ? ResolvableType.forType(type) : null);
		}

		@Override
		public Object getSource() {
			return this.typeVariableMap;
		}
	}

}
