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

package org.springframework.messaging.simp.annotation.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.support.AnnotationExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.annotation.support.DestinationVariableMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.HeaderMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.HeadersMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.MessageMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.AbstractMethodMessageHandler;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageMappingInfo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageTypeMessageCondition;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * A handler for messages delegating to {@link MessageMapping @MessageMapping}
 * and {@link SubscribeMapping @SubscribeMapping} annotated methods.
 *
 * <p>Supports Ant-style path patterns with template variables.
 *
 * <p>
 *  用于委托{@link MessageMapping @MessageMapping}和{@link SubscribeMapping @SubscribeMapping}注释方法的邮件处理程序
 * 
 *  <p>使用模板变量支持Ant样式路径模式
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 4.0
 */
public class SimpAnnotationMethodMessageHandler extends AbstractMethodMessageHandler<SimpMessageMappingInfo>
		implements SmartLifecycle {

	private final SubscribableChannel clientInboundChannel;

	private final SimpMessageSendingOperations clientMessagingTemplate;

	private final SimpMessageSendingOperations brokerTemplate;

	private MessageConverter messageConverter;

	private ConversionService conversionService = new DefaultFormattingConversionService();

	private PathMatcher pathMatcher = new AntPathMatcher();

	private boolean slashPathSeparator = true;

	private Validator validator;

	private MessageHeaderInitializer headerInitializer;

	private final Object lifecycleMonitor = new Object();

	private volatile boolean running = false;


	/**
	 * Create an instance of SimpAnnotationMethodMessageHandler with the given
	 * message channels and broker messaging template.
	 * <p>
	 * 使用给定的消息通道和代理消息传递模板创建SimpAnnotationMethodMessageHandler的实例
	 * 
	 * 
	 * @param clientInboundChannel the channel for receiving messages from clients (e.g. WebSocket clients)
	 * @param clientOutboundChannel the channel for messages to clients (e.g. WebSocket clients)
	 * @param brokerTemplate a messaging template to send application messages to the broker
	 */
	public SimpAnnotationMethodMessageHandler(SubscribableChannel clientInboundChannel,
			MessageChannel clientOutboundChannel, SimpMessageSendingOperations brokerTemplate) {

		Assert.notNull(clientInboundChannel, "clientInboundChannel must not be null");
		Assert.notNull(clientOutboundChannel, "clientOutboundChannel must not be null");
		Assert.notNull(brokerTemplate, "brokerTemplate must not be null");

		this.clientInboundChannel = clientInboundChannel;
		this.clientMessagingTemplate = new SimpMessagingTemplate(clientOutboundChannel);
		this.brokerTemplate = brokerTemplate;

		Collection<MessageConverter> converters = new ArrayList<MessageConverter>();
		converters.add(new StringMessageConverter());
		converters.add(new ByteArrayMessageConverter());
		this.messageConverter = new CompositeMessageConverter(converters);
	}


	/**
	 * {@inheritDoc}
	 * <p>Destination prefixes are expected to be slash-separated Strings and
	 * therefore a slash is automatically appended where missing to ensure a
	 * proper prefix-based match (i.e. matching complete segments).
	 *
	 * <p>Note however that the remaining portion of a destination after the
	 * prefix may use a different separator (e.g. commonly "." in messaging)
	 * depending on the configured {@code PathMatcher}.
	 * <p>
	 *  {@inheritDoc} <p>目标前缀预计是斜杠分隔的字符串,因此,自动附加斜杠以避免丢失,以确保正确的基于前缀的匹配(即匹配完整的段)
	 * 
	 *  <p>请注意,前缀之后的目的地的剩余部分可能会使用不同的分隔符(例如通常在消息传递中),具体取决于配置的{@code PathMatcher}
	 * 
	 */
	@Override
	public void setDestinationPrefixes(Collection<String> prefixes) {
		super.setDestinationPrefixes(appendSlashes(prefixes));
	}

	private static Collection<String> appendSlashes(Collection<String> prefixes) {
		if (CollectionUtils.isEmpty(prefixes)) {
			return prefixes;
		}
		Collection<String> result = new ArrayList<String>(prefixes.size());
		for (String prefix : prefixes) {
			if (!prefix.endsWith("/")) {
				prefix = prefix + "/";
			}
			result.add(prefix);
		}
		return result;
	}

	/**
	 * Configure a {@link MessageConverter} to use to convert the payload of a message
	 * from serialize form with a specific MIME type to an Object matching the target
	 * method parameter. The converter is also used when sending message to the message
	 * broker.
	 * <p>
	 *  配置{@link MessageConverter}用于将具有特定MIME类型的序列化形式的消息的有效内容转换为与目标方法参数匹配的对象。当向消息代理发送消息时也使用转换器
	 * 
	 * 
	 * @see CompositeMessageConverter
	 */
	public void setMessageConverter(MessageConverter converter) {
		this.messageConverter = converter;
		if (converter != null) {
			((AbstractMessageSendingTemplate<?>) this.clientMessagingTemplate).setMessageConverter(converter);
		}
	}

	/**
	 * Return the configured {@link MessageConverter}.
	 * <p>
	 * 返回配置的{@link MessageConverter}
	 * 
	 */
	public MessageConverter getMessageConverter() {
		return this.messageConverter;
	}

	/**
	 * Configure a {@link ConversionService} to use when resolving method arguments, for
	 * example message header values.
	 * <p>By default an instance of {@link DefaultFormattingConversionService} is used.
	 * <p>
	 *  配置{@link ConversionService}以在解析方法参数时使用,例如消息头值<p>默认情况下,使用{@link DefaultFormattingConversionService}的实
	 * 例。
	 * 
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * The configured {@link ConversionService}.
	 * <p>
	 *  配置的{@link ConversionService}
	 * 
	 */
	public ConversionService getConversionService() {
		return this.conversionService;
	}

	/**
	 * Set the PathMatcher implementation to use for matching destinations
	 * against configured destination patterns.
	 * <p>By default AntPathMatcher is used
	 * <p>
	 *  设置PathMatcher实现用于匹配目的地与已配置的目标模式<p>默认使用AntPathMatcher
	 * 
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		Assert.notNull(pathMatcher, "PathMatcher must not be null");
		this.pathMatcher = pathMatcher;
		this.slashPathSeparator = this.pathMatcher.combine("a", "a").equals("a/a");
	}

	/**
	 * Return the PathMatcher implementation to use for matching destinations
	 * <p>
	 *  返回PathMatcher实现以用于匹配的目的地
	 * 
	 */
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	/**
	 * The configured Validator instance
	 * <p>
	 *  配置的Validator实例
	 * 
	 */
	public Validator getValidator() {
		return this.validator;
	}

	/**
	 * Set the Validator instance used for validating @Payload arguments
	 * <p>
	 *  设置用于验证@Payload参数的Validator实例
	 * 
	 * 
	 * @see org.springframework.validation.annotation.Validated
	 * @see PayloadArgumentResolver
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Configure a {@link MessageHeaderInitializer} to pass on to
	 * {@link org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler}s
	 * that send messages from controller return values.
	 *
	 * <p>By default this property is not set.
	 * <p>
	 * 配置{@link MessageHeaderInitializer}传递给从控制器返回值发送消息的{@link orgspringframeworkmessaginghandlerinvocationHandlerMethodReturnValueHandler}
	 * 。
	 * 
	 *  <p>默认情况下,此属性未设置
	 */
	public void setHeaderInitializer(MessageHeaderInitializer headerInitializer) {
		this.headerInitializer = headerInitializer;
	}

	/**
	/* <p>
	/* 
	/* 
	 * @return the configured header initializer.
	 */
	public MessageHeaderInitializer getHeaderInitializer() {
		return this.headerInitializer;
	}


	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public int getPhase() {
		return Integer.MAX_VALUE;
	}

	@Override
	public final boolean isRunning() {
		synchronized (this.lifecycleMonitor) {
			return this.running;
		}
	}

	@Override
	public final void start() {
		synchronized (this.lifecycleMonitor) {
			this.clientInboundChannel.subscribe(this);
			this.running = true;
		}
	}

	@Override
	public final void stop() {
		synchronized (this.lifecycleMonitor) {
			this.running = false;
			this.clientInboundChannel.unsubscribe(this);
		}
	}

	@Override
	public final void stop(Runnable callback) {
		synchronized (this.lifecycleMonitor) {
			stop();
			callback.run();
		}
	}


	protected List<HandlerMethodArgumentResolver> initArgumentResolvers() {
		ConfigurableBeanFactory beanFactory =
				(ClassUtils.isAssignableValue(ConfigurableApplicationContext.class, getApplicationContext())) ?
						((ConfigurableApplicationContext) getApplicationContext()).getBeanFactory() : null;

		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

		// Annotation-based argument resolution
		resolvers.add(new HeaderMethodArgumentResolver(this.conversionService, beanFactory));
		resolvers.add(new HeadersMethodArgumentResolver());
		resolvers.add(new DestinationVariableMethodArgumentResolver(this.conversionService));

		// Type-based argument resolution
		resolvers.add(new PrincipalMethodArgumentResolver());
		resolvers.add(new MessageMethodArgumentResolver());

		resolvers.addAll(getCustomArgumentResolvers());
		resolvers.add(new PayloadArgumentResolver(this.messageConverter,
				(this.validator != null ? this.validator : new NoOpValidator())));

		return resolvers;
	}

	@Override
	protected List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers() {
		List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

		// Annotation-based return value types
		SendToMethodReturnValueHandler sth = new SendToMethodReturnValueHandler(this.brokerTemplate, true);
		sth.setHeaderInitializer(this.headerInitializer);
		handlers.add(sth);

		SubscriptionMethodReturnValueHandler sh = new SubscriptionMethodReturnValueHandler(this.clientMessagingTemplate);
		sh.setHeaderInitializer(this.headerInitializer);
		handlers.add(sh);

		// custom return value types
		handlers.addAll(getCustomReturnValueHandlers());

		// catch-all
		sth = new SendToMethodReturnValueHandler(this.brokerTemplate, false);
		sth.setHeaderInitializer(this.headerInitializer);
		handlers.add(sth);

		return handlers;
	}


	@Override
	protected boolean isHandler(Class<?> beanType) {
		return (AnnotationUtils.findAnnotation(beanType, Controller.class) != null);
	}

	@Override
	protected SimpMessageMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		MessageMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, MessageMapping.class);
		MessageMapping messageAnnot = AnnotationUtils.findAnnotation(method, MessageMapping.class);
		if (messageAnnot != null) {
			SimpMessageMappingInfo result = createMessageMappingCondition(messageAnnot);
			if (typeAnnotation != null) {
				result = createMessageMappingCondition(typeAnnotation).combine(result);
			}
			return result;
		}
		SubscribeMapping subsribeAnnotation = AnnotationUtils.findAnnotation(method, SubscribeMapping.class);
		if (subsribeAnnotation != null) {
			SimpMessageMappingInfo result = createSubscribeCondition(subsribeAnnotation);
			if (typeAnnotation != null) {
				result = createMessageMappingCondition(typeAnnotation).combine(result);
			}
			return result;
		}
		return null;
	}

	private SimpMessageMappingInfo createMessageMappingCondition(MessageMapping annotation) {
		return new SimpMessageMappingInfo(SimpMessageTypeMessageCondition.MESSAGE,
				new DestinationPatternsMessageCondition(annotation.value(), this.pathMatcher));
	}

	private SimpMessageMappingInfo createSubscribeCondition(SubscribeMapping annotation) {
		return new SimpMessageMappingInfo(SimpMessageTypeMessageCondition.SUBSCRIBE,
				new DestinationPatternsMessageCondition(annotation.value(), this.pathMatcher));
	}

	@Override
	protected Set<String> getDirectLookupDestinations(SimpMessageMappingInfo mapping) {
		Set<String> result = new LinkedHashSet<String>();
		for (String pattern : mapping.getDestinationConditions().getPatterns()) {
			if (!this.pathMatcher.isPattern(pattern)) {
				result.add(pattern);
			}
		}
		return result;
	}

	@Override
	protected String getDestination(Message<?> message) {
		return SimpMessageHeaderAccessor.getDestination(message.getHeaders());
	}

	@Override
	protected String getLookupDestination(String destination) {
		if (destination == null) {
			return null;
		}
		if (CollectionUtils.isEmpty(getDestinationPrefixes())) {
			return destination;
		}
		for (String prefix : getDestinationPrefixes()) {
			if (destination.startsWith(prefix)) {
				if (this.slashPathSeparator) {
					return destination.substring(prefix.length() - 1);
				}
				else {
					return destination.substring(prefix.length());
				}
			}
		}
		return null;
	}

	@Override
	protected SimpMessageMappingInfo getMatchingMapping(SimpMessageMappingInfo mapping, Message<?> message) {
		return mapping.getMatchingCondition(message);

	}

	@Override
	protected Comparator<SimpMessageMappingInfo> getMappingComparator(final Message<?> message) {
		return new Comparator<SimpMessageMappingInfo>() {
			@Override
			public int compare(SimpMessageMappingInfo info1, SimpMessageMappingInfo info2) {
				return info1.compareTo(info2, message);
			}
		};
	}

	@Override
	protected void handleMatch(SimpMessageMappingInfo mapping, HandlerMethod handlerMethod,
			String lookupDestination, Message<?> message) {

		String matchedPattern = mapping.getDestinationConditions().getPatterns().iterator().next();
		Map<String, String> vars = getPathMatcher().extractUriTemplateVariables(matchedPattern, lookupDestination);

		if (!CollectionUtils.isEmpty(vars)) {
			MessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
			Assert.state(accessor != null && accessor.isMutable());
			accessor.setHeader(DestinationVariableMethodArgumentResolver.DESTINATION_TEMPLATE_VARIABLES_HEADER, vars);
		}

		try {
			SimpAttributesContextHolder.setAttributesFromMessage(message);
			super.handleMatch(mapping, handlerMethod, lookupDestination, message);
		}
		finally {
			SimpAttributesContextHolder.resetAttributes();
		}
	}

	@Override
	protected AbstractExceptionHandlerMethodResolver createExceptionHandlerMethodResolverFor(Class<?> beanType) {
		return new AnnotationExceptionHandlerMethodResolver(beanType);
	}


	private static final class NoOpValidator implements Validator {

		@Override
		public boolean supports(Class<?> clazz) {
			return false;
		}

		@Override
		public void validate(Object target, Errors errors) {
		}
	}

}
