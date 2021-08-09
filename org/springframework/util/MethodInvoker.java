/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Helper class that allows for specifying a method to invoke in a declarative
 * fashion, be it static or non-static.
 *
 * <p>Usage: Specify "targetClass"/"targetMethod" or "targetObject"/"targetMethod",
 * optionally specify arguments, prepare the invoker. Afterwards, you may
 * invoke the method any number of times, obtaining the invocation result.
 *
 * <p>
 *  Helper类,允许以声明方式指定方法进行调用,无论是静态还是非静态
 * 
 * <p>用法：指定"targetClass"/"targetMethod"或"targetObject"/"targetMethod",可选地指定参数,准备调用者之后,您可以调用该方法任意次数,获取调用结
 * 果。
 * 
 * 
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @since 19.02.2004
 * @see #prepare
 * @see #invoke
 */
public class MethodInvoker {

	private Class<?> targetClass;

	private Object targetObject;

	private String targetMethod;

	private String staticMethod;

	private Object[] arguments = new Object[0];

	/** The method we will call */
	private Method methodObject;


	/**
	 * Set the target class on which to call the target method.
	 * Only necessary when the target method is static; else,
	 * a target object needs to be specified anyway.
	 * <p>
	 *  设置要调用目标方法的目标类只有当目标方法为静态时才需要;否则,需要指定目标对象
	 * 
	 * 
	 * @see #setTargetObject
	 * @see #setTargetMethod
	 */
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	/**
	 * Return the target class on which to call the target method.
	 * <p>
	 *  返回要调用目标方法的目标类
	 * 
	 */
	public Class<?> getTargetClass() {
		return this.targetClass;
	}

