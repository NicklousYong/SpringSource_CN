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

package org.springframework.context.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * {@link GenericApplicationListener} adapter that delegates the processing of
 * an event to an {@link EventListener} annotated method.
 *
 * <p>Delegates to {@link #processEvent(ApplicationEvent)} to give sub-classes
 * a chance to deviate from the default. Unwraps the content of a
 * {@link PayloadApplicationEvent} if necessary to allow method declaration
 * to define any arbitrary event type. If a condition is defined, it is
 * evaluated prior to invoking the underlying method.
 *
 * <p>
 *  {@link GenericApplicationListener}适配器,将事件的处理委托给{@link EventListener}注释方法
 * 
 * <p>代表{@link #processEvent(ApplicationEvent)}让子类有机会偏离默认状态展开{@link PayloadApplicationEvent}的内容,以便允许方法声明
 * 定义任意事件类型如果条件被定义,它在调用底层方法之前进行评估。
 * 
 * 
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.2
 */
public class ApplicationListenerMethodAdapter implements GenericApplicationListener {

	protected final Log logger = LogFactory.getLog(getClass());

	private final String beanName;

	private final Method method;

	private final Class<?> targetClass;

	private final Method bridgedMethod;

	private final List<ResolvableType> declaredEventTypes;

	private final String condition;

	private final int order;

	private final AnnotatedElementKey methodKey;

	private ApplicationContext applicationContext;

	private EventExpressionEvaluator evaluator;


	public ApplicationListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
		this.beanName = beanName;
		this.method = method;
		this.targetClass = targetClass;
		this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);

		EventListener ann = AnnotatedElementUtils.findMergedAnnotation(method, EventListener.class);
		this.declaredEventTypes = resolveDeclaredEventTypes(method, ann);
		this.condition = (ann != null ? ann.condition() : null);
		this.order = resolveOrder(method);

		this.methodKey = new AnnotatedElementKey(method, targetClass);
	}


	private List<ResolvableType> resolveDeclaredEventTypes(Method method, EventListener ann) {
		int count = method.getParameterTypes().length;
		if (count > 1) {
			throw new IllegalStateException(
					"Maximum one parameter is allowed for event listener method: " + method);
		}
		if (ann != null && ann.classes().length > 0) {
			List<ResolvableType> types = new ArrayList<ResolvableType>(ann.classes().length);
			for (Class<?> eventType : ann.classes()) {
				types.add(ResolvableType.forClass(eventType));
			}
			return types;
		}
		else {
			if (count == 0) {
				throw new IllegalStateException(
						"Event parameter is mandatory for event listener method: " + method);
			}
			return Collections.singletonList(ResolvableType.forMethodParameter(method, 0));
		}
	}

	private int resolveOrder(Method method) {
		Order ann = AnnotatedElementUtils.findMergedAnnotation(method, Order.class);
		return (ann != null ? ann.value() : 0);
	}

	/**
	 * Initialize this instance.
	 * <p>
	 *  初始化此实例
	 * 
	 */
	void init(ApplicationContext applicationContext, EventExpressionEvaluator evaluator) {
		this.applicationContext = applicationContext;
		this.evaluator = evaluator;
	}


	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		processEvent(event);
	}

	@Override
	public boolean supportsEventType(ResolvableType eventType) {
		for (ResolvableType declaredEventType : this.declaredEventTypes) {
			if (declaredEventType.isAssignableFrom(eventType)) {
				return true;
			}
			else if (PayloadApplicationEvent.class.isAssignableFrom(eventType.getRawClass())) {
				ResolvableType payloadType = eventType.as(PayloadApplicationEvent.class).getGeneric();
				if (declaredEventType.isAssignableFrom(payloadType)) {
					return true;
				}
			}
		}
		return eventType.hasUnresolvableGenerics();
	}

	@Override
	public boolean supportsSourceType(Class<?> sourceType) {
		return true;
	}

	@Override
	public int getOrder() {
		return this.order;
	}


	/**
	 * Process the specified {@link ApplicationEvent}, checking if the condition
	 * match and handling non-null result, if any.
	 * <p>
	 *  处理指定的{@link ApplicationEvent},检查条件是否匹配并处理非空结果(如果有)
	 * 
	 */
	public void processEvent(ApplicationEvent event) {
		Object[] args = resolveArguments(event);
		if (shouldHandle(event, args)) {
			Object result = doInvoke(args);
			if (result != null) {
				handleResult(result);
			}
			else {
				logger.trace("No result object given - no result to handle");
			}
		}
	}

	/**
	 * Resolve the method arguments to use for the specified {@link ApplicationEvent}.
	 * <p>These arguments will be used to invoke the method handled by this instance. Can
	 * return {@code null} to indicate that no suitable arguments could be resolved and
	 * therefore the method should not be invoked at all for the specified event.
	 * <p>
	 *  解决方法参数用于指定的{@link ApplicationEvent} <p>这些参数将用于调用此实例处理的方法可以返回{@code null}以指示没有合适的参数可以解析,因此该方法不应该为指定的事
	 * 件调用。
	 * 
	 */
	protected Object[] resolveArguments(ApplicationEvent event) {
		ResolvableType declaredEventType = getResolvableType(event);
		if (declaredEventType == null) {
			return null;
		}
		if (this.method.getParameterTypes().length == 0) {
			return new Object[0];
		}
		if (!ApplicationEvent.class.isAssignableFrom(declaredEventType.getRawClass()) &&
				event instanceof PayloadApplicationEvent) {
			return new Object[] {((PayloadApplicationEvent) event).getPayload()};
		}
		else {
			return new Object[] {event};
		}
	}

	protected void handleResult(Object result) {
		if (result.getClass().isArray()) {
			Object[] events = ObjectUtils.toObjectArray(result);
			for (Object event : events) {
				publishEvent(event);
			}
		}
		else if (result instanceof Collection<?>) {
			Collection<?> events = (Collection<?>) result;
			for (Object event : events) {
				publishEvent(event);
			}
		}
		else {
			publishEvent(result);
		}
	}

	private void publishEvent(Object event) {
		if (event != null) {
			Assert.notNull(this.applicationContext, "ApplicationContext must no be null");
			this.applicationContext.publishEvent(event);
		}
	}

	private boolean shouldHandle(ApplicationEvent event, Object[] args) {
		if (args == null) {
			return false;
		}
		String condition = getCondition();
		if (StringUtils.hasText(condition)) {
			Assert.notNull(this.evaluator, "EventExpressionEvaluator must no be null");
			EvaluationContext evaluationContext = this.evaluator.createEvaluationContext(
					event, this.targetClass, this.method, args, this.applicationContext);
			return this.evaluator.condition(condition, this.methodKey, evaluationContext);
		}
		return true;
	}

	/**
	 * Invoke the event listener method with the given argument values.
	 * <p>
	 * 使用给定的参数值调用事件侦听器方法
	 * 
	 */
	protected Object doInvoke(Object... args) {
		Object bean = getTargetBean();
		ReflectionUtils.makeAccessible(this.bridgedMethod);
		try {
			return this.bridgedMethod.invoke(bean, args);
		}
		catch (IllegalArgumentException ex) {
			assertTargetBean(this.bridgedMethod, bean, args);
			throw new IllegalStateException(getInvocationErrorMessage(bean, ex.getMessage(), args), ex);
		}
		catch (IllegalAccessException ex) {
			throw new IllegalStateException(getInvocationErrorMessage(bean, ex.getMessage(), args), ex);
		}
		catch (InvocationTargetException ex) {
			// Throw underlying exception
			Throwable targetException = ex.getTargetException();
			if (targetException instanceof RuntimeException) {
				throw (RuntimeException) targetException;
			}
			else {
				String msg = getInvocationErrorMessage(bean, "Failed to invoke event listener method", args);
				throw new UndeclaredThrowableException(targetException, msg);
			}
		}
	}

	/**
	 * Return the target bean instance to use.
	 * <p>
	 *  返回要使用的目标bean实例
	 * 
	 */
	protected Object getTargetBean() {
		Assert.notNull(this.applicationContext, "ApplicationContext must no be null");
		return this.applicationContext.getBean(this.beanName);
	}

	/**
	 * Return the condition to use.
	 * <p>Matches the {@code condition} attribute of the {@link EventListener}
	 * annotation or any matching attribute on a composed annotation that
	 * is meta-annotated with {@code @EventListener}.
	 * <p>
	 *  返回条件使用<p>匹配{@link EventListener}注释的{@code条件}属性或与{@code @EventListener}进行元注释的组合注释上的任何匹配属性匹配
	 * 
	 */
	protected String getCondition() {
		return this.condition;
	}

	/**
	 * Add additional details such as the bean type and method signature to
	 * the given error message.
	 * <p>
	 *  在给定的错误消息中添加其他详细信息,例如bean类型和方法签名
	 * 
	 * 
	 * @param message error message to append the HandlerMethod details to
	 */
	protected String getDetailedErrorMessage(Object bean, String message) {
		StringBuilder sb = new StringBuilder(message).append("\n");
		sb.append("HandlerMethod details: \n");
		sb.append("Bean [").append(bean.getClass().getName()).append("]\n");
		sb.append("Method [").append(this.bridgedMethod.toGenericString()).append("]\n");
		return sb.toString();
	}

	/**
	 * Assert that the target bean class is an instance of the class where the given
	 * method is declared. In some cases the actual bean instance at event-
	 * processing time may be a JDK dynamic proxy (lazy initialization, prototype
	 * beans, and others). Event listener beans that require proxying should prefer
	 * class-based proxy mechanisms.
	 * <p>
	 *  声明目标bean类是声明给定方法的类的实例在某些情况下,事件处理时间的实际bean实例可能是JDK动态代理(延迟初始化,原型bean等)事件侦听器bean要求代理应该更喜欢基于类的代理机制
	 */
	private void assertTargetBean(Method method, Object targetBean, Object[] args) {
		Class<?> methodDeclaringClass = method.getDeclaringClass();
		Class<?> targetBeanClass = targetBean.getClass();
		if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
			String msg = "The event listener method class '" + methodDeclaringClass.getName() +
					"' is not an instance of the actual bean class '" +
					targetBeanClass.getName() + "'. If the bean requires proxying " +
					"(e.g. due to @Transactional), please use class-based proxying.";
			throw new IllegalStateException(getInvocationErrorMessage(targetBean, msg, args));
		}
	}

	private String getInvocationErrorMessage(Object bean, String message, Object[] resolvedArgs) {
		StringBuilder sb = new StringBuilder(getDetailedErrorMessage(bean, message));
		sb.append("Resolved arguments: \n");
		for (int i = 0; i < resolvedArgs.length; i++) {
			sb.append("[").append(i).append("] ");
			if (resolvedArgs[i] == null) {
				sb.append("[null] \n");
			}
			else {
				sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
				sb.append("[value=").append(resolvedArgs[i]).append("]\n");
			}
		}
		return sb.toString();
	}


	private ResolvableType getResolvableType(ApplicationEvent event) {
		ResolvableType payloadType = null;
		if (event instanceof PayloadApplicationEvent) {
			PayloadApplicationEvent<?> payloadEvent = (PayloadApplicationEvent<?>) event;
			payloadType = payloadEvent.getResolvableType().as(
					PayloadApplicationEvent.class).getGeneric(0);
		}
		for (ResolvableType declaredEventType : this.declaredEventTypes) {
			if (!ApplicationEvent.class.isAssignableFrom(declaredEventType.getRawClass())
					&& payloadType != null) {
				if (declaredEventType.isAssignableFrom(payloadType)) {
					return declaredEventType;
				}
			}
			if (declaredEventType.getRawClass().isAssignableFrom(event.getClass())) {
				return declaredEventType;
			}
		}
		return null;
	}


	@Override
	public String toString() {
		return this.method.toGenericString();
	}

}
