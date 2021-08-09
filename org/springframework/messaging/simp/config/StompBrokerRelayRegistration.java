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

package org.springframework.messaging.simp.config;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.util.Assert;

/**
 * Registration class for configuring a {@link StompBrokerRelayMessageHandler}.
 *
 * <p>
 *  配置{@link StompBrokerRelayMessageHandler}的注册类
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class StompBrokerRelayRegistration extends AbstractBrokerRegistration {

	private String relayHost = "127.0.0.1";

	private int relayPort = 61613;

	private String clientLogin = "guest";

	private String clientPasscode = "guest";

	private String systemLogin = "guest";

	private String systemPasscode = "guest";

	private Long systemHeartbeatSendInterval;

	private Long systemHeartbeatReceiveInterval;

	private String virtualHost;

	private boolean autoStartup = true;


	public StompBrokerRelayRegistration(SubscribableChannel clientInboundChannel,
			MessageChannel clientOutboundChannel, String[] destinationPrefixes) {

		super(clientInboundChannel, clientOutboundChannel, destinationPrefixes);
	}


	/**
	 * Set the STOMP message broker host.
	 * <p>
	 *  设置STOMP消息代理主机
	 * 
	 */
	public StompBrokerRelayRegistration setRelayHost(String relayHost) {
		Assert.hasText(relayHost, "relayHost must not be empty");
		this.relayHost = relayHost;
		return this;
	}

	/**
	 * Set the STOMP message broker port.
	 * <p>
	 *  设置STOMP消息代理端口
	 * 
	 */
	public StompBrokerRelayRegistration setRelayPort(int relayPort) {
		this.relayPort = relayPort;
		return this;
	}

	/**
	 * Set the login to use when creating connections to the STOMP broker on
	 * behalf of connected clients.
	 * <p>By default this is set to "guest".
	 * <p>
	 * 设置与代理连接的客户端创建与STOMP代理程序的连接时使用的登录名<p>默认设置为"guest"
	 * 
	 */
	public StompBrokerRelayRegistration setClientLogin(String login) {
		Assert.hasText(login, "clientLogin must not be empty");
		this.clientLogin = login;
		return this;
	}

	/**
	 * Set the passcode to use when creating connections to the STOMP broker on
	 * behalf of connected clients.
	 * <p>By default this is set to "guest".
	 * <p>
	 *  设置代表连接的客户端创建与STOMP代理的连接时使用的密码<p>默认情况下,该密码设置为"guest"
	 * 
	 */
	public StompBrokerRelayRegistration setClientPasscode(String passcode) {
		Assert.hasText(passcode, "clientPasscode must not be empty");
		this.clientPasscode = passcode;
		return this;
	}

	/**
	 * Set the login for the shared "system" connection used to send messages to
	 * the STOMP broker from within the application, i.e. messages not associated
	 * with a specific client session (e.g. REST/HTTP request handling method).
	 * <p>By default this is set to "guest".
	 * <p>
	 *  设置用于从应用程序中向STOMP代理发送消息的共享"系统"连接的登录名,即与特定客户端会话不相关的消息(例如,REST / HTTP请求处理方法)<p>默认情况下,此设置为"客人"
	 * 
	 */
	public StompBrokerRelayRegistration setSystemLogin(String login) {
		Assert.hasText(login, "systemLogin must not be empty");
		this.systemLogin = login;
		return this;
	}

	/**
	 * Set the passcode for the shared "system" connection used to send messages to
	 * the STOMP broker from within the application, i.e. messages not associated
	 * with a specific client session (e.g. REST/HTTP request handling method).
	 * <p>By default this is set to "guest".
	 * <p>
	 * 设置用于从应用程序中向STOMP代理发送消息的共享"系统"连接的密码,即与特定客户端会话不相关的消息(例如REST / HTTP请求处理方法)<p>默认情况下,此设置为"客人"
	 * 
	 */
	public StompBrokerRelayRegistration setSystemPasscode(String passcode) {
		Assert.hasText(passcode, "systemPasscode must not be empty");
		this.systemPasscode = passcode;
		return this;
	}

	/**
	 * Set the interval, in milliseconds, at which the "system" relay session will,
	 * in the absence of any other data being sent, send a heartbeat to the STOMP broker.
	 * A value of zero will prevent heartbeats from being sent to the broker.
	 * <p>The default value is 10000.
	 * <p>
	 *  设置"系统"中继会话将在没有发送任何其他数据的情况下发送心跳到STOMP代理的间隔(以毫秒为单位)。值为零将防止心跳发送到代理<p>默认值为10000
	 * 
	 */
	public StompBrokerRelayRegistration setSystemHeartbeatSendInterval(long systemHeartbeatSendInterval) {
		this.systemHeartbeatSendInterval = systemHeartbeatSendInterval;
		return this;
	}

	/**
	 * Set the maximum interval, in milliseconds, at which the "system" relay session
	 * expects, in the absence of any other data, to receive a heartbeat from the STOMP
	 * broker. A value of zero will configure the relay session to expect not to receive
	 * heartbeats from the broker.
	 * <p>The default value is 10000.
	 * <p>
	 * 设置"系统"中继会话在没有任何其他数据的情况下从STOMP代理接收心跳的最大时间间隔(以毫秒为单位)零值将配置中继会话,以期望不接收心跳代理<p>默认值为10000
	 * 
	 */
	public StompBrokerRelayRegistration setSystemHeartbeatReceiveInterval(long heartbeatReceiveInterval) {
		this.systemHeartbeatReceiveInterval = heartbeatReceiveInterval;
		return this;
	}

	/**
	 * Set the value of the "host" header to use in STOMP CONNECT frames. When this
	 * property is configured, a "host" header will be added to every STOMP frame sent to
	 * the STOMP broker. This may be useful for example in a cloud environment where the
	 * actual host to which the TCP connection is established is different from the host
	 * providing the cloud-based STOMP service.
	 * <p>By default this property is not set.
	 * <p>
	 *  设置在STOMP CONNECT帧中使用的"主机"头的值当配置此属性时,将向发送到STOMP代理的每个STOMP帧添加"主机"头。
	 * 例如,在云环境中,建立TCP连接的实际主机与提供基于云的STOMP服务的主机不同<p>默认情况下,此属性未设置。
	 * 
	 */
	public StompBrokerRelayRegistration setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
		return this;
	}

	/**
	 * Configure whether the {@link StompBrokerRelayMessageHandler} should start
	 * automatically when the Spring ApplicationContext is refreshed.
	 * <p>The default setting is {@code true}.
	 * <p>
	 */
	public StompBrokerRelayRegistration setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
		return this;
	}


	protected StompBrokerRelayMessageHandler getMessageHandler(SubscribableChannel brokerChannel) {
		StompBrokerRelayMessageHandler handler = new StompBrokerRelayMessageHandler(getClientInboundChannel(),
				getClientOutboundChannel(), brokerChannel, getDestinationPrefixes());

		handler.setRelayHost(this.relayHost);
		handler.setRelayPort(this.relayPort);

		handler.setClientLogin(this.clientLogin);
		handler.setClientPasscode(this.clientPasscode);

		handler.setSystemLogin(this.systemLogin);
		handler.setSystemPasscode(this.systemPasscode);

		if (this.systemHeartbeatSendInterval != null) {
			handler.setSystemHeartbeatSendInterval(this.systemHeartbeatSendInterval);
		}
		if (this.systemHeartbeatReceiveInterval != null) {
			handler.setSystemHeartbeatReceiveInterval(this.systemHeartbeatReceiveInterval);
		}
		if (this.virtualHost != null) {
			handler.setVirtualHost(this.virtualHost);
		}

		handler.setAutoStartup(this.autoStartup);

		return handler;
	}

}
