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

package org.springframework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Simple utility class for working with the reflection API and handling
 * reflection exceptions.
 *
 * <p>Only intended for internal use.
 *
 * <p>
 *  用于处理反射API和处理反射异常的简单实用程序类
 * 
 *  <p>仅供内部使用
 * 
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Costin Leau
 * @author Sam Brannen
 * @author Chris Beams
 * @since 1.2.2
 */
public abstract class ReflectionUtils {

	/**
	 * Naming prefix for CGLIB-renamed methods.
	 * <p>
	 *  为CGLIB重命名方法命名前缀
	 * 
	 * 
	 * @see #isCglibRenamedMethod
	 */
	private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";

	private static final Method[] NO_METHODS = {};

	private static final Field[] NO_FIELDS = {};


	/**
	 * Cache for {@link Class#getDeclaredMethods()} plus equivalent default methods
	 * from Java 8 based interfaces, allowing for fast iteration.
	 * <p>
	 * 缓存{@link Class#getDeclaredMethods()}加上基于Java 8接口的等效的默认方法,允许快速迭代
	 * 
	 */
	private static final Map<Class<?>, Method[]> declaredMethodsCache =
			new ConcurrentReferenceHashMap<Class<?>, Method[]>(256);

	/**
	 * Cache for {@link Class#getDeclaredFields()}, allowing for fast iteration.
	 * <p>
	 *  缓存{@link Class#getDeclaredFields()},允许快速迭代
	 * 
	 */
	private static final Map<Class<?>, Field[]> declaredFieldsCache =
			new ConcurrentReferenceHashMap<Class<?>, Field[]>(256);


	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with the
	 * supplied {@code name}. Searches all superclasses up to {@link Object}.
	 * <p>
	 *  尝试使用提供的{@code名称}在提供的{@link Class}上找到{@link字段}}搜索所有超类{@link Object}
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @param name the name of the field
	 * @return the corresponding Field object, or {@code null} if not found
	 */
	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with the
	 * supplied {@code name} and/or {@link Class type}. Searches all superclasses
	 * up to {@link Object}.
	 * <p>
	 *  尝试使用提供的{@code名称}和/或{@link类类型}在提供的{@link类}上找到{@link字段字段}搜索所有超类{@link Object}
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @param name the name of the field (may be {@code null} if type is specified)
	 * @param type the type of the field (may be {@code null} if name is specified)
	 * @return the corresponding Field object, or {@code null} if not found
	 */
	public static Field findField(Class<?> clazz, String name, Class<?> type) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
		Class<?> searchType = clazz;
		while (Object.class != searchType && searchType != null) {
			Field[] fields = getDeclaredFields(searchType);
			for (Field field : fields) {
				if ((name == null || name.equals(field.getName())) &&
						(type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * Set the field represented by the supplied {@link Field field object} on the
	 * specified {@link Object target object} to the specified {@code value}.
	 * In accordance with {@link Field#set(Object, Object)} semantics, the new value
	 * is automatically unwrapped if the underlying field has a primitive type.
	 * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException(Exception)}.
	 * <p>
	 * 将指定的{@link对象目标对象}上提供的{@link字段对象}表示的字段设置为指定的{@code值}根据{@link Field#set(Object,Object)}语义,如果基础字段具有原始类型,
	 * 则新值将自动解包<p>抛出的异常通过调用{@link #handleReflectionException(Exception)}来处理)。
	 * 
	 * 
	 * @param field the field to set
	 * @param target the target object on which to set the field
	 * @param value the value to set (may be {@code null})
	 */
	public static void setField(Field field, Object target, Object value) {
		try {
			field.set(target, value);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Get the field represented by the supplied {@link Field field object} on the
	 * specified {@link Object target object}. In accordance with {@link Field#get(Object)}
	 * semantics, the returned value is automatically wrapped if the underlying field
	 * has a primitive type.
	 * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException(Exception)}.
	 * <p>
	 *  获取由指定的{@link对象目标对象}提供的{@link字段对象}表示的字段根据{@link Field#get(Object)}语义,返回的值将自动包装,如果基础字段一个原始类型<p>抛出的异常是通
	 * 过调用{@link #handleReflectionException(Exception)}来处理的。
	 * 
	 * 
	 * @param field the field to get
	 * @param target the target object from which to get the field
	 * @return the field's current value
	 */
	public static Object getField(Field field, Object target) {
		try {
			return field.get(target);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied name
	 * and no parameters. Searches all superclasses up to {@code Object}.
	 * <p>Returns {@code null} if no {@link Method} can be found.
	 * <p>
	 * 尝试使用提供的名称和无参数在提供的类上找到{@link Method}搜索所有超类{@code Object} <p>返回{@code null}如果没有{@link方法}可以找到
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 * @return the Method object, or {@code null} if none found
	 */
	public static Method findMethod(Class<?> clazz, String name) {
		return findMethod(clazz, name, new Class<?>[0]);
	}

	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied name
	 * and parameter types. Searches all superclasses up to {@code Object}.
	 * <p>Returns {@code null} if no {@link Method} can be found.
	 * <p>
	 *  尝试使用提供的名称和参数类型在提供的类上找到{@link方法}搜索所有超类{@code Object} <p>返回{@code null}如果没有{@link方法}可以找到
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 * @param paramTypes the parameter types of the method
	 * (may be {@code null} to indicate any signature)
	 * @return the Method object, or {@code null} if none found
	 */
	public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(name, "Method name must not be null");
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
			for (Method method : methods) {
				if (name.equals(method.getName()) &&
						(paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object with no arguments.
	 * The target object can be {@code null} when invoking a static {@link Method}.
	 * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
	 * <p>
	 *  针对提供的目标对象调用指定的{@link方法}无参数当调用静态{@link方法} <p>时,目标对象可以是{@code null}。
	 * 抛出的异常通过调用{@link# handleReflectionException}。
	 * 
	 * 
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @return the invocation result, if any
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, new Object[0]);
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object with the
	 * supplied arguments. The target object can be {@code null} when invoking a
	 * static {@link Method}.
	 * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
	 * <p>
	 * 使用提供的参数调用指定的{@link Method}对象提供的目标对象当调用静态{@link方法} <p>时,目标对象可以是{@code null}抛出的异常通过调用{@link #handleReflectionException}
	 * 。
	 * 
	 * 
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @param args the invocation arguments (may be {@code null})
	 * @return the invocation result, if any
	 */
	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		}
		catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * Invoke the specified JDBC API {@link Method} against the supplied target
	 * object with no arguments.
	 * <p>
	 *  针对没有参数的提供的目标对象调用指定的JDBC API {@link Method}
	 * 
	 * 
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @return the invocation result, if any
	 * @throws SQLException the JDBC API SQLException to rethrow (if any)
	 * @see #invokeJdbcMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeJdbcMethod(Method method, Object target) throws SQLException {
		return invokeJdbcMethod(method, target, new Object[0]);
	}

	/**
	 * Invoke the specified JDBC API {@link Method} against the supplied target
	 * object with the supplied arguments.
	 * <p>
	 *  使用提供的参数对提供的目标对象调用指定的JDBC API {@link Method}
	 * 
	 * 
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @param args the invocation arguments (may be {@code null})
	 * @return the invocation result, if any
	 * @throws SQLException the JDBC API SQLException to rethrow (if any)
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 */
	public static Object invokeJdbcMethod(Method method, Object target, Object... args) throws SQLException {
		try {
			return method.invoke(target, args);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof SQLException) {
				throw (SQLException) ex.getTargetException();
			}
			handleInvocationTargetException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * Handle the given reflection exception. Should only be called if no
	 * checked exception is expected to be thrown by the target method.
	 * <p>Throws the underlying RuntimeException or Error in case of an
	 * InvocationTargetException with such a root cause. Throws an
	 * IllegalStateException with an appropriate message or
	 * UndeclaredThrowableException otherwise.
	 * <p>
	 * 处理给定的反射异常只应该调用,如果没有检查的异常被目标方法抛出<p>抛出底层的RuntimeException或者错误,如果有这样一个根本原因的InvocationTargetException抛出一个
	 * IllegalStateException与一个适当的消息或UndeclaredThrowableException除此以外。
	 * 
	 * 
	 * @param ex the reflection exception to handle
	 */
	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * Handle the given invocation target exception. Should only be called if no
	 * checked exception is expected to be thrown by the target method.
	 * <p>Throws the underlying RuntimeException or Error in case of such a root
	 * cause. Throws an UndeclaredThrowableException otherwise.
	 * <p>
	 *  处理给定的调用目标异常只应在目标方法期望抛出任何检查异常时才调用<p>如果出现这样的根本原因,则抛出底层RuntimeException或Error抛出UndeclaredThrowableExcep
	 * tion异常。
	 * 
	 * 
	 * @param ex the invocation target exception to handle
	 */
	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}.
	 * Should only be called if no checked exception is expected to be thrown
	 * by the target method.
	 * <p>Rethrows the underlying exception cast to a {@link RuntimeException} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link UndeclaredThrowableException}.
	 * <p>
	 * 返回给定的{@link Throwable异常},这可能是{@link InvocationTargetException}的<em>目标异常</em>只有在目标方法不会抛出任何检查异常时才应该调用<p>
	 * 如果适当,将底层异常转换重新引导到{@link RuntimeException}或{@link Error};否则,抛出一个{@link UndeclaredThrowableException}。
	 * 
	 * 
	 * @param ex the exception to rethrow
	 * @throws RuntimeException the rethrown exception
	 */
	public static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}.
	 * Should only be called if no checked exception is expected to be thrown
	 * by the target method.
	 * <p>Rethrows the underlying exception cast to an {@link Exception} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link UndeclaredThrowableException}.
	 * <p>
	 *  返回给定的{@link Throwable异常},这可能是{@link InvocationTargetException}的<em>目标异常</em>只有在目标方法不会抛出任何检查异常时才应该调用<p>
	 * 如果适用,将底层的异常转换返回到{@link Exception}或{@link Error};否则,抛出一个{@link UndeclaredThrowableException}。
	 * 
	 * 
	 * @param ex the exception to rethrow
	 * @throws Exception the rethrown exception (in case of a checked exception)
	 */
	public static void rethrowException(Throwable ex) throws Exception {
		if (ex instanceof Exception) {
			throw (Exception) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * Determine whether the given method explicitly declares the given
	 * exception or one of its superclasses, which means that an exception
	 * of that type can be propagated as-is within a reflective invocation.
	 * <p>
	 * 确定给定方法是否明确声明给定的异常或其超类中的一个,这意味着该类型的异常可以在反射调用中原样传播
	 * 
	 * 
	 * @param method the declaring method
	 * @param exceptionType the exception to throw
	 * @return {@code true} if the exception can be thrown as-is;
	 * {@code false} if it needs to be wrapped
	 */
	public static boolean declaresException(Method method, Class<?> exceptionType) {
		Assert.notNull(method, "Method must not be null");
		Class<?>[] declaredExceptions = method.getExceptionTypes();
		for (Class<?> declaredException : declaredExceptions) {
			if (declaredException.isAssignableFrom(exceptionType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine whether the given field is a "public static final" constant.
	 * <p>
	 *  确定给定字段是否为"public static final"常量
	 * 
	 * 
	 * @param field the field to check
	 */
	public static boolean isPublicStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
	}

	/**
	 * Determine whether the given method is an "equals" method.
	 * <p>
	 *  确定给定的方法是否为"等于"方法
	 * 
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	public static boolean isEqualsMethod(Method method) {
		if (method == null || !method.getName().equals("equals")) {
			return false;
		}
		Class<?>[] paramTypes = method.getParameterTypes();
		return (paramTypes.length == 1 && paramTypes[0] == Object.class);
	}

	/**
	 * Determine whether the given method is a "hashCode" method.
	 * <p>
	 *  确定给定的方法是否为"hashCode"方法
	 * 
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public static boolean isHashCodeMethod(Method method) {
		return (method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0);
	}

	/**
	 * Determine whether the given method is a "toString" method.
	 * <p>
	 *  确定给定的方法是否为"toString"方法
	 * 
	 * 
	 * @see java.lang.Object#toString()
	 */
	public static boolean isToStringMethod(Method method) {
		return (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0);
	}

	/**
	 * Determine whether the given method is originally declared by {@link java.lang.Object}.
	 * <p>
	 *  确定给定的方法是否最初由{@link javalangObject}声明
	 * 
	 */
	public static boolean isObjectMethod(Method method) {
		if (method == null) {
			return false;
		}
		try {
			Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return true;
		}
		catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Determine whether the given method is a CGLIB 'renamed' method,
	 * following the pattern "CGLIB$methodName$0".
	 * <p>
	 *  确定给定方法是否是CGLIB"重命名"方法,遵循模式"CGLIB $ methodName $ 0"
	 * 
	 * 
	 * @param renamedMethod the method to check
	 * @see org.springframework.cglib.proxy.Enhancer#rename
	 */
	public static boolean isCglibRenamedMethod(Method renamedMethod) {
		String name = renamedMethod.getName();
		if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
			int i = name.length() - 1;
			while (i >= 0 && Character.isDigit(name.charAt(i))) {
				i--;
			}
			return ((i > CGLIB_RENAMED_METHOD_PREFIX.length()) &&
						(i < name.length() - 1) && name.charAt(i) == '$');
		}
		return false;
	}

	/**
	 * Make the given field accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called
	 * when actually necessary, to avoid unnecessary conflicts with a JVM
	 * SecurityManager (if active).
	 * <p>
	 * 使给定的字段可以访问,如果需要,明确地设置它可访问{@code setAccessible(true)}方法仅在实际需要时才调用,以避免与JVM SecurityManager的不必要冲突(如果处于活动
	 * 状态)。
	 * 
	 * 
	 * @param field the field to make accessible
	 * @see java.lang.reflect.Field#setAccessible
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) ||
				!Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
				Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * Make the given method accessible, explicitly setting it accessible if
	 * necessary. The {@code setAccessible(true)} method is only called
	 * when actually necessary, to avoid unnecessary conflicts with a JVM
	 * SecurityManager (if active).
	 * <p>
	 *  使给定的方法可访问,如果需要明确设置它可访问{@code setAccessible(true)}方法仅在实际需要时才调用,以避免与JVM SecurityManager发生不必要的冲突(如果处于活动
	 * 状态)。
	 * 
	 * 
	 * @param method the method to make accessible
	 * @see java.lang.reflect.Method#setAccessible
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) ||
				!Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * Make the given constructor accessible, explicitly setting it accessible
	 * if necessary. The {@code setAccessible(true)} method is only called
	 * when actually necessary, to avoid unnecessary conflicts with a JVM
	 * SecurityManager (if active).
	 * <p>
	 *  使给定的构造函数可访问,如果需要,明确设置它可访问{@code setAccessible(true)}方法仅在实际需要时才调用,以避免与JVM SecurityManager发生不必要的冲突(如果处
	 * 于活动状态)。
	 * 
	 * 
	 * @param ctor the constructor to make accessible
	 * @see java.lang.reflect.Constructor#setAccessible
	 */
	public static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) ||
				!Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

	/**
	 * Perform the given callback operation on all matching methods of the given
	 * class, as locally declared or equivalent thereof (such as default methods
	 * on Java 8 based interfaces that the given class implements).
	 * <p>
	 * 对给定类的所有匹配方法执行给定的回调操作,如本地声明的或等效的(例如给定类实现的基于Java 8的接口上的默认方法)
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @param mc the callback to invoke for each method
	 * @since 4.2
	 * @see #doWithMethods
	 */
	public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
		Method[] methods = getDeclaredMethods(clazz);
		for (Method method : methods) {
			try {
				mc.doWith(method);
			}
			catch (IllegalAccessException ex) {
				throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
			}
		}
	}

	/**
	 * Perform the given callback operation on all matching methods of the given
	 * class and superclasses.
	 * <p>The same named method occurring on subclass and superclass will appear
	 * twice, unless excluded by a {@link MethodFilter}.
	 * <p>
	 *  对给定类和超类的所有匹配方法执行给定的回调操作<p>子类和超类上发生的相同命名方法将显示两次,除非被{@link MethodFilter}
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @param mc the callback to invoke for each method
	 * @see #doWithMethods(Class, MethodCallback, MethodFilter)
	 */
	public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
		doWithMethods(clazz, mc, null);
	}

	/**
	 * Perform the given callback operation on all matching methods of the given
	 * class and superclasses (or given interface and super-interfaces).
	 * <p>The same named method occurring on subclass and superclass will appear
	 * twice, unless excluded by the specified {@link MethodFilter}.
	 * <p>
	 *  在给定类和超类(或给定的接口和超级接口)的所有匹配方法上执行给定的回调操作。<p>在子类和超类上发生的相同的命名方法将显示两次,除非被指定的{@link MethodFilter}
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @param mc the callback to invoke for each method
	 * @param mf the filter that determines the methods to apply the callback to
	 */
	public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf) {
		// Keep backing up the inheritance hierarchy.
		Method[] methods = getDeclaredMethods(clazz);
		for (Method method : methods) {
			if (mf != null && !mf.matches(method)) {
				continue;
			}
			try {
				mc.doWith(method);
			}
			catch (IllegalAccessException ex) {
				throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
			}
		}
		if (clazz.getSuperclass() != null) {
			doWithMethods(clazz.getSuperclass(), mc, mf);
		}
		else if (clazz.isInterface()) {
			for (Class<?> superIfc : clazz.getInterfaces()) {
				doWithMethods(superIfc, mc, mf);
			}
		}
	}

	/**
	 * Get all declared methods on the leaf class and all superclasses.
	 * Leaf class methods are included first.
	 * <p>
	 * 获取叶子类和所有超类的所有声明的方法首先包含Leaf类方法
	 * 
	 * 
	 * @param leafClass the class to introspect
	 */
	public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
		final List<Method> methods = new ArrayList<Method>(32);
		doWithMethods(leafClass, new MethodCallback() {
			@Override
			public void doWith(Method method) {
				methods.add(method);
			}
		});
		return methods.toArray(new Method[methods.size()]);
	}

	/**
	 * Get the unique set of declared methods on the leaf class and all superclasses.
	 * Leaf class methods are included first and while traversing the superclass hierarchy
	 * any methods found with signatures matching a method already included are filtered out.
	 * <p>
	 *  获取叶类和所有超类的唯一一组声明的方法首先在遍历超类层次结构时首先包含Leaf类方法,任何使用已经包含的方法匹配的签名的方法被过滤掉
	 * 
	 * 
	 * @param leafClass the class to introspect
	 */
	public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
		final List<Method> methods = new ArrayList<Method>(32);
		doWithMethods(leafClass, new MethodCallback() {
			@Override
			public void doWith(Method method) {
				boolean knownSignature = false;
				Method methodBeingOverriddenWithCovariantReturnType = null;
				for (Method existingMethod : methods) {
					if (method.getName().equals(existingMethod.getName()) &&
							Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
						// Is this a covariant return type situation?
						if (existingMethod.getReturnType() != method.getReturnType() &&
								existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
							methodBeingOverriddenWithCovariantReturnType = existingMethod;
						}
						else {
							knownSignature = true;
						}
						break;
					}
				}
				if (methodBeingOverriddenWithCovariantReturnType != null) {
					methods.remove(methodBeingOverriddenWithCovariantReturnType);
				}
				if (!knownSignature && !isCglibRenamedMethod(method)) {
					methods.add(method);
				}
			}
		});
		return methods.toArray(new Method[methods.size()]);
	}

	/**
	 * This variant retrieves {@link Class#getDeclaredMethods()} from a local cache
	 * in order to avoid the JVM's SecurityManager check and defensive array copying.
	 * In addition, it also includes Java 8 default methods from locally implemented
	 * interfaces, since those are effectively to be treated just like declared methods.
	 * <p>
	 *  此变体从本地缓存中检索{@link Class#getDeclaredMethods()},以避免JVM的SecurityManager检查和防御性阵列复制另外,它还包括来自本地实现的接口的Java 
	 * 8默认方法,因为那些被有效地被处理就像宣布的方法。
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @return the cached array of methods
	 * @see Class#getDeclaredMethods()
	 */
	private static Method[] getDeclaredMethods(Class<?> clazz) {
		Method[] result = declaredMethodsCache.get(clazz);
		if (result == null) {
			Method[] declaredMethods = clazz.getDeclaredMethods();
			List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
			if (defaultMethods != null) {
				result = new Method[declaredMethods.length + defaultMethods.size()];
				System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
				int index = declaredMethods.length;
				for (Method defaultMethod : defaultMethods) {
					result[index] = defaultMethod;
					index++;
				}
			}
			else {
				result = declaredMethods;
			}
			declaredMethodsCache.put(clazz, (result.length == 0 ? NO_METHODS : result));
		}
		return result;
	}

	private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
		List<Method> result = null;
		for (Class<?> ifc : clazz.getInterfaces()) {
			for (Method ifcMethod : ifc.getMethods()) {
				if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
					if (result == null) {
						result = new LinkedList<Method>();
					}
					result.add(ifcMethod);
				}
			}
		}
		return result;
	}

	/**
	 * Invoke the given callback on all fields in the target class, going up the
	 * class hierarchy to get all declared fields.
	 * <p>
	 *  在目标类中的所有字段上调用给定的回调函数,继续执行类层次结构以获取所有声明的字段
	 * 
	 * 
	 * @param clazz the target class to analyze
	 * @param fc the callback to invoke for each field
	 * @since 4.2
	 * @see #doWithFields
	 */
	public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
		for (Field field : getDeclaredFields(clazz)) {
			try {
				fc.doWith(field);
			}
			catch (IllegalAccessException ex) {
				throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
			}
		}
	}

	/**
	 * Invoke the given callback on all fields in the target class, going up the
	 * class hierarchy to get all declared fields.
	 * <p>
	 * 在目标类中的所有字段上调用给定的回调函数,继续执行类层次结构以获取所有声明的字段
	 * 
	 * 
	 * @param clazz the target class to analyze
	 * @param fc the callback to invoke for each field
	 */
	public static void doWithFields(Class<?> clazz, FieldCallback fc) {
		doWithFields(clazz, fc, null);
	}

	/**
	 * Invoke the given callback on all fields in the target class, going up the
	 * class hierarchy to get all declared fields.
	 * <p>
	 *  在目标类中的所有字段上调用给定的回调函数,继续执行类层次结构以获取所有声明的字段
	 * 
	 * 
	 * @param clazz the target class to analyze
	 * @param fc the callback to invoke for each field
	 * @param ff the filter that determines the fields to apply the callback to
	 */
	public static void doWithFields(Class<?> clazz, FieldCallback fc, FieldFilter ff) {
		// Keep backing up the inheritance hierarchy.
		Class<?> targetClass = clazz;
		do {
			Field[] fields = getDeclaredFields(targetClass);
			for (Field field : fields) {
				if (ff != null && !ff.matches(field)) {
					continue;
				}
				try {
					fc.doWith(field);
				}
				catch (IllegalAccessException ex) {
					throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);
	}

	/**
	 * This variant retrieves {@link Class#getDeclaredFields()} from a local cache
	 * in order to avoid the JVM's SecurityManager check and defensive array copying.
	 * <p>
	 *  此变体从本地缓存中检索{@link Class#getDeclaredFields()},以避免JVM的SecurityManager检查和防御阵列复制
	 * 
	 * 
	 * @param clazz the class to introspect
	 * @return the cached array of fields
	 * @see Class#getDeclaredFields()
	 */
	private static Field[] getDeclaredFields(Class<?> clazz) {
		Field[] result = declaredFieldsCache.get(clazz);
		if (result == null) {
			result = clazz.getDeclaredFields();
			declaredFieldsCache.put(clazz, (result.length == 0 ? NO_FIELDS : result));
		}
		return result;
	}

	/**
	 * Given the source object and the destination, which must be the same class
	 * or a subclass, copy all fields, including inherited fields. Designed to
	 * work on objects with public no-arg constructors.
	 * <p>
	 *  给定源对象和目标,它们必须是相同的类或子类,复制所有字段,包括继承字段设计为使用公共无参构造函数处理对象
	 * 
	 */
	public static void shallowCopyFieldState(final Object src, final Object dest) {
		if (src == null) {
			throw new IllegalArgumentException("Source for field copy cannot be null");
		}
		if (dest == null) {
			throw new IllegalArgumentException("Destination for field copy cannot be null");
		}
		if (!src.getClass().isAssignableFrom(dest.getClass())) {
			throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() +
					"] must be same or subclass as source class [" + src.getClass().getName() + "]");
		}
		doWithFields(src.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				makeAccessible(field);
				Object srcValue = field.get(src);
				field.set(dest, srcValue);
			}
		}, COPYABLE_FIELDS);
	}

	/**
	 * Clear the internal method/field cache.
	 * <p>
	 *  清除内部方法/字段缓存
	 * 
	 * 
	 * @since 4.2.4
	 */
	public static void clearCache() {
		declaredMethodsCache.clear();
		declaredFieldsCache.clear();
	}


	/**
	 * Action to take on each method.
	 * <p>
	 *  采取各种方法的行动
	 * 
	 */
	public interface MethodCallback {

		/**
		 * Perform an operation using the given method.
		 * <p>
		 *  使用给定的方法执行操作
		 * 
		 * 
		 * @param method the method to operate on
		 */
		void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
	}


	/**
	 * Callback optionally used to filter methods to be operated on by a method callback.
	 * <p>
	 *  回调可选用于过滤方法由方法回调操作
	 * 
	 */
	public interface MethodFilter {

		/**
		 * Determine whether the given method matches.
		 * <p>
		 * 确定给定方法是否匹配
		 * 
		 * 
		 * @param method the method to check
		 */
		boolean matches(Method method);
	}


	/**
	 * Callback interface invoked on each field in the hierarchy.
	 * <p>
	 *  在层次结构中的每个字段上调用回调接口
	 * 
	 */
	public interface FieldCallback {

		/**
		 * Perform an operation using the given field.
		 * <p>
		 *  使用给定字段执行操作
		 * 
		 * 
		 * @param field the field to operate on
		 */
		void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
	}


	/**
	 * Callback optionally used to filter fields to be operated on by a field callback.
	 * <p>
	 *  回调可选用于过滤通过字段回调操作的字段
	 * 
	 */
	public interface FieldFilter {

		/**
		 * Determine whether the given field matches.
		 * <p>
		 *  确定给定字段是否匹配
		 * 
		 * 
		 * @param field the field to check
		 */
		boolean matches(Field field);
	}


	/**
	 * Pre-built FieldFilter that matches all non-static, non-final fields.
	 * <p>
	 *  预先构建的FieldFilter匹配所有非静态非最终字段
	 * 
	 */
	public static final FieldFilter COPYABLE_FIELDS = new FieldFilter() {

		@Override
		public boolean matches(Field field) {
			return !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()));
		}
	};


	/**
	 * Pre-built MethodFilter that matches all non-bridge methods.
	 * <p>
	 *  预先构建的与所有非桥接方法匹配的MethodFilter
	 * 
	 */
	public static final MethodFilter NON_BRIDGED_METHODS = new MethodFilter() {

		@Override
		public boolean matches(Method method) {
			return !method.isBridge();
		}
	};


	/**
	 * Pre-built MethodFilter that matches all non-bridge methods
	 * which are not declared on {@code java.lang.Object}.
	 * <p>
	 *  预先构建的MethodFilter,匹配所有未在{@code javalangObject}上声明的非桥接方法
	 */
	public static final MethodFilter USER_DECLARED_METHODS = new MethodFilter() {

		@Override
		public boolean matches(Method method) {
			return (!method.isBridge() && method.getDeclaringClass() != Object.class);
		}
	};

}
