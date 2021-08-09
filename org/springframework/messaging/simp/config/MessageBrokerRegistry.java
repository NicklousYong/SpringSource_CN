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

import java.util.Arrays;
import java.util.Collection;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

/**
 * A registry for configuring message broker options.
 *
 * <p>
 *  用于配置消息代理选项的注册表
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @since 4.0
 */
public class MessageBrokerRegistry {

	private final SubscribableChannel clientInboundChannel;

	private final MessageChannel clientOutboundChannel;

	private SimpleBrokerRegistration simpleBrokerRegistration;

	private StompBrokerRelayRegistration brokerRelayRegistration;

	private final ChannelRegistration brokerChannelRegistration = new ChannelRegistration();

	private String[] applicationDestinationPrefixes;

	private String userDestinationPrefix;

	private PathMatcher pathMatcher;


	public MessageBrokerRegistry(SubscribableChannel clientInboundChannel, MessageChannel clientOutboundChannel) {
		Assert.notNull(clientInboundChannel);
		Assert.notNull(clientOutboundChannel);
		this.clientInboundChannel = clientInboundChannel;
		this.clientOutboundChannel = clientOutboundChannel;
	}


	/**
	 * Enable a simple message broker and configure one or more prefixes to filter
	 * destinations targeting the broker (e.g. destinations prefixed with "/topic").
	 * <p>
	 *  启用一个简单的消息代理并配置一个或多个前缀来过滤定位到代理的目标(例如,以"/ topic"为前缀的目标)
	 * 
	 */
	public SimpleBrokerRegistration enableSimpleBroker(String... destinationPrefixes) {
		this.simpleBrokerRegistration = new SimpleBrokerRegistration(
				this.clientInboundChannel, this.clientOutboundChannel, destinationPrefixes);
		return this.simpleBrokerRegistration;
	}

	/**
	 * Enable a STOMP broker relay and configure the destination prefixes supported by the
	 * message broker. Check the STOMP documentation of the message broker for supported
	 * destinations.
	 * <p>
	 * 启用S​​TOMP代理中继并配置消息代理支持的目标前缀检查消息代理的STOMP文档以获得支持的目标
	 * 
	 */
	public StompBrokerRelayRegistration enableStompBrokerRelay(String... destinationPrefixes) {
		this.brokerRelayRegistration = new StompBrokerRelayRegistration(
				this.clientInboundChannel, this.clientOutboundChannel, destinationPrefixes);
		return this.brokerRelayRegistration;
	}

	/**
	 * Customize the channel used to send messages from the application to the message
	 * broker. By default, messages from the application to the message broker are sent
	 * synchronously, which means application code sending a message will find out
	 * if the message cannot be sent through an exception. However, this can be changed
	 * if the broker channel is configured here with task executor properties.
	 * <p>
	 *  自定义用于将消息从应用程序发送到消息代理的通道默认情况下,从应用程序到消息代理程序的消息同步发送,这意味着应用程序代码发送消息将会发现消息是否不能通过异常发送但是,如果代理通道在此处配置了任务执行程序
	 * 属性,则可以更改此值。
	 * 
	 */
	public ChannelRegistration configureBrokerChannel() {
		return this.brokerChannelRegistration;
	}

	protected ChannelRegistration getBrokerChannelRegistration() {
		return this.brokerChannelRegistration;
	}

	/**
	 * Configure one or more prefixes to filter destinations targeting application
	 * annotated methods. For example destinations prefixed with "/app" may be
	 * processed by annotated methods while other destinations may target the
	 * message broker (e.g. "/topic", "/queue").
	 * <p>When messages are processed, the matching prefix is removed from the destination
	 * in order to form the lookup path. This means annotations should not contain the
	 * destination prefix.
	 * <p>Prefixes that do not have a trailing slash will have one automatically appended.
	 * <p>
	 * 配置一个或多个前缀以过滤目标应用程序注释方法的目标例如,以"/ app"为前缀的目标可以通过注释方法处理,而其他目标可能定位到消息代理(例如"/ topic","/ queue")<p>当消息被处理时,
	 * 匹配的前缀从目的地被删除,以便形成查找路径这意味着注释不应包含目的地前缀<p>不具有末尾斜杠的前缀将具有自动附加的前缀。
	 * 
	 */
	public MessageBrokerRegistry setApplicationDestinationPrefixes(String... prefixes) {
		this.applicationDestinationPrefixes = prefixes;
		return this;
	}

