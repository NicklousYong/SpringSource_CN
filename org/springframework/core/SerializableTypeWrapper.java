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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

/**
 * Internal utility class that can be used to obtain wrapped {@link Serializable} variants
 * of {@link java.lang.reflect.Type}s.
 *
 * <p>{@link #forField(Field) Fields} or {@link #forMethodParameter(MethodParameter)
 * MethodParameters} can be used as the root source for a serializable type. Alternatively
 * the {@link #forGenericSuperclass(Class) superclass},
 * {@link #forGenericInterfaces(Class) interfaces} or {@link #forTypeParameters(Class)
 * type parameters} or a regular {@link Class} can also be used as source.
 *
 * <p>The returned type will either be a {@link Class} or a serializable proxy of
 * {@link GenericArrayType}, {@link ParameterizedType}, {@link TypeVariable} or
 * {@link WildcardType}. With the exception of {@link Class} (which is final) calls to
 * methods that return further {@link Type}s (for example
 * {@link GenericArrayType#getGenericComponentType()}) will be automatically wrapped.
 *
 * <p>
 *  内部实用程序类,可用于获取{@link javalangreflectType}的包装{@link Serializable}变体
 * 
 * 可以使用{@ link #forField(Field)Fields}或{@link #forMethodParameter(MethodParameter)MethodParameters}作为可序列
 * 化类型的根源。
 * 或者{@link #forGenericSuperclass(Class)superclass},{@链接#forGenericInterfaces(Class)接口}或{@link #forTypeParameters(Class)类型参数}
 * 或常规{@link Class}也可以用作源。
 * 
 *  <p>返回的类型将是{@link Class}或{@link GenericArrayType},{@link ParameterizedType},{@link TypeVariable}或{@link WildcardType}
 * 的序列化代理除了{@link Class}(这是final)调用返回的方法{@link Type}(例如{@link GenericArrayType#getGenericComponentType()}
 * )将被自动包装。
 * 
 * 
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @since 4.0
 */
abstract class SerializableTypeWrapper {

	private static final Class<?>[] SUPPORTED_SERIALIZABLE_TYPES = {
			GenericArrayType.class, ParameterizedType.class, TypeVariable.class, WildcardType.class};

	private static final ConcurrentReferenceHashMap<Type, Type> cache =
			new ConcurrentReferenceHashMap<Type, Type>(256);


	/**
	 * Return a {@link Serializable} variant of {@link Field#getGenericType()}.
	 * <p>
	 * 返回{@link Field#getGenericType()}的{@link Serializable}变体
	 * 
	 */
	public static Type forField(Field field) {
		Assert.notNull(field, "Field must not be null");
		return forTypeProvider(new FieldTypeProvider(field));
	}

	/**
	 * Return a {@link Serializable} variant of
	 * {@link MethodParameter#getGenericParameterType()}.
	 * <p>
	 *  返回{@link MethodParameter#getGenericParameterType()}的{@link Serializable}变体
	 * 
	 */
	public static Type forMethodParameter(MethodParameter methodParameter) {
		return forTypeProvider(new MethodParameterTypeProvider(methodParameter));
	}

	/**
	 * Return a {@link Serializable} variant of {@link Class#getGenericSuperclass()}.
	 * <p>
	 *  返回{@link Class#getGenericSuperclass()}的{@link Serializable}变体
	 * 
	 */
	@SuppressWarnings("serial")
	public static Type forGenericSuperclass(final Class<?> type) {
		return forTypeProvider(new DefaultTypeProvider() {
			@Override
			public Type getType() {
				return type.getGenericSuperclass();
			}
		});
	}

	/**
	 * Return a {@link Serializable} variant of {@link Class#getGenericInterfaces()}.
	 * <p>
	 *  返回{@link Class#getGenericInterfaces()}的{@link Serializable}变体
	 * 
	 */
	@SuppressWarnings("serial")
	public static Type[] forGenericInterfaces(final Class<?> type) {
		Type[] result = new Type[type.getGenericInterfaces().length];
		for (int i = 0; i < result.length; i++) {
			final int index = i;
			result[i] = forTypeProvider(new DefaultTypeProvider() {
				@Override
				public Type getType() {
					return type.getGenericInterfaces()[index];
				}
			});
		}
		return result;
	}

