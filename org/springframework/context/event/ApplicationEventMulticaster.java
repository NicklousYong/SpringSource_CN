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

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;

/**
 * Interface to be implemented by objects that can manage a number of
 * {@link ApplicationListener} objects, and publish events to them.
 *
 * <p>An {@link org.springframework.context.ApplicationEventPublisher}, typically
 * a Spring {@link org.springframework.context.ApplicationContext}, can use an
 * ApplicationEventMulticaster as a delegate for actually publishing events.
 *
 * <p>
 *  要由可以管理多个{@link ApplicationListener}对象并向其发布事件的对象来实现的接口
 * 
 * <p> {@link orgspringframeworkcontextApplicationEventPublisher}(通常为Spring {@link orgspringframeworkcontextApplicationContext}
 * )可以使用ApplicationEventMulticaster作为实际发布事件的委托。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 */
public interface ApplicationEventMulticaster {

	/**
	 * Add a listener to be notified of all events.
	 * <p>
	 *  添加一个监听器以通知所有事件
	 * 
	 * 
	 * @param listener the listener to add
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * Add a listener bean to be notified of all events.
	 * <p>
	 *  添加一个监听器bean以通知所有事件
	 * 
	 * 
	 * @param listenerBeanName the name of the listener bean to add
	 */
	void addApplicationListenerBean(String listenerBeanName);

	/**
	 * Remove a listener from the notification list.
	 * <p>
	 *  从通知列表中删除一个侦听器
	 * 
	 * 
	 * @param listener the listener to remove
	 */
	void removeApplicationListener(ApplicationListener<?> listener);

	/**
	 * Remove a listener bean from the notification list.
	 * <p>
	 *  从通知列表中删除侦听器bean
	 * 
	 * 
	 * @param listenerBeanName the name of the listener bean to add
	 */
	void removeApplicationListenerBean(String listenerBeanName);

	/**
	 * Remove all listeners registered with this multicaster.
	 * <p>After a remove call, the multicaster will perform no action
	 * on event notification until new listeners are being registered.
	 * <p>
	 *  删除所有在这个多主机注册的监听器<p>删除呼叫后,多主机将不会对事件通知执行任何操作,直到新的监听器被注册
	 * 
	 */
	void removeAllListeners();

	/**
	 * Multicast the given application event to appropriate listeners.
	 * <p>Consider using {@link #multicastEvent(ApplicationEvent, ResolvableType)}
	 * if possible as it provides a better support for generics-based events.
	 * <p>
	 * 将给定应用程序事件组播到适当的侦听器<p>如果可能,请考虑使用{@link #multicastEvent(ApplicationEvent,ResolvableType)},因为它可以更好地支持基于泛
	 * 型的事件。
	 * 
	 * 
	 * @param event the event to multicast
	 */
	void multicastEvent(ApplicationEvent event);

	/**
	 * Multicast the given application event to appropriate listeners.
	 * <p>If the {@code eventType} is {@code null}, a default type is built
	 * based on the {@code event} instance.
	 * <p>
	 * 
	 * @param event the event to multicast
	 * @param eventType the type of event (can be null)
	 */
	void multicastEvent(ApplicationEvent event, ResolvableType eventType);

}
