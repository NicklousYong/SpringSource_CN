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

package org.springframework.aop.aspectj;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

/**
 * Implementation of AspectJ ProceedingJoinPoint interface
 * wrapping an AOP Alliance MethodInvocation.
 *
 * <p><b>Note</b>: the {@code getThis()} method returns the current Spring AOP proxy.
 * The {@code getTarget()} method returns the current Spring AOP target (which may be
 * {@code null} if there is no target), and is a plain POJO without any advice.
 * <b>If you want to call the object and have the advice take effect, use
 * {@code getThis()}.</b> A common example is casting the object to an
 * introduced interface in the implementation of an introduction.
 *
 * <p>Of course there is no such distinction between target and proxy in AspectJ.
 *
 * <p>
 *  实现AspectJ ProceedingJoinPoint界面包装AOP Alliance MethodInvocation
 * 
 * <p> <b>注意</b>：{@code getThis()}方法返回当前的Spring AOP代理{@code getTarget()}方法返回当前的Spring AOP目标(可能是{@code null }
 * 如果没有目标),并且是没有任何建议的普通POJO <b>如果要调用对象并让建议生效,请使用{@code getThis()} </b>一个常见的例子是转换该对象引入介绍介绍的介绍。
 * 
 *  <p>当然,AspectJ中的目标和代理之间没有这样的区别
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Adrian Colyer
 * @author Ramnivas Laddad
 * @since 2.0
 */
