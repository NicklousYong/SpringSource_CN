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

package org.springframework.messaging.support;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;

/**
 * Abstract base class for {@link SubscribableChannel} implementations.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractSubscribableChannel extends AbstractMessageChannel implements SubscribableChannel {

	private final Set<MessageHandler> handlers = new CopyOnWriteArraySet<MessageHandler>();


	public Set<MessageHandler> getSubscribers() {
		return Collections.<MessageHandler>unmodifiableSet(this.handlers);
	}

	public boolean hasSubscription(MessageHandler handler) {
		return this.handlers.contains(handler);
	}

	@Override
	public boolean subscribe(MessageHandler handler) {
		boolean result = this.handlers.add(handler);
		if (result) {
			if (logger.isDebugEnabled()) {
				logger.debug(getBeanName() + " added " + handler);
			}
		}
		return result;
	}

	@Override
	public boolean unsubscribe(MessageHandler handler) {
		boolean result = this.handlers.remove(handler);
		if (result) {
			if (logger.isDebugEnabled()) {
				logger.debug(getBeanName() + " removed " + handler);
			}
		}
		return result;
	}

}