	/**
	 * Set the target object on which to call the target method.
	 * Only necessary when the target method is not static;
	 * else, a target class is sufficient.
	 * <p>
	 *  设置要调用目标方法的目标对象仅当目标方法不是静态时才需要;否则,目标类就足够了
	 * 
	 * 
	 * @see #setTargetClass
	 * @see #setTargetMethod
	 */
	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
		if (targetObject != null) {
			this.targetClass = targetObject.getClass();
		}
	}

	/**
	 * Return the target object on which to call the target method.
	 * <p>
	 *  返回要调用目标方法的目标对象
	 * 
	 */
	public Object getTargetObject() {
		return this.targetObject;
	}

	/**
	 * Set the name of the method to be invoked.
	 * Refers to either a static method or a non-static method,
	 * depending on a target object being set.
	 * <p>
	 *  设置要调用的方法的名称指根据正在设置的目标对象的静态方法或非静态方法
	 * 
	 * 
	 * @see #setTargetClass
	 * @see #setTargetObject
	 */
	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}

	/**
	 * Return the name of the method to be invoked.
	 * <p>
	 * 返回要调用的方法的名称
	 * 
	 */
	public String getTargetMethod() {
		return this.targetMethod;
	}

	/**
	 * Set a fully qualified static method name to invoke,
	 * e.g. "example.MyExampleClass.myExampleMethod".
	 * Convenient alternative to specifying targetClass and targetMethod.
	 * <p>
	 *  设置一个完全限定的静态方法名称来调用,例如"exampleMyExampleClassmyExampleMethod"方便的替代方法来指定targetClass和targetMethod
	 * 
	 * 
	 * @see #setTargetClass
	 * @see #setTargetMethod
	 */
	public void setStaticMethod(String staticMethod) {
		this.staticMethod = staticMethod;
	}

	/**
	 * Set arguments for the method invocation. If this property is not set,
	 * or the Object array is of length 0, a method with no arguments is assumed.
	 * <p>
	 *  设置方法调用的参数如果未设置此属性,或者Object数组的长度为0,则假定无参数的方法
	 * 
	 */
	public void setArguments(Object[] arguments) {
		this.arguments = (arguments != null ? arguments : new Object[0]);
	}

	/**
	 * Return the arguments for the method invocation.
	 * <p>
	 *  返回方法调用的参数
	 * 
	 */
	public Object[] getArguments() {
		return this.arguments;
	}


	/**
	 * Prepare the specified method.
	 * The method can be invoked any number of times afterwards.
	 * <p>
	 *  准备指定的方法以后可以调用该方法任意次数
	 * 
	 * 
	 * @see #getPreparedMethod
	 * @see #invoke
	 */
	public void prepare() throws ClassNotFoundException, NoSuchMethodException {
		if (this.staticMethod != null) {
			int lastDotIndex = this.staticMethod.lastIndexOf('.');
			if (lastDotIndex == -1 || lastDotIndex == this.staticMethod.length()) {
				throw new IllegalArgumentException(
						"staticMethod must be a fully qualified class plus method name: " +
						"e.g. 'example.MyExampleClass.myExampleMethod'");
			}
			String className = this.staticMethod.substring(0, lastDotIndex);
			String methodName = this.staticMethod.substring(lastDotIndex + 1);
			this.targetClass = resolveClassName(className);
			this.targetMethod = methodName;
		}

		Class<?> targetClass = getTargetClass();
		String targetMethod = getTargetMethod();
		if (targetClass == null) {
			throw new IllegalArgumentException("Either 'targetClass' or 'targetObject' is required");
		}
		if (targetMethod == null) {
			throw new IllegalArgumentException("Property 'targetMethod' is required");
		}

		Object[] arguments = getArguments();
		Class<?>[] argTypes = new Class<?>[arguments.length];
		for (int i = 0; i < arguments.length; ++i) {
			argTypes[i] = (arguments[i] != null ? arguments[i].getClass() : Object.class);
		}

		// Try to get the exact method first.
		try {
			this.methodObject = targetClass.getMethod(targetMethod, argTypes);
		}
		catch (NoSuchMethodException ex) {
			// Just rethrow exception if we can't get any match.
			this.methodObject = findMatchingMethod();
			if (this.methodObject == null) {
				throw ex;
			}
		}
	}

	/**
	 * Resolve the given class name into a Class.
	 * <p>The default implementations uses {@code ClassUtils.forName},
	 * using the thread context class loader.
	 * <p>
	 *  将给定的类名称解析为Class <p>默认实现使用{@code ClassUtilsforName},使用线程上下文类加载器
	 * 
	 * 
	 * @param className the class name to resolve
	 * @return the resolved Class
	 * @throws ClassNotFoundException if the class name was invalid
	 */
	protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
		return ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Find a matching method with the specified name for the specified arguments.
	 * <p>
	 *  找到具有指定参数的指定名称的匹配方法
	 * 
	 * 
	 * @return a matching method, or {@code null} if none
	 * @see #getTargetClass()
	 * @see #getTargetMethod()
	 * @see #getArguments()
	 */
	protected Method findMatchingMethod() {
		String targetMethod = getTargetMethod();
		Object[] arguments = getArguments();
		int argCount = arguments.length;

		Method[] candidates = ReflectionUtils.getAllDeclaredMethods(getTargetClass());
		int minTypeDiffWeight = Integer.MAX_VALUE;
		Method matchingMethod = null;

		for (Method candidate : candidates) {
			if (candidate.getName().equals(targetMethod)) {
				Class<?>[] paramTypes = candidate.getParameterTypes();
				if (paramTypes.length == argCount) {
					int typeDiffWeight = getTypeDifferenceWeight(paramTypes, arguments);
					if (typeDiffWeight < minTypeDiffWeight) {
						minTypeDiffWeight = typeDiffWeight;
						matchingMethod = candidate;
					}
				}
			}
		}

		return matchingMethod;
	}

	/**
	 * Return the prepared Method object that will be invoked.
	 * <p>Can for example be used to determine the return type.
	 * <p>
	 * 返回将被调用的准备好的Method对象<p>可以例如用于确定返回类型
	 * 
	 * 
	 * @return the prepared Method object (never {@code null})
	 * @throws IllegalStateException if the invoker hasn't been prepared yet
	 * @see #prepare
	 * @see #invoke
	 */
	public Method getPreparedMethod() throws IllegalStateException {
		if (this.methodObject == null) {
			throw new IllegalStateException("prepare() must be called prior to invoke() on MethodInvoker");
		}
		return this.methodObject;
	}

	/**
	 * Return whether this invoker has been prepared already,
	 * i.e. whether it allows access to {@link #getPreparedMethod()} already.
	 * <p>
	 *  返回此调用者是否已经准备好了,即是否允许访问{@link #getPreparedMethod()}
	 * 
	 */
	public boolean isPrepared() {
		return (this.methodObject != null);
	}

	/**
	 * Invoke the specified method.
	 * <p>The invoker needs to have been prepared before.
	 * <p>
	 *  调用指定的方法<p>调用者需要在之前准备好
	 * 
	 * 
	 * @return the object (possibly null) returned by the method invocation,
	 * or {@code null} if the method has a void return type
	 * @throws InvocationTargetException if the target method threw an exception
	 * @throws IllegalAccessException if the target method couldn't be accessed
	 * @see #prepare
	 */
	public Object invoke() throws InvocationTargetException, IllegalAccessException {
		// In the static case, target will simply be {@code null}.
		Object targetObject = getTargetObject();
		Method preparedMethod = getPreparedMethod();
		if (targetObject == null && !Modifier.isStatic(preparedMethod.getModifiers())) {
			throw new IllegalArgumentException("Target method must not be non-static without a target");
		}
		ReflectionUtils.makeAccessible(preparedMethod);
		return preparedMethod.invoke(targetObject, getArguments());
	}


	/**
	 * Algorithm that judges the match between the declared parameter types of a candidate method
	 * and a specific list of arguments that this method is supposed to be invoked with.
	 * <p>Determines a weight that represents the class hierarchy difference between types and
	 * arguments. A direct match, i.e. type Integer -> arg of class Integer, does not increase
	 * the result - all direct matches means weight 0. A match between type Object and arg of
	 * class Integer would increase the weight by 2, due to the superclass 2 steps up in the
	 * hierarchy (i.e. Object) being the last one that still matches the required type Object.
	 * Type Number and class Integer would increase the weight by 1 accordingly, due to the
	 * superclass 1 step up the hierarchy (i.e. Number) still matching the required type Number.
	 * Therefore, with an arg of type Integer, a constructor (Integer) would be preferred to a
	 * constructor (Number) which would in turn be preferred to a constructor (Object).
	 * All argument weights get accumulated.
	 * <p>Note: This is the algorithm used by MethodInvoker itself and also the algorithm
	 * used for constructor and factory method selection in Spring's bean container (in case
	 * of lenient constructor resolution which is the default for regular bean definitions).
	 * <p>
	 * 判断候选方法的声明参数类型与该方法应该调用的特定参数列表之间的匹配的算法用于确定类型和参数之间的类层次差异的权重直接匹配,即类型整数 - >整数类型的arg,不会增加结果 - 所有直接匹配意味着权重0类
	 * 型对象和类Integer的arg之间的匹配会将权重增加2,这是由于层次结构中的超类2升级(即对象)是仍然匹配所需类型的最后一个对象类型数字和类整数将相应地将权重增加1,这是由于超类1升级层次结构(即数字
	 * )仍然匹配所需类型数因此,使用Integer类型的arg,构造函数(Integer)将优先于构造函数(Number),而构造函数(Number)又会优先于构造函数(Object)。
	 * 
	 * @param paramTypes the parameter types to match
	 * @param args the arguments to match
	 * @return the accumulated weight for all arguments
	 */
	public static int getTypeDifferenceWeight(Class<?>[] paramTypes, Object[] args) {
		int result = 0;
		for (int i = 0; i < paramTypes.length; i++) {
			if (!ClassUtils.isAssignableValue(paramTypes[i], args[i])) {
				return Integer.MAX_VALUE;
			}
			if (args[i] != null) {
				Class<?> paramType = paramTypes[i];
				Class<?> superClass = args[i].getClass().getSuperclass();
				while (superClass != null) {
					if (paramType.equals(superClass)) {
						result = result + 2;
						superClass = null;
					}
					else if (ClassUtils.isAssignable(paramType, superClass)) {
						result = result + 2;
						superClass = superClass.getSuperclass();
					}
					else {
						superClass = null;
					}
				}
				if (paramType.isInterface()) {
					result = result + 1;
				}
			}
		}
		return result;
	}

}
