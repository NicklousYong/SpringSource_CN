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

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A default implementation of {@link UserDestinationResolver} that relies
 * on the {@link org.springframework.messaging.simp.user.UserSessionRegistry}
 * provided to the constructor to find the sessionIds associated with a user
 * and then uses the sessionId to make the target destination unique.
 *
 * <p>When a user attempts to subscribe to "/user/queue/position-updates", the
 * "/user" prefix is removed and a unique suffix added, resulting in something
 * like "/queue/position-updates-useri9oqdfzo" where the suffix is based on the
 * user's session and ensures it does not collide with any other users attempting
 * to subscribe to "/user/queue/position-updates".
 *
 * <p>When a message is sent to a user with a destination such as
 * "/user/{username}/queue/position-updates", the "/user/{username}" prefix is
 * removed and the suffix added, resulting in something like
 * "/queue/position-updates-useri9oqdfzo".
 *
 * <p>
 * 依赖于{@link orgspringframeworkmessagingsimpuserUserSessionRegistry}的{@link UserDestinationResolver}的默认实
 * 现,提供给构造函数以查找与用户关联的会话ID,然后使用sessionId使目标目标唯一。
 * 
 *  <p>当用户尝试订阅"/ user / queue / position-updates"时,将删除"/ user"前缀,并添加唯一后缀,导致类似"/ queue / position-updates
 * -useri9oqdfzo",其中该后缀基于用户的会话,并确保它不与尝试订阅"/ user / queue / position-updates"的任何其他用户相冲突。
 * 
 * <p>当向具有诸如"/ user / {username} / queue / position-updates"的目的地的用户发送消息时,将删除"/ user / {username}"前缀,并添加后
 * 缀,导致像"/ queue / position-updates-useri9oqdfzo"。
 * 
 * 
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 4.0
 */
public class DefaultUserDestinationResolver implements UserDestinationResolver {

	private static final Log logger = LogFactory.getLog(DefaultUserDestinationResolver.class);


	private final UserSessionRegistry userSessionRegistry;

	private String destinationPrefix = "/user/";


	/**
	 * Create an instance that will access user session id information through
	 * the provided registry.
	 * <p>
	 *  创建一个通过提供的注册表访问用户会话ID信息的实例
	 * 
	 * 
	 * @param userSessionRegistry the registry, never {@code null}
	 */
	public DefaultUserDestinationResolver(UserSessionRegistry userSessionRegistry) {
		Assert.notNull(userSessionRegistry, "'userSessionRegistry' must not be null");
		this.userSessionRegistry = userSessionRegistry;
	}


	/**
	 * The prefix used to identify user destinations. Any destinations that do not
	 * start with the given prefix are not be resolved.
	 * <p>The default value is "/user/".
	 * <p>
	 *  用于标识用户目标的前缀不能从给定的前缀开始的任何目标都不会被解析<p>默认值为"/ user /"
	 * 
	 * 
	 * @param prefix the prefix to use
	 */
	public void setUserDestinationPrefix(String prefix) {
		Assert.hasText(prefix, "prefix must not be empty");
		this.destinationPrefix = prefix.endsWith("/") ? prefix : prefix + "/";
	}

	/**
	 * Return the prefix used to identify user destinations. Any destinations that do not
	 * start with the given prefix are not be resolved.
	 * <p>By default "/user/queue/".
	 * <p>
	 *  返回用于标识用户目的地的前缀不会以给定的前缀开头的任何目标不被解析<p>默认情况下,"/ user / queue /"
	 * 
	 */
	public String getDestinationPrefix() {
		return this.destinationPrefix;
	}


	/**
	 * Return the configured {@link UserSessionRegistry}.
	 * <p>
	 *  返回配置的{@link UserSessionRegistry}
	 * 
	 */
	public UserSessionRegistry getUserSessionRegistry() {
		return this.userSessionRegistry;
	}

	@Override
	public UserDestinationResult resolveDestination(Message<?> message) {
		String destination = SimpMessageHeaderAccessor.getDestination(message.getHeaders());
		DestinationInfo info = parseUserDestination(message);
		if (info == null) {
			return null;
		}
		Set<String> resolved = new HashSet<String>();
		for (String sessionId : info.getSessionIds()) {
			String targetDestination = getTargetDestination(
					destination, info.getDestinationWithoutPrefix(), sessionId, info.getUser());
			if (targetDestination != null) {
				resolved.add(targetDestination);
			}
		}
		return new UserDestinationResult(destination, resolved, info.getSubscribeDestination(), info.getUser());
	}