	protected Collection<String> getApplicationDestinationPrefixes() {
		return (this.applicationDestinationPrefixes != null ?
				Arrays.asList(this.applicationDestinationPrefixes) : null);
	}

	/**
	 * Configure the prefix used to identify user destinations. User destinations
	 * provide the ability for a user to subscribe to queue names unique to their
	 * session as well as for others to send messages to those unique,
	 * user-specific queues.
	 * <p>For example when a user attempts to subscribe to "/user/queue/position-updates",
	 * the destination may be translated to "/queue/position-updatesi9oqdfzo" yielding a
	 * unique queue name that does not collide with any other user attempting to do the same.
	 * Subsequently when messages are sent to "/user/{username}/queue/position-updates",
	 * the destination is translated to "/queue/position-updatesi9oqdfzo".
	 * <p>The default prefix used to identify such destinations is "/user/".
	 * <p>
	 * 配置用于标识用户目标的前缀用户目标提供用户订阅其会话唯一的队列名称的能力,以及其他用户将消息发送到这些唯一的用户特定队列<p>例如,当用户尝试要订阅"/ user / queue / position-
	 * updates",目的地可以转换为"/ queue / position-updatesi9oqdfzo",从而产生不与尝试执行相同操作的任何其他用户相冲突的唯一队列名称。
	 * 发送到"/ user / {username} / queue / position-updates",目的地被转换为"/ queue / position-updatesi9oqdfzo"<p>用于标
	 * 识这些目的地的默认前缀是"/ user /"。
	 * 
	 */
	public MessageBrokerRegistry setUserDestinationPrefix(String destinationPrefix) {
		this.userDestinationPrefix = destinationPrefix;
		return this;
	}

	protected String getUserDestinationPrefix() {
		return this.userDestinationPrefix;
	}

	/**
	 * Configure the PathMatcher to use to match the destinations of incoming
	 * messages to {@code @MessageMapping} and {@code @SubscribeMapping} methods.
	 * <p>By default {@link org.springframework.util.AntPathMatcher} is configured.
	 * However applications may provide an {@code AntPathMatcher} instance
	 * customized to use "." (commonly used in messaging) instead of "/" as path
	 * separator or provide a completely different PathMatcher implementation.
	 * <p>Note that the configured PathMatcher is only used for matching the
	 * portion of the destination after the configured prefix. For example given
	 * application destination prefix "/app" and destination "/app/price.stock.**",
	 * the message might be mapped to a controller with "price" and "stock.**"
	 * as its type and method-level mappings respectively.
	 * <p>When the simple broker is enabled, the PathMatcher configured here is
	 * also used to match message destinations when brokering messages.
	 * <p>
	 * 配置PathMatcher以将传入消息的目标匹配到{@code @MessageMapping}和{@code @SubscribeMapping}方法<p>默认情况下配置{@link orgspringframeworkutilAntPathMatcher}
	 * 但是应用程序可能会提供{@code AntPathMatcher}实例定制使用""(通常用于消息传递)而不是"/"作为路径分隔符或提供完全不同的PathMatcher实现<p>请注意,配置的PathMa
	 * tcher仅用于在配置的前缀之后匹配目标的部分例如给定应用目的地前缀"/ app"和目的地"/ app / pricestock **",该消息可能被映射到具有"价格"和"库存**"的控制器作为其类型和
	 * 
	 * @since 4.1
	 */
	public MessageBrokerRegistry setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
		return this;
	}

	protected PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}


	protected SimpleBrokerMessageHandler getSimpleBroker(SubscribableChannel brokerChannel) {
		if (this.simpleBrokerRegistration == null && this.brokerRelayRegistration == null) {
			enableSimpleBroker();
		}
		if (this.simpleBrokerRegistration != null) {
			SimpleBrokerMessageHandler handler = this.simpleBrokerRegistration.getMessageHandler(brokerChannel);
			handler.setPathMatcher(this.pathMatcher);
			return handler;
		}
		return null;
	}

	protected StompBrokerRelayMessageHandler getStompBrokerRelay(SubscribableChannel brokerChannel) {
		if (this.brokerRelayRegistration != null) {
			return this.brokerRelayRegistration.getMessageHandler(brokerChannel);
		}
		return null;
	}

}
