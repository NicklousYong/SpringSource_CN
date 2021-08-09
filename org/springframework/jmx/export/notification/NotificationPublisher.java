/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.jmx.export.notification;

import javax.management.Notification;

/**
 * Simple interface allowing Spring-managed MBeans to publish JMX notifications
 * without being aware of how those notifications are being transmitted to the
 * {@link javax.management.MBeanServer}.
 *
 * <p>Managed resources can access a {@code NotificationPublisher} by
 * implementing the {@link NotificationPublisherAware} interface. After a particular
 * managed resource instance is registered with the {@link javax.management.MBeanServer},
 * Spring will inject a {@code NotificationPublisher} instance into it if that
 * resource implements the {@link NotificationPublisherAware} inteface.
 *
 * <p>Each managed resource instance will have a distinct instance of a
 * {@code NotificationPublisher} implementation. This instance will keep
 * track of all the {@link javax.management.NotificationListener NotificationListeners}
 * registered for a particular mananaged resource.
 *
 * <p>Any existing, user-defined MBeans should use standard JMX APIs for notification
 * publication; this interface is intended for use only by Spring-created MBeans.
 *
 * <p>
 *  简单的界面允许Spring管理的MBean发布JMX通知,而不知道这些通知如何传送到{@link javaxmanagementMBeanServer}
 * 
 * <p>托管资源可以通过实现{@link NotificationPublisherAware}界面访问{@code NotificationPublisher}。
 * 在特定托管资源实例注册到{@link javaxmanagementMBeanServer}之后,Spring将向其中注入一个{@code NotificationPublisher}实例如果该资源实现
 * 了{@link NotificationPublisherAware}界面。
 * <p>托管资源可以通过实现{@link NotificationPublisherAware}界面访问{@code NotificationPublisher}。
 * 
 *  <p>每个受管资源实例都将有一个{@code NotificationPublisher}实现的不同实例。
 * 该实例将跟踪为特定管理资源注册的所有{@link javaxmanagementNotificationListener NotificationListeners}。
 * 
 * @author Rob Harrop
 * @since 2.0
 * @see NotificationPublisherAware
 * @see org.springframework.jmx.export.MBeanExporter
 */
public interface NotificationPublisher {

	/**
	 * Send the specified {@link javax.management.Notification} to all registered
	 * {@link javax.management.NotificationListener NotificationListeners}.
	 * Managed resources are <strong>not</strong> responsible for managing the list
	 * of registered {@link javax.management.NotificationListener NotificationListeners};
	 * that is performed automatically.
	 * <p>
	 * 
	 * 任何现有的用户定义的MBean都应该使用标准的JMX API通知发布;此接口仅供Spring创建的MBean使用
	 * 
	 * 
	 * @param notification the JMX Notification to send
	 * @throws UnableToSendNotificationException if sending failed
	 */
	void sendNotification(Notification notification) throws UnableToSendNotificationException;

}
