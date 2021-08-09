/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.context;

/**
 * Interface that encapsulates event publication functionality.
 * Serves as super-interface for {@link ApplicationContext}.
 *
 * <p>
 *  封装事件发布功能的接口作为{@link ApplicationContext}的超级接口
 * 
 * 
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 1.1.1
 * @see ApplicationContext
 * @see ApplicationEventPublisherAware
 * @see org.springframework.context.ApplicationEvent
 * @see org.springframework.context.event.EventPublicationInterceptor
 */
public interface ApplicationEventPublisher {

	/**
	 * Notify all <strong>matching</strong> listeners registered with this
	 * application of an application event. Events may be framework events
	 * (such as RequestHandledEvent) or application-specific events.
	 * <p>
	 * 通知在应用程序事件中注册的所有<strong>匹配</strong>侦听器事件可以是框架事件(例如RequestHandledEvent)或应用程序特定的事件
	 * 
	 * 
	 * @param event the event to publish
	 * @see org.springframework.web.context.support.RequestHandledEvent
	 */
	void publishEvent(ApplicationEvent event);

	/**
	 * Notify all <strong>matching</strong> listeners registered with this
	 * application of an event.
	 * <p>If the specified {@code event} is not an {@link ApplicationEvent},
	 * it is wrapped in a {@link PayloadApplicationEvent}.
	 * <p>
	 *  通知与此应用程序注册的所有<strong>匹配的</strong>侦听器<p>如果指定的{@code事件}不是{@link ApplicationEvent},则将其包装在{@link PayloadApplicationEvent}
	 * 。
	 * 
	 * @param event the event to publish
	 * @since 4.2
	 * @see PayloadApplicationEvent
	 */
	void publishEvent(Object event);

}
