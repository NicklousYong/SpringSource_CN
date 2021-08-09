/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Helper for resolving synthetic {@link Method#isBridge bridge Methods} to the
 * {@link Method} being bridged.
 *
 * <p>Given a synthetic {@link Method#isBridge bridge Method} returns the {@link Method}
 * being bridged. A bridge method may be created by the compiler when extending a
 * parameterized type whose methods have parameterized arguments. During runtime
 * invocation the bridge {@link Method} may be invoked and/or used via reflection.
 * When attempting to locate annotations on {@link Method Methods}, it is wise to check
 * for bridge {@link Method Methods} as appropriate and find the bridged {@link Method}.
 *
 * <p>See <a href="http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.12.4.5">
 * The Java Language Specification</a> for more details on the use of bridge methods.
 *
 * <p>
 *  帮助人员将{@link Method#isBridge bridge Methods}解析为{@link方法}被桥接
 * 
 * 给定一个合成的{@link方法#isBridge桥接方法}返回正在桥接的{@link方法}当扩展其参数化参数参数化的参数化类型时,编译器可以创建一个桥接方法在运行时调用期间bridge {链接方法}可以
 * 通过反射来调用和/或使用当尝试在{@link方法方法}上查找注释时,根据需要检查桥接器{@link方法}是明智的,并找到桥接的{@link方法}。
 * 
 *  <p>有关使用桥接方法的更多详细信息,请参阅<a href=\"http://javasuncom/docs/books/jls/third_edition/html/expressionshtml#151245\">
 *  Java语言规范</a>。
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @since 2.0
 */
public abstract class BridgeMethodResolver {

	/**
	 * Find the original method for the supplied {@link Method bridge Method}.
	 * <p>It is safe to call this method passing in a non-bridge {@link Method} instance.
	 * In such a case, the supplied {@link Method} instance is returned directly to the caller.
	 * Callers are <strong>not</strong> required to check for bridging before calling this method.
	 * <p>
	 * 查找所提供的{@link方法桥接方法}的原始方法<p>可以安全地将此方法传递给非桥接{@link Method}实例在这种情况下,提供的{@link方法}实例为直接返回给来电者来电者在调用此方法之前需要
	 * <strong>不</strong>来检查桥接。
	 * 
	 * 
	 * @param bridgeMethod the method to introspect
	 * @return the original method (either the bridged method or the passed-in method
	 * if no more specific one could be found)
	 */
	public static Method findBridgedMethod(Method bridgeMethod) {
		if (bridgeMethod == null || !bridgeMethod.isBridge()) {
			return bridgeMethod;
		}
		// Gather all methods with matching name and parameter size.
		List<Method> candidateMethods = new ArrayList<Method>();
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(bridgeMethod.getDeclaringClass());
		for (Method candidateMethod : methods) {
			if (isBridgedCandidateFor(candidateMethod, bridgeMethod)) {
				candidateMethods.add(candidateMethod);
			}
		}
		// Now perform simple quick check.
		if (candidateMethods.size() == 1) {
			return candidateMethods.get(0);
		}
		// Search for candidate match.
		Method bridgedMethod = searchCandidates(candidateMethods, bridgeMethod);
		if (bridgedMethod != null) {
			// Bridged method found...
			return bridgedMethod;
		}
		else {
			// A bridge method was passed in but we couldn't find the bridged method.
			// Let's proceed with the passed-in method and hope for the best...
			return bridgeMethod;
		}
	}

	/**
	 * Returns {@code true} if the supplied '{@code candidateMethod}' can be
	 * consider a validate candidate for the {@link Method} that is {@link Method#isBridge() bridged}
	 * by the supplied {@link Method bridge Method}. This method performs inexpensive
	 * checks and can be used quickly filter for a set of possible matches.
	 * <p>
	 *  如果提供的"{@code候选方法}"可以通过提供的{@link方法桥接方法来考虑{@link方法#isBridge()桥接的{@link方法})的有效候选者,则返回{@code true} }这种方法
	 * 执行廉价的检查,可以快速过滤一组可能的匹配。
	 * 
	 */
	private static boolean isBridgedCandidateFor(Method candidateMethod, Method bridgeMethod) {
		return (!candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) &&
				candidateMethod.getName().equals(bridgeMethod.getName()) &&
				candidateMethod.getParameterTypes().length == bridgeMethod.getParameterTypes().length);
	}

	/**
	 * Searches for the bridged method in the given candidates.
	 * <p>
	 *  在给定候选人中搜索桥接方法
	 * 
	 * 
	 * @param candidateMethods the List of candidate Methods
	 * @param bridgeMethod the bridge method
	 * @return the bridged method, or {@code null} if none found
	 */
	private static Method searchCandidates(List<Method> candidateMethods, Method bridgeMethod) {
		if (candidateMethods.isEmpty()) {
			return null;
		}
		Method previousMethod = null;
		boolean sameSig = true;
		for (Method candidateMethod : candidateMethods) {
			if (isBridgeMethodFor(bridgeMethod, candidateMethod, bridgeMethod.getDeclaringClass())) {
				return candidateMethod;
			}
			else if (previousMethod != null) {
				sameSig = sameSig &&
						Arrays.equals(candidateMethod.getGenericParameterTypes(), previousMethod.getGenericParameterTypes());
			}
			previousMethod = candidateMethod;
		}
		return (sameSig ? candidateMethods.get(0) : null);
	}

	/**
	 * Determines whether or not the bridge {@link Method} is the bridge for the
	 * supplied candidate {@link Method}.
	 * <p>
	 * 确定桥接器{@link方法}是否为提供的候选者的桥梁{@link方法}
	 * 
	 */
	static boolean isBridgeMethodFor(Method bridgeMethod, Method candidateMethod, Class<?> declaringClass) {
		if (isResolvedTypeMatch(candidateMethod, bridgeMethod, declaringClass)) {
			return true;
		}
		Method method = findGenericDeclaration(bridgeMethod);
		return (method != null && isResolvedTypeMatch(method, candidateMethod, declaringClass));
	}

	/**
	 * Searches for the generic {@link Method} declaration whose erased signature
	 * matches that of the supplied bridge method.
	 * <p>
	 *  搜索其擦除的签名与提供的桥接方法的通用{@link Method}声明
	 * 
	 * 
	 * @throws IllegalStateException if the generic declaration cannot be found
	 */
	private static Method findGenericDeclaration(Method bridgeMethod) {
		// Search parent types for method that has same signature as bridge.
		Class<?> superclass = bridgeMethod.getDeclaringClass().getSuperclass();
		while (superclass != null && Object.class != superclass) {
			Method method = searchForMatch(superclass, bridgeMethod);
			if (method != null && !method.isBridge()) {
				return method;
			}
			superclass = superclass.getSuperclass();
		}

		// Search interfaces.
		Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(bridgeMethod.getDeclaringClass());
		for (Class<?> ifc : interfaces) {
			Method method = searchForMatch(ifc, bridgeMethod);
			if (method != null && !method.isBridge()) {
				return method;
			}
		}

		return null;
	}

	/**
	 * Returns {@code true} if the {@link Type} signature of both the supplied
	 * {@link Method#getGenericParameterTypes() generic Method} and concrete {@link Method}
	 * are equal after resolving all types against the declaringType, otherwise
	 * returns {@code false}.
	 * <p>
	 *  如果在将所有类型与declaringType解析后提供的{@link Method#getGenericParameterTypes()通用方法}和具体{@link Method}的{@link Type}
	 * 签名相等,则返回{@code true},否则返回{@code true}代码虚假}。
	 * 
	 */
	private static boolean isResolvedTypeMatch(
			Method genericMethod, Method candidateMethod, Class<?> declaringClass) {
		Type[] genericParameters = genericMethod.getGenericParameterTypes();
		Class<?>[] candidateParameters = candidateMethod.getParameterTypes();
		if (genericParameters.length != candidateParameters.length) {
			return false;
		}
		for (int i = 0; i < candidateParameters.length; i++) {
			ResolvableType genericParameter = ResolvableType.forMethodParameter(genericMethod, i, declaringClass);
			Class<?> candidateParameter = candidateParameters[i];
			if (candidateParameter.isArray()) {
				// An array type: compare the component type.
				if (!candidateParameter.getComponentType().equals(genericParameter.getComponentType().resolve(Object.class))) {
					return false;
				}
			}
			// A non-array type: compare the type itself.
			if (!candidateParameter.equals(genericParameter.resolve(Object.class))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * If the supplied {@link Class} has a declared {@link Method} whose signature matches
	 * that of the supplied {@link Method}, then this matching {@link Method} is returned,
	 * otherwise {@code null} is returned.
	 * <p>
	 *  如果提供的{@link类}具有与所提供的{@link方法}的签名匹配的声明的{@link方法},则返回匹配的{@link方法},否则返回{@code null}
	 * 
	 */
	private static Method searchForMatch(Class<?> type, Method bridgeMethod) {
		return ReflectionUtils.findMethod(type, bridgeMethod.getName(), bridgeMethod.getParameterTypes());
	}

	/**
	 * Compare the signatures of the bridge method and the method which it bridges. If
	 * the parameter and return types are the same, it is a 'visibility' bridge method
	 * introduced in Java 6 to fix http://bugs.sun.com/view_bug.do?bug_id=6342411.
	 * See also http://stas-blogspot.blogspot.com/2010/03/java-bridge-methods-explained.html
	 * <p>
	 * 比较桥接方法的签名和桥接方法的参数如果参数和返回类型相同,那么在Java 6中引入的"可见性"桥接方法来修复http：// bugssuncom / view_bugdo?bug_id = 634241
	 * 1另请参见HTTP：//的STA-blogspotblogspotcom / 2010/03 / JAVA桥的方法,explainedhtml。
	 * 
	 * @return whether signatures match as described
	 */
	public static boolean isVisibilityBridgeMethodPair(Method bridgeMethod, Method bridgedMethod) {
		if (bridgeMethod == bridgedMethod) {
			return true;
		}
		return (Arrays.equals(bridgeMethod.getParameterTypes(), bridgedMethod.getParameterTypes()) &&
				bridgeMethod.getReturnType().equals(bridgedMethod.getReturnType()));
	}

}
