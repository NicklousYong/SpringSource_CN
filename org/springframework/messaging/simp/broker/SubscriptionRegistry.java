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

package org.springframework.messaging.simp.broker;

import org.springframework.messaging.Message;
import org.springframework.util.MultiValueMap;

/**
 * A registry of subscription by session that allows looking up subscriptions.
 *
 * <p>
 *  会话注册表,允许查看订阅
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface SubscriptionRegistry {

	/**
	 * Register a subscription represented by the given message.
	 * <p>
	 *  注册由给定消息表示的订阅
	 * 
	 * 
	 * @param subscribeMessage the subscription request
	 */
	void registerSubscription(Message<?> subscribeMessage);

	/**
	 * Unregister a subscription.
	 * <p>
	 *  取消注册订阅
	 * 
	 * 
	 * @param unsubscribeMessage the request to unsubscribe
	 */
	void unregisterSubscription(Message<?> unsubscribeMessage);

	/**
	 * Remove all subscriptions associated with the given sessionId.
	 * <p>
	 *  删除与给定sessionId关联的所有订阅
	 * 
	 */
	void unregisterAllSubscriptions(String sessionId);

	/**
	 * Find all subscriptions that should receive the given message.
	 * <p>
	 * 查找应该收到给定消息的所有订阅
	 * 
	 * @param message the message
	 * @return a {@link MultiValueMap} from sessionId to subscriptionId's, possibly empty.
	 */
	MultiValueMap<String, String> findSubscriptions(Message<?> message);

}
