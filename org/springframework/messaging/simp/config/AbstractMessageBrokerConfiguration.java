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

package org.springframework.messaging.simp.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.DefaultUserSessionRegistry;
import org.springframework.messaging.simp.user.UserDestinationMessageHandler;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.simp.user.UserSessionRegistry;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Provides essential configuration for handling messages with simple messaging
 * protocols such as STOMP.
 *
 * <p>{@link #clientInboundChannel()} and {@link #clientOutboundChannel()} deliver
 * messages to and from remote clients to several message handlers such as
 * <ul>
 * <li>{@link #simpAnnotationMethodMessageHandler()}</li>
 * <li>{@link #simpleBrokerMessageHandler()}</li>
 * <li>{@link #stompBrokerRelayMessageHandler()}</li>
 * <li>{@link #userDestinationMessageHandler()}</li>
 * </ul>
 * while {@link #brokerChannel()} delivers messages from within the application to the
 * the respective message handlers. {@link #brokerMessagingTemplate()} can be injected
 * into any application component to send messages.
 *
 * <p>Subclasses are responsible for the part of the configuration that feed messages
 * to and from the client inbound/outbound channels (e.g. STOMP over WebSocket).
 *
 * <p>
 *  提供使用简单消息协议(如STOMP)处理消息的基本配置
 * 
 * <p> {@ link #clientInboundChannel()}和{@link #clientOutboundChannel()}将消息传递到远程客户端并从远程客户端传递到多个消息处理程序,例如
 * 。
 * <ul>
 *  <li> {@ link #simpAnnotationMethodMessageHandler()} </li> <li> {@ link #simpleBrokerMessageHandler()}
 *  </li> <li> {@ link #stompBrokerRelayMessageHandler()} </li> <li> {@ link #userDestinationMessageHandler()}
 *  </li>。
 * </ul>
 *  而{@link #brokerChannel()}将应用程序中的消息传递到相应的消息处理程序{@link #brokerMessagingTemplate()}可以注入到任何应用程序组件中以发送消息。
 * 
 *  <p>子类负责将消息传递到客户端入站/出站通道(例如通过WebSocket的STOMP)的部分配置,
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 4.0
 */
public abstract class AbstractMessageBrokerConfiguration implements ApplicationContextAware {

	private static final String MVC_VALIDATOR_NAME = "mvcValidator";

	private static final boolean jackson2Present= ClassUtils.isPresent(
			"com.fasterxml.jackson.databind.ObjectMapper", AbstractMessageBrokerConfiguration.class.getClassLoader());


	private ChannelRegistration clientInboundChannelRegistration;

	private ChannelRegistration clientOutboundChannelRegistration;

	private MessageBrokerRegistry brokerRegistry;

	private ApplicationContext applicationContext;


	/**
	 * Protected constructor.
	 * <p>
	 *  受保护的构造函数
	 * 
	 */
	protected AbstractMessageBrokerConfiguration() {
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}


	@Bean
	public AbstractSubscribableChannel clientInboundChannel() {
		ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel(clientInboundChannelExecutor());
		ChannelRegistration reg = getClientInboundChannelRegistration();
		channel.setInterceptors(reg.getInterceptors());
		return channel;
	}

	@Bean
	public ThreadPoolTaskExecutor clientInboundChannelExecutor() {
		TaskExecutorRegistration reg = getClientInboundChannelRegistration().getOrCreateTaskExecRegistration();
		ThreadPoolTaskExecutor executor = reg.getTaskExecutor();
		executor.setThreadNamePrefix("clientInboundChannel-");
		return executor;
	}

	protected final ChannelRegistration getClientInboundChannelRegistration() {
		if (this.clientInboundChannelRegistration == null) {
			ChannelRegistration registration = new ChannelRegistration();
			configureClientInboundChannel(registration);
			registration.setInterceptors(new ImmutableMessageChannelInterceptor());
			this.clientInboundChannelRegistration = registration;
		}
		return this.clientInboundChannelRegistration;
	}

	/**
	 * A hook for sub-classes to customize the message channel for inbound messages
	 * from WebSocket clients.
	 * <p>
	 * 用于子类的钩子可以自定义来自WebSocket客户端的入站消息的消息通道
	 * 
	 */
	protected void configureClientInboundChannel(ChannelRegistration registration) {
	}

	@Bean
	public AbstractSubscribableChannel clientOutboundChannel() {
		ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel(clientOutboundChannelExecutor());
		ChannelRegistration reg = getClientOutboundChannelRegistration();
		channel.setInterceptors(reg.getInterceptors());
		return channel;
	}

	@Bean
	public ThreadPoolTaskExecutor clientOutboundChannelExecutor() {
		TaskExecutorRegistration reg = getClientOutboundChannelRegistration().getOrCreateTaskExecRegistration();
		ThreadPoolTaskExecutor executor = reg.getTaskExecutor();
		executor.setThreadNamePrefix("clientOutboundChannel-");
		return executor;
	}

	protected final ChannelRegistration getClientOutboundChannelRegistration() {
		if (this.clientOutboundChannelRegistration == null) {
			ChannelRegistration registration = new ChannelRegistration();
			configureClientOutboundChannel(registration);
			registration.setInterceptors(new ImmutableMessageChannelInterceptor());
			this.clientOutboundChannelRegistration = registration;
		}
		return this.clientOutboundChannelRegistration;
	}

	/**
	 * A hook for sub-classes to customize the message channel for messages from
	 * the application or message broker to WebSocket clients.
	 * <p>
	 *  用于子类的钩子,用于自定义从应用程序或消息代理到WebSocket客户端的消息的消息通道
	 * 
	 */
	protected void configureClientOutboundChannel(ChannelRegistration registration) {
	}

	@Bean
	public AbstractSubscribableChannel brokerChannel() {
		ChannelRegistration reg = getBrokerRegistry().getBrokerChannelRegistration();
		ExecutorSubscribableChannel channel = reg.hasTaskExecutor() ?
				new ExecutorSubscribableChannel(brokerChannelExecutor()) : new ExecutorSubscribableChannel();
		reg.setInterceptors(new ImmutableMessageChannelInterceptor());
		channel.setInterceptors(reg.getInterceptors());
		return channel;
	}

	@Bean
	public ThreadPoolTaskExecutor brokerChannelExecutor() {
		ChannelRegistration reg = getBrokerRegistry().getBrokerChannelRegistration();
		ThreadPoolTaskExecutor executor;
		if (reg.hasTaskExecutor()) {
			executor = reg.taskExecutor().getTaskExecutor();
		}
		else {
			// Should never be used
			executor = new ThreadPoolTaskExecutor();
			executor.setCorePoolSize(0);
			executor.setMaxPoolSize(1);
			executor.setQueueCapacity(0);
		}
		executor.setThreadNamePrefix("brokerChannel-");
		return executor;
	}

	/**
	 * An accessor for the {@link MessageBrokerRegistry} that ensures its one-time creation
	 * and initialization through {@link #configureMessageBroker(MessageBrokerRegistry)}.
	 * <p>
	 *  {@link MessageBrokerRegistry}的访问者通过{@link #configureMessageBroker(MessageBrokerRegistry)}确保其一次性创建和初始
	 * 化。
	 * 
	 */
	protected final MessageBrokerRegistry getBrokerRegistry() {
		if (this.brokerRegistry == null) {
			MessageBrokerRegistry registry = new MessageBrokerRegistry(clientInboundChannel(), clientOutboundChannel());
			configureMessageBroker(registry);
			this.brokerRegistry = registry;
		}
		return this.brokerRegistry;
	}

	/**
	 * A hook for sub-classes to customize message broker configuration through the
	 * provided {@link MessageBrokerRegistry} instance.
	 * <p>
	 *  通过提供的{@link MessageBrokerRegistry}实例定制消息代理配置的子类钩子
	 * 
	 */
	protected void configureMessageBroker(MessageBrokerRegistry registry) {
	}

	@Bean
	public SimpAnnotationMethodMessageHandler simpAnnotationMethodMessageHandler() {
		SimpAnnotationMethodMessageHandler handler = new SimpAnnotationMethodMessageHandler(
				clientInboundChannel(), clientOutboundChannel(), brokerMessagingTemplate());

		handler.setDestinationPrefixes(getBrokerRegistry().getApplicationDestinationPrefixes());
		handler.setMessageConverter(brokerMessageConverter());
		handler.setValidator(simpValidator());

		List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
		addArgumentResolvers(argumentResolvers);
		handler.setCustomArgumentResolvers(argumentResolvers);

		List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();
		addReturnValueHandlers(returnValueHandlers);
		handler.setCustomReturnValueHandlers(returnValueHandlers);

		PathMatcher pathMatcher = this.getBrokerRegistry().getPathMatcher();
		if (pathMatcher != null) {
			handler.setPathMatcher(pathMatcher);
		}
		return handler;
	}

	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}

	@Bean
	public AbstractBrokerMessageHandler simpleBrokerMessageHandler() {
		SimpleBrokerMessageHandler handler = getBrokerRegistry().getSimpleBroker(brokerChannel());
		return (handler != null ? handler : new NoOpBrokerMessageHandler());
	}

	@Bean
	public AbstractBrokerMessageHandler stompBrokerRelayMessageHandler() {
		AbstractBrokerMessageHandler handler = getBrokerRegistry().getStompBrokerRelay(brokerChannel());
		return (handler != null ? handler : new NoOpBrokerMessageHandler());
	}

	@Bean
	public UserDestinationMessageHandler userDestinationMessageHandler() {
		return new UserDestinationMessageHandler(clientInboundChannel(), brokerChannel(), userDestinationResolver());
	}

	@Bean
	public SimpMessagingTemplate brokerMessagingTemplate() {
		SimpMessagingTemplate template = new SimpMessagingTemplate(brokerChannel());
		String prefix = getBrokerRegistry().getUserDestinationPrefix();
		if (prefix != null) {
			template.setUserDestinationPrefix(prefix);
		}
		template.setMessageConverter(brokerMessageConverter());
		return template;
	}

	@Bean
	public CompositeMessageConverter brokerMessageConverter() {
		List<MessageConverter> converters = new ArrayList<MessageConverter>();
		boolean registerDefaults = configureMessageConverters(converters);
		if (registerDefaults) {
			converters.add(new StringMessageConverter());
			converters.add(new ByteArrayMessageConverter());
			if (jackson2Present) {
				converters.add(createJacksonConverter());
			}
		}
		return new CompositeMessageConverter(converters);
	}

	protected MappingJackson2MessageConverter createJacksonConverter() {
		DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
		resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setContentTypeResolver(resolver);
		return converter;
	}

	/**
	 * Override this method to add custom message converters.
	 * <p>
	 *  覆盖此方法以添加自定义消息转换器
	 * 
	 * 
	 * @param messageConverters the list to add converters to, initially empty
	 * @return {@code true} if default message converters should be added to list,
	 * {@code false} if no more converters should be added.
	 */
	protected boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		return true;
	}

	@Bean
	public UserDestinationResolver userDestinationResolver() {
		DefaultUserDestinationResolver resolver = new DefaultUserDestinationResolver(userSessionRegistry());
		String prefix = getBrokerRegistry().getUserDestinationPrefix();
		if (prefix != null) {
			resolver.setUserDestinationPrefix(prefix);
		}
		return resolver;
	}

	@Bean
	public UserSessionRegistry userSessionRegistry() {
		return new DefaultUserSessionRegistry();
	}

	/**
	 * Return a {@link org.springframework.validation.Validator}s instance for validating
	 * {@code @Payload} method arguments.
	 * <p>In order, this method tries to get a Validator instance:
	 * <ul>
	 * <li>delegating to getValidator() first</li>
	 * <li>if none returned, getting an existing instance with its well-known name "mvcValidator",
	 * created by an MVC configuration</li>
	 * <li>if none returned, checking the classpath for the presence of a JSR-303 implementation
	 * before creating a {@code OptionalValidatorFactoryBean}</li>
	 * <li>returning a no-op Validator instance</li>
	 * </ul>
	 * <p>
	 *  返回一个{@link orgspringframeworkvalidationValidator}的实例,用于验证{@code @Payload}方法参数<p>为了这个方法,尝试获取一个Validat
	 * or实例：。
	 * <ul>
	 * <li>如果没有返回,则委托getValidator()首先</li> <li>,如果没有返回,通过MVC配置</li> <li>创建一个名称为"mvcValidator"的现有实例,在创建一个{@code OptionalValidatorFactoryBean}
	 *  </li> <li>返回一个无效验证器实例</li>之前检查类路径是否存在JSR-303实现。
	 */
	protected Validator simpValidator() {
		Validator validator = getValidator();
		if (validator == null) {
			if (this.applicationContext.containsBean(MVC_VALIDATOR_NAME)) {
				validator = this.applicationContext.getBean(MVC_VALIDATOR_NAME, Validator.class);
			}
			else if (ClassUtils.isPresent("javax.validation.Validator", getClass().getClassLoader())) {
				Class<?> clazz;
				try {
					String className = "org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean";
					clazz = ClassUtils.forName(className, AbstractMessageBrokerConfiguration.class.getClassLoader());
				}
				catch (Throwable ex) {
					throw new BeanInitializationException("Could not find default validator class", ex);
				}
				validator = (Validator) BeanUtils.instantiate(clazz);
			}
			else {
				validator = new Validator() {
					@Override
					public boolean supports(Class<?> clazz) {
						return false;
					}
					@Override
					public void validate(Object target, Errors errors) {
					}
				};
			}
		}
		return validator;
	}

	/**
	 * Override this method to provide a custom {@link Validator}.
	 * <p>
	 * </ul>
	 * 
	 * @since 4.0.1
	 */
	public Validator getValidator() {
		return null;
	}


	private class NoOpBrokerMessageHandler extends AbstractBrokerMessageHandler {

		public NoOpBrokerMessageHandler() {
			super(clientInboundChannel(), clientOutboundChannel(), brokerChannel());
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
		}

		@Override
		public void handleMessage(Message<?> message) {
		}

		@Override
		protected void handleMessageInternal(Message<?> message) {
		}
	}

}