	/**
	 * Return a {@link Serializable} variant of {@link Class#getTypeParameters()}.
	 * <p>
	 *  返回{@link Class#getTypeParameters()}的{@link Serializable}变体
	 * 
	 */
	@SuppressWarnings("serial")
	public static Type[] forTypeParameters(final Class<?> type) {
		Type[] result = new Type[type.getTypeParameters().length];
		for (int i = 0; i < result.length; i++) {
			final int index = i;
			result[i] = forTypeProvider(new DefaultTypeProvider() {
				@Override
				public Type getType() {
					return type.getTypeParameters()[index];
				}
			});
		}
		return result;
	}

	/**
	 * Unwrap the given type, effectively returning the original non-serializable type.
	 * <p>
	 *  打开给定类型,有效地返回原始的非可序列化类型
	 * 
	 * 
	 * @param type the type to unwrap
	 * @return the original non-serializable type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Type> T unwrap(T type) {
		Type unwrapped = type;
		while (unwrapped instanceof SerializableTypeProxy) {
			unwrapped = ((SerializableTypeProxy) type).getTypeProvider().getType();
		}
		return (T) unwrapped;
	}

	/**
	 * Return a {@link Serializable} {@link Type} backed by a {@link TypeProvider} .
	 * <p>
	 *  返回由{@link TypeProvider}支持的{@link Serializable} {@link Type}
	 * 
	 */
	static Type forTypeProvider(final TypeProvider provider) {
		Assert.notNull(provider, "Provider must not be null");
		if (provider.getType() instanceof Serializable || provider.getType() == null) {
			return provider.getType();
		}
		Type cached = cache.get(provider.getType());
		if (cached != null) {
			return cached;
		}
		for (Class<?> type : SUPPORTED_SERIALIZABLE_TYPES) {
			if (type.isAssignableFrom(provider.getType().getClass())) {
				ClassLoader classLoader = provider.getClass().getClassLoader();
				Class<?>[] interfaces = new Class<?>[] {type, SerializableTypeProxy.class, Serializable.class};
				InvocationHandler handler = new TypeProxyInvocationHandler(provider);
				cached = (Type) Proxy.newProxyInstance(classLoader, interfaces, handler);
				cache.put(provider.getType(), cached);
				return cached;
			}
		}
		throw new IllegalArgumentException("Unsupported Type class: " + provider.getType().getClass().getName());
	}


	/**
	 * Additional interface implemented by the type proxy.
	 * <p>
	 *  由类型代理实现的附加接口
	 * 
	 */
	interface SerializableTypeProxy {

		/**
		 * Return the underlying type provider.
		 * <p>
		 *  返回底层类型提供程序
		 * 
		 */
		TypeProvider getTypeProvider();
	}


	/**
	 * A {@link Serializable} interface providing access to a {@link Type}.
	 * <p>
	 *  {@link Serializable}界面,提供访问{@link类型}
	 * 
	 */
	interface TypeProvider extends Serializable {

		/**
		 * Return the (possibly non {@link Serializable}) {@link Type}.
		 * <p>
		 *  返回(可能非{@link Serializable}){@link Type}
		 * 
		 */
		Type getType();

		/**
		 * Return the source of the type or {@code null}.
		 * <p>
		 * 返回类型的来源或{@code null}
		 * 
		 */
		Object getSource();
	}


	/**
	 * Default implementation of {@link TypeProvider} with a {@code null} source.
	 * <p>
	 *  使用{@code null}源的{@link TypeProvider}的默认实现
	 * 
	 */
	@SuppressWarnings("serial")
	private static abstract class DefaultTypeProvider implements TypeProvider {

		@Override
		public Object getSource() {
			return null;
		}
	}


	/**
	 * {@link Serializable} {@link InvocationHandler} used by the proxied {@link Type}.
	 * Provides serialization support and enhances any methods that return {@code Type}
	 * or {@code Type[]}.
	 * <p>
	 *  {@link Serializable} {@link InvocationHandler}由代理的{@link Type}使用提供序列化支持,并增强返回{@code Type}或{@code Type []}
	 * 的任何方法)。
	 * 
	 */
	@SuppressWarnings("serial")
	private static class TypeProxyInvocationHandler implements InvocationHandler, Serializable {

		private final TypeProvider provider;

		public TypeProxyInvocationHandler(TypeProvider provider) {
			this.provider = provider;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("equals")) {
				Object other = args[0];
				// Unwrap proxies for speed
				if (other instanceof Type) {
					other = unwrap((Type) other);
				}
				return this.provider.getType().equals(other);
			}
			else if (method.getName().equals("hashCode")) {
				return this.provider.getType().hashCode();
			}
			else if (method.getName().equals("getTypeProvider")) {
				return this.provider;
			}

