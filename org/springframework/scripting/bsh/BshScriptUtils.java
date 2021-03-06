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

package org.springframework.scripting.bsh;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.Primitive;
import bsh.XThis;

import org.springframework.core.NestedRuntimeException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Utility methods for handling BeanShell-scripted objects.
 *
 * <p>
 *  处理BeanShell脚本对象的实用方法
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class BshScriptUtils {

	/**
	 * Create a new BeanShell-scripted object from the given script source.
	 * <p>With this {@code createBshObject} variant, the script needs to
	 * declare a full class or return an actual instance of the scripted object.
	 * <p>
	 * 从给定的脚本源<p>创建一个新的BeanShell脚本对象使用此{@code createBshObject}变体,脚本需要声明一个完整的类或返回脚本对象的实际实例
	 * 
	 * 
	 * @param scriptSource the script source text
	 * @return the scripted Java object
	 * @throws EvalError in case of BeanShell parsing failure
	 */
	public static Object createBshObject(String scriptSource) throws EvalError {
		return createBshObject(scriptSource, null, null);
	}

	/**
	 * Create a new BeanShell-scripted object from the given script source,
	 * using the default ClassLoader.
	 * <p>The script may either be a simple script that needs a corresponding proxy
	 * generated (implementing the specified interfaces), or declare a full class
	 * or return an actual instance of the scripted object (in which case the
	 * specified interfaces, if any, need to be implemented by that class/instance).
	 * <p>
	 *  从给定的脚本源创建一个新的BeanShell脚本对象,使用默认的ClassLoader <p>该脚本可能是一个简单的脚本,需要生成相应的代理(实现指定的接口),或声明一个完整的类或返回一个实际的脚本对
	 * 象的实例(在这种情况下,指定的接口(如果有的话)需要由该类/实例实现)。
	 * 
	 * 
	 * @param scriptSource the script source text
	 * @param scriptInterfaces the interfaces that the scripted Java object is
	 * supposed to implement (may be {@code null} or empty if the script itself
	 * declares a full class or returns an actual instance of the scripted object)
	 * @return the scripted Java object
	 * @throws EvalError in case of BeanShell parsing failure
	 * @see #createBshObject(String, Class[], ClassLoader)
	 */
	public static Object createBshObject(String scriptSource, Class<?>... scriptInterfaces) throws EvalError {
		return createBshObject(scriptSource, scriptInterfaces, ClassUtils.getDefaultClassLoader());
	}

	/**
	 * Create a new BeanShell-scripted object from the given script source.
	 * <p>The script may either be a simple script that needs a corresponding proxy
	 * generated (implementing the specified interfaces), or declare a full class
	 * or return an actual instance of the scripted object (in which case the
	 * specified interfaces, if any, need to be implemented by that class/instance).
	 * <p>
	 * 从给定的脚本源创建一个新的BeanShell脚本对象脚本可能是一个简单的脚本,需要一个相应的代理生成(实现指定的接口),或者声明一个完整的类或返回一个实际的脚本对象实例(在这种情况下,指定的接口(如果有
	 * 的话)需要由该类/实例实现)。
	 * 
	 * 
	 * @param scriptSource the script source text
	 * @param scriptInterfaces the interfaces that the scripted Java object is
	 * supposed to implement (may be {@code null} or empty if the script itself
	 * declares a full class or returns an actual instance of the scripted object)
	 * @param classLoader the ClassLoader to use for evaluating the script
	 * @return the scripted Java object
	 * @throws EvalError in case of BeanShell parsing failure
	 */
	public static Object createBshObject(String scriptSource, Class<?>[] scriptInterfaces, ClassLoader classLoader)
			throws EvalError {

		Object result = evaluateBshScript(scriptSource, scriptInterfaces, classLoader);
		if (result instanceof Class) {
			Class<?> clazz = (Class<?>) result;
			try {
				return clazz.newInstance();
			}
			catch (Throwable ex) {
				throw new IllegalStateException("Could not instantiate script class: " + clazz.getName(), ex);
			}
		}
		else {
			return result;
		}
	}

	/**
	 * Evaluate the specified BeanShell script based on the given script source,
	 * returning the Class defined by the script.
	 * <p>The script may either declare a full class or return an actual instance of
	 * the scripted object (in which case the Class of the object will be returned).
	 * In any other case, the returned Class will be {@code null}.
	 * <p>
	 *  根据给定的脚本源评估指定的BeanShell脚本,返回由脚本定义的类<p>脚本可以声明一个完整的类或返回脚本对象的实际实例(在这种情况下,该对象的Class将为返回)在任何其他情况下,返回的类将是{@code null}
	 * 。
	 * 
	 * 
	 * @param scriptSource the script source text
	 * @param classLoader the ClassLoader to use for evaluating the script
	 * @return the scripted Java class, or {@code null} if none could be determined
	 * @throws EvalError in case of BeanShell parsing failure
	 */
	static Class<?> determineBshObjectType(String scriptSource, ClassLoader classLoader) throws EvalError {
		Assert.hasText(scriptSource, "Script source must not be empty");
		Interpreter interpreter = new Interpreter();
		interpreter.setClassLoader(classLoader);
		Object result = interpreter.eval(scriptSource);
		if (result instanceof Class) {
			return (Class<?>) result;
		}
		else if (result != null) {
			return result.getClass();
		}
		else {
			return null;
		}
	}

	/**
	 * Evaluate the specified BeanShell script based on the given script source,
	 * keeping a returned script Class or script Object as-is.
	 * <p>The script may either be a simple script that needs a corresponding proxy
	 * generated (implementing the specified interfaces), or declare a full class
	 * or return an actual instance of the scripted object (in which case the
	 * specified interfaces, if any, need to be implemented by that class/instance).
	 * <p>
	 * 根据给定的脚本源来评估指定的BeanShell脚本,保留返回的脚本Class或脚本Object as-is <p>脚本可以是需要生成相应代理(实现指定的接口)的简单脚本,或者声明完整类或返回脚本对象的实
	 * 际实例(在这种情况下,指定的接口(如果有)需要由该类/实例实现)。
	 * 
	 * 
	 * @param scriptSource the script source text
	 * @param scriptInterfaces the interfaces that the scripted Java object is
	 * supposed to implement (may be {@code null} or empty if the script itself
	 * declares a full class or returns an actual instance of the scripted object)
	 * @param classLoader the ClassLoader to use for evaluating the script
	 * @return the scripted Java class or Java object
	 * @throws EvalError in case of BeanShell parsing failure
	 */
	static Object evaluateBshScript(String scriptSource, Class<?>[] scriptInterfaces, ClassLoader classLoader)
			throws EvalError {

		Assert.hasText(scriptSource, "Script source must not be empty");
		Interpreter interpreter = new Interpreter();
		interpreter.setClassLoader(classLoader);
		Object result = interpreter.eval(scriptSource);
		if (result != null) {
			return result;
		}
		else {
			// Simple BeanShell script: Let's create a proxy for it, implementing the given interfaces.
			Assert.notEmpty(scriptInterfaces,
					"Given script requires a script proxy: At least one script interface is required.");
			XThis xt = (XThis) interpreter.eval("return this");
			return Proxy.newProxyInstance(classLoader, scriptInterfaces, new BshObjectInvocationHandler(xt));
		}
	}


	/**
	 * InvocationHandler that invokes a BeanShell script method.
	 * <p>
	 *  InvocationHandler调用BeanShell脚本方法
	 * 
	 */
	private static class BshObjectInvocationHandler implements InvocationHandler {

		private final XThis xt;

		public BshObjectInvocationHandler(XThis xt) {
			this.xt = xt;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (ReflectionUtils.isEqualsMethod(method)) {
				return (isProxyForSameBshObject(args[0]));
			}
			else if (ReflectionUtils.isHashCodeMethod(method)) {
				return this.xt.hashCode();
			}
			else if (ReflectionUtils.isToStringMethod(method)) {
				return "BeanShell object [" + this.xt + "]";
			}
			try {
				Object result = this.xt.invokeMethod(method.getName(), args);
				if (result == Primitive.NULL || result == Primitive.VOID) {
					return null;
				}
				if (result instanceof Primitive) {
					return ((Primitive) result).getValue();
				}
				return result;
			}
			catch (EvalError ex) {
				throw new BshExecutionException(ex);
			}
		}

		private boolean isProxyForSameBshObject(Object other) {
			if (!Proxy.isProxyClass(other.getClass())) {
				return false;
			}
			InvocationHandler ih = Proxy.getInvocationHandler(other);
			return (ih instanceof BshObjectInvocationHandler &&
					this.xt.equals(((BshObjectInvocationHandler) ih).xt));
		}
	}


	/**
	 * Exception to be thrown on script execution failure.
	 * <p>
	 *  在脚本执行失败时抛出异常
	 */
	@SuppressWarnings("serial")
	public static class BshExecutionException extends NestedRuntimeException {

		private BshExecutionException(EvalError ex) {
			super("BeanShell script execution failed", ex);
		}
	}

}
