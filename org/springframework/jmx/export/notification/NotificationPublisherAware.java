/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2011 the original author or authors.
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

import org.springframework.beans.factory.Aware;

/**
 * Interface to be implemented by any Spring-managed resource that is to be
 * registered with an {@link javax.management.MBeanServer} and wishes to send
 * JMX {@link javax.management.Notification javax.management.Notifications}.
 *
 * <p>Provides Spring-created managed resources with a {@link NotificationPublisher}
 * as soon as they are registered with the {@link javax.management.MBeanServer}.
 *
 * <p><b>NOTE:</b> This interface only applies to simple Spring-managed
 * beans which happen to get exported through Spring's
 * {@link org.springframework.jmx.export.MBeanExporter}.
 * It does not apply to any non-exported beans; neither does it apply
 * to standard MBeans exported by Spring. For standard JMX MBeans,
 * consider implementing the {@link javax.management.modelmbean.ModelMBeanNotificationBroadcaster}
 * interface (or implementing a full {@link javax.management.modelmbean.ModelMBean}).
 *
 * <p>
 *  要由任何使用{@link javaxmanagementMBeanServer}注册的Spring管理的资源实现的接口,并希望发送JMX {@link javaxmanagementNotification javaxmanagementNotifications}
 * 。
 * 
 * <p>使用{@link NotificationPublisher}向Spring创建的托管资源注册{@link javaxmanagementMBeanServer}
 * 
 *  <p> <b>注意：</b>此接口仅适用于通过Spring的{@link orgspringframeworkjmxexportMBeanExporter}导出的简单的Spring管理的bean。
 * 它不适用于任何未导出的bean;也不适用于Spring导出的标准MBean对于标准JMX MBean,请考虑实现{@link javaxmanagementmodelmbeanModelMBeanNotificationBroadcaster}
 * 
 * @author Rob Harrop
 * @author Chris Beams
 * @since 2.0
 * @see NotificationPublisher
 */
public interface NotificationPublisherAware extends Aware {

	/**
	 * Set the {@link NotificationPublisher} instance for the current managed resource instance.
	 * <p>
	 * 接口(或实现完整的{@link javaxmanagementmodelmbeanModelMBean})。
	 *  <p> <b>注意：</b>此接口仅适用于通过Spring的{@link orgspringframeworkjmxexportMBeanExporter}导出的简单的Spring管理的bean。
	 * 
	 */
	void setNotificationPublisher(NotificationPublisher notificationPublisher);

}
