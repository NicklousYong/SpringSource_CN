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

package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Static convenience methods for JavaBeans: for instantiating beans,
 * checking bean property types, copying bean properties, etc.
 *
 * <p>Mainly for use within the framework, but to some degree also
 * useful for application classes.
 *
 * <p>
 *  JavaBeans的静态方便方法：用于实例化bean,检查bean属性类型,复制bean属性等
 * 
 *  <p>主要用于框架内,但在一定程度上也适用于应用程序类
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 */
public abstract class BeanUtils {

	private static final Log logger = LogFactory.getLog(BeanUtils.class);

	private static final Set<Class<?>> unknownEditorTypes =
			Collections.newSetFromMap(new ConcurrentReferenceHashMap<Class<?>, Boolean>(64));


	/**
	 * Convenience method to instantiate a class using its no-arg constructor.
	 * <p>
	 * 使用no-arg构造函数实例化一个类的方便方法
	 * 
	 * 
	 * @param clazz class to instantiate
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 * @see Class#newInstance()
	 */
	public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isInterface()) {
			throw new BeanInstantiationException(clazz, "Specified class is an interface");
		}
		try {
			return clazz.newInstance();
		}
		catch (InstantiationException ex) {
			throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
		}
		catch (IllegalAccessException ex) {
			throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
		}
	}

	/**
	 * Instantiate a class using its no-arg constructor.
	 * <p>Note that this method tries to set the constructor accessible
	 * if given a non-accessible (that is, non-public) constructor.
	 * <p>
	 *  使用其no-arg构造函数实例化一个类<p>请注意,如果给定不可访问(即非公开)构造函数,则此方法尝试设置构造函数可访问
	 * 
	 * 
	 * @param clazz class to instantiate
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 * @see Constructor#newInstance
	 */
	public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isInterface()) {
			throw new BeanInstantiationException(clazz, "Specified class is an interface");
		}
		try {
			return instantiateClass(clazz.getDeclaredConstructor());
		}
		catch (NoSuchMethodException ex) {
			throw new BeanInstantiationException(clazz, "No default constructor found", ex);
		}
	}

	/**
	 * Instantiate a class using its no-arg constructor and return the new instance
	 * as the specified assignable type.
	 * <p>Useful in cases where the type of the class to instantiate (clazz) is not
	 * available, but the type desired (assignableTo) is known.
	 * <p>Note that this method tries to set the constructor accessible if given a
	 * non-accessible (that is, non-public) constructor.
	 * <p>
	 *  使用其no-arg构造函数实例化一个类,并返回新的实例作为指定的可分配类型<p>在实例化类的类型(clazz)不可用的情况下有用,但所需类型(assignableTo)已知< p>请注意,如果给定不可
	 * 访问(即非公开)构造函数,则此方法会尝试设置构造函数。
	 * 
	 * 
	 * @param clazz class to instantiate
	 * @param assignableTo type that clazz must be assignableTo
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 * @see Constructor#newInstance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiateClass(Class<?> clazz, Class<T> assignableTo) throws BeanInstantiationException {
		Assert.isAssignable(assignableTo, clazz);
		return (T) instantiateClass(clazz);
	}

	/**
	 * Convenience method to instantiate a class using the given constructor.
	 * <p>Note that this method tries to set the constructor accessible if given a
	 * non-accessible (that is, non-public) constructor.
	 * <p>
	 * 使用给定构造函数实例化类的便利方法<p>请注意,如果给定不可访问(即非公开)构造函数,则此方法尝试设置构造函数可访问
	 * 
	 * 
	 * @param ctor the constructor to instantiate
	 * @param args the constructor arguments to apply
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 * @see Constructor#newInstance
	 */
	public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
		Assert.notNull(ctor, "Constructor must not be null");
		try {
			ReflectionUtils.makeAccessible(ctor);
			return ctor.newInstance(args);
		}
		catch (InstantiationException ex) {
			throw new BeanInstantiationException(ctor, "Is it an abstract class?", ex);
		}
		catch (IllegalAccessException ex) {
			throw new BeanInstantiationException(ctor, "Is the constructor accessible?", ex);
		}
		catch (IllegalArgumentException ex) {
			throw new BeanInstantiationException(ctor, "Illegal arguments for constructor", ex);
		}
		catch (InvocationTargetException ex) {
			throw new BeanInstantiationException(ctor, "Constructor threw exception", ex.getTargetException());
		}
	}

	/**
	 * Find a method with the given method name and the given parameter types,
	 * declared on the given class or one of its superclasses. Prefers public methods,
	 * but will return a protected, package access, or private method too.
	 * <p>Checks {@code Class.getMethod} first, falling back to
	 * {@code findDeclaredMethod}. This allows to find public methods
	 * without issues even in environments with restricted Java security settings.
	 * <p>
	 *  找到一个方法,给定的方法名称和给定的参数类型,在给定的类或其超类之一中声明Prefers的公共方法,但将返回受保护的包访问或私有方法<p>检查{@code ClassgetMethod}首先,回到{@code findDeclaredMethod}
	 * 这样就可以在没有问题的情况下找到公共方法,即使在Java安全设置受限的环境中。
	 * 
	 * 
	 * @param clazz the class to check
	 * @param methodName the name of the method to find
	 * @param paramTypes the parameter types of the method to find
	 * @return the Method object, or {@code null} if not found
	 * @see Class#getMethod
	 * @see #findDeclaredMethod
	 */
	public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
		try {
			return clazz.getMethod(methodName, paramTypes);
		}
		catch (NoSuchMethodException ex) {
			return findDeclaredMethod(clazz, methodName, paramTypes);
		}
	}

	/**
	 * Find a method with the given method name and the given parameter types,
	 * declared on the given class or one of its superclasses. Will return a public,
	 * protected, package access, or private method.
	 * <p>Checks {@code Class.getDeclaredMethod}, cascading upwards to all superclasses.
	 * <p>
	 * 找到具有给定方法名称和给定参数类型的方法,在给定类或其超类中声明将返回公共,受保护的包访问或私有方法<p>检查{@code ClassgetDeclaredMethod},级联到所有超类
	 * 
	 * 
	 * @param clazz the class to check
	 * @param methodName the name of the method to find
	 * @param paramTypes the parameter types of the method to find
	 * @return the Method object, or {@code null} if not found
	 * @see Class#getDeclaredMethod
	 */
	public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, paramTypes);
		}
		catch (NoSuchMethodException ex) {
			if (clazz.getSuperclass() != null) {
				return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
			}
			return null;
		}
	}

	/**
	 * Find a method with the given method name and minimal parameters (best case: none),
	 * declared on the given class or one of its superclasses. Prefers public methods,
	 * but will return a protected, package access, or private method too.
	 * <p>Checks {@code Class.getMethods} first, falling back to
	 * {@code findDeclaredMethodWithMinimalParameters}. This allows for finding public
	 * methods without issues even in environments with restricted Java security settings.
	 * <p>
	 *  在给定的类或其超类中声明的给定方法名称和最小参数(最佳情况：无)找到一个方法Prefers公共方法,但将返回受保护的包访问或私有方法<p>检查{ @code ClassgetMethods}首先,回到
	 * {@code findDeclaredMethodWithMinimalParameters}这样就可以在没有问题的情况下查找公共方法,即使在Java安全设置受限的环境中。
	 * 
	 * 
	 * @param clazz the class to check
	 * @param methodName the name of the method to find
	 * @return the Method object, or {@code null} if not found
	 * @throws IllegalArgumentException if methods of the given name were found but
	 * could not be resolved to a unique method with minimal parameters
	 * @see Class#getMethods
	 * @see #findDeclaredMethodWithMinimalParameters
	 */
	public static Method findMethodWithMinimalParameters(Class<?> clazz, String methodName)
			throws IllegalArgumentException {

		Method targetMethod = findMethodWithMinimalParameters(clazz.getMethods(), methodName);
		if (targetMethod == null) {
			targetMethod = findDeclaredMethodWithMinimalParameters(clazz, methodName);
		}
		return targetMethod;
	}

	/**
	 * Find a method with the given method name and minimal parameters (best case: none),
	 * declared on the given class or one of its superclasses. Will return a public,
	 * protected, package access, or private method.
	 * <p>Checks {@code Class.getDeclaredMethods}, cascading upwards to all superclasses.
	 * <p>
	 * 在给定的类或其超类中声明的给定方法名称和最小参数(最佳情况：无)找到一个方法将返回公共,受保护的包访问或私有方法<p>检查{@code ClassgetDeclaredMethods} ,级联到所有超类
	 * 。
	 * 
	 * 
	 * @param clazz the class to check
	 * @param methodName the name of the method to find
	 * @return the Method object, or {@code null} if not found
	 * @throws IllegalArgumentException if methods of the given name were found but
	 * could not be resolved to a unique method with minimal parameters
	 * @see Class#getDeclaredMethods
	 */
	public static Method findDeclaredMethodWithMinimalParameters(Class<?> clazz, String methodName)
			throws IllegalArgumentException {

		Method targetMethod = findMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
		if (targetMethod == null && clazz.getSuperclass() != null) {
			targetMethod = findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
		}
		return targetMethod;
	}

	/**
	 * Find a method with the given method name and minimal parameters (best case: none)
	 * in the given list of methods.
	 * <p>
	 *  在给定的方法列表中找到具有给定方法名称和最小参数(最佳情况：无)的方法
	 * 
	 * 
	 * @param methods the methods to check
	 * @param methodName the name of the method to find
	 * @return the Method object, or {@code null} if not found
	 * @throws IllegalArgumentException if methods of the given name were found but
	 * could not be resolved to a unique method with minimal parameters
	 */
	public static Method findMethodWithMinimalParameters(Method[] methods, String methodName)
			throws IllegalArgumentException {

		Method targetMethod = null;
		int numMethodsFoundWithCurrentMinimumArgs = 0;
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				int numParams = method.getParameterTypes().length;
				if (targetMethod == null || numParams < targetMethod.getParameterTypes().length) {
					targetMethod = method;
					numMethodsFoundWithCurrentMinimumArgs = 1;
				}
				else if (!method.isBridge() && targetMethod.getParameterTypes().length == numParams) {
					if (targetMethod.isBridge()) {
						// Prefer regular method over bridge...
						targetMethod = method;
					}
					else {
						// Additional candidate with same length
						numMethodsFoundWithCurrentMinimumArgs++;
					}
				}
			}
		}
		if (numMethodsFoundWithCurrentMinimumArgs > 1) {
			throw new IllegalArgumentException("Cannot resolve method '" + methodName +
					"' to a unique method. Attempted to resolve to overloaded method with " +
					"the least number of parameters but there were " +
					numMethodsFoundWithCurrentMinimumArgs + " candidates.");
		}
		return targetMethod;
	}

	/**
	 * Parse a method signature in the form {@code methodName[([arg_list])]},
	 * where {@code arg_list} is an optional, comma-separated list of fully-qualified
	 * type names, and attempts to resolve that signature against the supplied {@code Class}.
	 * <p>When not supplying an argument list ({@code methodName}) the method whose name
	 * matches and has the least number of parameters will be returned. When supplying an
	 * argument type list, only the method whose name and argument types match will be returned.
	 * <p>Note then that {@code methodName} and {@code methodName()} are <strong>not</strong>
	 * resolved in the same way. The signature {@code methodName} means the method called
	 * {@code methodName} with the least number of arguments, whereas {@code methodName()}
	 * means the method called {@code methodName} with exactly 0 arguments.
	 * <p>If no method can be found, then {@code null} is returned.
	 * <p>
	 * 以{@code methodName [([arg_list])]}的形式解析方法签名,其中{@code arg_list}是一个可选的,以逗号分隔的全限定类型名称列表,并尝试根据提供的方式解析该签名{@code Class}
	 *  <p>当不提供参数列表({@code methodName})时,将返回名称匹配且参数最少的方法提供参数类型列表时,仅提供名称和参数的方法类型匹配将返回<p>请注意,{@code methodName}
	 * 和{@code methodName()}以<strong>不</strong>以相同的方式解决签名{@code methodName}是指具有最少数量参数的称为{@code methodName}的方
	 * 法,而{@code methodName()}表示具有正好0个参数的称为{@code methodName}的方法<p>如果没有方法可以找到,然后返回{@code null}。
	 * 
	 * 
	 * @param signature the method signature as String representation
	 * @param clazz the class to resolve the method signature against
	 * @return the resolved Method
	 * @see #findMethod
	 * @see #findMethodWithMinimalParameters
	 */
	public static Method resolveSignature(String signature, Class<?> clazz) {
		Assert.hasText(signature, "'signature' must not be empty");
		Assert.notNull(clazz, "Class must not be null");
		int firstParen = signature.indexOf("(");
		int lastParen = signature.indexOf(")");
		if (firstParen > -1 && lastParen == -1) {
			throw new IllegalArgumentException("Invalid method signature '" + signature +
					"': expected closing ')' for args list");
		}
		else if (lastParen > -1 && firstParen == -1) {
			throw new IllegalArgumentException("Invalid method signature '" + signature +
					"': expected opening '(' for args list");
		}
		else if (firstParen == -1 && lastParen == -1) {
			return findMethodWithMinimalParameters(clazz, signature);
		}
		else {
			String methodName = signature.substring(0, firstParen);
			String[] parameterTypeNames =
					StringUtils.commaDelimitedListToStringArray(signature.substring(firstParen + 1, lastParen));
			Class<?>[] parameterTypes = new Class<?>[parameterTypeNames.length];
			for (int i = 0; i < parameterTypeNames.length; i++) {
				String parameterTypeName = parameterTypeNames[i].trim();
				try {
					parameterTypes[i] = ClassUtils.forName(parameterTypeName, clazz.getClassLoader());
				}
				catch (Throwable ex) {
					throw new IllegalArgumentException("Invalid method signature: unable to resolve type [" +
							parameterTypeName + "] for argument " + i + ". Root cause: " + ex);
				}
			}
			return findMethod(clazz, methodName, parameterTypes);
		}
	}


	/**
	 * Retrieve the JavaBeans {@code PropertyDescriptor}s of a given class.
	 * <p>
	 * 检索给定类的JavaBeans {@code PropertyDescriptor}
	 * 
	 * 
	 * @param clazz the Class to retrieve the PropertyDescriptors for
	 * @return an array of {@code PropertyDescriptors} for the given class
	 * @throws BeansException if PropertyDescriptor look fails
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeansException {
		CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
		return cr.getPropertyDescriptors();
	}

	/**
	 * Retrieve the JavaBeans {@code PropertyDescriptors} for the given property.
	 * <p>
	 *  检索给定属性的JavaBeans {@code PropertyDescriptors}
	 * 
	 * 
	 * @param clazz the Class to retrieve the PropertyDescriptor for
	 * @param propertyName the name of the property
	 * @return the corresponding PropertyDescriptor, or {@code null} if none
	 * @throws BeansException if PropertyDescriptor lookup fails
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName)
			throws BeansException {

		CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
		return cr.getPropertyDescriptor(propertyName);
	}

	/**
	 * Find a JavaBeans {@code PropertyDescriptor} for the given method,
	 * with the method either being the read method or the write method for
	 * that bean property.
	 * <p>
	 *  找到给定方法的JavaBeans {@code PropertyDescriptor},方法是该bean属性的read方法或write方法
	 * 
	 * 
	 * @param method the method to find a corresponding PropertyDescriptor for,
	 * introspecting its declaring class
	 * @return the corresponding PropertyDescriptor, or {@code null} if none
	 * @throws BeansException if PropertyDescriptor lookup fails
	 */
	public static PropertyDescriptor findPropertyForMethod(Method method) throws BeansException {
		return findPropertyForMethod(method, method.getDeclaringClass());
	}

	/**
	 * Find a JavaBeans {@code PropertyDescriptor} for the given method,
	 * with the method either being the read method or the write method for
	 * that bean property.
	 * <p>
	 *  找到给定方法的JavaBeans {@code PropertyDescriptor},方法是该bean属性的read方法或write方法
	 * 
	 * 
	 * @param method the method to find a corresponding PropertyDescriptor for
	 * @param clazz the (most specific) class to introspect for descriptors
	 * @return the corresponding PropertyDescriptor, or {@code null} if none
	 * @throws BeansException if PropertyDescriptor lookup fails
	 * @since 3.2.13
	 */
	public static PropertyDescriptor findPropertyForMethod(Method method, Class<?> clazz) throws BeansException {
		Assert.notNull(method, "Method must not be null");
		PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
		for (PropertyDescriptor pd : pds) {
			if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) {
				return pd;
			}
		}
		return null;
	}

	/**
	 * Find a JavaBeans PropertyEditor following the 'Editor' suffix convention
	 * (e.g. "mypackage.MyDomainClass" -> "mypackage.MyDomainClassEditor").
	 * <p>Compatible to the standard JavaBeans convention as implemented by
	 * {@link java.beans.PropertyEditorManager} but isolated from the latter's
	 * registered default editors for primitive types.
	 * <p>
	 *  在"编辑器"后缀约定之后找到一个JavaBeans PropertyEditor(例如"mypackageMyDomainClass" - >"mypackageMyDomainClassEditor
	 * ")<p>与{@link javabeansPropertyEditorManager}实现的标准JavaBeans约定兼容,但与后者的原始类型的注册默认编辑器隔离。
	 * 
	 * 
	 * @param targetType the type to find an editor for
	 * @return the corresponding editor, or {@code null} if none found
	 */
	public static PropertyEditor findEditorByConvention(Class<?> targetType) {
		if (targetType == null || targetType.isArray() || unknownEditorTypes.contains(targetType)) {
			return null;
		}
		ClassLoader cl = targetType.getClassLoader();
		if (cl == null) {
			try {
				cl = ClassLoader.getSystemClassLoader();
				if (cl == null) {
					return null;
				}
			}
			catch (Throwable ex) {
				// e.g. AccessControlException on Google App Engine
				if (logger.isDebugEnabled()) {
					logger.debug("Could not access system ClassLoader: " + ex);
				}
				return null;
			}
		}
		String editorName = targetType.getName() + "Editor";
		try {
			Class<?> editorClass = cl.loadClass(editorName);
			if (!PropertyEditor.class.isAssignableFrom(editorClass)) {
				if (logger.isWarnEnabled()) {
					logger.warn("Editor class [" + editorName +
							"] does not implement [java.beans.PropertyEditor] interface");
				}
				unknownEditorTypes.add(targetType);
				return null;
			}
			return (PropertyEditor) instantiateClass(editorClass);
		}
		catch (ClassNotFoundException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("No property editor [" + editorName + "] found for type " +
						targetType.getName() + " according to 'Editor' suffix convention");
			}
			unknownEditorTypes.add(targetType);
			return null;
		}
	}

	/**
	 * Determine the bean property type for the given property from the
	 * given classes/interfaces, if possible.
	 * <p>
	 * 如果可能,从给定的类/接口确定给定属性的bean属性类型
	 * 
	 * 
	 * @param propertyName the name of the bean property
	 * @param beanClasses the classes to check against
	 * @return the property type, or {@code Object.class} as fallback
	 */
	public static Class<?> findPropertyType(String propertyName, Class<?>... beanClasses) {
		if (beanClasses != null) {
			for (Class<?> beanClass : beanClasses) {
				PropertyDescriptor pd = getPropertyDescriptor(beanClass, propertyName);
				if (pd != null) {
					return pd.getPropertyType();
				}
			}
		}
		return Object.class;
	}

	/**
	 * Obtain a new MethodParameter object for the write method of the
	 * specified property.
	 * <p>
	 *  为指定属性的write方法获取一个新的MethodParameter对象
	 * 
	 * 
	 * @param pd the PropertyDescriptor for the property
	 * @return a corresponding MethodParameter object
	 */
	public static MethodParameter getWriteMethodParameter(PropertyDescriptor pd) {
		if (pd instanceof GenericTypeAwarePropertyDescriptor) {
			return new MethodParameter(((GenericTypeAwarePropertyDescriptor) pd).getWriteMethodParameter());
		}
		else {
			return new MethodParameter(pd.getWriteMethod(), 0);
		}
	}

	/**
	 * Check if the given type represents a "simple" property:
	 * a primitive, a String or other CharSequence, a Number, a Date,
	 * a URI, a URL, a Locale, a Class, or a corresponding array.
	 * <p>Used to determine properties to check for a "simple" dependency-check.
	 * <p>
	 *  检查给定类型是否表示"简单"属性：一个原语,一个字符串或其他CharSequence,一个数字,一个日期,一个URI,一个URL,一个区域设置,一个类或一个对应的数组<p>用于确定属性检查"简单"依赖
	 * 检查。
	 * 
	 * 
	 * @param clazz the type to check
	 * @return whether the given type represents a "simple" property
	 * @see org.springframework.beans.factory.support.RootBeanDefinition#DEPENDENCY_CHECK_SIMPLE
	 * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#checkDependencies
	 */
	public static boolean isSimpleProperty(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
	}

	/**
	 * Check if the given type represents a "simple" value type:
	 * a primitive, a String or other CharSequence, a Number, a Date,
	 * a URI, a URL, a Locale or a Class.
	 * <p>
	 *  检查给定类型是否表示"简单"值类型：原语,字符串或其他CharSequence,数字,日期,URI,URL,区域设置或类
	 * 
	 * 
	 * @param clazz the type to check
	 * @return whether the given type represents a "simple" value type
	 */
	public static boolean isSimpleValueType(Class<?> clazz) {
		return (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() ||
				CharSequence.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				Date.class.isAssignableFrom(clazz) ||
				URI.class == clazz || URL.class == clazz ||
				Locale.class == clazz || Class.class == clazz);
	}


	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * <p>
	 * 将给定源bean的属性值复制到目标bean中<p>注意：源类和目标类不必匹配,甚至不能相互派生,只要属性与源bean暴露的任何bean属性匹配即可但目标bean不会被忽略忽略<p>这只是一个方便的方法对
	 * 于更复杂的传输需求,请考虑使用完整的BeanWrapper。
	 * 
	 * 
	 * @param source the source bean
	 * @param target the target bean
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target) throws BeansException {
		copyProperties(source, target, null, (String[]) null);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean,
	 * only setting properties defined in the given "editable" class (or interface).
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * <p>
	 * 将给定源bean的属性值复制到给定的目标bean中,只设置在给定的"可编辑"类(或接口)中定义的属性<p>注意：源和目标类不必匹配,甚至不能从彼此,只要属性匹配源bean暴露的任何bean属性,但目标b
	 * ean不会被忽略忽略<p>这只是一个方便的方法对于更复杂的传输需求,请考虑使用完整的BeanWrapper。
	 * 
	 * 
	 * @param source the source bean
	 * @param target the target bean
	 * @param editable the class (or interface) to restrict property setting to
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target, Class<?> editable) throws BeansException {
		copyProperties(source, target, editable, (String[]) null);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean,
	 * ignoring the given "ignoreProperties".
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * <p>
	 * 将给定源bean的属性值复制到给定的目标bean中,忽略给定的"ignoreProperties"<p>注意：只要属性匹配,源和目标类就不必匹配或甚至相互派生源bean暴露但是目标bean不会被忽略的任
	 * 何bean属性将不会被忽略<p>这只是一个方便的方法对于更复杂的传输需求,请考虑使用完整的BeanWrapper。
	 * 
	 * 
	 * @param source the source bean
	 * @param target the target bean
	 * @param ignoreProperties array of property names to ignore
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
		copyProperties(source, target, null, ignoreProperties);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>
	 * 
	 * @param source the source bean
	 * @param target the target bean
	 * @param editable the class (or interface) to restrict property setting to
	 * @param ignoreProperties array of property names to ignore
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	private static void copyProperties(Object source, Object target, Class<?> editable, String... ignoreProperties)
			throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
						"] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		for (PropertyDescriptor targetPd : targetPds) {
			Method writeMethod = targetPd.getWriteMethod();
			if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null) {
					Method readMethod = sourcePd.getReadMethod();
					if (readMethod != null &&
							ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
						try {
							if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
								readMethod.setAccessible(true);
							}
							Object value = readMethod.invoke(source);
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						}
						catch (Throwable ex) {
							throw new FatalBeanException(
									"Could not copy property '" + targetPd.getName() + "' from source to target", ex);
						}
					}
				}
			}
		}
	}

}