public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint, JoinPoint.StaticPart {

	private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	private final ProxyMethodInvocation methodInvocation;

	private Object[] defensiveCopyOfArgs;

	/** Lazily initialized signature object */
	private Signature signature;

	/** Lazily initialized source location object */
	private SourceLocation sourceLocation;


	/**
	 * Create a new MethodInvocationProceedingJoinPoint, wrapping the given
	 * Spring ProxyMethodInvocation object.
	 * <p>
	 *  创建一个新的MethodInvocationProceedingJoinPoint,包装给定的Spring ProxyMethodInvocation对象
	 * 
	 * 
	 * @param methodInvocation the Spring ProxyMethodInvocation object
	 */
	public MethodInvocationProceedingJoinPoint(ProxyMethodInvocation methodInvocation) {
		Assert.notNull(methodInvocation, "MethodInvocation must not be null");
		this.methodInvocation = methodInvocation;
	}

	@Override
	public void set$AroundClosure(AroundClosure aroundClosure) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object proceed() throws Throwable {
		return this.methodInvocation.invocableClone().proceed();
	}

	@Override
	public Object proceed(Object[] arguments) throws Throwable {
		Assert.notNull(arguments, "Argument array passed to proceed cannot be null");
		if (arguments.length != this.methodInvocation.getArguments().length) {
			throw new IllegalArgumentException("Expecting " +
					this.methodInvocation.getArguments().length + " arguments to proceed, " +
					"but was passed " + arguments.length + " arguments");
		}
		this.methodInvocation.setArguments(arguments);
		return this.methodInvocation.invocableClone(arguments).proceed();
	}

	/**
	 * Returns the Spring AOP proxy. Cannot be {@code null}.
	 * <p>
	 *  返回Spring AOP代理不能{@code null}
	 * 
	 */
	@Override
	public Object getThis() {
		return this.methodInvocation.getProxy();
	}

	/**
	 * Returns the Spring AOP target. May be {@code null} if there is no target.
	 * <p>
	 *  返回Spring AOP目标如果没有目标,可能是{@code null}
	 * 
	 */
	@Override
	public Object getTarget() {
		return this.methodInvocation.getThis();
	}

	@Override
	public Object[] getArgs() {
		if (this.defensiveCopyOfArgs == null) {
			Object[] argsSource = this.methodInvocation.getArguments();
			this.defensiveCopyOfArgs = new Object[argsSource.length];
			System.arraycopy(argsSource, 0, this.defensiveCopyOfArgs, 0, argsSource.length);
		}
		return this.defensiveCopyOfArgs;
	}

	@Override
	public Signature getSignature() {
		if (this.signature == null) {
			this.signature = new MethodSignatureImpl();
		}
		return signature;
	}

	@Override
	public SourceLocation getSourceLocation() {
		if (this.sourceLocation == null) {
			this.sourceLocation = new SourceLocationImpl();
		}
		return this.sourceLocation;
	}

	@Override
	public String getKind() {
		return ProceedingJoinPoint.METHOD_EXECUTION;
	}

	@Override
	public int getId() {
		// TODO: It's just an adapter but returning 0 might still have side effects...
		return 0;
	}

	@Override
	public JoinPoint.StaticPart getStaticPart() {
		return this;
	}

	@Override
	public String toShortString() {
		return "execution(" + getSignature().toShortString() + ")";
	}

	@Override
	public String toLongString() {
		return "execution(" + getSignature().toLongString() + ")";
	}

	@Override
	public String toString() {
		return "execution(" + getSignature().toString() + ")";
	}


	/**
	 * Lazily initialized MethodSignature.
	 * <p>
	 *  懒洋洋地初始化了MethodSignature
	 * 
	 */
	private class MethodSignatureImpl implements MethodSignature {

		private volatile String[] parameterNames;

		@Override
		public String getName() {
			return methodInvocation.getMethod().getName();
		}

		@Override
		public int getModifiers() {
			return methodInvocation.getMethod().getModifiers();
		}

		@Override
		public Class<?> getDeclaringType() {
			return methodInvocation.getMethod().getDeclaringClass();
		}

		@Override
		public String getDeclaringTypeName() {
			return methodInvocation.getMethod().getDeclaringClass().getName();
		}

		@Override
		public Class<?> getReturnType() {
			return methodInvocation.getMethod().getReturnType();
		}

		@Override
		public Method getMethod() {
			return methodInvocation.getMethod();
		}

		@Override
		public Class<?>[] getParameterTypes() {
			return methodInvocation.getMethod().getParameterTypes();
		}

		@Override
		public String[] getParameterNames() {
			if (this.parameterNames == null) {
				this.parameterNames = parameterNameDiscoverer.getParameterNames(getMethod());
			}
			return this.parameterNames;
		}

		@Override
		public Class<?>[] getExceptionTypes() {
			return methodInvocation.getMethod().getExceptionTypes();
		}

		@Override
		public String toShortString() {
			return toString(false, false, false, false);
		}

		@Override
		public String toLongString() {
			return toString(true, true, true, true);
		}

		@Override
		public String toString() {
			return toString(false, true, false, true);
		}

		private String toString(boolean includeModifier, boolean includeReturnTypeAndArgs,
				boolean useLongReturnAndArgumentTypeName, boolean useLongTypeName) {

			StringBuilder sb = new StringBuilder();
			if (includeModifier) {
				sb.append(Modifier.toString(getModifiers()));
				sb.append(" ");
			}
			if (includeReturnTypeAndArgs) {
				appendType(sb, getReturnType(), useLongReturnAndArgumentTypeName);
				sb.append(" ");
			}
			appendType(sb, getDeclaringType(), useLongTypeName);
			sb.append(".");
			sb.append(getMethod().getName());
			sb.append("(");
			Class<?>[] parametersTypes = getParameterTypes();
			appendTypes(sb, parametersTypes, includeReturnTypeAndArgs, useLongReturnAndArgumentTypeName);
			sb.append(")");
			return sb.toString();
		}

		private void appendTypes(StringBuilder sb, Class<?>[] types, boolean includeArgs,
				boolean useLongReturnAndArgumentTypeName) {

			if (includeArgs) {
				for (int size = types.length, i = 0; i < size; i++) {
					appendType(sb, types[i], useLongReturnAndArgumentTypeName);
					if (i < size - 1) {
						sb.append(",");
					}
				}
			}
			else {
				if (types.length != 0) {
					sb.append("..");
				}
			}
		}

		private void appendType(StringBuilder sb, Class<?> type, boolean useLongTypeName) {
			if (type.isArray()) {
				appendType(sb, type.getComponentType(), useLongTypeName);
				sb.append("[]");
			}
			else {
				sb.append(useLongTypeName ? type.getName() : type.getSimpleName());
			}
		}
	}


	/**
	 * Lazily initialized SourceLocation.
	 * <p>
	 * Lazily初始化SourceLocation
	 */
	private class SourceLocationImpl implements SourceLocation {

		@Override
		public Class<?> getWithinType() {
			if (methodInvocation.getThis() == null) {
				throw new UnsupportedOperationException("No source location joinpoint available: target is null");
			}
			return methodInvocation.getThis().getClass();
		}

		@Override
		public String getFileName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getLine() {
			throw new UnsupportedOperationException();
		}

		@Override
		@Deprecated
		public int getColumn() {
			throw new UnsupportedOperationException();
		}
	}

}
