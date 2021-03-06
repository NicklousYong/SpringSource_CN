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

package org.springframework.messaging.simp.user;

import java.util.Set;

import org.springframework.util.Assert;

/**
 * A simple container for the result of parsing and translating a "user" destination
 * in some source message into a set of actual target destinations by calling
 * {@link org.springframework.messaging.simp.user.UserDestinationResolver}.
 *
 * <p>
 * 一个简单的容器,用于通过调用{@link orgspringframeworkmessagingsimpuserUserDestinationResolver}来解析并将某些源消息中的"用户"目标转换为
 * 一组实际目标目标,。
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0.2
 */
public class UserDestinationResult {

	private final String sourceDestination;

	private final Set<String> targetDestinations;

	private final String subscribeDestination;

	private final String user;


	public UserDestinationResult(String sourceDestination,
			Set<String> targetDestinations, String subscribeDestination, String user) {

		Assert.notNull(sourceDestination, "'sourceDestination' must not be null");
		Assert.notNull(targetDestinations, "'targetDestinations' must not be null");
		Assert.notNull(subscribeDestination, "'subscribeDestination' must not be null");

		this.sourceDestination = sourceDestination;
		this.targetDestinations = targetDestinations;
		this.subscribeDestination = subscribeDestination;
		this.user = user;
	}


	/**
	 * The "user" destination as found in the headers of the source message.
	 *
	 * <p>
	 *  在源消息的头部中找到的"用户"目的地
	 * 
	 * 
	 * @return a destination, never {@code null}
	 */
	public String getSourceDestination() {
		return this.sourceDestination;
	}

	/**
	 * The result of parsing the source destination and translating it into a set
	 * of actual target destinations to use.
	 *
	 * <p>
	 *  解析源目的地并将其转换为要使用的一组实际目标目标的结果
	 * 
	 * 
	 * @return a set of destination values, possibly an empty set
	 */
	public Set<String> getTargetDestinations() {
		return this.targetDestinations;
	}

	/**
	 * The canonical form of the user destination as would be required to subscribe.
	 * This may be useful to ensure that messages received by clients contain the
	 * original destination they used to subscribe.
	 *
	 * <p>
	 *  订阅用户目的地的规范形式这可能有助于确保客户端收到的消息包含用于订阅的原始目的地
	 * 
	 * 
	 * @return a destination, never {@code null}
	 */
	public String getSubscribeDestination() {
		return this.subscribeDestination;
	}

	/**
	 * The user associated with the user destination.
	 *
	 * <p>
	 *  与用户目的地相关联的用户
	 * 
	 * @return the user name, never {@code null}
	 */
	public String getUser() {
		return this.user;
	}

	@Override
	public String toString() {
		return "UserDestinationResult[source=" + this.sourceDestination + ", target=" + this.targetDestinations +
				", subscribeDestination=" + this.subscribeDestination + ", user=" + this.user + "]";
	}
}