	private DestinationInfo parseUserDestination(Message<?> message) {
		MessageHeaders headers = message.getHeaders();
		SimpMessageType messageType = SimpMessageHeaderAccessor.getMessageType(headers);
		String destination = SimpMessageHeaderAccessor.getDestination(headers);
		Principal principal = SimpMessageHeaderAccessor.getUser(headers);
		String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);

		String destinationWithoutPrefix;
		String subscribeDestination;
		String user;
		Set<String> sessionIds;

		if (destination == null || !checkDestination(destination, this.destinationPrefix)) {
			return null;
		}

		if (SimpMessageType.SUBSCRIBE.equals(messageType) || SimpMessageType.UNSUBSCRIBE.equals(messageType)) {
			if (sessionId == null) {
				logger.error("No session id. Ignoring " + message);
				return null;
			}
			destinationWithoutPrefix = destination.substring(this.destinationPrefix.length()-1);
			subscribeDestination = destination;
			user = (principal != null ? principal.getName() : null);
			sessionIds = Collections.singleton(sessionId);
		}
		else if (SimpMessageType.MESSAGE.equals(messageType)) {
			int startIndex = this.destinationPrefix.length();
			int endIndex = destination.indexOf('/', startIndex);
			Assert.isTrue(endIndex > 0, "Expected destination pattern \"/user/{userId}/**\"");
			destinationWithoutPrefix = destination.substring(endIndex);
			subscribeDestination = this.destinationPrefix.substring(0, startIndex-1) + destinationWithoutPrefix;
			user = destination.substring(startIndex, endIndex);
			user = StringUtils.replace(user, "%2F", "/");
			if (user.equals(sessionId)) {
				user = null;
				sessionIds = Collections.singleton(sessionId);
			}
			else if (this.userSessionRegistry.getSessionIds(user).contains(sessionId)) {
				sessionIds = Collections.singleton(sessionId);
			}
			else {
				sessionIds = this.userSessionRegistry.getSessionIds(user);
			}
		}
		else {
			return null;
		}
		return new DestinationInfo(destinationWithoutPrefix, subscribeDestination, user, sessionIds);
	}

	protected boolean checkDestination(String destination, String requiredPrefix) {
		return destination.startsWith(requiredPrefix);
	}

	/**
	 * This methods determines the translated destination to use based on the source
	 * destination, the source destination with the user prefix removed, a session
	 * id, and the user for the session (if known).
	 * <p>
	 * destinationWithoutPrefix = destinationsubstring(endIndex); subscribeDestination = thisdestinationPref
	 * ixsubstring(0,startIndex-1)+ destinationWithoutPrefix; user = destinationsubstring(startIndex,endInde
	 * x); user = StringUtilsreplace(user,"％2F","/"); if(userequals(sessionId)){user = null; sessionIds = Collectionssingleton(sessionId); }
	 *  else if(thisuserSessionRegistrygetSessionIds(user)contains(sessionId)){sessionIds = Collectionssingleton(sessionId); }
	 *  else {sessionIds = thisuserSessionRegistrygetSessionIds(user); }} else {return null; } return new De
	 * stinationInfo(destinationWithoutPrefix,subscribeDestination,user,sessionIds); }。
	 * 
	 * @param sourceDestination the source destination of the input message
	 * @param sourceDestinationWithoutPrefix the source destination without the user prefix
	 * @param sessionId the id of the session for the target message
	 * @param user the user associated with the session, or {@code null}
	 * @return a target destination, or {@code null} if none
	 */
	protected String getTargetDestination(String sourceDestination,
			String sourceDestinationWithoutPrefix, String sessionId, String user) {

		return sourceDestinationWithoutPrefix + "-user" + sessionId;
	}

	@Override
	public String toString() {
		return "DefaultUserDestinationResolver[prefix=" + this.destinationPrefix + "]";
	}


	private static class DestinationInfo {

		private final String destinationWithoutPrefix;

		private final String subscribeDestination;

		private final String user;

		private final Set<String> sessionIds;

		public DestinationInfo(String destinationWithoutPrefix, String subscribeDestination, String user,
				Set<String> sessionIds) {

			this.user = user;
			this.destinationWithoutPrefix = destinationWithoutPrefix;
			this.subscribeDestination = subscribeDestination;
			this.sessionIds = sessionIds;
		}

		public String getDestinationWithoutPrefix() {
			return this.destinationWithoutPrefix;
		}

		public String getSubscribeDestination() {
			return this.subscribeDestination;
		}

		public String getUser() {
			return this.user;
		}

		public Set<String> getSessionIds() {
			return this.sessionIds;
		}

		@Override
		public String toString() {
			return "DestinationInfo[destination=" + this.destinationWithoutPrefix + ", subscribeDestination=" +
					this.subscribeDestination + ", user=" + this.user + ", sessionIds=" + this.sessionIds + "]";
		}
	}

}
