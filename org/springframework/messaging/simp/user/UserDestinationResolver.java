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

import org.springframework.messaging.Message;

/**
 * A strategy for resolving a "user" destination and translating it to one or more
 * actual destinations unique to the user's active session(s).
 * <p>
 * For messages sent to a user, the destination must contain the name of the target
 * user, The name, extracted from the destination, is used to look up the active
 * user session(s), and then translate the destination accordingly.
 * <p>
 * For SUBSCRIBE and UNSUBSCRIBE messages, the user is the user associated with
 * the message. In other words the destination does not contain the user name.
 * <p>
 * See the documentation on implementations for specific examples.
 *
 * <p>
 *  解决"用户"目的地并将其转换为用户活动会话唯一的一个或多个实际目的的策略
 * <p>
 * 对于发送给用户的邮件,目的地必须包含目标用户的名称,从目的地提取的名称用于查找活动用户会话,然后相应地翻译目的地
 * <p>
 *  对于SUBSCRIBE和UNSUBSCRIBE消息,用户是与消息相关联的用户。换句话说,目的地不包含用户名
 * <p>
 *  有关具体示例,请参阅有关实现的文档
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 4.0
 *
 * @see org.springframework.messaging.simp.user.DefaultUserDestinationResolver
 * @see UserDestinationMessageHandler
 */
public interface UserDestinationResolver {

	/**
	 * Resolve the destination of the message to a set of actual target destinations.
	 * <p>
	 * If the message is SUBSCRIBE/UNSUBSCRIBE, the returned set will contain a
	 * single translated target destination.
	 * <p>
	 * If the message represents data being sent to a user, the returned set may
	 * contain multiple target destinations, one for each active user session.
	 *
	 * <p>
	 * 
	 * @param message the message with a user destination to be resolved
	 *
	 * @return the result of the resolution, or {@code null} if the resolution
	 * 	fails (e.g. not a user destination, or no user info available, etc)
	 */
	UserDestinationResult resolveDestination(Message<?> message);

}
