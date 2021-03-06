/***** Lobxxx Translate Finished ******/
package org.springframework.messaging.simp.user;

import java.util.Set;

/**
 * A registry for looking up active session id's by user.
 *
 * <p>Used in support of resolving unique session-specific user destinations.
 * See {@link DefaultUserDestinationResolver} for more details.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 * @see DefaultUserDestinationResolver
 */
public interface UserSessionRegistry {

	/**
	 * Return the active session id's for the given user.
	 * <p>
	 *  返回给定用户的活动会话ID
	 * 
	 * 
	 * @param user the user
	 * @return a set with 0 or more session id's
	 */
	Set<String> getSessionIds(String user);

	/**
	 * Register an active session id for the given user.
	 * <p>
	 *  注册给定用户的活动会话ID
	 * 
	 * 
	 * @param user the user
	 * @param sessionId the session id
	 */
	void registerSessionId(String user, String sessionId);

	/**
	 * Unregister the session id for a user.
	 * <p>
	 *  取消注册用户的会话ID
	 * 
	 * @param user the user
	 * @param sessionId the session id
	 */
	void unregisterSessionId(String user, String sessionId);

}
