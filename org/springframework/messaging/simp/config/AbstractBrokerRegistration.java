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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.util.Assert;

/**
 * Base class for message broker registration classes.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractBrokerRegistration {

	private final SubscribableChannel clientInboundChannel;

	private final MessageChannel clientOutboundChannel;

	private final List<String> destinationPrefixes;


	public AbstractBrokerRegistration(SubscribableChannel clientInboundChannel,
			MessageChannel clientOutboundChannel, String[] destinationPrefixes) {

		Assert.notNull(clientOutboundChannel, "'clientInboundChannel' must not be null");
		Assert.notNull(clientOutboundChannel, "'clientOutboundChannel' must not be null");

		this.clientInboundChannel = clientInboundChannel;
		this.clientOutboundChannel = clientOutboundChannel;

		this.destinationPrefixes = (destinationPrefixes != null)
				? Arrays.<String>asList(destinationPrefixes) : Collections.<String>emptyList();
	}


	protected SubscribableChannel getClientInboundChannel() {
		return this.clientInboundChannel;
	}

	protected MessageChannel getClientOutboundChannel() {
		return this.clientOutboundChannel;
	}

	protected Collection<String> getDestinationPrefixes() {
		return this.destinationPrefixes;
	}

	protected abstract AbstractBrokerMessageHandler getMessageHandler(SubscribableChannel brokerChannel);

}
