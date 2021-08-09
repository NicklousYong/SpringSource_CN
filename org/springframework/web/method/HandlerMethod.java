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

package org.springframework.web.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Encapsulates information about a handler method consisting of a
 * {@linkplain #getMethod() method} and a {@linkplain #getBean() bean}.
 * Provides convenient access to method parameters, the method return value,
 * method annotations, etc.
 *
 * <p>The class may be created with a bean instance or with a bean name (e.g. lazy-init bean,
 * prototype bean). Use {@link #createWithResolvedBean()} to obtain a {@code HandlerMethod}
 * instance with a bean instance resolved through the associated {@link BeanFactory}.
 *
 * <p>
 * 封装关于由{@linkplain #getMethod()方法}和{@linkplain #getBean()bean组成的处理程序方法的信息)提供对方法参数,方法返回值,方法注释等的方便访问
 * 
 *  <p>可以使用bean实例或bean名称创建类(例如lazy-init bean,prototype bean)使用{@link #createWithResolvedBean()}获取一个解析了be
 * an实例的{@code HandlerMethod}实例通过相关的{@link BeanFactory}。
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 */
public class HandlerMethod {

	/** Logger that is available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private final Object bean;

	private final BeanFactory beanFactory;

	private final Class<?> beanType;

	private final Method method;

	private final Method bridgedMethod;

	private final MethodParameter[] parameters;

	private final HandlerMethod resolvedFromHandlerMethod;


	/**
	 * Create an instance from a bean instance and a method.
	 * <p>
	 *  从bean实例和方法创建一个实例
	 * 
	 */
	public HandlerMethod(Object bean, Method method) {
		Assert.notNull(bean, "Bean is required");
		Assert.notNull(method, "Method is required");
		this.bean = bean;
		this.beanFactory = null;
		this.beanType = ClassUtils.getUserClass(bean);
		this.method = method;
		this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
		this.parameters = initMethodParameters();
		this.resolvedFromHandlerMethod = null;
	}

	/**
	 * Create an instance from a bean instance, method name, and parameter types.
	 * <p>
	 *  从bean实例,方法名称和参数类型创建一个实例
	 * 
	 * 
	 * @throws NoSuchMethodException when the method cannot be found
	 */
	public HandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		Assert.notNull(bean, "Bean is required");
		Assert.notNull(methodName, "Method name is required");
		this.bean = bean;
		this.beanFactory = null;
		this.beanType = ClassUtils.getUserClass(bean);
		this.method = bean.getClass().getMethod(methodName, parameterTypes);
		this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(this.method);
		this.parameters = initMethodParameters();
		this.resolvedFromHandlerMethod = null;
	}

	/**
	 * Create an instance from a bean name, a method, and a {@code BeanFactory}.
	 * The method {@link #createWithResolvedBean()} may be used later to
	 * re-create the {@code HandlerMethod} with an initialized bean.
	 * <p>
	 * 从bean名称,方法和{@code BeanFactory}创建一个实例可以稍后使用{@link #createWithResolvedBean()}方法重新创建具有初始化bean的{@code HandlerMethod}
	 * 。
	 * 
	 */
	public HandlerMethod(String beanName, BeanFactory beanFactory, Method method) {
		Assert.hasText(beanName, "Bean name is required");
		Assert.notNull(beanFactory, "BeanFactory is required");
		Assert.notNull(method, "Method is required");
		this.bean = beanName;
		this.beanFactory = beanFactory;
		this.beanType = ClassUtils.getUserClass(beanFactory.getType(beanName));
		this.method = method;
		this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
		this.parameters = initMethodParameters();
		this.resolvedFromHandlerMethod = null;
	}

	/**
	 * Copy constructor for use in subclasses.
	 * <p>
	 *  复制构造函数以用于子类
	 * 
	 */
	protected HandlerMethod(HandlerMethod handlerMethod) {
		Assert.notNull(handlerMethod, "HandlerMethod is required");
		this.bean = handlerMethod.bean;
		this.beanFactory = handlerMethod.beanFactory;
		this.beanType = handlerMethod.beanType;
		this.method = handlerMethod.method;
		this.bridgedMethod = handlerMethod.bridgedMethod;
		this.parameters = handlerMethod.parameters;
		this.resolvedFromHandlerMethod = handlerMethod.resolvedFromHandlerMethod;
	}

	/**
	 * Re-create HandlerMethod with the resolved handler.
	 * <p>
	 *  用解决的处理程序重新创建HandlerMethod
	 * 
	 */
	private HandlerMethod(HandlerMethod handlerMethod, Object handler) {
		Assert.notNull(handlerMethod, "HandlerMethod is required");
		Assert.notNull(handler, "Handler object is required");
		this.bean = handler;
		this.beanFactory = handlerMethod.beanFactory;
		this.beanType = handlerMethod.beanType;
		this.method = handlerMethod.method;
		this.bridgedMethod = handlerMethod.bridgedMethod;
		this.parameters = handlerMethod.parameters;
		this.resolvedFromHandlerMethod = handlerMethod;
	}


	private MethodParameter[] initMethodParameters() {
		int count = this.bridgedMethod.getParameterTypes().length;
		MethodParameter[] result = new MethodParameter[count];
		for (int i = 0; i < count; i++) {
			result[i] = new HandlerMethodParameter(i);
		}
		return result;
	}

	/**
	 * Returns the bean for this handler method.
	 * <p>
	 *  返回此处理程序方法的bean
	 * 
	 */
	public Object getBean() {
		return this.bean;
	}

	/**
	 * Returns the method for this handler method.
	 * <p>
	 *  返回此处理程序方法的方法
	 * 
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * This method returns the type of the handler for this handler method.
	 * <p>Note that if the bean type is a CGLIB-generated class, the original
	 * user-defined class is returned.
	 * <p>
	 *  此方法返回此处理程序方法的处理程序的类型<p>请注意,如果bean类型是CGLIB生成的类,则返回原始用户定义的类
	 * 
	 */
	public Class<?> getBeanType() {
		return this.beanType;
	}

	/**
	 * If the bean method is a bridge method, this method returns the bridged
	 * (user-defined) method. Otherwise it returns the same method as {@link #getMethod()}.
	 * <p>
	 *  如果bean方法是一个桥接方法,则该方法返回桥接(用户定义)方法,否则返回与{@link #getMethod())相同的方法
	 * 
	 */
	protected Method getBridgedMethod() {
		return this.bridgedMethod;
	}

	/**
	 * Returns the method parameters for this handler method.
	 * <p>
	 *  返回此处理程序方法的方法参数
	 * 
	 */
	public MethodParameter[] getMethodParameters() {
		return this.parameters;
	}

	/**
	 * Return the HandlerMethod from which this HandlerMethod instance was
	 * resolved via {@link #createWithResolvedBean()}.
	 * <p>
	 * 返回通过{@link #createWithResolvedBean()}解析此HandlerMethod实例的HandlerMethod
	 * 
	 */
	public HandlerMethod getResolvedFromHandlerMethod() {
		return this.resolvedFromHandlerMethod;
	}

	/**
	 * Return the HandlerMethod return type.
	 * <p>
	 *  返回HandlerMethod返回类型
	 * 
	 */
	public MethodParameter getReturnType() {
		return new HandlerMethodParameter(-1);
	}

	/**
	 * Return the actual return value type.
	 * <p>
	 *  返回实际的返回值类型
	 * 
	 */
	public MethodParameter getReturnValueType(Object returnValue) {
		return new ReturnValueMethodParameter(returnValue);
	}

	/**
	 * Returns {@code true} if the method return type is void, {@code false} otherwise.
	 * <p>
	 *  如果方法返回类型为void,{@code false}则返回{@code true}
	 * 
	 */
	public boolean isVoid() {
		return Void.TYPE.equals(getReturnType().getParameterType());
	}

	/**
	 * Returns a single annotation on the underlying method traversing its super methods
	 * if no annotation can be found on the given method itself.
	 * <p>Also supports <em>merged</em> composed annotations with attribute
	 * overrides as of Spring Framework 4.2.2.
	 * <p>
	 *  如果在给定的方法本身没有找到注释,则返回遍历其超级方法的底层方法上的单个注释<p>还支持具有Spring Framework 422属性覆盖的<em>合并</em>组合注释
	 * 
	 * 
	 * @param annotationType the type of annotation to introspect the method for
	 * @return the annotation, or {@code null} if none found
	 * @see AnnotatedElementUtils#findMergedAnnotation
	 */
	public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
		return AnnotatedElementUtils.findMergedAnnotation(this.method, annotationType);
	}

	/**
	 * Return whether the parameter is declared with the given annotation type.
	 * <p>
	 *  返回参数是否以给定的注释类型声明
	 * 
	 * 
	 * @param annotationType the annotation type to look for
	 * @since 4.3
	 * @see AnnotatedElementUtils#hasAnnotation
	 */
	public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
		return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
	}

	/**
	 * If the provided instance contains a bean name rather than an object instance,
	 * the bean name is resolved before a {@link HandlerMethod} is created and returned.
	 * <p>
	 *  如果提供的实例包含bean名称而不是对象实例,则在创建并返回{@link HandlerMethod}之前解析bean名称
	 * 
	 */
	public HandlerMethod createWithResolvedBean() {
		Object handler = this.bean;
		if (this.bean instanceof String) {
			String beanName = (String) this.bean;
			handler = this.beanFactory.getBean(beanName);
		}
		return new HandlerMethod(this, handler);
	}

	/**
	 * Return a short representation of this handler method for log message purposes.
	 * <p>
	 * 返回此处理程序方法的简短表示形式,用于日志消息目的
	 * 
	 * 
	 * @since 4.3
	 */
	public String getShortLogMessage() {
		int args = this.method.getParameterTypes().length;
		return getBeanType().getName() + "#" + this.method.getName() + "[" + args + " args]";
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof HandlerMethod)) {
			return false;
		}
		HandlerMethod otherMethod = (HandlerMethod) other;
		return (this.bean.equals(otherMethod.bean) && this.method.equals(otherMethod.method));
	}

	@Override
	public int hashCode() {
		return (this.bean.hashCode() * 31 + this.method.hashCode());
	}

	@Override
	public String toString() {
		return this.method.toGenericString();
	}


	/**
	 * A MethodParameter with HandlerMethod-specific behavior.
	 * <p>
	 *  具有HandlerMethod特定行为的MethodParameter
	 * 
	 */
	protected class HandlerMethodParameter extends SynthesizingMethodParameter {

		public HandlerMethodParameter(int index) {
			super(HandlerMethod.this.bridgedMethod, index);
		}

		protected HandlerMethodParameter(HandlerMethodParameter original) {
			super(original);
		}

		@Override
		public Class<?> getContainingClass() {
			return HandlerMethod.this.getBeanType();
		}

		@Override
		public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
			return HandlerMethod.this.getMethodAnnotation(annotationType);
		}

		@Override
		public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
			return HandlerMethod.this.hasMethodAnnotation(annotationType);
		}

		@Override
		public HandlerMethodParameter clone() {
			return new HandlerMethodParameter(this);
		}
	}


	/**
	 * A MethodParameter for a HandlerMethod return type based on an actual return value.
	 * <p>
	 *  基于实际返回值的HandlerMethod返回类型的MethodParameter
	 */
	private class ReturnValueMethodParameter extends HandlerMethodParameter {

		private final Object returnValue;

		public ReturnValueMethodParameter(Object returnValue) {
			super(-1);
			this.returnValue = returnValue;
		}

		protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
			super(original);
			this.returnValue = original.returnValue;
		}

		@Override
		public Class<?> getParameterType() {
			return (this.returnValue != null ? this.returnValue.getClass() : super.getParameterType());
		}

		@Override
		public ReturnValueMethodParameter clone() {
			return new ReturnValueMethodParameter(this);
		}
	}

}
