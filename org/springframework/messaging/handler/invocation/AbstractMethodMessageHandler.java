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

package org.springframework.messaging.handler.invocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.handler.HandlerMethodSelector;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

/**
 * Abstract base class for HandlerMethod-based message handling. Provides most of
 * the logic required to discover handler methods at startup, find a matching handler
 * method at runtime for a given message and invoke it.
 *
 * <p>Also supports discovering and invoking exception handling methods to process
 * exceptions raised during message handling.
 *
 * <p>
 *  基于HandlerMethod的消息处理的抽象基类提供了在启动时发现处理程序方法所需的大部分逻辑,在运行时为给定的消息找到匹配的处理程序方法,并调用它
 * 
 * <p>还支持发现和调用异常处理方法来处理在消息处理期间引发的异常
 * 
 * 
 * @param <T> the type of the Object that contains information mapping a
 * {@link org.springframework.messaging.handler.HandlerMethod} to incoming messages
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractMethodMessageHandler<T>
		implements MessageHandler, ApplicationContextAware, InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private Collection<String> destinationPrefixes = new ArrayList<String>();

	private final List<HandlerMethodArgumentResolver> customArgumentResolvers = new ArrayList<HandlerMethodArgumentResolver>(4);

	private final List<HandlerMethodReturnValueHandler> customReturnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>(4);

	private HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();

	private HandlerMethodReturnValueHandlerComposite returnValueHandlers =new HandlerMethodReturnValueHandlerComposite();

	private ApplicationContext applicationContext;

	private final Map<T, HandlerMethod> handlerMethods = new LinkedHashMap<T, HandlerMethod>();

	private final MultiValueMap<String, T> destinationLookup = new LinkedMultiValueMap<String, T>();

	private final Map<Class<?>, AbstractExceptionHandlerMethodResolver> exceptionHandlerCache =
			new ConcurrentHashMap<Class<?>, AbstractExceptionHandlerMethodResolver>(64);


	/**
	 * When this property is configured only messages to destinations matching
	 * one of the configured prefixes are eligible for handling. When there is a
	 * match the prefix is removed and only the remaining part of the destination
	 * is used for method-mapping purposes.
	 *
	 * <p>By default no prefixes are configured in which case all messages are
	 * eligible for handling.
	 * <p>
	 *  当此属性仅配置与匹配其中一个配置的前缀的目的地的消息有资格处理当匹配时,前缀被删除,并且只有目的地的剩余部分用于方法映射目的
	 * 
	 *  <p>默认情况下,没有配置前缀,在这种情况下,所有消息都有资格处理
	 * 
	 */
	public void setDestinationPrefixes(Collection<String> prefixes) {
		this.destinationPrefixes.clear();
		if (prefixes != null) {
			for (String prefix : prefixes) {
				prefix = prefix.trim();
				this.destinationPrefixes.add(prefix);
			}
		}
	}

	/**
	 * Return the configured destination prefixes.
	 * <p>
	 *  返回配置的目标前缀
	 * 
	 */
	public Collection<String> getDestinationPrefixes() {
		return this.destinationPrefixes;
	}

	/**
	 * Sets the list of custom {@code HandlerMethodArgumentResolver}s that will be used
	 * after resolvers for supported argument type.
	 * <p>
	 *  设置自定义{@code HandlerMethodArgumentResolver}的列表,它将在解析器之后用于支持的参数类型
	 * 
	 * 
	 * @param customArgumentResolvers the list of resolvers; never {@code null}.
	 */
	public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> customArgumentResolvers) {
		this.customArgumentResolvers.clear();
		if (customArgumentResolvers != null) {
			this.customArgumentResolvers.addAll(customArgumentResolvers);
		}
	}

	/**
	 * Return the configured custom argument resolvers, if any.
	 * <p>
	 *  返回配置的自定义参数解析器(如果有)
	 * 
	 */
	public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
		return this.customArgumentResolvers;
	}

	/**
	 * Set the list of custom {@code HandlerMethodReturnValueHandler}s that will be used
	 * after return value handlers for known types.
	 * <p>
	 * 设置在已知类型的返回值处理程序之后将使用的自定义{@code HandlerMethodReturnValueHandler}的列表
	 * 
	 * 
	 * @param customReturnValueHandlers the list of custom return value handlers, never {@code null}.
	 */
	public void setCustomReturnValueHandlers(List<HandlerMethodReturnValueHandler> customReturnValueHandlers) {
		this.customReturnValueHandlers.clear();
		if (customReturnValueHandlers != null) {
			this.customReturnValueHandlers.addAll(customReturnValueHandlers);
		}
	}

	/**
	 * Return the configured custom return value handlers, if any.
	 * <p>
	 *  返回配置的自定义返回值处理程序(如果有)
	 * 
	 */
	public List<HandlerMethodReturnValueHandler> getCustomReturnValueHandlers() {
		return this.customReturnValueHandlers;
	}

	/**
	 * Configure the complete list of supported argument types effectively overriding
	 * the ones configured by default. This is an advanced option. For most use cases
	 * it should be sufficient to use {@link #setCustomArgumentResolvers(java.util.List)}.
	 * <p>
	 *  配置支持的参数类型的完整列表有效地覆盖默认配置的参数类型这是一个高级选项对于大多数用例,使用{@link #setCustomArgumentResolvers(javautilList)}应该是足够
	 * 的。
	 * 
	 */
	public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		if (argumentResolvers == null) {
			this.argumentResolvers.clear();
			return;
		}
		this.argumentResolvers.addResolvers(argumentResolvers);
	}

	public List<HandlerMethodArgumentResolver> getArgumentResolvers() {
		return this.argumentResolvers.getResolvers();
	}

	/**
	 * Configure the complete list of supported return value types effectively overriding
	 * the ones configured by default. This is an advanced option. For most use cases
	 * it should be sufficient to use {@link #setCustomReturnValueHandlers(java.util.List)}
	 * <p>
	 *  配置支持的返回值类型的完整列表有效地覆盖默认配置的返回值类型这是一个高级选项对于大多数用例,使用{@link #setCustomReturnValueHandlers(javautilList)}}
	 * 应该是足够的。
	 * 
	 */
	public void setReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		if (returnValueHandlers == null) {
			this.returnValueHandlers.clear();
			return;
		}
		this.returnValueHandlers.addHandlers(returnValueHandlers);
	}

	public List<HandlerMethodReturnValueHandler> getReturnValueHandlers() {
		return this.returnValueHandlers.getReturnValueHandlers();
	}

	/**
	 * Return a map with all handler methods and their mappings.
	 * <p>
	 *  使用所有处理方法及其映射返回地图
	 * 
	 */
	public Map<T, HandlerMethod> getHandlerMethods() {
		return Collections.unmodifiableMap(this.handlerMethods);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}


	@Override
	public void afterPropertiesSet() {

		if (this.argumentResolvers.getResolvers().isEmpty()) {
			this.argumentResolvers.addResolvers(initArgumentResolvers());
		}

		if (this.returnValueHandlers.getReturnValueHandlers().isEmpty()) {
			this.returnValueHandlers.addHandlers(initReturnValueHandlers());
		}

		for (String beanName : this.applicationContext.getBeanNamesForType(Object.class)) {
			if (isHandler(this.applicationContext.getType(beanName))){
				detectHandlerMethods(beanName);
			}
		}
	}

	/**
	 * Return the list of argument resolvers to use. Invoked only if the resolvers
	 * have not already been set via {@link #setArgumentResolvers(java.util.List)}.
	 * <p>Sub-classes should also take into account custom argument types configured via
	 * {@link #setCustomArgumentResolvers(java.util.List)}.
	 * <p>
	 * 返回参数解析器的列表以使用仅当解析器尚未通过{@link #setArgumentResolvers(javautilList)}设置时调用)</p>子类还应考虑通过{@link #setCustomArgumentResolvers( javautilList)}
	 * 。
	 * 
	 */
	protected abstract List<? extends HandlerMethodArgumentResolver> initArgumentResolvers();

	/**
	 * Return the list of return value handlers to use. Invoked only if the return
	 * value handlers have not already been set via {@link #setReturnValueHandlers(java.util.List)}.
	 * <p>Sub-classes should also take into account custom return value types configured
	 * via {@link #setCustomReturnValueHandlers(java.util.List)}.
	 * <p>
	 *  返回返回值处理程序列表使用仅当返回值处理程序尚未通过{@link #setReturnValueHandlers(javautilList)}设置时返回值处理程序)调用<p>子类还应考虑通过{@ link #setCustomReturnValueHandlers(javautilList)}
	 * 。
	 * 
	 */
	protected abstract List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers();


	/**
	 * Whether the given bean type should be introspected for messaging handling methods.
	 * <p>
	 *  是否应该对给定的bean类型进行内消息传递处理方法
	 * 
	 */
	protected abstract boolean isHandler(Class<?> beanType);

	/**
	 * Detect if the given handler has any methods that can handle messages and if
	 * so register it with the extracted mapping information.
	 * <p>
	 * 检测给定的处理程序是否有任何可以处理消息的方法,并且如果注册了提取的映射信息
	 * 
	 * 
	 * @param handler the handler to check, either an instance of a Spring bean name
	 */
	protected final void detectHandlerMethods(Object handler) {

		Class<?> handlerType = (handler instanceof String) ?
				this.applicationContext.getType((String) handler) : handler.getClass();

		final Class<?> userType = ClassUtils.getUserClass(handlerType);

		Set<Method> methods = HandlerMethodSelector.selectMethods(userType, new ReflectionUtils.MethodFilter() {
			@Override
			public boolean matches(Method method) {
				return getMappingForMethod(method, userType) != null;
			}
		});

		for (Method method : methods) {
			T mapping = getMappingForMethod(method, userType);
			registerHandlerMethod(handler, method, mapping);
		}
	}

	/**
	 * Provide the mapping for a handler method.
	 * <p>
	 *  提供处理程序方法的映射
	 * 
	 * 
	 * @param method the method to provide a mapping for
	 * @param handlerType the handler type, possibly a sub-type of the method's declaring class
	 * @return the mapping, or {@code null} if the method is not mapped
	 */
	protected abstract T getMappingForMethod(Method method, Class<?> handlerType);


	/**
	 * Register a handler method and its unique mapping.
	 * <p>
	 *  注册一个处理方法及其唯一的映射
	 * 
	 * 
	 * @param handler the bean name of the handler or the handler instance
	 * @param method the method to register
	 * @param mapping the mapping conditions associated with the handler method
	 * @throws IllegalStateException if another method was already registered
	 * under the same mapping
	 */
	protected void registerHandlerMethod(Object handler, Method method, T mapping) {

		HandlerMethod newHandlerMethod = createHandlerMethod(handler, method);
		HandlerMethod oldHandlerMethod = handlerMethods.get(mapping);

		if (oldHandlerMethod != null && !oldHandlerMethod.equals(newHandlerMethod)) {
			throw new IllegalStateException("Ambiguous mapping found. Cannot map '" + newHandlerMethod.getBean()
					+ "' bean method \n" + newHandlerMethod + "\nto " + mapping + ": There is already '"
					+ oldHandlerMethod.getBean() + "' bean method\n" + oldHandlerMethod + " mapped.");
		}

		this.handlerMethods.put(mapping, newHandlerMethod);
		if (logger.isInfoEnabled()) {
			logger.info("Mapped \"" + mapping + "\" onto " + newHandlerMethod);
		}

		for (String pattern : getDirectLookupDestinations(mapping)) {
			this.destinationLookup.add(pattern, mapping);
		}
	}

	/**
	 * Create a HandlerMethod instance from an Object handler that is either a handler
	 * instance or a String-based bean name.
	 * <p>
	 *  从处理程序实例或基于String的bean名称的Object处理程序中创建HandlerMethod实例
	 * 
	 */
	protected HandlerMethod createHandlerMethod(Object handler, Method method) {
		HandlerMethod handlerMethod;
		if (handler instanceof String) {
			String beanName = (String) handler;
			handlerMethod = new HandlerMethod(beanName, this.applicationContext, method);
		}
		else {
			handlerMethod = new HandlerMethod(handler, method);
		}
		return handlerMethod;
	}

	/**
	 * Return destinations contained in the mapping that are not patterns and are
	 * therefore suitable for direct lookups.
	 * <p>
	 *  包含在映射中的不是模式的返回目标,因此适合于直接查找
	 * 
	 */
	protected abstract Set<String> getDirectLookupDestinations(T mapping);


	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		String destination = getDestination(message);
		if (destination == null) {
			return;
		}
		String lookupDestination = getLookupDestination(destination);
		if (lookupDestination == null) {
			return;
		}
		MessageHeaderAccessor headerAccessor = MessageHeaderAccessor.getMutableAccessor(message);
		headerAccessor.setHeader(DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER, lookupDestination);
		headerAccessor.setLeaveMutable(true);
		message = MessageBuilder.createMessage(message.getPayload(), headerAccessor.getMessageHeaders());

		if (logger.isDebugEnabled()) {
			logger.debug("Searching methods to handle " + headerAccessor.getShortLogMessage(message.getPayload()));
		}

		handleMessageInternal(message, lookupDestination);
		headerAccessor.setImmutable();
	}

	protected abstract String getDestination(Message<?> message);

	/**
	 * Check whether the given destination (of an incoming message) matches to
	 * one of the configured destination prefixes and if so return the remaining
	 * portion of the destination after the matched prefix.
	 * <p>If there are no matching prefixes, return {@code null}.
	 * <p>If there are no destination prefixes, return the destination as is.
	 * <p>
	 *  检查给定目的地(传入消息)是否与配置的目标前缀之一匹配,如果返回匹配前缀后的目的地的剩余部分<p>如果没有匹配的前缀,则返回{@code null} < p>如果没有目标前缀,返回目的地
	 * 
	 */
	protected String getLookupDestination(String destination) {
		if (destination == null) {
			return null;
		}
		if (CollectionUtils.isEmpty(this.destinationPrefixes)) {
			return destination;
		}
		for (String prefix : this.destinationPrefixes) {
			if (destination.startsWith(prefix)) {
				return destination.substring(prefix.length());
			}
		}
		return null;
	}

	protected void handleMessageInternal(Message<?> message, String lookupDestination) {
		List<Match> matches = new ArrayList<Match>();

		List<T> mappingsByUrl = this.destinationLookup.get(lookupDestination);
		if (mappingsByUrl != null) {
			addMatchesToCollection(mappingsByUrl, message, matches);
		}
		if (matches.isEmpty()) {
			// No direct hits, go through all mappings
			Set<T> allMappings = this.handlerMethods.keySet();
			addMatchesToCollection(allMappings, message, matches);
		}
		if (matches.isEmpty()) {
			handleNoMatch(handlerMethods.keySet(), lookupDestination, message);
			return;
		}
		Comparator<Match> comparator = new MatchComparator(getMappingComparator(message));
		Collections.sort(matches, comparator);

		if (logger.isTraceEnabled()) {
			logger.trace("Found " + matches.size() + " methods: " + matches);
		}

		Match bestMatch = matches.get(0);
		if (matches.size() > 1) {
			Match secondBestMatch = matches.get(1);
			if (comparator.compare(bestMatch, secondBestMatch) == 0) {
				Method m1 = bestMatch.handlerMethod.getMethod();
				Method m2 = secondBestMatch.handlerMethod.getMethod();
				throw new IllegalStateException("Ambiguous handler methods mapped for destination '" +
						lookupDestination + "': {" + m1 + ", " + m2 + "}");
			}
		}

		handleMatch(bestMatch.mapping, bestMatch.handlerMethod, lookupDestination, message);
	}


	private void addMatchesToCollection(Collection<T> mappingsToCheck, Message<?> message, List<Match> matches) {
		for (T mapping : mappingsToCheck) {
			T match = getMatchingMapping(mapping, message);
			if (match != null) {
				matches.add(new Match(match, handlerMethods.get(mapping)));
			}
		}
	}

	/**
	 * Check if a mapping matches the current message and return a possibly
	 * new mapping with conditions relevant to the current request.
	 * <p>
	 * 检查映射是否匹配当前消息,并返回与当前请求相关的条件的可能的新映射
	 * 
	 * 
	 * @param mapping the mapping to get a match for
	 * @param message the message being handled
	 * @return the match or {@code null} if there is no match
	 */
	protected abstract T getMatchingMapping(T mapping, Message<?> message);

	/**
	 * Return a comparator for sorting matching mappings.
	 * The returned comparator should sort 'better' matches higher.
	 * <p>
	 *  返回用于排序匹配映射的比较器返回的比较器应该将更好的匹配排序更高
	 * 
	 * 
	 * @param message the current Message
	 * @return the comparator, never {@code null}
	 */
	protected abstract Comparator<T> getMappingComparator(Message<?> message);


	protected void handleMatch(T mapping, HandlerMethod handlerMethod, String lookupDestination, Message<?> message) {
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking " + handlerMethod.getShortLogMessage());
		}
		handlerMethod = handlerMethod.createWithResolvedBean();
		InvocableHandlerMethod invocable = new InvocableHandlerMethod(handlerMethod);
		invocable.setMessageMethodArgumentResolvers(this.argumentResolvers);
		try {
			Object returnValue = invocable.invoke(message);
			MethodParameter returnType = handlerMethod.getReturnType();
			if (void.class.equals(returnType.getParameterType())) {
				return;
			}
			this.returnValueHandlers.handleReturnValue(returnValue, returnType, message);
		}
		catch (Exception ex) {
			processHandlerMethodException(handlerMethod, ex, message);
		}
		catch (Throwable ex) {
			logger.error("Error while processing message " + message, ex);
		}
	}

	protected void processHandlerMethodException(HandlerMethod handlerMethod, Exception ex, Message<?> message) {
		if (logger.isDebugEnabled()) {
			logger.debug("Searching methods to handle " + ex.getClass().getSimpleName());
		}
		Class<?> beanType = handlerMethod.getBeanType();
		AbstractExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(beanType);
		if (resolver == null) {
			resolver = createExceptionHandlerMethodResolverFor(beanType);
			this.exceptionHandlerCache.put(beanType, resolver);
		}
		Method method = resolver.resolveMethod(ex);
		if (method == null) {
			logger.error("Unhandled exception", ex);
			return;
		}
		InvocableHandlerMethod invocable = new InvocableHandlerMethod(handlerMethod.getBean(), method);
		invocable.setMessageMethodArgumentResolvers(this.argumentResolvers);
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking " + invocable.getShortLogMessage());
		}
		try {
			Object returnValue = invocable.invoke(message, ex);
			MethodParameter returnType = invocable.getReturnType();
			if (void.class.equals(returnType.getParameterType())) {
				return;
			}
			this.returnValueHandlers.handleReturnValue(returnValue, returnType, message);
		}
		catch (Throwable t) {
			logger.error("Error while handling exception", t);
			return;
		}
	}

	protected abstract AbstractExceptionHandlerMethodResolver createExceptionHandlerMethodResolverFor(Class<?> beanType);

	protected void handleNoMatch(Set<T> ts, String lookupDestination, Message<?> message) {
		if (logger.isDebugEnabled()) {
			logger.debug("No matching methods.");
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[prefixes=" + getDestinationPrefixes() + "]";
	}


	/**
	 * A thin wrapper around a matched HandlerMethod and its matched mapping for
	 * the purpose of comparing the best match with a comparator in the context
	 * of a message.
	 * <p>
	 *  在匹配的HandlerMethod及其匹配映射周围的薄包装器,用于在消息的上下文中比较最佳匹配与比较器
	 */
	private class Match {

		private final T mapping;

		private final HandlerMethod handlerMethod;

		private Match(T mapping, HandlerMethod handlerMethod) {
			this.mapping = mapping;
			this.handlerMethod = handlerMethod;
		}

		@Override
		public String toString() {
			return this.mapping.toString();
		}
	}


	private class MatchComparator implements Comparator<Match> {

		private final Comparator<T> comparator;

		public MatchComparator(Comparator<T> comparator) {
			this.comparator = comparator;
		}

		@Override
		public int compare(Match match1, Match match2) {
			return this.comparator.compare(match1.mapping, match2.mapping);
		}
	}

}