			if (Type.class == method.getReturnType() && args == null) {
				return forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, -1));
			}
			else if (Type[].class == method.getReturnType() && args == null) {
				Type[] result = new Type[((Type[]) method.invoke(this.provider.getType(), args)).length];
				for (int i = 0; i < result.length; i++) {
					result[i] = forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, i));
				}
				return result;
			}

			try {
				return method.invoke(this.provider.getType(), args);
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}


	/**
	 * {@link TypeProvider} for {@link Type}s obtained from a {@link Field}.
	 * <p>
	 *  {@link TypeProvider}从{@link字段}获取{@link Type}
	 * 
	 */
	@SuppressWarnings("serial")
	static class FieldTypeProvider implements TypeProvider {

		private final String fieldName;

		private final Class<?> declaringClass;

		private transient Field field;

		public FieldTypeProvider(Field field) {
			this.fieldName = field.getName();
			this.declaringClass = field.getDeclaringClass();
			this.field = field;
		}

		@Override
		public Type getType() {
			return this.field.getGenericType();
		}

		@Override
		public Object getSource() {
			return this.field;
		}

		private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
			inputStream.defaultReadObject();
			try {
				this.field = this.declaringClass.getDeclaredField(this.fieldName);
			}
			catch (Throwable ex) {
				throw new IllegalStateException("Could not find original class structure", ex);
			}
		}
	}


	/**
	 * {@link TypeProvider} for {@link Type}s obtained from a {@link MethodParameter}.
	 * <p>
	 *  {@link TypeProvider}针对{@link方法参数}获得的{@link Type}
	 * 
	 */
	@SuppressWarnings("serial")
	static class MethodParameterTypeProvider implements TypeProvider {

		private final String methodName;

		private final Class<?>[] parameterTypes;

		private final Class<?> declaringClass;

		private final int parameterIndex;

		private transient MethodParameter methodParameter;

		public MethodParameterTypeProvider(MethodParameter methodParameter) {
			if (methodParameter.getMethod() != null) {
				this.methodName = methodParameter.getMethod().getName();
				this.parameterTypes = methodParameter.getMethod().getParameterTypes();
			}
			else {
				this.methodName = null;
				this.parameterTypes = methodParameter.getConstructor().getParameterTypes();
			}
			this.declaringClass = methodParameter.getDeclaringClass();
			this.parameterIndex = methodParameter.getParameterIndex();
			this.methodParameter = methodParameter;
		}


		@Override
		public Type getType() {
			return this.methodParameter.getGenericParameterType();
		}

		@Override
		public Object getSource() {
			return this.methodParameter;
		}

		private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
			inputStream.defaultReadObject();
			try {
				if (this.methodName != null) {
					this.methodParameter = new MethodParameter(
							this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
				}
				else {
					this.methodParameter = new MethodParameter(
							this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
				}
			}
			catch (Throwable ex) {
				throw new IllegalStateException("Could not find original class structure", ex);
			}
		}
	}


	/**
	 * {@link TypeProvider} for {@link Type}s obtained by invoking a no-arg method.
	 * <p>
	 *  {@link TypeProvider}通过调用no-arg方法获得的{@link Type}
	 */
	@SuppressWarnings("serial")
	static class MethodInvokeTypeProvider implements TypeProvider {

		private final TypeProvider provider;

		private final String methodName;

		private final Class<?> declaringClass;

		private final int index;

		private transient Method method;

		private transient volatile Object result;

		public MethodInvokeTypeProvider(TypeProvider provider, Method method, int index) {
			this.provider = provider;
			this.methodName = method.getName();
			this.declaringClass = method.getDeclaringClass();
			this.index = index;
			this.method = method;
		}

		@Override
		public Type getType() {
			Object result = this.result;
			if (result == null) {
				// Lazy invocation of the target method on the provided type
				result = ReflectionUtils.invokeMethod(this.method, this.provider.getType());
				// Cache the result for further calls to getType()
				this.result = result;
			}
			return (result instanceof Type[] ? ((Type[]) result)[this.index] : (Type) result);
		}

		@Override
		public Object getSource() {
			return null;
		}

		private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
			inputStream.defaultReadObject();
			this.method = ReflectionUtils.findMethod(this.declaringClass, this.methodName);
			Assert.state(Type.class == this.method.getReturnType() || Type[].class == this.method.getReturnType());
		}
	}

}
